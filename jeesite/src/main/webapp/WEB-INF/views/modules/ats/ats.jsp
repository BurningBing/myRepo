<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Act To Statute</title>
<link rel="stylesheet" type="text/css" href="/jeesite/css/reset.css">
<link rel="stylesheet" type="text/css" href="/jeesite/css/style.css">
<script type="text/javascript" src="/jeesite/js/ats.js"></script>
</head>
<body>
	<div style="overflow: auto;height: 100%;">
		<header>
			<nav>
				<i class="menu">Home</i>
				<i class="menu">Acts</i>
				<i class="menu">My Space</i>
				<i class="menu">Tools</i>
				<i class="menu">Tutorial</i>
					<div style="display: inline-block;">
						User Name: <input>
						Password: <input>
					</div>
				<div class="search-bar">
					<input type="text" name="" placeholder="search what you want...">
					<i class="search-btn"></i>
				</div>
			</nav>
		</header>
		<main>
			<div class="state-list"	>
				<p class="data-title">State List</p>
				<div class="list-data">
					<c:forEach  begin="0" end="${states.size()-1 }" varStatus="i" step="3">
						<ul>
							<c:forEach items="${states }" begin="${i.index }" end="${i.index+2}" var="s" >
								<li class="btn" data-href=${s.url }>${s.state }</li>
							</c:forEach>
						</ul>
					</c:forEach>
				</div>
			</div>
			
			<div>
				<p class="data-title">Daily Update</p>
				<div style="width: 90%; margin:0 auto;" >
					<ul style="overflow: hidden; display: flex">
						<li class="process-item">
							<span>RhodeIsland</span>
							<div class="process"></div>
							20/20
						</li>
						<li class="process-item">
							<span>Illinois</span>
							<div class="process"></div>
							76/76
						</li>
					</ul>
					<ul style="overflow: hidden; display: flex">
						<li class="process-item">
							<span>NorthCarolina</span>
							<div class="process"></div>
							20/20
						</li>
						<li class="process-item">
							<span>Idoha</span>
							<div class="process"></div>
							76/76
						</li>
					</ul>
					
				</div>
			</div>
	
			<div class="table-editor">
				<p class="data-title">Table Editor</p>
				<iframe src="/jeesite/tableEditor.html" style="width: 100%;background-color: #fff; height:400px;"></iframe>
			</div>
		</main>
	</div>
	
</body>
</html>