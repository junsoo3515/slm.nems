/**
 * User: 현재호
 * Date: 2016.04.25
 * Time: 오전 11:31
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'bootstrap-datepicker.lang', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'spareList':
                // 예비품 현황관리 그리드
                return formcheck.checkForm(tarID);
                break;
            default:

                return formcheck.checkForm(tarID);

                break;
        }
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

            case 'equipList' :
                // 장비 리스트
                filterData = jQuery('#srcPanel :input').serializeObject();
                break;
            case 'dataList' :
                // 기본항목
                var obj = jQuery("#equipList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                jqOpt = {
                    url: './getDataList'
                };

                if (rowid !== null) {

                    filterData = {
                        eqp_cd: obj.getRowData(rowid).eqp_cd,
                        eqp_grp_cd: obj.getRowData(rowid).eqp_grp_cd
                    };
                } else {

                    filterData = {
                        eqp_grp_cd: jQuery('#wGrp').val()
                    };
                }

                dataGrid.jqGrid('clearGridData');
                break;
            case 'spareList' :
                // 예비품 현황관리
                var obj = jQuery("#equipList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {

                    jqOpt = {
                        url: './getSpareList'
                    };

                    filterData = {
                        eqp_cd: obj.getRowData(rowid).eqp_cd
                    };
                }

                dataGrid.jqGrid('clearGridData');
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

    // 기본정보 폼 Setting
    function infoSetting(rowID, obj) {

        var regBtn = jQuery('#btnSave');

        if (rowID) {
            // 리스트 폼에 있는 객체 입력 폼 Setting
            jQuery('#wGrp').attr('disabled', true); // 장비유형(수정 모드에서 수정 못하게 비활성화)

            if(authCrud.MOD_FL === 'Y') {

                jQuery('#btnSave, #btnCancel, #btnFiles, #files').attr('disabled', false);
                regBtn.html(regBtn.html().replace("등록", "수정"));
            }

            jQuery('#btnSpareAdd').attr('disabled', authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ? false : true); // 예비품 현황관리 추가 버튼 권한 처리
            jQuery('#btnDel').attr('disabled', authCrud.DEL_FL === 'N' ? true : false); // 권한에 따라서 삭제 버튼 권한 처리

            jQuery.when(
                jQuery.ajax({
                    url: './getEquipData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        eqp_cd: jQuery("#equipList").jqGrid('getRowData', rowID).eqp_cd
                    })
                })
            ).then(function(data) {
                // 리턴 결과 값을 가지고 입력 값 Setting
                common.setValues({
                    wStrArea: data.str_area,
                    wRemark: data.remark
                });
            }).then(function() {
                // 그리드의 값을 가지고 입력 값 Setting
                common.setValues({
                    wGrp: obj.eqp_grp_cd,
                    wNm: obj.eqp_nm,
                    wSerial: obj.eqp_serial
                });
            }).done(function() {

                dataReload('data'); // 기본항목 jqGrid Data ReLoad
                dataReload('spare'); // 예비품 현황관리 jqGrid Data ReLoad
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
            panelClear(false, 'infoPanel', false);
            panelClear(false, 'dataPanel', false);
            panelClear(false, 'sparePanel', false);

            var rowid = jQuery("#equipList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#equipList").jqGrid("resetSelection"); // Grid Select reset
            }

            return false;
        }

        switch(objID) {
            case 'infoPanel':
                // 기본정보 입력폼
                common.clearElement('#' + objID); // form element

                jQuery('#btnSave, #btnCancel, #btnFiles, #files').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소, 파일, 파일버튼 Style 변경
                jQuery('#btnDel').attr('disabled', true); // 삭제 버튼 비활성화(입력 모드이기 때문에)

                jQuery('#wGrp').attr('disabled', false); // 장비 유형(수정 모드에서 수정 못하게 비활성화 처리하기 때문에)

                var regBtn = jQuery('#btnSave');

                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경
                break;
            case 'dataPanel':
                // 기본정보 > 기본항목
                jQuery('#dataList').jqGrid('clearGridData');
                break;
            case 'sparePanel':
                // 기본정보 > 예비품 현황관리
                spareLastSel = undefined;

                jQuery('#btnSpareAdd').attr('disabled', true); // 추가 버튼 비활성화(입력 모드이기 때문에)
                jQuery('#spareList').jqGrid('clearGridData');
                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#equipList").jqGrid('getGridParam', 'selrow');

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

        reqData.eqp_cd = (rowid === null ? '' : jQuery("#equipList").getRowData(rowid).eqp_cd ); // 장비코드

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)
        reqData = common.changeKeys(reqData, [
            { k: 'eqp_cd', v: 'eqp_cd' },
            { k: 'wGrp', v: 'eqp_grp_cd' },
            { k: 'wNm', v: 'eqp_nm' },
            { k: 'wSerial', v: 'eqp_serial' },
            { k: 'wStrArea', v: 'str_area' },
            { k: 'wRemark', v: 'remark' }
        ]);

        // 기본항목
        reqData.topicData = jQuery("#dataList").jqGrid('getRowData').map(function (item, index, array) {

            return {
                topic_cd: item.topic_cd,
                input_val: jQuery('#' + item.topic_cd + '_input_val').val()
            }
        });

        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setEquipAct',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data !== null) {

                common.setOSXModal('장비 정보를 성공적으로 저장하였습니다.');

                if (rowid !== null) {
                    // 수정모드일 때 성능향상을 고려
                    // 그리드에 바로 데이터 갱신 처리
                    jQuery("#equipList").jqGrid('setRowData', rowid, {
                        eqp_nm: data.eqp_nm,
                        eqp_serial: data.eqp_serial
                    });

                    // 입력 창 데이터 갱신
                    common.setValues({
                        wNm: data.eqp_nm,
                        wSerial: data.eqp_serial,
                        wStrArea: data.str_area,
                        wRemark: data.remark
                    });

                    // 기본항목 그리드 Reload
                    dataReload('data');
                } else {
                    // 입력모드일 때는 입력 폼 초기화 및 장비리스트 그리드 reload 처리
                    panelClear(true);
                    gridSetting('equip'); // 장비리스트
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
                    { list: "equipList", panel: "equipPanel" },
                    { list: "dataList", panel: "dataPanel" },
                    { list: "spareList", panel: "sparePanel" }
                ];
                break;
            case 1 :

                var arrObj = [
                    { list: "equipList", panel: "equipPanel" }
                ];
                break;
            case 2 :

                var arrObj = [
                    { list: "dataList", panel: "dataPanel" }
                ];
                break;
            case 3 :

                var arrObj = [
                    { list: "spareList", panel: "sparePanel" }
                ];
                break;
        }

        jQuery.each(arrObj, function (sIdx, data) {

            jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width() - 2);
        });
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
            case 'equipList' :

                var arrColNames = ['', '', '명칭', '제품번호'];
                var arrColModel = [
                    {name: 'eqp_cd', index: 'eqp_cd', hidden: true},
                    {name: 'eqp_grp_cd', index: 'eqp_grp_cd', hidden: true},
                    {name: 'eqp_nm', index: 'eqp_nm', width: 1, sortable: false},
                    {name: 'eqp_serial', index: 'eqp_serial', width: 1, sortable: false}
                ];

                jQuery.when(
                    jQuery.ajax({
                        url: jQuery.fn.preUrl + '/getGrpTopic',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            grpCD: jQuery('#srcGrp').val()
                        })
                    })
                ).then(function(data) {
                    // 장비 목록 초기화
                    if ( dataGrid.jqGrid('getGridParam','colNames') !== undefined ) {

                        jQuery.jgrid.gridUnload(listID);

                        dataGrid = jQuery('#' + listID); // dataGrid 초기화
                    }

                    return data;
                }).then(function(data) {
                    // 헤더 설정
                    data.forEach(function (item, index, array) {

                        if (index <= 4) {
                            arrColNames.push(item.val);
                            arrColModel.push({name: 'col' + item.id, index: 'col' + item.id, width: 1, sortable: false});
                        } else {

                            return false;
                        }
                    });
                }).done(function() {

                    jqOpt = {
                        url : './getEquipList',
                        height: 300,
                        scroll: 1,
                        rowList: [10, 30, 50, 100],
                        colNames: arrColNames,
                        colModel: arrColModel,
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
                            // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                            jQuery.when(
                                resizePanel(1)
                            ).always(function () {

                                jQuery.fn.loadingComplete();
                                return false;
                            });
                        }
                    };
                }).always(function () {

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
                });

                break;
            case 'dataList' :
                // 기본항목
                jqOpt = {
                    url : '',
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [],         // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    colNames: ['', '항목', '값'],
                    colModel: [
                        { name: 'topic_cd', index: 'topic_cd', hidden: true, key: true},
                        { name: 'topic_nm', index: 'topic_nm', width: 1, editable: false, sortable: false},
                        { name: 'input_val', index: 'input_val', width: 1, editable: true, sortable: false}
                    ],
                    beforeProcessing: function(data, status, xhr) {
                        // POST 후 리턴 이벤트
                        // 수정모드 일 경우 장비관리 그리드에 수정 값 적용하기 위해
                        var dataCnt = data.rows.length;
                        var rowid = jQuery("#equipList").jqGrid('getGridParam', 'selrow');

                        if (dataCnt > 0 && rowid !== null) {

                            var listData = {};

                            data.rows.map(function (item, index, array) {

                                if (index < 5) {

                                    listData['col' + item.topic_cd] = item.input_val
                                } else {

                                    return false;
                                }
                            });

                            jQuery("#equipList").jqGrid('setRowData', rowid, listData);
                        }
                    },
                    loadComplete: function () {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel(2) // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).then(function() {
                            // 배치 수정모드 변경
                            dataGrid.jqGrid('getDataIDs').reverse().forEach(function (item, index, array) {

                                dataGrid.jqGrid('editRow',item);
                            });
                        }).always(function() {
                            // 로딩완료
                            jQuery.fn.loadingComplete();
                            return false;
                        });
                    }
                };

                break;
            case 'spareList' :
                // 예비품 현황관리
                jqOpt = {
                    url : '',
                    editurl: './setSpareAct',
                    scroll: 1,
                    scrollrows: true,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '', '일자', '수량', '관리'],
                    colModel: [
                        {name: 'spare_seq', index: 'spare_seq', editable: true, editrules: { number: true }, hidden: true},
                        {name: 'eqp_cd', index: 'eqp_cd', editable: true, hidden: true},
                        {
                            name: 'reg_dt', index: 'reg_dt', width: 2,
                            editable: true, editrules: { required: true },
                            editoptions: {
                                dataInit: function (el) {

                                    var nowTemp = new Date();

                                    jQuery(el).attr({msg : '일자를', readonly: true});
                                    jQuery(el).val(function (idx, data) {

                                        return data;
                                    }).datepicker({
                                        language: 'kr',
                                        autoclose: true,
                                        todayHighlight: true,
                                        todayBtn: "linked",
                                        format: 'yyyy-mm-dd',
                                        endDate: new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0)
                                    }).trigger('change');
                                }
                            }
                        },
                        {
                            name: 'qnt', index: 'qnt', width: 1, align: 'right',
                            formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 0, defaultValue: '' },
                            editable: true, editrules: { required: true, number: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({isDigitOnly : 1, msg : '수량을'}).addClass('text-right');
                                }
                            }
                        },
                        {name: 'myac', width: 1, sortable: false, classes: 'text-center', formatter: 'actions', formatoptions: {
                            keys: true,
                            editbutton: false,
                            delbutton: false,
                            afterRestore: function (rowid) {
                                // 취소 버튼 클릭 시 Event
                            },
                            onSuccess: function (res) {
                                // 저장 후 리턴 결과
                                gridResAction(jQuery.parseJSON(res.responseText), 'spare');
                            },
                            restoreAfterError: true // 저장 후 입력 폼 restore 자동/수동 설정
                        }}
                    ],
                    onSelectRow: function (id, status, event) {
                        // 행 선택 시
                        if (id && id !== spareLastSel) {

                            if (spareLastSel !== undefined) {

                                jQuery(this).jqGrid('restoreRow', spareLastSel);
                                jqFn.jqGridListIcon(this.id, spareLastSel);

                                // jqGrid 버그 수정 addRow 후에 다른 row 선택을 여러번 해보면 highlight 버그 해결
                                var tmpObj = jQuery(this).find('#' + spareLastSel);

                                if (tmpObj.hasClass('success')) {

                                    tmpObj.removeClass('success').removeAttr('aria-selected');
                                }
                            }

                            spareLastSel = id;
                        }
                    },
                    loadComplete: function () {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel(3) // 브라우저 창 크기 변경 시 grid 크기 자동 적용
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

        if (tarID !== 'equip') {

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
        }

        return false;
    }

    return {
        inputCheckScript: inputCheckScript,
        setEvents: formcheck.setEvents,
        dataReload: dataReload,
        infoSetting: infoSetting,
        panelClear: panelClear,
        dataSend: dataSend,
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
                        lc.gridSetting('equip'); // 장비리스트
                    }
                });

                if(authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#infoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('infoPanel');
                        },
                        ret: "btnSave",
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
            case 'spareList' :

                id = spareLastSel;
                key = 'spare';
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

            jQuery('#btnSrch, #btnReport').attr('disabled', true);
        }
        if(authCrud.REG_FL === 'N' || authCrud.MOD_FL === 'N') {

            jQuery('#btnSave, #btnCancel, #btnFiles, #files').attr('disabled', true);
        }

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // 검색 폼
        common.setSelectOpt(jQuery('#srcGrp'), null, grpList); // 장비유형

        // 기본정보 폼
        common.setSelectOpt(jQuery('#wGrp'), '-선택-', grpList); // 장비유형

        // 삭제 버튼 클릭시
        jQuery('#btnDel').on('click', function () {

            var rowid = jQuery("#equipList").jqGrid('getGridParam', 'selrow');
            var res = jQuery("#equipList").getRowData(rowid);

            if (rowid !== null) {

                if (confirm('선택 된 장비를 삭제 하시겠습니까?') === true) {

                    jQuery.ajax({
                        url: './setEquipDel',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            eqp_cd: res.eqp_cd
                        }),
                        success: function (data) {

                            if (data.isSuccess === true) {

                                common.setOSXModal('성공적으로 삭제 하였습니다.');

                                jQuery('#btnSrch').trigger('click');
                            } else {

                                common.setOSXModal('삭제 실패 하였습니다.');
                            }
                        },
                        error: common.ajaxError
                    });
                }
            }
        });

        // 취소 버튼 클릭시
        jQuery('#btnCancel').on('click', function () {

            lc.panelClear(true);
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

            var form = "<form action='./exportEquipExcel' method='POST'>";

            form += "<input type='hidden' name='srcGrp' value='" + jQuery("#srcGrp").val() + "'>";

            form += "</form>";

            jQuery(form)
                .appendTo("body")
                .submit()
                .remove();
        });

        // 예비품 현황관리 추가 버튼 클릭 시
        jQuery('#btnSpareAdd').on('click', function() {

            jQuery.when(

                jQuery('#spareList').jqGrid('setGridParam', { page: 1 })
            ).always(function() {

                var rowid = jQuery("#equipList").jqGrid('getGridParam', 'selrow');

                common.addRow('spareList', { spare_seq: 0, eqp_cd : jQuery("#equipList").getRowData(rowid).eqp_cd, qnt: 0 }, function() {

                    var lc = require('local');

                    lc.setEvents();
                });
            });
        });

        // 입력 폼 - 장비유형 DropDown 변경 시 이벤트
        jQuery('#wGrp').on('change', function() {
            // TODO 장비유형별 기본항목 ITEM jqGrid 갱신
            lc.dataReload('data');
        });

        // 엑셀 양식 다운로드 버튼 클릭 시
        jQuery('#btnDetailReportForm').on('click', function() {

            location.href = $.fn.sysUrl + '/res/excel/form_equip.xlsx';
            return false;
        });

        // 엑셀 일괄등록 버튼 이벤트
        jQuery('#files').on('change', function() {

            jQuery.fn.loadingStart(); // 로딩 시작

            // 첨부파일 저장 Process(비동기 처리)
            var formData = new FormData(); // HTML5 지원 되는 브라우저부터 지원
            formData.append("files", jQuery('input[name=files]')[0].files[0]);

            jQuery.when(
                // 엑셀 일괄 등록
                jQuery.ajax({
                    url: './importEquipExcel',
                    type: "POST",
                    processData: false,
                    contentType: false,
                    data: formData
                })
            )
            .done(function(data) {
                // 성공 후
                if (data > -1) {

                    common.setOSXModal('엑셀 일괄등록을 성공적으로 저장하였습니다.');

                    lc.panelClear(true);
                    lc.gridSetting('equip'); // 장비리스트
                } else {

                    common.setOSXModal('엑셀 일괄등록이 실패하셨습니다.');
                }
            })
            .fail(function (jqXhr, textStatus, errorThrown) {
                // 통신 에러 발생시 처리
                console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");
                common.setOSXModal('엑셀 일괄등록이 실패하셨습니다.');
            })
            .always(function() {

                common.clearElement('#fileupload'); // 파일 form Clear
                jQuery.fn.loadingComplete(); // 로딩 종료
                return false;
            });
        });

        // jqGrid의 입력/수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#dataList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                lc.dataSend();
                return false;
            }
        });
        jQuery("#spareList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave('spareList');
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('equip'); // 장비 리스트
        lc.gridSetting('data'); // 기본항목
        lc.gridSetting('spare'); // 예비품 현황관리
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel(0);

    }).trigger('resize');
});