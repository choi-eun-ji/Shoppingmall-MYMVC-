package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.model.memberVO;

public class CoinPurchaseTypeChoiceAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		// 코인충전을 하기 위한 전제조건은 먼저 로그인을 해야 한다.
		if(super.checkLogin(request)){
			
			// 로그인을 했으면! 
			String userid = request.getParameter("userid");
			
			HttpSession session = request.getSession();
			memberVO loginuser = (memberVO) session.getAttribute("loginuser");
			
			if(loginuser.getUserid().equals(userid)){
				// 로그인한 사용자가 자신의 코인을 충전하는 경우
				super.setRedirect(false);
				super.setViewPage("/WEB-INF/member/coinPurchaseTypeChoice.jsp");
			}
			else{
				String message = "다른 사용자의 코인충전 시도는 불가합니다.!!";
				String loc = "javascript:history.back()";
				
				request.setAttribute("message", message);
				request.setAttribute("loc", loc);
				
				super.setRedirect(false);
				super.setViewPage("/WEB-INF/msg.jsp");
				return;
			}
		}
		else{
			// 로그인을 안했으면!
			String message = "코인충전을 하기 위해서는 먼저 로그인을 하세요!!!";
			String loc = "javascript:history.back()";
			
			request.setAttribute("message", message);
			request.setAttribute("loc", loc);
			
			super.setRedirect(false);
			super.setViewPage("/WEB-INF/msg.jsp");
		}
	}
}
