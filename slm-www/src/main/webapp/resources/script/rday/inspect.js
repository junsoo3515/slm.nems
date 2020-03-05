/**
 * User: 현재호
 * Date: 2016.05.02
 * Time: 오후 4:35
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

            case 'reportList' :
                // 점검현황 리스트
                jqOpt = {
                    url: './getReportList'
                };

                filterData = jQuery('#srcPanel :input').serializeObject();

                filterData['gg'] = 'DAY';
                break;
            case 'summaryList' :
                // 점검요약정보
                var obj = jQuery("#reportList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                jqOpt = {
                    url: './getSummaryList'
                };

                if (rowid !== null) {

                    filterData = {
                        da_seq: obj.getRowData(rowid).da_seq
                    };
                }

                dataGrid.jqGrid('clearGridData');
                break;
            case 'detailList' :
                // 장비별 세부정보
                var masObj = jQuery("#reportList");
                var masRowId = masObj.jqGrid('getGridParam', 'selrow');

                var subObj = jQuery("#summaryList");
                var subRowId = subObj.jqGrid('getGridParam', 'selrow');

                if (masRowId !== null && rowid !== null) {

                    jqOpt = {
                        url: './getDetailList'
                    };

                    filterData = {
                        da_seq: masObj.getRowData(masRowId).da_seq,
                        rpt_cd: subObj.getRowData(subRowId).rpt_cd,
                        sw_type_fl: subObj.getRowData(subRowId).sw_type_fl,
                        cnt: subObj.getRowData(subRowId).cnt_all
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

    // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if(isAll) {
            // 모든 패널 초기화
            panelClear(false, 'reportPanel', false);
            panelClear(false, 'summaryPanel', false);
            panelClear(false, 'detailPanel', false);

            var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#reportList").jqGrid("resetSelection"); // Grid Select reset
            }

            return false;
        }

        switch(objID) {
            case 'reportPanel':
                // 점검현황
                lastSel = undefined;

                if (authCrud.REG_FL === 'Y') {

                    jQuery('#btnAdd').attr('disabled', false);
                }
                break;
            case 'summaryPanel':
                // 점검요약정보
                summLastSel = undefined;

                jQuery('#btnApply').attr('disabled', true); // 적용 버튼 비활성화(입력 모드이기 때문에)
                jQuery('#summaryList').jqGrid('clearGridData');
                break;
            case 'detailPanel':
                // 장비별 세부정보
                jQuery('#btnFiles, #files, #btnDetailReport').attr('disabled', true); // 버튼 비활성화
                jQuery('#detailList').jqGrid('clearGridData');
                break;
        }
    }

    // 점검요약정보 동기화 이벤트
    function setSyncData(key) {

        if (key === undefined) key = '';

        jQuery.fn.loadingStart(); // 로딩 시작

        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setSummarySync',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({
                    da_seq: key
                })
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (key === '') {
                // 전체 동기화 때에만 메세지 문구 표시
                common.setOSXModal((data !== null ? '성공적으로 동기화 되었습니다.' : '동기화가 실패하였습니다.'));
            }
        })
        .fail(common.ajaxError)
        .always(function() {

            jQuery.fn.loadingComplete();
            return false;
        });
    }

    // 점검요약정보/장비별 세부정보 등록/수정 이벤트
    function dataSend(listID) {

        var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

        if (authCrud.REG_FL === 'N' && authCrud.MOD_FL === 'N') {
            // 등록/수정 권한 없을 경우 액션 처리 방지
            return false;
        }

        // 로딩 시작
        jQuery.fn.loadingStart();

        if (listID === undefined) listID = 'summaryList';

        switch(listID) {
            case 'detailList':
                // 장비별 세부정보
                break;
            default:
                // 점검요약정보
                var formData = jQuery('#summaryPanel :input');
                var reqData = {
                    da_seq: (rowid === null ? '' : jQuery("#reportList").getRowData(rowid).da_seq )
                };

                reqData.summData = jQuery("#" + listID).jqGrid('getRowData').map(function (item, index, array) {

                    return {
                        summ_seq: item.summ_seq,
                        inspect_nm: jQuery('#summ' + item.summ_seq + '_inspect_nm').val(),
                        confirm_nm: jQuery('#summ' + item.summ_seq + '_confirm_nm').val(),
                        res_fl: item.res_fl
                    }
                });

                // 데이터 전송
                jQuery.when(

                    jQuery.ajax({
                        url: './setSummaryAct',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify(reqData)
                    })
                )
                .then(function(data) {
                    // 결과에 따라 다음 이벤트 처리
                    common.setOSXModal((data.isSuccess ? '점검요약정보를 성공적으로 저장하였습니다.' : '점검요약정보 저장이 실패하였습니다.'));
                })
                .fail(common.ajaxError)
                .always(function() {

                    jQuery.fn.loadingComplete();
                    return false;
                });

                break;
        }

        return false;
    }

    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel(tarID) {

        if(tarID === undefined) tarID = 'report';

        jQuery('#' + tarID + 'List').jqGrid('setGridWidth', jQuery('#' + tarID + 'Panel').width() - 2);
    }

    // jqGrid 결과 후 액션
    function gridResAction(res, tarID) {

        var msg = '';

        switch(tarID) {
            case 'report':

                switch(res.isSuccess) {
                    case 2:
                        msg = '이미 해당 보고일에 점검현황이 존재합니다.'
                        break;
                    case 1:
                        msg = '성공적으로 적용되었습니다';
                        break;
                    default:
                        msg = '적용에 실패하였습니다.';
                        break;
                }

                break;
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
            case 'reportList' :
                // 일일점검현황
                jqOpt = {
                    url: '',
                    editurl: './setReportAct',
                    height: 224,
                    scroll: 1,
                    scrollrows: true,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '보고일', '데이터<br />복사', '일마감', '등록일자', '관리'],
                    colModel: [
                        {name: 'da_seq', index: 'da_seq', editable: true, editrules: {number: true}, hidden: true},
                        {
                            name: 'brief_dt', index: 'brief_dt', width: 2, align: "center",
                            editable: true, editrules: {required: true},
                            editoptions: {
                                dataInit: function (el) {

                                    var nowTemp = new Date();

                                    jQuery(el).attr({msg: '일자를', readonly: true});
                                    jQuery(el).val(function (idx, data) {

                                        return data;
                                    }).datepicker({
                                        language: 'kr',
                                        orientation: 'top auto',
                                        autoclose: true,
                                        format: 'yyyy-mm-dd',
                                        endDate: new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate() -1, 0, 0, 0, 0)
                                    }).trigger('changeDate');
                                }
                            }
                        },
                        {
                            name: 'data_copy_fl', index: 'data_copy_fl', width: 1, editable: true, sortable: false, align: 'center',
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        {
                            name: 'fin_fl', index: 'fin_fl', width: 1, editable: true, sortable: false, align: 'center',
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        {name: 'reg_dts_ux', index: 'reg_dts_ux', width: 3, align: "center", editable: false, formatter: 'date', formatoptions: {srcformat: 'U', newformat: 'Y-m-d H:i:s'}},
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                keys: true,
                                editbutton: (authCrud.MOD_FL === "N" ? false : true),
                                delbutton: (authCrud.DEL_FL === 'N' ? false : true),
                                onEdit: function (rowid) {
                                    // 수정 버튼 클릭 시 Event
                                    lastSel = jQuery.jgrid.jqID(rowid);

                                    // 수정할 수 없는 항목 disable 처리
                                    jQuery("tr#" + lastSel).find('input').filter(':eq(1), :eq(2)').attr("disabled", true);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'report');
                                },
                                restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                                delOptions: {
                                    url: './setReportDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {

                                        var reqData = dataGrid.jqGrid('getRowData', lastSel);

                                        return JSON.stringify({
                                            da_seq: reqData.da_seq
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {

                                        gridResAction(jQuery.parseJSON(res.responseText), 'report');
                                    }
                                }
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

                            // 점검요약정보 그리드 초기화 및 갱신
                            jQuery.when(
                            ).then(function() {
                                // 점검요약정보, 장비별 세부정보 그리드 초기화
                                panelClear(false, 'summaryPanel', false);
                                panelClear(false, 'detailPanel', false);
                            }).then(function() {
                                // 권한에 따라 활성화 처리
                                jQuery('#btnApply').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ? false : true));
                            }).done(function() {
                                // 점검요약정보 동기화 처리
                                setSyncData(dataGrid.jqGrid('getRowData', lastSel).da_seq);
                            }).always(function () {
                                // 점검요약정보 데이터 reload
                                dataReload('summary');
                            });
                        }
                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('report') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
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
            case 'summaryList' :
                // 점검요약정보
                jqOpt = {
                    url : '',
                    idPrefix: 'summ',    // jqGrid가 여러개일 경우 rowID를 고유하게 만들기 위해 사용
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [],         // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    colNames: ['', '', '', '분류', '구분', '수량', '점검자', '점검<br />확인자', '완료<br />여부', '전체', '정상', '이상'],
                    colModel: [
                        { name: 'summ_seq', index: 'summ_seq', editable: false, sortable: false, hidden: true, key: true},
                        { name: 'rpt_cd', index: 'rpt_cd', editable: false, sortable: false, hidden: true},
                        { name: 'sw_type_fl', index: 'sw_type_fl', editable: false, sortable: false, hidden: true},
                        { name: 'grp_nm', index: 'grp_nm', width: 2, editable: false, sortable: false, classes: 'wrappedJqGridCell'},
                        { name: 'rpt_nm', index: 'rpt_nm', width: 3, editable: false, sortable: false, classes: 'wrappedJqGridCell'},
                        { name: 'cnt_all', index: 'cnt_all', width: 1, editable: false, sortable: false, align: 'right', formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 0, defaultValue: '' } },
                        { name: 'inspect_nm', index: 'inspect_nm', width: 2, editable: true, sortable: false},
                        { name: 'confirm_nm', index: 'confirm_nm', width: 2, editable: true, sortable: false},
                        {
                            name: 'res_fl', index: 'res_fl', width: 1, align: 'center', sortable: false,
                            formatter: "checkbox",
                            editable: true,
                            edittype:'checkbox', editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                                },
                                value: "Y:N"
                            }
                        },
                        { name: 'itm_all', index: 'itm_all', width: 1, editable: false, sortable: false, align: 'right', formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 0, defaultValue: '' } },
                        { name: 'itm_normal', index: 'itm_normal', width: 1, editable: false, sortable: false, align: 'right', formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 0, defaultValue: '' } },
                        { name: 'itm_abnormal', index: 'itm_abnormal', width: 1, editable: false, sortable: false, align: 'right', formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 0, defaultValue: '' } }
                    ],
                    onSelectRow: function (id, status, event) {
                        // 행 선택 시
                        if (id && id !== summLastSel) {

                            summLastSel = id;
                        }

                        // 장비별 세부정보 그리드 데이터 호출
                        jQuery
                            .when()
                            .done(function() {
                                // 권한에 따라 활성화 처리
                                jQuery('#btnFiles, #files').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ? false : true));
                                jQuery('#btnDetailReport').attr('disabled', (authCrud.READ_FL === 'Y' ? false : true))
                            })
                            .always(function () {
                                // 장비별 세부정보(헤더 값 가변적이라서 gridSetting 처리
                                gridSetting('detail');
                            });
                    },
                    loadComplete: function (data) {

                        var obj = jQuery(this);

                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('summary') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).then(function() {
                            // 이상항목 > 0 인 경우 색상 처리
                            data.rows.filter(function(item, idx, array) {

                                if (item.itm_abnormal > 0) {

                                    obj.find("tr[id='summ" + item.summ_seq + "']").children().addClass('bg-danger');
                                    return true;
                                }
                            });
                        }).then(function() {
                            // 배치 수정모드 변경
                            obj.jqGrid('getDataIDs').reverse().forEach(function (item, index, array) {

                                obj.jqGrid('editRow',item);
                            });
                        }).then(function() {
                            // 데이터가 존재 할 경우 처음 행 선택
                            if (data.rows.length > 0) {

                                dataGrid.jqGrid('setSelection', dataGrid.find('tr[id]:eq(0)').attr('id'));
                            }
                        }).done(function() {
                            // group Header 추가
                            // loadComplete에서 안할 시 크기가 꺠짐
                            obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                                useColSpanStyle: true,
                                groupHeaders: [{
                                    startColumnName: 'itm_all',
                                    numberOfColumns: 3,
                                    titleText: '항목현황'
                                }]
                            });
                        }).always(function() {
                            // 로딩완료
                            jQuery.fn.loadingComplete();
                            return false;
                        });
                    }
                };

                break;
            case 'detailList' :
                // 장비별 세부정보
                var arrColNames = ['', '', '', ''];
                var arrColModel = [
                    {name: 'EQP_CD', index: 'EQP_CD', editable: false, sortable: false, hidden: true, key: true},
                    {name: 'DA_SEQ', index: 'DA_SEQ', editable: false, sortable: false, hidden: true},
                    {name: 'RPT_CD', index: 'RPT_CD', editable: false, sortable: false, hidden: true},
                    {name: 'TOPIC_GRP_SEQ', index: 'TOPIC_GRP_SEQ', editable: false, sortable: false, hidden: true}
                ];

                var summaryData = jQuery("#summaryList").getRowData(summLastSel);

                jQuery.when(
                    jQuery.ajax({
                        url: './getReportTopicList',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            rptCD: summaryData.rpt_cd
                        })
                    })
                ).then(function(data) {
                    // 장비별 세부정보 목록 초기화
                    if ( dataGrid.jqGrid('getGridParam','colNames') !== undefined ) {

                        jQuery.jgrid.gridUnload(listID);

                        dataGrid = jQuery('#' + listID); // dataGrid 초기화
                    }

                    return data;
                }).then(function(data) {
                    // 헤더 설정
                    var tmpLockCnt = 0;

                    data.forEach(function (item, index, array) {

                        if (item.MOD_LOCK_FL === 'N') tmpLockCnt ++;

                        var colModel = {name: 'COL' + item.TOPIC_CD, index: 'COL' + item.TOPIC_CD, width: 1, sortable: false, classes: 'wrappedJqGridCell', editable: item.MOD_LOCK_FL === 'Y' ? false : true};

                        switch(item.TOPIC_TYPE) {
                            case 'int':
                                jQuery.extend(true, colModel, {
                                    align: 'right', formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: '' }
                                });

                                if (item.MOD_LOCK_FL === 'N') {
                                    // 수정할 수 있는 항목이면
                                    jQuery.extend(true, colModel, {
                                        editrules: { required: true, number: true },
                                        editoptions: {
                                            dataInit: function (el) {

                                                jQuery(el).attr({isDigitDotOnly : 1}).addClass('text-right');
                                            }
                                        }
                                    });
                                }

                                break;
                            case 'select':

                                jQuery.extend(true, colModel, {
                                    align: 'center', edittype: 'select', editoptions: {value: {Y: '정상', N: '이상'}}
                                });
                                break;
                            default:
                                break;
                        }

                        arrColNames.push(item.TOPIC_NM);
                        arrColModel.push(colModel);
                    });

                    if (tmpLockCnt === 0) {
                        // 입력 할 수 있는 항목이 없을 경우에는 엑셀 업로드 버튼 비활성화
                        jQuery('#btnFiles, #files').attr('disabled', true);
                    }
                }).done(function() {

                    jqOpt = {
                        url : '',
                        idPrefix: 'deta',    // jqGrid가 여러개일 경우 rowID를 고유하게 만들기 위해 사용
                        height: 200,
                        scroll: 1,
                        rowList: [10, 30, 50, 100],
                        colNames: arrColNames,
                        colModel: arrColModel,
                        cellEdit: true,
                        cellsubmit: 'remote',
                        cellurl: './setDetailCellUpdate',
                        afterEditCell: function() {
                            // 위에 dataInit 부분에서 isDigitDotOnly attr추가 하면서 이벤트 처리하기 위해서 다시 호출
                            formcheck.setEvents();
                        },
                        beforeSubmitCell: function(rowid, cellname, value) {
                            // cellEdit Submit 보내기 전
                            var tmpObj = dataGrid.getRowData(rowid);

                            return {
                                da_seq: Number(tmpObj.DA_SEQ),
                                rpt_cd: tmpObj.RPT_CD,
                                topic_cd: cellname.substr(3).toString(),
                                eqp_cd: tmpObj.EQP_CD,
                                topic_grp_seq: tmpObj.TOPIC_GRP_SEQ,
                                inp_val: value
                           };
                        },
                        afterSubmitCell: function(res, rowid, cellname, value, iRow, iCol) {
                            // cellEdit Submit 변경 후
                            var resData = jQuery.parseJSON(res.responseText);

                            common.setOSXModal(resData.isSuccess === true ? '성공적으로 적용되었습니다.' : '적용에 실패하였습니다.');

                            // 사용자가 afterSubmitCell을 구현하는 경우 [성공여부, 띄울메시지] 배열을 리턴
                            return [resData.isSuccess === true ? true : false, ''];
                        },
                        afterSaveCell: function(rowid, cellname, value, iRow, iCol) {
                            // afterSubmitCell 이벤트 이후(점검요약정보 항목현황 동기화를 위해 dataReload 처리)
                            dataReload(tarID);
                        },
                        onInitGrid: function() {

                            dataReload(tarID);
                        },
                        loadComplete: function (data) {
                            // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                            var obj = jQuery(this);

                            jQuery.when(
                                resizePanel('detail')
                            ).then(function() {
                                // 이상항목 > 0 인 경우 색상 처리 후 점검요약정보의 항목현황 그리드 데이터 값 수정
                                var tdObj;
                                var abnomalCnt = 0;

                                data.rows.filter(function(item, idx, array) {

                                    if (item.ABNCOL.length > 0) {

                                        return true;
                                    }
                                }).map(function(item, idx, array) {

                                    return {
                                        eqpcd:  item.EQP_CD,
                                        col: item.ABNCOL
                                    };
                                }).forEach(function (item, index, array) {

                                    tdObj = obj.find("tr[id='deta" + item.eqpcd + "']").find('td');

                                    item.col.forEach(function (sitem, index, array) {

                                        abnomalCnt ++;
                                        tdObj.filter("[aria-describedby$='" + sitem + "']").addClass('bg-danger');
                                    });
                                });

                                return abnomalCnt;
                            }).then(function(cnt) {
                                // 점검요약 정보의 이상항목 개수 와 차이가 있을 경우 동기화 처리
                                jQuery("#summaryList").jqGrid('setRowData', summLastSel, {
                                    itm_normal: Number(summaryData.itm_all) - cnt,
                                    itm_abnormal: cnt
                                });

                                // 점검요약 정보의 이상항목 개수 존재 유무에 따른 tr 하이라이트 변경
                                var obj = jQuery("#summaryList").find("tr[id='summ" + summaryData.summ_seq + "']").children();

                                obj.removeClass('bg-danger');

                                if (cnt > 0) {

                                    obj.addClass('bg-danger');
                                }

                                // 점검요약 정보의 이상항목 개수 DB 저장
                                jQuery.when(

                                    jQuery.ajax({
                                        url: './setSummaryItmUpdate',
                                        type: "POST",
                                        dataType: "json",
                                        contentType: "application/json; charset=utf-8",
                                        data: JSON.stringify({
                                            da_seq: jQuery('#reportList').getRowData(lastSel).da_seq,
                                            summ_seq: summaryData.summ_seq,
                                            rpt_cd: summaryData.rpt_cd,
                                            cnt: cnt
                                        })
                                    })
                                ).then(function(data) {
                                    // 결과에 따라 다음 이벤트 처리
                                    if (!data.isSuccess) {

                                        common.setOSXModal('이상항목 현황 데이터 동기화 DB 적용이 실패하였습니다.');
                                    }
                                })
                                .fail(common.ajaxError)
                                .always(function() {

                                    return false;
                                });
                            }).always(function () {

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
        }

        if (tarID !== 'detail') {

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
        panelClear: panelClear,
        setSyncData: setSyncData,
        dataSend: dataSend,
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
                        lc.dataReload('report'); // 점검현황 목록
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
            case 'reportList' :

                id = lastSel;
                key = 'report';
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
        if (authCrud.READ_FL === 'N') {

            jQuery('#btnSrch, #btnReport').attr('disabled', true);
        }
        if (authCrud.REG_FL === 'Y') {

            jQuery('#btnAdd').attr('disabled', false);
        }

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

        // 보고서 내보내기 버튼 클릭 시
        jQuery('#btnReport').on('click', function() {

            var selIdx = jQuery('#reportList').jqGrid('getGridParam','selrow');

            if (selIdx === null) {

                common.setOSXModal('점검현황을 선택 후 사용하기 바랍니다.');
                return false;
            }

            jQuery.fn.loadingStart(); // 로딩 시작

            jQuery.when(
                jQuery.ajax({
                    url: './getReportSecureData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        da_seq: jQuery("#reportList").jqGrid('getRowData', lastSel).da_seq
                    })
                })
            ).then(function(data) {

                var handle = window.open($.fn.preUrl + '/report/ozReportDayPreview?key=' + data.key, 'reportPreview', 'directories=0, width=1171, height=600, location=0, menubar=0, resizeable=0, status=0, toolbar=0');
                handle.focus();
            })
            .fail(common.ajaxError)
            .always(function() {

                jQuery.fn.loadingComplete();
                return false;
            });
        });

        // 전체요약정보 동기화 버튼 클릭 시
        jQuery('#btnSync').on('click', function() {

            lc.setSyncData();
            return false;
        });

        // 점검현황 추가 버튼 클릭 시(그리드가 scroll 옵션으로 되어 있을 경우 문제 발생해서 해결책으로 아래 구문 추가)
        jQuery('#btnAdd').on('click', function() {

            jQuery.when(

                jQuery('#reportList').jqGrid('setGridParam', { page: 1 })
            ).always(function() {

                isAddState = false;
                common.addRow('reportList', { da_seq: 0 }, function() {

                    var lc = require('local');

                    lc.setEvents();
                });
            });

            //var nowPg = jQuery('#reportList').getGridParam('page');
            //
            //if (nowPg > 1) {
            //
            //    isAddState = true;
            //    lc.dataReload('report');
            //
            //} else {
            //
            //    isAddState = false;
            //    common.addRow('reportList', { da_seq: 0 }, function() {
            //
            //        var lc = require('local');
            //
            //        lc.setEvents();
            //    });
            //}
        });

        // 적용 버튼 클릭 시
        jQuery('#btnApply').on('click', function () {

            lc.dataSend('summaryList');
            return false;
        });

        // 장비별 세부정보 엑셀 일괄등록 버튼 이벤트
        jQuery('#files').on('change', function() {

            jQuery.fn.loadingStart(); // 로딩 시작

            // 첨부파일 저장 Process(비동기 처리)
            var formData = new FormData(); // HTML5 지원 되는 브라우저부터 지원
            formData.append("files", jQuery('input[name=files]')[0].files[0]);

            var masObj = jQuery("#reportList");
            var masData = masObj.getRowData(masObj.jqGrid('getGridParam', 'selrow'));

            var subObj = jQuery("#summaryList");
            var subData = subObj.getRowData(subObj.jqGrid('getGridParam', 'selrow'));

            formData.append("daSeq", masData.da_seq);
            formData.append("rptCd", subData.rpt_cd);
            formData.append("swTypeFl", subData.sw_type_fl);

            jQuery.when(
                // 엑셀 일괄 등록
                jQuery.ajax({
                    url: './importDetailEquipExcel',
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

                    lc.dataReload('detail'); // 장비별 세부정보 목록
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

        // 엑셀 양식 다운로드 버튼 클릭 시
        jQuery('#btnDetailReportForm').on('click', function() {

            location.href = $.fn.sysUrl + '/res/excel/form_inspect.xlsx';
            return false;
        });

       // 엑셀 내보내기 버튼 클릭 시
        jQuery('#btnDetailReport').on('click', function() {
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

            var masObj = jQuery("#reportList");
            var masData = masObj.getRowData(masObj.jqGrid('getGridParam', 'selrow'));

            var subObj = jQuery("#summaryList");
            var subData = subObj.getRowData(subObj.jqGrid('getGridParam', 'selrow'));

            var form = "<form action='./exportDetailEquipExcel' method='POST'>";

            form += "<input type='hidden' name='briefDt' value='" + masData.brief_dt + "'>";
            form += "<input type='hidden' name='daSeq' value='" + masData.da_seq + "'>";
            form += "<input type='hidden' name='rptCd' value='" + subData.rpt_cd + "'>";
            form += "<input type='hidden' name='swTypeFl' value='" + subData.sw_type_fl + "'>";

            form += "</form>";

            jQuery(form)
                .appendTo("body")
                .submit()
                .remove();
        });

        // jqGrid의 입력/수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#reportList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave(e.delegateTarget.id);
                return false;
            }
        });

        jQuery("#summaryList, #detailList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                lc.dataSend(e.delegateTarget.id);
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('report'); // 점검현황 리스트
        lc.gridSetting('summary'); // 점검요약정보
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {
        // jqGrid 3개
        lc.resizePanel('report');
        lc.resizePanel('summary');
        lc.resizePanel('detail');

    }).trigger('resize');
});