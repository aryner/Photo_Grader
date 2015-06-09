<%-- 
    Document   : assign_manual_meta
    Created on : Jun 5, 2015, 10:54:21 AM
    Author     : aryner
--%>
<%@page import="java.util.*"%>
<%@page import="model.*"%>
<%@page import="metaData.*"%>

<h1>Assign meta data</h1>
<p>A *** next to a photo name indicates it is missing meta-data</p>

<%
ArrayList<Photo> photos = (ArrayList)request.getAttribute("photos");
ArrayList<ManualMetaData> manualMetaData = (ArrayList)request.getAttribute("manualMetaData");

for(Photo photo : photos) {
	out.print("<a href='manually_assign_meta-data?id="+photo.getId()+"'>");
	out.print((photo.hasMissingMetaData((ArrayList)manualMetaData)?"*** ":" &nbsp&nbsp&nbsp&nbsp&nbsp")+photo.getName()+" <br>");
	out.print("</a>");
}
%>
