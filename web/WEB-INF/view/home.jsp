<%-- 
    Document   : home
    Created on : Mar 17, 2015, 4:36:08 PM
    Author     : aryner
--%>
<%@page import="java.util.*"%>

<h1>Home</h1>

<%
	User user = (User)session.getAttribute("user");

	ArrayList<String> errors = (ArrayList)session.getAttribute("errors");
	if(errors != null && !errors.isEmpty()) {
		out.print("<div class='error'><p>");
		for(String error : errors) out.print(error+"<br>");
		out.print("</p></div>");

		session.removeAttribute("errors");
	}
%>

<div class="container">
	<div class="meta-row">
		<div class="meta-col">
			<a href="upload" class="btn">Upload</a>
		</div>

		<div class="meta-col">
			<a href="define_grading_questions" class="btn">Define Grading Scenario</a>
		</div>
	</div>
</div>