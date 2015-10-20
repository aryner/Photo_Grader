<%-- 
    Document   : view_group
    Created on : Oct 19, 2015, 3:46:30 PM
    Author     : aryner
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.Photo"%>

<h1>Select a group to View</h1>

<%
ArrayList<Photo> groups = (ArrayList)request.getAttribute("groups");
ArrayList<String> groupOptions = (ArrayList)request.getAttribute("groupOptions");

for(Photo group : groups) {
	out.print(group.getOptionValues(groupOptions)+"<br>");
}
%>