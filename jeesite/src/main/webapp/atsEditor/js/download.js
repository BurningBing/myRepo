// 显示下载页面
function showDownloadWin(treeNode,div){
	$.get(
		"/jeesite/a/data/getDownloadTasks",
		function(ret){
			initDownloadPage(ret, div);
		},
		"json"
	);
}

// 初始化下载页面
function initDownloadPage(ret, div){
	div.style.flexFlow = "column";
	var list = ats.createElement({ name:"div", style:{backgroundColor:"#ccc"} },div);
	for(var i=0;i<ret.length;i+=6){
		var row = ats.createElement({name:"div",attrs:{className:"state-row"} } , list);
		for(var j=i;j<i+6;j++){
			var stateName = ""; 
			if(ret[j]){
				stateName = ret[j].state;
			}
			var state = ats.createElement({name:"div",attrs:{className:"task-item",innerHTML : stateName}},row);
			if(ret[j]){
				state.task = ret[j]; 
			}
			state.onclick = doCheckUpdates;
		}
	}
	var infor = ats.createElement({name:"div",attrs:{className:"download-details"}	},div);
}

// 执行更新检查
function doCheckUpdates(){
	var task = this.task;
	var container = document.querySelector(".download-details");
	var links = task.url.split(",");
	var left = ats.createElement({name:"div",style:{	flex:"1",	backgroundColor:"#345",	border : "1px solid #c7c7c7"}},container);
	ats.createElement([{name:"div",attrs:{innerText:"State : "+task.state,className:"state-infor"}},{name:"div",attrs:{innerText:"ShortName : "+task.shortName+"+ Caption",className:"state-infor"}}	],left);
	ats.forEachArray(links, function(item,index){
		ats.createElement({name:"div",children:[{	name:"a",	attrs:{	href:item,	target:"_blank",	innerText: "View "+(index+1)}}],className:"state-infor",style:{	paddingLeft: "10px"}},left);
	});
	ats.createElement({name:"hr",style:{	margin:"30px 0"}},left);
	$.get(
			"/jeesite/a/data/getTaskInfor",
			{state:task.state},
			function(ret){
				ats.createElement({	name:"div",	attrs:{	innerText:"Total Count : "+ret.totalCount,className:"state-infor"}	},left);
				var checkBtn = ats.createElement({name:"div",attrs:{className:"state-infor"},	children:[{ name:"button", attrs:{ innerText: "Check Updates"}}]},left);
				checkBtn.onclick = function(){
					// 执行下载
					$.post(
							"/jeesite/a/download/checkUpdates",
							{
								state : task.state
							}
					);
				}
			},
			"json"
	);
	var right = ats.createElement({name:"div",attrs:{id:"checkLog"},style:{flex:"1",backgroundColor:"#000",color:"rgb(179, 179, 179)",padding:"5px",fontSize: "14px",overflow:"auto"}	},container);
}

// 检查更新日志打印
function checkUpdateLog(infor){
	var  container = document.getElementById("checkLog");
	ats.createElement({
		name:"div",
		attrs:{
			innerText:infor.data
		}
	},container);
	container.scrollTop = container.scrollHeight;
}
