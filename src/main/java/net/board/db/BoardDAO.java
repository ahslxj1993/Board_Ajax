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

	// 생성자에서 JNDI 리소스를 참조하여 Connection 객체를 얻어옵니다
	public BoardDAO() {
		try {
			Context init = new InitialContext();
			ds = (DataSource) init.lookup("java:comp/env/jdbc/OracleDB");
		} catch (Exception ex) {
			System.out.println("DB연결 실패 : " + ex);
		}
	}
	
	
	
	
	//글의 갯수 구하기
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
			System.out.println("getListCount() 에러 : "+ ex);
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
		
		//page : 페이지
		//limit : 페이지당 목록의 수
		//board_re_ref desc, board_re_seq asc 에 의해 정렬한 것을
		//조건절에 맞는 rnum의 범위 만큼 가져오는 쿼리문입니다
		
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
		
		//한페이지당 10개씩 목록인 경우 1페이지 ,2페이지, 3페이지, 4페이지 ....
		int startrow = (page -1) * limit +1; //읽기 시작할 row 번호 (1 11 21 31 41 51) 
		int endrow = startrow + limit -1; // 읽을 마지막 row 번호 (10 20 30 40 50)
		
		try {
			con = ds.getConnection();
			pstmt = con.prepareStatement(board_list_sql);
			pstmt.setInt(1, endrow);
			pstmt.setInt(2, startrow);
			pstmt.setInt(3, endrow);
			rs = pstmt.executeQuery();
			
			//DB에서 가져온 데이터를 VO객체에 담습니다
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
				list.add(board); //값을 담은 객체를 리스트에 저장합니다
			}
		} catch (Exception ex) {
			System.out.println("getBoardList() 에러 : "+ ex);
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

}
