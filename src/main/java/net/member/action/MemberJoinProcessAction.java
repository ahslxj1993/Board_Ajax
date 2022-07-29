package net.member.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.comment.db.Member;
import net.comment.db.MemberDAO;

public class MemberJoinProcessAction implements Action {
	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String id = request.getParameter("id");
		String pass = request.getParameter("pass");
		String name = request.getParameter("name");
		int age = Integer.parseInt(request.getParameter("age"));
		String gender = request.getParameter("gender");
		String email = request.getParameter("email");
		
		Member m = new Member();
		m.setAge(age);
		m.setEmail(email);
		m.setGender(gender);
		m.setId(id);
		m.setName(name);
		m.setPassword(pass);
		
		response.setContentType("text/html;charset=utf-8");
		PrintWriter out = response.getWriter();
		
		MemberDAO mdao = new MemberDAO();
		
		int result = mdao.insert(m);
		
		if (result ==0) {
			System.out.println("ȸ������ ���� �Դϴ�");
			ActionForward forward = new ActionForward();
			forward.setRedirect(false);
			request.setAttribute("message","ȸ������ �����Դϴ�");
			forward.setPath("error/error.jsp");
			return forward;
		}
		
		out.println("<script>");
		if (result ==1) {
			out.println("alert('ȸ�������� �����մϴ�.');");
			out.println("location.href='login.net';");
		} else if (result == -1) {
			out.println("alert('���̵� �ߺ��Ǿ����ϴ�. �ٽ� �Է��ϼ���');");
			//���ΰ�ħ �Ǿ� ������ �Է��� �����Ͱ� ��Ÿ���� �ʽ��ϴ�.
			//out.println("location.href='join.net'");
			out.println("history.back()"); //��й�ȣ�� ������ �ٸ� �����ʹ� �����Ǿ� �ֽ��ϴ�.
		}
		out.println ("</script>");
		out.close();
		return null;
	}
}
