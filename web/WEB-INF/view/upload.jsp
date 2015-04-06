<%-- 
    Document   : upload
    Created on : Apr 6, 2015, 3:30:54 PM
    Author     : aryner
--%>
<%@page import="model.*"%>

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
	</div>
<%}%>