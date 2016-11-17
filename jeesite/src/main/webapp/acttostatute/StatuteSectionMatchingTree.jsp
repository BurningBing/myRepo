<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>模板列表</title>
<meta name="decorator" content="default" />
<%@include file="/WEB-INF/views/include/treeview.jsp"%>
<style type="text/css">
.ztree {
	overflow: auto;
	margin: 0;
	_margin-top: 10px;
	padding: 10px 0 0 0;
}

<%--
.ztree li span.button.level0, .ztree li a.level0 {
	display: none;
	height: 0;
}

.ztree li ul.level0 {
	padding: 0;
	background: none;
}

--%>
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
			url : "${ctx}/acttostatute/nonCaseStatuteSection/getStatuteTree",
			autoParam : [ "id" ],
			otherParam : {
				"otherParam" : "zTreeAsyncTest"
			},
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
		return childNodes;
	}
	function beforeClick(treeId, treeNode) {
		if (!treeNode.isParent) {
			alert(treeNode.id);
			var url="${ctx}/acttostatute/nonCaseStatuteSection/showStatuteSection?libraryDocumentID="+treeNode.id;
			window.open(url,"abc","") 
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
		for (var i=0, l=nodes.length; i<l; i++) {
// 			refreshNodes(nodes[i]);
// 			treeObj.reAsyncChildNodes(nodes[i], "refresh", true);
			var nodes1 = nodes[i].children;
			for(var i1=0, l1=nodes1.length; i1<l1; i1++)
			{
				treeObj.reAsyncChildNodes(nodes1[i], "refresh", true);
			}
		}
	}
	function test() {
		var treeObj = $.fn.zTree.getZTreeObj("treeRoot");
		var nodes = treeObj.getNodes();
		for (var i=0, l=nodes.length; i<l; i++) {
// 			refreshNodes(nodes[i]);
			treeObj.reAsyncChildNodes(nodes[i], "refresh", true);
		}
// 		treeObj.expandAll(true);
// // 		var node = treeObj.getNodeByParam("id", 35683817, null);
// // 		treeObj.expandNode(node, true, true, true);
	}
	function refreshNodes(node)
	{
		var nodes = node.getNodes();
		for (var i=0, l=nodes.length; i<l; i++) {
			treeObj.reAsyncChildNodes(nodes[i], "refresh", true);
			if (!true) treeObj.selectNode(nodes[i]);
			if(treeNode.isParent)
			{
				refreshNodes(nodes[i]);
			}
		}
	}
	$(document).ready(function() {
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
</script>
</head>
<body>
	<div>			<a class='btn btn-primary btn-small'  href="javascript:test()">ShowSectionsTree</a><a class='btn btn-primary btn-small'  href="javascript:test1()">ShowSectionsTree1</a>
	</div>
	<div class="accordion-group">
		<div class="accordion-heading">
			<a class="accordion-toggle">Statutes Tree</a>
		</div>
		<div class="accordion-body">
			<div class="accordion-inner">
				<div id="treeRoot" class="ztree"></div>
			</div>
		</div>
	</div>
</body>
</html>