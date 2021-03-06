package client.gui;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

import client.ClientBack;
import client.gui.ClientGUI.JTextFieldLimit;
import model.vo.RoomName;

public class ClientHome extends JFrame {
	private String userid; // 접속유저 아이디
	private JPanel contentPane;
	private ClientBack clientback;
	private JTextField textField;
	private JTable roomtable;
	private JTable chatGrouptable;
	private JTable findFritable; // 친구 찾기 테이블
	private JTable friListtable; // 친구 목록 테이블
	private JTable createGroupRoomListtable; // 그룹방 개설시 필요한 리스트 테이블
	ClientHome frame;
	JScrollPane scrollPane;
	private int menuInt; // 검색버튼 메뉴 구분자
	String findFricolumnNames[] = { "번호", "아이디", "상태메세지" };
	String chatGroupcolumnNames[] = { "채팅방id", "채팅방명"};
	
	CreateGroupRoomGUI cgrgui;
	JDialog roomNamedialog; 

	/**
	 * Launch the application.
	 */
	public void home(ClientBack clientBack,String userid) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					frame = new ClientHome(clientBack,userid);
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	public ClientHome getFrame() {
		return frame;
	}
	
	public void setuserid(String userid) {
		this.userid = userid;
	}

	public ClientHome() {
		getContentPane().setLayout(null);
	
		textField = new JTextField();
		textField.setBounds(12, 10, 227, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		JButton btnNewButton = new JButton("채팅방 선택");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton.setBounds(246, 9, 97, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("\uCE5C\uAD6C \uCC3E\uAE30");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
			}
		});
		btnNewButton_1.setBounds(12, 383, 331, 43);
		getContentPane().add(btnNewButton_1);
		
		JButton button = new JButton("\uCE5C\uAD6C \uBAA9\uB85D");
		button.setBounds(12, 436, 331, 43);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("\uCC44\uD305\uBC29 \uAC1C\uC124");
		button_1.setBounds(12, 489, 331, 43);
		getContentPane().add(button_1);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 41, 329, 332);
		getContentPane().add(scrollPane);
		
		String columnNames[] =
			{ "번호", "채팅방명", "최근 내용" };

			Object rowData[][] =
			{
			{ 1, "맛동산", "오리온" },
			{ 2, "아폴로", "불량식품" },
			{ 3, "칸쵸코", "과자계의 레전드" }
			};
	
			chatGrouptable = new JTable(rowData, columnNames);
		scrollPane.setViewportView(chatGrouptable);
		
		JButton button_2 = new JButton("\uCC44\uD305\uBC29 \uAC1C\uC124");
		button_2.setBounds(12, 542, 331, 43);
		getContentPane().add(button_2);
		
	}

	public ClientHome(ClientBack clientBack, String userid) {
		menuInt = 4;
		this.clientback = clientBack;
		setTitle(userid);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 370, 650);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		getContentPane().setLayout(null);
		
		textField = new JTextField();
		textField.setBounds(12, 10, 227, 21);
		getContentPane().add(textField);
		textField.setColumns(10);
		textField.setDocument(new JTextFieldLimit(20));
		
		JButton btnNewButton = new JButton("검색");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				switch(menuInt) {
				case 1: 
					String searchContent = textField.getText();
					fn_addfri(clientback,searchContent);
				break;
				case 2: break;
				case 3: break;
				case 4: break;
				}
			}
		});
		btnNewButton.setBounds(246, 9, 97, 23);
		getContentPane().add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("친구 찾기");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 1;
				fn_addfri(clientback, "");
			}
		});
		btnNewButton_1.setBounds(12, 383, 331, 43);
		getContentPane().add(btnNewButton_1);
		
		JButton button = new JButton("친구 목록");
		button.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 2;
				fn_friList(clientback);
			}
		});
		button.setBounds(12, 436, 331, 43);
		getContentPane().add(button);
		
		JButton button_1 = new JButton("채팅방 개설");
		button_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 3;
				fn_createGroupRoom(clientback);
			}
		});
		button_1.setBounds(12, 489, 331, 43);
		getContentPane().add(button_1);
		
		JButton button_2 = new JButton("채팅방 목록");
		button_2.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				menuInt = 4;
				fn_roomList(clientback);

//				scrollPane.setViewportView(chatGrouptable);
			}
		}); 
		button_2.setBounds(12, 542, 331, 43);
		getContentPane().add(button_2);
		getContentPane().add(button_2);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(12, 41, 329, 332);
		getContentPane().add(scrollPane);

		fn_roomList(clientback);

		scrollPane.setViewportView(chatGrouptable);
		
		
	}
	
	/* 친구찾기목록 전체 */
	public void fn_addfri(ClientBack clientback, String searchContent) {
		menuInt = 1;
		clientback.findFriend(searchContent);
	}
	
	public void fn_addfriView(Object[][] rowData) {
		// 내용 수정 불가 시작 //
        DefaultTableModel mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
		findFritable = new JTable(mod); // 친구 찾기 테이블
		findFritable.addMouseListener(new MyMouseListener(1,frame));
		scrollPane.setViewportView(findFritable);
	}
	
	/* 친구 목록 */
	public void fn_friList(ClientBack clientback) {
		menuInt = 2;
		clientback.friList();
	}
	
	
	public void fn_friListView(Object[][] rowData) {
		// 내용 수정 불가 시작 //
        DefaultTableModel mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        friListtable = new JTable(mod); // 친구 목록 테이블
        friListtable.addMouseListener(new MyMouseListener(2,frame));
		scrollPane.setViewportView(friListtable);
	}
	
	public void fn_roomList(ClientBack clientback) {
		menuInt=4;
		clientback.roomList();
	}
	
	public void fn_roomListView(Object[][] rowData) {
		DefaultTableModel mod = new DefaultTableModel(rowData, chatGroupcolumnNames) {
			public boolean isCellEditable(int rowIndex,int mColIndex) {
				return false;
			}
		};
		roomtable = new JTable(mod);
		roomtable.addMouseListener(new MyMouseListener(4,frame));
		scrollPane.setViewportView(roomtable);
	}
	
	public void fn_createGroupRoom(ClientBack clientback) {
		menuInt=3;
		clientback.createGroupRoom();
	}
	
	public void fn_CreateGroupRoomListView(Object[][] rowData, ClientHome frame) {
		// 내용 수정 불가 시작 //
        DefaultTableModel mod = new DefaultTableModel(rowData, findFricolumnNames) {
        public boolean isCellEditable(int rowIndex, int mColIndex) {
                return false;
            }
        };
        createGroupRoomListtable = new JTable(mod); // 친구 목록 테이블
        createGroupRoomListtable.addMouseListener(new MyMouseListener(3,frame));
		scrollPane.setViewportView(createGroupRoomListtable);
	}
	
	
	/* 친구 찾기 테이블 클릭 이벤트 */
	private class MyMouseListener extends MouseAdapter{
		int menu;
		ClientHome frame;
		Map<Long, Chatwindow> chatMap;
		public MyMouseListener(int menu, ClientHome frame) {
			super();
			this.menu = menu;
			this.frame = frame;
			this.chatMap = clientback.getChatMap();
		}
		@Override
		public void mouseClicked(MouseEvent e) {
			if(e.getButton() == 1) {
				if(e.getClickCount() == 2) {
					if(menu == 1) { // 친구 찾기 테이블 클릭 이벤트
						System.out.println(findFritable.getValueAt(findFritable.getSelectedRow(),1));
						System.out.println("더블클릭");

						addFriendAlert((String)findFritable.getValueAt(findFritable.getSelectedRow(),1));
					}else if (menu == 2) { // 친구 목록 테이블 클릭 이벤트
						String friendid = friListtable.getValueAt(friListtable.getSelectedRow(),1).toString(); 
						System.out.println(friendid);
						System.out.println("친구 목록 더블클릭"); 

						//그룹 채팅방 생성
						String[] str = {friendid};
						clientback.createRoom(str);
					}else if (menu == 3) { // 친구 목록 테이블 클릭 이벤트
						if(frame.cgrgui == null) {
							frame.cgrgui = new CreateGroupRoomGUI(frame,clientback);
							frame.cgrgui.setVisible(true);
						}else {
							Object[] o = { createGroupRoomListtable.getValueAt(createGroupRoomListtable.getSelectedRow(),1) };
							System.out.println("오브젝트의 값 " + o[0].toString());
							frame.cgrgui.addrow(o);
						}
					}else if(menu == 4) {
						System.out.println("채팅방 목록!!");
						Long groupid = Long.parseLong(roomtable.getValueAt(roomtable.getSelectedRow(), 0).toString());
						if(chatMap.get(groupid) == null)
							clientback.readchatFile(groupid);
						else {
							chatMap.get(groupid).getFrame().setVisible(true);
						}
					}
				}
			}else if( e.getButton() == 3) {
				if(menu == 4) {
					int column = roomtable.columnAtPoint(e.getPoint());
					int row = roomtable.rowAtPoint(e.getPoint());
					roomtable.changeSelection(row, column, false, false);
					Long groupid = Long.parseLong(roomtable.getValueAt(roomtable.getSelectedRow(), 0).toString());
					String groupName;
					groupName = JOptionPane.showInputDialog("채팅방명을 입력하세요.");
					if(groupName != null) {
						RoomName rn = new RoomName("",groupid,groupName);
						clientback.groupNameChange(rn);
					}
				}
				else if(menu == 2) {
					int column = friListtable.columnAtPoint(e.getPoint());
					int row = friListtable.rowAtPoint(e.getPoint());
					friListtable.changeSelection(row, column, false, false);
					String friendid= friListtable.getValueAt(friListtable.getSelectedRow(), 1).toString();
					
					int result = JOptionPane.showConfirmDialog(null, friendid + "님을 삭제하시겠습니까? ","친구 삭제", JOptionPane.OK_CANCEL_OPTION);
					
					if(result == JOptionPane.OK_OPTION) {
						clientback.deleteFriend(friendid);
					}


				}
			}
		}
	}
	
	public void addFriendAlert(String friendId) {

		int dialogButton = JOptionPane.showConfirmDialog(null, friendId + " 친구 추가하시겠습니까?","친구 추가",JOptionPane.YES_NO_OPTION);
		
		if(dialogButton == JOptionPane.YES_OPTION) {
			clientback.addFriend(friendId);
		}else {
			System.out.println("안해");
		}

	}
	
	public class JTextFieldLimit extends PlainDocument{
		private int limit;
		public JTextFieldLimit(int limit) {
			super();
			this.limit = limit;
		}
		@Override
		public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
			// TODO Auto-generated method stub
			if (str == null)
				return;
			if(getLength() + str.length() <= limit)
				super.insertString(offs, str, a);
		}
		
		
	}

	public void Alert(String msg) {
		JOptionPane.showMessageDialog(null, msg);
	}
	
	
}
