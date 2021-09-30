package magic.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

import com.mysql.jdbc.Statement;

import myUtil.HanConv;

public class BoardDBBean {
	
	private static BoardDBBean instance = new BoardDBBean();
	
	public static BoardDBBean getInstance() {
		return instance;
	}
	
	public Connection getConnection() throws Exception{
		Context ctx = new InitialContext();	//업캐스팅
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}
	
	// 잘 넘어갔으면 1 return
	public int insertBoard(BoardBean board) throws Exception{
		Connection conn=null;
		PreparedStatement pstmt1=null;
		PreparedStatement pstmt2=null;
		PreparedStatement pstmt3=null;
		String sql="";
		String sql1="";
		int number = 1;
		ResultSet rs;//결과값 받음
		int re=-1;
		
		int id = board.getB_id();
		int ref = board.getB_ref();
		int step = board.getB_step();
		int level = board.getB_level();
		
		try {
			conn = getConnection();
			
			//현재 레코드 중에서 가장 큰 글번호를 얻어낸다.(번호+1)
			sql = "select max(b_id) from boardt";
			pstmt1 = conn.prepareStatement(sql);
			rs = pstmt1.executeQuery();
			if(rs.next()){
				number = rs.getInt(1)+1;
			}
			
			System.out.println("@@@### id ===>"+id);
			//답글=글번호가 0이 아닌경우
			if(id!=0) {
				//글 그룹이 같고, 스탭이 큰 경우
				sql1 = "update boardt set b_step = b_step+1 where b_ref=? and b_step > ?";
				pstmt3 = conn.prepareStatement(sql1);
				pstmt3.setInt(1, ref);
				pstmt3.setInt(2, step);
				pstmt3.executeUpdate();
				step = step+1;
				level = level+1;
			}
			//답글 아닌경우
			else {
				ref = number;//글번호
				step = 0;
				level = 0;
			}
			
			sql="insert into boardt(b_id, b_name, b_email, b_title, b_content, b_date, b_pwd, b_ip, b_ref, b_step, b_level) "
					+ "values(?,?,?,?,?,?,?,?,?,?,?)";
			pstmt2 = conn.prepareStatement(sql);
			pstmt2.setInt(1, number);
			pstmt2.setString(2, HanConv.toKor(board.getB_name()));
			pstmt2.setString(3, board.getB_email());
			pstmt2.setString(4, HanConv.toKor(board.getB_title()));
			pstmt2.setString(5, HanConv.toKor(board.getB_content()));
			pstmt2.setTimestamp(6, board.getB_date());
			pstmt2.setString(7, board.getB_pwd());
			pstmt2.setString(8, board.getB_ip());
			pstmt2.setInt(9, ref);
			pstmt2.setInt(10, step);
			pstmt2.setInt(11, level);
			pstmt2.executeUpdate();
			
			re=1;
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(pstmt1 != null ) 	{	pstmt1.close();	}
				if(pstmt2 != null ) 	{	pstmt2.close();	}
				if(conn != null)		{	conn.close();	}
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		//insert 성공해서 수행이 다 되어서 / 실패하면 catch에서 끝나기 때문
		return re;
	}
	
	public ArrayList<BoardBean> listBoard(){
		Connection conn=null;
//		PreparedStatement pstmt=null;
		java.sql.Statement stmt=null;
		ResultSet rs=null;
		
		ArrayList<BoardBean> boardList = new ArrayList<BoardBean>();
		
	
		try {
			conn = getConnection();
			//table에 있는것 몽땅 받아와서 boardList에 저장
			stmt = conn.createStatement();
			//최신글부터 나오게
			String sql = "SELECT * FROM boardt order by b_ref desc, b_step asc"; 
			rs = stmt.executeQuery(sql);
			//받아와서 BoardBean에 넣어주기
			while(rs.next()) {
				BoardBean board = new BoardBean();
				board.setB_id(rs.getInt(1));
				board.setB_name(rs.getString(2));
				board.setB_email(rs.getString(3));
				board.setB_title(rs.getString(4));
				board.setB_content(rs.getString(5));
				board.setB_date(rs.getTimestamp(6));
				board.setB_hit(rs.getInt(7));
				
				//화면에 출력되지는 않고 데이터만 가지고 있음(필요할때만 화면 작업해서 사용가능)
				board.setB_pwd(rs.getString(8));
				board.setB_ip(rs.getString(9));
				board.setB_ref(rs.getInt(10));
				board.setB_step(rs.getInt(11));
				board.setB_level(rs.getInt(12));
				
				boardList.add(board);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(stmt != null) stmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}		
		return boardList;
	}
	
	public BoardBean getBoard(int bid, boolean hitadd){
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		
		//글내용 부터 글 번호까지 담아 보냄
		BoardBean board = new BoardBean();
		try {
			//DBCP로 연결(server.xml)
			conn = getConnection();
			//글 내용볼때는 조회수 증가, 수정시는 증가시키지x
			if(hitadd == true) {
				//조회수
				sql = "update boardT set b_hit = b_hit + 1 where b_id=?";
				pstmt  = conn.prepareStatement(sql);
				pstmt.setInt(1, bid);
				pstmt.executeUpdate();
				//다시 사용할 것이라서 close 해줌 or pstmt 하나 더 만들어서 하기
				pstmt.close();
			}
			sql = "select * from boardT where b_id=?";
			pstmt  = conn.prepareStatement(sql);
			pstmt.setInt(1, bid);
			rs = pstmt.executeQuery();
			
			//여러개면 while, 한개면 if 문을 통해 set해주기
			if (rs.next()) {
				board.setB_id(rs.getInt(1));
				board.setB_name(rs.getString(2));
				board.setB_email(rs.getString(3));
				board.setB_title(rs.getString(4));
				board.setB_content(rs.getString(5));
				board.setB_date(rs.getTimestamp(6));
				board.setB_hit(rs.getInt(7));
				board.setB_pwd(rs.getString(8));
				board.setB_ip(rs.getNString(9));
				board.setB_ref(rs.getInt(10));
				board.setB_step(rs.getInt(11));
				board.setB_level(rs.getInt(12));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return board;
	}
	
	public int deleteBoard(int b_id, String b_pwd) {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int re =-1;
		String pwd = "";
		
		try {
			conn = getConnection();
			//글 번호로 비밀번호 조회
			sql = "select b_pwd from boardt where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, b_id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//조회 한 결과 받기
				pwd = rs.getString(1);
				
				// 비밀번호를 가져온다.
				if(!pwd.equals(b_pwd)) {
					re=0;
				}else {
					sql = "delete from boardt where b_id=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, b_id);
					pstmt.executeUpdate();
					//정상 삭제
					re=1;
				}
			}
			
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		
		return re;
		
	}
	
	public int editBoard(BoardBean board) {	//수정할 것 BoardBean 객체로 받기
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql = "";
		int re =-1;
		String pwd = "";
		
		try {
			conn = getConnection();
			sql = "select b_pwd from boardt where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, board.getB_id());
			rs = pstmt.executeQuery();
			if(rs.next()) {
				pwd = rs.getString(1);
				if(!pwd.contentEquals(board.getB_pwd())) {
					re=0;
				}
				else {
					sql = "update boardT set b_name=?,b_email=?,b_title=?,b_content=? where b_id=? ";
					pstmt = conn.prepareStatement(sql);
	
					pstmt.setString(1, HanConv.toKor(board.getB_name()));
					pstmt.setString(2, board.getB_email());
					pstmt.setString(3, HanConv.toKor(board.getB_title()));
					pstmt.setString(4, HanConv.toKor(board.getB_content()));
					pstmt.setInt(5, board.getB_id()); // 글번호
					
					pstmt.executeUpdate();
					re = 1;
				}	
			}
		}catch (Exception e) {
			e.printStackTrace();
		}finally {
			try {
				if(rs != null) rs.close();
				if(pstmt != null) pstmt.close();
				if(conn != null) conn.close();
			} catch (Exception e2) {
				e2.printStackTrace();
			}
		}
		return re;
	}
}
