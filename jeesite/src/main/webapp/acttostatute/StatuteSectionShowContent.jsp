<%@ page contentType="text/html;charset=UTF-8"%>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
<title>信息管理</title>
<meta name="decorator" content="default" />
<script type="text/javascript">
	$(document).ready(function() {

	});
	function page(n, s) {
		$("#pageNo").val(n);
		$("#pageSize").val(s);
		$("#searchForm").submit();
		return false;
	}
</script>
</head>
<body>
		<div style="color:red">${message}</div>
	    <b> <span><a class='btn btn-primary btn-small'  href="${ctx}/acttostatute/nonCaseStatuteSection/confirmValidate?id=${nonCaseStatuteSection.id}">Confirm</a><a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;&nbsp;Return&nbsp;&nbsp;</a></b></span>
		<div>
		<div style="color:blue">
			<b><span>ParentMessage: ${libraryDocumentParent.indexCaption}  ${libraryDocumentParent.indexDescription}</span></b><br>
<%-- 			<b><span>ParentDescription: ${libraryDocumentParent.indexDescription}</span></b><br> --%>
			<%-- //由于属性缺失先用RevisionHistory代替ActNumber --%>
			<b><span>ActNumber: ${libraryDocument.revisionHistory}</span></b><br>
			<b><span>Description: ${libraryDocument.indexDescription}</b></span><br>
			<b><span>ShortName: ${libraryDocument.shortName}</b></span><br>
	    </div>
		<div agin="center">${libraryDocument.libraryDocumentHtml}</div>
		</div>
		<b> <span><a class='btn btn-primary btn-small'  href="${ctx}/acttostatute/nonCaseStatuteSection/confirmValidate?id=${nonCaseStatuteSection.id}">Confirm</a><a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;Return&nbsp;</a></b></span>
</body>

</html>