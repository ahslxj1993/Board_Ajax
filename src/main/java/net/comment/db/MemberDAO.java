package net.comment.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;

public class MemberDAO {
	private DataSource ds;

	// �����ڿ��� JNDI ���ҽ��� �����Ͽ� Connection ��ü�� ���ɴϴ�
	public MemberDAO() {
		try {
			Context init = new InitialContext();
			ds = (DataSource) init.lookup("java:comp/env/jdbc/OracleDB");
		} catch (Exception ex) {
			System.out.println("DB���� ���� : " + ex);
		}
	}

	public int isId(String id) {
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		int result = -1; // DB�� �ش� id�� �����ϴ�
		try {
			con = ds.getConnection();

			String sql = "select id from member where id = ? ";
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, id);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				result = 0; // DB�� �ش� id�� �ֽ��ϴ�
			}
		} catch (Exception e) {
			e.printStackTrace();
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
	}

}
