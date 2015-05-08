<%-- 
    Document   : select_CSVs
    Created on : May 8, 2015, 11:41:03 AM
    Author     : aryner
--%>
<%@page import="java.util.*"%>
<%@page import="model.*"%>

<h1>Select a grading category</h1>

<%
ArrayList<String> categories = (ArrayList)request.getAttribute("categories");
if(categories != null && categories.size() > 0) {
%>

<form action="present_CSV" method="GET">
	<select name="category" class="btn">
		<% for(String category : categories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="See CSV" class="btn">
</form>
<% } else { %>
<h3>No grading categories have been made yet</h3>
<% } %>