<%-- 
    Document   : select_study
    Created on : Mar 2, 2015, 11:59:21 AM
    Author     : aryner
--%>
<%@page import="utilities.Constants"%>

<h1>Select a study</h1>

<form action="setStudy" method="POST"> 
	<p>
		<select name="name" class="btn">
		</select>
	</p>
	<input type="submit" class="btn" value="Use Study">
</form>

<h1>...Or create a new one</h1>

<div class="hidden error" name="study_name_taken"><%out.print(Constants.STUDY_NAME_TAKEN);%></div>

<form action="createStudy" method="POST">
	<p>
		New Study Name: 
		<input type="text" name="name">
	</p>
	<input type="submit" class="btn" value="Create Study">
</form>