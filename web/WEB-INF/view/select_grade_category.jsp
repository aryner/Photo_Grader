<%-- 
    Document   : select_grade_category
    Created on : May 5, 2015, 4:20:28 PM
    Author     : aryner
--%>
<%@page import="java.util.*"%>
<%@page import="model.*"%>

<h1>Select a grading category</h1>

<%
ArrayList<String> categories = (ArrayList)request.getAttribute("categories");
if(categories != null && categories.size() > 0) {
%>

<form action="grade" method="POST">
	<select name="category" class="btn">
		<% for(String category : categories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="Start Grading" class="btn">
</form>
<% } else { %>
<h3>No grading categories have been made yet</h3>
<% } %>