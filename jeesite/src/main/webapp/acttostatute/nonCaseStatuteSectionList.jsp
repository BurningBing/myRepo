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
		<li><a href="${ctx}/acttostatute/nonCaseStatuteSection/form">信息添加</a></li>
	</ul>
	<form:form id="searchForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/list" method="post" class="breadcrumb form-search">
		<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
		<input id="pageSize" name="pageSize" type="hidden" value="30"/>
		<label>ShortName:&nbsp;</label><form:input path="shortName" htmlEscape="false" maxlength="50" class="input-small"/>
		<label>State:&nbsp;</label>
		<form:select path="state" class="input-small">
		         <form:option value="" label=""/>
		         <form:option value="" label=""/>
				<form:option value="Arizona" label="Arizona"/>
				 <form:option value="Virginia" label="Virginia"/>
				 <form:option value="SouthDakota" label="SouthDakota"/>	
				 <form:option value="Illinois" label="Illinois"/>
				 <form:option value="Ohio" label="Ohio"/>
				 <form:option value="Missouri" label="Missouri"/>
				 <form:option value="Iowa" label="Iowa"/>
				 <form:option value="Texas" label="Texas"/>
				 <form:option value="Idaho" label="Idaho"/>
				 <form:option value="Montana" label="Montana"/>
				 <form:option value="RhodeIsland" label="RhodeIsland"/>
				 <form:option value="Oklahoma" label="Oklahoma"/>
				 <form:option value="Montana" label="Montana"/>
				 <form:option value="Kentucky" label="Kentucky"/>
				 <form:option value="California" label="California"/>
				 <form:option value="SouthCarolina" label="SouthCarolina"/>
				 <form:option value="North Carolina" label="North Carolina"/>
				 <form:option value="Louisiana" label="Louisiana"/>
				 <form:option value="New Mexico" label="New Mexico"/>
				 <form:option value="Hawaii" label="Hawaii"/>
				 <form:option value="Nevada" label="Nevada"/>
				 <form:option value="Connecticut" label="Connecticut"/>
				 <form:option value="Florida" label="Florida"/>
				 <form:option value="Washington" label="Washington"/>
				 <form:option value="Oregon" label="Oregon"/>
				 <form:option value="West Virginia" label="West Virginia"/>
				 <form:option value="Minnesota" label="Minnesota"/>
				 
		</form:select>
		<label>ActNumber:&nbsp;</label><form:input path="actNumber" htmlEscape="false" maxlength="50" class="input-small"/>
	    <label>Status:&nbsp;</label>
		<form:select path="status" class="input-small">
		         <form:option value="" label=""/>
		         <form:option value="" label=""/>
				 <form:option value="Waiting" label="Waiting"/>
				 <form:option value="Passed" label="Passed"/>
				 <form:option value="Confirmed" label="Confirmed"/>
				 <form:option value="Remark" label="Remark"/>
		</form:select>
		<label>日期范围：&nbsp;</label><input id="beginDate" name="beginDate" type="text" readonly="readonly" maxlength="20" class="input-mini Wdate"
				value="<fmt:formatDate value="${beginDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>
			<label>&nbsp;--&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;</label><input id="endDate" name="endDate" type="text" readonly="readonly" maxlength="20" class="input-mini Wdate"
				value="<fmt:formatDate value="${endDate}" pattern="yyyy-MM-dd"/>" onclick="WdatePicker({dateFmt:'yyyy-MM-dd',isShowClear:false});"/>&nbsp;&nbsp;
<!-- 	  		<div>&nbsp;</div> -->
	  		<div align="center">
		  		<div align="right" class="btn-group">
			  		<a class='btn btn-primary btn-small'  href="javascript:searchForm();">Search</a>
			  		<a class='btn btn-success btn-small'  href="${ctx}/acttostatute/nonCaseStatuteSection/validateSection?flag=0">Validate</a>
					<a class='btn btn-warning btn-small'  href="javascript:matchingIndex();">Matching</a>
			  		<a class='btn btn-danger btn-small'  href="javascript:sumitForm();">Remark</a>
			  		<a class='btn btn-primary btn-small'  href="${ctx}/acttostatute/nonCaseStatuteSection/qaIndex">QAFind</a>
			  		<a class='btn btn-success btn-small'  href="${ctx}/acttostatute/nonCaseStatuteSection/validateSection?flag=1">Report</a>
		  		</div>&nbsp;&nbsp;&nbsp;
	  		</div>
	</form:form>
	<sys:message content="${message}"/>
	<form:form id="remarkForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/remark" method="post" class="breadcrumb form-search">
	<table id="contentTable" class="table table-striped table-bordered table-condensed">
		<thead>
			<tr>
			    <th>选择</th>
			    <th>State</th>
			    <th>Caption</th>
			    <th>ShortName</th>
			    <th>Status</th>
			    <th>ActNumber</th>
			    <th>QA User</th>
			    <th>CreateTime</th>
			    <th>DownloadTime</th>
			    <th>ExportTime</th>
			</tr>
		</thead>
		<tbody>
		<c:forEach items="${page.list}" var="nonCaseStatuteSection">
			<tr>
				<td nowrap='nowrap' width="10">
			    <form:checkbox path="ids" value="${nonCaseStatuteSection.id}"/>
			    </td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.state}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.caption}
				</td>
				<c:if test="${nonCaseStatuteSection.status eq 'Passed'||nonCaseStatuteSection.status eq 'Confirmed'}">
				<td nowrap='nowrap'> 
				 	<a href="${ctx}/acttostatute/nonCaseStatuteSection/showSectionByShortName?id=${nonCaseStatuteSection.id}">
						${nonCaseStatuteSection.shortName}
					</a>
				</td>
				</c:if>
				<c:if test="${nonCaseStatuteSection.status ne 'Passed'&&nonCaseStatuteSection.status ne 'Confirmed'}">
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.shortName}
				</td>
				</c:if>
				<td nowrap='nowrap'> 
					<c:if test="${nonCaseStatuteSection.status eq 'Remark'}">
						<b><font color="red">${nonCaseStatuteSection.status}</font></b>
					</c:if>
					<c:if test="${nonCaseStatuteSection.status eq 'Passed'}">
						 <b><font color="green">${nonCaseStatuteSection.status}</font></b>
					</c:if>
					<c:if test="${nonCaseStatuteSection.status eq 'Waiting'}">
						 ${nonCaseStatuteSection.status}
					</c:if>
					<c:if test="${nonCaseStatuteSection.status eq 'Confirmed'}">
						 <b><font color="blue">${nonCaseStatuteSection.status}</font></b>
					</c:if>
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.actNumber}
				</td>
					<td nowrap='nowrap'> 
					${nonCaseStatuteSection.qa}
				</td>
				<td>
  				   <fmt:formatDate value="${nonCaseStatuteSection.createTime}" pattern="yyyy-MM-dd"/>  
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.downloadTime}
				</td>
				<td nowrap='nowrap'> 
					${nonCaseStatuteSection.exportTime}
				</td>
			</tr>
		</c:forEach>
		</tbody>
	</table>
	</form:form>
	<div class="pagination">${page}</div>
</body>
</html>