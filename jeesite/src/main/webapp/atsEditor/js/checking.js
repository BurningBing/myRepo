function showCheckingPage(treeNode){
	initCheckingPage(treeNode);
	var table = document.querySelector("#checking-table");
	$.get(
		"/jeesite/a/data/getCheckingData",
		function (ret) {
			
		},
		"json"
	);
}

function initCheckingPage (treeNode) {
	var container = document.querySelector(".container");
	var div = ats.createElement({
		name:"div",
		attrs:{index:treeNode.name},
		style:{display:"flex",backgroundColor:"#649",flex:"1"}
	},container);
	var table = ats.createElement({
		name:"table",
		attrs:{
			id:"checking-table",
			border:"1",
			width:"100%",
			cellspacing:"0",
			cellpadding:"5px 10px"
		},
		children:[
			{
				name:"tr",
				children:[
					{
						name:"th",
						attrs:{
							innerText:"#"
						}
					},
					{
						name:"th",
						attrs:{
							innerText:"State"
						}
					},
					{
						name:"th",
						attrs:{
							innerText:"Bill Number"
						}
					},
					{
						name:"th",
						attrs:{
							innerText:"Caption"
						}
					},
					{
						name:"th",
						attrs:{
							innerText:"File Type"
						}
					},
					{
						name:"th",
						attrs:{
							innerText:"Editor"
						}
					},
					{
						name:"th",
						attrs:{
							innerText:""
						}
					}
				]
			},
			{
				name:"tbody"
			}
		]
	},div);
	
}