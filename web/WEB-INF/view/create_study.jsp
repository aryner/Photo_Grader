<%-- 
    Document   : create_study
    Created on : Mar 2, 2015, 2:08:36 PM
    Author     : aryner
--%>
<%@page import="model.MetaData"%>

<%
String name = request.getParameter("name");
int number_fields = Integer.parseInt(request.getParameter("number"));
%>

<h1>Describe the meta-data of photos for <%out.print(name);%></h1>
<p class="sub-text">
	If there are not enough rows go back and enter a larger number.<br>
	If there are too many rows leave some blank.<br><br><br>
	<b>(The photo name will be saved as meta-data by default)</b>
</p>

<form action="defineAssignment" method="POST">
	<input type="hidden" name="studyName" value="<%out.print(name);%>">
<%
	for(int i=0; i<number_fields; i++) {
%>
<div class="meta-row">
	<div class="meta-col">
		<b>(<%out.print(i+1);%>)</b> Meta-data descriptor(name): <input type="text" name="name<%out.print(i);%>">
	</div>
	<div class="meta-col">
		Type of data:<br>
		Integer <input type="radio" name="type<%out.print(i);%>" value="<%out.print(MetaData.INTEGER);%>">
		Decimal <input type="radio" name="type<%out.print(i);%>" value="<%out.print(MetaData.DECIMAL);%>">
		String <input type="radio" name="type<%out.print(i);%>" value="<%out.print(MetaData.STRING);%>">
	</div>
	<div class="meta-col">
		How will the data be collected?<br>
		Photo name <input type="radio" name="collect" value="<%out.print(MetaData.NAME);%>">
		Excel file <input type="radio" name="collect" value="<%out.print(MetaData.EXCEL);%>">
		CSV <input type="radio" name="collect" value="<%out.print(MetaData.CSV);%>">
		Manually entered <input type="radio" name="collect" value="<%out.print(MetaData.MANUAL);%>">
	</div>
</div>
<%
	}
%>
	<input type="submit" value="Submit" class="btn">
</form>