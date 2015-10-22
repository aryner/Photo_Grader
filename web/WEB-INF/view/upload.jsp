<%-- 
    Document   : upload
    Created on : Apr 6, 2015, 3:30:54 PM
    Author     : aryner
--%>
<%@page import="model.*"%>
<%@page import="metaData.TableMetaData"%>
<%@page import="SQL.Helper"%>
<%@page import="java.util.ArrayList"%>


<h1>Upload</h1>

<div class="container">
<h3>Upload Pictures</h3>
<p>Upload pictures to be graded</p>

<form action="upload_pictures" method="POST" enctype="multipart/form-data">
	<input type="file" multiple="multiple" name="pictures">
	<input type="submit" value="Upload" class="btn">
</form>
</div>

<%if(((Study)session.getAttribute("study")).usesTableMetaData()) {%>
	<div class="container">
	<h3>Upload Excel/CSV file</h3>
	<p>Upload a meta-data file (excel or csv)</p>

	<form action="upload_table_data" method="POST" enctype="multipart/form-data">
		<input type="file" name="file">
		<input type="submit" value="Upload" class="btn">
	</form>
	<%
	ArrayList<TableMetaData> excel = (ArrayList)request.getAttribute("excel_meta");
	ArrayList<TableMetaData> csv = (ArrayList)request.getAttribute("csv_meta");

	if(excel != null && excel.size() > 0) {
	%>
	<h4>Requirements for Excel files</h4>
	<%
		String head = "<th>"+excel.get(0).getIdentifier_col()+"</th>";
		String content = "<td>"+excel.get(0).getIdentifier()+"</td>";
		for(int i=0; i<excel.size(); i++) {
			head += "<th>"+Helper.unprocess(excel.get(i).getCol_name())+"</th>";
			content += "<td>"+Helper.unprocess(excel.get(i).getName())+"</td>";
		}
	%>
	<table class="excel">
		<tr><%out.print(head);%></tr>
		<tr><%out.print(content);%></tr>
	</table>
	<%}%>
	<%
	if(csv != null && csv.size() > 0) {
	%>
	<h4>Requirements for CSV files</h4>
	<%
		String head = "<th>"+csv.get(0).getIdentifier_col();
		String content = "<td>"+csv.get(0).getIdentifier();
		for(int i=0; i<csv.size(); i++) {
			head += ",</th> <th>"+Helper.unprocess(csv.get(i).getCol_name());
			content += ",</td> <td>"+Helper.unprocess(csv.get(i).getName());
		}
	%>
	<table class="csv">
		<tr><%out.print(head);%></tr>
		<tr><%out.print(content);%></tr>
	</table>
	<%}%>
	</div>
<%}%>