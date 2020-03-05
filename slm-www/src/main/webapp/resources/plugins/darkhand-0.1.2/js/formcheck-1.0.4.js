/*--------------------------------------------------
 기능   : form 항목의 필수 입력 항목 값 체크
 HISTORY:
 - ver1.0.1 : bootstrap style 및 확장 alert style 적용
 - ver1.0.3 : checkForm 함수 변경
   - msgTyp의 값 2 일 때 bootstrap error Style로 기능 변경
 - ver1.0.4 : checkForm 함수 변경
   - color_admin Notification 플랫폼의 gritter plugin 사용

 RETURN : null
 ----------------------------------------------------*/
/*global jQuery, define */
(function( factory ) {
    "use strict";
    if ( typeof define === "function" && define.amd ) {
        // AMD. Register as an anonymous module.
        define([
            "jquery",
            "common"
        ], factory );
    } else {
        // Browser globals
        factory( jQuery, common );
    }
}(function( jQuery, common ) {

    "use strict";

    // 입력값 체크
    function inputCheck(e) {
        var field   = jQuery(e.target);
        var keycode = e.keyCode;

        // 공통(백스페이스, 탭, 엔터, 슈프트 등...)
        if ((keycode >= 8 && keycode <= 9 ) || keycode === 13 || (keycode >= 16 && keycode <= 17 ) || (keycode >= 21 && keycode <= 40) || (keycode >= 45 && keycode <= 46)) {
            return true;
        }

        // 소숫점까지 입력되는 것(음수포함)
        if (field.attr("isDigitDotOnly") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105) || keycode === 110 || keycode === 190 || keycode === 109 || keycode === 189) {
                return true;
            }
            return false;
        }

        // 숫자만 입력 받는것(음수포함)
        if (field.attr("isDigitOnly") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105) || keycode === 109 || keycode === 189) {
                return true;
            }
            return false;
        }

        // 금액등 숫자만 들어가는 경우
        if (field.attr("isNumericOnly") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105)) {
                return true;
            }
            return false;
        }

        // 금액등 숫자만 들어가는 경우(소숫점포함)
        if (field.attr("isNumericDotOnly") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105) || keycode === 110 || keycode === 190) {
                return true;
            }
            return false;
        }

        // 영문자 및 숫자만 입력 받는것
        if (field.attr("isEngDigitOnly") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105) || (keycode >= 65 && keycode <= 90) || (keycode >= 97 && keycode <= 122)) {
                return true;
            }
            return false;
        }

        // 영문자만
        if (field.attr("isEngOnly") !== undefined) {
            if ((keycode >= 65 && keycode <= 90) || (keycode >= 97 && keycode <= 122)) {
                return true;
            }
            return false;
        }

        // 메일형태
        if (field.attr("isEmail") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105) || (keycode >= 65 && keycode <= 90) || (keycode >= 97 && keycode <= 122) || keycode === 110 || (keycode >= 189 && keycode <= 190)) {
                return true;
            }
            return false;
        }

        // 전화번호 형태
        if (field.attr("isTel") !== undefined) {
            if ((keycode >= 48 && keycode <= 57) || (keycode >= 96 && keycode <= 105) || (keycode === 189)) {
                return true;
            }
            return false;
        }
    }

    // 처음 로드 시 호출 하는 함수
    function setEvents() {
        jQuery(function () {

            jQuery(document).find(':input')
                .on('keydown', inputCheck)
                .on('keyup', inputCheck)
                .each(function () {
                    var field = jQuery(this);

                    if (field.attr("type") === 'button' || field.attr("type") === 'image') {

                        field.css('cursor', 'hand');
                    }

                    if ((field.attr("isNumericOnly") !== undefined) || (field.attr("isNumericDotOnly") !== undefined) || (field.attr("isDigitDotOnly") !== undefined) || (field.attr("isDigitOnly") !== undefined) || (field.attr("isEngDigitOnly") !== undefined) || (field.attr("isEngOnly") !== undefined) || (field.attr("isTel") !== undefined) || (field.attr("isEmail") !== undefined)) {

                        field.css('ime-mode', 'disabled');
                    }
                });
        });
    }

    //숫자 패턴 검사 true : 숫자
    function checkNumber(input) {

        var arrMatch = input.match(/^(-?)[0-9]+$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    //소수점 숫자 패턴 검사 true : 숫자
    function checkNumber2(input) {

        var arrMatch = input.match(/^(-?)[0-9.]+$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    //숫자 패턴 검사 true : 숫자
    function checkNumber3(input) {

        var arrMatch = input.match(/^[0-9]+$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    //소수점 숫자 패턴 검사 true : 숫자
    function checkNumber4(input) {

        var arrMatch = input.match(/^[0-9.]+$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    //영문 패턴 검사 true : 영문
    function checkEngNumber(input) {

        var arrMatch = input.match(/^([A-Za-z0-9.]*)$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    // 영문 패턴 검사 true : 영문
    function checkEnglish(input) {

        var arrMatch = input.match(/^([A-Za-z]*)$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    //E-Mail 패턴 검사
    function checkEmail(emailAddress) {

        var arrMatch = emailAddress.match(/^(([^<>()\[\]\\.,;:\s@\"]+(\.[^<>()\[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/);

        if (arrMatch === null) {

            return false;
        }
        return true;
    }

    //연락처 패턴 검사 true : 연락처
    function checkTel(input) {

        var arrMatch = input.match(/^\d{3}-\d{4}-\d{4}$/);

        if (arrMatch === null) {

            return false;
        }

        return true;
    }

    //문자 길이 반환 (영문 1byte, 한글 2byte 계산)
    function getLen(str) {
        var han_count = 0;

        han_count = (escape(str) + "%u").match(/%u/g).length - 1;

        return (str.length + han_count);
    }

    /*--------------------------------------------------
     기능   : 폼 검증 함수
     INPUT  :
     eleID   : 타겟 폼 ID
     msgTyp  : 0 : bootstrap Alerts, 1 : alert, 2 : bootstrap error style 변경, 3 : modal
     RETURN : null
     ----------------------------------------------------*/
    function checkForm(eleID, msgTyp, isShow) {

        var elejQ = jQuery('#' + eleID);
        var res = true; // 결과 bool

        if (msgTyp === undefined) {
            msgTyp = 0;
        }

        if (isShow === undefined) {

            isShow = true;
        }

        if ( msgTyp === 2 ) {
            elejQ.find('.error').removeClass("error");
        }

        var elejQF = elejQ.find(':input');

        if (isShow === true) {

            elejQF = elejQF.filter(':visible');
        }

        elejQF.each(function () {
            var fObj = null,   // 폼 요소
                fTyp = null,   // 폼 요소 Type
                fVal = null,   // 폼 요소 Value
                fMsg = null,   // 경고 메시지 속성
                fMax = null,   // 최대 길이 지정
                fMin = null,   // 최소 길이 지정
                fMxN = null,   // 최대값 지정
                fMnN = null,   // 최소값 지정
                fDigitOnly = null,
                fDigitDotOnly = null,
                fNumericOnly = null,
                fNumericDotOnly = null,
                fEngDigitOnly = null,
                fEngOnly = null,
                fEmail = null,
                fTel = null;

            fObj = jQuery(this);
            fTyp = fObj.attr("type");
            fVal = fObj.val();

            if (fObj.attr("msg") !== undefined) {
                fMsg = fObj.attr("msg");        // 경고 메시지
            }

            if (fMsg !== null) {

                if (fObj.attr("maxlen") !== undefined) {
                    fMax = fObj.attr("maxlen");     // 최대 입력글자수 제한
                }

                if (fObj.attr("minlen") !== undefined) {
                    fMin = fObj.attr("minlen");     // 최소 입력글자수 제한
                }

                if (fObj.attr("maxnum") !== undefined) {
                    fMxN = fObj.attr("maxnum");     // 최대 숫자 제한
                }

                if (fObj.attr("minnum") !== undefined) {
                    fMnN = fObj.attr("minnum");     // 최소 숫자 제한
                }

                if (fObj.attr("isDigitOnly") !== undefined) {
                    fDigitOnly = fObj.attr("isDigitOnly");
                }

                if (fObj.attr("isDigitDotOnly") !== undefined) {
                    fDigitDotOnly = fObj.attr("isDigitDotOnly");
                }

                if (fObj.attr("isNumericOnly") !== undefined) {
                    fNumericOnly = fObj.attr("isNumericOnly");
                }

                if (fObj.attr("isNumericDotOnly") !== undefined) {
                    fNumericDotOnly = fObj.attr("isNumericDotOnly");
                }

                if (fObj.attr("isEngDigitOnly") !== undefined) {
                    fEngDigitOnly = fObj.attr("isEngDigitOnly");
                }

                if (fObj.attr("isEngOnly") !== undefined) {
                    fEngOnly = fObj.attr("isEngOnly");
                }

                if (fObj.attr("isEmail") !== undefined) {
                    fEmail = fObj.attr("isEmail");
                }

                if (fObj.attr("isTel") !== undefined) {
                    fTel = fObj.attr("isTel");
                }
            }

            var titleMsg = "필수 항목 오류발생";

            if (fMsg !== null && (fTyp === "text" || fTyp === "hidden" || fTyp === "textarea" || fTyp === "password") && fVal.replace(/ /gi, "") === "") {
                common.setOSXModal({ title: titleMsg, text: fMsg + " 입력해 주세요." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fObj.css("display") !== "none" && fMsg !== null && this.nodeName.toLowerCase() === "select" && fVal === "") {
                common.setOSXModal({ title: titleMsg, text: fMsg + " 선택해 주세요." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMsg !== null && fTyp === "file" && fVal === "") {
                common.setOSXModal({ title: titleMsg, text: fMsg + " 올려주세요." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMsg !== null && (fTyp === "radio" || fTyp === "checkbox") && fObj.is(':checked') === false) {
                common.setOSXModal({ title: titleMsg, text: fMsg + " 선택해 주세요." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMsg !== null && fTyp === "date" && fVal === "") {
                common.setOSXModal({ title: titleMsg, text: fMsg + " 선택해 주세요." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMax !== null && fMax < getLen(fVal)) {
                common.setOSXModal({ title: titleMsg, text: fMsg + "다시 입력해 주세요. \n입력된 글자수가 " + fMax + "자보다 작아야합니다.\n(영문 " + fMax + "자, 한글 " + Math.floor(fMax / 2) + "자 까지 가능합니다.)" }, fObj, msgTyp, tabInfo);
                res = false;

                return false;
            }

            if (fMin !== null && fMin > getLen(fVal)) {
                common.setOSXModal({ title: titleMsg, text: "입력된 글자수가 " + fMin + "자보다 커야합니다." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMxN !== null && Number(fMxN, 10) < parseInt(fVal, 10)) {
                common.setOSXModal({ title: titleMsg, text: "입력된 숫자는 " + fMxN + "보다 작아야합니다." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMnN !== null && Number(fMnN, 10) > parseInt(fVal, 10)) {
                common.setOSXModal({ title: titleMsg, text: "입력된 숫자는 " + fMnN + "보다 커야합니다." }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fDigitOnly !== null && checkNumber(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "숫자로만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fDigitDotOnly !== null && checkNumber2(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "숫자와 소수점만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fNumericOnly !== null && checkNumber3(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "숫자로만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fNumericDotOnly !== null && checkNumber4(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "숫자와 소수점만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fEngDigitOnly !== null && checkEngNumber(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "영어와 숫자만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fEngOnly !== null && checkEnglish(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "영어만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fEmail !== null && checkEmail(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "E-Mail양식만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }

            if (fMsg !== null && fTel !== null && checkTel(fVal) === false) {
                common.setOSXModal({ title: titleMsg, text: "연락처 양식만 입력해 주세요" }, fObj, msgTyp);
                res = false;

                return false;
            }
        });

        return res;
    }

    return {
        version : "1.0.4",
        author : "darkhand",
        inputCheck : inputCheck,
        setEvents : setEvents,
        checkNumber : checkNumber,
        checkNumber2 : checkNumber2,
        checkEngNumber : checkEngNumber,
        checkEnglish : checkEnglish,
        checkEmail : checkEmail,
        checkTel : checkTel,
        getLen : getLen,
        checkForm : checkForm
    };
}));