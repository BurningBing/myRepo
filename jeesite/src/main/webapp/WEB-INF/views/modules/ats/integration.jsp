<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<!DOCTYPE html>
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="${ctxStatic}/jquery-ui/jquery-ui.css" />
<link rel="stylesheet" type="text/css" href="${ctxStatic }/jquery-ztree/3.5.12/css/zTreeStyle/zTreeStyle.css">
<link rel="stylesheet" type="text/css" href="/jeesite/simditor/styles/simditor.css" />
<script type="text/javascript" src="${ctxStatic }/jquery/jquery-1.9.1.min.js"></script>
<script type="text/javascript" src="${ctxStatic}/jquery-ui/jquery-ui-1.12.0.js"></script>
<script type="text/javascript" src="${ctxStatic }/jquery-ztree/3.5.12/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript" src="/jeesite/simditor/scripts/module.js"></script>
<script type="text/javascript" src="/jeesite/simditor/scripts/hotkeys.js"></script>
<script type="text/javascript" src="/jeesite/simditor/scripts/simditor.js"></script>
<style type="text/css">
	html,body{
		height: 100%;
		margin: 0;
		display: flex;
		flex-flow: column;
	}
	#tabs{
		flex: 1;
		display: flex;
		flex-flow:column;
	}
	#tabs > div{
		flex: 1;
		padding: 0 5px;
	}
	#tabs-1{
		display: flex;
	}
	#fixWin{
		width:800px;
		height:600px;
		position: absolute;
		border: 1px solid #ccc;
		border-radius: 5px;
		top: 20px;
		left:200px;
		background-color: #fff;
		display:none;
		flex-flow:column;
		box-shadow:0 0 10px;
	}
	#fixKeyInfoWin{
		width:600px;
		height:250px;
		position:absolute;
		border: 1px solid #ccc;
		border-radius: 5px;
		top: 60px;
		left:350px;
		background-color: #fff;
		display:none;
		flex-flow:column;
		box-shadow:0 0 10px;
	}
	.version-item{
		margin: 5px 10px;
	    cursor: pointer;
	    border-bottom: 1px solid #fff;
	}
	.version-item:HOVER {
		color: #32ffe4;
	}
</style>
<script type="text/javascript">
	//当前sectionId
	var sec;
	//当前版本
	var ver;
	
	//页面加载后事件
	window.onload = function(){
		
	}
	

	//ztree setting...
	var setting = {
			async : {
				enable : true,
				url : "loadStatuteTreeData",
				dataFilter : treeDataFilter,
				type : "get",
				autoParam:["id=nodeId","editionId"]
			},
			callback : {
				onAsyncSuccess : getTreeDataSuccess,
				onDblClick: showSection
			}
	}
	
	//ajax返回后预处理方法
	function treeDataFilter(treeId, parentNode, responseData){
		if(typeof responseData == 'string'){
			alert('加载失败');
			return;
		} 
		return responseData;
	}
	
	//ajax获取数据成功
	function getTreeDataSuccess(event, treeId, treeNode, msg){
		//TODO	
	}
	
	//双击显示section
	function showSection(event, treeId, treeNode){
		if(!treeNode.isParent){
			$.get(
					"showSection",
					{nodeId:treeNode.id},
					function(ret){
						sec = ret;
						console.log(sec);
						$("#content").html(ret.libraryDocumentHtml);
						$("#tool").find("button").each(function(i,item){
							item.disabled = false;
						});
						var text = ret.shortName;
						if(ret.indexDescription){
							text += " - "+ret.indexDescription;
						}
						$("#shortName").text(text);
					},
					"json"
				);
		}
	}
	
	// 选择州后加载Statutes
	function loadStatute(self){
		var state = self.value;
		$.get(
			"getStatuteTree",
			{"state":state},
			function(ret){
				zTreeObj = $.fn.zTree.init($("#tree"), setting, ret);
			},
			"json"
		)
	}
	
	//Fix按钮点击事件调用函数
	function fixSection(){
		$("#fixWin").css({"display":"flex"});
		showFixSection();
	}
	
	//显示修复窗口
	function showFixSection(){
		editor = new Simditor({
			  textarea: $('#fix-editor'),
			  toolbar:['underline','strikethrough','alignment','bold','italic','blockquote','eraser']
		});
		if(sec.documentUpdates.libraryDocumentUpdate){
			for(var i=0;i<sec.documentUpdates.libraryDocumentUpdate.length;i++){
				var li = document.createElement("li");
				li.id = sec.documentUpdates.libraryDocumentUpdate[i].libraryDocumentUpdateId;
				li.innerText = sec.documentUpdates.libraryDocumentUpdate[i].indexCaption+"("+sec.documentUpdates.libraryDocumentUpdate[i].updateOrder+")";
				li.className = "version-item";
				li.order = sec.documentUpdates.libraryDocumentUpdate[i].updateOrder;
				li.onclick = function(){
					editor.setValue(sec.documentUpdates.libraryDocumentUpdate[this.order-1].libraryDocumentUpdateHtml);
					ver = sec.documentUpdates.libraryDocumentUpdate[this.order-1];
				}
				document.getElementById("version-list").appendChild(li);
			}
			editor.setValue(sec.documentUpdates.libraryDocumentUpdate[0].libraryDocumentUpdateHtml);
			ver = sec.documentUpdates.libraryDocumentUpdate[0];
		}else{
			var li = document.createElement("li");
			li.className = "version-item";
			li.innerText = sec.indexCaption;
			document.getElementById("version-list").appendChild(li);
			editor.setValue($("#content").html());
			ver = sec;
		}
	}
	
	//隐藏修复窗口
	function hiddenFixWin(){
		$("#fixWin").css({"display":"none"});
		document.getElementById("version-list").innerHTML = "";
	}
	
	//显示修复关键信息窗口
	function showFixKeyInfo(){
		$("#fixKeyInfoWin").css({"display":"flex"});
		$("#fixCaption").val(ver.indexCaption);
		$("#fixDesc").val(ver.indexDescription);
		$("#fixShortName").val(sec.shortName);
	}
	
	//隐藏修复关键信息窗口
	function hiddenFixKeyInfoWin(){
		$("#fixKeyInfoWin").css({"display":"none"});
	}
	
	//执行修复Section
	function doFixSection(){
		var data = {};
		data.content = editor.getValue(); 
		var id = null;
		var isFromAct = null;
		if(ver.libraryDocumentUpdateId){
			id = ver.libraryDocumentUpdateId;
			isFromAct = false; 
		}else{
			id = sec.libraryDocumentID;
			isFromAct = true;
		}
		data.id = id;
		data.isFromAct = isFromAct;
		data.caption = $("#fixCaption").val();
		data.description = $("#fixDesc").val();
		data.shortName = $("#fixShortName").val();
		$.post(
			"/jeesite/a/atsAct/doFixSection",
			data,
			function(ret){
				
			}
		)		
	}
	
	$(function() {
		$("#tabs").tabs();
	});
	
	function testDownload(){
		$.get(
			"/jeesite/a/atsAct/checkUpdates",
			{"state":"Arizona"},
			function(ret){
				
			}
		)
	}
</script>
</head>
<body>
	<button onclick="testDownload()">Test</button>

	<div id="tabs">
		<ul style="flex:0 0 43px;">
			<li><a href="#tabs-1">Statute Tree</a></li>
			<li><a href="#tabs-2">Integration Tool</a></li>
		</ul>
		<div id="tabs-1">
			<div style="width:25%;flex:0 0 25%;border-left:1px solid #ccc;border-right: 1px solid #ccc;display: flex;flex-flow:column;">
				<div style="flex:0 0 40px; border: 1px solid #ccc;border-left:none;border-right:none;">
					<select style="width:100%;height:40px;border:none;outline:none;color:#555;" onchange="loadStatute(this)">
						<option value=''>Load Statute</option>
						<c:forEach items="${states }" var="state">
							<option>${state }</option>
						</c:forEach>
					</select>
				</div>
				<div id="tree" style="flex:1;overflow:auto;" class="ztree"></div>
			</div>
			<div style="flex:1;display:flex;flex-flow:column;font-family: courier New;overflow: hidden">
				<div style="height:40px;line-height:40px;flex:0 0 40px;border:1px solid #ccc;border-left:none;text-align: center;">
					<span id="shortName" style="font-size:13px;font-weight:bold;"></span>
				</div>
				<div id="content" style="flex:1;overflow:auto;padding:5px 12px;color:#444;font-family: Leelawadee UI"></div>
				<div id="tool" style="flex: 0 0 35px;background-color:#ccc;box-shadow: 0 0 5px;display: flex">
					<button  style="flex:1" onclick="fixSection()">Fix</button>
					<button disabled style="flex:1">Remove</button>
				</div>
			</div>
		</div>
		<div id="tabs-2">
			<p>Morbi tincidunt, dui sit amet facilisis feugiat, odio metus
				gravida ante, ut pharetra massa metus id nunc. Duis scelerisque
				molestie turpis. Sed fringilla, massa eget luctus malesuada, metus
				eros molestie lectus, ut tempus eros massa ut dolor. Aenean aliquet
				fringilla sem. Suspendisse sed ligula in ligula suscipit aliquam.
				Praesent in eros vestibulum mi adipiscing adipiscing. Morbi
				facilisis. Curabitur ornare consequat nunc. Aenean vel metus. Ut
				posuere viverra nulla. Aliquam erat volutpat. Pellentesque
				convallis. Maecenas feugiat, tellus pellentesque pretium posuere,
				felis lorem euismod felis, eu ornare leo nisi vel felis. Mauris
				consectetur tortor et purus.</p>
		</div>
	</div>
	
	<div id="fixWin">
		<div style="flex:0 0 35px;background-color:#ccc;height:35px;text-align: right;">
			<img style="margin:10px;cursor:pointer;" src="/jeesite/image/common/cls.png" onclick="hiddenFixWin()">
		</div>
		<div style="flex:1;display:flex">
			<div style="flex:0 0 200px;background-color: graytext;">
				<ul id="version-list" style="margin: 0;padding:0;color:#fff;list-style: none">
				</ul>
			</div>
			<div style="flex:1;display:flex;flex-flow:column;">
				<div style="flex:1;overflow:auto;">
					<textarea id='fix-editor' ></textarea>
				</div>
				<div style="flex:0 0 35px;display:flex;">
					<div style="flex:1;background-color: #ddd;text-align: center;line-height: 35px;cursor:pointer;font-weight: bold;border-right:1px solid #aaa;" onclick="doFixSection()">Fix</div>
					<div style="flex:1;background-color: #ddd;text-align: center;line-height: 35px;cursor:pointer;font-weight: bold" onclick="showFixKeyInfo()">KeyInfor</div>
				</div>
			</div>
		</div>
	</div>
	<div id="fixKeyInfoWin">
		<div style="flex:0 0 35px;background-color:#ccc;height:35px;text-align: right;">
			<img style="margin:10px;cursor:pointer;" src="/jeesite/image/common/cls.png" onclick="hiddenFixKeyInfoWin()">
		</div>
		<div style="flex:1">
			<br>
			<label>Caption:</label> <input id="fixCaption" style="width:400px;"><br><br>
			<label>Description:</label> <input  id="fixDesc" style="width:400px;"><br><br>
			<label>ShortName:</label> <input id="fixShortName" style="width:400px;"><br><br>
			<button style="display: block;margin:0 auto;">Ok</button>
		</div>
	</div>
	
	
</body>
</html>