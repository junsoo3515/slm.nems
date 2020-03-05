/**
 * User: 이준수
 * Date: 2017.05.21
 * Time: 오후 11:09
 */
define('local', ['common', 'formcheck', 'jqGrid.setting','jquery', 'jqGrid', 'jquery-ui'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'evalItemInfoPanel':

                return formcheck.checkForm(tarID);

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
        var rowid = null;

        var jqOpt = {};

        switch(listID) {

            case 'evalGroupList' :
                // 그룹관리 목록
                jQuery("#evalGroupList").jqGrid("setSelection",1); // Grid Select reset
                rowid = jQuery('#evalGroupList').jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {

                    filterData.grp_cd = jQuery('#evalGroupList').getRowData(rowid).grp_cd;
                }

                filterData = jQuery('#srcPanel :input').serializeObject();

                jqOpt = {
                    url: './getEvalGroupList'
                };
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        search: true,
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

            case 'evalItemList' :
                // 평가항목 관리 탭의 평가기준 목록
                rowid = jQuery('#evalGroupList').jqGrid('getGridParam', 'selrow');
                if(rowid !== null){
                    filterData.grp_cd = jQuery('#evalGroupList').getRowData(rowid).grp_cd;
                    jqOpt = {
                        url: './getEvalItemList'
                    };
                }
                jQuery('#grp_nm').attr("disabled", false);
                jQuery('#btnReg, #btnCancel').attr('disabled', true);


                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

            case 'reportMapList':
                // 보고서 연결 탭의 보고서 연계 목록
                rowid = jQuery('#evalGroupList').jqGrid('getGridParam', 'selrow');
                if(rowid !== null){
                    filterData.grp_cd = jQuery('#evalGroupList').getRowData(rowid).grp_cd;
                    jqOpt = {
                        url: './getReportMapList'
                    };
                }
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

        }


    }


    // 기본정보 폼 Setting
    function infoSetting(rowID, obj, str) {

        switch(str) {

            case 'evalItemInfo' :

                var btnReg = jQuery('#btnReg');
                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    if(authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') {

                        jQuery('#btnReg, #btnCancel').attr('disabled', false);
                        btnReg.html(btnReg.html().replace("등록", "수정"));
                        jQuery('#grp_nm').attr("disabled", true);
                    }
                    if(jQuery('#reportConntab').attr('aria-expanded')==='true'){
                        dataReload('reportMap');
                    }else {
                        jQuery.when(
                            jQuery.ajax({
                                url: './getEvalItemInfoData',
                                type: "POST",
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                data: JSON.stringify({
                                    grp_cd: obj.grp_cd,
                                    itm_cd: obj.itm_cd

                                })
                            })
                        ).then(function(data) {
                                // 입력 폼 데이터 셋팅
                                common.setValues({
                                    itm_cd: data.itm_cd,
                                    grp_cd: data.grp_cd,
                                    grp_nm: data.grp_cd,
                                    basis_nm: data.basis_nm,
                                    eng_nm: data.eng_nm,
                                    cont: data.cont,
                                    chk_fl: data.chk_fl,
                                    good: data.good,
                                    good_expr: data.good_expr,
                                    warning: data.warning,
                                    warning_expr: data.warning_expr,
                                    wrong: data.wrong,
                                    wrong_expr: data.wrong_expr
                                });
                            })
                            .fail(common.ajaxError)
                            .always(function() {

                                return false;
                            });
                    }
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
            panelClear(false, 'evalPanel', false);
            panelClear(false, 'evalGroupPanel', false);
            jQuery("#evalGroupList").jqGrid("setSelection",1); // Grid Select reset

            var rowid = jQuery("#evalGroupList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {


                jQuery("#evalItemList").jqGrid("resetSelection"); // Grid Select Reset 처리

            }
            jQuery('#myTabs a:first').tab('show'); // 처음 탭으로 강제 이동

            return false;
        }

        switch(objID) {
            case 'evalGroupPanel':
                break;

            case 'evalItemInfoPanel':

                common.clearElement('#' + objID); // form element

                var btnReg = jQuery('#btnReg');

                btnReg.html((btnReg.html().replace("수정", "등록"))); // 등록버튼 명칭 변경


                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend(str) {

        switch(str) {

            case 'evalItemInfo':
                // 로딩 시작
                jQuery.fn.loadingStart();

                var rowid = jQuery("#evalItemList").jqGrid('getGridParam', 'selrow');
                var crud = null;
                var formData = jQuery('#evalItemInfoPanel :input');
                var reqData = formData.serializeObject();

                if (rowid === null) {
                    // 등록 모드
                    if (authCrud.REG_FL === 'N') return false;
                    crud = 'C';
                } else {
                    // 수정 모드
                    if (authCrud.MOD_FL === 'N') return false;
                    crud = 'U';
                    reqData.prev_itm_cd = jQuery('#evalItemList').jqGrid('getRowData',rowid).itm_cd;
                }

                reqData.crud = crud;
                reqData.chk_fl = 'N';
                if(jQuery('#chk_fl').is(':checked')) reqData.chk_fl =  'Y';


                // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

                reqData = common.changeKeys(reqData, [
                    {k: 'itm_cd', v: 'itm_cd'},
                    {k: 'grp_cd', v: 'grp_cd'},
                    {k: 'grp_nm', v: 'grp_nm'},
                    {k: 'basis_nm', v: 'basis_nm'},
                    {k: 'eng_nm', v: 'eng_nm'},
                    {k: 'cont', v: 'cont'},
                    {k: 'chk_fl', v: 'chk_fl'},
                    {k: 'good', v: 'good'},
                    {k: 'good_expr', v: 'good_expr'},
                    {k: 'warning', v: 'warning'},
                    {k: 'warning_expr', v: 'warning_expr'},
                    {k: 'wrong', v: 'wrong'},
                    {k: 'wrong_expr', v: 'wrong_expr'},
                    {k: 'crud', v: 'crud'},
                    {k: 'prev_itm_cd', v: 'prev_itm_cd'}


                ]);


                // 데이터 전송
                jQuery.when(
                    jQuery.ajax({
                        url: './setEvalItemInfoAct',
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

                                // 입력모드일 때는 입력 폼 초기화 및 평가기준 목록 그리드 reload 처리
                                panelClear(true);
                                dataReload('evalItem');
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

    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel() {

        var arrObj = [
            { list: "evalGroupList", panel: "evalGroupPanel" },
            { list: "evalItemList", panel: "evalItemPanel" },
            { list: "reportMapList", panel: "reportMapPanel" }
        ];


        jQuery.each(arrObj, function (sIdx, data) {

            jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width());
        });
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
        var grpnm = {};
        var jqDefault = {
            url: '',
            datatype: 'json',
            mtype: 'POST',
            sortname: '', // 소팅 인자
            sortorder: '', // 처음 소팅
            pager : pageID,
            rowNum: 30,                 // 처음에 로드될 때 표출되는 row 수
            rowList: [], // row 갯수 표출 세팅6
            viewsortcols: [true, 'vertical', true], // 소팅 인자 세팅
            rownumbers: false,           // Grid의 RowNumber 표출
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
            case 'evalGroupList':
                jqOpt = {
                    url: '',
                    editurl: './setEvalGroupAct',
                    scroll: 1,
                    scrollrows: true,
                    height: '789',
                    rowList: [10, 30, 50, 100],
                    colNames: ['코드', '명칭', '관리'],
                    colModel: [
                        {name: 'grp_cd', index: 'grp_cd', width: 1, sortable: false, editable:true, edittype:'text', editrules:{required: true},
                            editoptions:{
                                dataInit: function(el){
                                    jQuery(el).attr({msg: '코드를'});
                                }
                            }},
                        {name: 'grp_nm', index: 'grp_nm', width: 1, align: 'right', sortable: false, editable:true, edittype:'text'},
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
                                    // 수정할 수 없는 항목 disable 처리
                                    jQuery("tr#" + lastSel).find("input").eq(0).attr("disabled", true);
                                    jQuery('#btnAdd2').attr('disabled',true);
                                    jQuery('#btnAdd3').attr('disabled',true);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                    jQuery('#btnAdd2').attr('disabled',false);
                                    jQuery('#btnAdd3').attr('disabled',false);
                                    dataReload(tarID);
                                },
                                onSuccess: function (res) {
                                        // 저장 후 리턴 결과
                                        gridResAction(jQuery.parseJSON(res.responseText), 'evalGroup');

                                },
                                restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                                delOptions: {
                                    url: './setEvalGroupDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {

                                        var reqData = dataGrid.jqGrid('getRowData', lastSel);
                                        return JSON.stringify({
                                            grp_cd: reqData.grp_cd,
                                            grp_nm: reqData.grp_nm
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {
                                        dataReload('evalGroup');
                                        gridResAction(jQuery.parseJSON(res.responseText), 'evalGroup');

                                    }
                                }
                            }
                        }
                    ],
                    onInitGrid: function () {

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
                        }

                        dataReload('evalItem');
                        dataReload('reportMap');

                    },
                    loadComplete: function (data) {


                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('evalGroup') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
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

            case 'evalItemList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    height: '789',
                    rowList: [10, 30, 50, 100],
                    colNames: ['','','','','코드', '그룹', '명칭', '점검대상', '관리'],
                    colModel: [
                        {name:'sort_seq', index:'sort_seq', hidden:true, editable:true},
                        {name:'prev_itm_cd', index:'prev_itm_cd', hidden:true, editable:true},
                        {name:'chk_fl', index:'chk_fl', hidden:true, editable:true},
                        {name:'grp_cd', index:'grp_cd', hidden:true, editable:true},
                        {name: 'itm_cd', index: 'itm_cd', width: 1, align: "right", sortable: false},
                        {name: 'grp_nm', index: 'grp_nm', width: 1, align: "center", sortable: false},
                        {name: 'basis_nm', index: 'basis_nm', width: 1, sortable: false},
                        {
                            name: 'chk_fl', index: 'chk_fl', width: 1, align: 'center', sortable: false,
                            editable: true,
                            edittype:'checkbox', editoptions: {
                            dataInit: function (el) {
                                jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                            },
                            value: "Y:N"
                        }
                        },
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                editbutton: false,
                                delbutton: (authCrud.DEL_FL === 'N' ? false : true),
                                delOptions: {
                                    url: './setEvalItemDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {
                                        var rowid = jQuery('#evalItemList').jqGrid('getGridParam','selrow');
                                        var reqData = dataGrid.jqGrid('getRowData', rowid);
                                        return JSON.stringify({
                                            itm_cd: reqData.itm_cd,
                                            grp_cd: reqData.grp_cd
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {
                                        gridResAction(jQuery.parseJSON(res.responseText), 'evalItem');
                                        panelClear(false,'evalItemInfoPanel',false);
                                    }
                                }
                            }
                        }

                    ],
                    onInitGrid: function () {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id) {

                        jQuery.when(
                            // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                            panelClear(true, '', false)
                        ).then(function (res) {
                                // 기본정보 입력폼 Setting
                                var ret = dataGrid.jqGrid('getRowData', id);
                                infoSetting(id, ret,'evalItemInfo');
                            }).always(function () {

                                return false;
                            });
                        jQuery('#reportConntab').prop('disabled',false);
                    },
                    loadComplete: function () {
                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        jQuery.fn.loadingComplete();
                    }
                };

                break;

            case 'reportMapList':

                jqOpt = {
                    url: '',
                    editurl: './setReportMapAct',
                    scroll: 1,
                    scrollrows: true,
                    height:510,
                    rowList: [10, 30, 50, 100],
                    rownumbers: true,
                    postData : {
                        grp_nm: jQuery("#evalGroupList").jqGrid('getRowData', jQuery("#evalGroupList").jqGrid('getGridParam', 'selrow')).grp_nm
                    },
                    colNames: ['','','그룹명','보고서', '관리'],
                    colModel: [
                        {name:'grp_cd', index:'grp_cd', hidden:true, editable:true},
                        {name:'rpt_cd', index:'rpt_cd', hidden:true, editable:true},
                        {name: 'grp_nm', index: 'grp_nm', width: 4, editable: true, edittype: 'select', sortable : false, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {
                                    jQuery(el).attr({msg : '그룹명을'});

                                },
                                value: common.setjqGridOpt(null, groupNmList)

                            }
                        },
                        {name: 'nm', index: 'nm', width: 4, editable: true, edittype: 'select', sortable : false, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({msg : '항목이름을'});
                                },
                                value: common.setjqGridOpt('-선택-', reportNmList)

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
                                    jQuery('#btnAdd1').attr('disabled',true);

                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                    jQuery('#btnAdd1').attr('disabled',false);
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    dataReload('reportMap');
                                    gridResAction(jQuery.parseJSON(res.responseText), 'reportMap');
                                },
                                restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                                delOptions: {
                                    url: './setReportMapDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {

                                        var reqData = dataGrid.jqGrid('getRowData', lastSel);
                                        console.log(JSON.stringify(reqData));
                                        return JSON.stringify({
                                            rpt_cd: reqData.rpt_cd,
                                            grp_cd: reqData.grp_cd
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {
                                        dataReload('reportMap');
                                        gridResAction(jQuery.parseJSON(res.responseText), 'reportConn');
                                    }
                                }
                            }
                        }
                    ],
                    onInitGrid: function () {

                        dataReload(tarID);
                    }, onSelectRow: function (id) {
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
        jQuery('#evalItemList').jqGrid('sortableRows', {
            cursor:'.handle',
            update: function (e, html){
                var sort_seq = jQuery('#evalItemList').jqGrid('getRowData',html.item[0].id).sort_seq;
                var itm_cd = jQuery('#evalItemList').jqGrid('getRowData',html.item[0].id).itm_cd;
                var rowIndex = html.item[0].rowIndex;
                var grp_cd = jQuery('#evalItemList').jqGrid('getRowData',html.item[0].id).grp_cd;
                var filterData = {
                    sort_seq: sort_seq,
                    itm_cd: itm_cd,
                    rowIndex: rowIndex,
                    grp_cd: grp_cd
                };

                jQuery.ajax({
                    type: 'POST',
                    url: './setEvalItemPosUpdate',
                    contentType: 'application/json; charset=UTF-8',
                    data: JSON.stringify(filterData),
                    dataType: 'json',
                    success: function(){
                        dataReload('evalItem');
                    },
                    error: function (xhr, status, error){
                        alert('통신에러')
                    }
                })

            }
            });

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

require(['common', 'darkhand', 'local' ,'bootstrap-datepicker.lang','bootstrap-datetimepicker', 'jquery'], function (common, darkhand, lc, datepicker, datetimepicker, jQuery) {
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
                        lc.panelClear(false,'evalGroup',false);

                        jQuery("#evalItemList").clearGridData();
                        lc.dataReload('evalGroup'); // 그룹관리 목록 갱신


                        jQuery('.nav-tabs a[href="#evalPanel"]').tab('show');

                        jQuery.fn.loadingComplete();
                    }
                });

                if(authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#evalItemInfoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('evalItemInfoPanel');
                        },
                        ret: "btnReg",
                        state: function() {
                            // 평가기준 등록 이벤트
                            var lc = require('local');
                            lc.dataSend('evalItemInfo');
                            lc.panelClear(false,'evalItemInfoPanel',false);
                            lc.dataReload('evalItem');
                            jQuery('#btnReg, #btnCancel').attr('disabled', true);
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
            case 'evalGroupList' :

                id = lastSel;
                key = 'evalGroup';

                break;

            case 'evalItemList':

                id = lastSel2;
                key = 'evalItem';

                break;

            case 'reportMapList':
                id = lastSel;
                key = 'reportMap';
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

    var tabObj = jQuery('#myTabs a[data-toggle="tab"]');

    // 탭 show trigger 이벤트
    tabObj.off('shown.bs.tab').on('shown.bs.tab', function (e) {
        // 해당 계통의 펌프현황 정보 가져오기(헤더 설정을 위한)
        var tabIdx = tabObj.index(e.target);

        switch(tabIdx) {
            case 0:
                lc.panelClear(false,'evalItemInfoPanel',false);
                lc.dataReload('evalItem');

                break;
            case 1:
                // 보고서 연결 탭
                jQuery('#btnAdd3').attr('disabled', authCrud.DEL_FL === 'N' ? true : false); // 권한에 따라서 삭제 버튼 권한 처리

                lc.dataReload('reportMap'); // 보고서 연계 조회

                break;
        }
    });
    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {


        // 권한에 따른 버튼 비활성화
        if(authCrud.READ_FL === 'N') {

            jQuery('#btnSrch').attr('disabled', true);
        }
        if(authCrud.REG_FL === 'N' || authCrud.MOD_FL === 'N') {

            jQuery('#btnAdd1, #btnAdd2, #btnReg, #btnCancel').attr('disabled', true);
        }


        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // jqGrid의 수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#evalGroupList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });

        jQuery("#reportMapList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });


        common.setSelectOpt(jQuery('#grp_nm'), '-선택-', groupNmList); // 그룹명


        // 그룹관리 추가 버튼 클릭 시
        jQuery('#btnAdd1').on('click', function(){
            lc.panelClear(false,'evalItemInfoPanel',false);
            jQuery('#btnAdd2').attr('disabled',true);
            jQuery('#btnAdd3').attr('disabled',true);
            jQuery.when(
                jQuery('#evalGroupList').jqGrid('setGridParam', { page: 1 })
            ).always(function(){
                    common.addRow('evalGroupList', { grp_cd :'', grp_nm : ''  }, function() {

                        var lc = require('local');

                        lc.setEvents();
                    });
                });
        });

        // 평가기준 목록 추가 버튼 클릭 시
        jQuery('#btnAdd2').on('click', function(){
            if(authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') {

                jQuery('#btnReg, #btnCancel').attr('disabled', false);
            }
            jQuery('#grp_nm').attr("disabled", false);
            lc.panelClear(false,'evalItemInfoPanel');
        });

        // 취소 버튼 클릭 시
        jQuery('#btnCancel').on('click', function () {
            lc.panelClear(false,'evalItemInfoPanel',false);
            jQuery('#evalItemList').jqGrid('resetSelection');
            jQuery('#btnReg, #btnCancel').attr('disabled', true);
            jQuery('#grp_nm').attr("disabled", false);
        });

        // 보고서 연결 추가 버튼 클릭 시
        jQuery('#btnAdd3').on('click', function () {
            jQuery('#btnAdd1').attr('disabled',true);
            jQuery.when(
                jQuery('#reportMapList').jqGrid('setGridParam', { page:1 })
            ).always(function(){
                    common.addRow('reportMapList', { grp_nm :jQuery('#evalGroupList').jqGrid('getRowData',jQuery('#evalGroupList').jqGrid('getGridParam','selrow')).grp_nm, nm : ''  }, function() {

                        var lc = require('local');

                        lc.setEvents();
                    });
                });
        });

        // 그리드 초기화
        lc.gridSetting('evalGroup'); // 그룹관리 목록
        lc.gridSetting('evalItem'); // 평가기준 목록
        lc.gridSetting('reportMap'); // 보고서 연계 목록

    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel();

    }).trigger('resize');
});