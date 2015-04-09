<%-- 
    Document   : define_assignment
    Created on : Mar 3, 2015, 10:47:33 AM
    Author     : aryner
--%>

<%@page import="metaData.MetaData"%>
<%@page import="utilities.ColorPicker"%>
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
<form action="defineAssignment" method="POST" name='assignmentDefinitions'>
	<input type='hidden' name='studyName' value='<%out.print(studyName);%>'>
	<input type='hidden' name='fieldsLength' value='<%out.print(metaData.size());%>'>
	<input type='hidden' name='nameCount' value='0'>
<%
for(int i=0; i<metaData.size(); i++) {
	//create hidden fields to show type (int, dec, string)
%>
	<input type='hidden' name='data_type_<%out.print(i);%>' value='<%out.print(metaData.get(i).getName()+"_"+metaData.get(i).getType());%>'>
<%
}

if(name > 0) {
%>
<h3>Specify how to extract meta-data from the photo name</h3>
<p>Enter them in the order they will appear in the name from left to right</p>
<%
	out.print("<div name='name-assignment' class='meta-row-container'>");
	out.print("<div class='meta-row'>");
	out.print("<div class='meta-col'>");
	out.print("<h4>(1) Which type of meta-data?</h4>");
	int index = 0;
	out.print("<input type='radio' value='_not-meta_' name='type_"+MetaData.NAME+"_1' title='"+index+"'> Not meta-data (use to help break name into sections)<br>");
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.NAME) {
			index++;
			out.print("<input type='radio' value='"+datum.getName()+"' name='type_"+MetaData.NAME+"_1' title='"+index+"'> "+datum.getName()+"<br>");
		}
	}
	out.print("</div>");

	out.print("<div class='meta-col'>");
	out.print("<h4>Where does this section start?</h4>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.START+"'> Begining of the file name<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.NUMBER+"' > After ");
	out.print("<input type='text'  class='small_text_box' name='start_"+MetaData.NUMBER+"_1'> characters (0 is the start, 1 is after the first character, etc...)<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.DELIMITER+"' > After ");
	out.print("<input type='text' class='small_text_box' name='start_"+MetaData.DELIMITER+"_1'> (Not including the delimiter character)<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.NEXT_NUMBER+"'> The next digit character<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.NEXT_LETTER+"'> The next letter character<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.NEXT_NOT_NUMBER+"'> The next character that is not a digit<br>");
	out.print("<input type='radio' name='start_1' value='"+MetaData.NEXT_NOT_LETTER+"'> The next character that is not a letter<br>");
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
	out.print("<input type='radio' name='end_1' value='"+MetaData.BEFORE+"'> Right before the start of the next section<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.NEXT_NUMBER+"'> The next digit character<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.NEXT_LETTER+"'> The next letter character<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.NEXT_NOT_NUMBER+"'> The next character that is not a digit<br>");
	out.print("<input type='radio' name='end_1' value='"+MetaData.NEXT_NOT_LETTER+"'> The next character that is not a letter<br>");
	out.print("</div>");

	//end of row
	out.print("</div>");
	//end of row container
	out.print("</div>");
	//row showing example of what has been done so far
	out.print("<div class='newRow'>");
	out.print("<div class='meta-row'>");

	out.print("<div class=meta-col>");
	out.print("<h4>Example key:</h4>");
	ColorPicker colorPicker = new ColorPicker();
	out.print("<span name='_not_meta_' style='background:white'></span>");
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.NAME) {
			out.print("<span name='"+datum.getName()+"' "+colorPicker.nextBackgroundColor()+">"+datum.getName()+"</span><br>");
		}
	}
	out.print("</div>");
	out.print("<div class=meta-col>");
	out.print("<h4>Enter an example photo name to check the results:</h4>");
	out.print("<input type='text' name='exampleInput' autocomplete='off'>");
	out.print("<p name='example'></p>");
	out.print("</div>");

	//end of row
	out.print("</div>");
}
if(excel > 0) {
%>
<h3>Specify how to extract excel meta-data</h3>
<%
	int excelCount = 0;
	out.print("<div class='meta-row-container'>");
%>
	<div class="meta-row">
		<div class="meta-col">
			<p><b>Identifying column</b></p>
			In order to use an excel document to provide photo <br>
			meta data it must have a column with values for each <br>
			photo that are unique to that photo or groups of photos.<br>
			<p class="note">(if you choose an attribute that groups of photos have <br>
			then all photos in that group will receive the same meta-data<br>
			from the excel file)</p>
			<input type="hidden" name="excel_0" value="">
		</div>
		<div class="meta-col">
			<p><b>Which piece of meta-data will <br>be the unique identifier?</b></p>
			<input type='radio' name='excelIdentifier' value='photo_name'>Photo file name<br>
<%
	for(MetaData datum : metaData) {
		out.print("<input type='radio' name='excelIdentifier' value='"+datum.getName()+"'>"+datum.getName()+"<br>");
	}
%>
		</div>
		<div class="meta-col">
			<p><b>Column name</b></p>
			<input type="text" name="excel_column_0">
		</div>
	</div>
	<div class="newRow"></div>
<%
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.EXCEL) {
			excelCount++;
%>
			<div class="meta-row">
				<div class="meta-col">
					<p><b><%out.print(datum.getName());%></b></p>
					<input type="hidden" name="excel_<%out.print(excelCount);%>" value="<%out.print(datum.getName());%>">
				</div>
				<div class="meta-col">
					<p><b>Column name</b></p>
					<input type="text" name="excel_column_<%out.print(excelCount);%>">
				</div>
			</div>

			<div class="newRow"></div>
<%
		}
	}
	out.print("<input type='hidden' name='excelCount' value='"+excelCount+"'>");
}
if(csv > 0) {
%>
<h3>Specify how to extract CSV meta-data</h3>
<%
	int csvCount = 0;
	out.print("<div class='meta-row-container'>");
%>
	<div class="meta-row">
		<div class="meta-col">
			<p><b>Identifying column</b></p>
			In order to use an CSV document to provide photo <br>
			meta data it must have a column with values for each <br>
			photo that are unique to that photo or groups of photos.<br>
			<p class="note">(if you choose an attribute that groups of photos have <br>
			then all photos in that group will receive the same meta-data<br>
			from the CSV file)</p>
			<input type="hidden" name="csv_0" value="">
		</div>
		<div class="meta-col">
			<p><b>Which piece of meta-data will <br>be the unique identifier?</b></p>
			<input type='radio' name='csvIdentifier' value='photo_name'>Photo file name<br>
<%
	for(MetaData datum : metaData) {
		out.print("<input type='radio' name='csvIdentifier' value='"+datum.getName()+"'>"+datum.getName()+"<br>");
	}
%>
		</div>
		<div class="meta-col">
			<p><b>Column name</b></p>
			<input type="text" name="csv_column_0">
		</div>
	</div>
	<div class="newRow"></div>
<%
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.CSV) {
			csvCount++;
%>
			<div class="meta-row">
				<div class="meta-col">
					<p><b><%out.print(datum.getName());%></b></p>
					<input type="hidden" name="csv_<%out.print(csvCount);%>" value="<%out.print(datum.getName());%>">
				</div>
				<div class="meta-col">
					<p><b>Column name</b></p>
					<input type="text" name="csv_column_<%out.print(csvCount);%>">
				</div>
			</div>

			<div class="newRow"></div>
<%
		}
	}
	out.print("<input type='hidden' name='csvCount' value='"+csvCount+"'>");
}
if(manual > 0) {
%>
<h3>Specify how to obtain manual meta-data</h3>
<%
	int manualCount = 0;
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.MANUAL) {
			manualCount++;
%>
			<input type="hidden" name="option_count_<%out.print(manualCount);%>" value="0">
			<input type="hidden" name='manual_name_<%out.print(manualCount);%>' value='<%out.print(datum.getName());%>'>
			<div class="meta-row" name="manual_<%out.print(manualCount);%>">
				<div class="meta-col">
					<p><b>(<%out.print(manualCount);%>)</b> Set <b><%out.print(datum.getName());%></b> with:</p>
					<input type="radio" name="manual_type_<%out.print(manualCount);%>" value="<%out.print(MetaData.TEXT);%>">Text<br>
					<input type="radio" name="manual_type_<%out.print(manualCount);%>" value="<%out.print(MetaData.RADIO);%>">Radio button<br>
					<input type="radio" name="manual_type_<%out.print(manualCount);%>" value="<%out.print(MetaData.CHECKBOX);%>">Check box<br>
				</div>
			</div>
			
			<div class="newRow"></div>
<%
		}
	}
	out.print("<input type='hidden' name='manualCount' value='"+manualCount+"'>");
}
%>
<div class="newRow"></div>
<div class="meta-row">
<div class="errorDiv meta-col error"></div>
</div>
<input type="submit" value="Submit" class="btn">
</form>

<script src="javascripts/jquery-1.11.1.min.js" type="text/javascript"></script>
<script src="javascripts/define_name_assignment.js" type="text/javascript"></script>
<script src="javascripts/define_manual_assignment.js" type="text/javascript"></script>