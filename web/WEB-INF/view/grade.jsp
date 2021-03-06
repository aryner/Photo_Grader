<%-- 
    Document   : grade
    Created on : May 5, 2015, 4:40:08 PM
    Author     : aryner
--%>
<%@page import="java.util.*"%>
<%@page import="model.Grade.GradeCounts"%>
<%@page import="metaData.grade.*"%>
<%@page import="utilities.*"%>

<%
GradeGroup group = (GradeGroup)session.getAttribute("grade_group");
ArrayList<Photo> photoGroup = (ArrayList)request.getAttribute("photoGroup");
String photoNumber = (String)request.getAttribute("photoNumber");
GradeCounts counts = (GradeCounts)request.getAttribute("gradeCounts");
Boolean repeat = (Boolean)request.getAttribute("repeat");

if(photoGroup.size()>0) {
%>

<div class='grade_rank_header'>
	<h1>Grade <%out.print(group.getName());%></h1>
	<progress value='<%out.print(counts.getGraded());%>' max='<%out.print((int)(counts.getTotal_grades()+(counts.getTotal_grades()*group.getRepeats()/100)));%>'>
	</progress>
	<!--
	<p>
		<%out.print(counts.getGraded()+"/"+counts.getTotal_grades());%>
	</p>
 	-->
</div>
<div class='newRow'></div>

<div class="meta-row">
	<form action="submitGrade" method="POST">
		<input type="hidden" name="repeat" value="<%out.print(repeat);%>">
		<input type="hidden" name="photo" value="<%out.print(photoGroup.get(0).getName());%>">
		<input type="hidden" name="questionCount" value="<%out.print(group.questionSize());%>">
		<input type="hidden" name="photoCount" value="<%out.print(photoGroup.size());%>">
		<%
		for(int i=0; i<photoGroup.size(); i++) {
			String src = Constants.SRC+"img?number="+photoNumber+"&name="+photoGroup.get(i).getName();
			out.print("<img class='gradeImg' name='photo_"+i+"' src='"+src+"'>");
		}
		%>
		<div class="newRow"></div>
		<%
		for(int i=0; i<group.questionSize(); i++) {
			Question question = group.getQuestion(i);
			out.print(question.getHtml(i));
			out.print("<input type='hidden' id='"+question.getLabel()+"' name='question_"+i+
				  "' title='"+question.getQ_type()+"_"+question.getConstraints()+"' value='"+question.optionSize()+"'>");
		}
		%>
		<div class="newRow"></div>
		<div class="meta-col">
			<input type="submit" value="Submit" class="btn">
		</div>
		<div class="newRow"></div>
		<div class="meta-col errorDiv"> </div>
	</form>
</div>
<%
} else {
%>
<h3>You have graded all available groups in this category</h3>
<%
}
%>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/grade.js" type="text/javascript"></script>