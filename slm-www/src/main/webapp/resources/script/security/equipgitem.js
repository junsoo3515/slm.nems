/**
 * User: 이준수
 * Date: 2017.06.05
 * Time: 오전 11:31
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jquery-ui', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

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

            case 'eqpGrpList' :
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

    // 기본항목관리 장비유형 select2(ajax)
    function equipTypeSelect2(pVal){
        if(pVal === undefined){
            pVal = '';
        }

        return{
            url: './getEquipTypeSelect2',
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
            panelClear(false, 'eqpGrpPanel', false);

            var rowid = jQuery("#eqpGrpList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#eqpGrpList").jqGrid("resetSelection"); // Grid Select Reset 처리
            }

            return false;
        }

        switch(objID) {

            case 'eqpGrpPanel':
                // 코드관리 그리드
                lastSel = undefined;

                jQuery('#btnAdd').attr('disabled', (authCrud.REG_FL === 'Y') ? false : true); // 추가버튼 Style 변경
                jQuery('#eqpGrpList').jqGrid('clearGridData'); // 그리드 데이터 초기화
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
            case 'eqpGrpList' :
                // 기본항목관리 목록
                jqOpt = {
                    url : './getEqpGrpList',
                    editurl: './setEqpGrpAct',
                    scroll: 1,
                    scrollrows: true,
                    sortname: 'pos',
                    rowList: [10, 30, 50, 100],
                    colNames: ['','','','','', '항목코드', '장비유형', '한글', '영어', '사용', '정렬순서', '관리'],
                    colModel: [
                        {name: 'prev_topic_nm', index: 'prev_topic_nm', editable: true, edittype: 'text', hidden:true},
                        {name: 'prev_eng_nm', index: 'prev_eng_nm', editable: true, edittype: 'text', hidden:true},
                        {name: 'prev_use_fl', index: 'prev_use_fl', editable: true, edittype: 'text', hidden:true},
                        {name: 'prev_pos', index: 'prev_pos', editable: true, edittype: 'text', hidden:true},
                        {name: 'eqp_grp_cd', index: 'eqp_grp_cd', editable: true, edittype: 'text', hidden:true},
                        {name: 'topic_cd', index: 'topic_cd', editable: true, width: 4, edittype: 'text', sortable: false, editrules:{required: true},
                            editoptions:{
                                dataInit: function (el) {
                                    jQuery(el).attr({msg : '항목코드를'});
                                }
                            }},
                        {name: 'eqp_nm', index: 'eqp_nm', width: 4, editable: true, edittype: 'select', sortable: false, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {
                                    var obj = jQuery(el);
                                    var rowid = obj.attr('rowid');
                                    var kObj = jQuery('#' + rowid + '_eqp_nm');
                                    obj.select2({
                                        minimumInputLength:0, // 최소 검색어 개수
                                        ajax: equipTypeSelect2()
                                    });

                                    obj
                                        .append(new Option(obj.parent('td').attr('title'), kObj.val(), true, true))
                                        .val(kObj.val())
                                        .trigger('change');
                                },
                                value: {}
                            }
                        },
                        {name: 'topic_nm', index: 'topic_nm', editable: true, width: 1, edittype: 'text', sortable: false, editrules:{required: true},
                            editoptions: {
                                dataInit: function (el) {
                                    jQuery(el).attr({msg : '항목이름(한글)을'});
                                }
                            }},
                        {name: 'eng_nm', index: 'eng_nm', editable: true, width: 1, edittype: 'text', sortable: false},
                        {name: 'use_fl', index: 'use_fl', width: 1, align: 'center', editable: true, edittype: 'checkbox', sortable: false, editoptions: { value: "Y:N" }},
                        {name: 'pos', index: 'pos', editable: true, width: 1, edittype: 'text',sortable:false, editrules:{required: true}, editoptions:{
                            dataInit: function (el) {
                                jQuery(el).attr({msg: '순번을', isDigitOnly: '1'});
                            }
                        }},
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
                                jQuery("tr#" + lastSel).find("input").eq(1).attr("readonly", true);
                                jQuery("tr#" + lastSel).find("select").attr("disabled", true);
                            },
                            afterRestore: function (rowid) {
                                // 취소 버튼 클릭 시 Event
                            },
                            onSuccess: function (res) {
                                // 저장 후 리턴 결과
                                gridResAction(jQuery.parseJSON(res.responseText), 'eqpGrp');
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
                        var obj = jQuery(this);
                        jQuery.when(
                            // grid 크기 자동 적용
                            resizePanel(tarID)
                        ).done(function () {
                            // 위에 dataInit 부분에서 isDigitDotOnly attr추가 하면서 이벤트 처리하기 위해서 다시 호출
                            formcheck.setEvents();
                                // group Header 추가
                                // loadComplete에서 안할 시 크기가 꺠짐
                                obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                                    useColSpanStyle: true,
                                    groupHeaders: [{
                                        startColumnName: 'topic_nm',
                                        numberOfColumns: 2,
                                        titleText: '항목'
                                    }]
                                });
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
        ).jqGrid('sortableRows', {
                cursor:'.handle',
                update: function (e, html){
                    var pos = jQuery('#eqpGrpList').jqGrid('getRowData',html.item[0].id).pos;
                    var eqp_grp_cd = jQuery('#eqpGrpList').jqGrid('getRowData',html.item[0].id).eqp_grp_cd;
                    var rowIndex = html.item[0].rowIndex;
                    var topic_cd = jQuery('#eqpGrpList').jqGrid('getRowData', html.item[0].id).topic_cd;
                    var filterData = {
                        pos : pos,
                        eqp_grp_cd : eqp_grp_cd,
                        rowIndex : rowIndex,
                        topic_cd: topic_cd
                    };
                    jQuery.ajax({
                       type: 'POST',
                       url: './setEqpGItemPosUpdate',
                       contentType : "application/json; charset=UTF-8",
                       data: JSON.stringify(filterData),
                       dataType: 'json',
                       success: function(){
                           dataReload('eqpGrp');
                        },
                       error: function (){
                           alert('통신에러');
                       }

                    });
                }

        });
        return false;
    }


    return {
        inputCheckScript: inputCheckScript,
        setEvents: formcheck.setEvents,
        dataReload: dataReload,
        equipTypeSelect2: equipTypeSelect2,
        panelClear : panelClear,
        resizePanel: resizePanel,
        gridResAction: gridResAction,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local', 'jquery', 'select2.lang'], function (common, darkhand, lc, jQuery) {
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
                        lc.dataReload('eqpGrp'); // 기본항목관리 목록
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
            case 'eqpGrpList' :

                id = lastSel;
                key = 'eqpGrp';
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

        // 검색 폼
        common.setSelectOpt(jQuery('#srcEqpType'), null, equipTypeSrchList); // 장비유형

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용



        // 추가 버튼 클릭 이벤트
        jQuery('#btnAdd').on('click', function() {

            jQuery.when(
                jQuery('#eqpGrpList').jqGrid('setGridParam',{ page: 1 })
            ).always(function(){
                    common.addRow('eqpGrpList', {eqp_nm: '장비유형을 선택하세요.'}, function() {

                        var lc = require('local');
                        var rowid = jQuery('#eqpGrpList').jqGrid('getGridParam','selrow');
                        jQuery('#' + rowid + '_eqp_nm').select2({
                            minimumInputLength: 0,
                            ajax: lc.equipTypeSelect2()
                        });

                        lc.setEvents();
                    });
                });
        });

        // jqGrid의 입력/수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#eqpGrpList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave('eqpGrpList');
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('eqpGrp'); // 기본항목 관리 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel('eqpGrp');

    }).trigger('resize');
});