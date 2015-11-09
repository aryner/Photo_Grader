<%-- 
    Document   : select_CSVs
    Created on : May 8, 2015, 11:41:03 AM
    Author     : aryner
--%>
<%@page import="java.util.*"%>
<%@page import="model.*"%>

<h1>Select a grading category</h1>

<%
ArrayList<String> gradeCategories = (ArrayList)request.getAttribute("gradeCategories");
ArrayList<String> rankCategories = (ArrayList)request.getAttribute("rankCategories");
ArrayList<String> compareCategories = (ArrayList)request.getAttribute("compareCategories");
%>
<h2>Grade CSVs</h2>
<%
if(gradeCategories != null && gradeCategories.size() > 0) {
%>

<form action="present_CSV" method="GET">
	<input type="hidden" name="type" value="grade">
	<select name="category" class="btn">
		<% for(String category : gradeCategories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="See CSV" class="btn">
</form>
<% } else { %>
<h3>No grading categories have been made yet</h3>
<% } 
%>
<h2>Rank CSVs</h2>
<%
if(rankCategories != null && rankCategories.size() > 0) {
%>

<form action="present_CSV" method="GET">
	<input type="hidden" name="type" value="rank">
	<select name="category" class="btn">
		<% for(String category : rankCategories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="See CSV" class="btn">
</form>
<% } else { %>
<h3>No ranking categories have been made yet</h3>
<% } %>
<h2>Compare CSVs</h2>
<%
if(compareCategories != null && compareCategories.size() > 0) {
%>

<form action="present_CSV" method="GET">
	<input type="hidden" name="type" value="compare">
	<select name="category" class="btn">
		<% for(String category : compareCategories) { %>
			<option value="<%out.print(category);%>"><%out.print(category);%></option>
		<% } %>
	</select>
	<input type="submit" value="See CSV" class="btn">
</form>
<% } else { %>
<h3>No compare categories have been made yet</h3>
<% } %>
