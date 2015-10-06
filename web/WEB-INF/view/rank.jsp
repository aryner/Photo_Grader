<%-- 
    Document   : rank
    Created on : Oct 5, 2015, 10:13:10 AM
    Author     : aryner
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="metaData.grade.GradeGroup"%>
<%@page import="model.Rank"%>
<%@page import="model.Photo"%>
<%@page import="model.Rank.Pair"%>

<%
GradeGroup group = (GradeGroup)session.getAttribute("rank_group");
Pair pair = (Pair)request.getAttribute("rank_pair");
%>

<h1>Rank <%out.print(group.getName());%></h1>

<%
for (Photo photo : pair.getParent_photos()) {
	out.print(photo.getName()+"<br>");
}
out.print("<br>");
for (Photo photo : pair.getChild_photos()) {
	out.print(photo.getName()+"<br>");
}
%>

<!-- present two columns of photos, one for each group -->
<!-- somewhere convenient, place a from for choosing which is worse or if they are the same -->