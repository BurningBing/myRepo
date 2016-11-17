//页面加载后执行
window.onload = function(){
// 	var ws =  new WebSocket("ws://localhost:8080/jeesite/websocketServer");
	ws =  new WebSocket("ws://http://192.168.1.103/:8080/jeesite/websocketServer");
	ws.onerror = function(e){
		ws =  new WebSocket("ws://192.168.1.209:8090/jeesite/websocketServer");
	}
//	
	
	ws.onmessage = function(e){
		var json = JSON.parse(e.data);
		window[json["method"]](json);
	}
	// 鼠标右悬浮，显示菜单栏
    document.body.onmousemove = function(e){
        if(e.x<10){
        	if(D.byId("menu").className != 'showMenu'){
        		document.getElementById('menu').className = "showMenu";
        	}
        }else if(e.x>251){
            if(document.getElementById('menu').className&&document.getElementById('menu').className != "hiddenMenu"){
                document.getElementById('menu').className = "hiddenMenu";
            }
        }
    }

	// zTree Config
	setting = {
		callback:{
			onClick : resetTitle,
			onDblClick: showSection
		},
		check:{
			enable : true,
			chkboxType:{ "Y": "s", "N": "s" }
		},
		view: {
			nameIsHTML:true,
			selectedMulti: false,
			addHoverDom:addHoverDom,
			removeHoverDom:removeHoverDom,
			fontCss:sefFontCss
		},
		data: {
			keep:{
				parent: true
			}
		}
	}
}

//设置字体样式
function sefFontCss(treeId,treeNode){
	return treeNode.status == '2' ? {color:"red"} : {};
}

//显示下载窗体
function showDownloadPanel(){
	var win = D.newCommonWin("downloadPanel");
	win.setTitleName("Download Panel");
	var main = D.newEl("div",{id:"download-main"});
	var header = D.newEl("ul");
	var index = D.newEl("li",{innerText:"#",className:"download-header"});
	var state = D.newEl("li",{innerText:"State",className:"download-header"});
	var year = D.newEl("li",{innerText:"Session Year",className:"download-header"});
	var Count = D.newEl("li",{innerText:"Count",className:"download-header"});
	var tool = D.newEl("li",{className:"download-header"});
	header.appendChild(index);
	header.appendChild(state);
	header.appendChild(year);
	header.appendChild(Count);
	header.appendChild(tool);
	main.appendChild(header);
	win.body.appendChild(main);
	$("#main").append(win);
	//show min icon
	var minWin = D.newEl("div",{id:"downloadPanel-min",className:"min-item"},{backgroundImage:"url('image/min/download-32.png')",backgroundColor:"rgb(48, 133, 121)"});
	minWin.onclick = function(){
		if(win.style.display=="none"){
			win.style.display = "flex";
		}else{
			win.style.display = "none";
		}
	}
	$("#footer").append(minWin);
	var list = D.newEl("div",{},{overflow: "auto",height:"calc(100% - 30px)",clear: "both"});
	$.post(
		"a/atsTask/taskList",
		function(array){
			for(var i in array){
				var ul = D.newEl("ul",{className:"task"},{});
				var json = array[i];
				ul.appendChild(D.newEl("li",{innerText:parseInt(i)+1,className:"donwload-item"}));
				ul.appendChild(D.newEl("li",{innerText:json.state,className:"donwload-item"}));
				ul.appendChild(D.newEl("li",{innerText:json.sessionYear,className:"donwload-item"}));
				ul.appendChild(D.newEl("li",{innerText:json.count,className:"donwload-item"}));
				var itemTool = D.newEl("li",{className:"donwload-item"},{width:"101px"});
				var downloadBtn = D.newEl("i",{className:"download-btn"},{backgroundImage:"url('./image/menu/download-24.png')"});
				downloadBtn.taskId = json.id;
				downloadBtn.onclick = function(){
					//执行下载程序
					$.post(
						"a/download/checkingUpdate",
						{id:this.taskId}
					);
				}
				var linkBtn = D.newEl("i",{className:"download-btn"},{backgroundImage:"url('./image/menu/link-24.png')"});
				linkBtn.url = json.url;
				linkBtn.onclick = function(){
					// open link
					window.open(this.url);
				}
				var setting = D.newEl("i",{className:"download-btn"},{backgroundImage:"url('./image/menu/setting-24.png')"});
				setting.taskId = json.id;
				setting.state = json.state;
				setting.sessionYear = json.sessionYear;
				setting.url = json.url;
				setting.onclick = function(){
					var taskId = this.parentElement.children[0].taskId;
					// task setting
					var settingWin = D.newCommonWin("taskSetting",{w:"550px",h:"300px",t:"200px"});
					settingWin.setTitleName("Task Setting - "+this.state);
					var div = D.newEl("div",{},{width:"100%",margin:"0 5px 5px 5px",backgroundColor:"#fff",borderRadius:"5px",padding:"10px"});
					div.innerHTML = "<b>State</b><br><input value='"+this.state+"' readonly style='background-color:#ccc;border:1px solid #aaa;width:50%' /><br><br>"
					div.innerHTML += "<b>Session Year</b><br><input style='width:50%' value='"+this.sessionYear+"'/><br><br>"
					div.innerHTML += "<b>Link</b><br><textarea style='width:100%;height:70px'>"+this.url+"</textarea>";
					div.innerHTML += "<br><br><button onclick='fun("+taskId+")'>Save</button>&nbsp;&nbsp;&nbsp;&nbsp;<button>Close</button>"
					settingWin.body.appendChild(div);
					$("#main").append(settingWin);
				}
				itemTool.appendChild(downloadBtn);
				itemTool.appendChild(linkBtn);
				itemTool.appendChild(setting);
				ul.appendChild(itemTool);
				list.appendChild(ul);
			}
			main.appendChild(list);
		},
		"json"
	);
}

//显示PDF签名列表
function showPdfActs(mode){
	var win = D.newCommonWin("all-acts-panel");
	if(mode==1){
		win.setTitleName("All Acts");
	}else{
		win.setTitleName("Special Acts");
	}
	var states = D.newEl("div",{className:"states"});
	var stateHeader = D.newEl("div",{className:"acts-title",innerText:"State"},{width:"100%"});
	states.appendChild(stateHeader);
	var stateList = D.newEl("div",{},{height: "calc(100% - 65px)",overflow: "auto",backgroundColor:"#f6f6f6",color:"#555"});
	states.appendChild(stateList);
	//show min icon
	var minWin = D.newEl("div",{id:"all-acts-panel-min",className:"min-item"},{backgroundImage:"url('image/min/download-32.png')",backgroundColor:"rgb(48, 103, 133)"});
	minWin.onclick = function(){
		if(win.style.display=="none"){
			win.style.display = "flex";
		}else{
			win.style.display = "none";
		}
	}
	$("#footer").append(minWin);
	$.post(
		"a/atsAct/findUnsignedStates",
		{type:"1"},
		function(array){
			for(var i in array){
				var state = D.newEl("div",{innerText:array[i],className:"state-item"});
				//切换州数据
				state.onclick = function(){
					D.byId("acts-list-data").innerHTML = "";
					$.post(
						"a/atsAct/findUnsignedActsByState",
						{"state":this.innerText,"type":"1"},
						function(ret){
							for(var j=0;j<ret.length;j++){
								var act = ret[j];
								var d = D.newEl("div",{id:"act"+act.ID});
								var no = D.newEl("div",{innerText:parseInt(j)+1,className:"acts-item"},{width:"65px"});
								var billNumber = D.newEl("div",{innerText:act.BILL_NUMBER,className:"acts-item"},{width:"130px"});
								var date = D.newEl("div",{innerText:act.DAY,className:"acts-item"},{width:"110px"});
								var signUp = D.newEl("div",{className:"acts-item"},{width:"calc(100% - 305px)"});
								var signBtn = D.newEl("div",{className:"signBtn",innerText:"Sign Up",actId:act.ID});
								signBtn.onclick = function(){
									$.post(
										"a/atsSign/signUp",
										{actId:this.actId},
										function(actId){
											if(actId){
												D.byId("acts-list-data").removeChild(D.byId("act"+actId));	
											}else{
												alert("This act is signed by others Oo(^_^)oO");
											}
											
										}
									);
								}
								signUp.appendChild(signBtn);
								d.appendChild(no);
								d.appendChild(billNumber);
								d.appendChild(date);
								d.appendChild(signUp);
								D.byId("acts-list-data").appendChild(d);
							}
						},"json"
					)
				}
				stateList.appendChild(state);
				if(i==0){
					state.className += " currentState";
					state.click();
					
				}
			}
		},
		"json"
	);
	var actList = D.newEl("div",{id:"acts-list"});
	var header = D.newEl("div");
	var no = D.newEl("div",{innerText:"#",className:"acts-title"},{width:"65px"});
	var billNumber = D.newEl("div",{innerText:"Bill Number",className:"acts-title"},{width:"130px"});
	var date = D.newEl("div",{innerText:"Date",className:"acts-title"},{width:"110px"});
	var signUp = D.newEl("div",{innerText:"Sign Up",className:"acts-title"},{width:"130px"});
	header.appendChild(no);
	header.appendChild(billNumber);
	header.appendChild(date);
	header.appendChild(signUp);
	var data = D.newEl("div",{id:"acts-list-data"});
	actList.appendChild(header);
	actList.appendChild(data);
	win.body.appendChild(states);
	win.body.appendChild(actList);
	$("#main").append(win);
}

//树状图鼠标悬浮触发函数
function addHoverDom(treeId, n){
	var aObj = $("#" + n.tId + "_a");
	if(n.level==1){
		if(n.type == 1){
			if ($("#diyBtn_upload").length>0) return;
			//上传HTML文件
			var btn1 = D.newEl("span",{className:"button",id:"diyBtn_upload",title:"上传"},{backgroundImage:"url('image/menu/upload-16.png')"});
			btn1.onclick = function(){
				uploadHtml(n.id);
			}
			//下载PDF
			var btn2 = D.newEl("span",{className:"button",id:"diyBtn_download",title:"下载"},{backgroundImage:"url('image/menu/download-16.png')"});
			btn2.onclick = function(){
				downloadPdf(n)
			}
			// 查看快照
			var btn3 = D.newEl("span",{className:"button",id:"diyBtn_view",title:"查看快照"},{backgroundImage:"url('image/menu/view-16.png')"});
			btn3.onclick = function(){
				viewSnapshot(n.name,n.id);
			}
			// 删除Act
			var btn4 = D.newEl("span",{className:"button",id:"diyBtn_delete",title:"删除"},{backgroundImage:"url('image/menu/delete-16.png')"});
			btn4.onclick = function(){
				deleteAct(n);
			}
			aObj.append(btn1);
			aObj.append(btn2);
			aObj.append(btn3);
			aObj.append(btn4);
		}else{
			if ($("#diyBtn_view").length>0) return;
			// 查看快照
			var btn3 = D.newEl("span",{className:"button",id:"diyBtn_view",title:"查看快照"},{backgroundImage:"url('image/menu/view-16.png')"});
			btn3.onclick = function(){
				viewSnapshot(n.name,n.id);	
			}
			// 删除Act
			var btn4 = D.newEl("span",{className:"button",id:"diyBtn_delete",title:"删除"},{backgroundImage:"url('image/menu/delete-16.png')"});
			btn4.onclick = function(){
				deleteAct(n);
			}
			aObj.append(btn3);
			aObj.append(btn4);
		}
	}else if(n.level == 2){
		// 删除Act
		if ($("#diyBtn_delete").length>0) return;
		var btn4 = D.newEl("span",{className:"button",id:"diyBtn_delete",title:"删除"},{backgroundImage:"url('image/menu/delete-16.png')"});
		btn4.onclick = function(){
			deleteSection(n);
		}
		aObj.append(btn4);
	}
}

//上传HTML
function uploadHtml(id){
	if(D.byId("uploadForm")){
		document.body.removeChild(D.byId("uploadForm"));
	}
	var form = D.newEl("form",{"id":"uploadForm","action":"a/atsAct/uploadAct","method":"post","enctype":"multipart/form-data"});
	var inputId = D.newEl("input",{"name":"id","type":"hidden","value":id});
	form.appendChild(inputId);
	var inputFile = D.newEl("input",{"name":"filename","type":"file"},{display:"none"});
	inputFile.onchange = submitUploadRequest;
	form.appendChild(inputFile);
	document.body.appendChild(form);
	inputFile.click();
}

//下载PDF
function downloadPdf(treeNode){
	var form = D.newEl("form",{"action":"a/atsAct/downloadAct","method":"post"});
	var input = D.newEl("input",{"name":"id","value":treeNode.id});
	form.appendChild(input);
	document.body.appendChild(form);
	form.submit();
	document.body.removeChild(form);
}

//查看快照
function viewSnapshot(name,id){
	if(!D.byId("snapshot-min")){
		var snapshotWin = D.newCommonWin("snapshot-panel",{"l":"100px","t":"10px","h":"750px",w:"700px"});
		snapshotWin.setTitleName(name);
		var iframe = D.newEl("iframe",{"id":"snapshot"});
		snapshotWin.body.style.height = "570px";
		snapshotWin.body.appendChild(iframe);
		D.byId("main").appendChild(snapshotWin);
		var minWin = D.newEl("div",{id:"snapshot-panel-min",className:"min-item"},{backgroundImage:"url('image/min/download-32.png')",backgroundColor:"rgb(128, 23, 133)"});
		minWin.onclick = function(){
			if(snapshotWin.style.display=="none"){
				snapshotWin.style.display = "flex";
			}else{
				snapshotWin.style.display = "none";
			}
		}
		$("#footer").append(minWin);
		iframe.src = "a/atsAct/viewSnapshot?id="+id;
	}else{
		var snapshot = el_Id("snapshot");
		snapshot.src = "a/atsAct/viewSnapshot?id="+id;
	}
}

//删除ACT
function deleteAct(treeNode){
	if(confirm("是否确定删除"+treeNode.name+"？")){
		$.post(
			"a/atsAct/deleteAct",
			{id:treeNode.id},
			function(ret){
				var treeObj = $.fn.zTree.getZTreeObj("sectionList");
				treeObj.removeNode(treeNode);
			}
		);
	}
}

//删除Section
function deleteSection(treeNode){
	if(confirm("是否确定删除"+treeNode.name+"？")){
		$.post(
			"a/atsSection/removeSection",
			{"ids":treeNode.id},
			function(ret){
				var treeObj = $.fn.zTree.getZTreeObj("sectionList");
				treeObj.removeNode(treeNode);
			}
		);
	}
}

//树状图鼠标悬浮离开触发事件
function removeHoverDom(treeId, treeNode){
	if(treeNode.type==2&&treeNode.actType=="html"){
		$("#diyBtn_view").remove();
		$("#diyBtn_delete").remove();
	}else{
		$("#diyBtn_upload").remove();
		$("#diyBtn_download").remove();
		$("#diyBtn_view").remove();
		$("#diyBtn_delete").remove();
	}
}

//显示HTML签名列表
function showHtmlActs(){
	var win = D.newCommonWin("html-acts-panel",{l:"50px"});
	win.setTitleName("HTML Acts");
	var states = D.newEl("div",{className:"states"});
	var stateHeader = D.newEl("div",{className:"acts-title",innerText:"State"},{width:"100%"});
	states.appendChild(stateHeader);
	var stateList = D.newEl("div",{},{height: "calc(100% - 65px)",overflow: "auto",backgroundColor:"#f6f6f6",color:"#555"});
	states.appendChild(stateList);
	//show min icon
	var minWin = D.newEl("div",{id:"html-acts-panel-min",className:"min-item"},{backgroundImage:"url('image/min/download-32.png')",backgroundColor:"rgb(48, 103, 133)"});
	minWin.onclick = function(){
		if(win.style.display=="none"){
			win.style.display = "flex";
		}else{
			win.style.display = "none";
		}
	}
	$("#footer").append(minWin);
	$.post(
		"a/atsAct/findUnsignedStates",
		{type:"2"},
		function(array){
			for(var i in array){
				var state = D.newEl("div",{innerText:array[i],className:"state-item"});
				//切换州数据
				state.onclick = function(){
					D.byId("acts-list-data").innerHTML = "";
					$.post(
						"a/atsAct/findUnsignedActsByState",
						{"state":this.innerText,"type":"2"},
						function(ret){
							for(var j=0;j<ret.length;j++){
								var act = ret[j];
								var d = D.newEl("div",{id:"act"+act.ID});
								var no = D.newEl("div",{innerText:parseInt(j)+1,className:"acts-item"},{width:"65px"});
								var billNumber = D.newEl("div",{innerText:act.BILL_NUMBER,className:"acts-item"},{width:"130px"});
								var date = D.newEl("div",{innerText:act.DAY,className:"acts-item"},{width:"110px"});
								var open = D.newEl("div",{className:"acts-item"},{width:"calc(100% - 305px)"});
								var openBtn = D.newEl("div",{className:"signBtn",innerText:"Open",actId:act.ID});
								openBtn.onclick = function(){
									//调用解析 
									$.post(
										"a/atsAct/parseAct",
										{"id":this.actId},
										function(ret){
											var editor = createEditor();
											editor.setTitleName("ACT ID : "+ret.id);
											var zTreeObj = $.fn.zTree.init($("#sectionList"), setting, ret);
										},
										"json"
									);
								}
								open.appendChild(openBtn);
								d.appendChild(no);
								d.appendChild(billNumber);
								d.appendChild(date);
								d.appendChild(open);
								D.byId("acts-list-data").appendChild(d);
							}
						},"json"
					)
				}
				stateList.appendChild(state);
				if(i==0){
					state.click();
				}
			}
		},
		"json"
	);
	var actList = D.newEl("div",{id:"acts-list"});
	var header = D.newEl("div");
	var no = D.newEl("div",{innerText:"#",className:"acts-title"},{width:"65px"});
	var billNumber = D.newEl("div",{innerText:"Bill Number",className:"acts-title"},{width:"130px"});
	var date = D.newEl("div",{innerText:"Date",className:"acts-title"},{width:"110px"});
	var signUp = D.newEl("div",{className:"acts-title"},{width:"130px"});
	header.appendChild(no);
	header.appendChild(billNumber);
	header.appendChild(date);
	header.appendChild(signUp);
	var data = D.newEl("div",{id:"acts-list-data"});
	actList.appendChild(header);
	actList.appendChild(data);
	win.body.appendChild(states);
	win.body.appendChild(actList);
	$("#main").append(win);
}

function showMyHtml(){
	var editor = createEditor();
	editor.setTitleName("My Acts");
	$.post(
		"a/atsAct/showMyAct",
		{type:"2"},
		function(array){
			$.fn.zTree.init($("#sectionList"), setting, array);
		},
		"json"
	);
}


function showMyFeedback(){
	var win = D.newCommonWin("feedback-list",{l:"5px",t:"5px",w:"850px"});
	win.setTitleName("Feedback List");
	win.body.style.flexFlow = "column";
	var header = D.newEl("div",{},{height:"35px",backgroundColor:"rgba(0, 0, 0, 0.57)",borderRadius:"5px",margin:"5px",boxShadow:"inset 0 0 5px rgb(197, 197, 194)"});
	header.innerHTML = "<ul class='feedback-head'>" +
														"<li style='width:10%'>#</li>" +
														"<li style='width:15%'>Id</li>" +
														"<li style='width:15%'>State</li>" +
														"<li style='width:20%'>Bill Number</li>" +
														"<li style='width:20%'>Day</li>" +
														"<li style='width:10%'>From</li>" +
														"<li style='width:10%'>Opt.</li>" +
													"</ul>";
	var list = D.newEl("div",{id:"fl",className:"content mCustomScrollbar"},{flex:"1",margin:"0 5px 5px 5px",borderRadius:"5px",backgroundColor:"rgba(99, 97, 92, 0.66)",height:"calc(100% - 80px)",overflow:"auto"});
	$.get(
			"a/atsAct/getFeedbackDataSync",
			function(ret){
				for(var i=0;i<ret.length;i++){
					var ul = D.newEl("ul",{id:ret[i].ACT_ID,className:"feedback-list"});
					ul.appendChild(D.newEl("li",{innerText:i+1},{width:"10%"}));
					ul.appendChild(D.newEl("li",{innerText:ret[i].ACT_ID},{width:"15%"}));
					ul.appendChild(D.newEl("li",{innerText:ret[i].STATE},{width:"15%"}));
					ul.appendChild(D.newEl("li",{innerText:ret[i].BILL_NUMBER},{width:"20%"}));
					ul.appendChild(D.newEl("li",{innerText:ret[i].DAY},{width:"20%"}));
					if(ret[i].STATUS == 3){
						ul.appendChild(D.newEl("li",{innerText:'系统对比'},{width:"10%"}));
					}else{
						ul.appendChild(D.newEl("li",{innerText:'组长检查'},{width:"10%"}));
					}
					ul.appendChild(D.newEl("li",{innerHTML:"<a href='javascript:void(0)' onclick='fixFeedback("+ret[i].ACT_ID+")'>View</a>"},{width:"10%"}));
					list.appendChild(ul);
					win.body.appendChild(list);
				}
			},
			"json"
	);
											
	win.body.appendChild(header);
	$("#main").append(win);
	
	$("#fl").mCustomScrollbar({
		theme:"minimal"
	});
}

function fixFeedback(id){
	var win = D.newCommonWin("fix-feedback",{h:"80%",w:"80%",l:"10%"});
	win.setTitleName("Fix Feedback");
	var left = D.newEl("div",{},{overflow:"hidden",flex:"1",margin:"0 5px 5px",backgroundColor:"#8f8c85",borderRadius:"5px",display:"flex",flexFlow:"column"});
	var leftHeader = D.newEl("div",{},{borderBottom:"1px solid #fff",width:"100%",flex:"0 0 30px",display:"flex"});
	leftHeader.innerHTML = "<div class='fix-head cur-fix' onclick='showFeedbackResult(this,"+id+")'>" +
																"Snapshot"+
														  "</div>" +
														  "<div class='fix-head' onclick='showFeedbackResult(this,"+id+")'>" +
														  		"Feedback" +
														  "</div>"
	var leftMain = D.newEl("div",{},{flex:"1",display:"flex",flexFlow:"column",overflow:"auto",padding:"5px",color:"#555",backgroundColor:"#fff"});
	leftMain.innerHTML = "<iframe style='flex:1' src='a/atsAct/viewSnapshot?id="+id+"'></iframe>" ;

	left.appendChild(leftHeader);
	left.appendChild(leftMain);
	var right = D.newEl("div",{},{flex:"1",margin:"0 5px 5px 0px",backgroundColor:"#8f8c85",overflow:"hidden",borderRadius:"5px",display:"flex"});
	var rightTree = D.newEl("div",{className:"zTree",id:"fixTree"},{flex:"0 0 150px",backgroundColor:"#fff",paddingTop:"10px",borderRight:"1px solid #ccc"});
	var setting = {
			data:{
				simpleData:{enable:true}
			},
			callback:{
				beforeDblClick:showFixSection
			}
	}
	$.get(
			"a/atsSection/getFeedbackTree",
			{actId:id},
			function(array){
				$.fn.zTree.init($(rightTree), setting, array);
			},
			"json"
	)
	
	var rightButton = D.newEl("div",{},{display:"flex"});
	rightSave = D.newEl("div",{innerText:"Save"},{textAlign:"center",flex:"1",color:"#fff",backgroundColor:"#0a0a9d",cursor:"pointer"});
	rightSave.onclick = function(){
		var html = editor.getValue();
		$.post(
			"a/atsSection/saveContent",
			{"id":D.curFixSec,"html":editor.getValue()},
			function(data){
				if(data.flag){
					alert(data.msg);
					editor.setValue(data.html);
				}
			},
			"json"
		);
	}
	rightSubmit = D.newEl("div",{innerText:'Submit'},{textAlign:"center",flex:"1",cursor:"pointer",color:"#fff"});
	rightSubmit.onclick = function(){
		$.get(
				"a/atsSection/submitFeedback",
				{id:D.curFixSec},
				function(ret){
					editor.setValue("");
					var treeObj = $.fn.zTree.getZTreeObj("fixTree");
					var node = treeObj.getNodeByParam("id",D.curFixSec);
					treeObj.removeNode(node);
					D.curFixSec = null;
					if(treeObj.transformToArray(treeObj.getNodes()).length==1){
						document.getElementById("id").parentElement.removeChild(document.getElementById("id"));
					}
					
				}
		)
	}
	rightButton.appendChild(rightSave);
	rightButton.appendChild(rightSubmit);
	
	var rightEditor = D.newEl("div",{},{flex:"1",display:"flex",flexFlow:"column"});
	var fixEditor = D.newEl("div",{},{flex:"1",height:"70%"});
	fixEditor.innerHTML = "<textarea id='fix-editor'></textarea>";
	var keyPanel = D.newEl("div",{},{});
	var key1 = D.newEl("div",{},{backgroundColor:"#d1d1d1"});
	key1.innerHTML = "<div style='border-bottom:1px solid #4e4e4e;color:#555;display:flex'>" +
												"<span>Description: </span><input id='fixDesc' style='flex:1' />" +
											"</div>";
	var key2 = D.newEl("div",{},{backgroundColor:"#d1d1d1"});
	key2.innerHTML = "<div style='border-bottom:1px solid #4e4e4e;color:#555;display:flex'>" +
												"<span>EffectiveDate: </span><input id='fixEff' style='flex:1' />" +
											"</div>";
	var key3 = D.newEl("div",{},{backgroundColor:"#d1d1d1"});
	key3.innerHTML = "<div style='border-bottom:1px solid #4e4e4e;color:#555;display:flex'>" +
												"<span>ExpirationDate: </span><input id='fixExp' style='flex:1' />" +
											"</div>";
	var key4 = D.newEl("div",{},{backgroundColor:"#d1d1d1"});
	key4.innerHTML = "<div style='border-bottom:1px solid #4e4e4e;color:#555;display:flex'>" +
												"<span>Link: </span><input id='fixLink' style='flex:1' />" +
											"</div>";
	var key5 = D.newEl("div",{},{backgroundColor:"#d1d1d1"});
	key5.innerHTML = "<div style='border-bottom:1px solid #4e4e4e;color:#555;display:flex'>" +
												"<span>ShortName: </span><input id='fixShortName' style='flex:1' />" +
											"</div>";
	var key6 = D.newEl("div",{},{backgroundColor:"#d1d1d1"});
	key6.innerHTML = "<div style='border-bottom:1px solid #4e4e4e;color:#555;display:flex'>" +
												"<span>UpdateType: </span><select id='fixUpdateType' style='flex:1'>" +
												"<option value='1'>New</option>"+
												"<option value='2'>Modify</option> "+
												"<option value='3'>Repeal</option>"+
												"</select>" +
											"</div>";
	keyPanel.appendChild(key4);
	keyPanel.appendChild(key5);
	keyPanel.appendChild(key6);
	keyPanel.appendChild(key1);
	keyPanel.appendChild(key2);
	keyPanel.appendChild(key3);
	
	
	rightEditor.appendChild(fixEditor);
	rightEditor.appendChild(rightButton);
	rightEditor.appendChild(keyPanel);
	
	right.appendChild(rightTree);
	right.appendChild(rightEditor);
	
	win.body.appendChild(left);
	win.body.appendChild(right);
	$("#main").append(win);
	
	editor = new Simditor({
		  textarea: $('#fix-editor'),
		  toolbar:['underline','strikethrough','alignment','bold','italic','blockquote','eraser']
	});
}

function showFixSection(treeId,treeNode){
	D.curFixSec = treeNode.id;
	if(!treeNode.isParent){
		$.get(
				"a/atsSection/getFixSectionData",
				{id:treeNode.id},
				function(ret){
					editor.setValue(ret.content);
					$('#fixDesc').val(ret.description);
					$('#fixEff').val(ret.effectiveDate);
					$('#fixExp').val(ret.expirationDate);
					$('#fixShortName').val(ret.shortName);
					$('#fixLink').val(ret.link);
					$('#fixUpdateType').val(ret.updateType);
				},
				"json"
		);
		
		
		
	}
}

function showFeedbackResult(self, id){
	var main = self.parentElement.nextElementSibling;
	if(self.innerText == 'Snapshot'){
		self.className = "fix-head cur-fix";
		self.nextElementSibling.className = "fix-head";
		main.innerHTML = "<iframe style='flex:1' src='a/atsAct/viewSnapshot?id="+id+"'></iframe>" ;
	}else if(self.innerText == 'Feedback'){
		self.className = "fix-head cur-fix";
		self.previousElementSibling.className = "fix-head";
		$.get(
				"a/ats/atsFeedback/getDataByActIdSync",
				{actId:id},
				function(ret){
					main.innerHTML = ret;
				}
		)
		
	}
}

//签名之后执行
function afterSignUp(json){
	if(D.byId("act"+json.actId)){
		D.byId("acts-list-data").removeChild(D.byId("act"+json.actId));
	}
}

//接收WebSocket消息
function receiveMessage(json){
	alert(json.sender+"说："+json.content);
}

//websocket回掉函数
function removeTreeNode(json){
	var treeObj = $.fn.zTree.getZTreeObj("sectionList");
	var n = treeObj.getNodesByParam("id",json.data)[0];
	treeObj.removeNode(n);
	D.curSect = null;
}

//显示个人签名Act
function showMyActs(){
	var editor = createEditor();
	editor.setTitleName("My Acts");
	$.post(
		"a/atsAct/showMyAct",
		{type:"1"},
		function(array){
			$.fn.zTree.init($("#sectionList"), setting, array);
		},
		"json"
	);
}

//创建编辑器
function createEditor(){
	var height = (D.byId("main").offsetHeight * 0.94) +"px";
	var top = (D.byId("main").offsetHeight * 0.06/2) +"px";
	var editor = D.newCommonWin("editor",{h:height,t:top,w:"850px",r:"20px"});
	var body = editor.body;
	var div = D.newEl("div",{},{width:"235px",height:"calc(100% - 35px)",backgroundColor:"#fff",margin:"0px 0px 5px 5px",borderRadius:"3px",display:"flex",flexDirection:"column",overflow:"hidden"});
	var sectionList = D.newEl("div",{id:"sectionList",className:"ztree"},{flex:"1",overflow:"auto"});
	var combineBtn = D.newEl("div",{id:"combineBtn",innerText:"Combine"});
	combineBtn.onclick = function(){
		if(confirm("是否确认合并选中Section？")){
			var treeObj = $.fn.zTree.getZTreeObj("sectionList");
			var nodes = treeObj.getCheckedNodes(true);
			var ids = [];
			for(var i=0;i<nodes.length;i++){
				ids.push(nodes[i].id);
			}
			$.post(
				"a/atsSection/combineSections",
				{"ids":ids.join(",")},
				function(ret){
					for(var id in ret){
						var n = treeObj.getNodesByParam("id",ret[id]);
						treeObj.removeNode(n[0]);
						$.post(
							"a/atsSection/findSectionById",
							{id:ids[0]},
							function(ret2){
								D.curSect = ret2;
								simditor.setValue(ret2.html);
								for(var name in ret2){
									if(D.byId(name)){
										D.byId(name).value = ret2[name];
									}
								}
							},
							"json"
						);
					}
				},
				"json"
			)
		}
	}
	
	
	
	
	
	div.appendChild(sectionList);
	div.appendChild(combineBtn);
	var sectionContainer = D.newEl("div",{},{width:"800px",height:"calc(100% - 35px)",margin:"0px 5px 5px 5px",borderRadius:"3px",overflow:"hidden"});
	var content = D.newEl("div",{},{width:"100%",height:"70%",backgroundColor:"#fff",display:"flex"});
	var textarea = D.newEl("div",{},{flex:"1"});
	textarea.appendChild(D.newEl("textarea",{id:"simditor"}));
	var toolBar = D.newEl("div",{},{flex:"0 0 34px",backgroundColor:"rgb(230, 228, 228)",boxShadow:"0 0 5px black",marginLeft:"2px"});
	initToolBar(toolBar);
	content.appendChild(textarea);
	content.appendChild(toolBar);
	var key = D.newEl("div",{},{width:"100%",height:"30%",display:"flex",flexDirection:"column",backgroundColor:"white"});
	initKey(key);
	sectionContainer.appendChild(content);
	sectionContainer.appendChild(key);
	
	body.appendChild(div);
	body.appendChild(sectionContainer);
	$("#main").append(editor);
	simditor = new Simditor({
        textarea: $("#simditor"),
        toolbar:['underline','strikethrough','alignment','bold','italic','blockquote','eraser']
    });
	
	var minWin = D.newEl("div",{id:"editor-min",className:"min-item"},{backgroundImage:"url('image/min/download-32.png')",backgroundColor:"rgb(88, 133, 113)"});
	minWin.onclick = function(){
		if(editor.style.display=="none"){
			editor.style.display = "flex";
		}else{
			editor.style.display = "none";
		}
	}
	$("#footer").append(minWin);
	return editor;
}

//初始化工具栏（归属于编辑器）
function initToolBar(t){
	// 保存
	t.appendChild(D.elToolItem("保存","save-24",function(){
		var html = simditor.getValue();
		$.post(
			"a/atsSection/saveContent",
			{"id":D.curSect.id,"html":simditor.getValue()},
			function(data){
				if(data.flag){
					alert(data.msg);
					simditor.setValue(data.html);
				}
			},
			"json"
		);
	}));
	
	//提交
	t.appendChild(D.elToolItem("提交","confirm-24",function(){
		var id = D.curSect.pid;
		var treeObj = $.fn.zTree.getZTreeObj("sectionList");
		var node = treeObj.getNodeByParam("id", id, null);
		var n = 0;
		for(var i =0;i<node.children.length;i++){
			var sec = node.children[i];
			if(sec.status==2){
				n++;
			}
		}
		var isSubmitAll = n>=node.children.length-1?true:false; 
		var url = "a/atsSection/submitSection";
		if(D.curSect.status==3){
			url = "a/atsSection/submitFeedback"
		}
		
		$.post(
			url,
			{id:D.curSect.id,flag:isSubmitAll},
			function(id){
				var treeObj = $.fn.zTree.getZTreeObj("sectionList");
				var n = treeObj.getNodesByParam("id",id)[0];
				if(n){
					n.status = "2";
					treeObj.updateNode(n);
				}
			}
		);
	}));
	
	//快照
	t.appendChild(D.elToolItem("快照","snapshot-24",function(){
		viewSnapshot(D.curSect.caption,D.curSect.pid);
	}));
	t.appendChild(D.newEl("hr"));
	//新增节点
	t.appendChild(D.elToolItem("新增","create-24",function(){
		if(confirm("确认要新增节点吗？")){
			$.post(
				"a/atsAct/createNewSection",
				{"id":D.curSect.pid},
				function(json){
					var treeObj = $.fn.zTree.getZTreeObj("sectionList");
					var parentNode = treeObj.getNodeByParam("id",json.pid);
					treeObj.addNodes(parentNode,json);
					$.post(
						"a/atsSection/findSectionById",
						{id:json.id},
						function(ret){
							initEditPage(ret);
						},
						"json"
					);
					
				},
				"json"
			);
		}
	}));
	//删除节点
	t.appendChild(D.elToolItem("删除","delete-24",function(){
		var treeObj = $.fn.zTree.getZTreeObj("sectionList");
		var nodes = treeObj.getCheckedNodes(true);
		var ids = [];
		if(nodes.length && confirm("确认要删除选中的节点吗？")){
			for(var i=0;i<nodes.length;i++){
				ids.push(nodes[i].id);
				treeObj.removeNode(nodes[i]);
			}
			ids.join(",");
		}else{
    		if(confirm("确认要删除此节点吗？")){
    			ids.push(D.curSect.id);
    			treeObj.removeNode(treeObj.getNodeByParam("id",D.curSect.id));
    		}
		}
		$.post(
			"a/atsSection/removeSection",
			{"ids":ids.join(",")}
		);
	}));
	
	t.appendChild(D.newEl("hr"));
	
	// 批量修改关键信息
	t.appendChild(D.elToolItem("批量修改关键信息","key-24",function(){
		var popWin = D.newCommonWin("keyinfro",{"w":"600px","h":"90px","t":"250px","l":"600px"});
		popWin.setTitleName("Key Information");
		var type = D.newEl("select",{},{"margin":"10px 0 10px 10px"});
		var opt1 = D.newEl("option",{"innerHTML":"EffectiveDate","value":"eff"});
		var opt2 = D.newEl("option",{"innerHTML":"SourceNoteLink","value":"link"});
		var opt3 = D.newEl("option",{"innerHTML":"ExpirationDate","value":"exp"});
		type.appendChild(opt1);
		type.appendChild(opt2);
		type.appendChild(opt3);
		popWin.body.appendChild(type);
		var input = D.newEl("input",{},{"margin":"10px 0","width":"420px"});
		popWin.body.appendChild(type);
		popWin.body.appendChild(input);
		var btn = D.newEl("div",{"innerText":"Ok","className":"btn"});
		btn.onclick = function(){
			$.post(
					"a/atsSection/modifyKeyInforBatch",
					{"pid":D.curSect.pid,"key":type.value,"value":input.value},
					function(d){
						alert(d);
					}
				);
		    	popWin.parentElement.removeChild(popWin);
		    	D.byId(type.value).value = input.value;
		}
		popWin.body.appendChild(btn);
		D.byId("main").appendChild(popWin);
		D.topPanel(D.byId("keyinfro"));
	}));
	//批量Repeal
	t.appendChild(D.elToolItem("批量Repeal","repeal-24",function(){
		var popWin = D.newCommonWin("keyinfro",{"w":"600px","h":"90px","t":"250px","l":"600px"});
		popWin.setTitleName("Key Information");
		var label = D.newEl("div",{"innerText":"Captions"},{
			display: "flex",
    	    justifyContent: "center",
    	    alignItems: "center",
    	    backgroundColor: "white",
    	    width: "105px",
    	    margin: "10px 0 10px 10px",
    	    padding: "0 5px",
    	    border: "1px solid #ccc"
		});
		popWin.body.appendChild(label);
		var input = D.newEl("input",{},{"margin":"10px 0","width":"420px"});
		popWin.body.appendChild(input);
		var btn = D.newEl("div",{"innerText":"Ok","className":"btn"});
		btn.onclick = function(){
			$.post(
					"a/atsSection/repealBath",
					{"pid":D.curSect.pid,"value":input.value},
					function(array){
						var treeObj = $.fn.zTree.getZTreeObj("sectionList");
    					treeObj.addNodes(treeObj.getNodeByParam("id",D.curSect.pid),array);
					},
					"json"
				);
		    	popWin.parentElement.removeChild(popWin);
		}
		popWin.body.appendChild(btn);
		D.byId("main").appendChild(popWin);
		D.topPanel(D.byId("keyinfro"));
	}));
	t.appendChild(D.newEl("hr"));
	// 加链接
	t.appendChild(D.elToolItem("加链接","link-24",function(){
		
	}));
	// 日期查询
	t.appendChild(D.elToolItem("日期查询","history-24",function(){
		var historyWin = D.newCommonWin("historyWin");
		historyWin.setTitleName("Bill History");
		D.byId("main").appendChild(historyWin);
		D.topPanel(D.byId("historyWin"));
		D.ajax(
			"a/atsAct/viewBillHistory",
			{"id":D.curSect.pid},
			function(ret){
				var div = D.newEl("div",{},{"flex":"1","margin":"0 5px 5px 5px","overflow":"auto","backgroundColor":"white","borderRadius":"3px"});
				div.innerHTML = ret.html;
				historyWin.body.appendChild(div);
			}
		);
	}));
	// 原始版本
	t.appendChild(D.elToolItem("原始版本","original statute-24",function(){
		var originalWin = D.newCommonWin("originalWin");
		originalWin.setTitleName("Original History");
		D.byId("main").appendChild(originalWin);
		D.topPanel(D.byId("originalWin"));
		originalWin.body.style.backgroundColor = "#fff";
		originalWin.body.style.overflow = "auto";
		originalWin.body.style.padding = "5px";
		originalWin.body.style.justifyContent = "center";
		originalWin.body.style.alignItems = "center";
		originalWin.body.innerHTML = "<img width='32' height='32' src = 'image/common/waiting.jpg' />"
		$.post(
			"a/atsStatute/viewOriginalSection",
			{"shortName":D.curSect.shortName,"state":D.curSect.state},
			function(ret){
				originalWin.body.style.backgroundColor = '#fff';
				originalWin.body.style.justifyContent = "";
	    		originalWin.body.style.alignItems = "";
				originalWin.body.innerHTML = ret;
			}
		);
	}));
}

//初始化关键信息（归属于编辑器）
function initKey(p){
	p.appendChild(D.newKeyItem("caption"));
    p.appendChild(D.newKeyItem("description"));
    p.appendChild(D.newKeyItem("shortName"));
    p.appendChild(D.newKeyItem("eff"));
    p.appendChild(D.newKeyItem("link"));
    p.appendChild(D.newKeyItem("exp"));
    p.appendChild(D.newKeyItem("updateType"));
}

//显示Section（归属于树状图）
function showSection(ev,treeId,treeNode){
	if(treeNode.level==2){
		$.post(
			"a/atsSection/findSectionById",
			{id:treeNode.id},
			function(ret){
				initEditPage(ret);
			},
			"json"
		);
	}
}

function initEditPage(ret){
	document.getElementsByClassName('simditor-body')[0].scrollTop = 0;
	D.curSect = ret;
	simditor.setValue(ret.html);
	for(var name in ret){
		if(D.byId(name)){
			D.byId(name).value = ret[name];
		}
	}
}

//动态提交上传请求
function submitUploadRequest(e){
	$(e.target.parentElement).ajaxSubmit({
		success:function(data){
			$.fn.zTree.init($("#sectionList"), setting, JSON.parse(data));
			document.body.removeChild(D.byId("uploadForm"));
		}
	});
}

// 单机叶子节点，改变Title
function resetTitle(ev,treeId,treeNode){
	if(treeNode.level==1){
		$("#editor>.common-title>.common-title-name")[0].innerHTML = "My Acts >> ID "+treeNode.id;
	}else if(treeNode.level==2){
		$("#editor>.common-title>.common-title-name")[0].innerHTML = "My Acts >> ID "+treeNode.pid +" >> SID "+treeNode.id;
	}
}