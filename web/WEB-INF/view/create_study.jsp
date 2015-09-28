<%-- 
    Document   : create_study
    Created on : Mar 2, 2015, 2:08:36 PM
    Author     : aryner
--%>
<%@page import="metaData.MetaData"%>

<%
String name = request.getParameter("name");
int number_fields = Integer.parseInt(request.getParameter("number"));
%>

<h1>Describe the meta-data of photos for <%out.print(name);%></h1>
<p class="sub-text">
	If there are not enough rows go back and enter a larger number.<br>
	If there are too many rows you can leave some blank.<br><br><br>
	<b>(The photo name will be saved as meta-data by default)</b>
</p>

<form action="define_assignment" method="GET">
	<input type="hidden" name="studyName" value="<%out.print(name);%>">
	<input type="hidden" name="maxCount" value="<%out.print(number_fields);%>">
<%
	for(int i=0; i<number_fields; i++) {
%>
<div class="meta-row">
	<div class="newRow error hidden" name="sameName<%out.print(i);%>" >You can't have duplicate names</div>
	<div class="meta-col">
		<span class="error hidden" name='<%out.print(i+1);%>'>Names must start with a letter and can only contain letters, <br>and numbers <br></span>
		<b>(<%out.print(i+1);%>)</b> Meta-data descriptor(name): <input type="text" name="name<%out.print(i);%>" autocomplete="off">
	</div>
	<div class="meta-col">
		How will the data be collected?<br>
		<input type="radio" name="collect<%out.print(i);%>" value="<%out.print(MetaData.NAME);%>">Photo name 
		<input type="radio" name="collect<%out.print(i);%>" value="<%out.print(MetaData.EXCEL);%>">Excel file 
		<input type="radio" name="collect<%out.print(i);%>" value="<%out.print(MetaData.CSV);%>">CSV 
		<input type="radio" name="collect<%out.print(i);%>" value="<%out.print(MetaData.MANUAL);%>">Manually entered 
	</div>
	<div class="meta-col error hidden" name="error<%out.print(i);%>">
		You must select a type of data and how it will be collected.<br>
		Or leave the descriptor section empty and this row will be skipped.
	</div>
</div>
<%
	}
%>
	<input type="submit" value="Submit" class="btn">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/createStudy.js" type="text/javascript"></script>