<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<div>
	<a href="/board/list.korea?typ=${data.typ}">돌아가기</a>
	
	<c:if test="${data.i_user == loginUser.i_user}">
		<button onclick="clkDel(${data.i_board}, ${data.typ});">삭제</button>
		<a href="regmod?typ=${data.typ}&i_board=${data.i_board}">
			<button>수정</button>
		</a>
	</c:if>
	<div>
		<div>번호 : ${data.seq}</div>
		<div>조회수 : ${data.hits}</div>
		<div>이름 : ${data.writer_nm}</div>
		<div>제목 : ${data.title}</div>
		<div>작성일 : ${data.r_dt}</div>
		<div>
			${data.ctnt}
		</div>
	</div>
	<div style="margin-top: 20px;">
		<c:if test="${loginUser != null}">
		<div>
			<form action="cmt/reg" method="post">				
				<input type="hidden" name="i_board" value="${data.i_board}">				
				댓글: <input type="text" name="ctnt">
				<input type="submit" value="댓글쓰기">
			</form>
		</div>	
		</c:if>
		<div>
			<table>
				<tr>					
					<th>내용</th>
					<th>작성자</th>
					<th>작성일</th>
					<th>비고</th>					
				</tr>
				<c:forEach items="${cmtList}" var="item">
					<tr>
						<td>${item.ctnt}</td>
						<td>${item.user_nm}</td>
						<td>${item.r_dt}</td>
						<td>
							<c:if test="${item.i_user == loginUser.i_user}">
								<a href="cmt/del?i_cmt=${item.i_cmt}&i_board=${data.i_board}">
									<button>삭제</button>
								</a>
								<button onclick="clkCmtMod(${item.i_cmt});">수정</button>
							</c:if>
						</td>
					</tr>
					<c:if test="${item.i_user == loginUser.i_user}">
						<tr id="mod_${item.i_cmt}" class="cmd_mod_form">
							<td colspan="4">
								<form action="cmt/mod" method="post">
									<input type="hidden" name="i_board" value="${data.i_board}">
									<input type="hidden" name="i_cmt" value="${item.i_cmt}">
									<input type="text" name="ctnt" value="${item.ctnt}">
									<input type="submit" value="수정">
									<!--input type="button" value="닫기" onclick="clkCmtClose(${item.i_cmt});"-->
									<button type="button" onclick="clkCmtClose(${item.i_cmt});">닫기</button>
								</form>
							</td>
						</tr>
					</c:if>
				</c:forEach>
			</table>
		</div>
	</div>
	
	<c:if test="${loginUser != null}">
	<div id="favoriteContainer" is_favorite="${data.is_favorite}"
		 onclick="toggleFavorite(${data.i_board});">	
		<c:choose>
			<c:when test="${data.is_favorite == 1}">
				<i class="fas fa-heart"></i>
			</c:when>
			<c:otherwise>
				<i class="far fa-heart"></i>
			</c:otherwise>
		</c:choose>		
	</div>
	</c:if>
</div>
<script>
	<c:if test="${msg != null}">
		alert('${msg}');
	</c:if>
</script>







