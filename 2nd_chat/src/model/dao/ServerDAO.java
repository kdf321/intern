package model.dao;

import static common.DBCPTemplate.getDataSource;

import java.io.UnsupportedEncodingException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import common.DBCPTemplate;
import model.vo.Chat;
import model.vo.ChatMember;
import model.vo.GroupInfo;
import model.vo.RoomName;
import model.vo.Roominfo;
import model.vo.User;
public class ServerDAO {
	DBCPTemplate dataSource;
    Connection con;
    PreparedStatement pstmt;
    ResultSet rs;
    public static final byte ONEROOM= 0x01;
    public static final byte GROUPROOM = 0x02;
 
    public ServerDAO() {
    	dataSource = getDataSource();
    }
    
    // 여기서부터 김성조 인턴사원 코드
    // 591번 줄부터 백상우 인턴사원 코드
    
 
	public int signUp(String id, String pw) {
		con = dataSource.getConnection();
		int chk = 0;

        if( con != null ) {
            try {
				pstmt = con.prepareStatement("insert into user values(?,?)");
				pstmt.setString(1, new String(id.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(pw.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();
		        
		        if(chk >0) {
		        	System.out.println("회원가입 성공");
		        	con.commit();
		        }
		        else {
		        	System.out.println("회원가입 실패");
		        	con.rollback();
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt);
			}
        }
        return chk;
	}

	public User login(String id, String pw) {
		con = dataSource.getConnection();
		
		User user = null;
		if( con != null ) {
            try {
				pstmt = con.prepareStatement("select * from user where userid = ? and password = ?");
				pstmt.setString(1, new String(id.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(pw.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	user = new User(rs.getString("userid"),rs.getString("password"));
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return user;
	} 

	public Object[][] friFind(String tempId,String searchContent) {
		con = dataSource.getConnection();
		Object rowData[][] = null;
		if( con != null ) {
            try {
            	int totalcount = 0;
            	// 나와 친구를 제외한 모든 사용자의 수
            	String query = "select count(userid) from user where userid != ? and userid not in (select friendid from friend where userid = ?) and userid like ?";
            	pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(3, "%" + new String(searchContent.getBytes("UTF-8"),"UTF-8") + "%");
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcount =  rs.getInt(1);
		        }
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
            	
            	rowData = new Object[totalcount][3];
            	// 나와 친구를 제외한 모든 사용자
            	String query2 = "select userid from user where userid != ? and userid not in (select friendid from friend where userid = ?)  and userid like ?";
				pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(tempId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(3, "%" + new String(searchContent.getBytes("UTF-8"),"UTF-8") + "%");
		        rs = pstmt.executeQuery();	
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i][1] = rs.getString(1);
		        	rowData[i++][2] = "";
		        }

			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return rowData;
	}

	public int addfri(String connectId, String data) {
		con = dataSource.getConnection();
		int chk = 0;

        if( con != null ) {
            try {
				pstmt = con.prepareStatement("insert into friend values(?,?)");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setString(2, new String(data.getBytes("UTF-8"),"UTF-8"));
		        chk = pstmt.executeUpdate();
		        
		        if(chk >0) {
		        	System.out.println("친구추가 성공");
		        	con.commit();
		        }
		        else {
		        	System.out.println("친구추가 실패");
		        	con.rollback();
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt);
			}
        }
        return chk;
	}

	public Object[][] friList(String connectId) {
		con = dataSource.getConnection();
		Object[][] rowData = null;
		if( con != null ) {
            try {
            	int totalcnt = 0;
            	pstmt = con.prepareStatement("select count(friendid) from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcnt =  rs.getInt(1);
		        }            	
            	
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        
            	rowData = new Object[totalcnt][3];
				pstmt = con.prepareStatement("select friendid from friend where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = i + 1;
		        	rowData[i][1] = rs.getString(1);
		        	rowData[i++][2] = "";
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
		        dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return rowData;
	}
	
	public int createRoom(String connectId, String data[]) { // data는 friendId
		con = dataSource.getConnection();
		int chk = 0;
		int chk2 = 0;
        if( con != null ) {
            try {
            	/* 1:1 있는지 검사 */
            	long groupidbefore = 0L;
            	String query = "select groupid from chatgroup where groupid in (select groupid from chatmember where userid in (?,?) group by groupid having count(*) = 2) and type = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8")); 
				int j = 0;
				for (;j < data.length; j++) { // 각 참여자
					pstmt.setString(2+j, new String(data[j].getBytes("UTF-8"),"UTF-8")); 
				}
				pstmt.setByte(2+j, ONEROOM); // 채팅타입
				
				rs = pstmt.executeQuery();
				while(rs.next()) {
					groupidbefore = rs.getLong(1);
				}
				
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				if(groupidbefore != 0L) {
					return chk;
				}
            	
            	/* 채팅방 개설 */
            	String query2 = "insert into chatgroup(userid,type) values(?,?)";
				pstmt = con.prepareStatement(query2);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
//				pstmt.setString(2, new String((connectId+"의 방").getBytes("UTF-8"),"UTF-8")); // 채팅 방명
				pstmt.setByte(2, ONEROOM);
		        chk = pstmt.executeUpdate();
		        dataSource.freeConnection(pstmt);
		        
		        /* groupid 가져오기 */
		        String query3 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query3);
		        rs = pstmt.executeQuery();
		        Long groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
//		        pstmt.close();
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        
		        
		        String query6 = "insert into usergroupname values(?,?,?)";
		        pstmt = con.prepareStatement(query6);	
		        pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, groupid);
		        pstmt.setString(3, new String((connectId+"의 방").getBytes("UTF-8"),"UTF-8"));
		        pstmt.executeUpdate();
		        
		        // 채팅방명 추가
				for (int i = 0; i < data.length; i++) {
					pstmt.setString(1, new String(data[i].getBytes("UTF-8"),"UTF-8"));
			        pstmt.setLong(2, groupid);
			        pstmt.setString(3, new String((connectId+"의 방").getBytes("UTF-8"),"UTF-8"));
					chk2 = pstmt.executeUpdate();
				}  
		        
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        
		        /* 채팅방 참여자 추가 */
            	String query4 = "insert into chatmember(groupid,userid,lastreadtime) values(?,?,now(6))";
		        
		        // chatmember에 개설자 아이디 추가
		        pstmt = con.prepareStatement(query4);
				pstmt.setLong(1, groupid);
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.executeUpdate(); 
		        
		        // chatmember에 초대한 아이디 추가
				for (int i = 0; i < data.length; i++) {
					pstmt.setLong(1, groupid);
					pstmt.setString(2, new String(data[i].getBytes("UTF-8"),"UTF-8"));
					chk2 = pstmt.executeUpdate();
				}  
				
			} catch (SQLException e) {
	        	System.out.println("채팅방개설 실패");
	        	e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally { // return 해도 실행됨
				try {
					if(chk > 0 && chk2 > 0) {
						con.commit();
					}else {
						con.rollback();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 dataSource.freeConnection(con,pstmt,rs); 
			}
        }
        return chk2;
	}
	
	public Long createGroupRoom(String connectId, String data[]) { // data는 friendId
		con = dataSource.getConnection();
		Long groupid = null;
		int chk = 0;
		int chk2 = 0;

        if( con != null ) {
            try {
            	/* 채팅방 개설 */
            	String query = "insert into chatgroup(userid, type) values(?,?)";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
//				pstmt.setString(2, new String((connectId+"의 방").getBytes("UTF-8"),"UTF-8")); // 채팅 방명
				pstmt.setByte(2, GROUPROOM); // 채팅 방명
		        chk = pstmt.executeUpdate();
		        dataSource.freeConnection(pstmt);
		        
		        /* groupid 가져오기 */
		        String query2 = "select LAST_INSERT_ID()";
		        pstmt = con.prepareStatement(query2);
		        rs = pstmt.executeQuery();
		        groupid = 0L;
		        while(rs.next()) {
		        	groupid = rs.getLong(1);
		        }
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        System.out.println("서버 db 저장시 groupid : " + groupid);
		        
		        
		        String query6 = "insert into usergroupname values(?,?,?)";
		        pstmt = con.prepareStatement(query6);	
		        pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, groupid);
		        pstmt.setString(3, new String((connectId+"의 방").getBytes("UTF-8"),"UTF-8"));
		        pstmt.executeUpdate();
		        
		        // 채팅방명 추가
				for (int i = 0; i < data.length; i++) {
					pstmt.setString(1, new String(data[i].getBytes("UTF-8"),"UTF-8"));
			        pstmt.setLong(2, groupid);
			        pstmt.setString(3, new String((connectId+"의 방").getBytes("UTF-8"),"UTF-8"));
					chk2 = pstmt.executeUpdate();
				}  
		        
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
		        
		        
		        
		        /* 채팅방 참여자 추가 */
            	String query3 = "insert into chatmember(groupid,userid,lastreadtime) values(?,?,now(6))";
		        
		        // chatmember에 개설자 아이디 추가
		        pstmt = con.prepareStatement(query3);
				pstmt.setLong(1, groupid);
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.executeUpdate(); 
		        
		        // chatmember에 초대한 아이디 추가
				for (int i = 0; i < data.length; i++) {
					pstmt.setLong(1, groupid);
					pstmt.setString(2, new String(data[i].getBytes("UTF-8"),"UTF-8"));
					chk2 = pstmt.executeUpdate();
				}  
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				try {
					if(chk > 0 && chk2 >0) {
						con.commit();
					}else {
						con.rollback();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				 dataSource.freeConnection(con,pstmt,rs);
			}
        }
        return groupid;
	}
	
	
	public Chat insertMSG(Chat message) {
		con = dataSource.getConnection();
		int chk = 0 ;
		Chat chat = null;
		if(con != null) {
			String query = "insert into chatcontent(userid,groupid,content,sendtime,count) values(?,?,?,now(6),(select count(*) from chatmember where groupid = ?))";
			String query2 = "select * from chatcontent where chatid = (select LAST_INSERT_ID())";
			
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(message.getUserid().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, message.getGroupid());
		        pstmt.setString(3, new String(message.getContent().getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(4, message.getGroupid());
			    chk = pstmt.executeUpdate();
				
				dataSource.freeConnection(pstmt);
				
				pstmt = con.prepareStatement(query2);
				rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        }
		        
		       
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				try {
					if(chk >=0) 
						con.commit();
					else
						con.rollback();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return chat;
	}
	

	public List<String> selectGroupmember(Long sendGroupid) {
		con = dataSource.getConnection();
		List<String> groupmemberList = new ArrayList<String>();
		
		if(con != null) {
			String query = "select userid from chatmember where groupid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, sendGroupid);

		        rs = pstmt.executeQuery();
		        
		        while(rs.next()) {
		        	groupmemberList.add(rs.getString(1));
		        }		        
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		
		}
		
		return groupmemberList;
	}

	public List<Chat> selectchatcontent(ChatMember chatmember) {
		con = dataSource.getConnection();
		List<Chat> chatcontent = new ArrayList<Chat>();
		Long groupid = chatmember.getGroupid();
		String userid = chatmember.getUserid();
		int result = 0;
		int result2 = 0;
		if(con != null) {
			// 해당하는 채팅방의 채팅정보를 로그인한 사용자의 마지막읽은 시간보다 늦는데이터 가져오기
			String query = "select * from chatcontent where groupid = ? "
					+ "and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?) order by sendtime, chatid";
			// 카운트감소 
			String query2 = "update chatcontent set count = count-1 where chatid in (select chatid from chatcontent where groupid = ? and sendtime > (select lastreadtime from chatmember where groupid = ? and userid = ?))";
			// 마지막 읽은시간 수정
			String query3 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
				pstmt.setLong(2, groupid);
				pstmt.setString(3, userid);
				
		        rs = pstmt.executeQuery();
		        Chat chat = null;
		        System.out.println("데이터 가져오기");
		        while(rs.next()) {
		        	chat = new Chat(rs.getLong("chatid"),rs.getString("userid"),rs.getLong("groupid"),rs.getString("content"),rs.getTimestamp("sendtime"),rs.getInt("count"));
		        	chatcontent.add(chat);
		        }		 
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
			        
			    if( chat != null) {   
			        pstmt = con.prepareStatement(query2);
			        pstmt.setLong(1, groupid);
					pstmt.setLong(2, groupid);
					pstmt.setString(3, userid);
					result = pstmt.executeUpdate();
					dataSource.freeConnection(pstmt);
					
					pstmt = con.prepareStatement(query3);
					pstmt.setTimestamp(1, chat.getSendtime());
					pstmt.setLong(2, groupid);
			        pstmt.setString(3, userid);				
					result2 = pstmt.executeUpdate();
		        }
			} catch (SQLException e) {
				e.printStackTrace();
			} finally {
				try {
					if(result >= 0 && result2 >=0/* && result3 >=0 */) {
						con.commit();
					}else {
						con.rollback();
					}
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			    dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return chatcontent;
	}

	public int updatereadtime(String member, Chat message) {
		con = dataSource.getConnection();
		int result = 0;
		int result2 = 0;
		String query = "update chatcontent set count = count -1 where chatid = ?";
		String query2 = "update chatmember set lastreadtime = ? where groupid = ? and userid = ?";
		Long groupid = message.getGroupid();
		Long chatid = message.getChatid();
		Timestamp date = message.getSendtime();
		try {
			pstmt = con.prepareStatement(query);
			pstmt.setLong(1, chatid);
			result = pstmt.executeUpdate();
			dataSource.freeConnection(pstmt);
			
			pstmt = con.prepareStatement(query2);
			pstmt.setTimestamp(1, date);
			pstmt.setLong(2, groupid);
			pstmt.setString(3, member);
			result2 = pstmt.executeUpdate();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if(result >=0 && result2 >=0) {
					con.commit();
				}else {
					con.rollback();
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			dataSource.freeConnection(con,pstmt);
		}
		return result;
	}

	public GroupInfo selectRoom(String connectId, String[] data, byte type) {
		con = dataSource.getConnection();
		Long groupid = null;
		GroupInfo info = null;
		if(con != null) {
			try {
//	        	String query = "select groupid from chatgroup where groupid in (select groupid from chatmember where userid in (?,?) group by groupid having count(*) = 2) and type = ?";
	        	String query = "select * from usergroupname where userid = ? and groupid = (select groupid from chatgroup where groupid in (select groupid from chatmember where userid in (?,?) group by groupid having count(*) = 2) and type = ?)";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8")); 
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8")); 
				int j = 0;
				for (;j < data.length; j++) { // 각 참여자
					pstmt.setString(3+j, new String(data[j].getBytes("UTF-8"),"UTF-8")); 
				}
				pstmt.setByte(3+j, ONEROOM); // 채팅타입
				
				rs = pstmt.executeQuery();
				while(rs.next()) {
					info = new GroupInfo(rs.getLong("groupid"), rs.getString("groupname"));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return info;
	}
	
	public ArrayList<Long> selectGroupid() {
		ArrayList<Long> list = new ArrayList<Long>();
		con = dataSource.getConnection();
		if(con!=null) {
			try {
				String query = "select groupid from chatgroup";
				pstmt = con.prepareStatement(query);
				rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	list.add(rs.getLong(1));
		        }
			}catch(SQLException e) {
				e.printStackTrace();
			}finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return list;
	}
	

	public ArrayList<Long> selectgroupiduser(String connectId) {
		ArrayList<Long> list = new ArrayList<Long>();
		con = dataSource.getConnection();
		if(con!=null) {
			try {
				String query = "select groupid from chatgroup where userid = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, connectId);
				rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	list.add(rs.getLong(1));
		        }
			}catch(SQLException e) {
				e.printStackTrace();
			}finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return list;
	}
	
	
	public int updateRoomName(RoomName rn) {
		con = dataSource.getConnection();
		int result = 0 ;
		if(con != null) {
			try {
				String query = "update usergroupname set groupname = ? where userid = ? and groupid = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, rn.getGroupName());
				pstmt.setString(2, rn.getUserid());
				pstmt.setLong(3, rn.getGroupid());
				result = pstmt.executeUpdate();
				
				if(result > 0) {
					con.commit();
				}else {
					con.rollback();
				}
			}catch(SQLException e) {
				e.printStackTrace();
			}finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return result;
	}

	public int deleteFriend(String connectId, String friendid) {
		con = dataSource.getConnection();
		int result = 0;
		if(con != null) {
			String query = "delete from friend where userid = ? and friendid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				pstmt.setString(2, new String(friendid.getBytes("UTF-8"),"UTF-8"));
				result = pstmt.executeUpdate();
				
				if(result > 0)
					con.commit();
				else
					con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con, pstmt, rs);
			}
		}
		return result;
	}
	
	public Object[][] selectChatFriList(Long groupid) {
		con = dataSource.getConnection();
		int totalCnt = 0;
		Object rowData[][] = null;
		if(con!=null) {
			try {
				String query = "select count(*) from chatmember where groupid = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalCnt = rs.getInt(1);
		        }
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				String query2 = "select userid from chatmember where groupid = ?";
				rowData = new Object[totalCnt][1];
			
				pstmt = con.prepareStatement(query2);
				pstmt.setLong(1, groupid);
				
				rs = pstmt.executeQuery();
				int i=0;
				while(rs.next()) {
					rowData[i++][0]=rs.getString("userid");
				}
				
				dataSource.freeConnection(con,pstmt,rs);
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return rowData;
	}
	
	
	/*
	 *  김성조 인턴사원													
	 */
	
	                     
	//-----------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	/*
	 *  백상우 인턴사원
	 */
		
	
	
	
   
	public Object[][] roomList(String connectId){
		con = dataSource.getConnection();
		if(con!=null) {
			try {
				int totalcount = 0;
				System.out.println("연결 : "+connectId);
//				pstmt = con.prepareStatement("select count(*) from usergroupname where groupid in (select groupid from chatmember where userid = ?)");
				pstmt = con.prepareStatement("select count(groupid) from usergroupname where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalcount =  rs.getInt(1);
		        }
		        dataSource.freeConnection(pstmt);
		        dataSource.freeConnection(rs);
				
				Object rowData[][] = new Object[totalcount][2];
//				pstmt = con.prepareStatement("select groupid, groupname from chatgroup where groupid in (select groupid from chatmember where userid = ?)");
				pstmt = con.prepareStatement("select groupid, groupname from usergroupname where userid = ?");
				pstmt.setString(1, new String(connectId.getBytes("UTF-8"),"UTF-8"));
		        rs = pstmt.executeQuery();		        
		        int i=0;
		        while(rs.next()) {
		        	rowData[i][0] = rs.getLong(1);
		        	rowData[i++][1] = rs.getString(2);
		        }
		        dataSource.freeConnection(con,pstmt,rs);
		        System.out.println("방 목록 커넥션 닫힘");
		        return rowData;
			} catch (SQLException e) {
				e.printStackTrace();
				return null;
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return null;
	}


	public boolean insertFile(String userid, Long roomid, String dir) {
		con = dataSource.getConnection();
//		con = getConnection();
		int chk=0;
		if(con!=null) {
			try {
				pstmt=con.prepareStatement("insert into filecontent values(?,?,?,now(6))");
				pstmt.setString(1, new String(userid.getBytes("UTF-8"),"UTF-8"));
				pstmt.setLong(2, roomid);
				pstmt.setString(3, new String(dir.getBytes("UTF-8"),"UTF-8"));
				//pstmt.setString(4, new String(time.getBytes("UTF-8"),"UTF-8"));
				chk=pstmt.executeUpdate();
				if(chk >=0) {
					System.out.println("메세지 전송 성공");
		        	con.commit();
		        }else {
		        	con.rollback();
		        	System.out.println("메세지 전송 실패");
		        }

//		        close(pstmt);
//		        close(con);
				dataSource.freeConnection(con,pstmt);
				
			}catch(SQLException e){
				return false;
			}catch(UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		}
		return true;
	}

	public void deleteolddata() {
		con = dataSource.getConnection();
//		String query = "delete from chatcontent where sendtime <= date_add(now(), interval -2 day)";
		String query = "delete from chatcontent where sendtime <= date_add(now(), interval -2 minute)";
		//2일 지난 데이터 제거
		try {
			pstmt = con.prepareStatement(query);
			int result = pstmt.executeUpdate();
			
			if(result >= 0) {
				con.commit();
				System.out.println("--server data remove--");
			}else {
				con.rollback();
			}
			
			dataSource.freeConnection(con,pstmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void deleteoldFiledata() {
		con = dataSource.getConnection();
		String query = "delete from filecontent where sendtime <= date_add(now(), interval -30 day)";
		
		//2일 지난 데이터 제거
		try {
			pstmt = con.prepareStatement(query);
			int result = pstmt.executeUpdate();
			
			if(result>0) {
				con.commit();
				System.out.println("--file data remove--");
			}else {
				con.rollback();
			}
		
			dataSource.freeConnection(con,pstmt);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public Object[][] selectfilecontent(Long groupid){
		con = dataSource.getConnection();
		int totalfileCnt = 0;
		if(con!=null) {
			try {
				String query = "select count(file_dir) from filecontent where groupid = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
		        rs = pstmt.executeQuery();
		        while(rs.next()) {
		        	totalfileCnt = rs.getInt(1);
		        }
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				String query2 = "select * from filecontent where groupid = ? order by sendtime";
				Object rowData[][] = new Object[totalfileCnt][1];
			
				pstmt = con.prepareStatement(query2);
				pstmt.setLong(1, groupid);
				
				rs = pstmt.executeQuery();
				int i=0;
				while(rs.next()) {
					rowData[i++][0]=rs.getString("file_dir");
					System.out.println(rs.getString("file_dir"));
				}
				
				dataSource.freeConnection(con,pstmt,rs);
				
				return rowData;
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return null;
		
	}
	

	public boolean outRoom(Roominfo roominfo) {
		con = dataSource.getConnection();
		Long groupid = roominfo.getGroupid();
		String userid = roominfo.getUserid();
		if(con!=null) {
			try {
				String query = "delete from chatmember where userid = ? and groupid = ?";
				pstmt=con.prepareStatement(query);
				pstmt.setString(1, userid);
				pstmt.setLong(2, groupid);
				int result = pstmt.executeUpdate();
				
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				String query2 = "delete from usergroupname where userid = ? and groupid = ?";
				pstmt=con.prepareStatement(query2);
				pstmt.setString(1, userid);
				pstmt.setLong(2, groupid);
				int result2 = pstmt.executeUpdate();
				
				con.commit();
				System.out.println(userid+groupid);
				dataSource.freeConnection(con,pstmt,rs);
				return true;
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return false;

	}
	
	public ArrayList<String> selectimagecontents(Long groupid){
		con=dataSource.getConnection();
		ArrayList<String> images = new ArrayList(); 
		if(con!=null) {
			try {
				String query = "select * from filecontent where groupid = ?";
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
				rs=pstmt.executeQuery();
				while(rs.next()) {
					String dir = rs.getString("file_dir");
					String []dir_tokens= dir.split("\\\\");
					String [] extension = dir_tokens[dir_tokens.length-1].split("\\.");
					//System.out.println(extension[1]);
					if(extension[1].equals("png")||extension[1].equals("jpg")||extension[1].equals("gif")) {
						images.add(dir);
						
					}
				}
				dataSource.freeConnection(con,pstmt,rs);
				return images;
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return images;
		
	}
	
	public ArrayList<String> memberavailable(String userid,Long groupid){
		con=dataSource.getConnection();
		ArrayList<String> avail= new ArrayList();
		if(con!=null) {
			try {
				String query = "select * from friend where userid = ? and friendid not in (select chatmember.userid from (select * from friend where userid = ?) p, chatmember where p.friendid = chatmember.userid and groupid =?)";
				pstmt = con.prepareStatement(query);
				pstmt.setString(1,userid);
				pstmt.setString(2,userid);
				pstmt.setLong(3, groupid);
				rs=pstmt.executeQuery();
				while(rs.next()) {
					System.out.println(rs.getString("friendid"));
					avail.add(rs.getString("friendid"));
				}
				dataSource.freeConnection(con,pstmt,rs);
				return avail;
			}catch(SQLException e) {
				e.printStackTrace();
			}
		}
		return avail;
	}
	
	public boolean memberInsert(String connectId, String userid, Long groupid) {
		con=dataSource.getConnection();
		int chk=0;
		int chk2=0;
		if(con!=null) {
			try {
				String query = "insert into chatmember(groupid,userid,lastreadtime) values(?,?,now(6))";
				pstmt = con.prepareStatement(query);
				pstmt.setLong(1, groupid);
				pstmt.setString(2, userid);
				chk = pstmt.executeUpdate();
				
				dataSource.freeConnection(pstmt);
				dataSource.freeConnection(rs);
				
				String query6 = "insert into usergroupname values(?,?,?)";
		        pstmt = con.prepareStatement(query6);	
		        pstmt.setString(1, new String(userid.getBytes("UTF-8"),"UTF-8"));
		        pstmt.setLong(2, groupid);
		        pstmt.setString(3, new String((userid+"의 방").getBytes("UTF-8"),"UTF-8"));
		        chk2 = pstmt.executeUpdate();
				
				if (chk > 0 && chk2 > 0) {
					System.out.println("멤버추가 완료");
					con.commit();
					return true;
				} else {
					System.out.println("멤버추가 실패");
					con.rollback();
					return false;
				}
			}catch(SQLException e) {
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
		}
		return false;
	}

	public String selectGroupName(ChatMember chatmember) {
		con = dataSource.getConnection();
		String groupName = null;
		int result = 0;
		if(con != null) {
			String query = "select groupname from usergroupname where userid = ? and groupid = ?";
			try {
				pstmt = con.prepareStatement(query);
				pstmt.setString(1, new String(chatmember.getUserid().getBytes("UTF-8"),"UTF-8"));
				pstmt.setLong(2, chatmember.getGroupid());
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					groupName  = rs.getString("groupname");
				}
				if(result > 0)
					con.commit();
				else
					con.rollback();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con, pstmt, rs);
			}
		}
		return groupName;
	}

	public byte typechk(Long groupid) {
		con = dataSource.getConnection();
		byte type = 0;
		if(con != null) {
			String sql = "select type from chatgroup where groupid = ?";
			try {
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(1, groupid);
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					type = rs.getByte("type");
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {

				dataSource.freeConnection(con,pstmt,rs);
			}
			
		}
		return type;
	}

	public String[] selectmember(String connectId, Long groupid,String userid) {
		con = dataSource.getConnection();
		String[] data = null;
		if(con != null) {
			try {
				String sql = "select * from chatmember where groupid = ? and userid != ?";
				pstmt = con.prepareStatement(sql);
				pstmt.setLong(1, groupid);
				pstmt.setString(2, new String(connectId.getBytes("UTF-8"),"UTF-8"));
				rs = pstmt.executeQuery();
				
				ArrayList<String> list = new ArrayList<String>();
				while(rs.next()) {
					list.add(rs.getString("userid"));
				}
				if(list.size() > 0) {
					data = new String[list.size()+1];
					for(int i = 0; i < list.size() ; i++) {
						data[i] = list.get(i);
					}
					data[list.size()] = userid;
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} finally {
				dataSource.freeConnection(con,pstmt,rs);
			}
			
		}
		return data;
	}
	

	

	

}
    
   
