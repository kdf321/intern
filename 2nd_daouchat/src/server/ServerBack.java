package server;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import model.dao.ServerDAO;
import model.vo.Chat;
import model.vo.Data;
import model.vo.Header;
import model.vo.Message;
import model.vo.User;

public class ServerBack {
	public static final int SIGNUP = 1; // 회원가입
	public static final int LOGIN = 2; // 로그인
	public static final int MSG = 3; // 일반메시지
	public static final int FRIFIND = 4; // 친구찾기
	public static final int ADDFRI = 5; // 친구추가
	public static final int FMSG = 6;// 파일, 이미지 전송
	public static final int CREATEROOM = 7; // 그룹생성
	public static final int FRILIST = 8; // 친구목록
	public static final int OPENCHAT = 9; // 그룹생성
	public static final int ROOM = 10; //채팅방목록

    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
	
	private ServerSocket serverSocket; // 서버소켓
	private ServerSocket fileserverSocket;

	private Socket socket; // 받아올 소켓
	private Socket filesocket;

	String connectId;

	/* 현재 접속중인 사용자들의 정보 */
	private Map<String, ObjectOutputStream> currentClientMap = new HashMap<String, ObjectOutputStream>();
	private Map<String, DataOutputStream> currentClientfileMap = new HashMap<String, DataOutputStream>();

	private int non_login_increment = 0; // 로그인 전 임시값
    private ServerDAO sDao;
	
	public ServerDAO getsDao() {
		return sDao;
	}
		
	public Map<String, ObjectOutputStream> getCurrentClientMap() {
		return currentClientMap;
	}
	public void setCurrentClientMap(Map<String, ObjectOutputStream> currentClientMap) {
		this.currentClientMap = currentClientMap;
	}
	public Map<String, DataOutputStream> getCurrentClientfileMap() {
		return currentClientfileMap;
	}
	public void setCurrentClientfileMap(Map<String, DataOutputStream> currentClientfileMap) {
		this.currentClientfileMap = currentClientfileMap;
	}
	
	
	public static void main(String[] args) {
		ServerBack serverBack = new ServerBack();
		serverBack.setting();
	}

	private void broadcast(Message message, List<String> groupmember) {
		synchronized (currentClientMap) {
			try {
				Header header = new Header(MSG,0); // 데이터크기가 사용처가 없음.
				Data sendData = new Data(header,message);
				ObjectOutputStream oos;
				for (String member : groupmember) {
					oos = currentClientMap.get(member);
					if(oos != null) {
						System.out.println("브로드캐스트 중");
						oos.writeObject(sendData);
						oos.flush();
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public void setting() {
		try {
			sDao = new ServerDAO();
			serverSocket = new ServerSocket(1993); // 서버 소켓 생성
			fileserverSocket = new ServerSocket(1994);

			System.out.println("---서버 오픈---");
			while(true) {
				socket = serverSocket.accept(); // 클라이언트 소켓 저장
				filesocket=fileserverSocket.accept();
				System.out.println(socket.getInetAddress() + "에서 접속"); // IP
				Receiver receiver = new Receiver(socket);
				receiver.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
	public synchronized int increment() {
		return non_login_increment++;
	}
	
	/* 현재접속자 맵에 추가 */
	public void addClient(String id, ObjectOutputStream oos, DataOutputStream fos) {
		currentClientMap.put(id, oos);
		currentClientfileMap.put(id, fos);
	}
	
	/* 서버는 연결된 클라이언트의 데이터 수신 대기 */
	class Receiver extends Thread{
		private DataInputStream is;
		private DataOutputStream os;
		private ObjectInputStream ois;
		private ObjectOutputStream oos;
		private DataInputStream fis;
		private DataOutputStream fos;
		private ServerBack serverback; 
		private Socket socket;
		String connectId = "GM" + increment();
		
		public Receiver(Socket socket) {
			try {
				this.socket = socket;
				is = new DataInputStream(socket.getInputStream());
				os = new DataOutputStream(socket.getOutputStream());
				fis = new DataInputStream(filesocket.getInputStream());
				fos = new DataOutputStream(filesocket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
				oos = new ObjectOutputStream(socket.getOutputStream());
				
				addClient(connectId,oos,fos);
				System.out.println("리시버 생성");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		@Override
		public void run() {
			try {
				while(ois != null){
					Data data = (Data) ois.readObject();
					if(data.getHeader().getMenu() == SIGNUP) {
						User user = (User)data.getObject();
						int result = sDao.signUp(user.getUserid(),user.getPassword());
						Header header = new Header(SIGNUP,0); // 데이터크기가 사용처가 없음.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == LOGIN) {
						User user = (User)data.getObject();
						int result = sDao.login(user.getUserid(),user.getPassword());
						Header header = new Header(LOGIN,0); // 데이터크기가 사용처가 없음.
						Data sendData = new Data(header,result);
						
						if(result > 0) {
							currentClientMap.put(user.getUserid(), currentClientMap.remove(connectId)); // 임시아이디를 로그인 아이디로 변경
							currentClientfileMap.put(user.getUserid(),currentClientfileMap.remove(connectId));
							connectId = user.getUserid(); // serverBack의 connectId를 접속자로
							System.out.println("로그인후 접속자수 : " + currentClientMap.size());
						}
						
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == FRIFIND) {
						Object rowData[][] = sDao.friFind(connectId);
						Header header = new Header(FRIFIND,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == ADDFRI) {
						String friendId = (String)data.getObject();
						int result = sDao.addfri(connectId,friendId);
						Header header = new Header(ADDFRI,0); // 데이터크기가 사용처가 없음.
						Data sendData = new Data(header,result);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == FRILIST) {
						Object rowData[][] = sDao.friList(connectId);
						Header header = new Header(FRILIST,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == CREATEROOM) {
						String[] friendids = (String[])data.getObject();
						int result = sDao.createRoom(connectId,friendids); // 채팅방 개설
						Long groupid = sDao.selectRoom(connectId,friendids,ONEROOM); // groupid
						System.out.println("CREATEROOM select 시 그룹아이디 : " + groupid);
						Header header = new Header(CREATEROOM,0);
						Data sendData = new Data(header,groupid);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == ROOM) {
						Object rowData[][] = sDao.roomList(connectId);
						Header header = new Header(ROOM,0);
						Data sendData = new Data(header,rowData);
						oos.writeObject(sendData);
						oos.flush();
					}
					else if(data.getHeader().getMenu() == MSG) {
						Message message = (Message)data.getObject();
						int result = sDao.insertMSG(message);
						List<String> groupmember = sDao.selectGroupmember(message.getGroupid());
						for (String member : groupmember) {
							System.out.println(member);
						}
						broadcast(message, groupmember);
					}
					else if(data.getHeader().getMenu() == FMSG) {
					}
					else if(data.getHeader().getMenu() == OPENCHAT) {
						Long groupid = (Long)data.getObject();
						List<Chat> chatcontent = sDao.selectchatcontent(groupid);
						Header header = new Header(OPENCHAT,0);
						Data sendData = new Data(header,chatcontent);
						oos.writeObject(sendData);
						oos.flush();
					}
				}
//					//파일 메세지
//					else if(headerBuffer[1]==FMSG) {
//						System.out.println(connectId + "가 파일을 보냅니다");
//						byte[] lengthChk = new byte[4];
//						lengthChk[0] = headerBuffer[2];
//						lengthChk[1] = headerBuffer[3];
//						lengthChk[2] = headerBuffer[4];
//						lengthChk[3] = headerBuffer[5];
//						int datalength = byteArrayToInt(lengthChk);
//						System.out.println("서버 데이터 길이: " + datalength);
//						ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//						int read;
//						reciveData = new byte[datalength];
//
//						// 파일 받을때까지 계속
//						while ((read = is.read(reciveData, 0, reciveData.length)) != -1) {
//							buffer.write(reciveData, 0, read);
//							datalength -= read;
//							if (datalength <= 0) { // 다 받으면 break
//								break;
//							}
//						}
//
//						System.out.println(buffer.toString("UTF-8"));
//						String data[] = buffer.toString("UTF-8").split(",");
//						new ServerFileThread(connectId,data[1],data[2],sDao,currentClientMap,currentClientfileMap,filesocket).start();
//					}// 파일메세지 END
			}catch (SocketException e) {
				try {
					currentClientMap.remove(connectId);
					socket.close();
					System.out.println(connectId + "님이 클라이언트 종료");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

}

