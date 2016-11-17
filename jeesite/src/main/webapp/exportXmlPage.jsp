<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<!-- jQuery -->
<title>export</title>
<script type="text/javascript" src="js\jquery\jquery-2.2.1.min.js"></script>
<script type="text/javascript">
	function load(){
		var state = $("#state").val();
		var date = $("#date").val();
		$.post(
			"f/test/loadExportList",
			{
				"state":state,
				"date":date
			},
			
			function(ret){
				var infor = document.getElementById("infor");
				infor.innerHTML = "";
				for(var i=0;i<ret.length;i++){
					var json = ret[i];
					infor.innerHTML += "<tr>"+
											"<td>"+json.id+"</td>"+
											"<td>"+json.state+"</td>"+
											"<td>"+json.billNumber+"</td>"+
											"<td>"+json.filePath+"</td>"+
											"<td>"+json.date+"</td>"+
											"<td>"+json.status+"</td>"+
											"<td>"+json.delFlag+"</td>"+
										"</tr>"	
				}
			}
			,
			"json"
		)
	}
	
	function exportFile(){
		var state = $("#state").val();
		var date = $("#date").val();
		$.post(
			"f/test/exportXmlFile",
			{
				"state":state,
				"date":date
			},
			function(ret){
				$("#bill-list").html(ret);
			}
		);
	}

</script>
</head>
<body>
	<div>
		State:<input id="state" type="text"/><br>
		Date :<input id="date" type = "text"/><br>
		<button onclick = load()>Load</button>
		<button onclick = exportFile()>export</button>
	</div>
	<div id="bill-list">
	
	</div>
	<div>
		<table border = "1">
			<thead>
				<tr>
					<th>ID</th>
					<th>State</th>
					<th>Bill Number</th>
					<th>File Path</th>
					<th>Date</th>
					<th>Status</th>
					<th>Del Flag</th>
				</tr>
			</thead>
			<tbody id="infor">
				
			</tbody>
		</table>
	</div>
</body>
</html>