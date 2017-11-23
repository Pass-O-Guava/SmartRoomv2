<%@ page language="java" contentType="text/html; charset=utf-8"%>
<%@ taglib prefix="s" uri="/struts-tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>控制界面</title>
</head>
<body>
<div align="center">
    <h2>Smart Light Detection</h2>
    <hr>
	<s:form action="start" method="post">
		<s:submit value="start" />
	</s:form>

	<s:form action="stop" method="post">
		<s:submit value="stop" />
	</s:form>

	<h2>${message}</h2>
</div>
</body>
</html>