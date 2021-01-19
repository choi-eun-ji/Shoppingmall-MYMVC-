package member.controller;

import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import common.controller.AbstractController;
import member.model.*;
import my.util.Myutil;

public class MemberListAction extends AbstractController {

   @Override
   public void execute(HttpServletRequest request, HttpServletResponse response) throws Exception {
      
      
      // 관리자로 로그인 했을 때만 조회가 가능하도록 한다
      HttpSession session = request.getSession();
      memberVO loginuser = (memberVO)session.getAttribute("loginuser");
      
      if (loginuser != null && "admin".equals(loginuser.getUserid())) {

         InterMemberDAO mdao = new MemberDAO();
         
         // *** 페이징 처리를 안한 전체 회원목록 보여주기 *** //
         //List<MemberVO> memberList = mdao.selectAllMember();
         
         // *** 페이징 처리를 안한 검색한 회원 보여주기 *** //
         String searchType = request.getParameter("searchType");
         String searchWord = request.getParameter("searchWord");
         
         
         Map<String, String> paraMap = new HashMap<String, String>();
         paraMap.put("searchType", searchType);
         paraMap.put("searchWord", searchWord);
         
         // *** 페이징 처리를 안한 검색한 회원 보여주기 *** //
         //List<MemberVO> memberList = mdao.selectMember(paraMap);
         
         
         // *** 시작: 페이징 처리를 한 검색한 회원 보여주기 *** //
         
         // currentShowPageNo : 사용자가 보고자하는 페이지 넘버
         // 메뉴에서 회원목록 만을 클릭했을 경우에는 currentShowPageNo은 null이 된다.
         // currentShowPageNo이 null이라면 currentShowPageNo을 1페이지로 바꿔줘야 한다(null 페이지 안됨)
         String currentShowPageNo = request.getParameter("currentShowPageNo");
          
         // 주소창에서 currentShowPageNo 숫자가 아닌 문자열을 넣었을 때 실행
         try {
            Integer.parseInt(currentShowPageNo);
         } catch(NumberFormatException e) {
            currentShowPageNo = "1";
         }
         
         // sizePerPage : 페이지 당 화면상에 보여줄 회원의 갯수 => 10 or 5 or 3
         // 메뉴에서 회원목록 만을 클릭했을 경우에는 sizePerPage은 null이 된다.
         // sizePerPage이 null이라면 sizePerPage을 10개로 바꿔줘야 한다(null개 안됨)
         String sizePerPage = request.getParameter("sizePerPage");
         
         if (currentShowPageNo == null) {
            currentShowPageNo = "1";
         }
         
         
         
         // 정해진 3, 5, 10 값이 아닐경우(get을 통한 잘못된 접근), 처음 페이지에 들어왔을 경우 10으로 강제지정해준다 => 기본값
         if (sizePerPage == null || !("3".equals(sizePerPage) || "5".equals(sizePerPage) || "10".equals(sizePerPage))) {
            sizePerPage = "10";
         }
         
         paraMap.put("currentShowPageNo", currentShowPageNo);
         paraMap.put("sizePerPage", sizePerPage);
         
         List<memberVO> memberList = mdao.selectPagingMember(paraMap);
         
         
         request.setAttribute("memberList", memberList);
         request.setAttribute("searchType", searchType);
         request.setAttribute("searchWord", searchWord);
         request.setAttribute("sizePerPage", sizePerPage);
         
         // ==== *** 페이지바 만들기 *** ==== //
         /*
             1개 블럭당 10개씩(페이지 수) 잘라서 페이지를 만든다.
             1개 페이지 당 3, 5, 10개 행을 보여주는데
             만약 1개 페이지당 5개의 행을 보여준다라면
             총 몇개 블럭이 나와야 할까?
             총 회원수가 207명이고, 1개 페이지당 보여줄 회원수가 5 이라면
             207/5 = 41.4 ==> 42(totalPage)(소수 첫째자리 올림)
             
             1블럭       1 2 3 4 5 6 7 8 9 10 [다음]
             2블럭 [이전] 11 12 13 14 15 16 17 18 19 20 [다음]
             3블럭 [이전] 21 22 23 24 25 26 27 28 29 30 [다음]
             4블럭 [이전] 31 32 33 34 35 36 37 38 39 40 [다음]
             5블럭 [이전] 41 42
         */
         
         // 페이징 처리를 위한 전체회원의 총페이지 개수 알아오기(select)
         int totalPage = mdao.getTotalPage(paraMap);
         
         // ==== !! 공식 !! ==== //
         /*
             1  2  3  4  5  6  7  8  9 10   -- 첫번째 블럭의 페이지 번호 시작값(pageNo)은 1이다.
             11 12 13 14 15 16 17 18 19 20   -- 두번째 블럭의 페이지 번호 시작값(pageNo)은 11이다.
             21 22 23 24 25 26 27 28 29 30   -- 세번째 블럭의 페이지 번호 시작값(pageNo)은 21이다.
             
             currentShowPageNo      pageNo
             ------------------------------------------------------
             1                     1   =   ( (currentShowPageNo - 1)/blockSize ) * blockSize + 1 
             2                     1   =   ( (2 - 1)/10 ) * 10 + 1 => int라 (2 - 1)/10 )는 0이 나옴
             3                     1   =   ( (3 - 1)/10 ) * 10 + 1 == 1
             4                     1   =   ( (4 - 1)/10 ) * 10 + 1 == 1
             5                     1   =   ( (5 - 1)/10 ) * 10 + 1 == 1
             6                     1   =   ( (6 - 1)/10 ) * 10 + 1 == 1
             7                     1   =   ( (7 - 1)/10 ) * 10 + 1 == 1
             8                     1   =   ( (8 - 1)/10 ) * 10 + 1 == 1
             9                     1   =   ( (9 - 1)/10 ) * 10 + 1 == 1
             10                     1   =   ( (10 - 1)/10 ) * 10 + 1 == 1
             
             11                     11   =   ( (11 - 1)/10 ) * 10 + 1 == 11
             12                     11   =   ( (12 - 1)/10 ) * 10 + 1 == 11
             13                     11   =   ( (13 - 1)/10 ) * 10 + 1 == 11
             14                     11   =   ( (14 - 1)/10 ) * 10 + 1 == 11
             15                     11   =   ( (15 - 1)/10 ) * 10 + 1 == 11
             16                     11   =   ( (16 - 1)/10 ) * 10 + 1 == 11
             17                     11   =   ( (17 - 1)/10 ) * 10 + 1 == 11
             18                     11   =   ( (18 - 1)/10 ) * 10 + 1 == 11
             19                     11   =   ( (19 - 1)/10 ) * 10 + 1 == 11
             20                     11   =   ( (20 - 1)/10 ) * 10 + 1 == 11
             
             21                     21   =   ( (21 - 1)/10 ) * 10 + 1 == 21
             22                     21   =   ( (22 - 1)/10 ) * 10 + 1 == 21
             23                     21   =   ( (23 - 1)/10 ) * 10 + 1 == 21
             ...                     21   =   ( (... - 1)/10 ) * 10 + 1 == 21
             29                     21   =   ( (29 - 1)/10 ) * 10 + 1 == 21
             30                     21   =   ( (30 - 1)/10 ) * 10 + 1 == 21
         */
         
         String pageBar = "";
         int blockSize = 10;   // blockSize => 블럭(토믹)당 보여지는 페이지 번호의 개수
         
         int loop = 1;   // loop => 1부터 증가하여 1개 블럭을 이루는 페이지번호의 개수(지금은 10개)까지만 증가하는 용도
         
         int pageNo = 0;   // pageNo => 페이지바에서 보여지는 첫번째 번호
         
         // !!!! 다음은 pageNo를 구하는 공식이다. !!! //
         pageNo = ( (Integer.parseInt(currentShowPageNo) - 1)/blockSize ) * blockSize + 1;   // 사용자에게 보여줄 블럭 내의 숫자
         
         if (searchType == null) {
            searchType = "";
         }
         if (searchWord == null) {
            searchWord = "";
         }
         
         if (Integer.parseInt(currentShowPageNo) > totalPage) {
            currentShowPageNo = "1";
         }
                  
         // **** [이전] 만들기 **** //
         // pageNo -1 == 11 - 1 == 10
         if ( pageNo != 1 ) {
            // **** [맨처음] 만들기 **** //
            pageBar += "&nbsp;<a href='memberList.up?currentShowPageNo=1&sizePerPage=" +
                  sizePerPage + "&searchType=" + searchType + "&searchWord=" + searchWord + "'>[맨처음]</a>&nbsp;";
            pageBar += "&nbsp;<a href='memberList.up?currentShowPageNo=" + (pageNo-1) + "&sizePerPage=" +
                  sizePerPage + "&searchType=" + searchType + "&searchWord=" + searchWord + "'>[이전]</a>&nbsp;";
         }
                  
         while(!(loop > blockSize || pageNo > totalPage)) {
            if ( pageNo == Integer.parseInt(currentShowPageNo) ) {
               pageBar += "&nbsp;<span style='border: solid 1px blue; padding: 2px 4px;'>" + pageNo +"</span>&nbsp;";
            }else {
               pageBar += "&nbsp;<a href='memberList.up?currentShowPageNo=" + pageNo + "&sizePerPage=" +
                     sizePerPage + "&searchType=" + searchType + "&searchWord=" + searchWord + "'>" + pageNo + "</a>&nbsp;";
            }

            loop++;
            pageNo++;   // 1 2 3 4 5 6 7 8 9 10
                     // 11 12 13 14 15 16 17 18 19 20
         }
         
         // **** [다음] 만들기 **** //
         if (!(pageNo > totalPage)) {
            pageBar += "&nbsp;<a href='memberList.up?currentShowPageNo=" + pageNo + "&sizePerPage=" +
                  sizePerPage + "&searchType=" + searchType + "&searchWord=" + searchWord + "'>[다음]</a>&nbsp;";
            // **** [마지막] 만들기 **** //
            pageBar += "&nbsp;<a href='memberList.up?currentShowPageNo=" + totalPage + "&sizePerPage=" +
                  sizePerPage + "&searchType=" + searchType + "&searchWord=" + searchWord + "'>[마지막]</a>&nbsp;";
         }
         
         // *** 현재페이지를 돌아갈 페이지(goBackURL)로 주소 지정하기
         String currentURL = Myutil.getCirrentURL(request);   // 회원개별조회를 했을 시 현재 목록페이지로 되돌아 가기위한 것
         
         // currentURL = member/memberList.up?searchType=name&searchWord=%ED%99%8D%EC%8A%B9%EC%9D%98&sizePerPage=5
         
         System.out.println("current url=> " + currentURL);
         
         currentURL = currentURL.replace("&", " ");
         request.setAttribute("pageBar", pageBar);
         request.setAttribute("goBackURL", currentURL);
         
         
         super.setRedirect(false);
         super.setViewPage("/WEB-INF/member/memberList.jsp");
         
      }///////////////////////////////////if (loginuser != null && "admin".equals(loginuser.getUserid()))///////////////////////////////
      
      else {
         // 로그인을 안한 경우 또는 일반사용자로 로그인 한 경우
         String message = "관리자가 아닙니다.";
         String loc = "javascript:history.back()";
         
         request.setAttribute("message", message);
         request.setAttribute("loc", loc);
         
         super.setRedirect(false);
         super.setViewPage("/WEB-INF/msg.jsp");
      }

   }

}