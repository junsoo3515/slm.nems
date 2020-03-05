<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="p" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<html style="height:100%">
<head>
    <meta charset="utf-8" />
    <meta http-equiv="X-UA-Compatible" content="IE=edge"/>
    <script src="${ozReportUrl}/ozhviewer/jquery-1.8.3.min.js"></script>
    <link rel="stylesheet" href="${ozReportUrl}/ozhviewer/jquery-ui-1.9.2.css" type="text/css"/>
    <script src="${ozReportUrl}/ozhviewer/jquery-ui-1.9.2.min.js"></script>
    <link rel="stylesheet" href="${ozReportUrl}/ozhviewer/ui.dynatree.css" type="text/css"/>
    <script type="text/javascript" src="${ozReportUrl}/ozhviewer/jquery.dynatree.js" charset="utf-8"></script>
    <script type="text/javascript" src="${ozReportUrl}/ozhviewer/OZJSViewer.js" charset="utf-8"></script>
</head>
<body style="margin:0;">
<div id="OZViewer"></div>
<script type="text/javascript">

    if (${isSuccess} === false) {

        alert('비정상적인 접근을 하여 강제로 종료합니다.');
        window.close();
    }

    function SetOZParamters_OZViewer() {
        var oz;
        oz = document.getElementById("OZViewer");
        oz.sendToActionScript("connection.servlet","${ozReportUrl}/server");
        oz.sendToActionScript("connection.reportname","/${ozReportname}");
        oz.sendToActionScript("odi.odinames", "${ozOdinames}");


        <c:set var="arrOdi" value="${fn:split(ozOdinames,',')}" />

        <c:forEach var="val" items="${arrOdi}" varStatus="i">

            oz.sendToActionScript("odi.${val}.pcount", "${ozPgcount}");

            <t:useAttribute id="ozArgs" name="ozArgs" classname="java.util.List" ignore="true"/>

            <c:forEach items="${ozArgs}" var="item" varStatus="j">
                oz.sendToActionScript("odi.${val}.args${j.count}", "${item}");
            </c:forEach>

            oz.sendToActionScript("odi.${val}.clientdmtype", "Memory");
            oz.sendToActionScript("odi.${val}.serverdmtype", "Memory");
            oz.sendToActionScript("odi.${val}.fetchtype", "Concurrent");
        </c:forEach>

        return true;
    }

    start_ozjs("OZViewer","${ozReportUrl}/ozhviewer/");
</script>
</body>
</html>