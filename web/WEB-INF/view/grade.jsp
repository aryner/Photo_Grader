<%-- 
    Document   : grade
    Created on : May 5, 2015, 4:40:08 PM
    Author     : aryner
--%>
<%@page import="java.util.*"%>
<%@page import="metaData.grade.*"%>

<%
GradeGroup group = (GradeGroup)session.getAttribute("grade_group");
ArrayList<Photo> photoGroup = (ArrayList)request.getAttribute("photoGroup");
%>

<h1>Grade <%out.print(group.getName());%></h1>

<div class="meta-row">
	<%
	for(int i=0; i<photoGroup.size(); i++) {
		out.print(photoGroup.get(i).getName()+"<br>");
	}
	for(int i=0; i<group.questionSize(); i++) {
		out.print(group.getQuestion(i).getHtml());
	}
	%>
</div>