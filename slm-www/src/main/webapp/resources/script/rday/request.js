/**
 * User: 이종혁
 * Date: 2016.05.03
 * Time: 오후 3:20
 */
define('local', ['common', 'formcheck', 'jqGrid.setting','jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'measureInfoPanel':

                var isReturn = formcheck.checkForm(tarID, 0);

                if (jQuery('#wMeasureCont').val() === '') {
                    common.setOSXModal('조치내용을 입력해 주세요.', jQuery('#wMeasureCont'));
                    return false;
                }
                return isReturn;

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

            case 'requestList' :
                // 요청사항 목록

                jqOpt = {
                    url: './getRdayRequestList'
                };

                filterData = jQuery('#srcPanel :input').serializeObject();
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        search: true,
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

            case 'measureGridList' :
                // 요청사항조치사항 목록


                jqOpt = {
                    url: './getRdayRequestMeasureList'
                };

                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            cause_seq: jQuery("#requestList").jqGrid('getRowData', jQuery("#requestList").jqGrid('getGridParam', 'selrow')).req_seq
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

        }


    }


    // 기본정보 폼 Setting
    function infoSetting(rowID, obj, str) {

        switch(str) {

            case 'request' :

                var regBtn = jQuery('#btnReg');

                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    if(authCrud.MOD_FL === 'Y') {

                        jQuery('#btnReg, #btnCancel').attr('disabled', false);
                        regBtn.html(regBtn.html().replace("등록", "수정"));
                    }
                    if(authCrud.DEL_FL === 'Y') {

                        jQuery('#btnDel').attr('disabled', false);
                    }

                    jQuery.when(
                        jQuery.ajax({
                            url: './getRdayRequestData',
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify({
                                req_seq: obj.req_seq
                            })
                        })
                    ).then(function(data) {

                            common.setValues({
                                wReq_seq : obj.req_seq,
                                requestGroup : data.request_type,
                                equipTypeGroup: data.equip_type,
                                equipGroup: data.eqp_cd,
                                wReq_nm: data.reg_mem_nm,
                                wCont: data.cont,
                            });


                            jQuery('#requestDateTimePicker').data('DateTimePicker').date(new Date(data.req_dt));
                            jQuery('#meaPlanDateTimePicker').data('DateTimePicker').date(new Date(data.mea_plan_dt));
                            jQuery('#meaFinDateTimePicker').data('DateTimePicker').date(new Date(data.mea_fin_dt));
                            return data;

                    })
                    .then(function(data) {

                        jQuery('#wFin_fl').prop("checked", data.fin_fl === 'Y' ? true : false);
                        jQuery('#wNature_fl').prop("checked", data.nature_fl === 'Y' ? true : false);

                    })
                    .fail(common.ajaxError)
                    .always(function() {

                        return false;
                    });

                }

                break;

            case 'measure' :

                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    if (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') {

                        jQuery('#btnReg2, #btnCancel2').attr('disabled', false);

                    }
                    common.setValues({
                        wMeasure_dt: obj.mea_dt,
                        wMeasureCont: obj.measure_cont,
                        wMea_seq: obj.mea_seq,
                    });
                }

                break;

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

            var rowid = jQuery("#requestList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery('#measuretab').prop('disabled',true);
                jQuery("#requestList").jqGrid("resetSelection"); // Grid Select Reset 처리

            }

            return false;
        }

        switch(objID) {

            case 'infoPanel':
                // 요청사항 입력폼

                common.clearElement('#' + objID); // form element

                var regBtn = jQuery('#btnReg');

                jQuery('#btnReg, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
                jQuery('#btnDel').attr('disabled',true);
                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경

                jQuery("select[name='equipGroup'] option").remove();
                common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []);

                break;
            case 'measureInfoPanel':
                // 조치사항 입력폼

                common.clearElement('#' + objID); // form element

                jQuery('#btnReg2, #btnCancel2').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.DEL_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경

                var rowid = jQuery("#measureGridList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null && isListReset) {

                    jQuery("#measureGridList").jqGrid("resetSelection"); // Grid Select Reset 처리
                }

                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend(str) {

        switch(str) {

            case 'request':
                // 로딩 시작
                jQuery.fn.loadingStart();

                var rowid = jQuery("#requestList").jqGrid('getGridParam', 'selrow');

                if (rowid === null) {
                    // 등록 모드
                    if (authCrud.REG_FL === 'N') return false;
                } else {
                    // 수정 모드
                    if (authCrud.MOD_FL === 'N') return false;
                }

                var formData = jQuery('#infoPanel :input');
                var reqData = formData.serializeObject();

                // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

                reqData = common.changeKeys(reqData, [
                    {k: 'wReq_seq', v: 'req_seq'},
                    {k: 'requestGroup', v: 'request_type'},
                    {k: 'equipTypeGroup', v: 'equip_type'},
                    {k: 'equipGroup', v: 'eqp_cd'},
                    {k: 'wReq_dt', v: 'req_dt'},
                    {k: 'wMea_plan_dt', v: 'mea_plan_dt'},
                    {k: 'wMea_fin_dt', v: 'mea_fin_dt'},
                    {k: 'wCont', v: 'cont'},
                    {k: 'wReq_nm', v: 'reg_mem_nm'},
                ]);
                reqData.fin_fl = jQuery("input:checkbox[id='wFin_fl']").is(":checked") === true ? 'Y' : 'N';


                // 데이터 전송
                jQuery.when(
                    jQuery.ajax({
                        url: './setRequestAct',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(reqData)
                    })
                )
                    .then(function (data) {
                        // 결과에 따라 다음 이벤트 처리
                        if (data > 0) {

                            common.setOSXModal('성공적으로 저장하였습니다.');

                            var obj = jQuery("#requestList");
                            var rowid = obj.jqGrid('getGridParam', 'selrow');
                            if (rowid !== null) {
                                panelClear(true);
                                obj.trigger("reloadGrid");
                                if (jQuery("select[name='equipTypeGroup'] option").val() === '') {
                                    jQuery("select[name='equipGroup'] option").remove();
                                    common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []); // 장비
                                }


                            } else {
                                // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                                panelClear(true);
                                dataReload('request');
                            }
                        } else {

                            common.setOSXModal('저장에 실패하였습니다.');
                        }
                    })
                    .fail(common.ajaxError)
                    .always(function () {

                        jQuery.fn.loadingComplete();
                        return false;
                    });

                return false;

                break;
            case 'measure':
                // 로딩 시작
                jQuery.fn.loadingStart();

                var rowid = jQuery("#measureGridList").jqGrid('getGridParam', 'selrow');

                if (rowid === null) {
                    // 등록 모드
                    if (authCrud.REG_FL === 'N') return false;
                } else {
                    // 수정 모드
                    if (authCrud.MOD_FL === 'N') return false;
                }

                var formData = jQuery('#measureInfoPanel :input');
                var reqData = formData.serializeObject();

                // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

                reqData = common.changeKeys(reqData, [
                    {k: 'wMea_seq', v: 'mea_seq'},
                    {k: 'wMeasure_dt', v: 'mea_dt'},
                    {k: 'wMeasureCont', v: 'measure_cont'}
                ]);
                reqData.cause_seq = jQuery("#requestList").jqGrid('getRowData', jQuery("#requestList").jqGrid('getGridParam', 'selrow')).req_seq;



                // 데이터 전송
                jQuery.when(
                    jQuery.ajax({
                        url: './setRequestMeasureAct',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(reqData)
                    })
                )
                    .then(function (data) {
                        // 결과에 따라 다음 이벤트 처리
                        if (data > 0) {

                            common.setOSXModal('성공적으로 저장하였습니다.');

                            var obj = jQuery("#measureGridList");
                            var rowid = obj.jqGrid('getGridParam', 'selrow');
                            if (rowid !== null) {
                                panelClear(false, 'measureInfoPanel', false)
                                obj.trigger("reloadGrid");

                            } else {
                                // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                                panelClear(false, 'measureInfoPanel', false);
                                dataReload('measureGrid');
                            }
                        } else {

                            common.setOSXModal('저장에 실패하였습니다.');
                        }
                    })
                    .fail(common.ajaxError)
                    .always(function () {

                        jQuery.fn.loadingComplete();
                        return false;
                    });

                return false;

                break;

        }


    }
    function dataSendDel() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#requestList").jqGrid('getGridParam', 'selrow');

        if (rowid === null) {
            // 삭제 모드
            if (authCrud.DEL_FL === 'N') return false;
        }

        var formData = jQuery('#infoPanel :input');
        var reqData = formData.serializeObject();

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

        reqData = common.changeKeys(reqData, [
            { k: 'wReq_seq', v: 'req_seq' },
        ]);


        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setRequestDel',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data > 0) {

                common.setOSXModal('성공적으로 삭제하였습니다.');

                var obj = jQuery("#requestList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {
                    obj.jqGrid('delRowData', rowid);
                    obj.trigger("reloadGrid");
                    panelClear(true);
                    dataReload('request');
                }
            } else {

                common.setOSXModal('삭제가 실패하였습니다.');
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
    function resizePanel() {

        var arrObj = [
            { list: "requestList", panel: "requestPanel" },
            { list: "measureGridList", panel: "measureGridPanel" }
        ];


        jQuery.each(arrObj, function (sIdx, data) {

            jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width());
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
            height: 500,                // 세로 크기
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

        switch (listID) {
            case 'requestList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    rowList: [10, 30, 50, 100],
                    colNames: ['구분', '장비유형', '장비', '완료 여부', '요청자', '요청일', '조치 예정일', '완료일', ''],
                    colModel: [
                        {
                            name: 'request_type_nm',
                            index: 'request_type_nm',
                            width: 2,
                            align: "center",
                            sortable: false
                        },
                        {name: 'equip_type_nm', index: 'equip_type_nm', width: 2, align: "left", sortable: false},
                        {name: 'eqp_nm', index: 'eqp_nm', width: 2, align: "left", sortable: false},
                        {name: 'fin_nm', index: 'fin_nm', width: 2, align: "center", sortable: false},
                        {name: 'reg_mem_nm', index: 'reg_mem_nm', width: 2, align: "center", sortable: false},
                        {
                            name: 'req_dt',
                            index: 'req_dt',
                            width: 2,
                            align: "center",
                            sortable: true,
                            editable: true,
                            formatter: 'date',
                            formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                            editrules: {required: true}
                        },
                        {
                            name: 'mea_plan_dt',
                            index: 'mea_plan_dt',
                            width: 2,
                            align: "center",
                            sortable: true,
                            editable: true,
                            formatter: 'date',
                            formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                            editrules: {required: true}
                        },
                        {
                            name: 'mea_fin_dt',
                            index: 'mea_fin_dt',
                            width: 2,
                            align: "center",
                            sortable: true,
                            editable: true,
                            formatter: 'date',
                            formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                            editrules: {required: true}
                        },

                        {name: 'req_seq', index: 'req_seq', hidden: true}
                    ],
                    onInitGrid: function () {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id) {

                        jQuery('.nav-tabs a:first').tab('show')
                        jQuery.when(
                            // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                            panelClear(true, '', false)
                        ).then(function (res) {
                                // 기본정보 입력폼 Setting
                                var ret = dataGrid.jqGrid('getRowData', id);

                                infoSetting(id, ret,'request');
                            }).always(function () {

                                return false;
                            });
                        jQuery('#measuretab').prop('disabled',false);
                    },
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {

                        jQuery('#wReq_nm').val(member_nm);

                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        jQuery.fn.loadingComplete();
                    }
                };

                break;
            case 'measureGridList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    height : 216,
                    rowList: [10, 30, 50, 100],
                    postData : {
                        cause_seq: jQuery("#requestList").jqGrid('getRowData', jQuery("#requestList").jqGrid('getGridParam', 'selrow')).req_seq
                    },
                    colNames: ['조치일자', '조치내용', '삭제',''],
                    colModel: [
                        {name: 'mea_dt',index: 'mea_dt',width: 2,align: "center",sortable: false},
                        {name: 'measure_cont', index: 'measure_cont',classes: 'ellipseJqGridCell', resizable: true, width: 3,  sortable: false},
                        {name: 'actions', width: 1, sortable: false, formatter: 'actions', formatoptions: {
                            keys: true,
                            editbutton:false,
                            delbutton: (authCrud.DEL_FL === "N" ? false : true),
                            delOptions: {
                                url: './setRequestMeasureDel',
                                mtype: 'POST',
                                ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                serializeDelData: function () {

                                    var formData = jQuery('#measureInfoPanel :input');
                                    var reqData = formData.serializeObject();
                                    reqData = common.changeKeys(reqData, [
                                        { k: 'wMea_seq', v: 'mea_seq' },
                                    ]);
                                    return JSON.stringify({
                                        mea_seq: reqData.mea_seq
                                    });
                                },
                                reloadAfterSubmit: false,
                                afterComplete: function (res) {

                                    common.setOSXModal((res.responseText > 0 ? '성공적으로 삭제되었습니다.' : '삭제에 실패하였습니다.'));
                                    dataReload(tarID);
                                    panelClear(false,'measureInfoPanel',false);
                                }
                            }

                        }},
                        {name: 'mea_seq',index: 'mea_seq',hidden : true},
                    ],
                    onSelectRow: function (id) {

                        jQuery.when(
                            // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                            panelClear(false, 'measureInfoPanel', false)
                        ).then(function (res) {
                                // 기본정보 입력폼 Setting
                                var ret = dataGrid.jqGrid('getRowData', id);

                                infoSetting(id, ret,'measure');
                            }).always(function () {

                                return false;
                            });
                    },
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {
                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        jQuery.fn.loadingComplete();
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
        infoSetting: infoSetting,
        panelClear : panelClear,
        dataSend: dataSend,
        dataSendDel: dataSendDel,
        resizePanel: resizePanel,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local','bootstrap-datepicker.lang','bootstrap-datetimepicker','bootstrap-switchery','jquery'], function (common, darkhand, lc,datepicker, datetimepicker,Switchery,jQuery) {
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

                        jQuery('#requestDateTimePicker,#meaPlanDateTimePicker,#meaFinDateTimePicker,#measureDateTimePicker').data('DateTimePicker').date(new Date());

                        lc.panelClear(true); // 전체 폼 초기화
                        lc.panelClear(false,'measureInfoPanel',false);
                        jQuery("#measureGridList").clearGridData();

                        lc.dataReload('request'); // 요청사항 목록
                        jQuery('.nav-tabs a[href="#infoPanel"]').tab('show')
                        jQuery.fn.loadingComplete();
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
                            lc.dataSend('request');
                        }
                    });
                }
                if(authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#measureInfoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('measureInfoPanel');
                        },
                        ret: "btnReg2",
                        state: function() {
                            // 검색 Event 처리
                            var lc = require('local');
                            lc.dataSend('measure');
                        }
                    });
                }
                if(authCrud.DEL_FL === 'Y' ) {

                    tw.push({
                        chk: jQuery("#infoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('infoPanel');
                        },
                        ret: "btnDel",
                        state: function() {
                            // 검색 Event 처리
                            var lc = require('local');
                            var result = confirm('선택 된 장애를 삭제 하시겠습니까?');
                            if(result) {
                                lc.dataSendDel();
                            }
                        }
                    });
                }
                break;
        }

        common.enterSend(tw);
    }
    function equipSetting() {
        var reQuestDateTypeList = [{ id:"req_dt",val :'요청일'} ,
            {id:'mea_plan_dt',val :'조치 예정일'},
            {id:'mea_fin_dt',val : '완료일'}];

        common.setSelectOpt(jQuery('#srcHeadGrp'), null, reQuestDateTypeList); // 기간항목
        common.setSelectOpt(jQuery('#srcGrp'), "- 전체 -", grpList); // 장비유형 검색창
        common.setSelectOpt(jQuery('#srcRequestGrp'), "- 전체 -", reQuestTypeList); // 요청유형
        common.setSelectOpt(jQuery('#requestGroup'), "- 선택 -", reQuestTypeList); // 요청유형
        common.setSelectOpt(jQuery('#equipTypeGroup'), "- 장비유형 선택 -", grpList); // 장비유형
        common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []); // 장비

        jQuery('#equipTypeGroup').change(function() {
            jQuery.when(
                jQuery.ajax({
                    url: '../getEquipList',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        eqp_grp_cd: jQuery(this).val()
                    }),
                })
            )
                .done(function(data) {
                    if (data ) {
                        // 성공 후
                        jQuery("select[name='equipGroup'] option").remove();
                        common.setSelectOpt(jQuery('#equipGroup'), null, data); // 장비유형
                    }
                    jQuery.when(
                        jQuery.ajax({
                            url: './getRdayRequestData',
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify({
                                req_seq: jQuery("#requestList").jqGrid('getRowData', jQuery("#requestList").jqGrid('getGridParam', 'selrow')).req_seq
                            })
                        })
                    ).then(function(data) {

                            common.setValues({
                                equipGroup: data.eqp_cd,
                            });


                            return data;

                    })
                    .fail(common.ajaxError)
                    .always(function() {

                        return false;
                    });
                })
                .fail(function (jqXhr, textStatus, errorThrown) {
                    jQuery("select[name='equipGroup'] option").remove();
                    common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []);
                })
                .always(function() {

                    return false;
                });
        })
    }
    function tabEvent() {

        jQuery('.nav-tabs a').on('shown.bs.tab', function(event) {
            var x = $(event.target).text();         // active tab

            if(x==='요청사항') {
                jQuery('#btnReg, #btnCancel').prop('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ) ? false : true);
                jQuery('#btnDel').prop('disabled', (authCrud.DEL_FL === 'Y' ) ? false : true);

                var rowid = jQuery("#measureGridList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null ) {

                    jQuery("#measureGridList").jqGrid("resetSelection"); // Grid Select Reset 처리
                }

                lc.panelClear(false, 'measureInfoPanel', false)


            }else {

                jQuery('#btnReg,#btnDel, #btnCancel').prop('disabled',  true );

                lc.dataReload('measureGrid');
            }
            var y = $(event.relatedTarget).text();  // previous tab
        });
    }
    function dateSetting() {
        var nowTemp = new Date();
        jQuery("#requestDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD',
            showTodayButton: true,
            showClear: true,
        });
        jQuery("#meaPlanDateTimePicker, #meaFinDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD',
            showTodayButton: true,
            showClear: true
        });
        jQuery('#measureDateTimePicker').datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD HH:mm:ss',
            showTodayButton: true,
            showClear: true
        });
        jQuery("#meaPlanDateTimePicker,#meaFinDateTimePicker").datetimepicker({
            useCurrent: false //Important! See issue #1075
        });
        jQuery("#requestDateTimePicker").on("dp.change", function (e) {

            jQuery("#meaPlanDateTimePicker").data("DateTimePicker").minDate(e.date);
            jQuery('#meaFinDateTimePicker').data("DateTimePicker").minDate(e.date);
        });


        jQuery('.input-daterange').datepicker({
            language: 'kr',
            format: 'yyyy-mm-dd',
            todayHighlight: true,
            endDate: new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0),
            todayBtn: "linked"
        }).find('input').each(function() {

            var nowVal = common.nowDate('-');

            switch (this.id) {
                case 'srcSDate':
                    // TODO : 개발 완료 후 m - 1 로 처리
                    nowVal = common.termDate(nowVal, 'm', -4, '-');
                    break;
            }

            jQuery(this).datepicker('update', nowVal).trigger('changeDate');
        });
    }

    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {

        jQuery('#measuretab').prop('disabled',true);
        // 권한에 따른 버튼 비활성화
        if(authCrud.READ_FL === 'N') {

            jQuery('#btnSrch').attr('disabled', true);
        }
        if(authCrud.REG_FL === 'N' || authCrud.MOD_FL === 'N') {

            jQuery('#btnReg, #btnCancel').attr('disabled', true);
        }
        if(authCrud.DEL_FL === 'N' ) {

            jQuery('#btnDel').attr('disabled', true);
        }


        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        dateSetting();

        equipSetting();
        Switchery.default ={
            color             : '#64bd63'
            , secondaryColor    : '#dfdfdf'
            , jackColor         : '#fff'
            , jackSecondaryColor: null
            , className         : 'switchery'
            , disabled          : false
            , disabledOpacity   : 0.5
            , speed             : '0.1s'
            , size              : 'default'

        }
        var testSwitchery = new Switchery(jQuery('#wNature_fl'), {color: '#3cc8ad'});


        tabEvent();

        jQuery('#measuretab').on('click', function() {
            if(jQuery('#measuretab').prop('disabled')) {
                common.setOSXModal('요청사항 목록을 선택 후 사용해주시기 바랍니다.');
            }
        });

        // 취소 버튼 클릭 시
        jQuery('#btnCancel').on('click', function () {

            jQuery('#requestDateTimePicker,#meaPlanDateTimePicker,#meaFinDateTimePicker,#measureDateTimePicker').data('DateTimePicker').date(new Date());

            lc.panelClear(true);
            lc.panelClear(false, 'measureInfoPanel', true);
            jQuery("#measureGridList").clearGridData();

            dateSetting();
        });
        jQuery('#btnCancel2').on('click', function () {

            jQuery('#measureDateTimePicker').data('DateTimePicker').date(new Date());
            lc.panelClear(false, 'measureInfoPanel', true);
        });

        // 그리드 초기화
        lc.gridSetting('request'); // 요청사항 목록

        lc.gridSetting('measureGrid'); // 요청사항 조치사항 목록

    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel();

    }).trigger('resize');
});