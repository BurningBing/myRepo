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
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			    <th>State</th>
			    <th>ActNumber</th>
			    <th>CountNum</th>
			    <th>Remark</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${remarList}" var="nonCaseStatuteSection">
			<tr>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.shortName}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.state}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.actNumber}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.remark}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	<div align="center">
		<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/remarkSection" method="post" class="breadcrumb form-search">
		<form:hidden path="ids"/>
		<div class="control-group">
			<label class="control-label"><b>Please input Remark:</b></label>
			<div class="controls">
 				<form:textarea  path="remark" htmlEscape="false" maxlength="180" class="input-xlarge  digits" /> 
			</div>
		</div>
		<a class='btn btn-primary btn-small'  href="javascript:sumitForm()">Remark</a>
		<a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;&nbsp;Return&nbsp;&nbsp;</a>
	</form:form>
	</div>
</body>
</html>