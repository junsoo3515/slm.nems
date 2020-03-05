<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles" prefix="t"%>
<%@ taglib uri="http://tiles.apache.org/tags-tiles-extras" prefix="tilesx" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<c:set var="p" value="${pageContext.request.contextPath}" />

<!DOCTYPE html>
<!--[if IE 8]>
<html class="ie8">
<![endif]-->
<!--[if !IE]><!-->
<html>
<head>
    <meta charset="utf-8" />
    <title><t:getAsString name="title" /></title>
    <meta http-equiv="X-UA-Compatible" content="IE=edge,chrome=1">
    <meta content="width=device-width, initial-scale=1.0, maximum-scale=1.0, user-scalable=no" name="viewport" />
    <meta content="" name="description" />
    <meta content="" name="author" />

    <!-- ================== BEGIN BASE CSS STYLE ================== -->
    <link href="${p}/res/assets/css/font.css" rel="stylesheet">
    <link href="${p}/res/assets/plugins/jquery-ui/themes/base/minified/jquery-ui.min.css" rel="stylesheet" />
    <link href="${p}/res/assets/plugins/bootstrap/css/bootstrap.min.css" rel="stylesheet" />
    <link href="${p}/res/assets/plugins/font-awesome/css/font-awesome.min.css" rel="stylesheet" />
    <link href="${p}/res/assets/css/animate.min.css" rel="stylesheet" />
    <link href="${p}/res/assets/css/style.min.css" rel="stylesheet" />
    <link href="${p}/res/assets/css/style-responsive.min.css" rel="stylesheet" />
    <link href="${p}/res/assets/css/theme/default.css" rel="stylesheet" id="theme" />
    <!-- ================== END BASE CSS STYLE ================== -->

    <!-- StyleSheet Plugins -->
	<tilesx:useAttribute id="cssPlugin" name="cssPlugin" classname="java.util.List" ignore="true"/>
	<c:forEach items="${cssPlugin}" var="item">

        <c:choose>
            <c:when test="${fn:contains(item, 'noscript')}">
                <noscript><link rel="stylesheet" type="text/css" href="${p}/res${item}"></noscript>
            </c:when>
            <c:otherwise>
                <link rel="stylesheet" type="text/css" href="${p}/res${item}">
            </c:otherwise>
        </c:choose>
    </c:forEach>
</head>
<body class="pace-top">
    <!-- begin #page-loader -->
    <div id="page-loader" class="fade in"><span class="spinner"></span></div>
    <!-- end #page-loader -->

    <t:insertAttribute name="body" />

    <div id="targetFrame"></div>

    <!-- ================== BEGIN BASE JS ================== -->
    <script src="${p}/res/plugins/jquery-2.1.3/jquery-2.1.3.min.js"></script>

    <!--[if lt IE 9]>
    <script src="${p}/res/assets/crossbrowserjs/html5shiv.js"></script>
    <script src="${p}/res/assets/crossbrowserjs/respond.min.js"></script>
    <script src="${p}/res/assets/crossbrowserjs/excanvas.min.js"></script>
    <![endif]-->
    <script src="${p}/res/assets/plugins/slimscroll/jquery.slimscroll.min.js"></script>
    <script src="${p}/res/assets/plugins/jquery-cookie/jquery.cookie.js"></script>
    <!-- ================== END BASE JS ================== -->

    <script type="text/javascript" src="${p}/res/plugins/require-v2.1.17/require.min.js"></script>
    <script type="text/javascript" data-root="${p}/res" src="${p}/res/script/master/pattern.default.js"></script>

    <script type="text/javascript" src="${p}/res/plugins/rsa/js/rsa.js"></script>
    <script type="text/javascript" src="${p}/res/plugins/rsa/js/jsbn.js"></script>
    <script type="text/javascript" src="${p}/res/plugins/rsa/js/prng4.js"></script>
    <script type="text/javascript" src="${p}/res/plugins/rsa/js/rng.js"></script>

    <!-- Javascript Plugins(jsScript 부분에 js 파일 대신 Script 구문을 넣게 변경 가능하면 하단으로 배치 변경) -->
    <tilesx:useAttribute id="jsPlugin" name="jsPlugin" classname="java.util.List" ignore="true" />
    <c:forEach items="${jsPlugin}" var="item">
        <script type="text/javascript" src="${p}/res/script${item}"></script>
    </c:forEach>

    <script type="text/javascript" src="${p}/res/assets/js/apps.min.js"></script>

    <script>
        $(document).ready(function() {
            //App.init();
            //LoginV2.init();
        });
    </script>
</body>
</html>