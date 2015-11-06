<%-- 
    Document   : select_compare_category
    Created on : Nov 4, 2015, 1:27:12 PM
    Author     : aryner
--%>


<%@page import="java.util.ArrayList"%>

<h1>Select Compare category</h1>


<%
ArrayList<String> categories = (ArrayList)request.getAttribute("categories");
if(categories != null && categories.size() > 0) {
%>

<form action="startComparing" method="POST">
	<select name="category" class="btn">
		<% for(String category : categories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="Start Comparing" class="btn">
</form>
<% } else { %>
<h3>No ranking categories have been made yet</h3>
<% } %>
