<%-- 
    Document   : remove_study
    Created on : Nov 20, 2015, 10:55:25 AM
    Author     : aryner
--%>
<%@page import="utilities.Constants"%>
<%@page import="java.util.*"%>

<h1 class="error">Delete a Study?</h1>

<form action="removeStudy" method="POST" > 
	<p class='error'>Deleting a study is permanent.<br> All data associated with the deleted study will be lost</p>
	<p>
		<select name="studyName" id="studyOptions" class="btn">
			<%
			ArrayList<String> studyNames = (ArrayList)request.getAttribute("studyNames");
			for(String study : studyNames) {
			%>
			<option class='error' value='<%out.print(study);%>'><%out.print(study);%></option>
			<%
			}
			%>
		</select>
	</p>
	<h2>Enter your user name and password to confirm the deletion of this study</h2>
	<p>User name: 
		<input type="text" name="userName">
	</p>
	<p>Password: 
		<input type="password" name="password">
	</p>
	<input type="submit" class="btn error" value="Delete Study" name="delete">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/remove_study.js" type="text/javascript"></script>