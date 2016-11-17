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
		function sumitForm()
		{
			$("#searchForm").submit();
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/acttostatute/nonCaseStatuteSection/">信息列表</a></li>
	</ul>
	<sys:message content="${message}"/>
	<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/showSection" method="post" class="breadcrumb form-search">
	<div align="center">	
		<div align="center" class='btn-group'>	
			<a class='btn btn-primary btn-small'  href="javascript:sumitForm()">ShowSections</a>
			<a class='btn btn-primary btn-small'  href="javascript:history.go(-1)">&nbsp;&nbsp;ReturnList&nbsp;&nbsp;</a>
		</div>
	</div>
	<table id="contentTable" class="table  table-bordered table-condensed" width="200">
		<thead>
			<tr>
			     <th>选择</th>
			    <th>State</th>
			    <th>ShortName</th>
			    <th>Relevance</th> 
 			    <th>UpdateType</th>  
			    <th>ActNumber</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${matchingList}" var="nonCaseStatuteSection">
			<c:if test="${nonCaseStatuteSection.relevance eq '100%'}">
				<tr class="info">
			</c:if>
			<c:if test="${nonCaseStatuteSection.relevance ne '100%'}">
				<tr>
			</c:if>
			    <td nowrap='nowrap' width="10">
			    <form:checkbox path="ids" value="${nonCaseStatuteSection.id}"/>
			    </td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.state}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.shortName}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.relevance}
				</td> 
 				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.updateType}
				</td> 
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.actNumber}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</form:form>
</body>

</html>