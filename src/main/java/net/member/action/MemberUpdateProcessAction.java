package net.member.action;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;

import net.comment.db.Member;
import net.comment.db.MemberDAO;

public class MemberUpdateProcessAction implements Action {

	@Override
	public ActionForward execute(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		String realFolder ="";
		
		//webapp아래에 꼭 폴더를 생성하세요
		String saveFolder="memberupload";
		
		int fileSize = 5*1024*1024; //업로드할 파일의 최대 사이즈는 5메가
		
		//실제 저장경로를 지정합니다.
		ServletContext sc = request.getServletContext();
		realFolder = sc.getRealPath(saveFolder);
		System.out.println("realFolder =["+ realFolder);
		
		try {
			MultipartRequest multi = new MultipartRequest(request, realFolder, fileSize, "utf-8",
															new DefaultFileRenamePolicy());
			String id = multi.getParameter("id");
			String name = multi.getParameter("name");
			int age = Integer.parseInt(multi.getParameter("age"));
			String gender = multi.getParameter("gender");
			String email = multi.getParameter("email");
			String memberfile = multi.getFilesystemName("memberfile");
			
			Member m = new Member();
			m.setAge(age);
			m.setEmail(email);
			m.setGender(gender);
			m.setId(id);
			m.setName(name);
			m.setMemberfile(memberfile);
			
			MemberDAO mdao = new MemberDAO();
			int result = mdao.update(m);
			
			response.setContentType("text/html;charset=utf-8");
			PrintWriter out = response.getWriter();
			out.println("<script>");
			//삽입이 된경우
			if (result == 1) {
				out.println("alert('수정되었습니다')");
				out.println("location.href='BoardList.bo';");
			} else {
				out.println("alert('회원 정보 수정에 실패했습니다');");
				out.println("history.back()");
			}
			out.println("</script>");
			out.close();
			return null;
		} catch (IOException ex) {
			ActionForward forward = new ActionForward();
			forward.setPath("error/error.jsp");
			request.setAttribute("message","프로필 사진 업로드 실패입니다.");
			forward.setRedirect(false);
			return forward;
		} //catch end
	}//execute end;
}
