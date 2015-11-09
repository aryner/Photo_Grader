<%-- 
    Document   : compare
    Created on : Nov 4, 2015, 1:33:21 PM
    Author     : aryner
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="model.Photo"%>
<%@page import="model.Compare"%>
<%@page import="model.Compare.CompareCounts"%>
<%@page import="metaData.grade.GradeGroup"%>
<%@page import="utilities.Constants"%>

<%
Compare compare = (Compare)request.getAttribute("compare");
GradeGroup group = (GradeGroup)request.getAttribute("group");
CompareCounts counts = (CompareCounts)request.getAttribute("counts");
String photo_table = (String)request.getAttribute("photo_table");
String photo_table_num = photo_table.substring(photo_table.lastIndexOf("_")+1);
%>

<div class='grade_rank_header'>
	<h1>Rank <%out.print(group.getName());%></h1>
	<progress value='<%out.print(counts.getCompared());%>' max='<%out.print(counts.getTotal_compares());%>'> </progress>
</div>
<div class='newRow'></div>

<div class="rank_col">
<%
if (compare != null) {
	boolean lowFirst = Math.random() > 0.5;
	ArrayList<Photo> left;
	ArrayList<Photo> right;
	if (lowFirst) {
		left = compare.getLow_photos();
		right = compare.getHigh_photos();
	} else {
		right = compare.getLow_photos();
		left = compare.getHigh_photos();
	}
	int photoCount = 0;
	if(lowFirst) { out.print(compare.getLow()); }
	else { out.print(compare.getHigh()); }
	for (Photo photo : left) {
		out.print("<img class='Img' name='photo_"+photoCount+"' src='"+Constants.SRC+"img?number="+photo_table_num+"&name="+photo.getName()+"'>");
		photoCount++;
	}
%>
</div>

<div class="rank_form">
	<form action="submitCompare" method="POST">
		<input type="hidden" id="back_pressed" name="back_pressed" value="0"/>
		<input type="hidden" name="compare_id" value="<%out.print(compare.getId());%>"/>
		<label>Which is worse?</label>
		<ul>
			<li><input type="radio" name="compare" value="<%out.print(lowFirst?"low_worse":"high_worse");%>">Left <b>(s)</b></li>
			<li><input type="radio" name="compare" value="equal">Equal <b>(d)</b></li>
			<li><input type="radio" name="compare" value="<%out.print(lowFirst?"high_worse":"low_worse");%>">Right <b>(f)</b></li>
		</ul>
		<input type="submit" value="Submit" class="btn"><b>(t)</b>
		<div class="errorDiv"></div>

</div>
<div class="rank_col">
<%
	if(!lowFirst) { out.print(compare.getLow()); }
	else { out.print(compare.getHigh()); }
	for (Photo photo : right) {
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
<h3>You have finished comparing this group</h3>
<%
}
%>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/compare.js" type="text/javascript"></script>

