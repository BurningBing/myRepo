function viewAct(node){
	var array = initViewActPage(node);
	$.get(
			"/jeesite/a/atsAct/getActById",
			{sid:node.fid},
			function(ret){
				array[0].src = ret.url;
				if(ret.status == 1){
					array[3].children[1].innerText = "Signable";
				}else{
					$.get(
							"/jeesite/a/data/getEditor",
							{actId:ret.id},
							function(editors){
								array[3].children[2].innerText = editors;
							}
					);
					if(ret.status == 2){
						array[3].children[1].innerText = "Editing";
					}else if(ret.status == 3){
						array[3].children[1].innerText = "Feedback";
					}else if(ret.status == 4){
						array[3].children[1].innerText = "Pass";
					}
				}
			},
			"json"
	);
}

function initViewActPage(node){
	var array = [];
	var container = document.querySelector(".container");
	var div = ats.createElement({ name: "div", attrs:{index : node.name, node:node} ,style:{display:"flex",flex:"1",flexFlow:"column",backgroundColor:"#fff"}}, container);
	// 顶部
	var top = ats.createElement({name: "div", style:{flex:"0 0 100px",display:"flex",borderTop:"1px solid #ccc"} },div);
		//顶部左侧
		var left = ats.createElement({name:"div",style:{flex:"1",background:"#fff",display:"flex"}},top);
			var label = ats.createElement({name:"div",style:{flex:"0 0 123px",display:"flex",flexFlow:"column"},
				children:[
					{name:"div",style:{flex:"1",lineHeight:"33px",paddingLeft:"10px",borderRight:"1px solid #ccc",borderBottom:"1px solid #ccc"},	attrs:{innerHTML:"<b>Bill Number</b>"	}},
					{name:"div",style:{flex:"1",lineHeight:"33px",paddingLeft:"10px",borderRight:"1px solid #ccc",borderBottom:"1px solid #ccc"},attrs:{innerHTML:"<b>Status</b>"}},
					{name:"div",style:{flex:"1",lineHeight:"33px",paddingLeft:"10px",borderRight:"1px solid #ccc"},attrs:{innerHTML:"<b>Signer</b>"}}
				]
			},left);
			var text = ats.createElement({name:"div",style:{flex:"1",display:"flex",flexFlow:"column"},
				children:[
					{name:"div",style:{flex:"1",lineHeight:"33px",paddingLeft:"10px",borderBottom:"1px solid #ccc"},attrs:{innerText:node.name}},
					{name:"div",style:{flex:"1",lineHeight:"33px",paddingLeft:"10px",borderBottom:"1px solid #ccc"},attrs:{id:"status_all"}},
					{name:"div",style:{flex:"1",lineHeight:"33px",paddingLeft:"10px"}}
				]
			},left);
		//顶部右侧
		var right = ats.createElement({name:"div",style:{ flex:"1",background:"#fff",borderLeft:"1px solid #ccc",display:"flex", flexFlow:"column"}},top);
			var searchBar = ats.createElement({name: "div", style: {flex:"1",display:"flex",justifyContent:"center",alignItems:"center"}},right);
			ats.createElement([{name:"input", attrs:{placeholder:"Please input the bill number."},style:{marginLeft:"10px",flex:"0.8"}},{name:"button",attrs:{innerText:"Search"},style:{marginRight:"10px"}}],searchBar);
			var buttonGroup = ats.createElement({name:"div",style:{display:"flex", flex:"1",borderTop:"1px solid #ccc"},attrs:{actId:node.fid}},right);
			ats.createElement({name:"div",style:{flex:"1",display:"flex",justifyContent:"center",alignItems:"center"},
				children:[
					{name:"button",attrs:{innerText:"Delete",onclick:deleteAct}}
				]
			},buttonGroup);
			ats.createElement({name:"div",style:{flex:"1",display:"flex",justifyContent:"center",alignItems:"center"},
				children:[
					{name:"button",attrs:{innerText:"Rollback"}}
				]
			},buttonGroup);
			ats.createElement({	name:"div",	style:{flex:"1",display:"flex",justifyContent:"center",alignItems:"center"},
				children:[
					{name:"button",attrs:{innerText:"Upload"}}
				]
			},buttonGroup);
			ats.createElement({	name:"div",	style:{flex:"1",display:"flex",justifyContent:"center",alignItems:"center"},
				children:[
					{name:"button",attrs:{innerText:"Download"}}
				]
			},buttonGroup);
	//底部
	var bottom = ats.createElement({name:"div", style:{flex:"1",display:"flex"}},div);
	var iframe = ats.createElement({name:"iframe",style:{flex:"1"},attrs:{src:"/jeesite/load.html"}},bottom);
	array.push(iframe);
	array.push(buttonGroup);
	array.push(searchBar);
	array.push(text);
	return array;
}

function deleteAct(ev){
	var target = ev.target;
	var id = target.parentElement.parentElement.actId;
	alert(id);
	$.post(
		"/jeesite/a/atsAct/deleteAct",
		{id:id},
		function(ret){
			var node = tree.getNodeByParam("id",ret);
			tree.reAsyncChildNodes(node.getParentNode(),"refresh");
			alert("Remove Success!");
		}
	);
	
	
}

function statuteTree(treeNode,div){
	$.get(
		"/jeesite/a/data/getStatuteTreeRoot",
		function(nodes){
			initStatuteTree(nodes,div);
		},
		"json"
	);
}

function initStatuteTree(nodes, div){
	ats.createElement({
		name:"div",
		attrs:{
			id:"statute-tree",
			className:"ztree"
		},
		style:{
			flex:"0 0 120px;",
			borderRight:"3px solid #ccc",
			borderTop:"1px solid #ccc",
			boxShadow:"2px 0 15px #9d9d9d",
			overflow:"auto"
		}
	
	},div);
	ats.createElement({
		name:"div",
		style:{
			flex:"1",
			padding:"10px 20px",
			color:"#5a5a5a",
			overflow:"auto"
		},
		attrs:{
			id:"section-content"
		}
	},div);
	
	
	var setting = {
		async:{
			enable: true,
			url:"/jeesite/a/data/findStatuteTreeData",
			autoParam:["id","editionId"],
			dataFilter: dataFilter
		},
		data:{
			simpleData:{
				enable:true
			}
		},
		callback:{
			beforeAsync:beforeAsync,
			onDblClick:onDblClick
		},
		view:{
			nameIsHTML:true,
			fontCss:sefFontCss
		}
	}
	statuteTree = $.fn.zTree.init($("#statute-tree"), setting, nodes);
}

function beforeAsync(treeId, treeNode){
	if(!treeNode.editionId){
		treeNode.editionId = treeNode.getParentNode().editionId;
	}
	return true;
}

function onDblClick(event, treeId, treeNode){
	if(!treeNode.isParent){
		$.get(
			"/jeesite/a/data/viewSection",
			{id:treeNode.id},
			function(ret){
				document.getElementById("section-content").innerHTML = ret.libraryDocumentHtml;
			},
			"json"
		)
	}
}

function dataFilter(treeId, parentNode, resp){
	for(var i=0;i<resp.length;i++){
		resp[i].id = resp[i].nodeID;
		resp[i].name = resp[i].nodeDescription;
		resp[i].isParent = resp[i].hasChildNodes;
	}
	return resp
}


