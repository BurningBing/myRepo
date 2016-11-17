// 显示签名页面
function showSignUpWin(treeNode,div){
	$.get(
		"/jeesite/a/data/findUnsignedState",
		function(ret){
			ats.forEachArray(ret,function(item,index){
				var data = ats.createElement({name:"div",attrs:{className:"sign-state",state:item,innerHTML:"<p class='sign-"+item+"'>"+item+"</p><p class='actCount'></p>"}},div);
				data.onclick = signUp;
				$.get(
					"/jeesite/a/data/getActsCount",
					{state:item},
					function(count){
						data.querySelector(".actCount").innerText = "("+count+")";
					}
				);
			});
		},
		"json"
	);
}

// 执行签名
function signUp(){
	var state = this.state;
	$.post(
		"/jeesite/a/data/signUpActs",
		{"state":state},
		function(ret){
			showTip(ret.msg);
			var node = tree.getNodeByParam("id",ret.pid);
			tree.reAsyncChildNodes(node,"refresh");
		},
		"json"
	);
}

//签名结束后调用，更新最新Act数量
function afterSignUp(json){
	var count = document.querySelector(".sign-"+json.state);
	if(count){
		count.nextElementSibling.innerText = "("+json.count+")"
	}
	
}

//----------------------------------------------------------------签名方法结束------------------------------------------------------------------------------------

//显示编辑器
function showEditor(treeNode,div){
	initEditorPage(treeNode,div);
	getEditorData(treeNode.fid);
}

function getEditorData(id){
	$.get(
		"/jeesite/a/data/getEditorData",
		{"id": id},
		function(ret){
			showEditorData(ret);
		},
		"json"
	);
}


function initEditorPage(treeNode,div){
	var snapshot = ats.createElement({
		name:"div",style:{flex:"1",borderRight:"2px solid #ccc",display:"flex"},attrs:{id:"snapshot-panel"}
	},div);
	var editorPanel = ats.createElement({
		name:"div",style:{flex:"1",display:"flex"}
	},div);
	var textarea = ats.createElement({
		name:"textarea"
	},editorPanel)
	
	editor = new Simditor({
	    textarea: $(textarea),
	    toolbar:['underline','strikethrough','alignment','bold','italic','blockquote','eraser']
	});
}

function showEditorData(data){
	section = data;
	var container = document.querySelector(".container");
	//展示快照
	var snapshot = document.getElementById("snapshot"); 
	if(snapshot){
		if(data.pid != snapshot.pid){
			snapshot.src = "/jeesite/a/data/getSnapshot?filepath="+data.snapshot;
			snapshot.pid = data.pid;
		}
	}else{
		showSnapshot(data.snapshot, data.pid);
	}
	//填充正文
	editor.setValue(data.content);
	//显示关键信息
	setTimeout(function(){
		showKeyInfor(data);
	},500);
	
}

// 显示关键信息
function showKeyInfor(data){
	document.querySelector("#key-infor-panel").style.animation = "showKeyInfor 1s forwards";
	var inputs = document.querySelector("#key-infor-panel").querySelectorAll("input");
	inputs[0].value = data.caption;
	inputs[1].value = data.description?data.description:'';
	inputs[2].value = data.shortName;
	inputs[3].value = data.eff?data.eff:'';
	inputs[4].value = data.exp?data.exp:'';
	inputs[5].value = data.link?data.link:'';
	inputs[5+data.updateType].checked = true;
}

// 显示快照
function showSnapshot(filepath, pid){
	var snapshot = ats.createElement({
		name:"iframe",
		attrs:{
			id:"snapshot",
			pid: pid,
			src:"/jeesite/a/data/getSnapshot?filepath="+filepath
		},
		style:{
			width:"100%",
			flex:"1",
			backgroundColor:"beige"
		}
	},document.querySelector("#snapshot-panel"));
}

// 更新编辑器数据
function updateEditor(treeNode){
	// 更新导航栏
	updateNavBar(treeNode);
	// 更新Tab栏
	var title = document.querySelector(".editor-tab").querySelector("span").innerText;
	ats.forEachArray(document.querySelector(".container").children,function(page,index){
		if(page.index == title){
			page.title = treeNode.fid;
			page.index = treeNode.name;
			return;
		}
	});
	document.querySelector(".editor-tab").querySelector("span").innerText = treeNode.name;
	getEditorData(treeNode.fid);
}

//----------------------------------------工具栏----------------------------------------

// 保存正文按钮
function saveSection(){
	var html = editor.getValue();
	html = html.replace(/&lt;/g,"<");
	html = html.replace(/&gt;/g,">");
	editor.setValue(html);
	$.post(
		'/jeesite/a/atsSection/saveSection',
		{id:section.id, html: editor.getValue()},
		function(ret){
			showTip(ret);
		}
	);
}

// 保存关键信息
function saveKeyInfor(){
	var caption,description,shortName,eff,exp,link,type;
	var inputs = document.querySelectorAll("#key-infor-panel input[type=text]");
	caption = inputs[0].value;
	description = inputs[1].value;
	shortName = inputs[2].value;
	eff = inputs[3].value;
	exp = inputs[4].value;
	link = inputs[5].value;
	var types = document.querySelectorAll("#key-infor-panel input[type=radio]");
	for(var i in types){
		if(types[i].checked){
			type = types[i].value;
			break;
		}
	}
	$.post(
			'/jeesite/a/atsSection/saveKeyInfor',
			{ id: section.id, caption:caption, description:description, shortName:shortName, eff : eff, exp  : exp, link : link, updateType :type },
			function(ret){
				var node = tree.getNodeByParam("fid",section.id);
				tree.reAsyncChildNodes(node.getParentNode(),"refresh");
				showTip(ret);
			}
	);
}

//设置字体样式
function sefFontCss(treeId,treeNode){
	return treeNode.status == '2' ? {color:"red"} : {};
}

// Section 提交
function submitSection(){
	if(confirm("是否要提交该Section？")){
		var keyInfor = document.getElementById("key-infor-panel");
		var eff = keyInfor.querySelectorAll("input")[3].value;
		if(eff){
			$.post(
				'/jeesite/a/atsSection/submitSection',
				{ id: section.id, tid:tnode.id },
				function(ret){
					if(ret.finish){
						var actNode = tree.getNodeByParam("fid",section.pid);
						tree.removeNode(actNode,false);
						document.querySelector(".current-item").querySelector(".item-close").click();
					}else{
						var sectionNode = tree.getNodeByParam("fid",section.id);
						sectionNode.status = "2";
						tree.updateNode(sectionNode);
						showTip("提交成功。")
					}
				},
				"json"
			);
		}else{
			showTip("提示：生效日期不可为空!");
		}
	}
}

//刷新Section
function refreshSection(){
	$.get(
			"/jeesite/a/atsSection/refreshSection",
			{id:section.id},
			function(ret){
				section = ret;
				editor.setValue(ret.content);
				showKeyInfor(ret);
				var node = tree.getNodeByParam("fid",section.id);
				node.name = section.caption;
				tree.updateNode(node);
			},
			"json"
	);
}

//删除Section
function deleteSection(){
	$.post(
			"/jeesite/a/atsSection/deleteSection",
			{id:section.id},
			function(ret){
				var node = tree.getNodeByParam("fid",ret.id);
				editor.setValue("");
				section = null;
				if(!ret.count){
					tree.removeNode(node.getParentNode());
				}else{
					tree.removeNode(node);
				}
				//隐藏关键信息
				document.querySelector("#key-infor-panel").style.animation = "hideKeyInfor 1s forwards";
				showTip("Delete Success!");
			},
			"json"
	)
}

//批量修改关键信息
function modifyKeyInfor(){
	var shadow = document.getElementById("shadow");
	shadow.style.display = "block";
	var modifyKeyInfor = ats.createElement({
		name:"div",
		attrs:{className:"modifyKeyInfor"},
		children:[
			{
				name:"select",
				attrs:{id:"key-infor-type"},
				style:{
					width:"200px",
					margin:"100px auto 0",
					display:"block"
				},
				children:[
					{
						name:"option",
						attrs:{innerText:"EffectiveDate",value:"Eff"}
					},{
						name:"option",
						attrs:{innerText:"ExpirationDate",value:"Exp"}
					},{
						name:"option",
						attrs:{innerText:"Link",value:"Link"}
					}
				]
			},
			{
				name:"input",
				attrs:{id:"key-infor-value"},
				style:{
					width:"200px",
					margin:"10px auto",
					display:"block"
				}
			},
			{
				name:"button",
				style:{display:"block",margin:"10px auto"},
				attrs:{innerText:"Submit",id:"modifyKeyInfor-submit-btn"}
			},
			{
				name:"button",
				style:{display:"block",margin:"10px auto"},
				attrs:{innerText:"Close",id:"modifyKeyInfor-close-btn"}
			}
		]
	},shadow);
	
	modifyKeyInfor.onclick = function(e){
		e.cancelBubble = true;
	}
	
	var submitBtn = document.getElementById("modifyKeyInfor-submit-btn");
	submitBtn.onclick = function(){
		var type = document.getElementById("key-infor-type").value;
		var value = document.getElementById('key-infor-value').value;
		$.post(
				"/jeesite/a/atsSection/modifyKeyInforBat",
				{pid:section.pid,type:type,value:value},
				function(ret){
					showTip(ret);
					shadow.style.display = "none";
					shadow.innerHTML = "";
					if(type == 'Eff'){
						document.getElementById("key-infor-panel").querySelectorAll("input")[3].value = value;
					}else if(type == 'Exp'){
						document.getElementById("key-infor-panel").querySelectorAll("input")[4].value = value;
					}else{
						document.getElementById("key-infor-panel").querySelectorAll("input")[5].value = value;
					}
				}
		);
		
	}
	
	var closeBtn = document.getElementById("modifyKeyInfor-close-btn");
	closeBtn.onclick = function(){
		shadow.style.display = "none";
		shadow.innerHTML = "";
	}
}

// 添加Section
function addSection(){
	$.post(
			"/jeesite/a/atsSection/addSection",
			{id:section.id},
			function(ret){
				var node = tree.getNodeByParam("fid",section.pid);
				tree.addNodes(node,ret.tree);
				section = ret.section;
				editor.setValue();
				showKeyInfor(section);
			},
			"json"
	);
}

// 下载PDF
function downloadPdf(ev){
	var target = ev.target;
	var form = ats.createElement({
		name:"form",
		attrs:{
			action:"/jeesite/a/atsAct/downloadAct",
			method: "post"
		},
		children:[{
			name:"input",
			attrs:{
				name:"sid",
				value:target.treeNode.fid
			}
		}]
		
	},document.body);
	form.submit();
	document.body.removeChild(form);
	removeContextMenu();
}

// 上传文件
function uploadHtml(ev){
	var treeNode = ev.target.treeNode;
	var input = ats.createElement({
		name:"input",
		attrs:{
			id:"filename",
			type:"file"
		},
		style:{
			display:"none"
		}
		
	},document.body);
	
	input.click();
	input.onchange = function(){
		var xhr = new XMLHttpRequest();
		xhr.open("POST","/jeesite/a/atsAct/uploadAct",true);
		xhr.setRequestHeader("X-Requested-With", "XMLHttpRequest");
		xhr.onreadystatechange = function() { 
			if (xhr.readyState == 4) {
				alert(xhr.responseText);
				input.parentElement.removeChild(input);
				removeContextMenu();
				tree.reAsyncChildNodes(treeNode.getParentNode(),"refresh");
			}
		}
		var fd = new FormData(); 
		fd.append("filename", input.files[0]);
		fd.append("sid",treeNode.fid);
		fd.append("treeId",treeNode.id);
		//执行发送 
		xhr.send(fd); 
	}
}

function removeContextMenu(){
	var div = document.querySelector(".contextMenu");
	if(div){
		div.parentElement.removeChild(div);
	}
}

//显示历史版本
function showHistory(){
	var shortName = document.querySelectorAll("#key-infor-panel input")[2].value;
	var state = section.state;
	$.post(
		"/jeesite/a/data/getSectionHistory",
		{
			shortName:shortName,
			state,state
		},
		function(ret){
			var shadow = document.getElementById("shadow");
			shadow.style.display = "block";
			var div = ats.createElement({
				name:"div",
				attrs:{
					className: "original-version",
					innerHTML : ret
				}
			},shadow);
			div.onclick = function(e){
				e.cancelBubble = true;
			}
		},
		"html"
	);
}

function repealBat(){
	var popwindow = ats.createElement({
		name:"div",
		attrs:{
			id:"popwin"
		},
		style:{
			width:"400px",
			height:"200px",
			backgroundColor:"rgb(240, 240, 240)",
			border:"1px solid rgb(181, 181, 181)",
			borderRadius:"3px",
			position:"absolute",
			top:"200px",
			right:"200px",
			boxShadow:"0px 2px 5px"
		}
		
	},document.body);
	
	popwindow.innerHTML = "<p style='margin-left:20px'>Please input the captions</p>" +
			"<div style='margin:0 0 30px 30px'><input id='captions' style='width:250px' type='text' /></div>" +
			"<div style='margin:0 0 30px 30px'><select id='batType'>" +
				"<option value='1'>New</option>" +
				"<option value='3'>Repeal</option>" +
			"</select></div>" +
			"<div style='text-align:right;margin-right:10px'><button style='margin:10px' onclick='submitPopwin()'>Submit</button><button onclick='closePopwin()'>Cancel</button></div>";
	
//	var captions = prompt("Please input the cpation:");

}

function submitPopwin(){
	var captions = document.getElementById('captions').value;
	var type = document.getElementById('batType').value;
	if(captions){
		$.post(
			"/jeesite/a/atsSection/repealBath",
			{sid:section.pid,value:captions,type:type},
			function(ret){
				var node = tree.getNodeByParam("fid",ret);
				tree.reAsyncChildNodes(node,"refresh");
				closePopwin();
			},
			"json"
		);
	}
}

function closePopwin(){
	document.body.removeChild(document.getElementById("popwin"));
}

function combine(){
	var ids = [];
	var nodes = tree.getSelectedNodes();
	var parentNode = nodes[0].getParentNode();
	ats.forEachArray(nodes,function(item,index){
		ids.push(item.fid);
	});
	$.post(
		"/jeesite/a/atsSection/combineSections",
		{ids:JSON.stringify(ids)},
		function(ret){
			tree.reAsyncChildNodes(parentNode,"refresh");
			showEditorData(ret);
		},
		"json"
	);
}

function viewLink(ev){
	window.open(section.href);
}

function showFeedback(treeNode,div){
	
	ats.createElement({
		name:"iframe",
		attrs:{
			src:"/jeesite/a/data/getFeedback?sid="+treeNode.fid
		},
		style:{
			width:"100%",
			backgroundColor:"#fff"
		}
	},div);
}
