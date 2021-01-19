package my.util;

import javax.servlet.http.HttpServletRequest;

public class Myutil {
	
	// ? 다음의 데이터까지 포함한 현재 URL 주소를 알려주는 메소드를 생성
	public static String getCirrentURL(HttpServletRequest request) {
		
		String currentURL = request.getRequestURL().toString();
		// http://localhost:9090/MyMVC/member/memberList.up
		
		String queryString = request.getQueryString();
		//searchType=name&searchWord=%ED%99%8D%EC%8A%B9%EC%9D%98&sizePerPage=10
		
		if(queryString != null){
			currentURL += "?" + queryString;
		}

		currentURL += "?" + queryString;
		// http://localhost:9090/MyMVC/member/memberList.up?searchType=name&searchWord=%ED%99%8D%EC%8A%B9%EC%9D%98&sizePerPage=10
		
		String ctxPath = request.getContextPath();
		// /MyMVC
		
		int beginIndex = currentURL.indexOf(ctxPath) + ctxPath.length();
		
		currentURL = currentURL.substring(beginIndex + 1);
		System.out.println("8" + currentURL);
 		System.out.println("00" + queryString);
		
		return currentURL;
	}
	
	
	// 크로스 사이트 스크립트 공격에 대응하는 안전한 코드 작성하기
	public static String secureCode(String str){
		str = str.replaceAll("<", "&lt;");
		str = str.replaceAll(">", "&gt;");
		
		return str;
	}
}
