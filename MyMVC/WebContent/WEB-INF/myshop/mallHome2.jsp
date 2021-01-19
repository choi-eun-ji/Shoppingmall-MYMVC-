<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<jsp:include page="../header.jsp" ></jsp:include>
<style type="text/css">
   div.moreProdInfo {
      display: inline-block;
      margin: 10px;
   
   }
   
   div.moreProdInfo > ul {
      text-align: left;
      
   }
   
   div.moreProdInfo label.prodInfo {
      display: inline-block;
      width: 70px;
   }
</style>

<script type="text/javascript">
   // HIT상품 스크롤을 할때 보여줄 상품의 개수
   var lenHIT = 8;
   
   var start = 1;
   
   
   $(document).ready(function(){
      
      /* $("span#totalHITCount").hide();
      $("span#countHIT").hide(); */
      
      // 맨 처음에 8개의 상품 보여주기
      // 처음에 1을 가지고 함수를 호출하기 때문에! 
      // button에 있는 value값은 9가되어진다.
      // HIT상품 게시물을 더보기 위하여 스크롤 액션에 대한 초기값 호출하기 
      // 즉, 맨처음에는 스크롤을 하지 않더라도 클릭한 것 처럼 8개의 HIT상품을 게시해주어야 한다는 말이다. 
      
      // displayHIT ();
      
      // 스크롤 이벤트 발생 시키기 시작시작시작 =============================================================
      displayHIT(start);
         
      $(window).scroll(function(){
         
         // 스크롤탑 의 위치값을 알아야 한다.
         
         // console.log($(window).scrollTop());
         
         //  보여주어야할 문서의 높이값(더보기를 해주므로 append 되어져서 높이가 계속 증가 될것이다)
         //   console.log( "$(document).height() => " + $(document).height() );
         
         //  웹브라우저창의 높이값(디바이스마다 다르게 표현되는 고정값) 
         //   console.log( "$(window).height() => " + $(window).height() );  
         
         if($(window).scrollTop()+1 >= $(document).height() - $(window).height()) {
            var totalHITCount = Number ($("span#totalHITCount").text());
            var countHIT = Number($("span#countHIT").text());
            
            if(totalHITCount != countHIT) {
               start = start + lenHIT;
               displayHIT(start);
            }
         }
         
         if($(window).scrollTop() == 0 && start == 33) {  // 스크롤이 페이지 맨 위로 올라갔을 경우
            // 다시 처음부터 보여주어야 한다.
            
            $("div#displayHIT").empty();
            $("span#end").empty();
            $("span#countHIT").text("0");
            start = 1;
            displayHIT(start);
         }
         // header.jsp 의 하단에 표시된 div content 의 height 값을 구해서, header.jsp 의 div sideinfo 의 height 값으로 설정하기 
         func_height();
         
      });

      	 // 스크롤 이벤트 발생 시키기 시작시작시작 =============================================================
   });
   
   // HIT상품 더보기 버튼을 클릭할 때 보여줄 상품의 갯수
   var lenHIT = 8;
   
   // display할 히트상품 정보를 추가요청하기 Ajax로 처리
   function displayHIT (start) {
        // start가 1 이라면 1~8 까지 상품 8개를 보여준다.
        // start가 9 이라면 9~16 까지 상품 8개를 보여준다.
        // start가 17 이라면 17~24 까지 상품 8개를 보여준다.
        // start가 25 이라면 25~32 까지 상품 8개를 보여준다.
        // start가 33 이라면 33~36 까지 상품 4개를 보여준다.(마지막 상품)
      
      $.ajax({
         url:"/MyMVC/shop/mallDisplayJSON.up",
         type:"GET",
         data:{"sname":"HIT"
            , "start":start  // "1"
            , "len":lenHIT},  // 8
         dataType:"JSON",
         success:function(json){
            html = "";
            if(start == "1" && json.length == 0) {
                 // 처음부터 데이터가 존재하지 않는 경우
                 // !!! 주의 !!!
                 // if(json == null) 이 아님!!!
                 // if(json.length == 0) 으로 해야함!!
                html += "현재 상품 준비중....";
                
                 // HIT 상품 결과를 출력하기
                $("div#displayHIT").html(html);
              }
            else if(json.length > 0) {
               
               
                 // 제이쿼리 내의 반복문 $.each(json, function(index, item){});
                 $.each(json, function(index, item){
                  
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
                  if ( (index + 1) % 4 == 0) {
                     html += "<br>";
                  }
                  
               });
               
               $("div#displayHIT").append(html);
               
               // countHIT에 지금까지 출력된 상품의 개수를 누적해서 기록한다!!!
               
               $("span#countHIT").text((Number)($("span#countHIT").text())  +  json.length );
               
               // 스크롤을  계속 클릭하여 countHIT 값과 totalHITCount값이 일치하는 경우
               if($("span#countHIT").text() == $("span#totalHITCount").text()) {
                  $("span#end").html("더이상 조회할 제품이 없습니다.");
               }
            } 
         },
         error: function(request, status, error){
              alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
           } 
      });
   }
</script>



<%--   HIT상품을 모두 가져와서 디스플레이(스크롤 방식으로 페이징 처리) --%>

<div>
   <div style="margin: 20px 0;">- HIT 상품 -</div>
   <div id="displayHIT"></div>

   <div style="margin: 20px 0;">
      <span id="end" style="font-size: 16pt; font-weight: bold; color: red;"></span>
      <br />
      <span id="totalHITCount">${totalHITCount}</span>
      <span id="countHIT">0</span>
   </div>
</div>
<jsp:include page="../footer.jsp" ></jsp:include>