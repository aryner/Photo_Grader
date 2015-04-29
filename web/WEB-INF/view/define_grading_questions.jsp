<%-- 
    Document   : define_grading_questions
    Created on : Apr 29, 2015, 1:43:06 PM
    Author     : aryner
--%>

<%@page import="java.util.*"%>

<%
	ArrayList<String> columns = (ArrayList)request.getAttribute("columns");

%>


<h1> Define Grading questions </h1>

<form action="defineGradingQuestions" method="POST">
	<div class="container">
		<h3>Grade Pictures that share:</h3>
		<input type="checkbox" name="groupBy_-1" value="-1"> File name (checking this means each picture is graded individually)
		<%
		for(int i=0; i<columns.size(); i++) {
			out.print("<br><input type='checkbox' name='groupBy_"+i+"' value='"+i+"'> "+columns.get(i));
		}
		%>

		<h3>Define questions</h3>
		<div class="meta-col">
			<h4>Answer type</h4>
			<input type="radio" name="type_0" value="radio"> Radio 
			<input type="radio" name="type_0" value="checkbox"> Check box
			<input type="radio" name="type_0" value="text"> Text box
		</div>
		<div class="meta-col">
			<h4>Question</h4>
			<textarea name="question" cols="40" rows="1"></textarea>
		</div>

		<div class="newRow"></div>
	</div>
	<input type="submit" value="Submit" class="btn">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/define_grading_questions.js" type="text/javascript"></script>