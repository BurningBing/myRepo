<%@ page contentType="text/html;charset=UTF-8" %>
<%@ include file="/WEB-INF/views/include/taglib.jsp"%>
<html>
<head>
	<title>NoneCase管理</title>
	<meta name="decorator" content="default"/>
	<style type="text/css">
		.act{width:200px;height:80px;border:1px solid #ccc;}
		#main{width:100%;height: 100%;position: absolute;overflow: hidden;}
		#list_table{width:68%;float: left;}
		#list_table tr{border-bottom:1px solid #ccc;line-height: 35px;cursor: pointer;}
		#list_table tbody tr:nth-of-type(odd){background:#F5F5F5;}
		#list_table tbody tr:nth-of-type(even){background:white;}
		#list_table td{border-right:1px solid #ccc;text-align: center;}
	</style>
	<script type="text/javascript" src="${ctxModules }/js/simditor/scripts/jquery.min.js"></script>
	<script type="text/javascript" src="${ctxModules }/static/jquery-filestyle/jquery.filestyle.js"></script>
	<script type="text/javascript" src="${ctxModules }/static/jquery-validation/1.11.1/lib/jquery.form.js"></script>
	<script type="text/javascript">
		window.onload = function()
		{
			var iHeight = $("#main")[0].offsetHeight;
			$('#bodyDiv').css('height',iHeight-$('#headDiv')[0].offsetHeight-8);
			
			$("input[type=file]").filestyle({ 
			    image: "${ctxModules}/img/nonCase_upload.ico",
			    imageheight : 25,
			    imagewidth : 25,
			});
		}
		
		function uploadFile(id)
		{
			$("#fm"+id).ajaxSubmit({
				success: function(data) { //成功   
					var json = $.parseJSON(data); 
					alert(json['message']);
					window.location = "${ctx}/acttostatute/nonCase/toSpecialActsList";
				}
			});
		}

		function page(n,s){
			$("#pageNo").val(n);
			$("#pageSize").val(s);
			$("#searchForm").submit();
        	return false;
        }
	</script>
</head>
<body>
	<div id="main">
		<div id="headDiv">
			<form:form id="searchForm" modelAttribute="nonCase" action="${ctx}/acttostatute/nonCase/noncaeList" method="post" class="breadcrumb form-search">
				<input id="pageNo" name="pageNo" type="hidden" value="${page.pageNo}"/>
				<input id="pageSize" name="pageSize" type="hidden" value="${page.pageSize}"/>
				<label>Case Id:&nbsp;</label>
				<input id="caseId" name="id" class="input-small" type="text" maxlength="50">
				<label>State:&nbsp;</label>
				<input id="state" name="state" class="input-small" type="text" maxlength="50">
				<label>Act Number:&nbsp;</label>
				<input id="actNumber" name="actNumber" class="input-small" type="text" maxlength="50">
				&nbsp;&nbsp;&nbsp;
				<input id="btnSubmit" class="btn btn-primary" type="submit" value="查&nbsp;&nbsp;&nbsp;&nbsp;询">
			</form:form>
		</div>
		<div id="bodyDiv" style="overflow: auto;">
			<table id="list_table">
				<thead>
					<tr style="border-top: 1px solid #ccc;">
						<td width="10px"><b>#</b></td>
						<td width="50px"><b>CaseId</b></td>
						<td width="100px"><b>State</b></td>
						<td width="100px"><b>Act Number</b></td>
						<td width="100px"><b>Day</b></td>
						<td colspan="3"></td>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${page.list }" var="n" varStatus="s">
						<tr>
							<td>${s.index+1 }</td>
							<td>${n.id }</td>
							<td>${n.state }</td>
							<td>${n.actNumber }</td>
							<td>${n.day }</td>
							<td style="text-align: left;" width="10%">
								&nbsp;&nbsp;<img title="下载" width="25px" height="25px" src="${ctxModules }/img/nonCase_download.ico">
							</td>
							<td width="10%">
								<form id='fm${n.id }' style="width: 50px;margin:0px;padding: 0px;height:30px;position: relative;" method="post" action="${ctx }/acttostatute/nonCase/uploadSpecialActs" enctype="multipart/form-data">
									<input type="hidden" name="id" value="${n.id }" />
									<input type="hidden" name="special" value="special" />
									<input type="file" title="上传" width="20px" style="opacity: 0.0;overflow: hidden;" name="filename" onchange="uploadFile(${n.id })">
								</form>
							</td>
							<td width="15%">
<%-- 								<c:choose> --%>
<%-- 									<c:when test="${nonCase:isMyActs(n.id)}"> --%>
<%-- 										<a href="parseSpecialActs?id=${n.id }&&state=${n.state}" class="btn btn-info"  target="_BLANK" ><span>预览</span></a> --%>
<%-- 										<a href="deleteSpecialActs?id=${n.id }&&state=${n.state}" class="btn btn-danger"  ><span>删除</span></a> --%>
<%-- 									</c:when> --%>
<%-- 								</c:choose> --%>
							</td>
						</tr>
					</c:forEach>
				</tbody>
			</table>
		</div>
	</div>
</body>
</html>