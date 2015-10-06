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
<%@page import="utilities.Constants"%>

<%
GradeGroup group = (GradeGroup)session.getAttribute("rank_group");
Pair pair = (Pair)request.getAttribute("rank_pair");
String photo_table = (String)request.getAttribute("photo_table");
String photo_table_num = photo_table.substring(photo_table.lastIndexOf("_")+1);
%>

<h1>Rank <%out.print(group.getName());%></h1>

<div class="rank_col">
<%
for (Photo photo : pair.getParent_photos()) {
	out.print("<img class='Img' src='"+Constants.SRC+"img?number="+photo_table_num+"&name="+photo.getName()+"'>");
}
%>
</div>
<div class="rank_form">
	<p> Placeholder for ranking form </p>
</div>
<div class="rank_col">
<%
out.print("<br>");
for (Photo photo : pair.getChild_photos()) {
	out.print("<img class='Img' src='"+Constants.SRC+"img?number="+photo_table_num+"&name="+photo.getName()+"'>");
}
%>
</div>
