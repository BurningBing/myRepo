<%@ page contentType="text/html;charset=UTF-8"%>
<html>
<head>
    <title>Fast Case - Login Page</title>
    <style type="text/css">
        html,body{
            width:100%;
            height:100%;
            margin:0;
        }
        body{
            display:flex;
            flex-direction:column;
        }
        a{
        	text-decoration: none;
        }
        #header{
            flex:0 0 60px;
            background-color:rgb(78, 78, 78);
            box-shadow:0 0 5px black;
            z-index:1;
        }
        #logo{
            margin-left:10%;
            height:60px;
            line-height:60px;
        }
        #logo span{
            color:white;
            text-shadow:0 0 10px white;
        }
        #main{
            flex: 1;
            background-image: url(../image/login/main-bg.jpg);
            background-size: cover;
            background-repeat: no-repeat;
            background-position: 0 60%;
            display:flex;
            justify-content:center;
        }

        #login-panel{
            height: 328px;
            background: rgba(255, 210, 142, 0.42);
            flex: 0 0 500px;
            display: flex;
            flex-direction: column;
            border-radius: 6px;
            margin-top: 135px;
        }

        #login-header{
            flex: 0 0 40px;
            display: flex;
            justify-content: center;
            align-items: center;
            color: #FFFFFF;
            cursor: default;
            font-size: 20px;
            font-weight: bold;
            border-bottom: 1px solid #ccc;
        }
        #login-form{
            flex: 1;
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            border-bottom: 1px solid #ccc;
        }
        #login-form input[type=text],input[type=password]{
            width: 248px;
            height: 30px;
            border-radius: 3px;
            border: none;
            outline: none;
            padding-left: 5px;
        }

        #login-btn{
            width: 253px;
            height: 30px;
            background-color: #0089FF;
            margin-top: 10px;
            display: flex;
            justify-content: center;
            align-items: center;
            color: white;
            cursor: pointer;
            border-radius: 5px;
        }

        #login-btn:hover{
            background-color: #0066FF;
        }

        #login-btn:active{
            background-color: #002BFF;
            color:#ccc;
        }

        #login-footer{
            flex: 0 0 40px;
            display: flex;
            justify-content: center;
            align-items: center;
            cursor: default;
            color: #555;
        }

    </style>
    <script type="text/javascript">


    </script>
</head>
<body>
    <div id="header">
        <div id="logo">
            <span style="font-size:30px">Fast Case | </span><span style="font-size:20px">Act to Statute</span>
        </div>
    </div>
    <div id="main">
        <div id="login-panel">
            <div id="login-header">Log in To Fast Case</div>
            <div id="login-form">
                <form id="fm" action="/jeesite/a/login" style="display: flex;flex-direction: column" method="post">
<!--                 	<input type="hidden" name="page" value="background"> -->
<!--                 	<input type="hidden" name="page" value="front"> -->
                    <input type="text" name="username" placeholder="user name">
                    <input type="password" name="password" style="margin-top: 20px" placeholder="password">
                    <div style="margin-top: 10px;color:#5B435F;font-size: 14px;width: 253px">
                       <select name="page">
                       		<option value="front">Front</option>
                    		<option value="background">Background</option>
                       </select>
                    </div>
                    <input id="login-btn" type="submit" value="Log In" />
                </form>
            </div>
            <div id="login-footer">
                <span>Don't have an account?</span>
                <a href="#" style="color: #008EFF;margin-left: 8px;">Sign up!</a>
            </div>
        </div>
    </div>
</body>
</html>