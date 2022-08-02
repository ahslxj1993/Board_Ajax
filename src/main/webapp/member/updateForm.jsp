<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>회원관리 시스템 회원수정 페이지</title>
<link href="css/join.css" type="text/css" rel="stylesheet">

<style>
h3 {
text-align:center; color: #1a92b9;
}
input[type=file]{
display:none;
}
</style>

<script  src="http://code.jquery.com/jquery-latest.min.js"></script>


</head>
<body>
	<jsp:include page="../board/header.jsp" />
	<form name="joinform" action="updateProcess.net" method="post" enctype="multipart/form-data">
	<h3>회원정보 수정</h3>
	<hr>
	<b>아이디</b>
	<input type="text" name="id" value="${memberinfo.id }" readonly>
	
	<b>비밀번호</b>
	<input type="password" name="pass" value="${memberinfo.password }" readonly>
	
	<b>이름</b>
	<input type="text" name="name" value="${memberinfo.name }" placeholder="Enter Name" required>
	
	<b>나이</b>
	<input type="text" name="age" value="${memberinfo.age }" required>
	
	<b>성별</b>
	<div>
		<input type="radio" name="gender" value="남" checked><span>남자</span>
		<input type="radio" name="gender" value="여"><span>여자</span>
	</div>
	
	<b>이메일 주소</b>
	<input type="text" name="email" value="${memberinfo.email }" placeholder="Enter Email"
	maxLength="30" required>
	<span id="email_message"></span>
	
	<b>프로필 사진</b>
	<label>
		<img src="image/attach.png" width="10px">
		<span id="filename">${memberinfo.memberfile}</span>
		<span id="showImage">
			<c:if test='${empty memberinfo.memberfile}'>
				<c:set var='src' value='image/profile.png' />
			</c:if>
			
			<c:if test='${!empty memberinfo.memberfile}'>
				<c:set var='src' value='${"memberupload/"}${memberinfo.memberfile}'/>	
			</c:if>
			
			<img src="${src}" width="20px" alt="profile">
		</span>
		
		
		<%-- accept : 업로드할 파일 타입을 설정합니다
		<input type="file" accept="파일 확장자 |audio/*|video/*|image/*">
		(1)파일 확장자는 .png, .jpg, .pdf, .hwp 처럼 (.)으로 시작되는 파일 확장자를 의미합니다
					예) accept= ".png, .jpg, .pdf, .hwp" 
		(2)audio/* : 모든 타입의 오디오 파일
		(3)image/* : 모든 타입의 이미지 파일
		
		--%>
		<input type="file" name="memberfile" accept="image/*">
	</label>
	
	<div class="clearfix">
		<button type="submit" class="submitbtn">수정</button>
		<button type="reset" class="cancelbtn">취소</button>
	</div>
	</form>
	
	
	<script>
	//성별 체크해주는 부분
	$("input[value='${memberinfo.gender}']").prop('checked',true);
	
	$(".cancelbtn").click(function () {
		history.back();
	})
	
	//처음 화면 로드시 보여줄 이메일은 이미 체크 와료된 것이므로 기본 checkemail = true 입니다.
	var checkemail = true;
	$("input:eq(6)").on ('keyup', function () {
		$("#email_message").empty();
		//[A-Za-z0-9_]와 동일한 것이 \w
		//+는 1회 이상 반복을 의미합니다. {1,}와 동일합니다.
		//\w+ 는 [A-Za-z0-9_]를 1개 이상 사용하라는 의미입니다.,
		var pattern = /^\w+@\w+[.]\w{3}$/;
		var email = $("input:eq(6)").val();
		if (!pattern.test(email)) {
			$("#email_message").css('color','red').html("이메일 형식이 맞지 않습니다.")
			checkemail = false;
		} else {
			$("#email_message").css('color','green').html("이메일 형식을 만족합니다.")
			checkemail=true;
		}
	}) //email keyup event end
	
	$('form').submit(function () {
		if(!$.isNumeric($("input[name='age']").val())){
			alert("나이는 숫자를 입력하세요");
			$("input[name='age']").val('').focus();
			return false;
		}
		
		if(!checkemail){
			alert("email 형식을 확인하세요");
			$("input:eq(6)").focus();
			return false;
		}
	})
	</script>
</body>
</html>