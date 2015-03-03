<%-- 
    Document   : create_study
    Created on : Mar 2, 2015, 2:08:36 PM
    Author     : aryner
--%>

<%
String name = request.getParameter("name");
int number_fields = Integer.parseInt(request.getParameter("number"));
%>

<h1>Describe the meta-data of photos for <%out.print(name);%></h1>

<form action="createStudy" method="POST">
	<input type="hidden" name="studyName" value="<%out.print(name);%>">
<%
	for(int i=0; i<number_fields; i++) {
%>
<div>
		Meta-data descriptor(name) <input type="text" name="name<%out.print(i);%>">
</div>
<%
	}
%>
</form>