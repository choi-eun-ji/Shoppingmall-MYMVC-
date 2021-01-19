package myshop.controller;

import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import common.controller.AbstractController;
import myshop.model.*;

public class CommentListAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String fk_pnum = request.getParameter("fk_pnum");	// 제품번호
		InterProductDAO pdao = new ProductDAO();
		
		JSONArray jsArr = new JSONArray();
		ArrayList<PurchaseReviewsVO> commentList = pdao.commentList(fk_pnum);
		
		// [{}, [], {}...]
		if(commentList != null && commentList.size() > 0){
			for(PurchaseReviewsVO reviewsvo : commentList){
				JSONObject jsobj = new JSONObject();
				jsobj.put("contents", reviewsvo.getContents());
				jsobj.put("name", reviewsvo.getMvo().getName());
				jsobj.put("writeDate", reviewsvo.getWriteDate());
				
				jsArr.put(jsobj);
			}
		}
		
		String json = jsArr.toString();
		
		request.setAttribute("json", json);
		
		super.setViewPage("/WEB-INF/jsonciew.jsp");
		
	}

}
