/**
 * User: 이준수
 * Date: 2017.05.19
 * Time: 오후 7:35
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'bootstrap-datepicker.lang', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

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

            case 'nmsCodeList' :
                // 연계시스템(NMS) 코드 리스트

                filterData = jQuery('#srcPanel :input').serializeObject();

                jqOpt = {
                    url: './getNmsCodeList'
                };


                break;
            case 'nmsDataList' :
                // 연계시스템(NMS) 데이터 리스트

                filterData = jQuery('#srcPanel :input').serializeObject();

                jqOpt = {
                    url: './getNmsDataList'
                };
                var rowid = jQuery('#nmsCodeList').jqGrid('getGridParam','selrow');
                if(rowid != null) filterData.host_cd = jQuery('#nmsCodeList').jqGrid('getRowData',rowid).host_cd;
                else filterData.host_cd = null;

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

    // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if(isAll) {
            // 모든 패널 초기화
            panelClear(false, 'nmsCodePanel', false);
            panelClear(false, 'nmsDataPanel', false);

            var rowid = jQuery("#nmsCodeList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#nmsCodeList").jqGrid("resetSelection"); // Grid Select reset
            }

            return false;
        }

        switch(objID) {
            case 'nmsCodePanel':
                // 연계시스템(NMS) 코드
                lastSel = undefined;
                break;
            case 'nmsDataPanel':
                // 연계시스템(NMS) 데이터
                lastSel2 = undefined;
                jQuery('#btnReport').attr('disabled', true); // 버튼 비활성화
                jQuery('#nmsDataList').jqGrid('clearGridData');
                break;
        }
    }




    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel(tarID) {

        if(tarID === undefined) tarID = 'nmsCode';

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
            case 'nmsCodeList' :
                // 연계시스템(NMS) 코드
                jqOpt = {
                    url: '',
                    editurl: './setNmsCodeAct',
                    height: 500,
                    scroll: 1,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', 'HOST 코드', '관리'],
                    colModel: [
                        {name: 'prev_host_cd', index: 'prev_host_cd', sortable: false, editable:true, hidden: true},
                        {name: 'host_cd', index: 'host_cd', width: 3, sortable: false, editable:true, editrules:{required: true}, editoptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg : 'HOST 코드를'});
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
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'nmsCode');
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
                            // 연계시스템(NMS) 데이터 그리드 초기화 및 갱신
                            dataReload('nmsData');
                        }
                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('nmsCode') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).then(function() {
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

            case 'nmsDataList' :
               // 연계시스템(NMS) 데이터
                jqOpt = {
                    url : '',
                    editurl : "./setNmsDataAct",
                    scroll: 1,
                    height: 500,
                    rowList: [10, 30, 50, 100],
                    colNames: ['','','','HOST 코드', '항목 코드', '수집일', '평균 값', '관리'],
                    colModel: [
                        {name: 'prev_host_cd', index: 'prev_host_cd', editable:true, hidden:true},
                        {name: 'prev_col_dt', index: 'prev_col_dt', hidden: true, editable:true},
                        {name: 'prev_itm_cd', index: 'prev_itm_cd', hidden: true, editable:true},
                        {name: 'host_cd', index: 'host_cd', width: 2, sortable: false, editable:true, editrules:{required: true}, editoptions: {
                            dataInit: function (el){
                                jQuery(el).attr({msg: 'HOST 코드를'});
                            }
                        }},
                        {name: 'itm_cd', index: 'itm_cd', width: 2, sortable: false, editable:true, editrules: {required: true}, editoptions: {
                            dataInit: function (el){
                                jQuery(el).attr({msg: '항목 코드를'});
                            }
                        }},
                        {
                            name: 'col_dt',
                            index: 'col_dt',
                            width: 2,
                            sortable:false,
                            align: 'center',
                            editable: false
                        },
                        {name: 'avg_val', index: 'avg_val', width: 2, sortable: false, editable:true, align: 'right', formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: '' }, editrules: { required: true, number: true }, editoptions:{
                            dataInit: function (el){
                                jQuery(el).attr({msg: '평균 값을'});
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
                                    lastSel2 = jQuery.jgrid.jqID(rowid);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'nmsData');
                                },
                                restoreAfterError: true // 저장 후 입력 폼 restore 자동/수동 설정
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
                            resizePanel('nmsData') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).done(function() {
                                // 권한 및 연계시스템 데이터 유무에 따른 엑셀 내보내기 활성화 처리
                                if (data.rows.length > 0) {
                                    jQuery('#btnReport').attr('disabled',(authCrud.READ_FL === 'Y' ? false : true));
                                }
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

        return false;
    }
    return {
        inputCheckScript: inputCheckScript,
        setEvents: formcheck.setEvents,
        panelClear: panelClear,
        dataReload: dataReload,
        resizePanel: resizePanel,
        gridResAction: gridResAction,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local', 'bootstrap-datetimepicker', 'jquery'], function (common, darkhand, lc, datetimepicker, jQuery) {
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
                        lc.dataReload('nmsCode'); // 연계시스템(NMS) 코드 목록
                        lc.dataReload('nmsData'); // 연계시스템(NMS) 데이터 목록
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
            case 'nmsCodeList' :

                id = lastSel;
                key = 'nmsCode';
                break;
            case 'nmsDataList' :

                id = lastSel2;
                key = 'nmsData';
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

        // 검색 폼
        var nowTemp = new Date();

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
                    nowVal = common.termDate(nowVal, 'm', -1, '-');
                    break;
            }

            jQuery(this).datepicker('update', nowVal).trigger('changeDate');
        });


        // 엑셀 내보내기 버튼 클릭 시
        jQuery('#btnReport').on('click', function() {
            // 엑셀 complete 체크 이벤트
            if (common.getCookie('export-end-state') === undefined) {

                common.setCookie('export-end-state', '0', 1); // 엑셀 Export 체크 하기 위해 쿠키 사용
            }

            excelInterId = setInterval(function() {

                if (common.getCookie('export-end-state') === '1') {

                    jQuery.fn.loadingComplete();

                    common.setCookie('export-end-state', '0', -1);
                    clearInterval(excelInterId);
                }
            }, 1000);

            jQuery.fn.loadingStart(); // 로딩 중

            var masObj = jQuery("#nmsCodeList");
            var masData = masObj.getRowData(masObj.jqGrid('getGridParam', 'selrow'));
            var srcSDate = jQuery("#srcSDate").val().replace('-','');
            var srcEDate = jQuery("#srcEDate").val().replace('-','');

            var form = "<form action='./exportNmsDataExcel' method='POST'>";

            form += "<input type='hidden' name='host_cd' value='" + masData.host_cd + "'>";
            form += "<input type='hidden' name='itm_cd' value='" + masData.itm_cd + "'>";
            form += "<input type='hidden' name='srcSDate' value='" + srcSDate + "'>";
            form += "<input type='hidden' name='srcEDate' value='" + srcEDate + "'>";

            form += "</form>";

            jQuery(form)
                .appendTo("body")
                .submit()
                .remove();
        });

        // jqGrid의 수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#nmsCodeList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });

        jQuery("#nmsDataList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('nmsCode'); // 연계시스템(NMS)코드 리스트
        lc.gridSetting('nmsData'); // 연계시스템(NMS)데이터 리스트
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {
        lc.resizePanel('nmsCode');
        lc.resizePanel('nmsData');

    }).trigger('resize');
});