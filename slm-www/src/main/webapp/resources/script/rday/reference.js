/**
 * User: 이종혁
 * Date: 2016.04.29
 * Time: 오전 11:17
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';


    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel() {


        var arrObj = [
            { list: "rdayReferenceList", panel: "rdayReference" },
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
            rowNum: 61,                 // 처음에 로드될 때 표출되는 row 수
            viewsortcols: [true, 'vertical', true], // 소팅 인자 세팅
            rownumbers: true,           // Grid의 RowNumber 표출
            viewrecords: true,          // 우측 View 1-4 Text 표출 부분
            gridview: true,             // Grid Alert
            autowidth: true,            // width 자동 맞춤
            shrinkToFit: true,          // width에 맞춰 Cell Width 자동 설정
            height: '100%',                // 세로 크기
            caption: '',
            // 캡션 명(없으면 표출 안됨)
            beforeRequest : function() {
                // POST 보내기 전 이벤트
                if (jQuery(this).jqGrid('getGridParam', 'url') === '') return false;
            },
            gridComplete: function () {

                jQuery('#' + tarID + ' .ui-pg-input').attr('readonly', true);
            }
        };
        var jqOpt = {};

        var nullFormatter = function(cellvalue, options, rowObject) {
            if(cellvalue === undefined ||  cellvalue == null || cellvalue =='') {
                cellvalue = '-';
            }
            return cellvalue;
        }


        var prevCellVal = { cellId: undefined, value: undefined };

        switch(listID) {
            case 'rdayReferenceList' :
                // 사용자 목록
                jqOpt = {
                    url : './getRdayReferenceList',
                    rowNum: -1,          // 처음에 로드될 때 표출되는 row 수
                    rownumbers: false,  // Grid의 RowNumber 표출
                    viewrecords: false, // 우측 View 1-4 Text 표출 부분
                    recordtext: '',      //
                    scrollOffset:0,
                    pgbuttons: false,   // disable page control like next, back button
                    pgtext: null,
                    // disable pager text like 'Page 0 of 10'
                    //colNames: ['영역', '서비스 측정지표', '지표구분', '가중치', '최대기대수준', '최소기대수준',''],
                    colModel: [
                        { name: 'grp_nm' , index:'grp_nm',key: true ,width: 2, align: "center",hidden:true},
                        { label: '점검항목' ,name: 'basis_nm' , width: 2, align: "center",sortable: false},
                        { label: '양호' ,name: 'good' , width: 3, align: "center",sortable: false},
                        {
                            label: '주의' ,name: 'warning'  ,width: 3, align: "center",sortable: false,
                            editable: true, formatter: nullFormatter
                        },
                        { label: '이상' ,name: 'wrong' , width: 3, align: "center",sortable: false },
                        { label: '점검방법' ,name: 'cont' , width: 4, align: "center" ,sortable: false},
                    ],
                    grouping: true,
                    groupingView: {
                        groupField: ["grp_nm"],
                        groupColumnShow: [false],
                        groupText: ["<b>{0}</b>"],
                        groupOrder: ["asc"],
                        groupSummary: [false],
                        groupCollapse: false

                    },
                    loadComplete: function () {

                        resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                        var grid = this;

                        jQuery(this).jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                            useColSpanStyle: true,
                            groupHeaders:[
                                {startColumnName: 'good', numberOfColumns: 2, titleText: '정상'},
                            ]
                        });

                        jQuery.fn.loadingComplete();
                    }
                };

                break;

        }



        dataGrid.jqGrid(jQuery.extend(true, jqDefault, jqOpt));

        return false;
    }

    return {
        setEvents: formcheck.setEvents,
        resizePanel: resizePanel,
        gridSetting: gridSetting
    }
});

require([ 'darkhand', 'local', 'jquery'], function ( darkhand, lc, jQuery) {



    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {

        // 그리드 초기화
        lc.gridSetting('rdayReference'); // 점검기준표 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel();

    }).trigger('resize');
});