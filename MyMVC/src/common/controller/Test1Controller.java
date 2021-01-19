package common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class Test1Controller extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		request.setAttribute("name", "최은지");
		
		super.setViewPage("WEB-INF/test1.jsp");
	}

}
