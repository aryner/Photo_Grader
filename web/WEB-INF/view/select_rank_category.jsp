<%-- 
    Document   : select_rank_category
    Created on : Oct 5, 2015, 10:11:25 AM
    Author     : aryner
--%>

<%@page import="java.util.ArrayList"%>

<h1>Select Ranking category</h1>


<%
ArrayList<String> categories = (ArrayList)request.getAttribute("categories");
if(categories != null && categories.size() > 0) {
%>

<form action="startRanking" method="POST">
	<select name="category" class="btn">
		<% for(String category : categories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="Start Ranking" class="btn">
</form>
<% } else { %>
<h3>No ranking categories have been made yet</h3>
<% } %>