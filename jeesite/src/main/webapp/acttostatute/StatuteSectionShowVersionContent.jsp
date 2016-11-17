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
	
	function sumitForm()
	{
		$("#searchForm").submit();
	}
</script>
</head>
<body>
		<div style="color:red">${message}</div>
	    <b> <span><a class='btn btn-primary btn-small'  href="javascript:sumitForm()">Remove</a><a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;&nbsp;Return&nbsp;&nbsp;</a></b></span>
		<div>
		<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/removeVersion" method="post" class="breadcrumb form-search">
		<form:hidden path="actNumber"/>
		<c:forEach items="${libraryDocuments}" var="libraryDocumentUpdate">
		<div style="color:blue">
		<c:if test="${libraryDocumentUpdate.updateOrder ne '1'}">
			<form:checkbox path="ids" value="${libraryDocumentUpdate.libraryDocumentUpdateId}"/>
		</c:if>
			<b><span>UpdateId: ${libraryDocumentUpdate.libraryDocumentUpdateId}</span></b><br>
			<b><span>UpdateOrder: ${libraryDocumentUpdate.updateOrder}</span></b><br>
			<b><span>Caption: ${libraryDocumentUpdate.indexCaption}</b></span><br>
			<b><span>Description: ${libraryDocumentUpdate.indexDescription}</b></span><br>
	    </div>
		<div agin="center">${libraryDocumentUpdate.libraryDocumentUpdateHtml}</div>
		</div>
		<span><hr></span><br>
		</c:forEach>
		  <form:select path="libraryName" items="${nonCaseUsEditionList}" itemLabel="libraryEdition" itemValue="libraryEdition" class="input-xxlarge"></form:select><hr> 
		</form:form>
		<b> <span><a class='btn btn-primary btn-small'  href="javascript:sumitForm()">Remove</a><a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;Return&nbsp;</a></b></span>
</body>

</html>