<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>회원관리 시스템 관리자모드(회원정보)</title>
<jsp:include page="../board/header.jsp"/>
<%--
1. 검색어를 입력한 후 다시 memberList.net으로 온 경우 검색 필드와 검색어가 나타나도록 합니다
 --%>
<script>



</script>
<style>
table caption {
	caption-side: top;
	text-align: center
}

h1 {
	text-align: center
}

li .gray {
	color: gray;
}

body>div>table>tbody>tr>td:last-child>a {
	color: red
}

form {
	margin: 0 auto;
	width: 80%;
	text-align: center
}

select {
	color: #495057;
	background-color: #fff;
	background-clip: padding-box;
	border: 1px solid #ced4da;
	border-radius: .25rem;
	transition: border-color .15s ease-in-out, box-shadow .15s ease-in-out;
	outline: none;
}

.container {
	width: 60%
}

td:nth-child(1) {
	width: 33%
}

.input-group {
	margin-bottom: 3em
}
</style>

</head>
<body>
	<div class="container">
		<form action="memberList.net" method="post">
			<div class="input-group">
				<select id="vidwcount" name="search_field">
					<option value="0" selected>아이디</option>
					<option value="1">이름</option>
					<option value="2">나이</option>
					<option value="3">성별</option>
				</select>
					<input name="search_word" type="text" class="form-control"
					placeholder="아이디를 입력하세요" value="${search_word }">
					<button class="btn btn-primary" type="submit">검색</button>
			</div>
		</form>
		<c:if test="${listcount >0 }">
			<%-- 회원이 있는 경우 --%>
			<table class="table table-striped">
				<caption style="font-weight:bold">회원 목록</caption>
				<thead>
					<tr>
						<th colspan="2">MVC 게시판 - 회원정보 list</th>
						<th><font size="3">회원수 : ${listcount }</font></th>
					</tr>
					<tr>
						<td>아이디</td>
						<td>이름</td>
						<td>삭제</td>
					</tr>
				</thead>		
				<tbody>
				 <c:forEach var="m" items="${totallist }">
				 	<tr>
				 		<td><a href="memberInfo.net?id=${m.id}">${m.id }</a></td>
				 		<td>${m.name }</td>
				 		<td><a href="memberDelete.net?id=${m.id}">삭제</a></td>
				 	</tr>
				 </c:forEach>
				</tbody>
			</table>
		</c:if>
		
	</div>
</body>
</html>