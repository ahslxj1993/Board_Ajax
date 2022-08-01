package net.board.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class BoardDAO {
	private DataSource ds;

	// �����ڿ��� JNDI ���ҽ��� �����Ͽ� Connection ��ü�� ���ɴϴ�
	public BoardDAO() {
		try {
			Context init = new InitialContext();
			ds = (DataSource) init.lookup("java:comp/env/jdbc/OracleDB");
		} catch (Exception ex) {
			System.out.println("DB���� ���� : " + ex);
		}
	}
	
	
	
	
	//���� ���� ���ϱ�
	public int getListCount() {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int x =0;
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement("select count(*) from board");
			rs = pstmt.executeQuery();
			
			if (rs.next()) {
				x= rs.getInt(1);
			}
		} catch (Exception ex) {
			System.out.println("getListCount() ���� : "+ ex);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		} // finally end
		return x;
	}//get ListCount end




	public List<BoardBean> getBoardList(int page, int limit) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<BoardBean> list = new ArrayList<BoardBean> ();
		
		//page : ������
		//limit : �������� ����� ��
		//board_re_ref desc, board_re_seq asc �� ���� ������ ����
		//�������� �´� rnum�� ���� ��ŭ �������� �������Դϴ�
		
		String board_list_sql = "select * "
							+ " from (select rownum rnum, j.* "
							+ "		  from (select board.* , nvl(cnt,0) cnt "
							+ "				from board left outer join (select comment_board_num, count(*) cnt"
							+ "											from comm "
							+ "											group by comment_board_num) "
							+ "				on board_num = comment_board_num "
							+ "				order by BOARD_RE_REF desc, "
							+ "				BOARD_RE_SEQ asc) j "
							+ "			where rownum <= ? "
							+ "		) "
							+ " where rnum>=? and rnum <=? ";
		
		//���������� 10���� ����� ��� 1������ ,2������, 3������, 4������ ....
		int startrow = (page -1) * limit +1; //�б� ������ row ��ȣ (1 11 21 31 41 51) 
		int endrow = startrow + limit -1; // ���� ������ row ��ȣ (10 20 30 40 50)
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(board_list_sql);
			pstmt.setInt(1, endrow);
			pstmt.setInt(2, startrow);
			pstmt.setInt(3, endrow);
			rs = pstmt.executeQuery();
			
			//DB���� ������ �����͸� VO��ü�� ����ϴ�
			while (rs.next()) {
				BoardBean board = new BoardBean();
				board.setBoard_num(rs.getInt("BOARD_NUM"));
				board.setBoard_name(rs.getString("BOARD_NAME"));
				board.setBoard_subject(rs.getString("BOARD_SUBJECT"));
				board.setBoard_content(rs.getString("BOARD_CONTENT"));
				board.setBoard_file(rs.getString("Board_FILE"));
				board.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));
				board.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));
				board.setBoard_re_seq(rs.getInt("BOARD_RE_SEQ"));
				board.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));
				board.setBoard_date(rs.getString("BOARD_DATE"));
				board.setCnt(rs.getInt("cnt"));
				list.add(board); //���� ���� ��ü�� ����Ʈ�� �����մϴ�
			}
		} catch (Exception ex) {
			System.out.println("getBoardList() ���� : "+ ex);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		} // finally end
		return list;
		
	}//getBoardList end



	//�� ����ϱ�
	public boolean boardInsert(BoardBean board) {
		Connection con = null;
		PreparedStatement pstmt = null;
		int result =0;
		
		try {
			con = ds.getConnection();
			
			String max_sql = "(select nvl(max(board_num),0)+1 from board)";
			
			String sql = "insert into  board "
						+ " (BOARD_NUM,	BOARD_NAME, BOARD_PASS,	BOARD_SUBJECT, "
						+ "	 BOARD_CONTENT,	BOARD_FILE, BOARD_RE_REF, "
						+ "	 BOARD_RE_LEV, BOARD_RE_SEQ, BOARD_READCOUNT) "
						+ "	values ("+ max_sql+ " ,?,?,?, "
						+ "			?,?," + max_sql + ", "
						+ "			?,?,?)";
			
			//���ο� ���� ����ϴ� �κ��Դϴ�.
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, board.getBoard_name());
			pstmt.setString(2, board.getBoard_pass());
			pstmt.setString(3, board.getBoard_subject());
			pstmt.setString(4, board.getBoard_content());
			pstmt.setString(5, board.getBoard_file());
			
			//������ ��� BOARD_RE_LEV, BOARD_RE_SEQ �ʵ� ���� 0 �Դϴ�.
			pstmt.setInt(6,0);
			pstmt.setInt(7,0);
			pstmt.setInt(8,0);
			
			result = pstmt.executeUpdate();
			if (result ==1) {
				System.out.println("������ ������ ��� �Ϸ�Ǿ����ϴ�");
				return true;
			}
		} catch (Exception ex) {
			System.out.println("boardInsert() ���� : "+ ex);
			ex.printStackTrace();
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		} // finally end
		return false;
	} //boardInsert () end




	public void setReadCountUpdate(int num) {
		Connection con = null;
		PreparedStatement pstmt = null;
		
		String sql = "update board "
					+ " set BOARD_READCOUNT = BOARD_READCOUNT+1 "
					+ " where BOARD_NUM = ?";
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, num);
			pstmt.executeUpdate();
		} catch (SQLException ex) {
			System.out.println("setReadCountUpdate() ���� : "+ ex);
		} finally {
			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
			} // finally end
	}//setReadCountUpdate () end



	//�� ���� ����
	public BoardBean getDetail(int num) {
		BoardBean board = null;
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement("select * from  board where BOARD_NUM = ?");
			pstmt.setInt(1,num);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				board = new BoardBean();
				board.setBoard_num(rs.getInt("BOARD_NUM"));
				board.setBoard_name(rs.getString("BOARD_NAME"));
				board.setBoard_subject(rs.getString("BOARD_SUBJECT"));
				board.setBoard_content(rs.getString("BOARD_CONTENT"));
				board.setBoard_file(rs.getString("BOARD_FILE"));
				board.setBoard_re_ref(rs.getInt("BOARD_RE_REF"));
				board.setBoard_re_lev(rs.getInt("BOARD_RE_LEV"));
				board.setBoard_re_seq(rs.getInt("BOARD_RE_SEQ"));
				board.setBoard_readcount(rs.getInt("BOARD_READCOUNT"));
				board.setBoard_date(rs.getString("BOARD_DATE"));
				
			}
		} catch (Exception ex) {
			System.out.println("getDetail() ���� : " + ex);
		} finally {
			if (rs != null)
				try {
					rs.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (pstmt != null)
				try {
					pstmt.close();
				} catch (SQLException e) {
					System.out.println(e.getMessage());
				}

			if (con != null)
				try {
					con.close();
				} catch (Exception e) {
					System.out.println(e.getMessage());
				}
		} // finally end
		return board;
	} //getDetail()  end

}
