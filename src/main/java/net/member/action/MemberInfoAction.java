package net.member.action;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.comment.db.Member;
import net.comment.db.MemberDAO;

public class MemberInfoAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		ActionForward forward = new ActionForward();
		String id = request.getParameter("id");
		MemberDAO mdao = new MemberDAO();
		Member m = mdao.member_info(id);
		
		if(m==null) {
			forward.setPath("error/error.jsp");
			forward.setRedirect(false);
			request.setAttribute("messasge","아이디에 해당하는 정보가 없습니다.");
			return forward;
		}
		
		forward.setPath("member/memberInfo.jsp");
		forward.setRedirect(false);
		request.setAttribute("memberinfo" , m);
		return forward;

	}

}
