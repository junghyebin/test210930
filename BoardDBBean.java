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
		Context ctx = new InitialContext();	//��ĳ����
		DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/oracle");
		return ds.getConnection();
	}
	
	// �� �Ѿ���� 1 return
	public int insertBoard(BoardBean board) throws Exception{
		Connection conn=null;
		PreparedStatement pstmt1=null;
		PreparedStatement pstmt2=null;
		PreparedStatement pstmt3=null;
		String sql="";
		String sql1="";
		int number = 1;
		ResultSet rs;//����� ����
		int re=-1;
		
		int id = board.getB_id();
		int ref = board.getB_ref();
		int step = board.getB_step();
		int level = board.getB_level();
		
		try {
			conn = getConnection();
			
			//���� ���ڵ� �߿��� ���� ū �۹�ȣ�� ����.(��ȣ+1)
			sql = "select max(b_id) from boardt";
			pstmt1 = conn.prepareStatement(sql);
			rs = pstmt1.executeQuery();
			if(rs.next()){
				number = rs.getInt(1)+1;
			}
			
			System.out.println("@@@### id ===>"+id);
			//���=�۹�ȣ�� 0�� �ƴѰ��
			if(id!=0) {
				//�� �׷��� ����, ������ ū ���
				sql1 = "update boardt set b_step = b_step+1 where b_ref=? and b_step > ?";
				pstmt3 = conn.prepareStatement(sql1);
				pstmt3.setInt(1, ref);
				pstmt3.setInt(2, step);
				pstmt3.executeUpdate();
				step = step+1;
				level = level+1;
			}
			//��� �ƴѰ��
			else {
				ref = number;//�۹�ȣ
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
		//insert �����ؼ� ������ �� �Ǿ / �����ϸ� catch���� ������ ����
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
			//table�� �ִ°� ���� �޾ƿͼ� boardList�� ����
			stmt = conn.createStatement();
			//�ֽűۺ��� ������
			String sql = "SELECT * FROM boardt order by b_ref desc, b_step asc"; 
			rs = stmt.executeQuery(sql);
			//�޾ƿͼ� BoardBean�� �־��ֱ�
			while(rs.next()) {
				BoardBean board = new BoardBean();
				board.setB_id(rs.getInt(1));
				board.setB_name(rs.getString(2));
				board.setB_email(rs.getString(3));
				board.setB_title(rs.getString(4));
				board.setB_content(rs.getString(5));
				board.setB_date(rs.getTimestamp(6));
				board.setB_hit(rs.getInt(7));
				
				//ȭ�鿡 ��µ����� �ʰ� �����͸� ������ ����(�ʿ��Ҷ��� ȭ�� �۾��ؼ� ��밡��)
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
		
		//�۳��� ���� �� ��ȣ���� ��� ����
		BoardBean board = new BoardBean();
		try {
			//DBCP�� ����(server.xml)
			conn = getConnection();
			//�� ���뺼���� ��ȸ�� ����, �����ô� ������Ű��x
			if(hitadd == true) {
				//��ȸ��
				sql = "update boardT set b_hit = b_hit + 1 where b_id=?";
				pstmt  = conn.prepareStatement(sql);
				pstmt.setInt(1, bid);
				pstmt.executeUpdate();
				//�ٽ� ����� ���̶� close ���� or pstmt �ϳ� �� ���� �ϱ�
				pstmt.close();
			}
			sql = "select * from boardT where b_id=?";
			pstmt  = conn.prepareStatement(sql);
			pstmt.setInt(1, bid);
			rs = pstmt.executeQuery();
			
			//�������� while, �Ѱ��� if ���� ���� set���ֱ�
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
			//�� ��ȣ�� ��й�ȣ ��ȸ
			sql = "select b_pwd from boardt where b_id=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, b_id);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//��ȸ �� ��� �ޱ�
				pwd = rs.getString(1);
				
				// ��й�ȣ�� �����´�.
				if(!pwd.equals(b_pwd)) {
					re=0;
				}else {
					sql = "delete from boardt where b_id=?";
					pstmt = conn.prepareStatement(sql);
					pstmt.setInt(1, b_id);
					pstmt.executeUpdate();
					//���� ����
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
	
	public int editBoard(BoardBean board) {	//������ �� BoardBean ��ü�� �ޱ�
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
					pstmt.setInt(5, board.getB_id()); // �۹�ȣ
					
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
