/**
 * User: 이준수
 * Date: 2017.06.07
 * Time: 오후 4:08
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jquery-ui', 'bootstrap-datepicker.lang', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';
    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        return formcheck.checkForm(tarID);

    }

    // jqGrid data 리프레쉬
    function dataReload(tarID) {

        if (authCrud.READ_FL === 'N') {
            // 조회 권한이 없으면 데이터 조회 실패
            return false;
        }

        var listID = tarID + "List";
        var dataGrid = jQuery("#" + listID);
        var filterData = {};
        var jqOpt = {};
        switch(listID) {

            case 'reportCodeList' :
                // 보고서 코드 관리 리스트

                filterData = jQuery('#srcPanel :input').serializeObject();
                jqOpt = {
                    url: './getReportCodeList'
                };

                break;
            case 'reportEquipMapList' :
                // 장비연계 리스트

                filterData = jQuery('#srcPanel :input').serializeObject();
                filterData.rpt_cd = jQuery('#reportCodeList').jqGrid('getRowData',jQuery('#reportCodeList').jqGrid('getGridParam','selrow')).rpt_cd;
                jqOpt = {
                    url: './getReportEquipMapList'
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

    // 장비 select2(ajax)
    function equipSelect2(pVal){
        if(pVal === undefined){
            pVal = '';
        }

        return{
            url: './getEquipSelect2',
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            delay: 250,
            data: JSON.stringify( function (params){
                    return {
                        q: params.term,
                        sv: pVal
                    };
                }
            ),
            processResults: function (data) {

                return {
                    results: data
                };
            }
        }

    }

   // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if(isAll) {
            // 모든 패널 초기화
            panelClear(false, 'reportCodePanel', false);
            panelClear(false, 'reportTopicInfoPanel', false);

            var rowid = jQuery("#reportCodeList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#reportCodeList").jqGrid("resetSelection"); // 그리드 선택 초기화
            }

            return false;
        }

        switch(objID) {
            case 'reportCodePanel':
                // 보고서 코드 관리
                break;

            case 'reportEquipMapPanel':
                // 장비 연계
                jQuery('#reportEquipMapList').jqGrid('clearGridData');
                break;
        }
    }

    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel(tarID) {

        if(tarID === undefined) tarID = 'reportCode';

        jQuery('#' + tarID + 'List').jqGrid('setGridWidth', jQuery('#' + tarID + 'Panel').width() - 2);
    }

    // jqGrid 결과 후 액션
    function gridResAction(res, tarID) {

        var msg = '';

        switch(tarID) {
            default:

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
            rownumbers: false,           // Grid의 RowNumber 표출
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
            case 'reportCodeList' :
                // 보고서 코드관리
                jqOpt = {
                    url: '',
                    editurl: './setReportCodeAct',
                    height: 500,
                    scroll: 1,
                    scrollrows:true,
                    rowList: [10, 30, 50, 100],
                    colNames: ['코드', '부모코드', '명칭', '장비Dept', '점검자', '점검확인자', '사용', '점검결과', 'S/W유형', '월간 보고서 사용', '일간 보고서 정렬 순서', '월간 보고서 정렬 순서', '관리'],
                    colModel: [
                        {name: 'rpt_cd', index: 'rpt_cd', width: 1, sortable: false, editable:true, editrules:{required: true}, editoptions:{
                            dataInit: function(el) {
                                jQuery(el).attr({msg : '코드를'});
                            }
                        }},
                        {name: 'hig_rpt_cd', index: 'hig_rpt_cd', width: 1, sortable: false, editable:true, edittype: 'select',
                            editoptions: {
                                dataInit: function (el) {
                                },
                                value: common.setjqGridOpt('-선택-', higRptCdList)
                            }
                        },
                        {name: 'nm', index: 'nm', width: 1, sortable: false, editable:true, editrules:{required: true}, editoptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg : '명칭을'});
                            }
                        }},
                        {name: 'rpt_depth', index: 'rpt_depth', width: 1, sortable: false, editable:true, editrules:{required:true, number: true}, editoptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg : '장비Dept를', isDigitOnly: '1' });
                            }
                        }},
                        {name: 'inspector', index: 'inspector', width: 1, sortable: false, editable:true},
                        {name: 'confirmor', index: 'confirmor', width: 1, sortable: false, editable:true},
                        {name: 'use_fl', index: 'use_fl', width: 1, sortable: false, editable:true, edittype: 'checkbox', editoptions: { value: "Y:N" }},
                        {name: 'res_fl', index: 'res_fl', width: 1, sortable: false, editable:true, edittype: 'checkbox', editoptions: { value: "Y:N" }},
                        {name: 'sw_type_fl', index: 'sw_type_fl', width: 1, sortable: false, editable:true, edittype: 'checkbox', editoptions: { value: "Y:N" }},
                        {name: 'mrpt_use_fl', index: 'mrpt_use_fl', width: 1, sortable: false, editable:true, edittype: 'checkbox', editoptions: { value: "Y:N" }},
                        {name: 'rpt_sort_seq', index: 'rpt_sort_seq', width: 1, sortable: false, editable:true, editrules:{required: true, number: true}, editoptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg : '일간 보고서 정렬 순서를', isDigitOnly:'1'});
                            }
                        }},
                        {name: 'mrpt_sort_seq', index: 'mrpt_sort_seq', width: 1, sortable: false, editable:true,editrules:{required: true, number: true}, edtioptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg : '월간 보고서 정렬 순서를', isDigitOnly:'1'});
                            }
                        }},
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                keys: true,
                                editbutton: (authCrud.MOD_FL === "N" ? false : true),
                                delbutton: false,
                                onEdit: function (rowid) {
                                    // 수정 버튼 클릭 시 Event
                                    lastSel = jQuery.jgrid.jqID(rowid);

                                    // 수정할 수 없는 항목 disabled 처리
                                    jQuery('tr#' + lastSel).find("input").eq(0).attr("readonly", true);
                                    jQuery('tr#' + lastSel).find("select").attr("disabled", true);
                                    jQuery('#btnAdd2').attr('disabled', true);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                    jQuery('#btnAdd2').attr('disabled',false);
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'reportCode');
                                },
                                restoreAfterError: true // 저장 후 입력 폼 restore 자동/수동 설정
                            }
                        }
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id, status, event) {

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
                            // 장비 연계 그리드 초기화 및 갱신
                                    dataReload('reportEquipMap');
                        }
                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('reportCode') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).then(function() {
                            // 데이터가 존재 할 경우 처음 행 선택
                            if (data.rows.length > 0) {

                                dataGrid.jqGrid('setSelection', dataGrid.find('tr[id]:eq(0)').attr('id'));
                            }
                        }).done(function () {
                            // 위에 dataInit 부분에서 isDigitDotOnly attr추가 하면서 이벤트 처리하기 위해서 다시 호출
                            formcheck.setEvents();
                        }).always(function () {
                            // 그리드가 scroll 옵션으로 되어 있을 경우 문제 발생해서 해결책으로 아래 구문 추가
                            jQuery.fn.loadingComplete();
                            return false;
                        });
                    }
                };
                break;

            case 'reportEquipMapList' :
               // 장비연계
                jqOpt = {
                    url: '',
                    editurl: './setReportEquipMapAct',
                    scroll: 1,
                    scrollrows:true,
                    height: 500,
                    rowList: [10, 30, 50, 100],
                    colNames: ['','', '', '','장비', '순서', '관리'],
                    colModel: [
                        {name: 'rpt_cd', index: 'rpt_cd', hidden:true, sortable: false, editable:true},
                        {name: 'prev_sort_seq', index: 'prev_sort_seq', hidden:true, sortable: false, editable:true},
                        {name: 'prev_eqp_cd', index: 'prev_eqp_cd', hidden:true, sortable: false, editable:true},
                        {name: 'eqp_cd', index: 'eqp_cd', hidden:true, sortable: false, editable:true},
                        {name: 'eqp_nm', index: 'eqp_nm', width: 1, sortable: false, editable:true, editrules: { required: true }, edittype: 'select',
                            editoptions: {
                                dataInit: function (el) {
                                    var obj = jQuery(el);
                                    var rowid = obj.attr('rowid');
                                    var kObj = jQuery('#' + rowid + '_eqp_nm');

                                    obj.select2({
                                        minimumInputLength: 0, // 최소 검색어 개수
                                        ajax: equipSelect2()
                                    });
                                    // DB 값 setting하기 위한 옵션\
                                    obj
                                        .append(new Option(obj.parent('td').attr('title'), kObj.val(), true, true))
                                        .val(kObj.val())
                                        .trigger('change');

                                },
                                value: {}
                            }
                        },
                        {name: 'sort_seq', index: 'sort_seq', width: 2, sortable: false, editable:true, editrules:{ required: true, number: true} ,editoptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg: '순서를', isDigitOnly: '1' });
                            }
                        }},
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                keys: true,
                                editbutton: (authCrud.MOD_FL === "N" ? false : true),
                                delbutton: (authCrud.DEL_FL === "N" ? false : true),
                                onEdit: function (rowid) {
                                    // 수정 버튼 클릭 시 Event
                                    lastSel2 = jQuery.jgrid.jqID(rowid);
                                    jQuery('#btnAdd1').attr('disabled',true);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                    jQuery('#btnAdd1').attr('disabled',false);
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'reportEquipMap');
                                },
                                restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                                delOptions: {
                                    url: './setReportEquipMapDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {

                                        var reqData = dataGrid.jqGrid('getRowData', lastSel2);
                                        return JSON.stringify({
                                            rpt_cd: reqData.rpt_cd,
                                            prev_eqp_cd: reqData.prev_eqp_cd,
                                            sort_seq: reqData.sort_seq
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {
                                        dataReload('reportEquipMap');
                                        gridResAction(jQuery.parseJSON(res.responseText), 'reportEquipMap');

                                    }
                                }
                            }
                        }
                    ],
                    onSelectRow: function (id, status, event) {
                        // 행 선택 시
                        if (id && id !== lastSel2) {

                            if (lastSel2 !== undefined) {
                                jQuery(this).jqGrid('restoreRow', lastSel2);
                                jqFn.jqGridListIcon(this.id, lastSel2);

                                // jqGrid 버그 수정 addRow 후에 다른 row 선택을 여러번 해보면 highlight 버그 해결
                                var tmpObj = jQuery(this).find('#' + lastSel2);

                                if (tmpObj.hasClass('success')) {

                                    tmpObj.removeClass('success').removeAttr('aria-selected');
                                }
                            }

                            lastSel2 = id;
                        }

                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('reportEquipMap') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).done(function() {
                            }).always(function() {
                            // 로딩완료
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
        jQuery('#reportEquipMapList').jqGrid('sortableRows', {
            cursor: '.handle',
            update: function (e, html){
                var sort_seq = jQuery('#reportEquipMapList').jqGrid('getRowData',html.item[0].id).sort_seq;
                var rpt_cd = jQuery('#reportEquipMapList').jqGrid('getRowData',html.item[0].id).rpt_cd;
                var rowIndex = html.item[0].rowIndex;
                var eqp_cd = jQuery('#reportEquipMapList').jqGrid('getRowData', html.item[0].id).eqp_cd;
                var filterData = {
                    sort_seq : sort_seq,
                    rpt_cd : rpt_cd,
                    eqp_cd : eqp_cd,
                    rowIndex : rowIndex
                };
                jQuery.ajax({
                    type: 'POST',
                    url: './setEqpMapPosUpdate',
                    contentType : "application/json; charset=UTF-8",
                    data: JSON.stringify(filterData),
                    dataType: 'json',
                    success: function(){
                        dataReload('reportEquipMap');
                    },
                    error: function (xhr, status, error){
                        alert('통신에러');
                    }

                });
            }
        });

        return false;
    }
    return {
        inputCheckScript: inputCheckScript,
        equipSelect2: equipSelect2,
        setEvents: formcheck.setEvents,
        panelClear: panelClear,
        dataReload: dataReload,
        resizePanel: resizePanel,
        gridResAction: gridResAction,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local', 'bootstrap-datetimepicker', 'jquery', 'select2.lang'], function (common, darkhand, lc, datetimepicker, jQuery) {
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
                        lc.dataReload('reportCode'); // 보고서 코드 관리 목록

                    }
                });
                break;
        }

        common.enterSend(tw);
    }

    // 그리드 엔터 키 누를 경우 validation 및 저장 함수
    function gridEnterSave(listID) {

        var obj = jQuery('#' + listID);
        var id, key;

        switch(listID) {
            case 'reportCodeList' :

                id = lastSel;
                key = 'reportCode';

                break;

            case 'reportEquipMapList':

                id = lastSel2;
                key = 'reportEquipMap';

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

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // jqGrid의 수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#reportCodeList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });

        jQuery("#reportEquipMapList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });

        // 보고서 코드 관리 추가 버튼 클릭 이벤트
        jQuery('#btnAdd1').on('click', function() {
            jQuery('#btnAdd2').attr('disabled', true);
            jQuery.when(
                jQuery('#reportCodeList').jqGrid('setGridParam', { page: 1 })
            ).always(function(){

                    common.addRow('reportCodeList', {}, function() {
                        var lc = require('local');
                        lc.setEvents();
                    });
                });
        });
        // 장비 연계 추가 버튼 클릭 이벤트
        jQuery('#btnAdd2').on('click', function() {
            jQuery('#btnAdd1').attr('disabled',true);
            jQuery.when(
                jQuery('#reportEquipMapList').jqGrid('setGridParam', { page: 1 })
            ).always(function(){
                common.addRow('reportEquipMapList', {rpt_cd: jQuery('#reportCodeList').jqGrid('getRowData',jQuery('#reportCodeList').jqGrid('getGridParam','selrow')).rpt_cd,prev_sort_seq: 0, eqp_nm: '장비를 선택하세요.'},
                    function() {
                        var lc = require('local');
                        var rowid = jQuery('#reportEquipMapList').jqGrid('getGridParam','selrow');
                        jQuery('#' + rowid + '_eqp_nm').select2({
                            minimumInputLength: 0,
                            ajax: lc.equipSelect2()
                        });

                        lc.setEvents();
                    });
            });
        });


        // 그리드 초기화
        lc.gridSetting('reportCode'); // 보고서 코드관리 목록
        lc.gridSetting('reportEquipMap'); // 장비연계 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {
        lc.resizePanel('reportCode');
        lc.resizePanel('reportEquipMap');

    }).trigger('resize');
});