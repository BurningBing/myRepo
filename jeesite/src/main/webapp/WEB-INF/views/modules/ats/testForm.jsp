<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!doctype html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<style>
	#username{width: 800px}
</style>

<script>
	function fun(){
		var input = document.getElementById("username");
		input.value="Chad";
	}
</script>
</head>
<body>
	<input id="username" value="chad"/>
	<button onclick="fun()">click</button>
</body>
</html>