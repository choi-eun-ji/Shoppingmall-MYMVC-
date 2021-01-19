package member.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.model.InterMemberDAO;
import member.model.MemberDAO;

public class PwdFindAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String method = request.getMethod();
	    
	      if (method.equalsIgnoreCase("POST")) {
	         // 비밀번호 찾기 modal 창에서 찾기 버튼을 클릭했을 경우
	         
	         String userid = request.getParameter("userid");
	         String email = request.getParameter("email");
	         
	         InterMemberDAO mdao = new MemberDAO();
	         
	         Map<String, String> paraMap = new HashMap<>();
	         paraMap.put("userid", userid);
	         paraMap.put("email", email);
	         
	         boolean isUserExist = mdao.isUserExist(paraMap);
	         
	         // 메일이 정상적으로 전달되었는지 유무를 알아보는 방법
	         boolean sendMailSuccess = true;
	         
	         // 회원으로 존재하는 경우
	         if(isUserExist){
	        	Random rnd = new Random();
	        	
	        	// 인증키는 영문소문자 5글자 + 숫자 7글자로 만들겠습니다.
	        	// 예: certificationCode ==> dngrn4745003
	        	String certificationCode = "";
	        	
	        	char randchar = ' ';
	        	for(int i=0; i<5; i++){
	            /*
	                min 부터 max 사이의 값으로 랜덤한 정수를 얻으려면 
	                int rndnum = rnd.nextInt(max - min + 1) + min;
	                                영문 소문자 'a' 부터 'z' 까지 랜덤하게 1개를 만든다.     
	            */
	        		randchar = (char) (rnd.nextInt('z' - 'a' + 1) + 'a');
	        		certificationCode += randchar;
	        	}// end of for-------------------
	        	
	        	int random = 0;
	        	for(int i=0; i<7; i++){
	        		random = rnd.nextInt(9 - 0 + 1) + 0;
	        		certificationCode += random;
	        	}
	        	
	        	// System.out.println("~~~~ 확인용 certificationCode =>" + certificationCode );
	        	
	        	// 랜덤하게 생성한 인증코드(certificationCode)를 비밀번호 찾기를 하고자 하는 사용자의 email로 전송시킨다. 
	        	
	        	GoogleMail mail = new GoogleMail();
	        	
	        	try{
	        		mail.sendmail(email, certificationCode);
	        		HttpSession session = request.getSession();
	        		// 발급한 인증코드를 세션에 저장시킴.
		        	session.setAttribute("certificationCode", certificationCode);
	
	        	}catch(Exception e){
	        		e.printStackTrace();
	        		sendMailSuccess = false;
	        	}
	        }

	         request.setAttribute("isUserExist", isUserExist);
	         request.setAttribute("userid", userid);
	         request.setAttribute("email", email);
	         request.setAttribute("sendMailSuccess", sendMailSuccess);
	         
	         
	       }// end of if (method.equalsIgnoreCase("POST")) {});--------------

	      request.setAttribute("method", method);
	      
	      super.setRedirect(false);
	      super.setViewPage("/WEB-INF/login/PwdFind.jsp");	
	      
	}
	

}
