<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false" %>

<div class="login-cover">
    <div class="login-cover-image"><img src="${p}/res/assets/img/login-bg/bg-5.jpg" data-id="login-cover-image" alt="" /></div>
    <div class="login-cover-bg"></div>
</div>
<!-- begin #page-container -->
<div id="page-container" class="fade">
    <!-- begin login -->
    <div class="login login-v2" data-pageload-addclass="animated fadeIn">
        <!-- begin brand -->
        <div class="login-header">
            <div class="brand">
                <span class="logo"></span> 파주시 SLM 시스템
                <small>System Level Management System</small>
            </div>
            <div class="icon">
                <i class="fa fa-sign-in"></i>
            </div>
        </div>
        <!-- end brand -->
        <div id="loginPanel" class="login-content">
            <div class="form-group m-b-20">
                <input type="text" id="memID" name="memID" class="form-control input-lg" placeholder="아이디를 입력해 주세요" msg="아이디를" />
                <label for="memID" id="memIDHelp" class="control-label"></label>
            </div>
            <div class="form-group m-b-20">
                <input type="password" id="memPWD" name="memPWD" class="form-control input-lg" placeholder="암호를 입력해 주세요" msg="암호를" />
                <label for="memPWD" id="memPWDHelp" class="control-label"></label>
            </div>
            <div class="login-buttons">
                <button type="button" id="btn_logok" class="btn btn-success btn-block btn-lg">로그인</button>
            </div>
            <div class="m-t-20">
                • 로그인 후 이용해 주시기 바랍니다.<br />
                • 인증이 허가된 사용자만 접속 가능합니다.<br />
                • 부당한 방법으로 접속시 불이익을 받을 수 있습니다.<br />
                • 사용자 계정은 관리자에게 문의하여 주시기 바랍니다.<br />
            </div>
        </div>
    </div>
    <!-- end login -->
</div>
<!-- end page container -->
<script type="text/javascript" charset="utf-8">
    <!--
    var rsaModulus = '${rsaModulus}';
    var rsaExponent= '${rsaExponent}';
    //-->
</script>