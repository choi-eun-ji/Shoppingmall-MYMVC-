package myshop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import common.controller.AbstractController;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;

public class MallHome2Action extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 카테고리 목록을 조회해오기
		super.getCategoryList(request);
		
		super.goBackURL(request);
		// ajax를 사용하여 hit 상품목록을 스크롤 방식으로 페이징처리해서 보여주겠다.
		InterProductDAO pdao = new ProductDAO();
				
		int totalGITCount = pdao.totalPspecCount("1");	// HIT 상품의 전체 갯수를 알아온다.
		System.out.println("~~~ 확인용 totalGITCount : " + totalGITCount);
				
		request.setAttribute("totalGITCount", totalGITCount);
				
		super.setRedirect(false);
		super.setViewPage("/WEB-INF/myshop/mallHome2.jsp");
	}
}
