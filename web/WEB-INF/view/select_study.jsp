<%-- 
    Document   : select_study
    Created on : Mar 2, 2015, 11:59:21 AM
    Author     : aryner
--%>
<%@page import="utilities.Constants"%>
<%@page import="java.util.*"%>

<h1>Select a study</h1>

<form action="setStudy" method="POST" > 
	<p>
		<select name="name" id="studyOptions" class="btn">
			<%
			ArrayList<String> studyNames = (ArrayList)request.getAttribute("studyNames");
			for(String study : studyNames) {
			%>
			<option value='<%out.print(study);%>'><%out.print(study);%></option>
			<%
			}
			%>
		</select>
	</p>
	<input type="submit" class="btn" value="Use Study" name="setStudy">
</form>

<%
User user = (User) request.getAttribute("user");
if(user.isStudy_coordinator()) {
%>
<h1>...Or create a new one</h1>

<div class="hidden error" name="study_name_taken"><%out.print(Constants.STUDY_NAME_TAKEN);%></div>

<form action="create_study" method="GET">
	<p>
		<span class="hidden error" name="repeatName"><%out.print(Constants.REPEAT_NAME);%><br></span>
		<span class="hidden error" name="missingNameNumber"><%out.print(Constants.MISSING_NAME_NUMBER);%><br></span>
		New Study Name: 
		<input type="text" name="name">
		<br>
		Amount of meta-data to track in photos:
		<input type="text" name="number">
		<span class='note'>(Not including the photo name, this is saved by default)<span>
	</p>
	<input type="submit" class="btn" value="Create Study" name="newStudy">
</form>
<%}%>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/newStudy.js" type="text/javascript"></script>