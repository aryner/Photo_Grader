<%-- 
    Document   : rank
    Created on : Oct 5, 2015, 10:13:10 AM
    Author     : aryner
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="metaData.grade.GradeGroup"%>

<%
GradeGroup group = (GradeGroup)session.getAttribute("rank_group");
%>

<h1>Rank <%out.print(group.getName());%></h1>