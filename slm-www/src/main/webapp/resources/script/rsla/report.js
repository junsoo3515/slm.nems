/**
 * User: 현재호
 * Date: 2016.06.07
 * Time: 오후 4:43
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'bootstrap-datepicker.lang', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        return formcheck.checkForm(tarID);
    }

    // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if(isAll) {
            // 모든 패널 초기화
            panelClear(false, 'infoPanel', false);
            panelClear(false, 'resPanel', false);

            var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#reportList").jqGrid("resetSelection"); // Grid Select reset
            }

            jQuery('#myTabs a:first').tab('show'); // 처음 탭으로 강제 이동

            return false;
        }

        switch(objID) {
            case 'infoPanel':
                // SLA 보고서 입력폼
                common.clearElement('#' + objID); // form element

                var nowTemp = new Date();

                jQuery('#wRemoveDtGrp').datepicker('setDates', new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate())).datepicker('setDates', null);
                jQuery('#wBriefDtGrp').datepicker('update', common.nowDate('-')).trigger('changeDate');
                jQuery('#wSDate').datepicker('update', new Date(nowTemp.getFullYear(), nowTemp.getMonth(), 1)).trigger('changeDate');
                jQuery('#wEDate').datepicker('update', new Date(nowTemp.getFullYear(), nowTemp.getMonth() + 1, 0)).trigger('changeDate');

                if(jQuery('#myTabs li:eq(0)').hasClass('active') === true) {
                    // 처음 탭일 경우(하단에 shown 이벤트가 안됨)
                    var regBtn = jQuery('#btnSave');

                    jQuery('#btnSave, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소, 파일, 파일버튼 Style 변경
                    jQuery('#btnDel').attr('disabled', true); // 삭제 버튼 비활성화(입력 모드이기 때문에)

                    regBtn.html(regBtn.html().replace("수정", "등록")); // 등록 버튼 명칭 변경
                }
                break;
            case 'resPanel':
                // 평가결과 Panel
                jQuery('#resSummPanel').find('p[id]').text('-');
                jQuery('#resGridList').jqGrid('clearGridData'); // SLA 서비스 평가결과 그리드 초기화
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

            case 'reportList' :
                // SLA 보고서 목록
                jqOpt = {
                    url: './getReportList'
                };

                filterData = jQuery('#srcPanel :input').serializeObject();
                break;
            case 'resGridList' :
                // SLA 서비스 평가결과
                var obj = jQuery("#reportList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                jqOpt = {
                    url: './getResultList'
                };

                if (rowid !== null) {

                    filterData = {
                        sla_seq: obj.getRowData(rowid).sla_seq
                    };
                }

                dataGrid.jqGrid('clearGridData');
                break;
            case 'bwList' :
                // 대역폭 사용률 정보
                var obj = jQuery("#reportList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                jqOpt = {
                    url: './getBandWidthList'
                };

                if (rowid !== null) {

                    filterData = {
                        sla_seq: obj.getRowData(rowid).sla_seq
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

    // SLA 보고서 등록/수정 이벤트
    function dataSend() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

        if (rowid === null) {
            // 등록 모드
            if (authCrud.REG_FL === 'N') return false;
        } else {
            // 수정 모드
            if (authCrud.MOD_FL === 'N') return false;
        }

        var formData = jQuery('#infoPanel :input');
        var reqData = formData.serializeObject();

        reqData.sla_seq = (rowid === null ? '' : jQuery("#reportList").getRowData(rowid).sla_seq ); // SLA 보고서 고유번호

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)
        reqData = common.changeKeys(reqData, [
            { k: 'sla_seq', v: 'sla_seq' },
            { k: 'wBriefDt', v: 'brief_dt' },
            { k: 'wSDate', v: 'start_dt' },
            { k: 'wEDate', v: 'end_dt' },
            { k: 'wRemoveDt', v: 'remove_dt' },
            { k: 'wTotTime', v: 'tot_time' },
            { k: 'wPlanCnt', v: 'plan_cnt' },
            { k: 'wSucCnt', v: 'suc_cnt' },
            { k: 'wFailCnt', v: 'fail_cnt' },
            { k: 'wStopCnt', v: 'stop_cnt' },
            { k: 'wResPlanCnt', v: 'res_plan_cnt' },
            { k: 'wResSucCnt', v: 'res_suc_cnt' },
            { k: 'wRemark', v: 'remark' }
        ]);

        // 대역폭 사용률 정보
        reqData.bandWidthData = jQuery("#bwList").jqGrid('getRowData').map(function (item, index, array) {

            return {
                sla_seq: reqData.sla_seq,
                bw_itm_cd: item.bw_itm_cd,
                use_rate: jQuery('#bw' + item.bw_itm_cd + '_use_rate').val()
            }
        });

        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setSLAReportAct',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data !== null) {

                common.setOSXModal('SLA 보고서 정보를 성공적으로 저장하였습니다.');

                if (rowid !== null) {
                    // 수정모드일 때 성능향상을 고려
                    // 그리드에 바로 데이터 갱신 처리
                    jQuery("#reportList").jqGrid('setRowData', rowid, {
                        brief_dt: data.brief_dt.substr(0, 4) + '-' + data.brief_dt.substr(4, 2) + '-' + data.brief_dt.substr(6, 2),
                        sla_title: data.brief_dt.substr(0, 4) + '년' + data.brief_dt.substr(4, 2) + '월 보고서',
                        start_dt: data.start_dt.substr(0, 4) + '-' + data.start_dt.substr(4, 2) + '-' + data.start_dt.substr(6, 2),
                        end_dt: data.end_dt.substr(0, 4) + '-' + data.end_dt.substr(4, 2) + '-' + data.end_dt.substr(6, 2)
                    });

                    jQuery.extend(true, data, jQuery("#reportList").jqGrid('getRowData', rowid));

                    // 입력 창 데이터 갱신
                    var rVal = data.remove_dt;

                    if (data.remove_dt !== null) {

                        rVal = data.remove_dt.split(',').map(function (item, index, array) {

                            return new Date(Number(item.substr(0, 4)), Number(item.substr(4, 2)) - 1, Number(item.substr(6, 2)));
                        });
                    }

                    jQuery('#wRemoveDtGrp').datepicker('setDates', rVal);

                    jQuery('#wBriefDtGrp').datepicker('update', data.brief_dt).trigger('changeDate');
                    jQuery('#wSDate').datepicker('update', data.start_dt).trigger('changeDate');
                    jQuery('#wEDate').datepicker('update', data.end_dt).trigger('changeDate');

                    common.setValues({
                        wTotTime: data.tot_time,
                        wPlanCnt: data.plan_cnt,
                        wSucCnt: data.suc_cnt,
                        wFailCnt: data.fail_cnt,
                        wStopCnt: data.stop_cnt,
                        wResPlanCnt: data.res_plan_cnt,
                        wResSucCnt: data.res_suc_cnt,
                        wRemark: data.remark
                    });

                    // 대역폭 사용률 정보 그리드 Reload
                    dataReload('bw');
                } else {
                    // 입력모드일 때는 입력 폼 초기화 및 SLA 보고서 목록 그리드 reload 처리
                    panelClear(true);
                    dataReload('report');
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
    function resizePanel(tarID) {

        if(tarID === undefined) tarID = 'report';

        jQuery('#' + tarID + 'List').jqGrid('setGridWidth', jQuery('#' + tarID + 'Panel').width() - 2);
    }

    // SLA 보고서 입력 폼 Setting
    function infoSetting(rowID, obj) {

        if (rowID) {

            jQuery.when(
                jQuery.ajax({
                    url: './getReportData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        sla_seq: jQuery("#reportList").jqGrid('getRowData', rowID).sla_seq
                    })
                })
            ).then(function(data) {
                // 리턴 결과 값을 가지고 입력 값 Setting
                var rVal = data.remove_dt;

                if (data.remove_dt !== null) {

                    rVal = data.remove_dt.split(',').map(function (item, index, array) {

                        return new Date(Number(item.substr(0, 4)), Number(item.substr(4, 2)) - 1, Number(item.substr(6, 2)));
                    });
                }

                jQuery('#wRemoveDtGrp').datepicker('setDates', rVal);

                // 백업성공율 정보
                common.setValues({
                    wTotTime: data.tot_time,
                    wPlanCnt: data.plan_cnt,
                    wSucCnt: data.suc_cnt,
                    wFailCnt: data.fail_cnt,
                    wStopCnt: data.stop_cnt,
                    wResPlanCnt: data.res_plan_cnt,
                    wResSucCnt: data.res_suc_cnt,
                    wRemark: data.remark
                });
            }).then(function() {
                // 대역폭 사용률 정보 Reload
                dataReload('bw');
            }).done(function() {
                // 그리드의 값을 가지고 입력 값 Setting
                jQuery('#wBriefDtGrp').datepicker('update', obj.brief_dt).trigger('changeDate');
                jQuery('#wSDate').datepicker('update', obj.start_dt).trigger('changeDate');
                jQuery('#wEDate').datepicker('update', obj.end_dt).trigger('changeDate');
            })
            .fail(common.ajaxError)
            .always(function() {

                return false;
            });
        } else {
            // 대역폭 사용률 정보 Reload
            dataReload('bw');
        }
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
            height: 302,                // 세로 크기
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
                // SLA 보고서 목록
                jqOpt = {
                    url: '',
                    height: 690,
                    scroll: 1,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '제목', '보고일', '시작일', '종료일' , '등록일자'],
                    colModel: [
                        {name: 'sla_seq', index: 'sla_seq', hidden: true},
                        {name: 'sla_title', index: 'sla_title', width: 2, sortable: false},
                        {name: 'brief_dt', index: 'brief_dt', width: 1, align: "center"},
                        {name: 'start_dt', index: 'start_dt', width: 1, align: "center"},
                        {name: 'end_dt', index: 'end_dt', width: 1, align: "center"},
                        {name: 'reg_dts_ux', index: 'reg_dts_ux', width: 2, align: "center", formatter: 'date', formatoptions: {srcformat: 'U', newformat: 'Y-m-d H:i:s'}}
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id, status, event) {

                        //var tabIdx = jQuery("#myTabs li.active").index(jQuery("#myTabs li"));
                        // 행 선택 시
                        jQuery.when(
                        ).then(function() {
                            // 우측 탭 관련 전체 초기화(그리드 리셋 제외하고)
                            panelClear(true, '', false)
                        }).then(function() {
                            // 버튼 활성화 처리
                            var regBtn = jQuery('#btnSave');

                            if (authCrud.MOD_FL === 'Y') {

                                jQuery('#btnSave, #btnCancel').attr('disabled', false);
                                regBtn.html(regBtn.html().replace("등록", "수정"));
                            }
                        }).done(function() {
                            //// 현재 탭이 처음일 경우에는 panelClear에서 처리되는 shown 이벤트가 안먹기 때문에 별도 처리
                            //if (tabIdx === 0) {
                            //
                            //    var rowIdx = dataGrid.jqGrid('getGridParam', 'selrow');
                            //
                            //    infoSetting(rowIdx, dataGrid.jqGrid('getRowData', rowIdx));
                            //}
                        }).always(function () {
                            // 평가결과 탭으로 이동
                            jQuery('#myTabs a:last').tab('show');
                            return false;
                        });
                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(

                            resizePanel('report') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).always(function () {

                            jQuery.fn.loadingComplete();
                            return false;
                        });
                    }
                };

                break;
            case 'resGridList' :
                // SLA 서비스 평가결과
                jqOpt = {
                    url : '',
                    idPrefix: 'res',    // jqGrid가 여러개일 경우 rowID를 고유하게 만들기 위해 사용
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [],         // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    colNames: ['영역', '서비스<br />측정지표', '가중치', '최대기대수준', '최소기대수준', '측정결과', '평가점수'],
                    colModel: [
                        { name: 'grp_nm', index: 'grp_nm', width: 2, sortable: false },
                        { name: 'nm', index: 'nm', width: 2, sortable: false, classes: 'wrappedJqGridCell' },
                        {
                            name: 'weight', index: 'weight', width: 1, sortable: false, align: 'right',
                            formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 1, defaultValue: '-' },
                            summaryTpl: "{0}", // set the summary template to show the group summary
                            summaryType: "sum" // set the formula to calculate the summary type
                        },
                        { name: 'max_lev', index: 'max_lev', width: 1, sortable: false, align: 'center' },
                        { name: 'min_lev', index: 'min_lev', width: 1, sortable: false, align: 'center' },
                        { name: 'mea_res', index: 'mea_res', width: 1, sortable: false, align: 'center' },
                        {
                            name: 'mea_point', index: 'mea_point', width: 1, sortable: false, align: 'right',
                            formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 1, defaultValue: '-' },
                            summaryTpl: "{0}", // set the summary template to show the group summary
                            summaryType: "sum" // set the formula to calculate the summary type
                        }
                    ],
                    footerrow: true, // set a footer row
                    userDataOnFooter: true, // the calculated sums and/or strings from server are put at footer row.
                    grouping: true,
                    groupingView: {
                        groupField: ['grp_nm'],
                        groupColumnShow: [true],
                        groupText: ["<strong>{0}</strong>"],
                        groupOrder: ["asc"],
                        groupSummary: [true],
                        groupCollapse: false
                    },
                    loadComplete: function (data) {

                        var obj = jQuery(this);

                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(

                            resizePanel('resGrid') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).then(function() {
                            // group Header 추가
                            // loadComplete에서 안할 시 크기가 꺠짐
                            obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                                useColSpanStyle: true,
                                groupHeaders: [{
                                    startColumnName: 'max_lev',
                                    numberOfColumns: 2,
                                    titleText: '목표수준'
                                }]
                            });
                        }).then(function () {
                            // 그룹별 summary 나오는 결과 위에 Dashboard에 표시
                            dataGrid
                                .find("tr[jqfootlevel='0']")
                                .find('td:last')
                                .each(function (idx) {

                                    jQuery('#summText' + idx).text(jQuery(this).text());
                                });
                        }).done(function() {
                            // 하단 총 가중치, 평가점수 합계 표출
                            obj.jqGrid( 'footerData' , 'set' , {
                                grp_nm: '계' ,
                                weight: dataGrid.jqGrid( 'getCol' , 'weight' , false, 'sum' ),
                                mea_point: dataGrid.jqGrid( 'getCol' , 'mea_point' , false, 'sum' )
                            });

                            jQuery('#resGridPanel').find('tr').filter('.footrow').removeClass('warning').addClass('warning');

                            // 그룹별 summary 나오는 결과 위에 총점 Dashboard에 표시
                            jQuery('#summTotText').text(jQuery('#resGridPanel').find('.footrow').find('td:last').text());
                        }).always(function() {
                            // 로딩완료
                            jQuery.fn.loadingComplete();
                            return false;
                        });
                    }
                };

                break;
            case 'bwList' :
                // 대역폭 사용률 정보
                jqOpt = {
                    url : '',
                    idPrefix: 'bw',      // jqGrid가 여러개일 경우 rowID를 고유하게 만들기 위해 사용
                    height: 347,
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [],         // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    colNames: ['', '', '그룹', '항목', '값'],
                    colModel: [
                        { name: 'bw_seq', index: 'bw_seq', hidden: true },
                        { name: 'bw_itm_cd', index: 'bw_itm_cd', hidden: true, key:true },
                        { name: 'grp_nm', index: 'grp_nm', width: 1, editable: false, sortable: false },
                        { name: 'bw_itm_nm', index: 'bw_itm_nm', width: 2, editable: false, sortable: false },
                        {
                            name: 'use_rate', index: 'use_rate', width: 2, align: 'right', sortable: false,
                            formatter: 'number', formatoptions: { decimalSeparator: ".", thousandsSeparator: ",", decimalPlaces: 2, defaultValue: '' },
                            editable: true, editrules: { number: true },
                            editoptions: {
                                dataInit: function (el) {

                                    jQuery(el).attr({isDigitDotOnly : 1}).addClass('text-right');
                                }
                            }}
                    ],
                    onInitGrid: function() {

                        dataReload(tarID);
                    },
                    loadComplete: function () {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            resizePanel('bw') // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        ).then(function() {
                            // 배치 수정모드 변경
                            dataGrid.jqGrid('getDataIDs').reverse().forEach(function (item, index, array) {

                                dataGrid.jqGrid('editRow', item);
                            });
                        }).done(function() {
                            // 위에 dataInit 부분에서 isDigitDotOnly attr추가 하면서 이벤트 처리하기 위해서 다시 호출
                            formcheck.setEvents();
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
        dataSend: dataSend,
        resizePanel: resizePanel,
        infoSetting: infoSetting,
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
                        lc.dataReload('report'); // SLA 보고서 목록
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

    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {
        // 권한에 따른 버튼 비활성화
        if (authCrud.READ_FL === 'N') {

            jQuery('#btnSrch, #btnReport').attr('disabled', true);
        }
        if(authCrud.REG_FL === 'N' || authCrud.MOD_FL === 'N') {

            jQuery('#btnSave, #btnCancel').attr('disabled', true);
        }

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        // 날짜 타입 유형
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
                    nowVal = common.termDate(nowVal, 'y', -1, '-');
                    break;
                case 'wSDate':
                    nowVal = new Date(nowTemp.getFullYear(), nowTemp.getMonth(), 1);
                    break;
                case 'wEDate':
                    nowVal = new Date(nowTemp.getFullYear(), nowTemp.getMonth() + 1, 0);

                    jQuery(this).datepicker('setEndDate', null);
                    break;
            }

            jQuery(this).datepicker('update', nowVal).trigger('changeDate');
        });

        jQuery('#wBriefDtGrp').datepicker({
            language: 'kr',
            format: 'yyyy-mm-dd',
            endDate: new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0),
            autoclose: true,
            todayHighlight: true,
            todayBtn: "linked"
        }).datepicker('update', common.nowDate('-')).trigger('changeDate');

        jQuery('#wRemoveDtGrp').datepicker({
            language: 'kr',
            format: 'yyyy-mm-dd',
            endDate: new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0),
            todayHighlight: true,
            todayBtn: "linked",
            multidate: true
        });

        // 삭제 버튼 클릭시
        jQuery('#btnDel').on('click', function () {

            var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');
            var res = jQuery("#reportList").getRowData(rowid);

            if (rowid !== null) {

                if (confirm('선택 된 SLA 보고서를 삭제 하시겠습니까?') === true) {

                    jQuery.ajax({
                        url: './setSLAReportDel',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            sla_seq: res.sla_seq
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

        // 보고서 내보내기 버튼 클릭 시
        jQuery('#btnReport').on('click', function() {

            var rowIdx = jQuery('#reportList').jqGrid('getGridParam','selrow');

            if (rowIdx === null) {

                common.setOSXModal('SLA 보고서 목록을 선택 후 사용하기 바랍니다.');
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
                        sla_seq: jQuery("#reportList").jqGrid('getRowData', rowIdx).sla_seq
                    })
                })
            ).then(function(data) {

                var handle = window.open($.fn.preUrl + '/report/ozReportSLAPreview?key=' + data.key, 'reportPreview', 'directories=0, width=1171, height=600, location=0, menubar=0, resizeable=0, status=0, toolbar=0');
                handle.focus();
            })
            .fail(common.ajaxError)
            .always(function() {

                jQuery.fn.loadingComplete();
                return false;
            });
        });

        var tabObj = jQuery('#myTabs a[data-toggle="tab"]');

        // 탭 show trigger 이벤트
        tabObj.off('shown.bs.tab').on('shown.bs.tab', function (e) {
            // 해당 계통의 펌프현황 정보 가져오기(헤더 설정을 위한)
            var tabIdx = tabObj.index(e.target);
            var dataObj = jQuery("#reportList");

            switch(tabIdx) {
                case 0:
                    // 기본정보 입력폼 Setting
                    var selIdx = dataObj.jqGrid('getGridParam', 'selrow');

                    jQuery('#btnSave, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소, 파일, 파일버튼 Style 변경
                    jQuery('#btnDel').attr('disabled', true); // 삭제 버튼 비활성화(입력 모드이기 때문에)

                    var regBtn = jQuery('#btnSave');

                    var tmpBtnStat = regBtn.html();

                    if (selIdx !== null) {

                        tmpBtnStat = tmpBtnStat.replace('등록', '수정');
                    } else {

                        tmpBtnStat = tmpBtnStat.replace('수정', '등록');
                    }

                    regBtn.html(tmpBtnStat); // 등록버튼 명칭 변경

                    lc.infoSetting(selIdx, dataObj.jqGrid('getRowData', selIdx));

                    break;
                case 1:
                    // 평가결과 탭
                    jQuery('#btnSave, #btnCancel').attr('disabled', true); // 등록, 취소 버튼 비활성화
                    jQuery('#btnDel').attr('disabled', authCrud.DEL_FL === 'N' ? true : false); // 권한에 따라서 삭제 버튼 권한 처리

                    lc.dataReload('resGrid'); // SLA 서비스 평가결과 조회
                    break;
            }
        });

        // jqGrid의 입력/수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#bwList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                lc.dataSend();
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('report'); // SLA 보고서 목록
        lc.gridSetting('resGrid'); // SLA 서비스 평가결과
        lc.gridSetting('bw'); // 대역폭 사용률 정보
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {
        // jqGrid 2개
        lc.resizePanel('report');
        lc.resizePanel('resGrid');

    }).trigger('resize');
});