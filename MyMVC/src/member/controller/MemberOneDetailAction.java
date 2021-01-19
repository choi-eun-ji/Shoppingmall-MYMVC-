package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.model.*;

public class MemberOneDetailAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

			// 관리자(admin)로 로그인 했을 때만 조회가 가능하도록 한다.
			HttpSession session = request.getSession();
			memberVO loginuser = (memberVO) session.getAttribute("loginuser");

			// 관리자로 로그인 했을 경우
			if (loginuser != null && loginuser.getUserid().equals("admin")) {
				String userid = request.getParameter("userid");
				InterMemberDAO mdao = new MemberDAO();
				memberVO mvo = mdao.memberOneDetail(userid);
				
				request.setAttribute("mvo", mvo);
				
				// 현재 페이지를 돌아갈 페이지로 주소 지정하기
				String goBackURL = request.getParameter("goBackURL");
				request.setAttribute("goBackURL", goBackURL);
				
				super.setRedirect(false);
				super.setViewPage("/WEB-INF/member/memberOneDetail.jsp");
			}
			// 관리자가 아니라면
			else{
				String message = "관리자만 접근이 가능합니다.";
				String loc = "javascript:history.back()";

				request.setAttribute("message", message);
				request.setAttribute("loc", loc);

				super.setRedirect(false);
				super.setViewPage("/WEB-INF/msg.jsp");
			}
	}
}
