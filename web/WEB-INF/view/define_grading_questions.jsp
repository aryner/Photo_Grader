<%-- 
    Document   : define_grading_questions
    Created on : Apr 29, 2015, 1:43:06 PM
    Author     : aryner
--%>

<%@page import="java.util.*"%>
<%@page import="metaData.*"%>
<%@page import="metaData.grade.*"%>

<%
	ArrayList<String> columns = (ArrayList)request.getAttribute("columns");
	ArrayList<String> usedNames = (ArrayList)request.getAttribute("usedNames");
%>

<h1> Define Grading questions </h1>

<form action="defineGradingQuestions" method="POST">
	<input type="hidden" name="used_count" value="<%out.print(usedNames.size());%>">
	<%
	for(int i=0; i<usedNames.size(); i++) {
		out.print("<input type='hidden' name='used_"+i+"' value='"+usedNames.get(i)+"'>");
	}
	%>
	<div class="container">
		<h3>Name this grading category</h3>
		<input type="text" name="name">
		<h3>Group Pictures that share:</h3>
		<input type="checkbox" name="groupBy_-1" value="-1"> File name (checking this means each picture is graded individually)
		<input type="hidden" name="questionCount" value="1">
		<input type="hidden" name="groupOptionCount" value="<%out.print(columns.size());%>">
		<%
		for(int i=0; i<columns.size(); i++) {
			out.print("<br><input type='checkbox' name='groupBy_"+i+"' value='"+i+"'> "+columns.get(i));
		}
		%>

		<h3>Include Random Repeats</h3>
		<input type="input" name="repeats" value="0" style="width:20px;" class="repeats"> 
		percent of the time graders will be given a randomly selected patient to re-grade.

		<h3>Define questions</h3>
		<h3>Question 1</h3>
		<div class="meta-col">
			<h4>Question label</h4>
			<input type="text" name="label_0">
		</div>
		<div class="meta-col">
			<h4>Answer type</h4>
			<input type="radio" name="type_0" value="<%out.print(MetaData.RADIO);%>"> Radio 
			<input type="radio" name="type_0" value="<%out.print(MetaData.CHECKBOX);%>"> Check box
			<input type="radio" name="type_0" value="<%out.print(MetaData.TEXT);%>"> Text box
		</div>
		<div class="meta-col">
			<h4>Question</h4>
			<textarea name="question_0" cols="20" rows="1"></textarea>
		</div>
		<input type="hidden" name='option_count_0' value='0'>
		<div class="meta-col" name="options_0">
		</div>

		<div class="newRow"></div>

		<div class="meta-col">
			<h4>Constraints?</h4>
			<input type="radio" name="constraints_0" value="<%out.print(Question.MANDATORY);%>"> Mandatory 
			<input type="radio" name="constraints_0" value="<%out.print(Question.OPTIONAL);%>"> Optional
		</div>

		<div class="newRow"></div>

		<br><br><br><input type="checkbox" name="new_question_0"> Ask Another Question?

		<div class="newRow"></div>

		<div name="generated_sections">
		</div>
	</div>
	<div class="errorDiv"></div>
	<input type="submit" value="Submit" class="btn">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/define_grading_questions.js" type="text/javascript"></script>