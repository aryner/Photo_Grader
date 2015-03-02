<%-- 
    Document   : index
    Created on : Feb 27, 2015, 11:52:16 AM
    Author     : aryner
--%>


<h2>Sign in</h2>

<%
	if(session.getAttribute("error") != null){
		out.print("<p class='error'>"+session.getAttribute("error")+"</p>");
		session.removeAttribute("error");
	}
	if(session.getAttribute("user") != null) {
		response.sendRedirect("/Photo_Grader/home"); 
	}
%>

<form action="login" method="POST">
	<p>User name: 
		<input type="text" name="userName">
	</p>
	<p>Password: 
		<input type="password" name="password">
	</p>
	<p>
		<input type="submit" value="Log in" class="btn">
	</p>
</form>

<a href="register">Register</a>