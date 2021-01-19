<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="fn"  uri="http://java.sun.com/jsp/jstl/functions"%>

<jsp:include page="../header.jsp" />

<style type="text/css">
   div#mvoInfo {
      width: 60%; 
      text-align: left;
      border: solid 0px red;
      margin-top: 30px; 
      font-size: 13pt;
      line-height: 200%;
   }
   
   span.myli {
      display: inline-block;
      width: 90px;
      border: solid 0px blue;
   }
   
      div#sms {
      margin: 0 auto; 
      /* border: solid 1px red; */ 
      overflow: hidden; 
      width: 50%;
      padding: 10px 0 10px 80px;
   }
   
   span#smsTitle {
      display: block;
      font-size: 13pt;
      font-weight: bold;
      margin-bottom: 10px;
   }
   
   textarea#smsContent {
      float: left;
      height: 100px;
   }
   
   button#btnSend {
      float: left;
      border: none;
      width: 50px;
      height: 100px;
      background-color: navy;
      color: white;
   }
   
   div#smsResult {
      clear: both;
      color: red;
      padding: 20px;
   }
</style>

<script type="text/javascript">

   var goBackURL;

   $(document).ready(function() {
      $("div#smsResult").hide();
      
      $("button#btnSend").click(function() {
         
         $.ajax({
            url: "<%= request.getContextPath() %>/member/smsSend.up",
            type: "POST",
            data: {"mobile":"${mvo.mobile}", "smsContent":$("textarea#smsContent").val()},
            dataType: "json",
            success:function(json){   
               // 받은 json 내부: {"group_id":"R2G9SvjnjOSoI32T","success_count":1,"error_count":0}
               alert(json.success_count);
               if (json.success_count == 1) {
                  // 메세지전송 성공 시
                  $("div#smsResult").html("메세지전송이 완료되었습니다.");
               }else if(json.error_count != 0){
                  // 메세지전송 실패 시
                  $("div#smsResult").html("메세지전송에 실패했습니다.");
               }
               $("div#smsResult").show();
            },
            error:function(request, status, error){
               alert("code: "+request.status+"\n"+"message: "+request.responseText+"\n"+"error: "+error);
            }
         });
         
      });
      
      /////////////////////////////////////
      
      goBackURL = "${ goBackURL }";
      
      // !!! 자바스크립트에서 replace(자바스크립트)를 replaceAll(자바) 처럼 사용하기 !!! //
      // 자바스크립트에서는 replaceAll이 없으므로 replace를 사용해서 구현
      // "korea kenya" ==> "korea kenya".replace("k", "y") ==> "yorea kenya"
      // 변수 goBackURL 에 공백 ' ' 을 모두 '&' 로 변경해준다
      // ======== 방법 =======> "문자열".replace(/k/gi, "y");   결과 => "yorea yenya"
      goBackURL = goBackURL.replace(/ /gi, "&");
      alert("goBackURL은 " + goBackURL);
      
   });
   
   // 뒤로가기(최신데이터) 함수
   function goMemberList() {
      location.href = "<%= request.getContextPath() %>/" + goBackURL;
   }

</script>


<c:if test="${ empty mvo }">
   존재하지 않는 회원입니다.
</c:if>

<c:if test="${ ! empty mvo }">
   <h3>:::${ mvo.name }님의 회원 상세정보:::</h3>
   <div id="mvoInfo">
       <ol>   
          <li><span class="myli">아이디 : </span>${mvo.userid}</li>
          <li><span class="myli">회원명 : </span>${mvo.name}</li>
          <li><span class="myli">이메일 : </span>${mvo.email}</li>
          <li><span class="myli">휴대폰 : </span>${fn:substring(mvo.mobile, 0, 3)}-${fn:substring(mvo.mobile, 3, 7)}-${fn:substring(mvo.mobile, 7, 11)}</li>
          <li><span class="myli">우편번호 : </span>${mvo.postcode}</li>
          <li><span class="myli">주소 : </span>${mvo.address}&nbsp;${mvo.detailaddress}&nbsp;${mvo.extraaddress}</li>
          <li>
              <span class="myli">성별 : </span>
             <c:if test="${ mvo.gender == '1' }">남</c:if>
             <c:if test="${ mvo.gender == '2' }">여</c:if>
         </li>
          <li><span class="myli">생년월일 : </span>${fn:substring(mvo.birthday, 0, 4)}. ${fn:substring(mvo.birthday, 4, 6)}. ${fn:substring(mvo.birthday, 6, 8)}</li>
          <li><span class="myli">나이 : </span>${ mvo.age }세</li> 
          <li><span class="myli">코인액 : </span><fmt:formatNumber value="${mvo.coin}" pattern="###,###" /> 원</li>
          <li><span class="myli">포인트 : </span><fmt:formatNumber value="${mvo.point}" pattern="###,###" /> POINT</li>
          <li><span class="myli">가입일자 : </span>${mvo.registerday}</li>
        </ol>
     </div>
     
     
     <%-- ==== 휴대폰 SMS(문자) 보내기 ==== --%>
     <div id="sms" align="left">
        <span id="smsTitle">&gt;&gt;휴대폰 SMS(문자) 보내기 내용 입력란&lt;&lt;</span>
        <textarea rows="4" cols="40" id="smsContent"></textarea>
        <button id="btnSend">전송</button>
        <div id="smsResult"></div>
    </div>
    
    
    <div>
       <%-- javascript:history.back();로 뒤로가기: DB를 조회해서 다시 불러온게 아니기 때문에 삭제한 정보가 남아있을 수 있음 --%>
       <button style="margin-top: 50px;" onclick="javascript:history.back();">회원목록1</button>
       <button style="margin-top: 50px;" onclick="javascript:goMemberList();">회원목록2</button>
    </div>
     
     
     
     
     
     
     
</c:if>











<jsp:include page="../footer.jsp" />