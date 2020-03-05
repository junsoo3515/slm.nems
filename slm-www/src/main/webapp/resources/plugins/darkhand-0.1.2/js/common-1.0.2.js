/*--------------------------------------------------
기능   : 웹 Solution의 공통 Script
HISTORY:
    - ver1.0 : 기본
    - ver1.1 : setOSXModal 함수 변경
       - color_admin Notification 플랫폼의 gritter plugin 사용
    
RETURN : null
----------------------------------------------------*/
/*global jQuery, define */
(function (factory) {
    "use strict";
    if (typeof define === "function" && define.amd) {
        // AMD. Register as an anonymous module.
        define([
            "jqGrid.setting",
            "jquery",
            "jquery.gritter"
        ], factory);
    } else {
        // Browser globals
        factory(jqFn, jQuery);
    }
}(function (jqFn, jQuery) {

    "use strict";

    // 쿠키값을 가져오는 함수
    function getCookie(name) {
        var from_idx = document.cookie.indexOf(name + '=');
        if (from_idx != -1) {
            from_idx += name.length + 1;
            var to_idx = document.cookie.indexOf(';', from_idx);
            if (to_idx == -1) {
                to_idx = document.cookie.length;
            }
            return unescape(document.cookie.substring(from_idx, to_idx));
        }
    }

    // 쿠키값 Setting
    function setCookie(name, value, expiredays) {
        var todayDate = new Date();
        todayDate.setDate(todayDate.getDate() + expiredays);
        todayDate.setHours(0, 0, -1);
        document.cookie = name + "=" + escape(value) + "; path=/; expires=" + todayDate.toGMTString() + ";";
    }

    // json : function callback 실행하는 함수
    function callFunction(fnc) {

        if (fnc != null) {

            var filtersString = JSON.stringify(fnc, function (k, v) {
                if (typeof v === 'function') {
                    return v.toString();
                }

                return v;
            });

            var retFunc = JSON.parse(filtersString, function (k, v) {

                if (v && typeof v === "string" && v.substr(0, 8) == "function") {
                    var startBody = v.indexOf('{') + 1;
                    var endBody = v.lastIndexOf('}');
                    var startArgs = v.indexOf('(') + 1;
                    var endArgs = v.indexOf(')');

                    return new Function(v.substring(startArgs, endArgs), v.substring(startBody, endBody));
                }

                return v;
            });

            return retFunc();
        }
    }

    // 엔터키 적용(chk, script, ret);
    function enterSend(val) {

        var retVal = true;

        jQuery.each(val, function (index, data) {

            jQuery("#" + data["ret"]).off("click").on("click", function (e) {

                e.stopPropagation(); // 이벤트 버블링 방지

                if (data["script"] != null) {

                    var tmps = callFunction(data.script);

                    retVal = (tmps === undefined ? true : tmps);
                }

                if (retVal === true) {

                    callFunction(data.state);
                }
                return false;
            });

            data["chk"].not('textarea').off("keydown").on("keydown", function (evt) {

                if (evt.keyCode === 13) {

                    if (data["script"] != null) {

                        var tmps = callFunction(data.script);

                        retVal = (tmps === undefined ? true : tmps);
                    }

                    if (retVal === true) {

                        if (data["state"] != null) {

                            jQuery("#" + data["ret"]).off("click").on("click", function () {
                                callFunction(data.state);
                            }).click();
                        }
                    }
                    return false;
                }
            });
        });
    }

    /*--------------------------------------------------
     기능   : input select box focus Style 적용
     INPUT  :
     RETURN : null
     ----------------------------------------------------*/
    function focusCSS() {

        jQuery(":input, select").live("focus",
            function () {
                jQuery(this).addClass("focus");
            }
        ).live("blur",
            function () {
                jQuery(this).removeClass("focus");
                jQuery(this).addClass("focusnot");
            }
        );
    }

    /*--------------------------------------------------
     기능   : 특정 부분에 로딩중 이미지 표출
     INPUT  :
     panelID : 로딩중 이미지가 있는 LayerID
     targetID: 특정 부분 LayerID
     state   : true / false ( 시작 / 종료)
     v       : 속도(기본값 : 500)
     RETURN : null
     ----------------------------------------------------*/
    function loadingImg(panelID, targetID, state, v) {
        if (v === undefined) v = 500;

        switch (state) {
            case true:
                var padingTop = (jQuery('#' + targetID).height() / 2) - 20;
                var padingLeft = (jQuery('#' + targetID).width() / 2) - 20;

                //통신을 시작할때 처리
                jQuery('#' + panelID + " #loadingImg").css({
                    'position': 'relative',
                    'left': padingLeft + "px",
                    'top': padingTop + "px"
                });

                jQuery('#' + panelID).css({
                    'position': 'absolute',
                    'left': jQuery('#' + targetID).offset().left + "px",
                    'top': jQuery('#' + targetID).offset().top + "px",
                    'width': jQuery('#' + targetID).width() + "px",
                    'height': jQuery('#' + targetID).height() + "px"
                }).fadeIn(v);

                break;
            case false:
                jQuery('#' + panelID).fadeOut(v);
                break;
        }
    }

    /*--------------------------------------------------
     기능   : 적용 폼 안에 input 객체들 초기화
     INPUT  :
     ele : 적용 LayerID OR jQuery Object
     isForm : form 객체 여부
     RETURN : null
     ----------------------------------------------------*/
    function clearElement(ele, isForm) {

        if (isForm === undefined) {

            isForm = true;
        }

        var eles = (jQuery.type(ele) === 'string' ? jQuery(ele) : ele);

        if (isForm === true) {
            eles = eles.find(':input');
        } else {
            eles = eles.find("[id]");
        }

        eles.each(function () {

            if (isForm === false && this.type === undefined) {
                this.type = 'html';
            }

            switch (this.type) {

                case 'select-multiple':
                case 'select-one':

                    this.selectedIndex = 0;
                    break;
                case 'checkbox':
                case 'radio':

                    this.checked = false;
                    break;
                case 'file':
                    if (/MSIE/.test(navigator.userAgent)) {
                        jQuery(this).replaceWith(jQuery(this).clone(true));
                    } else {
                        jQuery(this).val('');
                    }
                    break;
                case 'html':

                    jQuery(this).html('');
                    break;
                default:

                    jQuery(this).val('');
                    break;
            }
        });
    }

    /*--------------------------------------------------
     기능   : 적용 폼 안에 객체들 값 일괄 세팅
     INPUT  :
     obj : 적용 Json 데이터
     RETURN : null
     ----------------------------------------------------*/
    function setHtmlValues(obj) {

        jQuery.each(obj, function (k, v) {
            jQuery('#' + k).html(v);
        });
    }

    /*--------------------------------------------------
     기능   : 적용 폼 안에 input 객체들 값 일괄 세팅
     INPUT  :
     obj : 적용 Json 데이터,
     pattern : regex 패턴
     prefix : input 객체 앞의 구분자
     parentID : form ID
     예제 :
     var setValues(res, /_[a-z]/g, 'w_', 'infoPanel');
     RETURN : null
     ----------------------------------------------------*/
    function setValues(obj, pattern, prefix, parentID) {

        if (prefix === undefined) prefix = '';

        jQuery.each(obj, function (k, v) {

            var ids = prefix + k;

            if (pattern !== undefined) {

                jQuery.each(ids.match(pattern), function (idx, data) {
                    ids = ids.replace(data, data.toUpperCase().substring(1));
                });
            }

            var o = jQuery('#' + ids);

            if (o.val() === undefined) {

                jQuery('#' + parentID + ' input:radio').filter("[name='" + ids + "'][value='" + v + "']").prop('checked', true).change();
            } else {

                o.val([v]);

                if ((o.attr('type') === 'checkbox' && o.is(':checked')) || o.find('option').size() > 0) {

                    o.change();
                }
            }
        });
    }

    /*--------------------------------------------------
     기능   : 적용 폼 안에 input 객체들 값 일괄 세팅
     INPUT  :
     obj : 적용 Json 데이터
     RETURN : null
     ----------------------------------------------------*/
    function setTextValues(obj) {
        jQuery.each(obj, function (k, v) {
            jQuery('#' + k).text(v);
        });
    }

    /*--------------------------------------------------
     기능   : json 객체 중 특정 객체 삭제 기능
     INPUT  :
     obj : 적용 Json 데이터
     cObj : 변경 할 Json 객체
     pattern : regex 패턴
     prefix : input 객체 앞의 구분자
     예제 :
     1) 수동 적용
     var reqData = jQuery("#infoPanel :input").serializeObject();
     reqData = changeKeys(reqData, [
     { k: 'wUserID', v: 'mem_id' },
     { k: 'wNm', v: 'nm' },
     { k: 'wPwd0', v: 'pwd' }
     ...
     ]);
     2) 패턴 적용
     var reqData = jQuery("#infoPanel :input").serializeObject();
     reqData = changeKeys(reqData, null, /[A-Z]/g, 'w_');

     RETURN : json
     ----------------------------------------------------*/
    function changeKeys(obj, cObj, pattern, prefix) {

        var retObj = {};

        if (pattern !== undefined) {
            // 정규표현식 패턴으로 일괄 Setting 시
            jQuery.each(obj, function (k, v) {

                var ids = k;
                var resultArray = ids.match(pattern);

                jQuery.each(resultArray, function (idx, data) {
                    ids = ids.replace(data, '_' + data.toLowerCase());
                });

                ids = ids.replace(prefix, '');

                retObj[ids] = v;
            });
        }

        if (cObj !== null) {
            // 수동으로 적용한 키 값 Setting 시
            jQuery.each(cObj, function (index, data) {
                retObj[data.v] = obj[data.k];
            });
        }

        return retObj;
    }

    /*--------------------------------------------------
     기능   : json 객체 Select Box에 설정 해 주는 것
     INPUT  :
     obj : 적용 객체
     strFirstMsg : 선택 메세지
     jsonData :
     - [{ id: 'N', val: '무' }, { id: 'Y', val: '유' }]
     selVal :
     - 선택 된 val 값
     jsonType :
     - "A" : [{ id: 'N', val: '무' }, { id: 'Y', val: '유' }]
     - "B" : {'N', '무', 'Y', '유'}
     RETURN : null
     ----------------------------------------------------*/
    function setSelectOpt(obj, strFirstMsg, jsonData, selVal, jsonType) {

        if (selVal == undefined) {
            selVal = '';
        }
        if (jsonType === undefined) {
            jsonType = "A";
        }

        if (strFirstMsg !== null) {
            obj.append("<option value=''>" + strFirstMsg + "</option>");
        }

        switch (jsonType) {
            case "A":
                // [{ id: 'N', val: '무' }, { id: 'Y', val: '유' }]
                jQuery.each(jsonData, function (idx, data) {
                    obj.append("<option value='" + data.id + "'>" + data.val + "</option>");
                });
                break;
            case "B":
                // {'N', '무', 'Y', '유'}
                var stateId, newOptions = '';
                for (stateId in jsonData) {
                    if (jsonData.hasOwnProperty(stateId)) {

                        obj.append("<option value='" + stateId + "'>" + jsonData[stateId] + "</option>");
                    }
                }
                break;
        }

        if (selVal !== '') {

            obj.val(selVal);
        }
    }

    /*--------------------------------------------------
     기능   : SELECT BOX AJAX를 통해 DB에서 값 가져 올 때 사용
     INPUT  :
     url : url
     fcData : POST Type의 data 객체
     fcSuccess : success의 함수
     fcError : error일 때 함수
     asyncOpt : async Option
     RETURN : null
     ----------------------------------------------------*/
    function changeSelOpt(url, fcData, fcSuccess, fcError, asyncOpt) {

        var ajaxType = "GET";

        if (fcData !== undefined) {

            if (fcData !== null) ajaxType = "POST";
        }

        if (fcError === undefined) {

            fcError = ajaxError;
        }

        if (asyncOpt === undefined) {

            asyncOpt = false;
        }

        jQuery.ajax({
            url: url,
            type: ajaxType,
            dataType: "json",
            data: (ajaxType === "GET" ? {} : JSON.stringify(fcData)),
            contentType: "application/json; charset=utf-8",
            async: asyncOpt,
            success: fcSuccess,
            error: fcError
        });
    }

    /*--------------------------------------------------
     기능   : json 객체 jqGrid의 Select Box에 설정 해 주는 것
     INPUT  :
     strFirstMsg : 선택 메세지
     jsonData :
     - [{ id: 'N', val: '무' }, { id: 'Y', val: '유' }]
     RETURN : {'N', '무', 'Y', '유'}
     ----------------------------------------------------*/
    function setjqGridOpt(strFirstMsg, jsonData) {

        var retV = {};

        if (strFirstMsg != null) {
            retV[''] = strFirstMsg;
        }

        jQuery.each(jsonData, function (idx, data) {
            retV[data.id] = data.val;
        });

        return retV;
    }

    /*--------------------------------------------------
     기능   : 입력 or 업로드를 submit으로 할경우 target (listFunction을 성정시 자동 리스트 로딩 )
     INPUT  :
     listFunc : load 후 function
     RETURN : null
     ----------------------------------------------------*/
    function targetFrame(listFunc) {
        if (listFunc != null && listFunc != '') {
            return '<iframe id="upload_target" name="upload_target" src="" style="width:0;height:0;border:0px solid #fff;" onload=\"' + listFunc + '()\"></iframe>';
        } else {
            return '<iframe id="upload_target" name="upload_target" src="" style="width:0;height:0;border:0px solid #fff;"></iframe>';
        }
    }

    /*--------------------------------------------------
     기능   : OSX Modal 창 내용 갱신
     INPUT  :
     data    : gritter json object type
     target  : 다음 Target JQuery 객체
     jong    : 옵션(0 : bootstrap Alerts, 1 : alert)
     RETURN : null
     ----------------------------------------------------*/
    function setOSXModal(data, target, jong) {
        if (target === undefined) {
            target = null;
        }

        if (jong === undefined) {
            jong = 0;
        }

        switch (jong) {
            case 0:

                jQuery.gritter.add(data);
                break;
            case 1:

                alert(data.text);
                break;
        }
    }

    // 날짜 포맷 Setting(날짜 사이 구분자)
    function nowDate(gubun, milliSec) {

        var resDate;

        if (milliSec === undefined) {
            resDate = new Date();
        } else {
            resDate = new Date(milliSec);
        }

        var resMM = (new String(resDate.getMonth() + 1).length == 1) ? '0' + (resDate.getMonth() + 1) : (resDate.getMonth() + 1);
        var resDD = (new String(resDate.getDate()).length == 1) ? '0' + resDate.getDate() : resDate.getDate();

        return resDate.getFullYear() + gubun + resMM + gubun + resDD;
    }

    // 날짜 포맷 Setting(날짜(구분자 포함) string, 간격계산 구분자(y,m,d), 차, 날짜 사이 구분자)
    function termDate(data, jong, term, gubun) {

        var resDate = calDate(data, jong, term, gubun);
        var resMM = (new String(resDate.getMonth() + 1).length == 1) ? '0' + (resDate.getMonth() + 1) : (resDate.getMonth() + 1);
        var resDD = (new String(resDate.getDate()).length == 1) ? '0' + resDate.getDate() : resDate.getDate();

        return resDate.getFullYear() + gubun + resMM + gubun + resDD;
    }

    // 간격 계산한 날짜 가져오기
    function calDate(data, jong, term, gubun) {

        var resDate, realDate, tmps;

        data = data.replace(eval("/\\" + gubun + "/g"), "");

        switch (jong) {
            case "y":
                tmps = jsAddYear(data, term);
                resDate = new Date(tmps.substring(0, 4), tmps.substring(4, 6) - 1, tmps.substring(6, 8));

                break;
            case "m":
                tmps = jsAddMonths(data, term);
                resDate = new Date(tmps.substring(0, 4), tmps.substring(4, 6) - 1, tmps.substring(6, 8));

                break;
            case "d":
                realDate = new Date(parseInt(data.substring(0, 4)), parseInt(data.substring(4, 6)) - 1, parseInt(data.substring(6, 8)));
                resDate = new Date(realDate.valueOf() + (1000 * 24 * 60 * 60 * term));
                break;
        }

        return resDate;
    }

    /**
     * 두 날짜 사이의 일자 차이 초과 유무
     *
     * @param val1 - 조회 시작일(날짜 ex.2002-01-01)
     * @param val2 - 조회 종료일(날짜 ex.2002-01-01)
     * @param term - 최대 차이 일 수
     * @return 초과 시 : false / 아닐 경우 : true
     */
    function isMaxTermDate(fromdt, todt, term) {
        var sDT, eDT;
        var gubun = "-";

        fromdt = fromdt.replace(eval("/\\" + gubun + "/g"), "");
        todt = todt.replace(eval("/\\" + gubun + "/g"), "");

        sDT = new Date(fromdt.substring(0, 4), Number(fromdt.substring(4, 6)) - 1, Number(fromdt.substring(6, 8)));
        eDT = new Date(todt.substring(0, 4), Number(todt.substring(4, 6)) - 1, Number(todt.substring(6, 8)));

        if (eDT.getTime() < sDT.getTime()) {

            setOSXModal('검색종료일이 검색 시작일 보다 크게 설정 되어 있습니다.');
            return false;
        }

        if ((eDT.getTime() - sDT.getTime()) / 1000 / 60 / 60 / 24 > term) {

            setOSXModal('최대 검색 기간은 ' + term + '일을 초과할 수 없습니다.');
            return false;
        }

        return true;
    }

    /*--------------------------------------------------
     기능   : 날짜에 년수를 더한다.
     INPUT  : startDt(YYYYMMDD), year
     RETURN : rtnValue : 날짜에 년수를 더한 날짜
     -1       : ERROR..!
     예) 20000110 + 1년 = 20010110
     예) 20000229 + 1년 = 20010228
     MSG :
     ----------------------------------------------------*/
    function jsAddYear(startDt, plusYear) {
        var rtnValue = "",
            yyyy = startDt.substring(0, 4),
            mm = startDt.substring(4, 6),
            dd = startDt.substring(6, 8);

        var newYyyy = (Number(yyyy) + Number(plusYear));
        // 윤달(29일) 인 경우 28일로 고침
        // 예) 20000229 에 1년을 더하면 20000228
        var isYoonYear = false;
        // 4 로 나누어 떨어지면 윤년
        // 100 으로 나누어 떨어지면 윤년 아님
        // 400 으로 나누어 떨어지면 윤년

        if ((eval(newYyyy) % 4) === 0) isYoonYear = true;
        if ((eval(newYyyy) % 100) === 0) isYoonYear = false;
        if ((eval(newYyyy) % 400) === 0) isYoonYear = true;
        if ((mm === '02') && (dd === '29') && !isYoonYear) dd = '28';

        rtnValue = String(newYyyy) + mm + dd;

        return rtnValue;
    }

    /*--------------------------------------------------
     기능   : 날짜에 월수를 더한다.
     INPUT  : startDt(YYYYMMDD), year
     RETURN : rtnValue : 날짜에 월수를 더한 날짜
     -1       : ERROR..!
     예) 20000110 + 3월  = 20000410
     예) 20000229 + 12월 = 20000228
     MSG :
     ----------------------------------------------------*/
    function jsAddMonths(startDt, plusMonth) {
        var rtnValue = "",
            yyyy = startDt.substring(0, 4),
            mm = startDt.substring(4, 6),
            dd = startDt.substring(6, 8),
            newMm = null;

        // 월수를 더하여 1년이 넘는 경우
        if ((Number(mm) + Number(plusMonth)) > 12) {
            yyyy = String(Number(yyyy) + 1);
            newMm = Number(mm) + Number(plusMonth) - 12;
        } else if ((Number(mm) + Number(plusMonth)) < 0) {
            yyyy = String(Number(yyyy) - 1);
            newMm = 12 + (Number(mm) + Number(plusMonth));
        } else {
            newMm = Number(mm) + Number(plusMonth);
        }
        // 윤년 처리
        var isYoonYear = false;
        // 4 로 나누어 떨어지면 윤년
        // 100 으로 나누어 떨어지면 윤년 아님
        // 400 으로 나누어 떨어지면 윤년
        if ((eval(yyyy) % 4) === 0) isYoonYear = true;
        if ((eval(yyyy) % 100) === 0) isYoonYear = false;
        if ((eval(yyyy) % 400) === 0) isYoonYear = true;
        // 윤년인 경우
        if (isYoonYear) {
            if ((newMm === '02') && (dd === '30' || dd === '31')) {
                dd = '29';
            }
        } else {
            // 평년인 경우
            if ((newMm === '02') && (dd === '29' || dd === '30' || dd === '31')) {
                dd = '28';
            }
        }
        // 월의 자리수를 맞춘다. ( 2 월 -> 02 )
        if (Number(newMm) < 10) {
            newMm = String("0" + newMm);
        }
        ;

        rtnValue = yyyy + '' + newMm + '' + dd;

        return rtnValue;
    }

    /*--------------------------------------------------
     기능   : 자리수 앞자리 0 채우기
     INPUT  : 숫자, 자리수
     RETURN : rtnValue : 0 채운 값
     예) 12, 4  = 0012
     MSG :
     ----------------------------------------------------*/
    function leadingZeros(n, ditis) {

        var zero = "";

        n = Number(n).toString();

        if (n.length < ditis) {

            for (var i = 0; i < ditis - n.length; i++) zero += '0';
        }

        return zero + n;
    }

    //콤마찍기
    function comma(str) {
        str = String(str);
        return str.replace(/(\d)(?=(?:\d{3})+(?!\d))/g, '$1,');
    }

    //콤마풀기
    function uncomma(str) {
        str = String(str);
        return str.replace(/[^\d.]+/g, '');
    }

    /*--------------------------------------------------
     기능   : ajax 통신 에러 공통 처리
     INPUT  :
     RETURN :
     MSG :
     ----------------------------------------------------*/
    function ajaxError(jqXhr, textStatus, errorThrown) {
        // 통신 에러 발생시 처리
        if (textStatus === undefined) textStatus = '';
        if (errorThrown === undefined) errorThrown = '';

        console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");
    }

    /*--------------------------------------------------
     기능   : jqGrid 상단에 AddRow
     INPUT  :
     gridID : 그리드 ID
     initData : 그리드 처음 로딩 시 넘겨야 할 값
     afterFunc : 추가 이벤트
     예제  :
     addRow('inpList');
     addRow('inpList', {mdl_seq: 0});
     addRow('inpList', {mdl_seq: 0}, function() { alert('1') });
     RETURN : null
     ----------------------------------------------------*/
    // GRID 상위에 AddRow
    function addRow(gridID, initData, afterFunc) {

        if (gridID !== undefined) {

            if (initData === undefined) initData = {};

            var dataGrid = jQuery("#" + gridID);
            var rid = jQuery.jgrid.randId('jqgadd');

            dataGrid.jqGrid('addRow', { rowID: rid, initdata: initData });

            jQuery("tr#" + rid + " :input[class*='editable']").removeAttr('style').parent('td').addClass('p-r-15');

            jqFn.jqGridListIcon(gridID, rid, 'add');

            if (afterFunc !== undefined) callFunction(afterFunc);
        }

        return false;
    }

    return {
        version: "1.1",
        author: "darkhand",
        getCookie: getCookie,
        setCookie: setCookie,
        callFunction: callFunction,
        enterSend: enterSend,
        focusCSS: focusCSS,
        loadingImg: loadingImg,
        clearElement: clearElement,
        setHtmlValues: setHtmlValues,
        setValues: setValues,
        setTextValues: setTextValues,
        changeKeys: changeKeys,
        setSelectOpt: setSelectOpt,
        changeSelOpt: changeSelOpt,
        setjqGridOpt: setjqGridOpt,
        targetFrame: targetFrame,
        setOSXModal: setOSXModal,
        nowDate: nowDate,
        termDate: termDate,
        calDate: calDate,
        isMaxTermDate: isMaxTermDate,
        jsAddYear: jsAddYear,
        jsAddMonths: jsAddMonths,
        leadingZeros: leadingZeros,
        comma: comma,
        uncomma: uncomma,
        ajaxError: ajaxError,
        addRow: addRow
    };
}));