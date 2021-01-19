package common.controller;

import java.sql.SQLException;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import member.model.memberVO;
import my.util.Myutil;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;

// 공통인 것들만 구현해주는 추상 클래스이다.
public abstract class AbstractController implements InterCommand {

	/*
    === 다음의 나오는 것은 우리끼리한 약속이다. ===

       ※ view 단 페이지(.jsp)로 이동시 forward 방법(dispatcher)으로 이동시키고자 한다라면 
       자식클래스에서는 부모클래스에서 생성해둔 메소드 호출시 아래와 같이 하면 되게끔 한다.
     
    super.setRedirect(false);(데이터를 바꾸는 것 ) 
    super.setViewPage("/WEB-INF/index.jsp");
    
    
        ※ URL 주소를 변경하여 페이지 이동시키고자 한다라면
        즉, sendRedirect 를 하고자 한다라면(페이지만 바꾸는것을 의미)
        자식클래스에서는 부모클래스에서 생성해둔 메소드 호출시 아래와 같이 하면 되게끔 한다.
          
    super.setRedirect(true);
    super.setViewPage("registerMember.up");               
    */
	
	private boolean isRedirect = false;	 
	// isRedirect 변수의 값이 false 이라면 view 단 페이지로 forward 방법(dispatcher)으로 이동시키고자 한다.
	// isRedirect 변수의 값이 true 이라면 sendRedirect 로 페이지이동을 시키겠다. 
	
	private String viewPage;	// isRedirect 값이 false 이라면 viewPate 가 view단 페이지(.jsp)의 경로명 이거나, 
								// isRedirect 값이 true 이라면 이동해야할 페이지 URL 주소이다. 

	
	// private 접근제한자를 상속받은 페이지에서 변수에 접근하기 위한 getter setter method 생성
	public boolean isRedirect() {
		return isRedirect;
	}

	public void setRedirect(boolean isRedirect) {
		this.isRedirect = isRedirect;
	}

	public String getViewPage() {
		return viewPage;
	}

	public void setViewPage(String viewPage) {
		this.viewPage = viewPage;
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	// 로그인 유무를 검사해서 로그인 했으면 true를 리턴해주고 
	// 로그인 하지 않았으면 false를 리턴해주도록 한다.
	public boolean checkLogin(HttpServletRequest request) {
		
		HttpSession session = request.getSession();
		memberVO loginuser = (memberVO) session.getAttribute("loginuser");
		
		// 로그인 한 경우
		if(loginuser != null){
			return true;
		}
		else{
			return false;
		}
	}
	
	//////////////////////////////////////////////////////////////////////////////////
	// *** 제품목록(Category)를 보여줄 메소드를 생성하기 ***
	// vo를 사용하지 않고 Map 으로 처리해보겠습니다.
	public void getCategoryList(HttpServletRequest request) throws SQLException{
		InterProductDAO pdao = new ProductDAO();
		ArrayList<HashMap<String, String>> categoryList = pdao.getCategoryList();
	
		request.setAttribute("categoryList", categoryList);
	}
	
	// 로그인을 하면 시작페이지로 가는것이 아니라 방금 보았던 그 페이지 그대로 가기 위한 것이다.
	public void goBackURL(HttpServletRequest request) {
		HttpSession session = request.getSession();
		session.setAttribute("goBackURL", Myutil.getCirrentURL(request));
	}
	
}
