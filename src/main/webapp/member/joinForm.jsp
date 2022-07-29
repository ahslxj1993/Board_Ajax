<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<title>Insert title here</title>
<link href="css/join.css" type="text/css" rel="stylesheet">
<script  src="http://code.jquery.com/jquery-latest.min.js"></script>

<script>
$(function () {
	var check_id = false;
	var check_email = false;
	
	$("input:eq(0)").on('keyup',function () {
		$('#message').empty(); //처음에 pattern에 적합하지 않은 경우 메세지 출력후 적합한 데이터를 입력해도
								//계속 같은 데이터를 출력하므로 이벤트를 시작할때마다 비워둡니다
		
		//[A-Za-z0-9_]의 의미가 \w
		var pattern = /^\w{5,12}$/;
		var id = $('input:eq(0)').val();
		if (!pattern.test(id)){
			$('#message').css("color","red").html("영문자, 숫자, _ 로 5~12자를 입력해주세요");
			check_id=false;
			return
		}
		
		$.ajax({
			url : "idcheck.net",
			data : {"id" : id},
			success : function (resp) {
				if (resp == -1) {//db에  해당 id가 없는경우
				$('#message').css("color","green").html("사용 가능한 아이디 입니다.");
				check_id = true;
				} else {
					$('#message').css("color","blue").html("사용중인 아이디 입니다");
					check_id = false;
				}
			}//success
		});//ajax end
		
	})//id keyup function end
	
	$('form').on('submit', function () {
		var senddata = $("#insert_form").serialize();
		
	})//form submit function end
	
	
	$("input:eq(6)").on('keyup', function () {
		$('#email_message').empty();
		//[A-Za-z0-9_]와 동일한 것이 \w 입니다
		//+는 1회이상 반복을 의미하고 {1,}와 동일합니다.
		//\w+ 는 [A-za-z0-9_]를 1개이상 사용하라는 의미힙니다
		var pattern = /^\w+@\w+[.]\w{3}/;
		var email = $("input:eq(6)").val();
		
		if(!pattern.test(email)){
			$('#email_message').css("color","red").html("이메일 형식이 맞지 않습니다");
			check_email = false;
		} else {
			$('#email_message').css("color","green").html("이메일 형식에 맞습니다");
			check_email=true;
		}
	})//email keyup event end
	
	$('form').submit(function () {
		if (!$.isNumeric($('input[name="age"]').val())){
			alert("나이는 숫자를 입력하세요");
			$("input[name='age']").val('').focus();
			return false;
		}
		
		if(!check_id){
			alert("사용가능한 아이디로 입력하세요");
			$("input:eq(0)").val('').focus();
			return false;
		}
		
		if(!check_email){
			alert("이메일 형식을 확인하세요");
			$("input:eq(6)").focus();
			return false;
		}
	})//submit event end
	
})//ready
</script>

</head>
<body>
	<form name="joinform" action="joinProcess.net" method="post">
	<h1>회원가입</h1>
	<hr>
	<b>아이디</b>
	<input type="text" name="id" placeholder="Enter ID" required maxLength="12">
	<span id="message"></span>
	
	<b>비밀번호</b>
	<input type="password" name="pass" placeholder="Enter PASSWORD" required>
	
	<b>이름</b>
	<input type="text" name="name" placeholder="Enter NAME" maxLength="5" required>
	
	<b>나이</b>
	<input type="text" name="age" maxLength="2" placeHolder="Enter AGE" required>
	
	<b>성별</b>
	<div>
		<input type="radio" name="gender" value="남" checked><span>남자</span>
		<input type="radio" name="gender" value="여"><span>여자</span>
	</div>
	
	<b>이메일 주소</b>
	<input type="text" name="email" placeholder="Enter EMAIL" maxLength="30" required>
	<span id="email_message"></span>
	
	<div class="clearfix">
		<button type="submit" class="submitbtn">회원가입</button>
		<button type="reset" class="cancelbtn">다시작성</button>
	</div>
	</form>
</body>
</html>