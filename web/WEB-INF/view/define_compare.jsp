<%-- 
    Document   : define_inter_class_ranking
    Created on : Oct 27, 2015, 11:43:51 AM
    Author     : aryner
--%>


<%@page import="java.util.*"%>
<%@page import="metaData.*"%>
<%@page import="metaData.grade.*"%>

<%
	ArrayList<String> columns = (ArrayList)request.getAttribute("columns");
	ArrayList<String> usedNames = (ArrayList)request.getAttribute("usedNames");
%>

<h1>Define Compare Category</h1>

<form action="defineCompare" method="POST">
	<input type="hidden" name="used_count" value="<%out.print(usedNames.size());%>">
	<%
	for(int i=0; i<usedNames.size(); i++) {
		out.print("<input type='hidden' name='used_"+i+"' value='"+usedNames.get(i)+"'>");
	}
	%>
	<div class="container">
		<div class="meta-row">
			<h3>Name this compare category</h3>
			<input type="text" name="name">
		</div>
		<div class="newRow"></div>
		<div class="meta-row">
			<div class="meta-col">
				<h3>Group Pictures that share:</h3>
				<input type="hidden" name="questionCount" value="1">
				<input type="hidden" name="groupOptionCount" value="<%out.print(columns.size());%>">
				<%
				for(int i=0; i<columns.size(); i++) {
					out.print("<br><input type='checkbox' name='groupBy_"+i+"' value='"+i+"'> "+columns.get(i));
				}
				%>
			</div>
		</div>
		<div class="newRow"></div>
		<div class="meta-row">
			<div class="meta-col">
				<h3>Select field to compare between</h3>
				<%
				for(int i=0; i<columns.size(); i++) {
					out.print("<br><input type='radio' name='compare_between' value='"+i+"'> "+columns.get(i));
				}
				%>
			</div>
		</div>
		<div class="newRow"></div>
		<div class="meta-row" id="low">
			<div class="meta-col">
				<h3>Select the values to compare</h3>
				<h4>Ideal low end</h4>
				<input type="text" name="low_0" style="margin-right:20px;"> Next Option? <input type="checkbox" name="low_opt_0">
				<input type="hidden" name="low_count" value="0">
			</div>
		</div>
		<div class="newRow"></div>
		<div class="meta-row" id="high">
			<div class="meta-col">
				<h4>Ideal high end</h4>
				<input type="text" name="high_0" style="margin-right:20px;"> Next Option? <input type="checkbox" name="high_opt_0">
				<input type="hidden" name="high_count" value="0">
			</div>
		</div>
	</div>

	<div class="errorDiv"></div>
	<input type="submit" value="Submit" class="btn">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/define_compare_questions.js" type="text/javascript"></script>
		