/**
 * User: 현재호
 * Date: 2016-04-14
 * Time: 오전 11:51
 */
define('local', ['formcheck'], function (formcheck) {

    // 리턴 스크립트 체크
    function inputCheckScript() {

        return formcheck.checkForm('loginPanel');
    }

    return {
        inputCheckScript : inputCheckScript
    }
});

require(['common', 'formcheck', 'darkhand', 'local', 'jquery'], function (common, formcheck, darkhand, lc, jQuery) {
    // 엔터 적용
    function enterCheck(idx) {

        if (idx == undefined) idx = 0;

        var tw = [
            {
                chk: jQuery("#loginPanel :input"),
                script: function() {
                    var lc = require('local');

                    return lc.inputCheckScript();
                },
                ret: "btn_logok",
                state: submitData
            }];

        common.enterSend(tw);
    }

    // 로그인 Action 통신 처리
    function submitData() {

        var reqData = jQuery("#loginPanel :input").serializeObject();

        reqData['returnUrl'] = jQuery.fn.accessURL().retURL;

        // rsa 암호화
        var rsa = new RSAKey();
        rsa.setPublic(rsaModulus, rsaExponent);

        reqData['memID'] = rsa.encrypt(reqData['memID']);
        reqData['memPWD'] = rsa.encrypt(reqData['memPWD']);

        jQuery.ajax({
            url: jQuery.fn.preUrl + '/login/act',
            type: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(reqData),
            success: function (data) {
                // 통신 성공시 처리
                jQuery("#memIDHelp, #memPWDHelp").empty();
                jQuery(".form-group").removeClass('has-error');

                if (data.isSuccess === false) {

                    var obj, msg, arrMsg;

                    switch (data.idState) {
                        case 0 :

                            obj = jQuery('#memPWDHelp');
                            msg = '아이디 / 비밀번호 오류입니다.<br />' + data.fail_cnt + '번째 로그인 실패 입니다.<br />5회 이상 로그인 실패시 사용이 제약 됩니다.';
                            break;
                        case 1 :
                        case 2 :
                        case 3 :
                        case 4 :

                            arrMsg = ['', '아이디가 존재하지 않습니다.', '탈퇴 사용자 입니다.', 'DB 접근 실패입니다.', '로그인 연속 5회 실패로 인한 시스템 사용이 제한 된 사용자 입니다.'];

                            obj = jQuery("#memIDHelp");
                            msg = arrMsg[data.idState] + '<br />관리자에게 문의해 주세요.';
                            break;
                    }

                    if (obj !== null) {
                        obj.html(msg);
                    }

                    obj.parents(".form-group").addClass('has-error');

                } else {
                    localStorage["token"] = data.token;
                    localStorage["expireIn"] = data.expireIn;

                    location.href = data.returnUrl; // 해당 returnURL 로 페이지 이동
                }
            },
            error: jQuery.fn.ajaxError
        });
    }

    jQuery(function () {
        // 로그인 상태 확인 후 타이머 및 페이지 접근 확인
        if (localStorage["token"] !== undefined) {

            if (localStorage["token"]) {

                jQuery.fn.logOut();
            }
        }

        formcheck.setEvents(); // 키 이벤트 적용
        enterCheck(); // 엔터키 적용

        App.initPageLoad(); // 로딩 바 종료
    });
});