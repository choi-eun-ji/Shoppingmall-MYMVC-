package myshop.controller;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import common.controller.AbstractController;
import myshop.model.*;

public class LikeDislikeCountAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String pnum = request.getParameter("pnum");
		InterProductDAO pdao = new ProductDAO();
		
		Map<String, Integer> hashMap = pdao.getLikeDislikeCnt(pnum);
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("likecnt", hashMap.get("likecnt"));
		jsonObj.put("dislikecnt", hashMap.get("dislikecnt"));
		
		String json = jsonObj.toString();
		
		request.setAttribute("json", json);
		
		super.setRedirect(false);
		super.setViewPage("/WEB-INF/jsonview.jsp");
	}
}
