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
String high_rank = request.getAttribute("high_rank")+"";
String low_rank = request.getAttribute("low_rank")+"";

String photo_table = (String)request.getAttribute("photo_table");
String photo_table_num = photo_table.substring(photo_table.lastIndexOf("_")+1);
int photoCount = 0;
%>

<h1>Rank <%out.print(group.getName());%></h1>

<div class="rank_col">
<%
if (pair.isFull()) {
	for (Photo photo : pair.getParent_photos()) {
		out.print("<img class='Img' name='photo_"+photoCount+"' src='"+Constants.SRC+"img?number="+photo_table_num+"&name="+photo.getName()+"'>");
		photoCount++;
	}
%>
</div>
<div class="rank_form">
	<form action="submitRank" method="POST">
		<input type="hidden" name="left_rank" value="<%out.print(pair.getParent().getId());%>">
		<input type="hidden" name="right_rank" value="<%out.print(pair.getChild().getId());%>">
		<input type="hidden" name="last_compared_rank" value="<%out.print(pair.getParent().getRank());%>">
		<input type="hidden" name="high_rank" value="<%out.print(high_rank);%>">
		<input type="hidden" name="low_rank" value="<%out.print(low_rank);%>">
		<label>Which is worse?</label>
		<ul>
			<li><input type="radio" name="compare" value="left">Left</li>
			<li><input type="radio" name="compare" value="equal">Equal</li>
			<li><input type="radio" name="compare" value="right">Right</li>
		</ul>
		<input type="submit" value="Submit" class="btn">
		<div class="errorDiv"></div>

</div>
<div class="rank_col">
<%
	for (Photo photo : pair.getChild_photos()) {
		out.print("<img class='Img' name='photo_"+photoCount+"' src='"+Constants.SRC+"img?number="+photo_table_num+"&name="+photo.getName()+"'>");
		photoCount++;
	}
%>
		<input type="hidden" name="photoCount" value="<%out.print(photoCount);%>">
	</form>
</div>
<%
} else {
%>
<h3>You have finished ranking this group</h3>
<%
}
%>


<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/rank.js" type="text/javascript"></script>
