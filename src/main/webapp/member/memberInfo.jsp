<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>회원정보 조회</title>
<jsp:include page="../board/header.jsp"/>

<style>
  tr>td:nth-child(odd){font-weight: bold}
  td{text-align:center}
  .container{width:50%}
</style>



</head>
<body>
<div class="container">
<table class="table table-bordered">
	<tr>
		<td>아이디</td>
		<td>${memberinfo.id }</td> <%-- Member클래스의 getId() 메서드 호출 --%>
	</tr>
	
	<tr>
		<td>비밀번호</td>
		<td>${memberinfo.password }</td>
	</tr>
	
	<tr>
		<td>이름</td>
		<td>${memberinfo.name }</td>
	</tr>
	
	<tr>
		<td>나이</td>
		<td>${memberinfo.age }</td>
	</tr>
	
	<tr>
		<td>성별</td>
		<td>${memberinfo.gender }</td>
	</tr>
	
	<tr>
		<td>이메일 주소</td>
		<td>${memberinfo.email }</td>
	</tr>
	
	<tr>
		<td colspan="2">
		<a href="memberList.net">리스트로 돌아가기</a></td>
	</tr>
</table>
</div>
</body>
</html>