<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<title>MVC 게시판</title>
<jsp:include page="header.jsp" />
<script src="js/modifyform.js"></script>
<style>
h1{fontsize : 1.5rem; text-align:center; color:#1a92b9}
.container {width:60%}
label{font-weight:bold}
#upfile{display:none}
</style>
</head>
<body>
<%-- 게시판 수정 --%>
<div class="container">
	<form action="BoardModifyAction.bo" method="post" enctype="multipart/form-data" name="modifyform">
		<input type="hidden" name="board_num" value="${boarddata.board_num }">
		<h1>MVC 게시판 - 수정</h1>
		<div class="form-group">
			<label for="board_name">글쓴이</label>
			<input name="board_name" id="board_name" value="${boarddata.board_name}" readOnly
					type="text" class="form-control">
		</div>
		
		<div class="form-group">
			<label for="board_subject">제목</label>
			<textarea name="board_subject" id="board_subject" rows=1
				class="form-control" maxlength=100>${boarddata.board_subject}</textarea>
		
		</div>
			<div class="form-group">
			<label for="board_content">내용</label>
			<textarea name="board_content" id="board_content" rows="15"
					class="form-control">${boarddata.board_content }</textarea>
		</div>
		
		<%-- 원문글인 경우에만 파일 첨부 수정 가능합니다 --%>
		<c:if test="${boarddata.board_re_lev ==0 }">
		<div class="form-group">
			<label for="board_file">파일 첨부</label>
			<label for="upfile">
				<img src="image/attach.png" alt="파일첨부" width="20px">
			</label>
			<input type="file" id="upfile" name="board_file">
			<span id="filevalue">${boarddata.board_file}</span>
			<img src="image/remove.png" alt="파일삭제" width="10px" class="remove">
		</div>
		</c:if>
		
		
		<div class="form-group">
			<label for="board_pass">비밀번호</label>
			<input name="board_pass" id="board_pass" type="password" size="10" maxlength="30"
					class="form-control" placeholder="Enter password">
		</div>
		
		<div class="form-group">
			<button type=submit class="btn btn-primary">수정</button>
			<button type=reset class="btn btn-danger" onClick="history.go(-1)">취소</button>
		</div>
	</form>
</div>
</body>
</html>