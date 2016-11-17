<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>NonCaseUSEdition管理</title>
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
		function mark(self,id,flag){
			$.post(
				"mark",
				{"id":id,"status":flag},
				function(data)
				{
				},
				"json"
			)
			if(flag==1)
				{self.parentElement.parentElement.children[3].innerText = 'Active';}
			else
				{self.parentElement.parentElement.children[3].innerText = 'DeActive';}
		}
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li class="active"><a href="${ctx}/acttostatute/nonCaseUsEdition/">NonCaseUSEdition列表</a></li>
		<shiro:hasPermission name="acttostatute:nonCaseUsEdition:edit"><li><a href="${ctx}/acttostatute/nonCaseUsEdition/form">NonCaseUSEdition添加</a></li></shiro:hasPermission>
	</ul>
	<form:form id="searchForm" modelAttribute="nonCaseUsEdition" action="${ctx}/acttostatute/nonCaseUsEdition/" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
		<div align="center">
		  		<div align="right" class="btn-group">
			  		<a class='btn btn-success btn-small'  href="${ctx}/acttostatute/nonCaseUsEdition/synchrodata">Synchrodata</a>
		  		</div>&nbsp;&nbsp;&nbsp;
	  	</div>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>	
				<td>Library Name</td>
				<td>Library State</td>
				<td>Library Edition</td>
				<td>Status</td>
				<td>LibrarySourceConst</td>
				<td>LibraryEditionID</td>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="nonCaseUsEdition">
			<tr>
				<td>${nonCaseUsEdition.libraryName}</td>
				<td>${nonCaseUsEdition.state}</td>
 				<td><a href="${ctx}/acttostatute/nonCaseUsEdition/form?id=${nonCaseUsEdition.id}">${nonCaseUsEdition.libraryEdition}</a></td> 
				 <c:if test="${nonCaseUsEdition.status eq '0'}">
					<td> <a href="javascript:; " onclick="mark(this,${nonCaseUsEdition.id },'1')">DeActive</a></td>
				</c:if>
				 <c:if test="${nonCaseUsEdition.status eq '1'}">
					<td> <a href="javascript:; " onclick="mark(this,${nonCaseUsEdition.id },'0')">Active</a></td>
				</c:if>
				<td>${nonCaseUsEdition.librarySourceConst}</td>
 		        <td>${nonCaseUsEdition.editionId}</td>  
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${page}</div>
</body>
</html>