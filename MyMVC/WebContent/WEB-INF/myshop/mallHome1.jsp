<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../header.jsp" />

<style type="text/css">
	.moreProdInfo {
		display: inline-block;
		margin: 10px;
	}
	div.moreProdInfo > ul {
		text-align: left;
	}
	div.moreProdInfo label.prodInfo {
		display: inline-block;
		width: 70px;
		border: solid 1px gray;
	}

</style>

<script type="text/javascript">
   
   $(document).ready(function() {
      
	  $("span#totalHITCount").hide();
	  $("span#countHIT").hide();
	  
	  // HIT상품 게시물을 더보기 위하여 "더보기..." 버튼 클릭액션에 대한 초기값 호출하기 
      // 즉, 맨처음에는 "더보기..." 버튼을 클릭하지 않더라도 클릭한 것 처럼 8개의 HIT상품을 게시해주어야 한다는 말이다. 
      // 페이지가 로딩됐을 때 처음 물품을 띄워줘야 하기때문에 1을 넣어주고 시작
      displayHIT("1");
      
      // 더보기 버튼을 눌렀을 때
      $("button#btnMoreHIT").click(function() {
         
    	 if($(this).text() == "처음으로") {
    		 $("div#displayHIT").empty();
    		 $("span#end").empty();
    		 displayHIT("1");
    		 $(this).text("더보기...");
    	 }
    	 else {
    		 // start 값을 컨트롤할 수 있는 버튼의 value 값을 매개변수로 보내줌
             displayHIT($(this).val());   // displayHIT("9")
                                  // displayHIT("17")
    	 }
    	  
      });
      
   });
   
   // 보여줄 HIT상품 정보를 계속해서 추가 요청해주는 함수(더보기 버튼 누를 시 실행) => ajax
   function displayHIT(start) {
      
      /*
         // start가 1 이라면 1~8 까지 상품 8개를 보여준다.
            // start가 9 이라면 9~16 까지 상품 8개를 보여준다.
            // start가 17 이라면 17~24 까지 상품 8개를 보여준다.
            // start가 25 이라면 25~32 까지 상품 8개를 보여준다.
            // start가 33 이라면 33~36 까지 상품 4개를 보여준다.(마지막 상품)
      
         sname => 해당하는 상품을 찾을 키워드
         start => 한 페이지를 시작할 첫 물품의 rno?
      */
      
      // 더보기 버튼 한번을 클릭했을 때 보여줄 상품의 갯수
      var lenHIT = 8;
      
      $.ajax({
         url: "<%= request.getContextPath() %>/shop/mallDisplayJSON.up",
         type: "GET",
         data: {
               "sname":"HIT",
               "start":start,
               "len":lenHIT
               },
         dataType: "json",
         success: function(json) {
            
            /*
               값이 있을 때 json => [{"pnum":1,"code":"100000","pname":"스마트TV","pcompany":"삼성","saleprice":800000,"point":50,"pinputdate":"2020-11-13","pimage1":"tv_samsung_h450_1.png","pqty":100,"pimage2":"tv_samsung_h450_2.png","pcontent":"42인치 스마트 TV. 기능 짱!!","price":1200000,"sname":"HIT"},{"pnum":2,"code":"100000","pname":"노트북","pcompany":"엘지","saleprice":750000,"point":30,"pinputdate":"2020-11-13","pimage1":"notebook_lg_gt50k_1.png","pqty":150,"pimage2":"notebook_lg_gt50k_2.png","pcontent":"노트북. 기능 짱!!","price":900000,"sname":"HIT"},{"pnum":3,"code":"200000","pname":"바지","pcompany":"S사","saleprice":10000,"point":5,"pinputdate":"2020-11-13","pimage1":"cloth_canmart_1.png","pqty":20,"pimage2":"cloth_canmart_2.png","pcontent":"예뻐요!!","price":12000,"sname":"HIT"},{"pnum":4,"code":"200000","pname":"남방","pcompany":"버카루","saleprice":13000,"point":10,"pinputdate":"2020-11-13","pimage1":"cloth_buckaroo_1.png","pqty":50,"pimage2":"cloth_buckaroo_2.png","pcontent":"멋져요!!","price":15000,"sname":"HIT"},{"pnum":5,"code":"300000","pname":"세계탐험보물찾기시리즈","pcompany":"아이세움","saleprice":33000,"point":20,"pinputdate":"2020-11-13","pimage1":"book_bomul_1.png","pqty":100,"pimage2":"book_bomul_2.png","pcontent":"만화로 보는 세계여행","price":35000,"sname":"HIT"},{"pnum":6,"code":"300000","pname":"만화한국사","pcompany":"녹색지팡이","saleprice":120000,"point":60,"pinputdate":"2020-11-13","pimage1":"book_koreahistory_1.png","pqty":80,"pimage2":"book_koreahistory_2.png","pcontent":"만화로 보는 이야기 한국사 전집","price":130000,"sname":"HIT"},{"pnum":7,"code":"100000","pname":"노트북1","pcompany":"DELL","saleprice":1000000,"point":60,"pinputdate":"2020-11-13","pimage1":"1.jpg","pqty":100,"pimage2":"2.jpg","pcontent":"1번 노트북","price":1200000,"sname":"HIT"},{"pnum":8,"code":"100000","pname":"노트북2","pcompany":"에이서","saleprice":1000000,"point":60,"pinputdate":"2020-11-13","pimage1":"3.jpg","pqty":100,"pimage2":"4.jpg","pcontent":"2번 노트북","price":1200000,"sname":"HIT"}]
               값이 없을 때 json => []
            */
            
            var html = "";
               
            if(start == "1" && json.length == 0) {
       			// 처음부터 데이터가 존재하지 않는 경우
		    	// !!! 주의 !!!
		    	// if(json == null) 이 아님!!!
		    	// if(json.length == 0) 으로 해야함!!
		    	html += "현재 상품 준비중....";
		    		
		    	// HIT 상품 결과를 출력하기
		    	$("div#displayHIT").html(html);
       		}
            
            else if (json.length > 0) {
               // 데이터가 존재하는 경우
               
               //    받은배열                    인덱스,  값
               $.each(json, function(index, item) {
            	   html +=  "<div class='moreProdInfo'>"+
					"<ul style='list-style-type: none; border: solid 0px red; padding :0px; width :240px;'>"+
		            "<li><a href='/MyMVC/shop/prodView.up?pnum="+item.pnum+"'><img width='120px;' height='130px' src='/MyMVC/images/"+item.pimage1+"'/></a></li>"+
		            "<li><label class='prodInfo'>제품명</label>"+item.pname+"</li>"+
         	        "<li><label class='prodInfo'>정가</label><span style=\"color: red; text-decoration: line-through;\">"+(item.price).toLocaleString('en')+" 원</span></li>"+
         	        "<li><label class='prodInfo'>판매가</label><span style=\"color: red; font-weight: bold;\">"+(item.saleprice).toLocaleString('en')+" 원</span></li>"+
         	        "<li><label class='prodInfo'>할인율</label><span style=\"color: blue; font-weight: bold;\">["+item.discoutPercent+"%] 할인</span></li>"+
         	        "<li><label class='prodInfo'>포인트</label><span style=\"color: orange;\">"+item.point+" POINT</span></li>"+ 
           	        "</ul>"+
		        "</div>";
               	  
                  if((index+1)%4 == 0){
                	  html += "<br>";
                  }
               });
               
               // HIT 상품 결과를 출력하기
               $("div#displayHIT").append(html);
               
               // !!! 중요 !!! // >>> "더보기" 버튼의 value 속성에 값을 지정하기 <<< //
               start = $("button#btnMoreHIT").val(Number(start) + lenHIT);
               // 더보기 버튼의 value 값이 9로 변경된다      => start 값
               // 더보기 버튼의 value 값이 17로 변경된다   => start 값
               // 더보기 버튼의 value 값이 25로 변경된다   => start 값
               // 더보기 버튼의 value 값이 33로 변경된다   => start 값
               // 더보기 버튼의 value 값이 41로 변경된다   => start 값
               
               // countHIT에 지금까지 출력된 상품의 개수를 누적해서 기록한다.
               $("span#countHIT").text(Number($("span#countHIT").text()) + json.length );
            	
               if($("span#countHIT").text() == $("span#totalHITCount").text()){
            	   $("span#end").html("더이상 조회할 제품이 없습니다.");
         		   $("button#btnMoreHIT").text("처음으로");
         		  $("span#countHIT").text("0");
               }
               
               // header.jsp 의 하단에 표시된 div content 의 height 값을 구해서, header.jsp 의 div sideinfo 의 height 값으로 설정하기 
               func_height();
            }
        },
         error: function(request, status, error){
                alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
         }
      });
   }
   
</script>

   <%-- ==== HIT 상품을 모두 가져와서 디스플레이(더보기 방식) ==== --%>
   <div>
      <div style="margin: 20px 0;" >- HIT 상품 -</div>
      <div id="displayHIT"></div>
   
      <div style="margin: 20px 0;">
         <span id="end" style="font-size: 16pt; font-weight: bold; color: red;"></span><br/> 
         <button type="button" id="btnMoreHIT" value="">더보기...</button>
         <span id="totalHITCount">${totalGITCount}</span>
         <span id="countHIT">0</span>
      </div>
   </div>

<jsp:include page="../footer.jsp" />