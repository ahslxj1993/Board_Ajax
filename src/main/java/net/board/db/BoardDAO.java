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




	public boolean isBoardWriter(int num, String pass) {
		Connection con = null ;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		boolean result = false;
		String board_sql = "select BOARD_PASS from board where BOARD_NUM = ?";
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(board_sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				if(pass.equals(rs.getString("BOARD_PASS"))) {
					result = true;
				}
			}
		} catch (SQLException ex) {
			System.out.println("isBoardWriter() ���� : " + ex);
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
		
		return result;
	} // isBoardWriter() end




	public boolean boardModify(BoardBean modifyboard) {
		Connection con = null;
		PreparedStatement pstmt = null;
		String sql = "update board "
					+ " set BOARD_SUBJECT = ?, BOARD_CONTENT = ? , BOARD_FILE = ? "
					+ " where BOARD_NUM = ? ";
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, modifyboard.getBoard_subject());
			pstmt.setString(2, modifyboard.getBoard_content());
			pstmt.setString(3, modifyboard.getBoard_file());
			pstmt.setInt(4, modifyboard.getBoard_num());
			int result = pstmt.executeUpdate();
			if (result == 1) {
				System.out.println("������Ʈ ����");
				return true;
			}
		} catch (Exception ex) {
			System.out.println("boardModify() ���� : "+ ex);
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
	} //boardModify () end




	public int boardReply(BoardBean board) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int num = 0 ;
		
		//board ���̺��� �۹�ȣ�� ���ϱ� ���� board_num �÷��� �ִ밪+1�� ���ؿɴϴ�
		String board_max_sql = "select max(board_num)+1 from board";
		/*
		 * �亯�� �� ���� �� �׷� ��ȣ�Դϴ�.
		 * �亯�� �ް� �Ǹ� �亯 ���� �� ��ȣ�� ���� ���ñ� ��ȣ�� ���� ó���Ǹ鼭 ���� �׷쿡 ���ϰ� �˴ϴ�.
		 * �� ��Ͽ��� �����ٶ� �ϳ��� �׷����� ������ ��µ˴ϴ�
		 * */
		int re_ref = board.getBoard_re_ref();
		
		/*
		 * ����� ���̸� �ǹ��մϴ�.
		 * ������ ���� ����� ��� �� �� �ѹ� �鿩���� ó���� �Ȱ� ��ۿ� ���� ����� �鿩���Ⱑ �ι� ó���ǰ� �մϴ�.
		 * ������ ��쿡�� �� ���� 0�̰� ������ ����� 1, ����� ����� 2�� �˴ϴ�
		 * */
		int re_lev = board.getBoard_re_lev();
		
		//���� ���ñ� �߿��� �ش���� ��µǴ� �����Դϴ�.,
		int re_seq = board.getBoard_re_seq();
		try {
			con = ds.getConnection();
			
			//Ʈ�������� �̿��ϱ� ���ؼ� setAutoCommit�� false�� �����մϴ�.
			con.setAutoCommit(false);
			pstmt = con.prepareStatement(board_max_sql);
			rs=pstmt.executeQuery();
			if(rs.next()) {
				num=rs.getInt(1);
			}
			pstmt.close();
			
			//BOARD_RE_REF, BOARD_RE_SEQ ���� Ȯ���Ͽ� �����ۿ� ����� �޷��ִٸ�
			//�޸� ��۵��� BOARD_RE_SEQ ���� 1�� ������ŵ�ϴ�.
			//���� ���� �̹� �޸� ��ۺ��� �տ� ��µǰ� �ϱ� ���ؼ� �Դϴ�.
			
			String sql = "update board "
						+ " set BOARD_RE_SEQ = BOARD_RE_SEQ + 1 "
						+ "	where BOARD_RE_REF = ? "
						+ " and BOARD_RE_SEQ > ?";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setInt(1, re_ref);
			pstmt.setInt(2, re_seq);
			pstmt.executeUpdate();
			pstmt.close();
			
			//����� �亯 ���� BOARD_RE_LEV, BOARD_RE_SEQ���� ���� �ۺ��� 1�� ������ŵ�ϴ�.
			re_seq = re_seq +1;
			re_lev = re_lev +1;
			
			sql = "insert into board "
					+ "(BOARD_NUM, BOARD_NAME, BOARD_PASS, BOARD_SUBJECT, "
					+ " BOARD_CONTENT, BOARD_FILE, BOARD_RE_REF, "
					+ " BOARD_RE_LEV, BOARD_RE_SEQ, BOARD_READCOUNT) "
					+ " values (" + num + ", "
					+ "			?,?,?, "
					+ "			?,?,?, "
					+ "			?,?,?)";
			
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, board.getBoard_name());
			pstmt.setString(2, board.getBoard_pass());
			pstmt.setString(3, board.getBoard_subject());
			pstmt.setString(4, board.getBoard_content());
			pstmt.setString(5, ""); //�亯���� ������ ���ε� ���� �ʽ��ϴ�
			pstmt.setInt(6, re_ref); //������ �۹�ȣ
			pstmt.setInt(7, re_lev);
			pstmt.setInt(8, re_seq);
			pstmt.setInt(9, 0); //READ_COUNT(��ȸ��) �� 0
			if (pstmt.executeUpdate() ==1) {
				con.commit(); //commit �մϴ�
			} else {
				con.rollback();
			}
		} catch (Exception ex) {
			ex.printStackTrace();
			System.out.println("boardReply() ���� : "+ ex);
			if( con != null) {
				try {
					con.rollback();
				} catch(SQLException e) {
					e.printStackTrace();
				}
			}
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
					con.setAutoCommit(true);
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
		} // finally end
		return num;
	}//boardReply() end




	public boolean boardDelete(int num) {
		Connection con = null;
		PreparedStatement pstmt = null, pstmt2 = null;
		ResultSet rs = null;
		
		String select_sql = "select BOARD_RE_REF, BOARD_RE_LEV, BOARD_RE_SEQ "
							+ " from board "
							+ " where BOARD_NUM = ? ";
		
		String board_delete_sql = "delete from board"
				+ "				where BOARD_RE_REF = ?"
				+ "				and	BOARD_RE_LEV >= ?"
				+ "				and BOARD_RE_SEQ >= ?"
				+ "				and BOARD_RE_SEQ <= ("
				+ "									nvl((SELECT min(board_re_seq)-1"
				+ "										FROM BOARD "
				+ "										WHERE BOARD_RE_REF=?"
				+ "										AND BOARD_RE_LEV=?"
				+ "										AND BOARD_RE_SEQ>?) , "
				+ "										(SELECT max(board_re_seq)"
				+ "										FROM BOARD "
				+ "										WHERE BOARD_RE_REF =? ))"
				+ "									)";
		boolean result_check = false;
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(select_sql);
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();
			if (rs.next()) {
				pstmt2 = con.prepareStatement(board_delete_sql);
				pstmt2.setInt(1, rs.getInt("BOARD_RE_REF"));
				pstmt2.setInt(2, rs.getInt("BOARD_RE_LEV"));
				pstmt2.setInt(3, rs.getInt("BOARD_RE_SEQ"));
				pstmt2.setInt(4, rs.getInt("BOARD_RE_REF"));
				pstmt2.setInt(5, rs.getInt("BOARD_RE_LEV"));
				pstmt2.setInt(6, rs.getInt("BOARD_RE_SEQ"));
				pstmt2.setInt(7, rs.getInt("BOARD_RE_REF"));
				
				int count = pstmt2.executeUpdate();
				
				if (count >=1)
					result_check= true; //������ �ȵȰ�쿡�� false�� ��ȯ�մϴ�.
			}
		} catch (Exception ex ) {
			System.out.println("boardDelete() ���� : "+ ex);
			ex.printStackTrace();
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
		
		return result_check;
	}//boardDelete () end

}
