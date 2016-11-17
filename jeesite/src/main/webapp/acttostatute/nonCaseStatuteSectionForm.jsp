<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>信息管理</title>
	<meta name="decorator" content="default"/>
	<script type="text/javascript">
		$(document).ready(function() {
			//$("#name").focus();
			$("#inputForm").validate({
				submitHandler: function(form){
					loading('正在提交，请稍等...');
					form.submit();
				},
				errorContainer: "#messageBox",
				errorPlacement: function(error, element) {
					$("#messageBox").text("输入有误，请先更正。");
					if (element.is(":checkbox")||element.is(":radio")||element.parent().is(".input-append")){
						error.appendTo(element.parent().parent());
					} else {
						error.insertAfter(element);
					}
				}
			});
		});
	</script>
</head>
<body>
	<ul class="nav nav-tabs">
		<li><a href="${ctx}/acttostatute/nonCaseStatuteSection/">信息列表</a></li>
		<li class="active"><a href="${ctx}/acttostatute/nonCaseStatuteSection/form?id=${nonCaseStatuteSection.id}">信息<shiro:hasPermission name="acttostatute:nonCaseStatuteSection:edit">${not empty nonCaseStatuteSection.id?'修改':'添加'}</shiro:hasPermission><shiro:lacksPermission name="acttostatute:nonCaseStatuteSection:edit">查看</shiro:lacksPermission></a></li>
	</ul><br/>
	<form:form id="inputForm" modelAttribute="nonCaseStatuteSection" action="${ctx}/acttostatute/nonCaseStatuteSection/newUpload" method="post" class="form-horizontal" ENCTYPE="multipart/form-data">
		<form:hidden path="id"/>
		<sys:message content="${message}"/>		
		<div class="control-group">
			<label class="control-label">State</label>
			<div class="controls">
				<form:select path="state" class="input-large">
				 <form:option value="Arizona" label="Arizona"/>
				 <form:option value="Virginia" label="Virginia"/>
				 <form:option value="SouthDakota" label="SouthDakota"/>	
				 <form:option value="West Virginia" label="West Virginia"/>
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
				 <form:option value="Minnesota" label="Minnesota"/>
			</form:select>	
			</div>
		</div>
		<%--<div class="control-group">
			<label class="control-label">LibraryName:</label>
			<div class="controls">
				<form:input path="libraryName" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div>
		 <div class="control-group">
			<label class="control-label">state:</label>
			<div class="controls">
				<form:input path="state" htmlEscape="false" maxlength="500" class="input-xlarge "/>
			</div>
		</div> --%>
		<div class="control-group">
			<label class="control-label">file:</label>
			<div class="controls">
		<form:input type="file" id="cfile" name="file" path="fileName" htmlEscape="false" rows="2" maxlength="200"   class="input-xxlarge"/>
			</div>
		</div>
		
		 <div class="form-actions">
			<input id="btnSubmit" class="btn btn-primary" type="submit" value="保 存"/>&nbsp;
			<input id="btnCancel" class="btn" type="button" value="返 回" onclick="history.go(-1)"/>
		</div>
	</form:form>
</body>
</html>