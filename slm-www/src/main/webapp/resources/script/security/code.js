/**
 * User: 현재호
 * Date: 2016.04.22
 * Time: 오전 10:17
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
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

            case 'codeList' :
                // 코드 목록
                filterData = jQuery('#srcPanel :input').serializeObject();
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
            panelClear(false, 'codePanel', false);

            var rowid = jQuery("#codeList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#codeList").jqGrid("resetSelection"); // Grid Select Reset 처리
            }

            return false;
        }

        switch(objID) {

            case 'codePanel':
                // 코드관리 그리드
                lastSel = undefined;

                jQuery('#btnAdd').attr('disabled', (authCrud.REG_FL === 'Y') ? false : true); // 추가버튼 Style 변경
                jQuery('#codeList').jqGrid('clearGridData'); // 그리드 데이터 초기화
                break;
        }
    }

    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel(id) {

        if (id === undefined) {

            id = null;
        }

        if (id !== null) {

            jQuery.each([{ list: id + "List", panel: id + "Panel" }], function (sIdx, data) {

                jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width() - 2);
            });
        }
    }

    // jqGrid 결과 후 액션
    function gridResAction(res, tarID) {

        common.setOSXModal((res.isSuccess === true ? '성공적으로 적용되었습니다.' : '적용에 실패하였습니다.'));

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

        switch(listID) {
            case 'codeList' :
                // 코드 목록
                jqOpt = {
                    url : './getCodeList',
                    editurl: './setCodeAct',
                    scroll: 1,
                    scrollrows: true,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '코드구분', '코드명', '코드', '사용', '관리'],
                    colModel: [
                        {name: 'jong_cd', index: 'jong_cd', editable: true, hidden: true},
                        {name: 'jong_nm', index: 'jong_nm', width: 4, editable: true, edittype: 'select', editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({msg : '코드구분을'});
                                },
                                value: common.setjqGridOpt('-선택-', codeJongList)
                            }
                        },
                        {name: 'cd_nm', index: 'cd_nm', width: 10, editable: true, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({msg : '코드명을'});
                                }
                            }
                        },
                        {name: 'com_cd', index: 'com_cd', width: 2, align: 'center', editable: true, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({
                                        msg : '코드를',
                                        isEngDigitOnly : 1
                                    });
                                }
                            }},
                        {name: 'use_fl', index: 'use_fl', width: 1, align: 'center', editable: true, edittype: 'checkbox', editoptions: { value: "Y:N" }},
                        {name: 'myac', width: 1, sortable: false, classes: 'text-center', formatter: 'actions', formatoptions: {
                            keys: true,
                            editbutton: (authCrud.MOD_FL === "N" ? false : true),
                            delbutton: false,
                            onEdit: function (rowid) {
                                // 수정 버튼 클릭 시 Event
                                if (lastSel !== rowid) {

                                    jQuery(this).jqGrid('restoreRow', lastSel);
                                    jqFn.jqGridListIcon(this.id, lastSel);
                                }

                                lastSel = jQuery.jgrid.jqID(rowid);

                                // 수정할 수 없는 항목 disable 처리
                                jQuery("tr#" + lastSel).find("input").eq(2).attr("disabled", true);
                                jQuery("tr#" + lastSel).find("select").attr("disabled", true);
                            },
                            afterRestore: function (rowid) {
                                // 취소 버튼 클릭 시 Event
                            },
                            onSuccess: function (res) {
                                // 저장 후 리턴 결과
                                gridResAction(jQuery.parseJSON(res.responseText), 'code');
                            },
                            restoreAfterError: true // 저장 후 입력 폼 restore 자동/수동 설정
                        }}
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
                        }
                    },
                    loadComplete: function () {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            // grid 크기 자동 적용
                            resizePanel(tarID)
                        ).done(function () {
                            // 위에 dataInit 부분에서 isDigitDotOnly attr추가 하면서 이벤트 처리하기 위해서 다시 호출
                            formcheck.setEvents();
                        }).always(function () {

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
        panelClear : panelClear,
        resizePanel: resizePanel,
        gridResAction: gridResAction,
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
                        lc.dataReload('code'); // 코드 목록
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
            case 'codeList' :

                id = lastSel;
                key = 'code';
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
        if(authCrud.REG_FL === 'N') {

            jQuery('#btnAdd').attr('disabled', true);
        }

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // 검색 폼
        common.setSelectOpt(jQuery('#srcJong'), '-전체-', codeJongList); // 코드종류

        // 추가 버튼 클릭 이벤트
        jQuery('#btnAdd').on('click', function() {

            jQuery.when(

                jQuery('#codeList').jqGrid('setGridParam', { page: 1 })
            ).always(function() {

                common.addRow('codeList', {}, function() {

                    var lc = require('local');

                    lc.setEvents();
                });
            });
        });

        // jqGrid의 입력/수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#codeList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave('codeList');
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('code'); // 코드 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel('code');

    }).trigger('resize');
});