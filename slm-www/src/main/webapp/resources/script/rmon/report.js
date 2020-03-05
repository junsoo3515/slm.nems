/**
 * User: 이종혁
 * Date: 2016.06.09
 * Time: 오후 3:20
 */
define('local', ['common', 'formcheck', 'jqGrid.setting','jquery', 'jqGrid','highcharts'], function (common, formcheck, jqFn, jQuery, Highcharts) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    var jsonData = [];
    var chartData = [];
    var tempArr = [];
    var seriesData = [];
    var jObject = new Object();
    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'checkInfoPanel':

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

            case 'reportList' :
                // 요청사항 목록

                jqOpt = {
                    url: './getRmonReportList'
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

            case 'checkGridList' :
                // 요청사항조치사항 목록


                jqOpt = {
                    url: './getRmonReportCheckList'
                };
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            da_seq: jQuery("#reportList").jqGrid('getRowData', jQuery("#reportList").jqGrid('getGridParam', 'selrow')).da_seq
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

            case 'troubleGridList':
                // 장애현황표 목록
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            da_seq: jQuery('#reportList').jqGrid('getRowData', jQuery('#reportList').jqGrid('getGridParam', 'selrow')).da_seq
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

            case 'troubleDetailGridList' :
                // 장애세부내역 목록

                jqOpt = {
                    url: './getTroubleDetailList'
                };
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            da_seq: jQuery('#reportList').jqGrid('getRowData', jQuery('#reportList').jqGrid('getGridParam', 'selrow')).da_seq
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page:1}]);
                break;

        }


    }


    // 기본정보 폼 Setting
    function infoSetting(rowID, obj, str) {

        switch(str) {

            case 'report' :

                var regBtn = jQuery('#btnReg');

                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    if(authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') {

                        jQuery('#btnReg, #btnCancel').attr('disabled', false);
                        regBtn.html(regBtn.html().replace("등록", "수정"));
                    }
                    if(authCrud.DEL_FL === 'Y') {

                        jQuery('#btnDel').attr('disabled', false);
                    }
                    if(jQuery('#checktab').attr('aria-expanded')==='true'){
                        jQuery('#btnReg, #btnCancel').prop('disabled', true);
                        dataReload('checkGrid');
                    }
                    if(jQuery('#troubletab').attr('aria-expanded')==='true'){
                        jQuery('#btnReg, #btnCancel').prop('disabled', true);
                        gridSetting('troubleGrid');
                    }
                    if(jQuery('#troubleDetailtab').attr('aria-expanded')==='true'){
                        jQuery('#btnReg, #btnCancel').prop('disabled', true);
                        dataReload('troubleDetailGrid');
                    }
                    else {
                        jQuery('#btnReg, #btnCancel').prop('disabled', false);


                        jQuery.when(
                            jQuery.ajax({
                                url: './getRmonReportData',
                                type: "POST",
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                data: JSON.stringify({
                                    da_seq: obj.da_seq
                                })
                            })
                        ).then(function(data) {

                                common.setValues({
                                    wPfmc: data.pfmc,
                                    wPlan: data.plan,
                                    wDa_seq: data.da_seq
                                });
                                jQuery('#briefDateTimePicker').data('DateTimePicker').date(new Date(obj.brief_dt));
                                jQuery('#wStart_dt').datepicker('update', data.start_dt).trigger('changeDate');
                                jQuery('#wEnd_dt').datepicker('update', data.end_dt).trigger('changeDate');
                            })
                            .fail(common.ajaxError)
                            .always(function() {

                                return false;
                            });

                    }



                }

                break;

            case 'check' :

                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    common.setValues({
                        wMeasure_dt: obj.mea_dt,
                        wMeasureCont: obj.measure_cont,
                        wMea_seq: obj.mea_seq
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

            var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery('#checktab').prop('disabled',true);
                jQuery('#troubletab').prop('disabled',true);
                jQuery('#troubleDetailtab').prop('disabled',true);
                jQuery("#reportList").jqGrid("resetSelection"); // Grid Select Reset 처리

            }

            return false;
        }

        switch(objID) {

            case 'infoPanel':

                common.clearElement('#' + objID); // form element

                var regBtn = jQuery('#btnReg');

                jQuery('#btnReg, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
                jQuery('#btnDel').attr('disabled',true);
                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경


                break;
            case 'checkPanel':

                common.clearElement('#' + objID); // form element


                var rowid = jQuery("#checkGridList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null && isListReset) {
                    jQuery("#checkGridList").jqGrid("resetSelection"); // Grid Select Reset 처리
                }

                break;
            case 'updownPanel':

                jQuery('#checkPanel>div>div').find('h3,p').each(function(){
                    jQuery('#'+this.id).text('');
                });
                break;

            case 'troublePanel':
                var rowid = jQuery('#troubleGridList').jqGrid('getGridParam','selrow');

                if(rowid !== null & isListReset){
                    jQuery('#troubleGridList').jqGrid('resetSelection'); // Grid Select Reset 처리
                }
                break;

            case 'troubleDetailPanel':

                var rowid = jQuery('#troubleDetailGridList').jqGrid('getGridParam', 'selrow');

                if (rowid !== null && isListReset) {
                    jQuery('#troubleDetailGridList').jqGrid('resetSelection'); // Grid Select Reset 처리
                }
                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend(str) {

        switch(str) {

            case 'report':
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

                // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

                reqData = common.changeKeys(reqData, [
                    {k: 'wBrief_dt', v: 'brief_dt'},
                    {k: 'wStart_dt', v: 'start_dt'},
                    {k: 'wEnd_dt', v: 'end_dt'},
                    {k: 'wPfmc', v: 'pfmc'},
                    {k: 'wPlan', v: 'plan'},
                    {k: 'wDa_seq', v: 'da_seq'},
                ]);


                // 데이터 전송
                jQuery.when(
                    jQuery.ajax({
                        url: './setReportAct',
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

                            var obj = jQuery("#reportList");
                            var rowid = obj.jqGrid('getGridParam', 'selrow');
                            if (rowid !== null) {
                                panelClear(true);
                                obj.trigger("reloadGrid");

                            } else {
                                // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                                panelClear(true);
                                dataReload('report');
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

        var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

        if (rowid === null) {
            // 삭제 모드
            if (authCrud.DEL_FL === 'N') return false;
        }

        var formData = jQuery('#infoPanel :input');
        var reqData = formData.serializeObject();

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

        reqData = common.changeKeys(reqData, [
            { k: 'wDa_seq', v: 'da_seq' },
        ]);


        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setReportDel',
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

                    var obj = jQuery("#reportList");
                    var rowid = obj.jqGrid('getGridParam', 'selrow');

                    if (rowid !== null) {
                        obj.jqGrid('delRowData', rowid);
                        obj.trigger("reloadGrid");
                        panelClear(true);
                        dataReload('report');
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
            { list: "reportList", panel: "reportPanel" },
            { list: "checkGridList", panel: "checkGridPanel" },
            { list: "troubleGridList", panel: "troublePanel" },
            { list: "troubleDetailGridList", panel: "troubleDetailPanel" }
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
            case 'reportList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    height: '789',
                    rowList: [10, 30, 50, 100],
                    colNames: ['제목', '보고일', '',''],
                    colModel: [
                        {name: 'subject', index: 'subject', width: 2, align: "center", sortable: false},
                        {name: 'brief_dt', index: 'brief_dt', width: 2, align: "center", sortable: false},
                        {name: 'da_seq', index: 'da_seq', hidden: true},
                        {name: 'rpt_cd', index: 'rpt_cd', hidden: true}
                    ],
                    onInitGrid: function () {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id) {

                        jQuery('.nav-tabs a[href="#checkPanel"]').tab('show');
                        jQuery.when(
                            // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                            panelClear(true, '', false)
                        ).then(function (res) {
                                // 기본정보 입력폼 Setting
                                var ret = dataGrid.jqGrid('getRowData', id);

                                infoSetting(id, ret,'report');
                            }).always(function () {

                                return false;
                            });
                            dataReload('troubleDetailGrid');
                        jQuery('#checktab').prop('disabled',false);
                        jQuery('#troubletab').prop('disabled',false);
                        jQuery('#troubleDetailtab').prop('disabled',false);
                    },
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {

                        jQuery('#btnDel').attr('disabled', true);

                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        jQuery.fn.loadingComplete();
                    }
                };

                break;
            case 'checkGridList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    height:510,
                    rowList: [10, 30, 50, 100],
                    rownumbers: false,
                    postData : {
                        da_seq: jQuery("#reportList").jqGrid('getRowData', jQuery("#reportList").jqGrid('getGridParam', 'selrow')).da_seq
                    },
                    colNames: ['구분', '분류','점검<br />결과','장애<br />시간', '서비스<br />중단시간', '전체','내부<br />요인','외부<br />요인','조치<br />완료','장비<br />수량','비고'],
                    colModel: [
                        {name: 'rpt_gubun',index: 'rpt_gubun',width: 2,align: "center",sortable: false},
                        {name: 'rpt_bunryu',index: 'rpt_bunryu',width: 2,align: "center",sortable: false},
                        {name: 'res_fl',index: 'res_fl',width: 1,align: "center",sortable: false},
                        {name: 'error_time_sum',index: 'error_time_sum',width: 2,align: "center",sortable: false},
                        {name: 'hold_time_sum',index: 'hold_time_sum',width: 2,align: "center",sortable: false},
                        {name: 'cnt_err',index: 'cnt_err',width: 1,align: "center",sortable: false},
                        {name: 'cnt_inn',index: 'cnt_inn',width: 1,align: "center",sortable: false},
                        {name: 'cnt_out',index: 'cnt_out',width: 1,align: "center",sortable: false},
                        {name: 'cnt_fin',index: 'cnt_fin',width: 1,align: "center",sortable: false},
                        {name: 'cnt_all',index: 'cnt_all',width: 1,align: "center",sortable: false},
                        {name: 'add_cont',index: 'add_cont',width: 3,align: "center",sortable: false}
                    ],
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {
                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                        jQuery(this).jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders:[
                                {startColumnName: 'cnt_err', numberOfColumns: 3, titleText: '장애수량'},
                            ]
                        });

                        var obj = jQuery("#reportList").jqGrid('getRowData', jQuery("#reportList").jqGrid('getGridParam', 'selrow'));
                        obj.subject = obj.subject.substr(0,8);

                        var cnt_err_sum = jQuery(this).jqGrid('getCol','cnt_err', false, 'sum');
                        var cnt_inn_sum = jQuery(this).jqGrid('getCol','cnt_inn', false, 'sum');
                        var cnt_out_sum = jQuery(this).jqGrid('getCol','cnt_out', false, 'sum');
                        var cnt_fin_sum = jQuery(this).jqGrid('getCol','cnt_fin', false, 'sum');

                        jQuery('#wMonthDay').text(obj.subject);
                        jQuery('#wCnt_err_sum').text(cnt_err_sum);
                        jQuery('#wCnt_out_sum').text(cnt_out_sum);
                        jQuery('#wCnt_inn_sum').text(cnt_inn_sum);
                        jQuery('#wCnt_fin_sum').text(cnt_fin_sum);

                        // 데이터 전송
                        jQuery.when(
                            jQuery.ajax({
                                url: './getRmonReportCheckDateUpDown',
                                type: "POST",
                                dataType: "json",
                                contentType: "application/json; charset=utf-8",
                                data: JSON.stringify({
                                    da_seq: jQuery("#reportList").jqGrid('getRowData', jQuery("#reportList").jqGrid('getGridParam', 'selrow')).da_seq
                                })
                            })
                        )
                        .then(function (data) {
                            // 결과에 따라 다음 이벤트 처리
                            jQuery('#wError_time_sum').text(data.error_time_sum);
                            jQuery('#wHold_time_sum').text(data.hold_time_sum);
                            var err_signal = data.up_down_error;
                            var hold_signal = data.up_down_hold;
                            if(err_signal==='up') {
                                jQuery('#disorderupdown').attr('class','fa fa-arrow-up text-danger');
                            }else if(err_signal==='eq') {
                                jQuery('#disorderupdown').attr('class','fa fa-minus');
                            }else {
                                jQuery('#disorderupdown').attr('class','fa fa-arrow-down text-primary');

                            }
                            if(hold_signal==='up') {
                                jQuery('#holdupdown').attr('class','fa fa-arrow-up text-danger');
                            }else if(err_signal==='eq') {
                                jQuery('#holdupdown').attr('class','fa fa-minus');
                            }else {
                                jQuery('#holdupdown').attr('class','fa fa-arrow-down text-primary');
                            }


                        })
                        .fail(function () {
                                jQuery('#wError_time_sum').text('');
                                jQuery('#wHold_time_sum').text('');
                                jQuery('#disorderupdown').removeClass();
                                jQuery('#holdupdown').removeClass();
                                common.setOSXModal('당월 또는 전월에 해당하는 일일점검현황이 존재하지 않습니다.');
                                common.ajaxError
                            })
                        .always(function () {

                            jQuery.fn.loadingComplete();
                            return false;
                        });

                        jQuery.fn.loadingComplete();
                    }
                };

                break;

            case 'troubleGridList':

                var arrColNames = ['','','장비유형','장비','장애수량'];
                var arrColModel = [

                    {name: 'da_seq', index: 'da_seq', hidden:true, sortable: false},
                    {name: 'rpt_cd', index: 'rpt_cd', hidden:true, sortable: false},
                    {name: 'eqp_type', index: 'eqp_type', width: 170, sortable: false,align:'center'},
                    {name: 'eqp_nm', index: 'eqp_nm', width: 170, sortable: false,align:'center'},
                    {name: 'cnt_err', index: 'cnt_err', width: 100, sortable: false,align:'center'}
                ];
                jqDefault.rownumbers = false;
                jqDefault.rowNum = -1;
                jqDefault.autowidth = false;
                jqDefault.shrinkToFit = false;

                jQuery.when(
                    jQuery.ajax({
                        url: './getTroubleAmt',
                        type: "POST",
                        dataType: "json",
                        contentType: "application/json; charset=utf-8",
                        data: JSON.stringify({
                            da_seq: jQuery("#reportList").jqGrid('getRowData', jQuery("#reportList").jqGrid('getGridParam', 'selrow')).da_seq
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
                            if(index+1) {
                                arrColNames.push(item.val);
                                arrColModel.push({name: 'col' + item.id, index: 'col' + item.id, width: 1, sortable: false, align:'center',width:'45'});
                            }else{
                                return false;
                            }
                        });

                    }).done(function() {
                        jqOpt = {
                            url : './getTroubleGridList',
                            height: 300,
                            scroll: 1,
                            postData: {
                                da_seq: jQuery('#reportList').jqGrid('getRowData', jQuery('#reportList').jqGrid('getGridParam', 'selrow')).da_seq
                            },
                            rowList: [10, 30, 50, 100],
                            colNames: arrColNames,
                            colModel: arrColModel,
                            beforeRequest: function (){
                                // POST 보내기 전 이벤트
                                if(jQuery(this).jqGrid('getGridParam', 'url') === '') return false;
                                var highCharts = jQuery('#troubleCharts').highcharts();
                                if(highCharts !== undefined) {
                                    highCharts.xAxis[0].setCategories([]);
                                    while(highCharts.series.length){
                                        highCharts.series[0].remove();
                                    }
                                }
                            },
                            beforeProcessing: function (data){
                                var highCharts = jQuery('#troubleCharts').highcharts();
                                var index = '';
                                seriesData = [];
                                tempArr = [];
                                if(data != null) {
                                    jsonData = data.rows;
                                    jQuery.each(jsonData, function(key, value){
                                        if(parseInt(JSON.stringify(value.cnt_err).replace(/"/g,'').trim()) > 0) {
                                            if(jQuery.inArray(value.eqp_type, tempArr) != -1){
                                                index = tempArr.indexOf(value.eqp_type) + 1;
                                                tempArr[index] = parseInt(tempArr[index]) + parseInt(value.cnt_err);
                                            }
                                            if(jQuery.inArray(value.eqp_type, tempArr) == -1){
                                                tempArr.push(value.eqp_type);
                                                tempArr.push(JSON.parse(JSON.stringify(value.cnt_err).replace(/"/g, '')));
                                            }
                                        }
                                    });
                                    console.log(tempArr);
                                    for(var i = 0; i< tempArr.length; i+=2){
                                        chartData.push(tempArr[i]);
                                        chartData.push(tempArr[i+1]);
                                        seriesData.push(chartData);
                                        chartData= [];
                                    }
                                    highCharts.addSeries({
                                        name: '장애 건수',
                                        data: seriesData,
                                        dataLabels: {
                                            enabled: true,
                                            rotation: -90,
                                            color: '#FFFFFF',
                                            align: 'right',
                                            format: '{point.y}', // one decimal
                                            y: 10, // 10 pixels down from the top
                                            style: {
                                                fontSize: '13px',
                                                fontFamily: 'Verdana, sans-serif'
                                            }
                                        }
                                    });
                                    highCharts.redraw();
                                    console.log(seriesData);
                                }
                            },
                            onInitGrid: function () {

                                dataReload(tarID);
                            },
                            loadError: function (xhr, status, error) {
                                alert(xhr.responseText);
                            },
                            loadComplete: function () {
                                jQuery("td[aria-describedby^='troubleGridList_col']").css('color','red');
                                jQuery(".ui-jqgrid tr.jqgrow td").css('padding-right','15px');
                                jQuery(".ui-jqgrid .ui-jqgrid-htable thead th").css('padding-right','15px');
                                // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                                jQuery.when(
                                    resizePanel()
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
                            {}, // search options
                            {} // view options
                        );
                    });
                break;

            case 'troubleDetailGridList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    height:660,
                    sortable: true,
                    rowList: [10, 30, 50, 100],
                    rownumbers: true,
                    colNames: ['장애유형', '장비유형', '장비', '점검완료', '발생일', '정지일', '시작일', '예정일', '완료일'],
                    colModel: [
                        {name: 'dis_type',index: 'dis_type',width: 1,align: "center",sortable: false, edittype:'text'},
                        {name: 'eqp_type',index: 'eqp_type',width: 1,align: "center",sortable: false, edittype:'text'},
                        {name: 'eqp_nm',index: 'eqp_nm',width: 1,align: "center",sortable: false},
                        {name: 'fin_fl',index: 'fin_fl',width: 1,align: "center",sortable: false, edittype:'text',
                            formatter: function(cellValue){
                                var ret = null;
                                if(cellValue == 'Y') ret = 'O';
                                else ret = '';
                                return ret;
                            }
                        },
                        {name: 'occur_dt',index: 'occur_dt',width: 1,align: "center",sortable: true},
                        {name: 'sv_stop_dt',index: 'sv_stop_dt',width: 1,align: "center",sortable: true},
                        {name: 'sv_start_dt',index: 'sv_start_dt',width: 1,align: "center",sortable: true},
                        {name: 'mea_plan_dt',index: 'mea_plan_dt',width: 1,align: "center",sortable: true},
                        {name: 'mea_fin_dt',index: 'mea_fin_dt',width: 1,align: "center",sortable: true}
                    ],
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {
                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        jQuery(this).jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders:[
                                {startColumnName: 'sv_stop_dt', numberOfColumns: 2, titleText: '서비스 중지기간'},
                                {startColumnName: 'mea_plan_dt', numberOfColumns: 2, titleText: '조치일'}
                            ]
                        });

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
    function highChartsSetting() {
        jQuery('#troubleCharts').highcharts({
            chart: {
                rendTo: 'troubleCharts',
                type: 'column'
            },
            title: {
                text: ' '
            },
            xAxis: {
                categories: null,
                type: 'category',
                labels: {
                    enable: true,
                    style: {
                        fontSize: '13px',
                        fontFamily: 'Verdana, sans-serif'
                    }
                }
            },
            yAxis: {
                min: 0,
                title: {
                    text: '장애 건수'
                },
                labels: {
                    enabled: true,
                    style: {
                        fontWeight: 'bold'
                    }
                }
            },
            legend: {
                enabled: false
            },
            tooltip: {
                pointFormat: ' 장애 건수: <b>{point.y} 건</b>'
            },
            series: null
        });
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
        gridSetting: gridSetting,
        highChartsSetting: highChartsSetting
    }
});

require(['common', 'darkhand', 'local' ,'bootstrap-datepicker.lang','bootstrap-datetimepicker','bootstrap-switchery','jquery'], function (common, darkhand, lc, datepicker, datetimepicker, Switchery, jQuery) {
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

                        //jQuery('#briefDateTimePicker').data('DateTimePicker').date(new Date(new Date().setHours(23, 59, 59)));
                        jQuery('#briefDateTimePicker').data('DateTimePicker').date(new Date());
                        var tempDate = new Date();
                        var nowVal  =  new Date(tempDate.getFullYear(),tempDate.getMonth(),"01");
                        var resMM = (new String(nowVal.getMonth()+1).length == 1) ? '0' + (nowVal.getMonth()+1) : (nowVal.getMonth()+1);
                        var resDD = (new String(nowVal.getDate()).length == 1) ? '0' + nowVal.getDate() : nowVal.getDate();
                        nowVal  = nowVal.getFullYear()+'-'+resMM+'-'+resDD;
                        resMM = (new String(tempDate.getMonth()+1).length == 1) ? '0' + (tempDate.getMonth()+1) : (tempDate.getMonth()+1);
                        resDD = (new String(tempDate.getDate()).length == 1) ? '0' + tempDate.getDate() : tempDate.getDate();
                        tempDate  = tempDate.getFullYear()+'-'+resMM+'-'+resDD;
                        jQuery('#wStart_dt').datepicker('update', nowVal).trigger('changeDate');
                        jQuery('#wEnd_dt').datepicker('update', tempDate).trigger('changeDate');
                        lc.panelClear(true); // 전체 폼 초기화
                        lc.panelClear(false,'checkPanel',false);
                        lc.panelClear(false,'updownPanel',false);
                        lc.panelClear(false,'troublePanel',false);
                        lc.panelClear(false,'troubleDetailPanel',false);

                        jQuery("#checkGridList").clearGridData();
                        //jQuery("#troubleGridList").clearGridData();
                        jQuery("#troubleDetailGridList").clearGridData();

                        lc.dataReload('report'); // 리포트 목록 갱신


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
                            lc.dataSend('report');
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
    function tabEvent() {

        jQuery('.nav-tabs a').on('shown.bs.tab', function(event) {
            var x = $(event.target).text();         // active tab

            if(x==='주요현황') {
                jQuery('#btnReg, #btnCancel').prop('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ) ? false : true);
                jQuery('#btnDel').prop('disabled', (authCrud.DEL_FL === 'Y' ) ? false : true);

                var rowid = jQuery("#checkGridList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null ) {

                    jQuery("#checkGridList").jqGrid("resetSelection"); // Grid Select Reset 처리
                }

                lc.panelClear(false, 'checkPanel', false);
                lc.panelClear(false, 'infoPanel', false);

                var rowid = jQuery("#reportList").jqGrid('getGridParam', 'selrow');

                var ret = jQuery("#reportList").jqGrid('getRowData', rowid);

                lc.infoSetting(rowid, ret,'report');


            }
            if(x==='장애현황'){
                jQuery('#btnReg, #btnCancel').prop('disabled',  true );
                lc.panelClear(false, 'troublePanel', false);
                lc.gridSetting('troubleGrid');
                lc.highChartsSetting();
            }
            if(x==='장애세부내역'){
                jQuery('#btnReg, #btnCancel').prop('disabled',  true );
                lc.panelClear(false, 'troubleDetailPanel', false);
                lc.dataReload('troubleDetailGrid');

            }
            if(x==='점검현황') {

                jQuery('#btnReg, #btnCancel').prop('disabled',  true );

                lc.dataReload('checkGrid');

            }
            var y = $(event.relatedTarget).text();  // previous tab
        });
    }
    function dateSetting() {
        var nowTemp = new Date();
        jQuery("#briefDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD',
            showTodayButton: true,
            showClear: true
            //maxDate: 'now',
        });

        //jQuery("#briefDateTimePicker").datetimepicker({
        //    useCurrent: false //Important! See issue #1075
        //});

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
                case 'wStart_dt':
                    // TODO : 개발 완료 후 m - 1 로 처리
                    nowVal = common.termDate(nowVal, 'm', -1, '-');
                    break;
            }

            jQuery(this).datepicker('update', nowVal).trigger('changeDate');
        });
    }


    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {



        jQuery('#checktab').prop('disabled',true);
        jQuery('#troubletab').prop('disabled',true);
        jQuery('#troubleDetailtab').prop('disabled',true);
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

        jQuery('#checktab').on('click', function() {
            if(jQuery('#checktab').prop('disabled')) {
                common.setOSXModal('월간주요현황 목록을 선택 후 사용해주시기 바랍니다.');
            }
        });
        jQuery('#troubletab').on('click', function() {
            if(jQuery('#troubletab').prop('disabled')) {
                common.setOSXModal('월간주요현황 목록을 선택 후 사용해주시기 바랍니다.');
            }
        });
        jQuery('#troubleDetailtab').on('click', function() {
            if(jQuery('#troubleDetailtab').prop('disabled')) {
                common.setOSXModal('월간주요현황 목록을 선택 후 사용해주시기 바랍니다.');
            }
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
                        da_seq: jQuery("#reportList").jqGrid('getRowData', jQuery("#reportList").jqGrid('getGridParam', 'selrow')).da_seq
                    })
                })
            ).then(function(data) {

                    var handle = window.open($.fn.preUrl + '/report/ozReportMonPreview?key=' + data.key, 'reportPreview', 'directories=0, width=1171, height=600, location=0, menubar=0, resizeable=0, status=0, toolbar=0');
                    handle.focus();
                })
                .fail(common.ajaxError)
                .always(function() {

                    jQuery.fn.loadingComplete();
                    return false;
                });
        });

        // 취소 버튼 클릭 시
        jQuery('#btnCancel').on('click', function () {
            jQuery('#briefDateTimePicker').data('DateTimePicker').date(new Date());
            var tempDate = new Date();
            var nowVal  =  new Date(tempDate.getFullYear(),tempDate.getMonth(),"01");
            var resMM = (new String(nowVal.getMonth()+1).length == 1) ? '0' + (nowVal.getMonth()+1) : (nowVal.getMonth()+1);
            var resDD = (new String(nowVal.getDate()).length == 1) ? '0' + nowVal.getDate() : nowVal.getDate();
            nowVal  = nowVal.getFullYear()+'-'+resMM+'-'+resDD;
            resMM = (new String(tempDate.getMonth()+1).length == 1) ? '0' + (tempDate.getMonth()+1) : (tempDate.getMonth()+1);
            resDD = (new String(tempDate.getDate()).length == 1) ? '0' + tempDate.getDate() : tempDate.getDate();
            tempDate  = tempDate.getFullYear()+'-'+resMM+'-'+resDD;
            jQuery('#wStart_dt').datepicker('update', nowVal).trigger('changeDate');
            jQuery('#wEnd_dt').datepicker('update', tempDate).trigger('changeDate');
            lc.panelClear(true);
            lc.panelClear(true,'checkPanel',true);
            lc.panelClear(false,'updownPanel',false);
            lc.panelClear(false,'troublePanel',false);
            lc.panelClear(false,'troubleDetailPanel',false);

            jQuery("#checkGridList").clearGridData();

            jQuery('.nav-tabs a[href="#infoPanel"]').tab('show')
        });

        // 그리드 초기화
        lc.gridSetting('report'); // 요청사항 목록

        lc.gridSetting('checkGrid'); // 요청사항 조치사항 목록

        lc.gridSetting('troubleDetailGrid'); // 장애내역 목록

    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel();

    }).trigger('resize');
});