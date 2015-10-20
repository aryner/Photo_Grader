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

for(int i=0; i<groups.size(); i++) {
	out.print("<a href='view_group?index="+i+"'>");
	out.print(groups.get(i).getOptionValues(groupOptions));
	out.print("</a><br>");
}
%>
