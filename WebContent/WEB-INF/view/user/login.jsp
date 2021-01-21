<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>${title}</title>
</head>
<body>
	<div id="centerContainer">		
		<div>
			<form action="/user/loginProc.korea" method="post">
				<div><input type="text" name="user_id" placeholder="id" value="mic"></div>
				<div><input type="password" name="user_pw" placeholder="password" value="1212"></div>
				<div><input type="submit" value="LOGIN"></div>
			</form>
			<div style="color:red;">${msg}</div>
			<a href="/user/join.korea">join</a>
		</div>
		
	</div>
</body>
</html>