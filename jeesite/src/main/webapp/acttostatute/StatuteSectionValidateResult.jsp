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
	 <c:if test="${flag eq '0'}">
	<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/showSection" method="post" class="breadcrumb form-search">
	<div align="center">	
		<div align="center" class='btn-group'>	
			<a class='btn btn-primary btn-small'  href="javascript:sumitForm()">ShowSections</a>
			<a class='btn btn-success btn-small'  href="${ctx}/acttostatute/nonCaseStatuteSection/updatePassed">ValidatePass</a>
			<a class='btn btn-primary btn-small'  href="javascript:history.go(-1)">&nbsp;&nbsp;ReturnList&nbsp;&nbsp;</a>
		</div>
	</div>
	<table id="contentTable" class="table  table-bordered table-condensed" width="200">
		<thead>
			<tr>
			     <th>选择</th>
			    <th>State</th>
			    <th>ShortName</th>
<!-- 			    <th>UpdateType</th> -->
			    <th>ActNumber</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${repetitionSection}" var="nonCaseStatuteSection">
			<c:if test="${nonCaseStatuteSection.updateType eq 'Repeal'}">
				<tr class="error">
			</c:if>
			<c:if test="${nonCaseStatuteSection.updateType ne 'Repeal'}">
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
<%-- 				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.updateType}
				</td> --%>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.actNumber}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</form:form>
	</c:if>
	<c:if test="${flag eq '1'}">
	<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/validateSection?flag=1" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${countSectionPage.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="${countSectionPage.pageSize}"/>
		<label>State:&nbsp;</label>
		<form:select path="state" class="input-large">
		          <form:option value="" label="All State"/>
				 <form:option value="Illinois" label="Illinois"/>
				 <form:option value="Ohio" label="Ohio"/>
				 <form:option value="SouthDakota" label="SouthDakota"/>	
				 <form:option value="WestVirginia" label="WestVirginia"/>
				 <form:option value="Arizona" label="Arizona"/>
		</form:select>
		<label>Status:&nbsp;</label>
		<form:select path="status" class="input-large">
		         <form:option value="" label=""/>
				 <form:option value="Wait Validate" label="Rwait Validate"/>
				 <form:option value="Validate Passed" label="Validate Passed"/>
		</form:select>
		<label>ActNumber:&nbsp;</label><form:input path="actNumber" htmlEscape="false" maxlength="50" class="input-small"/>
		<a class='btn btn-primary btn-small'  href="javascript:sumitForm()">Search</a>
		<a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;&nbsp;ReturnList&nbsp;&nbsp;</a>
	    <br><div align="right">
	    <br>
	</form:form>
	<sys:message content="${message}"/>
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			    <th>State</th>
			    <th>ActNumber</th>
			    <th>CountNum</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${countSectionPage.list}" var="nonCaseStatuteSection">
			<tr>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.state}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.actNumber}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.countNum}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div class="pagination">${countSectionPage}</div>
	</c:if>
</body>

</html>