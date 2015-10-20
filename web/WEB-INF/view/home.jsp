<%-- 
    Document   : home
    Created on : Mar 17, 2015, 4:36:08 PM
    Author     : aryner
--%>
<%@page import="java.util.*"%>

<h1>Home</h1>

<%
	User user = (User)session.getAttribute("user");
	Study study = (Study)session.getAttribute("study");

	ArrayList<String> errors = (ArrayList)session.getAttribute("errors");
	if(errors != null && !errors.isEmpty()) {
		out.print("<div class='error'><p>");
		for(String error : errors) out.print(error+"<br>");
		out.print("</p></div>");

		session.removeAttribute("errors");
	}
%>

<div class="container">
	<div class="meta-row">
		<div class="meta-col">
			<a href="upload" class="btn">Upload</a>
		</div>

		<div class="meta-col">
			<a href="define_grading_questions" class="btn">Define Grading Category</a>
		</div>
		<div class="meta-col">
			<a href="define_ranking" class="btn">Define Ranking Category</a>
		</div>
		<div class="meta-col">
			<a href="select_grade_category" class="btn">Grade</a>
		</div>
		<div class="meta-col">
			<a href="select_rank_category" class="btn">Rank</a>
		</div>
		<div class="meta-col">
			<a href="select_CSVs" class="btn">Get CSVs</a>
		</div>
		<%
		if(study.hasManualMetaData()) {
		%>
		<div class="meta-col">
			<a href="assign_manual_meta" class="btn">Assign Manual meta-data</a>
		</div>
		<%
		}
		%>
		<div class="meta-col">
			<a href="select_study" class="btn">Change Study</a>
		</div>
		<div class="meta-col">
			<a href="set_view_group" class="btn">View Uploaded Photos</a>
		</div>
	</div>
</div>
