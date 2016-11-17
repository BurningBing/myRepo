<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/atsEditor/head.jsp"%>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="utf-8">
		<title>Act To Statutes' Editor</title>
	</head>
	<body>
		<div id="head-tool">
			<ul>
				<li class="menu" style="width: 50px;">
					<i id="logo"></i>
				</li>
				<li class="menu">MySpace</li>
				<li class="menu">Tutorial</li>
				<li class="menu">Help</li>
				<li style="float:right;margin-right:10px;font-size:14px">
					<a href="a/logout" style="color:#797979;text-decoration:none">[Logout]</a>
				</li>
			</ul>
			
		</div>
		<div id="nav">
			<ul id="nav-container">
				<li class="nav-item">
					<i class="nav-folder"></i>
					<span>Ats</span>
				</li>
			</ul>
		</div>
		<div id="main-body">
			<div id="body-left">
				<div id="main-tree" class="ztree">
				
				</div>	
				<div id="key-infor-panel">
					<div class="key-infor-item">
						<h5>Caption</h5>
						<input type="text" name="">
					</div>
					<div class="key-infor-item">
						<h5>Description</h5>
						<input type="text" name="">
					</div>
					<div class="key-infor-item">
						<h5>ShortName</h5>
						<input type="text" name="">
					</div>
					<div class="key-infor-item">
						<h5>EffectiveDate</h5>
						<input type="text" name="">
					</div>
					<div class="key-infor-item">
						<h5>ExpirationDate</h5>
						<input type="text" name="">
					</div>
					<div class="key-infor-item">
						<h5>Link</h5>
						<input type="text" name="">
					</div>
					<div class="key-infor-item">
						<h5>Update</h5>
						<input type="radio" value="1" name="update"><label>New</label>
						<input type="radio" value="2"  name="update"><label>Modify</label>
						<input type="radio" value="3" name="update"><label>Repeal</label>
					</div>
					<div class="key-infor-item">
						<button style="width:100%" onclick="saveKeyInfor()">Save</button>
					</div>
	
				</div>
			</div>
			<div class="main-separator"></div>
			<div id="main-window">
				<div id="work-space">
					<div id="items-bar">
						<ul id="tab-container">
							
						</ul>
					</div>
					<div class="container">
						
					</div>
				</div>
				<div id="right-tool">
					<ul>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/save.png" title="save"  onclick="saveSection()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/submit.png" title="submit" onclick="submitSection()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/add.png" title="add" onclick="addSection()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/delete.png" title="delete" onclick="deleteSection()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/refresh.png" title="refresh" onclick="refreshSection()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/key.png" title="keyInfor" onclick="modifyKeyInfor()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/history.png" title="history" onclick="showHistory()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/repeal.png" title="repeal" onclick="repealBat()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/combine.svg" title="combine" onclick="combine()">
						</li>
						<li class="menu-editor">
							<img alt="" src="atsEditor/images/link.png" title="link" onclick="viewLink()">
						</li>
					</ul>
				</div>
			</div>
		</div>
		<div id="bottom-tool"></div>
		<div id="shadow"></div>
		
	</body>
</html>