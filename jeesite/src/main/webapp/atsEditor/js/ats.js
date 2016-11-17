var tree = null;
var index = 0;
var editor,section;
// tree配置信息
var setting = {
	async:{
		enable: true,
		url:"/jeesite/a/data/findTreeData",
		autoParam:["id","method"],
		dataFilter: dataFilter
	},
	data:{
		simpleData:{
			enable:true
		}
	},
	callback:{
		onDblClick : openWindow,
		onAsyncSuccess : onAsyncSuccess,
		beforeAsync : beforeAsync,
		beforeRightClick : beforeRightClick,
		onRightClick : onRightClick
	},
	view:{
		nameIsHTML:true,
		fontCss:sefFontCss
	}
}

//设置字体样式
function sefFontCss(treeId,treeNode){
	if(treeNode.status == '2'){
		return {color:"red"}
	}else if(treeNode.status == '3'){
		return {color:"#2df153"}
	}else{
		return {}
	}
}


// tree的异步加载，数据预处理
function dataFilter(treeId, parentNode, resp){
	if(resp.length){
		ats.forEachArray(resp,function(item,index){
			if(item.method == 'showEditor'){
				item.name = "("+(index+1)+"). "+item.name;
			}
			item.iconSkin = item.iconSkin=="1"?"folder":"file";
			item.isParent = item.isParent=="1"?true:false;
		})
	}else{
		resp.iconSkin = resp.iconSkin=="1"?"folder":"file";
		resp.isParent = resp.isParent=="1"?true:false;
	}
	return resp;
}

function onAsyncSuccess(event, treeId, treeNode, msg){

}

function beforeAsync(tid, tnode){
	if(tnode){
		if(tnode.isParent){
			return true;
		}else{
			return false;
		}
	}
		
}

function beforeRightClick(treeId, treeNode){
	return true;
}

function onRightClick(ev, treeId, treeNode){
	var menuBar = ats.createElement({
		name:"div",
		attrs:{
			className:"contextMenu",
		}
	},document.body);
	menuBar.style.top = ev.clientY+"px";
	menuBar.style.left = ev.clientX+"px";
	var menus = ats.createElement({
		name:"ul",
		attrs:{
			className:"contextMenu-ul"
		}
	},menuBar);
	
	if(treeNode && treeNode.getParentNode().pid == 7){
		ats.createElement(
			[
				{
					name: "li",
					attrs:{innerText:"Download",className:"contextMenu-item",onclick:downloadPdf,treeNode:treeNode}
				},{
					name: "li",
					attrs:{innerText:"Upload",className:"contextMenu-item",onclick:uploadHtml,treeNode:treeNode}
				},{
					name: "li",
					attrs:{innerText:"Delete",className:"contextMenu-item"}
				},{
					name: "li",
					attrs:{innerText:"Roll Back",className:"contextMenu-item"}
				}
			],
			menus
		);
	}else{
		var refresh = ats.createElement(
			{
				name: "li",
				attrs:{innerText:"Refresh",className:"contextMenu-item",treeNode:treeNode}
			},
			menus
		);
		refresh.onclick = function(){
			tree.reAsyncChildNodes(treeNode,"refresh");
			removeContextMenu();
		}
	}
	setTimeout(function(){
		if(menuBar.parentElement){
			document.body.removeChild(menuBar);
		}
	},2000);
	
	
}

// 页面加载完成事件
$(document).ready(function(){
	//进行websocket链接
	ws =  new WebSocket("ws://192.168.1.196:8081/jeesite/websocketServer");
//	ws =  new WebSocket("ws://localhost:8081/jeesite/websocketServer");
	//websocket信息处理
	ws.onmessage = function(e){
		var json = JSON.parse(e.data);
		window[json["method"]](json);
	}
	tree = $.fn.zTree.init($("#main-tree"), setting, null);
	//
	var shadow = document.getElementById("shadow");
	shadow.onclick = function(){
		this.innerHTML = "";
		this.style.display = "none";
	}
	container = document.querySelector(".container");
});

// 双击菜单，打开窗口
function openWindow(event,treeId,treeNode){
	if(!treeNode.isParent){
		tnode = treeNode;
		openNewPage(treeNode);
		
		
//		if(treeNode.pid == 7){
//			//节点为PDF
//		}else if(treeNode.pid ==8){
//			//节点为html
//			parseAct(treeNode);
//		}else{
//			if(treeNode.method == 'showEditor' && editor){
//				document.querySelector(".simditor-body").scrollTop = 0;
//				updateEditor(treeNode);
//			}else{
//				openNewPage(treeNode);
//			}
//		}
	}
}

function openNewPage(treeNode){
	// 添加导航项
	updateNavBar(treeNode);
	if(editor && treeNode.method == 'showEditor'){
		updateEditor(treeNode);
	}else{
		var div = null;
		if(document.querySelector(".active-page")){
			document.querySelector(".active-page").classList.remove("active-page");
		}
		if(document.querySelector("."+treeNode.tId)){
			div = document.querySelectorAll("."+treeNode.tId)[1];
		}else{
			// 添加Tab栏
			addTabItem(treeNode);
			div = ats.createElement({name:"div",attrs:{className:treeNode.tId}},container);
			// 调用方法
			window[treeNode.method](treeNode,div);
		}
		div.classList.add("active-page");
	}
}

//添加导航栏
function addNavItem(treeNode){
	var newItem = ats.createElement({name:"li",attrs:{className:"nav-item"},children:[{name:"i",attrs:{className:treeNode.isParent?"nav-folder":"nav-file"}},{name:"span",attrs:{innerText:treeNode.name}}		]	},document.querySelector("#nav-container"));
	newItem.previousElementSibling.className = "nav-item ahead-item";
	return newItem;
}

//添加Tab栏
function addTabItem(treeNode){
	if(document.querySelector(".current-item")){
		document.querySelector(".current-item").classList.remove('current-item');
	}
	var newItem = ats.createElement({name:"li",attrs:{className:"current-item "+treeNode.tId,id:treeNode.id,method:treeNode.method},children:[{name:"i",attrs:{className:"nav-file"}},{name:"span",attrs:{innerText:treeNode.name}},{name:"i",attrs:{	className:"item-close",onclick: closeTab}}]	},document.querySelector("#tab-container"));	
	if(treeNode.method == 'showEditor'){
		newItem.classList.add("editor-tab");
	}
	
	newItem.onclick = function(){
		switchTab(this);
	}
	return newItem;
}

// 切换Tab
function switchTab(item){
	var treeNode = tree.getNodeByParam("id", item.id, null);
	var navContainer = document.querySelector("#nav-container");
	navContainer.children[0].className = "nav-item";
	navContainer.innerHTML = navContainer.children[0].outerHTML;
	if(item){
		document.querySelector(".current-item").classList.remove("current-item");
		item.classList.add("current-item");
		ats.forEachArray(treeNode.getPath(),function(node,index){
			if(index!=0){
				addNavItem(node);
			}
		});
	}
	document.querySelector(".active-page").classList.remove("active-page");
	var page = document.querySelectorAll("."+treeNode.tId)[1];
	page.classList.add("active-page");
}

// 关闭Tab
function closeTab(e){
	var parent = this.parentElement;
	if(parent.method == 'showEditor'){
		editor = null;
		document.querySelector("#key-infor-panel").style.animation = "hideKeyInfor 1s forwards";
	}
	if(parent.classList.contains("current-item")){
		var tempElement = parent.previousElementSibling?parent.previousElementSibling:parent.nextElementSibling;
		if(tempElement){
			document.querySelectorAll("."+tempElement.classList[0])[1].classList.add("active-page");
			tempElement.classList.add("current-item");
		}else{
			var navContainer = document.querySelector("#nav-container");
			navContainer.children[0].className = "nav-item";
			navContainer.innerHTML = navContainer.children[0].outerHTML;
		}
	}
	parent.classList.remove("current-item");
	container.removeChild(document.querySelectorAll("."+parent.classList[0])[1]);
	document.querySelector("#tab-container").removeChild(parent);
	if(e){
		e.cancelBubble = true;
	}
}

// 显示提示信息
function showTip(msg){
	if(msg.data){
		msg = msg.data;
	}
	var tip = ats.createElement({
		name:"div",
		attrs:{
			className:"tip",
			innerText:msg
		}
	},document.body);
	tip.style.animation = "showTip ease-in 0.3s forwards";
	setTimeout(function(){
		tip.style.animation = "hideTip ease-in 0.3s forwards";
		setTimeout(function(){
			document.body.removeChild(tip);
		},400)
	},3000);
}

// 进行act 解析
function parseAct(treeNode){
	$.get(
			"/jeesite/a/data/parseAct",
			{"id": treeNode.fid,"treeId":treeNode.id},
			function(ret){
				if(ret.msg){
					showTip(ret.msg);
				}else{
					tree.reAsyncChildNodes(treeNode.getParentNode(),"refresh");
				}
			},
			"json"
	);
}

function updateNavBar(treeNode){
	var navContainer = document.querySelector("#nav-container");
	navContainer.innerHTML = navContainer.children[0].outerHTML;
	ats.forEachArray(treeNode.getPath(),function(item,index){
		if(index!=0){
			addNavItem(item);
		}
	});
}

function tableEditor(treeNode, div){
	var iframe = ats.createElement({
		name:"iframe",
		attrs:{
			src:"/jeesite/tableEditor.html"
		},
		style:{
			flex:"1",
			backgroundColor:"#fff"
		}
	},div);
	
}
