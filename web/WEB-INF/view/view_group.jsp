<%-- 
    Document   : view_group
    Created on : Oct 20, 2015, 10:27:48 AM
    Author     : aryner
--%>
<%@page import="java.util.ArrayList"%>
<%@page import="model.Photo"%>
<%@page import="utilities.Constants"%>

<a href="view_groups">Go back to list of groups</a>

<%
int index = (Integer)request.getAttribute("index");
int groupsSize = (Integer)request.getAttribute("groupsSize");
String photo_table = request.getAttribute("photo_table_num")+"";
String photo_table_num = photo_table.substring(photo_table.lastIndexOf("_")+1);
ArrayList<Photo> photos = (ArrayList)request.getAttribute("photos");

for(int i=0; i<photos.size(); i++) {
	if(i%3==0) { out.print("<div class='newRow'></div>"); }
	out.print("<div class='group_view_col'>");
	out.print("<img class='group_view_img' src='"+Constants.SRC+"img?number="+photo_table_num+"&name="+photos.get(i).getName()+"'>");
%>
<br>
<form action="removePhoto" method="POST">
	<input type="submit" value="Delete Image From Program" class="btn">
</form>
</div>
<%
}
%>
<div class="newRow"></div>
<%if(index>0) {%>
<form action="view_group" method="GET" class="group_left_right_btn">
	<input type="hidden" name="index" value="<%out.print(index-1);%>">
	<input type="submit" value="Previous Group" class="btn">
</form>
<%
} 
if (index < groupsSize-1) {
%>
<form action="view_group" method="GET" class="group_left_right_btn">
	<input type="hidden" name="index" value="<%out.print(index+1);%>">
	<input type="submit" value="Next Group" class="btn">
</form>
<%
}
%>
