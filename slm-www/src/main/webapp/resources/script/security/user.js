/**
 * User: 현재호
 * Date: 2016.04.15
 * Time: 오후 3:42
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'infoPanel':
                // 입력 폼
                var pwd1 = jQuery('#wPwd0');
                var pwd2 = jQuery('#wPwd1');
                var ol = jQuery('#wUserIDChk');

                if (jQuery('#wUserID').val() === '') {
                    common.setOSXModal('아이디를 입력해 주세요.', jQuery('#wUserID'));
                    return false;
                }

                if (ol.val() !== 'A') {
                    if (!ol.val()) {
                        common.setOSXModal('아이디를 중복 체크해 주세요.', jQuery('#wUserID'));
                        return false;
                    }
                    if (pwd1.val() === '') {
                        common.setOSXModal('비밀번호를 입력해 주세요.', jQuery('#wPwd0'));
                        return false;
                    }
                    var pwdPattern = /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[^a-zA-Z0-9])(?!.*\s).{8,15}$/;
                    if(!pwd1.val().match(pwdPattern)) {
                        common.setOSXModal('비밀번호는 문자, 대소문자, 숫자, 특수문자의 조합으로 8~15자리로 입력해 주세요.', jQuery('#wPwd0'));
                        return false;
                    }

                    if (pwd2.val() === '') {
                        common.setOSXModal('비밀번호확인을 입력해 주세요.', jQuery('#wPwd1'));
                        return false;
                    }
                    if(!pwd2.val().match(pwdPattern)) {
                        common.setOSXModal('비밀번호는 문자, 대소문자, 숫자, 특수문자의 조합으로 8~15자리로 입력해 주세요.', jQuery('#wPwd0'));
                        return false;
                    }
                    if (ol.val() === 'N') {
                        common.setOSXModal('회원 아이디가 존재합니다.', jQuery('#wUserID'));
                        return false;
                    }
                }

                if (pwd1.val() !== pwd2.val()) {
                    common.setOSXModal('비밀번호 입력이 틀립니다, 다시입력 하세요', jQuery("#wPwd0"));
                    return false;
                }

                return formcheck.checkForm(tarID, 0);
                break;
            default:

                return formcheck.checkForm(tarID);

                break;
        }
    }

    // jqGrid data 리프레쉬
    function dataReload(tarID) {

        var listID = tarID + "List";
        var dataGrid = jQuery("#" + listID);
        var filterData = {};

        var jqOpt = {};

        switch(listID) {

            case 'userList' :
                // 사용자 목록
                filterData = jQuery('#srcPanel :input').serializeObject();
                break;
            case 'authList' :
                // 사용자 메뉴 접근 관리 목록
                jqOpt = {
                    url: './getMenuList'
                };
                break;
        }

        dataGrid
            .jqGrid("setGridParam", jQuery.extend(true, {
                search: true,
                postData: {
                    filters: JSON.stringify(filterData)
                }
            }, jqOpt))
            .trigger("reloadGrid", [{page:1}]);
    }

    // 사용자/권한 별 메뉴 정보 불러오기
    function menuSetting(jongCD, val) {
        // 권한 DropDown 변경 시 해당 메뉴별 접근 권한 불러오기
        common.changeSelOpt('./getMenuData', {

            jongCd : jongCD,
            val : val
        }, function(data) {

            jQuery.when(
                data,
                jQuery('#authList')
            ).then(function(data, obj) {

                data.forEach(function (item, index, array) {

                    obj.jqGrid('setRowData', item.mnu_cd, item);
                });

                return obj;
            }).done(function(obj) {
                // 그룹에 해당하는 checkbox 비활성화
                var grpData = obj.getRowData().filter(function (itm, index, array) {

                    if (itm.mnu_nm3 === '') return true;
                });

                grpData.forEach(function (item, index, array) {

                    obj.find("tr[id='" + item.mnu_cd + "']").find(':checkbox').attr('disabled', true);
                });
            }).always(function() {

                return false;
            });

        }, common.ajaxError, true);
    }

    // 기본정보 폼 Setting
    function infoSetting(rowID, obj) {

        isAuthDataLoad = false;

        var regBtn = jQuery('#btnReg');

        if (rowID) {
            // 리스트 폼에 있는 객체 입력 폼 Setting
            jQuery('#wUserID').attr("readonly", true); // 사용자 아이디(수정 모드에서 수정 못하게 비활성화)
            jQuery('#btnUserChk').hide(); // 아이디 중복확인 버튼 비 활성화

            if(authCrud.MOD_FL === 'Y') {

                jQuery('#btnReg, #btnCancel').attr('disabled', false);
                regBtn.html(regBtn.html().replace("등록", "수정"));
            }

            jQuery.when(
                jQuery.ajax({
                    url: './getUserData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        mem_seq: obj.mem_seq
                    })
                })
            ).then(function(data) {
                // 그리드의 값을 가지고 입력 값 Setting
                common.setValues({
                    wUserIDChk: 'A',
                    wUserID: obj.mem_id,
                    wUseFl: obj.use_fl,
                    wAuth: obj.auth_cd,
                    wNm: obj.nm,
                    wEmail: obj.email,
                    wTelOffice: obj.tel_office,
                    wTelHp: obj.tel_hp
                });

                // 프로필 사진 Setting
                if (obj.files_seq > 0) {
                    jQuery('#profile-image').css({
                        'background-image': "url('" + jQuery.fn.sysUrl + "/files/download/" + obj.files_seq + "')",
                        'background-size': 'cover',
                        'background-position': 'center center'
                    });
                }

                return data;
            }).then(function(data) {
                // DB에서 검색된 결과 값 Setting
                common.setValues({
                    wEtc: data.etc
                });

                // 로그인 실패횟수 초기화 버튼 활성화
                jQuery('#btnPwdClear').attr('disabled', (data.fail_pwd_cnt > 0) ? false : true);
            }).done(function() {
                // 사용자 별 메뉴 접근 설정 데이터 가져오기
                menuSetting('B', obj.mem_id);
            })
            .fail(common.ajaxError)
            .always(function() {

                isAuthDataLoad = true;
                return false;
            });
        }
    }

    // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if(isAll) {
            // 모든 패널 초기화
            panelClear(false, 'infoPanel', false);

            var rowid = jQuery("#userList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#userList").jqGrid("resetSelection"); // Grid Select reset
            }

            return false;
        }

        switch(objID) {

            case 'infoPanel':
                // 기본정보 입력폼
                jQuery('#wUserID').attr("readonly", false);
                jQuery('#btnUserChk').show();

                common.clearElement('#' + objID); // form element

                jQuery('#profile-image').removeAttr("style"); // 프로필 사진 초기화

                var regBtn = jQuery('#btnReg');

                jQuery('#btnPwdClear').attr('disabled', true); // 로그인 실패횟수 초기화 버튼 비활성화
                jQuery('#btnReg, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경
                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#userList").jqGrid('getGridParam', 'selrow');

        if (rowid === null) {
            // 등록 모드
            if (authCrud.REG_FL === 'N') return false;
        } else {
            // 수정 모드
            if (authCrud.MOD_FL === 'N') return false;
        }

        var formData = jQuery('#infoPanel :input');
        var tmpData = formData.filter(":disabled").attr('disabled', false);
        var reqData = formData.serializeObject();

        tmpData.attr('disabled', true);

        // 사용자 고유번호
        reqData.mem_seq = (rowid === null ? 0 : Number(jQuery("#userList").getRowData(rowid).mem_seq) );

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)
        reqData = common.changeKeys(reqData, [
            { k: 'mem_seq', v: 'mem_seq' },
            { k: 'wUserID', v: 'mem_id' },
            { k: 'wNm', v: 'nm' },
            { k: 'wPwd0', v: 'pwd' },
            { k: 'wEmail', v: 'email' },
            { k: 'wTelHp', v: 'tel_hp' },
            { k: 'wTelOffice', v: 'tel_office' },
            { k: 'wAuth', v: 'auth_cd' },
            { k: 'wUseFl', v: 'use_fl' },
            { k: 'wEtc', v: 'etc' }
        ]);

        // 사용자 메뉴 접근 관리 값
        reqData.authData = jQuery("#authList").jqGrid('getRowData').map(function (item, index, array) {

            return {
                mnu_cd: item.mnu_cd,
                use_fl: item.use_fl,
                read_fl: item.read_fl,
                reg_fl: item.reg_fl,
                mod_fl: item.mod_fl,
                del_fl: item.del_fl
            }
        });

        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setUserAct',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data > 0) {

                common.setOSXModal('사용자 정보가 성공적으로 저장하였습니다.');

                var obj = jQuery("#userList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                // 첨부파일 존재 시 이벤트
                if (jQuery('#files').val() !== '') {
                    var selData = obj.jqGrid('getRowData', rowid);

                    if (selData.files_seq > 0) {
                        // 기존 첨부파일 삭제 Process(비동기 처리)
                        jQuery.ajax({
                            url: jQuery.fn.sysUrl + '/files/delete/' + selData.files_seq,
                            type: 'DELETE'
                        });
                    }

                    // 첨부파일 저장 Process(비동기 처리)
                    var formData = new FormData(); // HTML5 지원 되는 브라우저부터 지원

                    formData.append('rootPath', jQuery.fn.sysUrl);
                    formData.append('systemPath', 'slm');
                    formData.append('folPath', 'security');
                    formData.append('tableKey', 'COM_MEM_INFO');
                    formData.append('gubunKey', 'A');
                    formData.append('seq', jQuery('#wUserID').val());
                    formData.append("files", jQuery('input[name=files]')[0].files[0]);

                    jQuery.ajax({
                        url: jQuery.fn.sysUrl + '/files/singleUpload',
                        type: "POST",
                        processData: false,
                        contentType: false,
                        data: formData,
                        success: function(data) {

                            if (rowid !== null) {
                                // 수정모드일 때 성능향상을 고려하여 그리드에 바로 데이터 갱신 처리(비동기 처리라서 이 부분 다시 적용)
                                obj.jqGrid('setRowData', rowid, {files_seq: data});
                            }
                        },
                        error: function (jqXhr, textStatus, errorThrown) {
                            //통신 에러 발생시 처리
                            console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");
                            common.setOSXModal('첨부한 사진이 저장에 실패하셨습니다.');
                        }
                    });
                }

                if (rowid !== null) {
                    // 수정모드일 때 성능향상을 고려하여 그리드에 바로 데이터 갱신 처리
                    obj.jqGrid('setRowData', rowid, {
                        nm: reqData.nm,
                        email: reqData.email,
                        tel_hp: reqData.tel_hp,
                        tel_office: reqData.tel_office,
                        auth_cd: reqData.auth_cd,
                        auth_nm: jQuery('#wAuth').find('option:selected').text(),
                        use_fl: reqData.use_fl
                    });
                } else {
                    // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                    panelClear(true);
                    dataReload('user');
                }
            } else {

                common.setOSXModal('저장이 실패하였습니다.');
            }
        })
        .fail(common.ajaxError)
        .always(function() {

            jQuery.fn.loadingComplete();
            return false;
        });

        return false;
    }

    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel(idx) {

        if(idx === undefined) idx = 0;

        switch(idx) {
            case 0 :
                var arrObj = [
                    { list: "userList", panel: "userPanel" },
                    { list: "authList", panel: "authPanel" }
                ];

                break;
            case 1 :
                var arrObj = [
                    { list: "userList", panel: "userPanel" }
                ];

                break;
            case 2 :
                var arrObj = [
                    { list: "authList", panel: "authPanel" }
                ];

                break;
        }

        jQuery.each(arrObj, function (sIdx, data) {

            jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width() - 2);
        });
    }

    // 사용하는 jqGrid Setting
    function gridSetting(tarID) {

        var listID = tarID + "List";
        var pageID = tarID + 'Pager';

        var dataGrid = jQuery("#" + listID);

        var jqDefault = {
            url: '',
            datatype: 'json',
            mtype: 'POST',
            sortname: '', // 소팅 인자
            sortorder: '', // 처음 소팅
            pager : pageID,
            rowNum: 30,                 // 처음에 로드될 때 표출되는 row 수
            rowList: [], // row 갯수 표출 세팅
            viewsortcols: [true, 'vertical', true], // 소팅 인자 세팅
            rownumbers: true,           // Grid의 RowNumber 표출
            viewrecords: true,          // 우측 View 1-4 Text 표출 부분
            gridview: true,             // Grid Alert
            autowidth: true,            // width 자동 맞춤
            shrinkToFit: true,          // width에 맞춰 Cell Width 자동 설정
            height: 200,                // 세로 크기
            caption: '',                // 캡션 명(없으면 표출 안됨)
            beforeRequest : function() {
                // POST 보내기 전 이벤트
                if (jQuery(this).jqGrid('getGridParam', 'url') === '') return false;
            },
            gridComplete: function () {

                jQuery('#' + pageID + ' .ui-pg-input').attr('readonly', true);
            }
        };
        var jqOpt = {};

        switch(listID) {
            case 'userList' :
                // 사용자 목록
                jqOpt = {
                    url : './getUserList',
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '', '', '권한', '아이디', '이름', '연락처', '핸드폰', 'E-Mail', '사용여부'],
                    colModel: [
                        {name: 'mem_seq', index: 'mem_seq', hidden: true},
                        {name: 'auth_cd', index: 'auth_cd', hidden: true},
                        {name: 'files_seq', index: 'files_seq', hidden: true},
                        {name: 'auth_nm', index: 'auth_nm', width: 2},
                        {name: 'mem_id', index: 'mem_id', width: 2},
                        {name: 'nm', index: 'nm', width: 2},
                        {name: 'tel_office', index: 'tel_office', width: 2},
                        {name: 'tel_hp', index: 'tel_hp', width: 2},
                        {name: 'email', index: 'email', width: 3},
                        {name: 'use_fl', index: 'use_fl', width: 1, align: "center"}
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id) {

                        jQuery.when(
                            // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                            panelClear(true, '', false)
                        ).then(function(res) {
                            // 기본정보 입력폼 Setting
                            var ret = dataGrid.jqGrid('getRowData', id);

                            infoSetting(id, ret);
                        }).always(function() {

                            return false;
                        });
                    },
                    loadComplete: function () {

                        resizePanel(1); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        jQuery.fn.loadingComplete();
                    }
                };

                break;
            case 'authList' :
                // 사용자 메뉴 접근 관리 목록
                jqOpt = {
                    url : '',
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [],         // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    height: 400,
                    colNames: ['', '시스템', '메뉴', '화면', '접근', '조회', '추가', '수정', '삭제'],
                    colModel: [
                        { name: 'mnu_cd', index: 'mnu_cd', hidden: true, key: true},
                        { name: 'mnu_nm1', index: 'mnu_nm1', width: 2, editable: false, sortable: false},
                        { name: 'mnu_nm2', index: 'mnu_nm2', width: 2, editable: false, sortable: false},
                        { name: 'mnu_nm3', index: 'mnu_nm3', width: 2, editable: false, sortable: false},
                        {
                            name: 'use_fl', index: 'use_fl', width: 1, align: "center", sortable: false,
                            formatter: "checkbox", formatoptions: {disabled: false},
                            editable: true,
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        {
                            name: 'read_fl', index: 'read_fl', width: 1, align: "center", sortable: false,
                            formatter: "checkbox", formatoptions: {disabled: false},
                            editable: true,
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        {
                            name: 'reg_fl', index: 'reg_fl', width: 1, align: "center", sortable: false,
                            formatter: "checkbox", formatoptions: {disabled: false},
                            editable: true,
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        {
                            name: 'mod_fl', index: 'mod_fl', width: 1, align: "center", sortable: false,
                            formatter: "checkbox", formatoptions: {disabled: false},
                            editable: true,
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        {
                            name: 'del_fl', index: 'del_fl', width: 1, align: "center", sortable: false,
                            formatter: "checkbox", formatoptions: {disabled: false},
                            editable: true,
                            edittype:'checkbox', editoptions: {
                            dataInit: function (el) {

                                jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                            },
                            value: "Y:N"
                        }
                        }
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    loadComplete: function () {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel(2) // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).done(function() {
                            // 그룹에 해당하는 checkbox 비활성화
                            var grpData = jQuery('#authList').getRowData().filter(function (itm, index, array) {

                                if (itm.mnu_nm3 === '') return true;
                            });

                            grpData.forEach(function (item, index, array) {

                                dataGrid.find("tr[id='" + item.mnu_cd + "']").find(':checkbox').attr('disabled', true);
                            });
                        }).always(function() {

                            jQuery.fn.loadingComplete();
                            return false;
                        });
                    }
                };

                break;
        }

        dataGrid
            .jqGrid(jQuery.extend(true, jqDefault, jqOpt))
            .jqGrid('navGrid', '#' + pageID,
            {edit: false, add: false, del: false, search: false, refresh: false}, // options
            {}, // edit options
            {}, // add options
            {}, // del options
            {width: '100%'}, // search options
            {} // view options
        );

        return false;
    }

    return {
        inputCheckScript: inputCheckScript,
        setEvents: formcheck.setEvents,
        dataReload: dataReload,
        menuSetting: menuSetting,
        infoSetting: infoSetting,
        panelClear : panelClear,
        dataSend: dataSend,
        resizePanel: resizePanel,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local', 'jquery'], function (common, darkhand, lc, jQuery) {
    // 엔터 적용
    function enterCheck(idx) {

        if (idx === undefined) idx = 0;

        var tw = [];

        switch (idx) {
            case 0:

                tw.push({
                    chk: jQuery("#srcPanel :input"),
                    script: function() {

                        var lc = require('local');
                        return lc.inputCheckScript('srcPanel');
                    },
                    ret: "btnSrch",
                    state: function() {

                        var lc = require('local');

                        jQuery.fn.loadingStart();

                        lc.panelClear(true); // 전체 폼 초기화
                        lc.dataReload('user'); // 사용자 목록
                    }
                });

                if(authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#infoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('infoPanel');
                        },
                        ret: "btnReg",
                        state: function() {
                            // 검색 Event 처리
                            var lc = require('local');
                            lc.dataSend();
                        }
                    });
                }
                break;
        }

        common.enterSend(tw);
    }

    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {
        // 권한에 따른 버튼 비활성화
        if(authCrud.READ_FL === 'N') {

            jQuery('#btnSrch').attr('disabled', true);
        }
        if(authCrud.REG_FL === 'N' || authCrud.MOD_FL === 'N') {

            jQuery('#btnReg, #btnCancel').attr('disabled', true);
        }

        jQuery('#btnPwdClear').attr('disabled', true);

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // 검색 폼
        common.setSelectOpt(jQuery('#srcAuth'), '-전체-', authList); // 권한

        // 기본정보 폼
        common.setSelectOpt(jQuery('#wAuth'), '-선택-', authList); // 권한

        // ID 중복 체크 버튼 이벤트
        jQuery('#btnUserChk').on('click', function () {

            if (!jQuery('#wUserID').val()) {
                common.setOSXModal('아이디를 입력해 주세요.', jQuery("#wUserID"));
                return false;
            }

            jQuery.fn.loadingStart();

            jQuery.when(

                jQuery.ajax({
                    url: './getIDCheck',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        mem_id: jQuery('#wUserID').val()
                    })
                })
            )
            .then(function(data) {

                jQuery('#wUserIDChk').val('');

                return data;
            })
            .done(function(data) {

                var cd = 'N', msg = '이미 등록된 사용자 ID 입니다.';

                if (data.isSuccess) {
                    cd = 'Y';
                    msg = '성공적으로 중복확인을 하였습니다.';
                }

                jQuery('#wUserIDChk').val(cd);
                common.setOSXModal(msg, null);
            })
            .fail(common.ajaxError)
            .always(function() {

                jQuery.fn.loadingComplete();
                return false;
            });
            return true;
        });

        // 로그인 실패 횟수 초기화 이벤트
        jQuery('#btnPwdClear').on('click', function () {

            jQuery.fn.loadingStart();

            jQuery.when(

                jQuery.ajax({
                    url: './setPwdFailClear',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        mem_id: jQuery('#wUserID').val()
                    })
                })
            )
            .done(function(data) {

                if(data.isSuccess) {

                    common.setOSXModal('비밀번호 실패 횟수를 초기화 하였습니다.', null);
                    jQuery('#btnPwdClear').attr('disabled', true);
                } else {

                    common.setOSXModal('초기화에 실패하였습니다.', null);
                }
            })
            .fail(common.ajaxError)
            .always(function() {

                jQuery.fn.loadingComplete();
                return false;
            });
            return true;
        });

        // 취소 버튼 클릭시
        jQuery('#btnCancel').on('click', function () {

            lc.panelClear(true);
        });

        // 권한 DropDown 변경 시 이벤트
        jQuery('#wAuth').on('change', function() {

            if ( isAuthDataLoad )  {
                // 사용자 정보 그리드 Setting 시 change 이벤트랑 동시에 처리되기 때문에 예외처리
                lc.menuSetting('A', this.value);
            }
        });

        // 이미지 파일 선택 시 이미지 미리보기
        jQuery.fn.uploadPreview({
            input_field: "#files",
            preview_box: "#profile-image",
            no_label: true
        });

        // 그리드 초기화
        lc.gridSetting('user'); // 사용자 목록
        lc.gridSetting('auth'); // 사용자 메뉴 접근 관리 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel(0);

    }).trigger('resize');
});