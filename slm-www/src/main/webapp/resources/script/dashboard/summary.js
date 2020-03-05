/**
 * User: 이종혁
 * Date: 2016.06.21
 * Time: 오후 3:20
 */
define('local', ['common', 'formcheck', 'highcharts', 'jqGrid.setting', 'jquery', 'jqGrid'], function (common, formcheck, Highcharts, jqFn, jQuery) {

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

        switch (listID) {

            case 'slaList' :
                // sla 리스트 목록

                jqOpt = {
                    url: './getDashSlaSummaryList'
                };

                filterData = jQuery('#slaPanel :input').serializeObject();
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        search: true,
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page: 1}]);
                break;

            case 'dailyList' :
                // 일일점검현황 목록

                jqOpt = {
                    url: './getDashDailySummaryList'
                };

                filterData = jQuery('#dailyPanel :input').serializeObject();
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        search: true,
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page: 1}]);
                break;

        }


    }


    // 기본정보 폼 Setting
    function infoSetting(str) {


        switch (str) {

            case 'sla' :
                var chart = jQuery('#container').highcharts();
                chart.xAxis[0].setCategories(jQuery("#slaList").jqGrid("getCol", "brief_dt"));

                var timeliness = jQuery("#slaList").jqGrid("getCol", "timeliness");
                var availability = jQuery("#slaList").jqGrid("getCol", "availability");
                var productivity = jQuery("#slaList").jqGrid("getCol", "productivity");
                var general = jQuery("#slaList").jqGrid("getCol", "general");
                for (var i = 0; i < timeliness.length; i++) {
                    timeliness[i] = parseInt(timeliness[i]);
                    availability[i] = parseInt(availability[i]);
                    productivity[i] = parseInt(productivity[i]);
                    general[i] = parseInt(general[i]);
                }

                chart.addSeries({
                    name: '적시성',
                    data: timeliness,
                    color: '#2f7ed8'
                });
                chart.addSeries({
                    name: '가용성',
                    data: availability,
                    color: '#0d233a'
                });
                chart.addSeries({
                    name: '생산성 및 보안관리',
                    data: productivity,
                    color: '#8bbc21'
                });
                chart.addSeries({
                    name: '일반관리',
                    data: general,
                    color: '#910000'
                });

                chart.redraw();


                break;


            case 'daily' :

                var chart = jQuery('#dailyContainer').highcharts();

                var brief_dt = jQuery("#dailyList").jqGrid("getCol", "brief_dt");
                var daily_center_percent = jQuery("#dailyList").jqGrid("getCol", "daily_center_percent");
                var daily_infra_percent = jQuery("#dailyList").jqGrid("getCol", "daily_infra_percent");
                var daily_develop_percent = jQuery("#dailyList").jqGrid("getCol", "daily_develop_percent");
                var daily_solution_percent = jQuery("#dailyList").jqGrid("getCol", "daily_solution_percent");
                var center_percent = [];
                var infra_percent = [];
                var develop_percent = [];
                var solution_percent = [];


                for (var i = 0; i < brief_dt.length; i++) {
                    daily_center_percent[i] = parseFloat(daily_center_percent[i]);
                    daily_infra_percent[i] = parseFloat(daily_infra_percent[i]);
                    daily_develop_percent[i] = parseFloat(daily_develop_percent[i]);
                    daily_solution_percent[i] = parseFloat(daily_solution_percent[i]);
                    var tempdate = Date.UTC(brief_dt[i].substr(0, 4), parseInt(brief_dt[i].substr(5, 2)) - 1, brief_dt[i].substr(8, 2))
                    center_percent.push([tempdate, daily_center_percent[i]]);
                    infra_percent.push([tempdate, daily_infra_percent[i]]);
                    develop_percent.push([tempdate, daily_develop_percent[i]]);
                    solution_percent.push([tempdate, daily_solution_percent[i]]);

                }

                chart.addSeries({
                    name: '센터',
                    data: center_percent,
                    color: '#1aadce'
                });
                chart.addSeries({
                    name: '인프라',
                    data: infra_percent,
                    color: '#492970'
                });
                chart.addSeries({
                    name: '개발',
                    data: develop_percent,
                    color: '#f28f43'
                });
                chart.addSeries({
                    name: '상용',
                    data: solution_percent,
                    color: '#77a1e5'
                });

                break;

        }


    }

    // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if (isAll) {


            var rowid = jQuery("#slaList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#slaList").jqGrid("resetSelection"); // Grid Select Reset 처리

            }
            var rowid = jQuery("#dailyList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#dailyList").jqGrid("resetSelection"); // Grid Select Reset 처리

            }

            return false;
        }

        switch (objID) {

            case 'slaPanel':
                var rowid = jQuery("#slaList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null && isListReset) {

                    jQuery("#slaList").jqGrid("resetSelection"); // Grid Select Reset 처리

                }


                break;
            case 'dailyPanel':

                var rowid = jQuery("#dailyList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null && isListReset) {
                    jQuery("#dailyList").jqGrid("resetSelection"); // Grid Select Reset 처리
                }

                break;
        }
    }


    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel() {

        var arrObj = [
            {list: "slaList", panel: "slaGridPanel"},
            {list: "dailyList", panel: "dailyGridPanel"}
        ];


        jQuery.each(arrObj, function (sIdx, data) {

            jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width());
        });
    }

    function hiChartSla(objID) {
        switch (objID) {

            case 'sla':

                jQuery('#container').highcharts({
                    chart: {
                        renderTo: 'container',
                        type: 'column'
                        //zoomType: 'xy'
                    },
                    credits: {
                        enabled: false
                    },
                    title: {
                        text: null
                    },
                    xAxis: {
                        categories: null
                    },
                    yAxis: {
                        min: 0,
                        title: {
                            text: '평가점수'
                        },
                        stackLabels: {
                            enabled: true,
                            style: {
                                fontWeight: 'bold',
                                color: (Highcharts.theme && Highcharts.theme.textColor) || 'gray'
                            }
                        }
                    },
                    legend: {
                        align: 'right',
                        x: -30,
                        verticalAlign: 'top',
                        y: 25,
                        floating: true,
                        backgroundColor: (Highcharts.theme && Highcharts.theme.background2) || 'white',
                        borderColor: '#CCC',
                        borderWidth: 1,
                        shadow: false
                    },
                    tooltip: {
                        crosshairs: true,
                        shared: true,
                        valueDecimals: 1, // 소수점 자리수
                        valueSuffix: '', // 단위
                        formatter: function () {

                            var date = new Date(this.x.substr(0, 5), parseInt(this.x.substr(5, 2)) - 1, parseInt(this.x.substr(8, 2)) + 1)

                            var p = '<span style="font-size: 10px">' + Highcharts.dateFormat('%Y-%m-%d', date) + '</span><br/>';

                            jQuery.each(this.points, function () {

                                p += '<span style="color:' + this.point.color + '">\u25CF</span>' + this.series.name + ': <b>' + this.y + '</b><br/>';
                            });

                            return p;

                        }
                    },
                    plotOptions: {
                        column: {
                            stacking: 'normal',
                            dataLabels: {
                                enabled: true,
                                color: (Highcharts.theme && Highcharts.theme.dataLabelsColor) || 'white',
                                style: {
                                    textShadow: '0 0 3px black'
                                }
                            }
                        }
                    },
                    series: null

                });

                break;
            case 'daily':

                jQuery('#dailyContainer').highcharts({
                    chart: {
                        renderTo: 'dailyContainer',
                        //type : 'spline',
                        zoomType: 'xy',

                        type: 'spline'
                    },
                    credits: {
                        enabled: false
                    },
                    title: {
                        text: null
                    },
                    subtitle: {
                        text: null
                    },
                    xAxis: {
                        type: 'datetime',
                        dateTimeLabelFormats: { // don't display the dummy year
                            second: '%H:%M:%S',
                            minute: '%H:%M',
                            hour: '%H:%M',
                            day: '%Y-%m-%d',
                            week: '%Y-%m-%d',
                            month: '%Y-%m',
                            year: '%Y'

                        }
                    },
                    yAxis: {
                        title: {
                            text: '정상항목 비율 (%)'
                        },
                        min: 0
                    },
                    tooltip: {
                        crosshairs: true,
                        shared: true,
                        valueDecimals: 1, // 소수점 자리수
                        valueSuffix: '', // 단위
                        formatter: function () {

                            var p = '<span style="font-size: 10px">' + Highcharts.dateFormat('%Y-%m-%d %H:%M', this.x) + '</span><br/>';

                            jQuery.each(this.points, function () {

                                p += '<span style="color:' + this.point.color + '">\u25CF</span>' + this.series.name + ': <b>' + this.y + '</b><br/>';
                            });

                            return p;

                        }
                    },

                    plotOptions: {
                        spline: {
                            marker: {
                                enabled: true
                            }
                        }
                    },

                    series: null
                });

                break;
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
            pager: pageID,
            rowNum: 30,                 // 처음에 로드될 때 표출되는 row 수
            rowList: [], // row 갯수 표출 세팅
            viewsortcols: [true, 'vertical', true], // 소팅 인자 세팅
            rownumbers: true,           // Grid의 RowNumber 표출
            viewrecords: true,          // 우측 View 1-4 Text 표출 부분
            gridview: true,             // Grid Alert
            autowidth: true,            // width 자동 맞춤
            shrinkToFit: true,          // width에 맞춰 Cell Width 자동 설정
            height: 300,                // 세로 크기
            caption: '',                // 캡션 명(없으면 표출 안됨)
            beforeRequest: function () {
                // POST 보내기 전 이벤트
                if (jQuery(this).jqGrid('getGridParam', 'url') === '') return false;
                var chart = jQuery('#container').highcharts();
                if (chart !== undefined) {
                    chart.xAxis[0].setCategories([]);
                    while (chart.series.length) {
                        chart.series[0].remove();
                    }

                }

                var dailychart = jQuery('#dailyContainer').highcharts();
                if (dailychart !== undefined) {
                    while (dailychart.series.length) {
                        dailychart.series[0].remove();
                    }

                }
            },
            gridComplete: function () {

                jQuery('#' + pageID + ' .ui-pg-input').attr('readonly', true);
            }
        };
        var jqOpt = {};

        switch (listID) {
            case 'slaList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [10, 30, 50, 100],        // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    colNames: ['일자', '합계', '적시성', '가용성', '생산성 및 보안관리', '일반관리'],
                    colModel: [
                        {name: 'brief_dt', index: 'brief_dt', width: 1, align: "left", sortable: false},
                        {name: 'slatotal', index: 'slatotal', width: 1, align: "right", sortable: false},
                        {name: 'timeliness', index: 'timeliness', width: 1, align: "right", sortable: false},
                        {name: 'availability', index: 'availability', width: 1, align: "right", sortable: false},
                        {name: 'productivity', index: 'productivity', width: 1, align: "right", sortable: false},
                        {name: 'general', index: 'general', width: 1, align: "right", sortable: false},
                    ],
                    beforeRequest: function () {
                        // POST 보내기 전 이벤트
                        if (jQuery(this).jqGrid('getGridParam', 'url') === '') return false;
                        var chart = jQuery('#container').highcharts();
                        if (chart !== undefined) {
                            chart.xAxis[0].setCategories([]);
                            while (chart.series.length) {
                                chart.series[0].remove();
                            }

                        }

                    },
                    onInitGrid: function () {

                        dataReload(tarID);
                    },
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {

                        infoSetting('sla');

                        jQuery('#summText0').text(jQuery('#slaList').find("tr:last").find("td[aria-describedby='slaList_timeliness']").text());
                        jQuery('#summText1').text(jQuery('#slaList').find("tr:last").find("td[aria-describedby='slaList_availability']").text());
                        jQuery('#summText2').text(jQuery('#slaList').find("tr:last").find("td[aria-describedby='slaList_productivity']").text());
                        jQuery('#summText3').text(jQuery('#slaList').find("tr:last").find("td[aria-describedby='slaList_general']").text());
                        jQuery('#summTotText').text(jQuery('#slaList').find("tr:last").find("td[aria-describedby='slaList_slatotal']").text());

                        resizePanel();

                        var obj = jQuery(this);

                        obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders: [{
                                startColumnName: 'slatotal',
                                numberOfColumns: 5,
                                titleText: '평가 점수'
                            }]
                        });


                    }
                };

                break;
            case 'dailyList':
                jqOpt = {
                    url: '',
                    scroll: 1,
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rowList: [10, 30, 50, 100],        // row 갯수 표출 세팅
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,       // disable pager text like 'Page 0 of 10'
                    colNames: ['일자', '전체', '센터', '인프라', '개발', '상용'],
                    colModel: [
                        {name: 'brief_dt', index: 'brief_dt', width: 2, align: "left", sortable: false},
                        {
                            name: 'daily_tot_percent',
                            index: 'daily_tot_percent',
                            width: 1,
                            align: "right",
                            sortable: false,
                            formatter: 'number',
                            formatoptions: {decimalSeparator: ".", decimalPlaces: 1, defaultValue: '-'}
                        },
                        {
                            name: 'daily_center_percent',
                            index: 'daily_center_percent',
                            width: 1,
                            align: "right",
                            sortable: false,
                            formatter: 'number',
                            formatoptions: {decimalSeparator: ".", decimalPlaces: 1, defaultValue: '-'}
                        },
                        {
                            name: 'daily_infra_percent',
                            index: 'daily_infra_percent',
                            width: 1,
                            align: "right",
                            sortable: false,
                            formatter: 'number',
                            formatoptions: {decimalSeparator: ".", decimalPlaces: 1, defaultValue: '-'}
                        },
                        {
                            name: 'daily_develop_percent',
                            index: 'daily_develop_percent',
                            width: 1,
                            align: "right",
                            sortable: false,
                            formatter: 'number',
                            formatoptions: {decimalSeparator: ".", decimalPlaces: 1, defaultValue: '-'}
                        },
                        {
                            name: 'daily_solution_percent',
                            index: 'daily_solution_percent',
                            width: 1,
                            align: "right",
                            sortable: false,
                            formatter: 'number',
                            formatoptions: {decimalSeparator: ".", decimalPlaces: 1, defaultValue: '-'}
                        }
                    ],
                    beforeRequest: function () {
                        // POST 보내기 전 이벤트
                        if (jQuery(this).jqGrid('getGridParam', 'url') === '') return false;

                        var dailychart = jQuery('#dailyContainer').highcharts();
                        if (dailychart !== undefined) {
                            while (dailychart.series.length) {
                                dailychart.series[0].remove();
                            }

                        }
                    },
                    onInitGrid: function () {

                        dataReload(tarID);
                    },
                    loadError: function (xhr, status, error) {
                        alert(xhr.responseText);
                    },
                    loadComplete: function () {
                        infoSetting('daily');

                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        var obj = jQuery(this);

                        obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders: [{
                                startColumnName: 'daily_tot_percent',
                                numberOfColumns: 5,
                                titleText: '정상항목 비율'
                            }]
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


    return {
        inputCheckScript: inputCheckScript,
        setEvents: formcheck.setEvents,
        dataReload: dataReload,
        infoSetting: infoSetting,
        panelClear: panelClear,
        hiChartSla: hiChartSla,
        resizePanel: resizePanel,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local', 'bootstrap-datepicker.lang', 'bootstrap-datetimepicker', 'moment', 'bootstrap-switchery', 'jquery'], function (common, darkhand, lc, datepicker, datetimepicker, moment, Switchery, jQuery) {
    // 엔터 적용
    function enterCheck(idx) {

        if (idx === undefined) idx = 0;

        var tw = [];

        switch (idx) {
            case 0:

                tw.push({
                    chk: jQuery("#slaPanel :input"),
                    script: function () {

                        var lc = require('local');
                        return lc.inputCheckScript('slaPanel');
                    },
                    ret: "btnSrch",
                    state: function () {

                        var lc = require('local');

                        jQuery.fn.loadingStart();


                        lc.panelClear(false, 'slaPanel', false);


                        lc.dataReload('sla'); // 리포트 목록 갱신

                        jQuery.fn.loadingComplete();
                    }
                });
                tw.push({
                    chk: jQuery("#dailyPanel :input"),
                    script: function () {

                        var lc = require('local');
                        return lc.inputCheckScript('dailyPanel');
                    },
                    ret: "btnSrch2",
                    state: function () {

                        var lc = require('local');

                        jQuery.fn.loadingStart();


                        lc.panelClear(false, 'dailyPanel', false);


                        lc.dataReload('daily'); // 리포트 목록 갱신


                        jQuery.fn.loadingComplete();
                    }
                });
        }

        common.enterSend(tw);
    }

    function dateSetting() {
        var nowTemp = new Date();
        jQuery("#briefDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD',
            showTodayButton: true,
            showClear: true
        });


        jQuery('.input-daterange').datepicker({
            language: 'kr',
            format: 'yyyy-mm-dd',
            todayHighlight: true,
            endDate: new Date(nowTemp.getFullYear(), nowTemp.getMonth(), nowTemp.getDate(), 0, 0, 0, 0),
            todayBtn: "linked"
        }).find('input').each(function () {

            var nowVal = common.nowDate('-');

            switch (this.id) {
                case 'srcSDate':
                    nowVal = common.termDate(nowVal, 'm', -6, '-');
                    break;
                case 'srcSDate2':
                    // TODO : 개발 완료 후 d - 7 로 처리
                    nowVal = common.termDate(nowVal, 'm', -1, '-');
                    break;
            }

            jQuery(this).datepicker('update', nowVal).trigger('changeDate');
        });
    }

    // 운영지수 가져오기
    function OpIndexData() {

        jQuery.ajax({
            url: './getOpIndexData',
            type: 'POST',
            dataType: 'json',
            async: false,
            contentType: 'application/json; charset=utf-8',
            success: function (data) {

                var val = parseInt(data.operateIndex);
                jQuery('.operate strong').after('<span> ( ' + (data.brief_dt).substring(0, 4) + '년 ' + (data.brief_dt).substring(4, 6) + '월 ' + (data.brief_dt).substring(6, 8) + '일' + ' 기준 )</span>');

                if(val != -1) {

                    jQuery('#index' + (val < 10 ? val + 1 : val)).text(data.operateIndex);
                }
            },
            error: function (request, status, error) {

                console.log('code: ' + request.status + '\n' + 'message: ' + request.responseText + '\n' + 'error: ' + error);
            }
        });
    }


// 페이지 로딩 완료 후 이벤트
    jQuery(function () {



        // 권한에 따른 버튼 비활성화
        if (authCrud.READ_FL === 'N') {

            jQuery('#btnSrch').attr('disabled', true);
            jQuery('#btnSrch2').attr('disabled', true);
        }


        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        dateSetting();
        OpIndexData();


        // 그리드 초기화
        lc.gridSetting('sla'); // sla 리스트 목록
        lc.gridSetting('daily'); // daily 리스트 목록
        lc.hiChartSla('sla');
        lc.hiChartSla('daily');


    });

// 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize', function () {

        lc.resizePanel();

    }).trigger('resize');
})
;