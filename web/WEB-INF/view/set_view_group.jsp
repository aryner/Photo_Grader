<%-- 
    Document   : view_photos
    Created on : Oct 19, 2015, 3:29:41 PM
    Author     : aryner
--%>
<%@page import="java.util.ArrayList"%>


<%
	ArrayList<String> columns = (ArrayList)request.getAttribute("columns");
%>

<h1>Select how you want to group photos to view</h1>
<p>Select only 'File name' if you want to see each photo individually</p>

<form action="selectGrouping" method="POST">
	<div class="container">
		<h3>Group Pictures that share:</h3>
		<input type="checkbox" name="groupBy_-1" value="-1"> File name
		<input type="hidden" name="questionCount" value="1">
		<input type="hidden" name="groupOptionCount" value="<%out.print(columns.size());%>">
		<%
		for(int i=0; i<columns.size(); i++) {
			out.print("<br><input type='checkbox' name='groupBy_"+i+"' value='"+i+"'> "+columns.get(i));
		}
		%>
	</div>

	<div class="errorDiv"></div>
	<input type="submit" value="Submit" class="btn">
</form>