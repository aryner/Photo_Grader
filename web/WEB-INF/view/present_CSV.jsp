<%-- 
    Document   : present_CSV
    Created on : May 8, 2015, 11:44:30 AM
    Author     : aryner
--%>
<%@page import="java.util.*"%>

<%
String category = (String)request.getAttribute("category");
ArrayList<String> lines = (ArrayList)request.getAttribute("csvLines");
%>
<form method="printCSV" method="POST">
	<input type="hidden" name="category" value="<%out.print(category);%>">
	<input type="submit" value="Print CSV to Desktop" class="btn">
</form>
<p>
<%
lines.set(0,"<b>"+lines.get(0)+"</b>");
for(String line : lines) {
	out.print(line+"<br>");
}
%>
</p>