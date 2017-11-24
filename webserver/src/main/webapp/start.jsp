<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>CMRI: Smart Room</title>
</head>
<body>	
	<div align="center">
    <h2>Smart Room V2</h2>
    <h3>For Light/Temperature Detection and Control (OWL)<h3>
    <hr>
	<s:form action="sensor_detection_run" method="post">
		<s:submit value="sensor_detection_[Run!]" />
	</s:form>

	<s:form action="sensor_detection_stop" method="post">
		<s:submit value="sensor_detection_[Stop]" />
	</s:form>

	<h2>${message}</h2>
</div>
	
</body>
</html>