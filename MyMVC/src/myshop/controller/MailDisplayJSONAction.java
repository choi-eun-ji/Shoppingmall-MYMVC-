package myshop.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import common.controller.AbstractController;
import myshop.model.*;

public class MailDisplayJSONAction extends AbstractController {
   
   // JSONArray 사용부분

   @Override
   public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
      
      String sname = request.getParameter("sname");
      String start = request.getParameter("start");
      String len = request.getParameter("len");
      
      /*
             맨 처음에는 sname("HIT")상품을  start("1") 부터 len("8")개를 보여준다.
             더보기... 버튼을 클릭하면  sname("HIT")상품을  start("9") 부터 len("8")개를 보여준다.
             또  더보기... 버튼을 클릭하면  sname("HIT")상품을  start("17") 부터 len("8")개를 보여준다.      
      */
      
      InterProductDAO pdao = new ProductDAO();
      
      Map<String, String> paraMap = new HashMap<String, String>();
      paraMap.put("sname", sname);
      paraMap.put("start", start); // start  "1"  "9"  "17"
      // 여기서 rno를 구분할 end를 받은 값으로 만들어 줌(공식참고)
      String end = Integer.toString(Integer.parseInt(start) + Integer.parseInt(len) - 1);
      paraMap.put("end", end);    // end   < start + len - 1 >
      
      ArrayList<ProductVO> prodList = pdao.selectBySpecName(paraMap);
      
      // List로 받았기 때문에 ajax로 돌려줄때도 json 객체에 담아서 json List인 JSONArray에 담아서 보내준다
      JSONArray jsonArr = new JSONArray();   // []
      
      // selectBySpecName에서 prodList의 객체를 생성해줬기 때문에 null이 올 수 없음 => 내용물이 있는지 없는지는 size()로 판별해야 함
      if (prodList.size() > 0) {
         // 내용물이 있을 때(검색결과가 있을 때)
         for(ProductVO pvo : prodList) {
            JSONObject jsonObj = new JSONObject();
            
            jsonObj.put("pnum", pvo.getPnum());
            jsonObj.put("pname", pvo.getPname());
            jsonObj.put("code", pvo.getCategvo().getCode());
            jsonObj.put("pcompany", pvo.getPcompany());
            jsonObj.put("pimage1", pvo.getPimage1());
            jsonObj.put("pimage2", pvo.getPimage2());
            jsonObj.put("pqty", pvo.getPqty());
            jsonObj.put("price", pvo.getPrice());
            jsonObj.put("saleprice", pvo.getSaleprice());
            jsonObj.put("sname", pvo.getSvo().getSname());
            jsonObj.put("pcontent", pvo.getPcontent());
            jsonObj.put("point", pvo.getPoint());
            jsonObj.put("pinputdate", pvo.getPinputdate());
            jsonObj.put("discountPercent", pvo.getDiscountPercent());
            
            jsonArr.put(jsonObj);
            
            // jsonObj 내부
            // {"pnum":2, "pname", "스마트TV", "code":"1000000", "pcompany":"삼성", ... , "pinputdate":"2020-11-13"}
            
            // 위와같은 jsonObj 들을 JSONArray에 넣어준다
         }// end of for(ProductVO pvo : prodList) {--------------------
         
         String json = jsonArr.toString();
         
         // 만약에 select 되어진 정보가 없다라면 []로 나오므로 null이 아닌 요소가 없는 [] 빈배열로 나온다
         
         request.setAttribute("json", json);
         System.out.println("dd");
         System.out.println("확인용 json => " + json);
         
         super.setRedirect(false);
         super.setViewPage("/WEB-INF/jsonview.jsp");
         
      }else {
         
      }
      
      
      
      
      
   }

}