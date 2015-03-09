<%-- 
    Document   : define_assignment
    Created on : Mar 3, 2015, 10:47:33 AM
    Author     : aryner
--%>

<%@page import="model.MetaData"%>
<%@page import="java.util.*"%>

<%
ArrayList<MetaData> metaData = (ArrayList)request.getAttribute("metaData");
String studyName = (String)request.getAttribute("studyName");
int name = 0, excel = 0, csv = 0, manual = 0;

for(MetaData datum : metaData) {
	switch(datum.getCollection()) {
		case MetaData.NAME :
			name++;
			break;
		case MetaData.EXCEL :
			excel++;
			break;
		case MetaData.CSV :
			csv++;
			break;
		case MetaData.MANUAL :
			manual++;
			break;
	}
}
%>

<h1>Specify details of meta-data collection...</h1>

<%
if(name > 0) {
%>
<div class="meta-row">
<h3>Specify how to extract meta-data from the photo name</h3>
<p>Enter them in the order they will appear in the name from left to right</p>
<%
	out.print("<div class='meta-col'>");
	out.print("<h4>Which type of meta-data?</h4>");
	out.print("<input type='radio' value='' name='name_1'> Not meta-data (use to help break name into sections)<br>");
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.NAME) {
			out.print("<input type='radio' value='"+datum.getName()+"' name='"+MetaData.NAME+"_1'> "+datum.getName()+"<br>");
		}
	}
	out.print("<br><input type='checkbox' name='specialCharacters' title='1'> Field contains special characters");
	out.print("</div>");

	out.print("<div class='meta-col'>");
	out.print("<h4>Where does this section start?</h4>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.START+"'> Begining of the file name<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.NUMBER+"' > After ");
	out.print("<input type='text'  class='small_text_box' name='start_"+MetaData.NUMBER+"_1'> characters (0 is the start, 1 is after the first character, etc...)<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.DELIMITER+"' > After ");
	out.print("<input type='text' class='small_text_box' name='start_"+MetaData.DELIMITER+"_1'> (Not including the delimiter character)<br>");
	//this response is only valid after the first section
	// I'm leaving it in as a comment to be used as template/example
//	out.print("<input type='radio' name='start_1' value='"+MetaData.AFTER+"'> Right after the end of the previous section");
	out.print("</div>");

	out.print("<div class='meta-col'>");
	out.print("<h4>Where does this section end?</h4>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.END+"'> The end of the file name (not including the extension)<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.NUMBER+"'> After ");
	out.print("<input type='text' name='end_"+MetaData.NUMBER+"_1' class='small_text_box'> characters from the start section");
	out.print(" (this number is the same as the length of this section in characters)<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.DELIMITER+"'> Before ");
	out.print("<input type='text' name='end_"+MetaData.DELIMITER+"_1' class='small_text_box'> ");
	out.print(" (Not including the delmiter character)<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.BEFORE+"'> Right before the start of the next section");
	out.print("</div>");

	//end of row
	out.print("</div>");
	//row showing example of what has been done so far
	out.print("<div class='meta-row'>");

	out.print("<div class=meta-col>");
	out.print("</div>");

	//end of row
	out.print("</div>");
}
if(excel > 0) {
%>
<h3>Specify how to extract excel meta-data</h3>

<%
}
if(csv > 0) {
%>
<h3>Specify how to extract CSV meta-data</h3>

<%
}
if(manual > 0) {
%>
<h3>Specify how to obtain manual meta-data</h3>

<%
}
%>


<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/define_assignment.js" type="text/javascript"></script>