<%-- 
    Document   : admin_page
    Created on : Oct 27, 2015, 3:54:43 PM
    Author     : aryner
--%>

<%@page import="java.util.ArrayList"%>
<%@page import="model.User"%>

<h1>Admin Page</h1>

<h4>Assign user privileges</h4>
<form action="updatePrivileges" method="POST">
<table class="user_table">
	<tr>
		<th>User Name</th>
		<th>Grader</th>
		<th>Study Coordinator</th>
		<th>Admin</th>
	</tr>
<%
ArrayList<User> users = (ArrayList) request.getAttribute("users");
int index = 0;
for (User user : users) {
%>
	<tr>
		<input type="hidden" name="name_<%out.print(index);%>" value="<%out.print(user.getName());%>">
		<input type='hidden' name='id_<%out.print(index);%>' value='<%out.print(user.getId());%>'>
		<td><%out.print(user.getName());%></td>
		<td><input type="checkbox" name="grader_<%out.print(index);%>" title="<%out.print(user.getId());%>" <%if(user.isGrader()){out.print("checked='true'");}%>></td>
		<td><input type="checkbox" name="study_coordinator_<%out.print(index);%>" title="<%out.print(user.getId());%>" <%if(user.isStudy_coordinator()){out.print("checked='true'");}%>></td>
		<td><input type="checkbox" name="admin_<%out.print(index);%>" title="<%out.print(user.getId());%>" <%if(user.isAdmin()){out.print("checked='true'");}%>></td>
	</tr>
<%
		index++;
}
%>
	<input type="hidden" name="user_count" value="<%out.print(users.size());%>">
</table>
<input type="submit" value="Make Changes" name='makeChanges' class="btn">
<%
User user = (User)request.getAttribute("user");
if(user.isStudy_coordinator() || user.isGrader()){
%>
	<a href="select_study" class="btn">Select Study</a>
<%
}
%>
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/admin_page.js" type="text/javascript"></script>

