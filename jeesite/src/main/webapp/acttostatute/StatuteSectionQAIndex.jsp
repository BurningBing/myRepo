<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			
		});
		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
		function searchForm()
		{
			$("#searchForm").submit();
		}
		function sumitForm()
		{
			$("#remarkForm").submit();
		}
		function matchingIndex()
		{
			loading('正在提交，请稍等...');
			window.location="${ctx}/acttostatute/nonCaseStatuteSection/matchingIndex";
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/acttostatute/nonCaseStatuteSection/">信息列表</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/findQA2List" method="post" class="breadcrumb form-search">
		<label>ShortName:&nbsp;</label><form:input path="shortName" htmlEscape="false" maxlength="100" class="input-xlarge"/>
		<label>DocumentEidtion:&nbsp;</label>
		<form:select path="state" class="input-xlarge"> 
			<form:options items="${eidtionMap}"/>  
		</form:select> 
		&nbsp;&nbsp;<input id="btnSubmit" class="btn btn-primary" type="submit" value="Search"/>
	</form:form>
	<sys:message content="${message}"/>
	<form:form id="remarkForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/remark" method="post" class="breadcrumb form-search">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			    <th>ShortName</th>
			    <th>Relevance</th>
			    <th>LibraryDocument</th>
			    <th>Operation</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${libraryList}" var="libraryDocumentListItemIObject">
			<tr>
				<td><a href="${ctx}/acttostatute/nonCaseStatuteSection/showStatuteSection?id=${nonCaseStatuteSection.id}&&libraryDocumentID=${libraryDocumentListItemIObject.libraryDocumentID}">
					${libraryDocumentListItemIObject.citation}</a>
				</td>
				<td nowrap='nowrap'> 
					${libraryDocumentListItemIObject.relevanceString}
				</td>
				<td nowrap='nowrap'> 
 					${libraryDocumentListItemIObject.libraryDocumentEidtion}  
				</td>	
				<td><a href="${ctx}/acttostatute/nonCaseStatuteSection/showStatuteSectionVersion?id=${nonCaseStatuteSection.id}&&libraryDocumentID=${libraryDocumentListItemIObject.libraryDocumentID}">
					Show Version</a>
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</form:form>
</body>
</html>