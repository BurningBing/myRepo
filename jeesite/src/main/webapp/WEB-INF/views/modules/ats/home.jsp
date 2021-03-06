<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
<meta charset="UTF-8">
<title>Act To Statute</title>

<style type="text/css">
	html,body{
		margin:0;
		width:100%;
		height:100%;
	}
	html {
	}
	body{
		background-image: url(image/common/home-bg.jpg);
	    background-size: 100% 100%;
	    display: flex;
	    flex-direction: column;
	    overflow: hidden;
	}
	ul{
	    list-style: none;
	    margin: 0;
	    padding: 0;
	}
	#main{
		flex: 1;
	}
	#menu{
	    width: 250px;
	    height: calc(100% - 50px);
	    background-color: rgba(0, 0, 0, 0.11);
	    position: absolute;
	    left: -250px;
	    bottom: 50px;
	    z-index: 1000;
	    box-shadow: 3px 3px 10px #595858;
	}
	.item{
        width: 119px;
	    height: 119px;
	    float: left;
	    color: #fff;
	    margin: 3px;
	    cursor: pointer;
	    background-position: 26px;
	    background-repeat: no-repeat;
	    display: flex;
	    justify-content: center;
	    align-items: flex-end;
	    border-radius: 5px;
	    position: relative;
	    background-color: rgb(197, 178, 138);
	    box-shadow: 0 0 5px black;
	    text-shadow: 0 0 5px #182E11;
	}
	.item:hover{
		background-color: #7E562C;
	}
	
 	#all-acts{ 
 	    background-image: url('image/menu/all acts.png');
 	}
 	
 	#html-acts{
 		background-image: url('image/menu/html.png');
 	}
	
 	#my-acts-menu{ 
 	    background-image: url('image/menu/my acts.png');  
 	} 
 	
 	#special{ 
 	    background-image: url('image/menu/special.png');
 	}
 	
 	#my-spacial{ 
 	    background-image: url('image/menu/my acts.png');  
 	} 
 	
 	#download{ 
 	    background-image: url('image/menu/download.png');  
 	} 
 	
 	#feedback{ 
 	    background-image: url('image/menu/download.png');  
 	} 
 	
 	#myChecking{ 
 	    background-image: url('image/menu/download.png');  
 	} 
 	
 	#checking-page{ 
 	    background-image: url('image/menu/checking.png');  
 	}
 	
 	.showMenu{
	    animation: showMenu 0.5s;
	    animation-fill-mode: forwards;
	}
	
	.hiddenMenu{
	    animation: hiddenMenu 0.5s;
	    animation-fill-mode: forwards;
	}
	
	@keyframes showMenu{
	    from{left: -250px}
	    to{left: 0px}
	}
	
	@keyframes  hiddenMenu{
	    from{left: 0px}
	    to{left: -250px}
	}
	#footer{
		flex:0 0 45px;
		background-color:rgb(196, 178, 138);
	}
	
	#download-main{
		width: 590px;
	    height: calc(100% - 35px);
	    margin: 0px 5px 5px 5px;
	    background: #fff;
	    border-radius: 5px;
	}
	.download-header{
		width: 20%;
	    float: left;
	    text-align: center;
	    height: 30px;
	    line-height: 30px;
	    box-shadow: 0px 1px 2px #999999;
	    font-weight: bold;
	    font-size: 17px;
	    background-color: #ECECEC;
	}
	.donwload-item{
		width: 118px;
	    float: left;
	    text-align: center;
	    height: 30px;
	    line-height: 30px;
	    color: #7C3F3F;
	    box-shadow: 0px 0px 1px #D1D1D1;
	}
	.task{
		float:left;
	}
	.task:nth-child(odd){
		background-color:#fff;
	}
	.task:nth-child(even){
		background-color:#F6F6F6;
	}
	.task:hover{
		background-color:rgb(255, 202, 51);
	}
	.download-btn{
    	height: 24px;
		display: inline-block;
		width: 24px;
		margin: 3px;
		cursor: pointer;
	}
	.download-btn:hover{
		background-color: #ccc;
	    border-radius: 4px;
	    box-shadow: 0 0 4px black;
	}
	#searchBtn{
		background-image: url('image/common/search-32.png');
	    width: 39px;
	    height: 39px;
	    float: left;
	    margin-top: 3px;
	    background-position: 3.5px;
	    background-repeat: no-repeat;
	    background-color: #8E8268;
	}
	#searchBtn:hover{
		background-color:#5B5546;
	}
	#searchBtn:active{
		background-color:#38352C;
	}
	.min-item{
		width: 39px;
	    height: 39px;
	    float: left;
	    margin: 3px 10px;
	    background-position: 3.5px;
	    background-repeat: no-repeat;
	    border-radius: 5px;
	}
	.min-item:hover{
		box-shadow: 0 0 10px;
	}
	.min-item:active{
		box-shadow: none;
	}
	.states{
	    flex: 0 0 150px;
	    display: flex;
	    flex-direction: column;
	    margin: 0 0 5px 5px;
	    background-color: rgba(255, 255, 255, 0.91);
	    border-radius: 5px;
	    overflow: auto;
	}
	#acts-list{
	    flex: 1;
	    background-color: rgba(255, 255, 255, 0.91);
	    margin: 0 5px 5px 5px;
	    border-radius: 5px;
	    overflow:hidden;
	}
	.acts-title{
		width: 25%;
	    float: left;
	    text-align: center;
	    height: 30px;
	    line-height: 30px;
	    box-shadow: 0px 1px 2px #999999;
	    font-weight: bold;
	    font-size: 17px;
	    background-color: #ECECEC;
	}
	.acts-item{
	    width: 25%;
	    float: left;
	    text-align: center;
	    height: 30px;
	    line-height: 30px;
	    box-shadow: 0px 1px 2px #999999;
	    background-color: #FFFFFF;
	    color: #5C5C5C;
	}
	#acts-list-data{
	    clear: both;
    	height: calc(100% - 65px);
    	overflow: auto;
	}
	.currentState{
		background-color:#E3E3E3;
	}
	.state-item{
		height: 25px;
	    line-height: 25px;
	    padding-left: 10px;
	    border-bottom: 1px solid #ccc;
	}
	.state-item:hover{
		background-color:#E3E3E3;
	}
	.state-item:active{
		background-color:#d8d8d8;
	}
	.signBtn{
		width: 70px;
	    height: 24px;
	    line-height: 24px;
	    background-color: #573A1F;
	    border-radius: 5px;
	    margin: 3px auto;
	    color: #fff;
	    cursor: default;
	    font-family: monospace;
	}
	.signBtn:hover{
		background-color:#B8A273;
	}
	.editor-tool{
	    width: 24px;
	    height: 24px;
	    margin: 5px;
	    display: flex;
	    justify-content:center;
	    align-items: center;
	}
	.editor-tool:hover{
	    cursor: pointer;
	    box-shadow: 0 0 10px;
	    border-radius: 5px;
	    background-size: 20px;
	    background-repeat: no-repeat;
	    background-position: center;
	}
	.editor-tool:active{
	    cursor: pointer;
	    box-shadow: 0 0 5px;
	    border-radius: 5px;
	}
	.key-infor-value{
	    width: 97.5%;
	    outline: none;
	    border: none;
	    padding-left: 5px;
	    margin-left: 5px;
	    flex: 0.9;
	}
	#snapshot{
	    flex: 1;
	    background-color: #fff;
	    margin: 0 10px 10px 10px;
	    border: none;
	    border-radius: 5px;
	}
	.btn{
	    width: 40px;
	    background-color: rgb(204, 204, 204);
	    float: left;
	    display: flex;
	    justify-content: center;
	    align-items: center;
	    cursor: pointer;
	    margin: 10px 0;
	}
	.btn:hover{
	    background-color: #aaa;
	}
	#combineBtn{
		flex: 0 0 35px;
	    align-items: center;
	    justify-content: center;
	    display: flex;
	    background: #932A2A;
	    color: #fff;
	    cursor: default;
	}
	#combineBtn:hover{
		background: #762222;
	}
	#combineBtn:active{
		box-shadow:  0px 0px 3px black;
		background: #5D1A1A;
	}
	
</style>
<link href="css/chad.css" rel="stylesheet">
<link href="js/zTree/css/zTreeStyle/zTreeStyle.min.css" rel="stylesheet">
<link rel="stylesheet" type="text/css" href="simditor/styles/simditor.css" />
<link rel="stylesheet" type="text/css" href="css/jquery.mCustomScrollbar.css" />
<script type="text/javascript" src="js/jquery/jquery-2.2.1.min.js"></script>
<script type="text/javascript" src="js/jquery.mCustomScrollbar.concat.min.js"></script>
<script type="text/javascript" src="js/jquery/jquery.form.js"></script>
<script type="text/javascript" src="js/zTree/js/jquery.ztree.all-3.5.js"></script>
<script type="text/javascript" src="simditor/scripts/module.js"></script>
<script type="text/javascript" src="simditor/scripts/hotkeys.js"></script>
<script type="text/javascript" src="simditor/scripts/simditor.js"></script>
<script type="text/javascript" src="js/ats_1.0.js"></script>
<script type="text/javascript" src="js/chad.js"></script>

</head>
<body>
	<div id="main">
        <!-- 菜单 -->
        <div id="menu">
            <div id="all-acts" class="item" onclick="showPdfActs(1)">All Acts</div>
            <div id="my-acts-menu" class="item" onclick="showMyActs()">My Acts</div>
            <div id="html-acts" class="item" onclick="showHtmlActs()">HTML Acts</div>
            <div id="special" class="item" onclick="showMyHtml()">My HTML</div>
            <div id="special" class="item" onclick="showAllActs(0);">Special Acts</div>
            <div id="my-spacial" class="item" onclick="showMySpacial()">My Special</div>
            <div id="download" class="item" onclick="showDownloadPanel()">Download</div>
            <div id="feedback" class="item" onclick="showMyFeedback()">Feedback</div>
            <div id="checking-page" class="item" onclick="showCheckingPage()">Checking Page</div>
            <div id="myChecking" class="item" onclick="showMycheckingList()">MyChecking</div>
        </div>
<!--         <button onclick = "createEditor()">Click</button> -->
    </div>
    <div id="footer">
    	<input style="height:39px;margin-left:10px;margin-top:3px;padding-left:10px;width:350px;outline:none;border:none;float:left;" placeholder="Search Something..">
    	<div id="searchBtn"></div>
    </div>
    <a href="a/logout">Log out</a>
</body>
</html>