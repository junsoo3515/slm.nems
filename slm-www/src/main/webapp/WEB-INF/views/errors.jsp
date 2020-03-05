<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" session="false"%>

<!-- begin error -->
<div class="error">
    <div class="error-code m-b-10">${errorCode} <i class="fa fa-warning"></i></div>
    <div class="error-content">
        <div class="error-message">${errorMsg}</div>
        <div class="error-desc m-b-20">
        </div>
        <div>
            <a href="${p}" class="btn btn-success">홈으로 이동</a>
        </div>
    </div>
</div>
<!-- end error -->