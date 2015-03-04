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
<h3>Specify how to extract meta-data from the photo name</h3>
<%
	for(MetaData datum : metaData) {
		if(datum.getCollection() == MetaData.NAME) {
		}
	}
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