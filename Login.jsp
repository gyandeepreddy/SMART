<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>    
   <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>  

<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sign in</title>
<link href="/SMART/resources/css/login.css"
    rel="stylesheet"/>
 <link href="/SMART/resources/css/home.css" rel="stylesheet">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.3/jquery.min.js"></script>

<style>
#loggedinmsg
{
	font-weight:bold;
	text-align:center;
	color:red;
}
</style>
</head>

<body>
<div class="wrapper">
<div class="logocontainer">
<a href="/SMART"><img src="/SMART/resources/img/SMART_Logo.png" alt="logo"></a>
  <div class="topnav-right">
	<a href="/SMART" class="class1">Home</a>
<!-- 	<a href="#" class="class4">Help</a>
 -->	<a href="login" class="class6">Login</a>
	<a href="registerAs" class="class7">Sign Up</a>
  </div>
</div>

<div class="sign">SIGN IN <span style="color:#f442dc;">${model.loginStatus}</span></div>

<div id="loginform" class="login">
	<form:form method="post" action="dashboard">
            <center>
            <table class="tablecss">
             <tr><td>
             <br />
            <div >
                	<input id="userName" type="text" name="userName" value="" placeholder="USERNAME" width="100px" height="50px"/>
            </div>
            </td></tr>
            <tr><td>
            <br />
            <div >
                 <input id="password" type="password" name="password" placeholder="PASSWORD"/>
            </div>  
            </td></tr> 
            <tr>
       		<td>
       		<br />
       		<div>
                <input type="submit" value="Sign In" class="btn"/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                <!-- 	<input type="reset" value="Reset" class="resetbtn"/> -->
            </div>
            </td>
            </tr> 
            </table>
            <br />
			<table>
			<tr>
       		<td style="font-size: 20px;color:black;" colspan="2">Forgot Password? &nbsp;<a href="forgotpassword">Click Here</a></td>
            </tr>
       		<tr>
       		<td style="font-size: 20px;color:black;" colspan="2">Not Yet Registered? &nbsp;<a href="registerAs">Register Here</a></td>
            </tr>
  			</table>
            </center>
        </form:form>
</div>

<div id="loggedinmsg">
Logged in as <%=session.getAttribute("firstname")%> &nbsp;
<p id="dashboardlink"> </p>
</div>
</div>
</div>
<p></p>
<div class="footer">
<table width="100%" cellspacing="10px" border="0" align="center">
  <tbody><tr>
    <td width="30%" valign="bottom" align="left"><h3>SMART</h3>
    <p>30 Pryor St SW, Atlanta, GA 30303 </p></td>
    <td width="70%" valign="bottom" align="right"><a href="/SMART/#AboutUs">About Us</a> | <a href="Contact">Contact</a> | <a href="TermsOfService">Terms of Usage</a> | <a href="PrivacyPolicy">Privacy Policy</a></td>
  </tr>
</tbody></table>
</div>
</body>
<script type="text/javascript">

var userid=<%=session.getAttribute("userid")%>;
if( userid==null || userid==0)
{
	document.getElementById("loggedinmsg").style.display="none";
}
else
	{
	var logindiv=document.getElementById("loginform");
	logindiv.style.display="none";
	}
var usertype="<%=session.getAttribute("usertype")%>";
var dashboard="";
if(usertype=="student")
	dashboard="<a href='StudentDashboard'>Go to my dashboard</a>";
else if(usertype=="teacher")
	dashboard="<a href='InstructorDashboard'>Go to my dashboard</a>";
else
	dashboard="<a href='InstructorDashboard'>Go to my dashboard</a>";
	
	document.getElementById("dashboardlink").innerHTML=dashboard;
</script>
</html>