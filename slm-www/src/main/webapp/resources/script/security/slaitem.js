/**
 * User: 이준수
 * Date: 2017.05.23
 * Time: 오후 6:04
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {



        if(!(parseFloat(jQuery('[id^="point_"]').val().trim()) > 0 && parseFloat(jQuery('[id^="point_"]').val().trim()) < 1) && !(jQuery('[id^="point_"]').val() === '') ){
            common.setOSXModal('각 배점을 확인해주세요.', jQuery('[id^="point_"]'));
            return false;
        }

        return formcheck.checkForm(tarID);
    }

    // jqGrid data 리프레쉬
    function dataReload(tarID) {

        var listID = tarID + "List";
        var dataGrid = jQuery("#" + listID);
        var filterData = {};

        var obj = null;
        var rowid = null;
        var jqOpt = {};

        switch(listID) {

            case 'slaEvalList' :
                // 평가기준 관리 목록
                filterData = jQuery('#srcPanel :input').serializeObject();
                jqOpt = {
                    url: './getSlaEvalList'
                };
                break;
            case 'reportConnList' :
                // 보고서 연결
                obj = jQuery("#slaEvalList");
                rowid = obj.jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {

                    filterData['eval_cd'] = obj.getRowData(rowid).eval_cd;
                }
                jqOpt = {
                    url: './getReportConnList'
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


    // 평가기준 입력 폼 Setting
    function infoSetting(rowID, obj) {

        var regBtn = jQuery('#btnReg');

        if (rowID) {
            // 리스트 폼에 있는 객체 입력 폼 Setting
            jQuery('#eval_cd').attr("readonly", true); // 평가 기준 코드(수정 모드에서 수정 못하게 비활성화)

            if(authCrud.MOD_FL === 'Y') {

                jQuery('#btnReg, #btnCancel').attr('disabled', false);
                regBtn.html(regBtn.html().replace("등록", "수정"));
            }

            jQuery.when(
                jQuery.ajax({
                    url: './getSlaEvalData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        eval_cd: obj.eval_cd
                    })
                })
            ).then(function(data) {

                // 그리드의 값을 가지고 입력 값 setting
                common.setValues({
                    eval_cd : data.eval_cd,
                    item_nm : data.item_nm,
                    weight : data.weight,
                    target : data.target,
                    mea_method : data.mea_method,
                    mea_tool : data.mea_tool,
                    mea_period : data.mea_period,
                    arith_expression_nm : data.arith_expression_nm,
                    arith_expression : data.arith_expression,
                    mea_cont : data.mea_cont,
                    max_lev : data.max_lev,
                    min_lev : data.min_lev,
                    score_exce_nm : data.score_exce_nm,
                    score_good_nm : data.score_good_nm,
                    score_normal_nm : data.score_normal_nm,
                    score_insuf_nm : data.score_insuf_nm,
                    score_bad_nm : data.score_bad_nm,
                    score_exce : data.score_exce,
                    score_good : data.score_good,
                    score_normal : data.score_normal,
                    score_insuf : data.score_insuf,
                    score_bad : data.score_bad,
                    point_exce : data.point_exce,
                    point_good : data.point_good,
                    point_normal : data.point_normal,
                    point_insuf : data.point_insuf,
                    point_bad : data.point_bad,
                    mea_res : data.mea_res,
                    mea_point : data.mea_point,
                    mea_res_unit : data.mea_res_unit
                });

                return data;
            }).then(function(data) {
                // DB에서 검색된 결과 값 Setting
            }).done(function() {
            })
            .fail(common.ajaxError)
            .always(function() {
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
            panelClear(false, 'slaEvalInfoPanel', false);
            panelClear(false, 'reportConnPanel', false);

            var rowid = jQuery("#slaEvalList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#slaEvalList").jqGrid("resetSelection"); // Grid Select reset
            }

            return false;
        }

        switch(objID) {

            case 'slaEvalInfoPanel':
                // 평가기준 입력폼
                jQuery('#eval_cd').attr("readonly", false);

                common.clearElement('#' + objID); // form element
                jQuery('#reportConnList').jqGrid('clearGridData'); // 그리드 데이터 초기화

                var regBtn = jQuery('#btnReg');

                jQuery('#btnReg, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경
                break;

            case 'reportConnPanel':
                jQuery('#btnAdd').attr('disabled', true); // 버튼 비활성화
                break;

        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#slaEvalList").jqGrid('getGridParam', 'selrow');
        var evalVal = jQuery("#eval_cd").val();

        if (rowid === null) {
            // 등록 모드
            if (authCrud.REG_FL === 'N') return false;
        } else {
            // 수정 모드
            if (authCrud.MOD_FL === 'N') return false;
        }

        var formData = jQuery('#slaEvalInfoPanel :input');
        var tmpData = formData.filter(":disabled").attr('disabled', false);
        var reqData = formData.serializeObject();

        tmpData.attr('disabled', true);

        // 사용자 고유번호
        reqData.eval_cd = evalVal;

        reqData['divCd'] = (rowid === null ? 0 : 1);

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)
        reqData = common.changeKeys(reqData, [
            { k: 'eval_cd', v: 'eval_cd' },
            { k: 'item_nm', v: 'item_nm' },
            { k: 'weight', v: 'weight' },
            { k: 'target', v: 'target' },
            { k: 'mea_res_unit', v: 'mea_res_unit' },
            { k: 'mea_method', v: 'mea_method' },
            { k: 'mea_tool', v: 'mea_tool' },
            { k: 'mea_period', v: 'mea_period' },
            { k: 'arith_expression_nm', v: 'arith_expression_nm' },
            { k: 'arith_expression', v: 'arith_expression' },
            { k: 'mea_cont', v: 'mea_cont' },
            { k: 'max_lev', v: 'max_lev' },
            { k: 'min_lev', v: 'min_lev' },
            { k: 'score_exce_nm', v: 'score_exce_nm' },
            { k: 'point_exce', v: 'point_exce' },
            { k: 'score_exce', v: 'score_exce' },
            { k: 'score_good_nm', v: 'score_good_nm' },
            { k: 'point_good', v: 'point_good' },
            { k: 'score_good', v: 'score_good' },
            { k: 'score_normal_nm', v: 'score_normal_nm' },
            { k: 'point_normal', v: 'point_normal' },
            { k: 'score_normal', v: 'score_normal' },
            { k: 'score_insuf_nm', v: 'score_insuf_nm' },
            { k: 'point_insuf', v: 'point_insuf' },
            { k: 'score_insuf', v: 'score_insuf' },
            { k: 'score_bad_nm', v: 'score_bad_nm' },
            { k: 'point_bad', v: 'point_bad' },
            { k: 'score_bad', v: 'score_bad' },
            { k: 'divCd', v: 'divCd' }
        ]);

        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setSlaEvalAct',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data > 1) {

                common.setOSXModal('평가기준을 성공적으로 저장하였습니다.');

                var obj = jQuery("#slaEvalList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {
                    // 수정모드일 때 성능향상을 고려하여 그리드에 바로 데이터 갱신 처리
                    obj.jqGrid('setRowData', rowid, {
                        eval_cd: reqData.eval_cd,
                        item_nm: reqData.item_nm,
                        weight: reqData.weight,
                        target: reqData.target,
                        mea_method: reqData.mea_method,
                        mea_tool: reqData.mea_tool,
                        mea_period: reqData.mea_period,
                        score_exec_nm: reqData.score_exec_nm
                    });
                } else {
                    // 입력모드일 때는 입력 폼 초기화 및 평가기준 관리 목록 그리드 reload 처리
                    panelClear(true);
                    dataReload('slaEval');
                }
            } else {

                common.setOSXModal('저장을 실패하였습니다.');
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
                    { list: "slaEvalList", panel: "slaEvalPanel" },
                    { list: "reportConnList", panel: "reportConnPanel" }
                ];

                break;
            case 1 :
                var arrObj = [
                    { list: "slaEvalList", panel: "slaEvalPanel" }
                ];

                break;
            case 2 :
                var arrObj = [
                    { list: "reportConnList", panel: "reportConnPanel" }
                ];

                break;
        }

        jQuery.each(arrObj, function (sIdx, data) {

            jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width() - 2);
        });
    }

    // jqGrid 결과 후 액션
    function gridResAction(res, tarID) {

        var msg = '';

        switch(tarID) {

            case 'reportConn':

                msg = (res.isSuccess === true ? '성공적으로 적용되었습니다.' : '적용에 실패하였습니다.');
                break;
        }

        common.setOSXModal(msg);
        dataReload(tarID);
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
            case 'slaEvalList' :
                // 사용자 목록
                jqOpt = {
                    url : './getSlaEvalList',
                    rowList: [10, 30, 50, 100],
                    colNames: ['코드', '명칭', '가중치', '대상', '측정방법', '측정 툴', '측정 보고', '계산식(한글)'],
                    colModel: [
                        {name: 'eval_cd', index: 'eval_cd', width: 1, sortable:false},
                        {name: 'item_nm', index: 'item_nm', width: 3, sortable:false},
                        {name: 'weight', index: 'weight', width: 1, sortable:false},
                        {name: 'target', index: 'target', width: 3, sortable:false},
                        {name: 'mea_method', index: 'mea_method', width: 2, sortable:false},
                        {name: 'mea_tool', index: 'mea_tool', width: 2, sortable:false},
                        {name: 'mea_period', index: 'mea_period', width: 2, sortable:false},
                        {name: 'arith_expression_nm', index: 'arith_expression_nm', width: 5, sortable:false}
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id) {
                        // 보고서 연결 그리드 초기화 및 갱신
                        jQuery.when(
                        ).then(function() {
                                // 기본정보 입력폼 Setting
                                var ret = dataGrid.jqGrid('getRowData', id);
                                infoSetting(id, ret);
                                // 권한에 따른 보고서 연결 추가 활성화 처리
                                jQuery('#btnAdd').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ? false : true));

                            }).always(function () {
                                // 보고서 연결 데이터 reload
                                dataReload('reportConn');
                            });

                    },
                    loadComplete: function () {
                        resizePanel(1); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        jQuery.fn.loadingComplete();
                    }
                };

                break;
            case 'reportConnList' :
                // 보고서 연결 목록
                jqOpt = {
                    url : '',
                    editurl : "./setReportConnAct",
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [],         // row 갯수 표출 세팅
                    rownumbers: true,  // Grid의 RowNumber 표출
                    pager : pageID,
                    scroll: 1,
                    scrollrows: true,
                    viewrecords: true, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    height: 400,
                    colNames: ['', '', '','보고서', '관리'],
                    colModel: [
                        {name: 'rpt_cd', index: 'rpt_cd', hidden:true , editable: true},
                        {name: 'eval_cd', index: 'eval_cd',hidden:true, editable: true },
                        {name: 'prev_nm', index: 'prev_nm',hidden:true, editable: true },
                        {name: 'nm', index: 'nm', width: 4, editable: true, edittype: 'select', sortable : false, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({msg : '보고서를'});
                                },
                                value: common.setjqGridOpt('-선택-', nmList)

                            }
                        },
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                editbutton: (authCrud.MOD_FL === "N" ? false : true),
                                delbutton: (authCrud.DEL_FL === 'N' ? false : true),
                                onEdit: function (rowid) {
                                    // 수정 버튼 클릭 시 Event
                                    lastSel = jQuery.jgrid.jqID(rowid);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    dataReload('reportConn');
                                    gridResAction(jQuery.parseJSON(res.responseText), 'reportConn');
                                },
                                restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                                delOptions: {
                                    url: './setReportConnDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {

                                        var reqData = dataGrid.jqGrid('getRowData', lastSel);

                                        return JSON.stringify({
                                            rpt_cd: reqData.rpt_cd,
                                            eval_cd: reqData.eval_cd
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {

                                        gridResAction(jQuery.parseJSON(res.responseText), 'reportConn');
                                    }
                                }
                            }
                        }
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    onSelectRow: function(id, status, event){
                        // 행 선택 시
                        if (id && id !== lastSel) {

                            if (lastSel !== undefined) {

                                jQuery(this).jqGrid('restoreRow', lastSel);
                                jqFn.jqGridListIcon(this.id, lastSel);

                                // jqGrid 버그 수정 addRow 후에 다른 row 선택을 여러번 해보면 highlight 버그 해결
                                var tmpObj = jQuery(this).find('#' + lastSel);

                                if (tmpObj.hasClass('success')) {

                                    tmpObj.removeClass('success').removeAttr('aria-selected');
                                }
                            }

                            lastSel = id;
                        }
                    },
                    loadComplete: function () {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel(2) // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).done(function() {
                        }).always(function() {
                            // 그리드가 scroll 옵션으로 되어 있을 경우 문제 발생해서 해결책으로 아래 구문 추가
                            if(isAddState) {
                                // 활성화 된 버튼만 클릭 이벤트 처리
                                jQuery('#btnAdd:enabled').trigger('click');
                            }

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
        infoSetting: infoSetting,
        panelClear : panelClear,
        dataSend: dataSend,
        resizePanel: resizePanel,
        gridSetting: gridSetting,
        gridResAction: gridResAction
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
                    ret: "btnSrch",
                    state: function() {

                        var lc = require('local');

                        jQuery.fn.loadingStart();

                        lc.panelClear(true); // 전체 폼 초기화
                        lc.dataReload('slaEval'); // 평가기준 관리 목록
                        lc.dataReload('reportConn'); // 보고서 연결 목록
                    }
                });
                if(authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#slaEvalInfoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('slaEvalInfoPanel');
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

    // 그리드 엔터 키 누를 경우 validation 및 저장 함수
    function gridEnterSave(listID) {

        var obj = jQuery('#' + listID);
        var id, key;

        switch(listID) {
            case 'reportConnList' :

                id = lastSel;
                key = 'reportConn';

                break;
        }

        if (lc.inputCheckScript(listID) === true) {

            var opers = ( jQuery("#" + id).hasClass('jqgrid-new-row') ? "add" : "edit" );

            obj.jqGrid('saveRow', id, {
                extraparam: { oper: opers },
                successfunc: function (res) {
                    // 리턴 결과
                    lc.gridResAction(jQuery.parseJSON(res.responseText), key);
                },
                restoreAfterError: false //저장 실패 시 restore기능 사용 유무(true : 입력 상태 grid 복원, false : 입력 상태 계속 유지)
            });
        }
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

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // 보고서 연결 추가 버튼 클릭시
        jQuery('#btnAdd').on('click', function() {
            jQuery.when(
                jQuery('#reportConnList').jqGrid('setGridParam', { page: 1 })
            ).always(function(){
                jQuery("#reportConnList").jqGrid("setSelection",1); // Grid Select reset
                var rptRowid = jQuery("#reportConnList").jqGrid('getGridParam', 'selrow');
                var slaRowid = jQuery("#slaEvalList").jqGrid('getGridParam', 'selrow');

                common.addRow('reportConnList', { rpt_cd : jQuery("#reportConnList").getRowData(rptRowid).rpt_cd , eval_cd : jQuery("#slaEvalList").getRowData(slaRowid).eval_cd  }, function() {

                    var lc = require('local');

                    lc.setEvents();
                });
            });
        });

        // 취소 버튼 클릭시
        jQuery('#btnCancel').on('click', function () {

            lc.panelClear(true);
        });

        // jqGrid의 수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#reportConnList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });


        // 그리드 초기화
        lc.gridSetting('slaEval'); // 평가기준 관리 목록
        lc.gridSetting('reportConn'); // 보고서 연결 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel(0);

    }).trigger('resize');
});