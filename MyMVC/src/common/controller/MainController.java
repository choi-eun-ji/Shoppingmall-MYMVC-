package common.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MainController extends AbstractController {

	
	@Override // object 클래스에 toString을 재정의하는 것이다.
	public String toString() {
		return "클래스 MainController의 인스턴스 메소드 toString() 호출함###";
	}
	
	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {	
		// System.out.println("### 확인용 MainController의 인스턴스 메소드 execute()가 호출됨 ###");
		super.setRedirect(true);
		super.setViewPage("index.up");
	}
}
