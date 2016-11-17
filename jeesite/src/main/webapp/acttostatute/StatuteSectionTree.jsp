<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>模板列表</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/treeview.jsp"%>
<style type="text/css">
.ztree {
	margin: 0;
	margin-top: 10px;
	padding: 10px 0 0 0;
}

.accordion-inner {
	padding: 2px;
}
</style>
<script type="text/javascript">
	
	var setting = {
		view : {
			selectedMulti : false
		},
		async : {
			enable : true,
			url : "${ctx}/acttostatute/nonCaseStatuteSection/getMyStatuteTree",
			autoParam : ["id"],
			otherParam: { "initFlag":"0"},
			dataFilter : filter
		},
		callback : {
			beforeClick : beforeClick,
			beforeAsync : beforeAsync,
			onAsyncError : onAsyncError,
			onAsyncSuccess : onAsyncSuccess
		}
	};
	function filter(treeId, parentNode, childNodes) {
		if (!childNodes)
			return null;
		for (var i = 0, l = childNodes.length; i < l; i++) {
			childNodes[i].name = childNodes[i].nodeDescription;
			childNodes[i].id = childNodes[i].nodeID;
			childNodes[i].pId = childNodes[i].parentNodeID;
			childNodes[i].isParent = childNodes[i].hasChildNodes;
		}
		closeTip();
		return childNodes;
	}
	function beforeClick(treeId, treeNode) {
		if (!treeNode.isParent) {
			var url = "${ctx}/acttostatute/nonCaseStatuteSection/showStatuteSection?libraryDocumentID="
					+ treeNode.id;
			loading('正在查询，请稍等...');
			showSection(treeNode.id);
			return false;
		} else {
			return true;
		}
	}
	var log, className = "dark";
	function beforeAsync(treeId, treeNode) {
		className = (className === "dark" ? "" : "dark");
		return true;
	}
	function onAsyncError(event, treeId, treeNode, XMLHttpRequest, textStatus,
			errorThrown) {
	}
	function onAsyncSuccess(event, treeId, treeNode, msg) {
	}

	function test1() {
		var treeObj = $.fn.zTree.getZTreeObj("treeRoot");
		var nodes = treeObj.getNodes();
		for (var i = 0, l = nodes.length; i < l; i++) {
			// 			refreshNodes(nodes[i]);
			// 			treeObj.reAsyncChildNodes(nodes[i], "refresh", true);
			var nodes1 = nodes[i].children;
			for (var i1 = 0, l1 = nodes1.length; i1 < l1; i1++) {
				treeObj.reAsyncChildNodes(nodes1[i], "refresh", true);
			}
		}
	}
	function test() {
		var treeObj = $.fn.zTree.getZTreeObj("treeRoot");
		var nodes = treeObj.getNodes();
		for (var i = 0, l = nodes.length; i < l; i++) {
			// 			refreshNodes(nodes[i]);
			treeObj.reAsyncChildNodes(nodes[i], "refresh", true);
		}
		// 		treeObj.expandAll(true);
		// // 		var node = treeObj.getNodeByParam("id", 35683817, null);
		// // 		treeObj.expandNode(node, true, true, true);
	}
	function refreshNodes(node) {
		var nodes = node.getNodes();
		for (var i = 0, l = nodes.length; i < l; i++) {
			treeObj.reAsyncChildNodes(nodes[i], "refresh", true);
			if (!true)
				treeObj.selectNode(nodes[i]);
			if (treeNode.isParent) {
				refreshNodes(nodes[i]);
			}
		}
	}
	$(document).ready(function() {
		loading('正在加载，请稍等...');
		$.fn.zTree.init($("#treeRoot"), setting);
	})
	$(window).resize(function() {
		wSize();
	});
	function wSize() {
		$(".ztree").width($(window).width() - 16).height(
				$(window).height() - 62);
		$(".ztree").css({
			"overflow" : "auto",
			"overflow-x" : "auto",
			"overflow-y" : "auto"
		});
		$("html,body").css({
			"overflow" : "hidden",
			"overflow-x" : "hidden",
			"overflow-y" : "hidden"
		});
	}
	function showSection(id)
	{
		$.post(
				"showMyStatuteSectionByJson",
				{"libraryDocumentID":id},
				function(data){
					if(data['libraryDocumentHtml']=="")
					{
						$('#sectionContent').html('正文內容为空！');
					}
					else
					{
						$('#sectionContent').html(data['libraryDocumentHtml']);
					}
					closeTip();
				},
				"json"
		);
	}
	function loadSatute()
	{
		loading('正在加载，请稍等...');
		var editionId=$('#state').val();
		setting.async.otherParam =  { "editionId":editionId,"initFlag":"1"};
		$.fn.zTree.init($("#treeRoot"), setting);
		return false;
	}
</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/acttostatute/nonCaseStatuteSection/">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="nonCaseStatuteSection"  method="post" class="breadcrumb form-search">
		<label>StatuteEidtion:&nbsp;</label>
		<form:select path="state" class="input-xxlarge"> 
			<form:options items="${eidtionMap}"/>  
		</form:select> 
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="button" value="Load Statute" onclick="loadSatute();"/>
	</form:form>
	  <div style="width:100%;height:85%;position:absolute;">
		<div style="width:30%;height:100%;position:absolute;overflow: auto;">
			<div id="treeRoot" class="ztree"></div>
		</div>
		<div style="width:68%;left:31.5%;position:relative;">
			<div id="sectionContent" class="well" ></div>
		</div>
	</div>  
</body>
</html>