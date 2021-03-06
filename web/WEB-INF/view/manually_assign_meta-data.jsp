<%-- 
    Document   : manually_assign_meta-data
    Created on : Jun 5, 2015, 1:13:14 PM
    Author     : aryner
--%>
<%@page import="metaData.*"%>
<%@page import="model.*"%>
<%@page import="java.util.*"%>
<%@page import="utilities.*"%>

<%
String photoNumber = (String)request.getAttribute("photoNumber");
Photo photo = (Photo)request.getAttribute("photo");
ArrayList<ManualMetaData> manualMetaData = (ArrayList)request.getAttribute("manualMetaData");
ArrayList<Photo> prevNext = (ArrayList)request.getAttribute("prevNext");
%>

<h1><%out.print(photo.getName());%></h1>
<p><a href="/Photo_Grader/assign_manual_meta">Back to the assign meta-data page</a></p>
<div class="meta-row">
<%
String src = Constants.SRC+"img?number="+photoNumber+"&name="+photo.getName();
out.print("<img class='gradeImg' src='"+src+"'>");
%>
<div class="newRow"></div>
<form action="setManualMetaData" method="POST">
	<input type="hidden" name="photo_id" value="<%out.print(photo.getId());%>">
	<input type="hidden" name="meta_count" value="<%out.print(manualMetaData.size());%>">
	<%
	int index = 0;
	for(ManualMetaData meta : manualMetaData) {
		out.print("<div class='meta-col question'>");
		out.print(meta.getHtml(photo,index));
		out.print("</div>");
		index++;
	}
	%>
	<div class="newRow"></div>
	<%
	if(prevNext.get(0) != null) {
		out.print("<div class='meta-col'><input class='btn' type='submit' name='prev' value='<- Submit and go to previous'></div>");
	}
	if(prevNext.get(1) != null) {
		out.print("<div class='meta-col'><input class='btn' type='submit' name='next' value='Submit and go to next ->'></div>");
	}
	%>
	<div class="newRow"></div>
	<div class="meta-col">
	<input type="submit" class='btn' name="submit" value="Submit">
	</div>
</form>
</div>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/manually_assign.js" type="text/javascript"></script>