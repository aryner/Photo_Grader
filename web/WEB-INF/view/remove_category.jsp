<%-- 
    Document   : remove_category
    Created on : Oct 23, 2015, 9:17:41 AM
    Author     : aryner
--%>

<%@page import="java.util.ArrayList"%>

<h1>Delete Category</h1>
<p>
	It's recommended that you get CSVs from categories you are about to delete.  
	<a href='select_CSVs' class='btn'>Get CSV file</a>
	<br><span class='error'>Warning: deleting cannot be undone; all data from the category will be lost.</span>
</p>

<%
ArrayList<String> grades = (ArrayList)request.getAttribute("grades");
ArrayList<String> ranks = (ArrayList) request.getAttribute("ranks");
ArrayList<String> compares = (ArrayList) request.getAttribute("compares");
%>

<h2>Delete a Grading Category</h2>
<%
if(grades!=null&&grades.size()>0) { 
%>
<form action="removeGradeCategory" method="POST">
	<select name="category" class="btn">
		<% for(String category : grades) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" name='delete' value="Delete Grading Category" class="btn">
</form>
<%
} else {
%>
<h4>There are no grade categories to be deleted</h4>
<%
}
%>
<h2>Delete a Ranking Category</h2>
<%
if(ranks!=null&&ranks.size()>0) {
%>
<form action="removeRankCategory" method="POST">
	<select name="category" class="btn">
		<% for(String category : ranks) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" name='delete' value="Delete Ranking Category" class="btn">
</form>
<%
} else {
%>
<h4>There are no rank categories to be deleted</h4>
<%
}
%>
<h2>Delete a Compare Category</h2>
<%
if(compares!=null&&compares.size()>0) {
%>
<form action="removeCompareCategory" method="POST">
	<select name="category" class="btn">
		<% for(String category : compares) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" name='delete' value="Delete Ranking Category" class="btn">
</form>
<%
} else {
%>
<h4>There are no compare categories to be deleted</h4>
<%
}
%>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/remove_category.js" type="text/javascript"></script>

