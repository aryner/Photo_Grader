<%-- 
    Document   : register
    Created on : Mar 2, 2015, 10:29:32 AM
    Author     : aryner
--%>

<h2>Register</h2>


<p><a href="index.jsp">Already registered?</a></p>

	<%
		if(session.getAttribute("user") != null) {
			response.sendRedirect("/Photo_Grader/home"); 
		}
		if(session.getAttribute("error") != null) {
			out.print("<p class='error'>" + session.getAttribute("error") + "</p>");
			session.removeAttribute("error");
		}
	%>

<form action="createUser" method="Post">
	<p>
		User name: <input type="text" name="userName">
	</p>

	<p>
		Password: <input type="password" name="password">
	</p>
	<p>
		Repeat password: <input type="password" name="rePassword">
	</p>
	<p>
		<input type="submit" value="submit" class="btn">
	</p>
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/register.js" type="text/javascript"></script>