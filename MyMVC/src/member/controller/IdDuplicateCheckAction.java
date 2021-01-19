package member.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import common.controller.AbstractController;
import member.model.*;

public class IdDuplicateCheckAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		String userid = request.getParameter("userid");
		
		// System.out.println(userid);
		
		InterMemberDAO mdao = new MemberDAO();
		boolean isExists = mdao.idDuplicateCheck(userid);
		
		// json형태의 객체를 생성해준다.
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("isExists", isExists);	// {"isExists":true} 또는 {"isExists":false} 으로 만들어준다. 
		
		String json = jsonObj.toString();	// 문자열 형태인 {"isExists":true} 또는 {"isExists":false} 으로 만들어준다. 
		
		// System.out.println(json);
		
		// jsonview.jsp 인 view 단으로 json타입인 문자열인 json을 넘겨준다.
		// 이코드를 써주지 않으면 ajax는 값을 받아오는데 성공인지 성공이 아닌지 알 수 없다. 따라서 반드시 써줘야 한다.!!!!!!!1
		request	.setAttribute("json", json);
		super.setViewPage("/WEB-INF/jsonview.jsp");	// 이페이지의 값 : {"isExists":false} or  {"isExists":true}
	}
	
}
