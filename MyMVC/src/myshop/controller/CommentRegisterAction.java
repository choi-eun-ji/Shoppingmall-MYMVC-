package myshop.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
 
import common.controller.AbstractController;
import my.util.Myutil;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;
import myshop.model.PurchaseReviewsVO;

public class CommentRegisterAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

 		String fk_pnum = request.getParameter("fk_pnum");
 		String fk_userid = request.getParameter("fk_userid");
 		String contents = request.getParameter("contents");
	
 		// 크로스 사이트 스크립트 공격에 대응하는 안전한 코드(시큐어 코드) 작성하기
 		contents = Myutil.secureCode(contents);
 		contents.replaceAll("\r\n", "<br>");
 		
 		PurchaseReviewsVO reviewsVO = new PurchaseReviewsVO();
 		reviewsVO.setFk_userid(fk_userid);
 		reviewsVO.setFk_pnum(Integer.parseInt(fk_pnum));
 		reviewsVO.setContents(contents);
 		
 		InterProductDAO pdao = new ProductDAO();
 		int n = pdao.addComment(reviewsVO);
 		
 		
	}
}
