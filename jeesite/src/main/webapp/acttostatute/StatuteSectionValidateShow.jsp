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
	<div class="row">
	    <b> <span><a class='btn btn-success btn-small'  href="javascript:history.go(-1)">&nbsp;Return&nbsp;</a></b></span>
		<div class="span7">
		<div style="color:blue"><b><span>ActNumber: ${nonCaseStatuteSection0.actNumber}</span></b><br>
			<b> <span>ShortName: ${nonCaseStatuteSection0.shortName}</b></span><br>
	    </div>
		<div>${nonCaseStatuteSection0.content}</div>
		</div>
		<div class="span7" >
		<div style="color:blue"><b><span>ActNumber: ${nonCaseStatuteSection1.actNumber}</span></b><br>
			 <b><span>ShortName: ${nonCaseStatuteSection1.shortName}</span></b><br>
	    </div>
		<div>${nonCaseStatuteSection1.content}</div>
		</div>
	</div>
</body>

</html>