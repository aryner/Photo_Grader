<%-- 
    Document   : present_CSV
    Created on : May 8, 2015, 11:44:30 AM
    Author     : aryner
--%>
<%@page import="java.util.*"%>

<%
String category = (String)request.getAttribute("category");
ArrayList<String> lines = (ArrayList)request.getAttribute("csvLines");
if(lines == null) {
%>
<h3>This grading category has not yet been graded</h3>
<%
}
else {
%>
<form action="printCSV" method="POST">
	<input type="hidden" name="category" value="<%out.print(category);%>">
	<input type="submit" value="Print CSV to Desktop" class="btn">
	<input type='hidden' name='type' value='<%out.print(request.getAttribute("type")+"");%>'>
</form>
<p>
<%
lines.set(0,"<b>"+lines.get(0)+"</b>");
for(String line : lines) {
	out.print(line+"<br>");
}
%>
</p>
<%}%>