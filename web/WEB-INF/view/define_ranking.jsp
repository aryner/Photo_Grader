<%-- 
    Document   : define_ranking
    Created on : Sep 28, 2015, 11:50:24 AM
    Author     : aryner
--%>


<%@page import="java.util.*"%>
<%@page import="metaData.*"%>
<%@page import="metaData.grade.*"%>

<%
	ArrayList<String> columns = (ArrayList)request.getAttribute("columns");
	ArrayList<String> usedNames = (ArrayList)request.getAttribute("usedNames");
%>

<h1>Define Ranking Category</h1>

<form action="defineRanking" method="POST">
	<input type="hidden" name="used_count" value="<%out.print(usedNames.size());%>">
	<%
	for(int i=0; i<usedNames.size(); i++) {
		out.print("<input type='hidden' name='used_"+i+"' value='"+usedNames.get(i)+"'>");
	}
	%>
	<div class="container">
		<h3>Name this ranking category</h3>
		<input type="text" name="name">
		<h3>Group Pictures that share:</h3>
		<input type="checkbox" name="groupBy_-1" value="-1"> File name (checking this means each picture is ranked individually)
		<input type="hidden" name="questionCount" value="1">
		<input type="hidden" name="groupOptionCount" value="<%out.print(columns.size());%>">
		<%
		for(int i=0; i<columns.size(); i++) {
			out.print("<br><input type='checkbox' name='groupBy_"+i+"' value='"+i+"'> "+columns.get(i));
		}
		%>

		<h3>Include Random Repeats</h3>
		<input type="input" name="repeats" value="0" style="width:20px;" class="repeats"> 
		percent of the time graders will be given a randomly selected patient to re-rank.
	</div>

	<div class="errorDiv"></div>
	<input type="submit" value="Submit" class="btn">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/define_ranking_questions.js" type="text/javascript"></script>
		