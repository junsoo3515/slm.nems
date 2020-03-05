/**
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
define('local', ['common', 'formcheck', 'jqGrid.setting','jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'infoPanel':
                return formcheck.checkForm(tarID, 0);
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

            case 'workList' :

                jqOpt = {
                    url: './getRdayWorkList'
                };
                // 일일작업내역 목록
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

        var regBtn = jQuery('#btnReg');

        if (rowID) {
            // 리스트 폼에 있는 객체 입력 폼 Setting

            if(authCrud.MOD_FL === 'Y') {

                jQuery('#btnReg, #btnCancel').attr('disabled', false);
                regBtn.html(regBtn.html().replace("등록", "수정"));
            }
            if(authCrud.DEL_FL === 'Y') {

                jQuery('#btnDel').attr('disabled', false);
            }

            jQuery.when(
                jQuery.ajax({
                    url: './getRdayWorkData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        rec_seq: obj.rec_seq
                    })
                })
            ).then(function(data) {
                // 그리드의 값을 가지고 입력 값 Setting

                    common.setValues({
                        wRec_seq : data.rec_seq,
                        wTitle : data.title,
                        wOp_issue: data.op_issue,
                        wReq_issue: data.req_issue,
                        wPolicy_issue: data.policy_issue
                    });


                    jQuery('#occurDateTimePicker').data('DateTimePicker').date(new Date(data.occur_dt));

            }).done(function() {

                    if(authCrud.DEL_FL === 'Y') {

                        jQuery('#btnDel').attr('disabled', false);
                    }

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

            var rowid = jQuery("#workList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#workList").jqGrid("resetSelection"); // Grid Select Reset 처리
            }

            return false;
        }

        switch(objID) {

            case 'infoPanel':
                // 기본정보 입력폼
                common.clearElement('#' + objID); // form element

                var regBtn = jQuery('#btnReg');

                jQuery('#btnReg, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
                jQuery('#btnDel').attr('disabled',true);
                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경
                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#workList").jqGrid('getGridParam', 'selrow');

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
            { k: 'wRec_seq', v: 'rec_seq' },
            { k: 'wTitle', v: 'title' },
            { k: 'wOccur_dt', v: 'occur_dt' },
            { k: 'wOp_issue', v: 'op_issue' },
            { k: 'wReq_issue', v: 'req_issue' },
            { k: 'wPolicy_issue', v: 'policy_issue' }
        ]);



        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setWorkAct',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data > 0) {

                common.setOSXModal('일일작업내역 정보가 성공적으로 저장하였습니다.');

                var obj = jQuery("#workList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');
                if (rowid !== null) {
                    panelClear(true);
                    obj.trigger("reloadGrid");
                } else {
                    // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                    panelClear(true);
                    dataReload('work');
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
    function dataSendDel() {
        // 로딩 시작
        jQuery.fn.loadingStart();

        var rowid = jQuery("#workList").jqGrid('getGridParam', 'selrow');

        if (rowid === null) {
            // 삭제 모드
            if (authCrud.DEL_FL === 'N') return false;
        }

        var formData = jQuery('#infoPanel :input');
        var reqData = formData.serializeObject();

        // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

        reqData = common.changeKeys(reqData, [
            { k: 'wRec_seq', v: 'rec_seq' },
        ]);


        // 데이터 전송
        jQuery.when(

            jQuery.ajax({
                url: './setWorkDel',
                type: "POST",
                dataType: "json",
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify(reqData)
            })
        )
        .then(function(data) {
            // 결과에 따라 다음 이벤트 처리
            if (data > 0) {

                common.setOSXModal('일일작업내역 정보가 성공적으로 삭제하였습니다.');

                var obj = jQuery("#workList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {
                    obj.jqGrid('delRowData', rowid);
                    obj.trigger("reloadGrid");
                    panelClear(true);
                    dataReload('work');
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
    function resizePanel() {

        var arrObj = [
            { list: "workList", panel: "workPanel" }
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
            height: '500',                // 세로 크기
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


        jqOpt = {
            url : '',
            scroll: 1,
            height: 747,
            rowList: [10, 30, 50, 100],
            colNames: ['','제목', '발생일자'],
            colModel: [
                {name: 'rec_seq', index: 'rec_seq', hidden: true},
                {name: 'title', index: 'title', width: 2,align: "center",sortable: false},
                {name: 'occur_dt', index: 'occur_dt', width: 2,align: "center",sortable: false}
            ],
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
            loadError:function(xhr, status, error){
                alert(xhr.responseText);
            },
            loadComplete: function () {

                resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                jQuery.fn.loadingComplete();
            }
        };


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
        panelClear : panelClear,
        dataSend: dataSend,
        dataSendDel: dataSendDel,
        resizePanel: resizePanel,
        gridSetting: gridSetting
    }
});

require(['common', 'darkhand', 'local','bootstrap-datepicker.lang','bootstrap-datetimepicker', 'jquery'], function (common, darkhand, lc,datepicker, datetimepicker, jQuery) {
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

                        jQuery('#occurDateTimePicker').data('DateTimePicker').date(new Date());

                        lc.panelClear(true); // 전체 폼 초기화
                        lc.dataReload('work'); // 일일작업내역 목록
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
                            lc.dataSend();
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
                            var result = confirm('선택 된 작업내역을 삭제 하시겠습니까?');
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

    function dateSetting() {
        var nowTemp = new Date();
        jQuery("#occurDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD',
            showTodayButton: true,
            showClear: true,
        });

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
            }

            jQuery(this).datepicker('update', nowVal).trigger('changeDate');
        });
    }

    // 페이지 로딩 완료 후 이벤트
    jQuery(function () {
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

        // 취소 버튼 클릭 시
        jQuery('#btnCancel').on('click', function () {

            jQuery('#occurDateTimePicker').data('DateTimePicker').date(new Date());

            lc.panelClear(true);
        });

        // 그리드 초기화
        lc.gridSetting('work'); // 일일작업내역 목록
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel();

    }).trigger('resize');
});