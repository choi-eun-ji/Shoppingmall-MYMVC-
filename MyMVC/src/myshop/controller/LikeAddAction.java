package myshop.controller;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import common.controller.AbstractController;
import myshop.model.InterProductDAO;
import myshop.model.ProductDAO;

public class LikeAddAction extends AbstractController {

	@Override
	public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String pnum = request.getParameter("pnum");
		String userid = request.getParameter("userid");
		System.out.println(pnum + " " + userid);
		Map<String, String> paraMap = new HashMap<>();
		paraMap.put("pnum", pnum);
		paraMap.put("userid", userid);
		
		InterProductDAO pdao = new ProductDAO();
		
		int n = 0;
		
		
		n = pdao.likeAdd(paraMap);
		
		System.out.println("ddd"+ n);
		String msg = "";
		
		if(n==1){
			msg = "해당 제품에\n 좋아요를 클릭하셨습니다.";
		}else{
			msg = "이미 좋아요를 클릭하셨기에 두번 이상 좋아요는 불가합니다.";
		}
		
		
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("msg", msg);
		
		String json = jsonObj.toString();
		
		request.setAttribute("json", json);
		super.setViewPage("/WEB-INF/jsonview.jsp");
	}

}
