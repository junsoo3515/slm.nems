/**
 * User: 이종혁
 * Date: 2016.05.03
 * Time: 오후 3:20
 */
define('local', ['common', 'formcheck', 'jqGrid.setting','jquery','bootstrap-switchery' ,'jqGrid'], function (common, formcheck, jqFn, jQuery, Switchery) {


    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        switch (tarID) {
            case 'measureInfoPanel':

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

        switch (listID) {

            case 'disorderList' :
                // 장애처리 목록

                jqOpt = {
                    url: './getRdayTroubleShootList'
                };

                filterData = jQuery('#srcPanel :input').serializeObject();
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        search: true,
                        postData: {
                            filters: JSON.stringify(filterData)
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page: 1}]);
                break;

            case 'measureGridList' :
                // 장애처리조치사항 목록


                jqOpt = {
                    url: './getRdayTroubleShootMeasureList'
                };

                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            cause_seq: jQuery("#disorderList").jqGrid('getRowData', jQuery("#disorderList").jqGrid('getGridParam', 'selrow')).dis_seq
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page: 1}]);
                break;

            case 'workerGridList' :
                jqOpt = {
                    url: './getRdayTroubleShootWorkerList'
                };
                dataGrid
                    .jqGrid("setGridParam", jQuery.extend(true, {
                        postData: {
                            dis_seq: jQuery("#disorderList").jqGrid('getRowData', jQuery("#disorderList").jqGrid('getGridParam', 'selrow')).dis_seq
                        }
                    }, jqOpt))
                    .trigger("reloadGrid", [{page: 1}]);
                break;

        }


    }

    // 작업자 정보 이름 select2(ajax)
    function workerNameSelect2(pVal) {
        if (pVal === undefined) {
            pVal = '';
        }

        return {
            url: './getWorkerNameSelect2',
            type: 'GET',
            dataType: 'json',
            contentType: 'application/json; charset=utf-8',
            delay: 250,
            data: JSON.stringify(function (params) {
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

    // 작업자 정보 이름 선택 시 세부정보 가져오기
    function workerInfoSetting(mem_id) {
        var infoData;
        jQuery.ajax({
            url: './getWorkInfoSetting',
            type: 'POST',
            dataType: 'json',
            async: false,
            contentType: 'application/json; charset=utf-8',
            data: mem_id,
            success: function (data) {
                infoData = data;
            },
            error: function () {
                alert('에러');
            }
        });
        return infoData;
    }


    // 기본정보 폼 Setting
    function infoSetting(rowID, obj, str) {

        switch (str) {

            case 'disorder' :

                var regBtn = jQuery('#btnReg');

                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    if (authCrud.REG_FL === 'Y') {
                        jQuery('#btnAdd').attr('disabled', false);
                    }
                    if (authCrud.MOD_FL === 'Y') {

                        jQuery('#btnReg, #btnCancel').attr('disabled', false);
                        regBtn.html(regBtn.html().replace("등록", "수정"));
                    }
                    if (authCrud.DEL_FL === 'Y') {

                        jQuery('#btnDel').attr('disabled', false);
                    }

                    jQuery.when(
                        jQuery.ajax({
                            url: './getRdayTroubleShootData',
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify({
                                dis_seq: obj.dis_seq
                            })
                        })
                    ).then(function (data) {
                            common.setValues({
                                wDis_seq: obj.dis_seq,
                                disorderGroup: data.disorder_type,
                                equipTypeGroup: data.equip_type,
                                workTypeGroup: data.work_type,
                                workStateGroup: data.work_state,
                                meaTypeGroup: data.mea_type,
                                wCont: data.cont,
                                wSumm_cont: data.summ_cont
                            });
                            jQuery('#occurDateTimePicker').data('DateTimePicker').date(new Date(data.occur_dt));
                            jQuery('#meaPlanDateTimePicker').data('DateTimePicker').date(new Date(data.mea_plan_dt));
                            jQuery('#meaFinDateTimePicker').data('DateTimePicker').date(new Date(data.mea_fin_dt));
                            jQuery('#svStopDateTimePicker').data('DateTimePicker').date(new Date(data.sv_stop_dt));
                            jQuery('#svStartDateTimePicker').data('DateTimePicker').date(new Date(data.sv_start_dt));


                            return data;

                        })
                        .then(function (data) {


                            changeSwitchery('#wFin_fl',data.fin_fl === 'Y' ? true : false);
                            changeSwitchery('#wNature_fl',data.nature_fl === 'Y' ? true : false);
                            changeSwitchery('#wErr_fl',data.err_fl === 'Y' ? true : false);
                        })
                        .fail(common.ajaxError)
                        .always(function () {

                            return false;
                        });


                }

                break;

            case 'measure' :

                if (rowID) {
                    // 리스트 폼에 있는 객체 입력 폼 Setting

                    if (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') {

                        jQuery('#btnReg2, #btnCancel2').attr('disabled', false);

                    }
                    jQuery.when(
                        jQuery.ajax({
                            url: './getFileName',
                            type: 'POST',
                            contentType: 'application/json; charset=utf-8',
                            data: JSON.stringify({
                                real_seq: parseInt(obj.mea_seq)
                            })
                        })
                    ).then(function (data) {
                            common.setValues({
                                wMeasureCont: obj.measure_cont,
                                wMea_seq: obj.mea_seq
                            });
                            jQuery('#wbFileName').append(data.wbFileName);

                            jQuery('#waFileName').append(data.waFileName);


                            return data;
                        }
                    )
                        .
                        fail(common.ajaxError)
                        .always(function () {

                            return false;
                        });
                jQuery('#measureDateTimePicker').data('DateTimePicker').date(new Date(obj.mea_dt));
        }

        break;

    }


}

// 패널 초기화
function panelClear(isAll, objID, isListReset) {

    if (isAll === undefined) isAll = false; // 전체 reset 여부
    if (objID === undefined) objID = ''; // panelID
    if (isListReset === undefined) isListReset = true; // Master List reset 여부

    if (isAll) {
        // 모든 패널 초기화
        panelClear(false, 'infoPanel', false);

        var rowid = jQuery("#disorderList").jqGrid('getGridParam', 'selrow');

        if (rowid !== null && isListReset) {

            jQuery('#measuretab').prop('disabled', true);

            jQuery("#disorderList").jqGrid("resetSelection"); // Grid Select Reset 처리

        }

        return false;
    }

    switch (objID) {

        case 'infoPanel':
            // 장애처리 입력폼

            common.clearElement('#' + objID); // form element

            var regBtn = jQuery('#btnReg');

            jQuery('#btnReg, #btnCancel').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
            jQuery('#btnDel').attr('disabled', true);
            jQuery('#btnAdd').attr('disabled', true);
            regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경

            jQuery("select[name='equipGroup'] option").remove();
            common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []);

            changeSwitchery('#wFin_fl',false);
            changeSwitchery('#wNature_fl',false);
            changeSwitchery('#wErr_fl',false);

            var rowid = jQuery('#workerGridList').jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery('#workerGridList').jqGrid('resetSelection'); // Grid Select Reset 처리
            }

            break;
        case 'measureInfoPanel':
            // 조치사항 입력폼

            common.clearElement('#' + objID); // form element

            jQuery('#btnReg2, #btnCancel2').attr('disabled', (authCrud.REG_FL === 'Y' || authCrud.DEL_FL === 'Y') ? false : true); // 등록, 취소버튼 Style 변경
            jQuery('#wbFileName').empty();
            jQuery('#waFileName').empty();

            var rowid = jQuery("#measureGridList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#measureGridList").jqGrid("resetSelection"); // Grid Select Reset 처리
            }

            break;
    }
}

// 기본정보 등록/수정 이벤트
function dataSend(str) {

    switch (str) {

        case 'disorder':
            // 로딩 시작
            jQuery.fn.loadingStart();

            var rowid = jQuery("#disorderList").jqGrid('getGridParam', 'selrow');

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
                {k: 'wDis_seq', v: 'dis_seq'},
                {k: 'disorderGroup', v: 'disorder_type'},
                {k: 'equipTypeGroup', v: 'equip_type'},
                {k: 'equipGroup', v: 'eqp_cd'},
                {k: 'workTypeGroup', v: 'work_type'},
                {k: 'workStateGroup', v: 'work_state'},
                {k: 'meaTypeGroup', v: 'mea_type'},
                {k: 'wOccur_dt', v: 'occur_dt'},
                {k: 'wMea_plan_dt', v: 'mea_plan_dt'},
                {k: 'wMea_fin_dt', v: 'mea_fin_dt'},
                {k: 'wCont', v: 'cont'},
                {k: 'wSumm_cont', v: 'summ_cont'},
                {k: 'wSv_stop_dt', v: 'sv_stop_dt'},
                {k: 'wSv_start_dt', v: 'sv_start_dt'}
            ]);
            reqData.fin_fl = jQuery("input:checkbox[id='wFin_fl']").is(":checked") === true ? 'Y' : 'N';
            reqData.nature_fl = jQuery("input:checkbox[id='wNature_fl']").is(":checked") === true ? 'Y' : 'N';
            reqData.err_fl = jQuery("input:checkbox[id='wErr_fl']").is(":checked") === true ? 'Y' : 'N';


            // 데이터 전송
            jQuery.when(
                jQuery.ajax({
                    url: './setTroubleShootAct',
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


                        var obj = jQuery("#disorderList");
                        var rowid = obj.jqGrid('getGridParam', 'selrow');
                        if (rowid !== null) {
                            panelClear(true);
                            obj.trigger("reloadGrid");
                            if (jQuery("select[name='equipTypeGroup'] option").val() === '') {
                                jQuery("select[name='equipGroup'] option").remove();
                                common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []); // 장비
                            }


                        } else {
                            // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                            panelClear(true);
                            dataReload('disorder');
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
        case 'measure':
            // 로딩 시작
            jQuery.fn.loadingStart();

            var rowid = jQuery("#measureGridList").jqGrid('getGridParam', 'selrow');

            if (rowid === null) {
                // 등록 모드
                if (authCrud.REG_FL === 'N') return false;
            } else {
                // 수정 모드
                if (authCrud.MOD_FL === 'N') return false;
            }

            var formData = jQuery('#measureInfoPanel :input');
            var reqData = formData.serializeObject();

            // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

            reqData = common.changeKeys(reqData, [
                {k: 'wMea_seq', v: 'mea_seq'},
                {k: 'wMeasure_dt', v: 'mea_dt'},
                {k: 'wMeasureCont', v: 'measure_cont'}
            ]);
            reqData.cause_seq = jQuery("#disorderList").jqGrid('getRowData', jQuery("#disorderList").jqGrid('getGridParam', 'selrow')).dis_seq;


            // 데이터 전송
            jQuery.when(
                jQuery.ajax({
                    url: './setTroubleShootMeasureAct',
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

                        var obj = jQuery("#disorderList");
                        var rowid = obj.jqGrid('getGridParam', 'selrow');
                        var mea_seq = jQuery('#measureGridList').jqGrid('getRowData', jQuery('#measureGridList').jqGrid('getGridParam','selrow')).mea_seq;
                        var max_mea_seq;

                        jQuery.ajax({
                            url: './getMaxMeaSeq',
                            type: 'POST',
                            dataType: 'json',
                            async: false,
                            contentType: 'application/json; charset=utf-8',
                            success: function (data) {
                                max_mea_seq = data;
                            },
                            error: function () {
                                alert('에러');
                            }
                        });

                        // 첨부파일 존재 시 이벤트
                        if (mea_seq !== undefined && jQuery('#wbFileName').text() !== '' && jQuery('#wBeforeFiles').val() !== '') {

                            jQuery.ajax({
                                url: './setFileDel',
                                type: 'POST',
                                dataType: 'json',
                                contentType: 'application/json; charset=utf-8',
                                data: JSON.stringify({
                                    gubun_cd: 'SB',
                                    real_seq: mea_seq
                                })
                            });
                        }
                            // 첨부파일 저장 Process(비동기 처리)
                            var formData = new FormData(); // HTML5 지원 되는 브라우저부터 지원

                            formData.append('rootPath', jQuery.fn.sysUrl);
                            formData.append('systemPath', 'slm');
                            formData.append('folPath', 'rday');
                            formData.append('tableKey', 'SLM_DISORDER');
                            formData.append('gubunKey', 'SB');
                            formData.append('seq', parseInt(max_mea_seq));
                            formData.append("files", jQuery('input[name=wBeforeFiles]')[0].files[0]);

                            jQuery.ajax({
                                url: jQuery.fn.sysUrl + '/files/singleUpload',
                                type: "POST",
                                processData: false,
                                contentType: false,
                                data: formData,
                                success: function (data) {

                                    if (rowid !== null) {
                                        // 수정모드일 때 성능향상을 고려하여 그리드에 바로 데이터 갱신 처리(비동기 처리라서 이 부분 다시 적용)
                                        obj.jqGrid('setRowData', rowid, {bFiles_seq: data});
                                    }
                                },
                                error: function (jqXhr, textStatus, errorThrown) {
                                    //통신 에러 발생시 처리
                                    console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");
                                    common.setOSXModal('첨부한 사진이 저장에 실패하셨습니다.');
                                }
                            });
                        // 첨부파일 존재 시 이벤트
                        if (mea_seq !== undefined && jQuery('#waFileName').text() !== '' && jQuery('#wAfterFiles').val() !== '') {
                            jQuery.ajax({
                                url: './setFileDel',
                                type: 'POST',
                                dataType: 'json',
                                contentType: 'application/json; charset=utf-8',
                                data: JSON.stringify({
                                    gubun_cd: 'SA',
                                    real_seq: mea_seq
                                })
                            });
                        }
                            // 첨부파일 저장 Process(비동기 처리)
                            var formData = new FormData(); // HTML5 지원 되는 브라우저부터 지원

                            formData.append('rootPath', jQuery.fn.sysUrl);
                            formData.append('systemPath', 'slm');
                            formData.append('folPath', 'rday');
                            formData.append('tableKey', 'SLM_DISORDER');
                            formData.append('gubunKey', 'SA');
                            formData.append('seq', parseInt(max_mea_seq));
                            formData.append("files", jQuery('input[name=wAfterFiles]')[0].files[0]);

                            jQuery.ajax({
                                url: jQuery.fn.sysUrl + '/files/singleUpload',
                                type: "POST",
                                processData: false,
                                contentType: false,
                                data: formData,
                                success: function (data) {

                                    if (rowid !== null) {
                                        // 수정모드일 때 성능향상을 고려하여 그리드에 바로 데이터 갱신 처리(비동기 처리라서 이 부분 다시 적용)
                                        obj.jqGrid('setRowData', rowid, {aFiles_seq: data});
                                    }
                                },
                                error: function (jqXhr, textStatus, errorThrown) {
                                    //통신 에러 발생시 처리
                                    console.log("Error '" + jqXhr.status + "' (textStatus: '" + textStatus + "', errorThrown: '" + errorThrown + "')");
                                    common.setOSXModal('첨부한 사진이 저장에 실패하셨습니다.');
                                }
                            });

                        var obj = jQuery("#measureGridList");
                        var rowid = obj.jqGrid('getGridParam', 'selrow');

                        if (rowid !== null) {
                            panelClear(false, 'measureInfoPanel', false)
                            obj.trigger("reloadGrid");

                        } else {
                            // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                            panelClear(false, 'measureInfoPanel', false);
                            dataReload('measureGrid');
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

    var rowid = jQuery("#disorderList").jqGrid('getGridParam', 'selrow');

    if (rowid === null) {
        // 삭제 모드
        if (authCrud.DEL_FL === 'N') return false;
    }

    var formData = jQuery('#infoPanel :input');
    var reqData = formData.serializeObject();

    // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

    reqData = common.changeKeys(reqData, [
        {k: 'wDis_seq', v: 'dis_seq'},
    ]);



    // 데이터 전송
    jQuery.when(
        jQuery.ajax({
            url: './setTroubleShootDel',
            type: "POST",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            data: JSON.stringify(reqData)
        })
    )
        .then(function (data) {
            // 결과에 따라 다음 이벤트 처리
            if (data > 0) {

                common.setOSXModal('성공적으로 삭제하였습니다.');

                var obj = jQuery("#disorderList");
                var rowid = obj.jqGrid('getGridParam', 'selrow');

                if (rowid !== null) {
                    obj.jqGrid('delRowData', rowid);
                    obj.trigger("reloadGrid");
                    panelClear(true);
                    dataReload('disorder');
                }
            } else {

                common.setOSXModal('삭제가 실패하였습니다.');
            }
        })
        .fail(common.ajaxError)
        .always(function () {

            jQuery.fn.loadingComplete();
            return false;
        });
    return false;
}

// 레이아웃 변경 시 사이즈 조절 리턴 함수
function resizePanel() {

    var arrObj = [
        {list: "disorderList", panel: "disorderPanel"},
        {list: "measureGridList", panel: "measureGridPanel"}
    ];


    jQuery.each(arrObj, function (sIdx, data) {

        jQuery("#" + data["list"]).jqGrid('setGridWidth', jQuery("#" + data["panel"]).width());
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
        pager: pageID,
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
        beforeRequest: function () {
            // POST 보내기 전 이벤트
            if (jQuery(this).jqGrid('getGridParam', 'url') === '') return false;
        },
        gridComplete: function () {

            jQuery('#' + pageID + ' .ui-pg-input').attr('readonly', true);
        }
    };
    var jqOpt = {};

    switch (listID) {
        case 'disorderList':
            jqOpt = {
                url: '',
                scroll: 1,
                rowList: [10, 30, 50, 100],
                colNames: ['장애유형', '장비유형', '장비', '장애완료 여부', '발생일', '서비스 정지일', '서비스 시작일', '조치 예정일', '조치 완료일', ''],
                colModel: [
                    {
                        name: 'disorder_type_nm',
                        index: 'disorder_type_nm',
                        width: 2,
                        align: 'center',
                        sortable: false
                    },
                    {name: 'equip_type_nm', index: 'equip_type_nm', width: 2, align: "left", sortable: false},
                    {name: 'eqp_nm', index: 'eqp_nm', width: 2, align: "left", sortable: false},
                    {name: 'fin_nm', index: 'fin_nm', width: 2, align: 'center', sortable: false},
                    {
                        name: 'occur_dt',
                        index: 'occur_dt',
                        width: 2,
                        align: 'center',
                        sortable: true,
                        editable: true,
                        formatter: 'date',
                        formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                        editrules: {required: true}
                    },
                    {
                        name: 'sv_stop_dt',
                        index: 'sv_stop_dt',
                        width: 2,
                        align: 'center',
                        sortable: true,
                        editable: true,
                        formatter: 'date',
                        formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                        editrules: {required: true}
                    },
                    {
                        name: 'sv_start_dt',
                        index: 'sv_start_dt',
                        width: 2,
                        align: 'center',
                        sortable: true,
                        editable: true,
                        formatter: 'date',
                        formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                        editrules: {required: true}
                    },
                    {
                        name: 'mea_plan_dt',
                        index: 'mea_plan_dt',
                        width: 2,
                        align: 'center',
                        sortable: true,
                        editable: true,
                        formatter: 'date',
                        formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                        editrules: {required: true}
                    },
                    {
                        name: 'mea_fin_dt',
                        index: 'mea_fin_dt',
                        width: 2,
                        align: 'center',
                        sortable: true,
                        editable: true,
                        formatter: 'date',
                        formatoptions: {srcformat: 'ISO8601Short', newformat: 'Y.m.d',},
                        editrules: {required: true}
                    },

                    {name: 'dis_seq', index: 'dis_seq', hidden: true}
                ],
                onInitGrid: function () {

                    dataReload(tarID);
                },
                onSelectRow: function (id) {

                    jQuery('.nav-tabs a:first').tab('show')
                    jQuery.when(
                        // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                        panelClear(true, '', false)
                    ).then(function (res) {
                            // 기본정보 입력폼 Setting
                            var ret = dataGrid.jqGrid('getRowData', id);

                            infoSetting(id, ret, 'disorder');
                            dataReload('workerGrid');
                        }).always(function () {

                            return false;
                        });
                    jQuery('#measuretab').prop('disabled', false);
                },
                loadError: function (xhr, status, error) {
                    alert(xhr.responseText);
                },
                loadComplete: function () {

                    resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용

                    jQuery.fn.loadingComplete();
                }
            };

            break;
        case 'measureGridList':
            jqOpt = {
                url: '',
                scroll: 1,
                rowList: [10, 30, 50, 100],
                height: 266,
                postData: {
                    cause_seq: jQuery("#disorderList").jqGrid('getRowData', jQuery("#disorderList").jqGrid('getGridParam', 'selrow')).dis_seq
                },
                colNames: ['조치일자', '조치내용', '삭제', ''],
                colModel: [
                    {name: 'mea_dt', index: 'mea_dt', width: 2, align: 'center', sortable: false},
                    {
                        name: 'measure_cont',
                        index: 'measure_cont',
                        classes: 'ellipseJqGridCell',
                        resizable: true,
                        width: 3,
                        sortable: false
                    },
                    {
                        name: 'actions', width: 1, sortable: false, formatter: 'actions', formatoptions: {
                        keys: true,
                        editbutton: false,
                        delbutton: (authCrud.DEL_FL === "N" ? false : true),
                        delOptions: {
                            url: './setTroubleShootMeasureDel',
                            mtype: 'POST',
                            ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                            serializeDelData: function () {

                                var formData = jQuery('#measureInfoPanel :input');
                                var reqData = formData.serializeObject();
                                var gubun_cd = '';

                                if(jQuery('#wbFileName').text() !== '')
                                gubun_cd = 'SB';

                                if(jQuery('#waFileName').text() !== '')
                                gubun_cd = 'SA';

                                if(jQuery('#waFileName').text() !== '' && jQuery('#wbFileName').text() !== '')
                                gubun_cd = 'SASB';




                                reqData = common.changeKeys(reqData, [
                                    {k: 'wMea_seq', v: 'mea_seq'},
                                ]);
                                return JSON.stringify({
                                    mea_seq: reqData.mea_seq,
                                    real_seq: jQuery('#measureGridList').jqGrid('getRowData',jQuery('#measureGridList').jqGrid('getGridParam','selrow')).mea_seq,
                                    gubun_cd: gubun_cd
                                });
                            },
                            reloadAfterSubmit: false,
                            afterComplete: function (res) {

                                common.setOSXModal((res.responseText > 0 ? '성공적으로 삭제되었습니다.' : '삭제에 실패하였습니다.'));
                                dataReload(tarID);
                                panelClear(false, 'measureInfoPanel', false);
                            }
                        }

                    }
                    },
                    {name: 'mea_seq', index: 'mea_seq', hidden: true}
                ],
                onSelectRow: function (id) {

                    jQuery.when(
                        // 하단 탭 관련 전체 초기화(그리드 리셋 제외하고)
                        panelClear(false, 'measureInfoPanel', false)
                    ).then(function (res) {
                            // 기본정보 입력폼 Setting
                            var ret = dataGrid.jqGrid('getRowData', id);

                            infoSetting(id, ret, 'measure');
                        }).always(function () {

                            return false;
                        });
                },
                loadError: function (xhr, status, error) {
                    alert(xhr.responseText);
                },
                loadComplete: function () {
                    resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용
                    jQuery.fn.loadingComplete();
                }
            };

            break;

        case 'workerGridList' :
            jqOpt = {
                url: '',
                editurl: './setWorkerGridAct',
                scroll: 1,
                scrollrows:true,
                rowList: [10, 30, 50, 100],
                height: 266,
                colNames: ['', '', '', '', '', '이름', '업체명', '연락처', 'E-Mail', '관리'],
                colModel: [
                    {name: 'dis_seq', index: 'dis_seq', hidden: true, editable: true},
                    {name: 'work_mem_id', index: 'work_mem_id', hidden: true, editable: true},
                    {name: 'comp_nm1', index: 'comp_nm1', hidden: true, editable: true},
                    {name: 'tel1', index: 'tel1', hidden: true, editable: true},
                    {name: 'email1', index: 'email1', hidden: true, editable: true},
                    {
                        name: 'nm',
                        index: 'nm',
                        width: 1,
                        sortable: false,
                        editable: true,
                        editrules: {required: true},
                        edittype: 'select',
                        editoptions: {
                            dataInit: function (el) {
                                var obj = jQuery(el);
                                var rowid = obj.attr('rowid');
                                var kObj = jQuery('#' + rowid + '_nm');

                                obj.select2({
                                    minimumInputLength: 0, // 최소 검색어 개수
                                    ajax: workerNameSelect2()
                                });

                                // DB 값 setting 하기 위한 옵션
                                obj
                                    .append(new Option(obj.parent('td').attr('title'), kObj.val(), true, true))
                                    .val(kObj.val())
                                    .trigger('change');
                            },
                            value: {}
                        }
                    },
                    {name: 'comp_nm', index: 'comp_nm', width: 3, edittype:'text',sortable: false, editable: true},
                    {name: 'tel', index: 'tel', width: 2, align: 'center', sortable: false, editable: false},
                    {name: 'email', index: 'email', width: 2, align: 'center', sortable: false, editable: false},
                    {
                        name: 'myac',
                        width: 1,
                        sortable: false,
                        classes: 'text-center',
                        formatter: 'actions',
                        formatoptions: {
                            editbutton: (authCrud.MOD_FL === "N" ? false : true),
                            delbutton: (authCrud.DEL_FL === "N" ? false : true),
                            onEdit: function (rowid) {
                                // 수정 버튼 클릭 시 Event
                                lastSel = jQuery.jgrid.jqID(rowid);
                                jQuery('#' + rowid + '_nm').on('select2:select', function () {
                                    var infoData = workerInfoSetting(jQuery('#' + rowid + '_nm').val());


                                    jQuery('#workerGridList').setRowData(rowid, {
                                        tel: infoData.tel_hp,
                                        email: infoData.email
                                    });

                                });
                            },
                            afterRestore: function (rowid) {
                                // 취소 버튼 클릭 시 Event
                            },
                            onSuccess: function (res) {
                                // 저장 후 리턴 결과
                                gridResAction(jQuery.parseJSON(res.responseText), 'workerGrid');
                            },
                            restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                            delOptions: {
                                url: './setWorkerGridDel',
                                mtype: 'POST',
                                ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                serializeDelData: function () {

                                    var reqData = dataGrid.jqGrid('getRowData', lastSel);
                                    return JSON.stringify({
                                        work_seq: reqData.work_seq,
                                        dis_seq: reqData.dis_seq,
                                        work_mem_id: reqData.work_mem_id

                                    });
                                },
                                afterComplete: function (res) {
                                    dataReload('workerGrid');
                                    gridResAction(jQuery.parseJSON(res.responseText), 'workerGrid');
                                }
                            }
                        }
                    }
                ],
                onSelectRow: function (id) {
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
                loadError: function (xhr, status, error) {
                    alert(xhr.responseText);
                },
                loadComplete: function () {
                    resizePanel(); // 브라우저 창 크기 변경 시 grid 크기 자동 적용
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
    function changeSwitchery(element, checked){
        var switchery;
        var defaults = {
            color             : '#64bd63'
            , secondaryColor    : '#dfdfdf'
            , jackColor         : '#fff'
            , jackSecondaryColor: null
            , className         : 'switchery'
            , disabled          : false
            , disabledOpacity   : 0.5
            , speed             : '0.1s'
            , size              : 'default'
        };
        var elem = document.querySelector(element);
        elem.checked = checked;
        if(jQuery('#disorderList').jqGrid('getGridParam','selrow') == null) {
            jQuery(element).siblings().remove();
            return switchery = new Switchery(elem, defaults);
        }
        jQuery(element).siblings().remove();
        return switchery = new Switchery(elem, defaults);
    }
    return {
        inputCheckScript: inputCheckScript,
        setEvents: formcheck.setEvents,
        dataReload: dataReload,
        workerNameSelect2: workerNameSelect2,
        workerInfoSetting: workerInfoSetting,
        infoSetting: infoSetting,
        panelClear : panelClear,
        dataSend: dataSend,
        dataSendDel: dataSendDel,
        resizePanel: resizePanel,
        gridSetting: gridSetting,
        changeSwitchery: changeSwitchery
    }
});

require(['common', 'darkhand', 'local','bootstrap-datepicker.lang','bootstrap-datetimepicker','bootstrap-switchery','jquery','select2.lang'], function (common, darkhand, lc,datepicker, datetimepicker,Switchery,jQuery) {
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

                        jQuery('#occurDateTimePicker,#meaPlanDateTimePicker,#meaFinDateTimePicker,#svStopDateTimePicker,#svStartDateTimePicker,#measureDateTimePicker').data('DateTimePicker').date(new Date());

                        lc.panelClear(true);// 전체 폼 초기화
                        lc.panelClear(false,'measureInfoPanel',false);
                        jQuery("#measureGridList").clearGridData();

                        lc.dataReload('disorder'); // 장애처리 목록
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
                            lc.dataSend('disorder');
                        }
                    });
                }
                if(authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#measureInfoPanel :input"),
                        script: function() {

                            var lc = require('local');
                            return lc.inputCheckScript('measureInfoPanel');
                        },
                        ret: "btnReg2",
                        state: function() {
                            // 검색 Event 처리
                            var lc = require('local');
                            lc.dataSend('measure');
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
    function equipSetting() {
        var disOrderDateTypeList = [{ id:"occur_dt",val :'발생일'} ,
            { id:'sv_stop_dt',val :'서비스 정지일'},
            { id:'sv_start_dt',val :'서비스 시작일'},
            {id:'mea_plan_dt',val :'조치 예정일'},
            {id:'mea_fin_dt',val : '조치 완료일'}];

        common.setSelectOpt(jQuery('#srcHeadGrp'), "- 선택 -", disOrderDateTypeList); // 기간항목
        common.setSelectOpt(jQuery('#srcGrp'), "- 전체 -", grpList); // 장비유형 검색창
        common.setSelectOpt(jQuery('#disorderGroup'), null, disOrderTypeList); // 장애유형
        common.setSelectOpt(jQuery('#equipTypeGroup'), "- 장비유형 선택 -", grpList); // 장비유형
        common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -",[]); // 장비
        common.setSelectOpt(jQuery('#workTypeGroup'), "- 작업유형 선택 -", workTypeList); // 작업유형
        common.setSelectOpt(jQuery('#meaTypeGroup'), "- 조치사항 선택 -", meaTypeList); // 조치사항
        common.setSelectOpt(jQuery('#workStateGroup'), "- 작업상태 선택 -", workStateList); // 작업상태

        jQuery('#equipTypeGroup').change(function() {
            jQuery.when(
                jQuery.ajax({
                    url: '../getEquipList',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        eqp_grp_cd: jQuery(this).val()
                    }),
                })
            )
                .done(function(data) {
                    if (data ) {
                        // 성공 후
                        jQuery("select[name='equipGroup'] option").remove();
                        common.setSelectOpt(jQuery('#equipGroup'), null, data); // 장비유형
                    }


                    jQuery.when(
                        jQuery.ajax({
                            url: './getRdayTroubleShootData',
                            type: "POST",
                            dataType: "json",
                            contentType: "application/json; charset=utf-8",
                            data: JSON.stringify({
                                dis_seq: jQuery("#disorderList").jqGrid('getRowData', jQuery("#disorderList").jqGrid('getGridParam', 'selrow')).dis_seq
                            })
                        })
                    ).then(function(data) {

                            common.setValues({
                                equipGroup: data.eqp_cd,
                            });

                        })
                        .fail(common.ajaxError)
                        .always(function() {

                            return false;
                        });

                })
                .fail(function (jqXhr, textStatus, errorThrown) {
                    jQuery("select[name='equipGroup'] option").remove();
                    common.setSelectOpt(jQuery('#equipGroup'), "- 장비 선택 -", []);
                })
                .always(function() {

                    return false;
                });

        })
    }
    function tabEvent() {

        jQuery('.nav-tabs a').on('shown.bs.tab', function(event) {
            var x = $(event.target).text();         // active tab

            if(x==='장애처리') {
                jQuery('#btnReg, #btnCancel').prop('disabled', (authCrud.REG_FL === 'Y' || authCrud.MOD_FL === 'Y' ) ? false : true);
                jQuery('#btnDel').prop('disabled', (authCrud.DEL_FL === 'Y' ) ? false : true);

                var rowid = jQuery("#measureGridList").jqGrid('getGridParam', 'selrow');

                if (rowid !== null ) {

                    jQuery("#measureGridList").jqGrid("resetSelection"); // Grid Select Reset 처리
                }

                lc.panelClear(false, 'measureInfoPanel', false)


            }else {

                jQuery('#btnReg,#btnDel, #btnCancel').prop('disabled',  true );

                lc.dataReload('measureGrid');
            }
            var y = $(event.relatedTarget).text();  // previous tab
        });
    }
    function dateSetting() {
        var nowTemp = new Date();
        jQuery("#occurDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD HH:mm:ss',
            showTodayButton: true,
            showClear: true,
        });
        jQuery("#meaPlanDateTimePicker, #meaFinDateTimePicker, #svStopDateTimePicker, #svStartDateTimePicker ,#measureDateTimePicker").datetimepicker({
            locale: 'ko',
            format: 'YYYY-MM-DD HH:mm:ss',
            showTodayButton: true,
            showClear: true
        });
        jQuery("#svStartDateTimePicker,#meaPlanDateTimePicker,#meaFinDateTimePicker").datetimepicker({
            useCurrent: false //Important! See issue #1075
        });
        jQuery("#occurDateTimePicker").on("dp.change", function (e) {

            jQuery("#meaPlanDateTimePicker").data("DateTimePicker").minDate(e.date);
            jQuery('#meaFinDateTimePicker').data("DateTimePicker").minDate(e.date);
            jQuery('#svStartDateTimePicker').data("DateTimePicker").minDate(e.date);
            jQuery('#svStopDateTimePicker').data("DateTimePicker").minDate(e.date);
        });

        //$('#svStartDateTimePicker').datetimepicker({
        //    useCurrent: false //Important! See issue #1075
        //});
        jQuery("#svStopDateTimePicker").on("dp.change", function (e) {
            jQuery('#svStartDateTimePicker').data("DateTimePicker").minDate(e.date);
        });
        $("#svStartDateTimePicker").on("dp.change", function (e) {
            jQuery('#svStopDateTimePicker').data("DateTimePicker").maxDate(e.date);
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

        jQuery('#measuretab').prop('disabled',true);
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

        equipSetting();

        tabEvent();

        lc.changeSwitchery('#wFin_fl',false);
        lc.changeSwitchery('#wNature_fl',false);
        lc.changeSwitchery('#wErr_fl',false);

        jQuery('#measuretab').on('click', function() {
            if(jQuery('#measuretab').prop('disabled')) {
                common.setOSXModal('장애처리 목록을 선택 후 사용해주시기 바랍니다.');
            }
        });

        // 작업자 정보 추가 버튼 클릭 시
        jQuery('#btnAdd').on('click',function() {
            jQuery.when(
                jQuery('#workerGridList').jqGrid('setGridParam', { page : 1 })
            ).always(function(){
                var row = jQuery('#disorderList').jqGrid('getGridParam','selrow');
                common.addRow('workerGridList', {dis_seq:jQuery('#disorderList').jqGrid('getRowData', row).dis_seq , work_mem_id: '', comp_nm: '', tel: '', email: '',  nm:'이름을 선택하세요.'}, function (){
                    var lc = require('local');
                    var rowid = jQuery('#workerGridList').jqGrid('getGridParam','selrow');
                    jQuery('#' + rowid + '_nm').select2({
                        minimumInputLength: 0,
                        ajax:lc.workerNameSelect2()

                    });

                    jQuery('#' + rowid + '_nm').on('select2:select', function(){
                        var infoData = lc.workerInfoSetting(jQuery('#' + rowid + '_nm').val());


                        jQuery('#workerGridList').setRowData(rowid,{tel: infoData.tel_hp, email:infoData.email });

                    });
                    lc.setEvents();
                });
            });
        });

        // 보고서 내보내기 버튼 클릭 시
        jQuery('#btnReport').on('click', function() {

            var selIdx = jQuery('#disorderList').jqGrid('getGridParam','selrow');

            if (selIdx === null) {

                common.setOSXModal('장애처리목록을 선택 후 사용하기 바랍니다.');
                return false;
            }

            jQuery.fn.loadingStart(); // 로딩 시작

            jQuery.when(
                jQuery.ajax({
                    url: './getDisOrderSecureData',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        dis_seq: jQuery("#disorderList").jqGrid('getRowData', jQuery("#disorderList").jqGrid('getGridParam', 'selrow')).dis_seq
                    })
                })
            ).then(function(data) {

                    var handle = window.open($.fn.preUrl + '/report/ozReportDisOrderPreview?key=' + data.key, 'reportPreview', 'directories=0, width=1171, height=600, location=0, menubar=0, resizeable=0, status=0, toolbar=0');
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

            jQuery('#occurDateTimePicker,#meaPlanDateTimePicker,#meaFinDateTimePicker,#svStopDateTimePicker,#svStartDateTimePicker,#measureDateTimePicker').data('DateTimePicker').date(new Date());

            lc.panelClear(true);
            lc.panelClear(false, 'measureInfoPanel', true);
            jQuery('#workerGridList').clearGridData();
            jQuery("#measureGridList").clearGridData();
            dateSetting();
        });
        jQuery('#btnCancel2').on('click', function () {
            jQuery('#measureDateTimePicker').data('DateTimePicker').date(new Date());
            lc.panelClear(false, 'measureInfoPanel', true);
        });

        function fileSelect(obj){
            var len = obj.value.length;
            var last = obj.value.lastIndexOf('\\');
            var fileName = obj.value.substring(last+1,len);

            return fileName;
        }

        jQuery('#wbUpload').on('click', function (e){
            e.preventDefault();
            jQuery('#wBeforeFiles').click();
        });

        jQuery('#wBeforeFiles').on('change', function (){
            var html = ''
            jQuery('#wbFileName').empty();
            html = fileSelect(this);
            jQuery('#wbFileName').append(html);
        });
        jQuery('#waUpload').on('click', function (e){
            e.preventDefault();
            jQuery('#wAfterFiles').click();
        });

        jQuery('#wAfterFiles').on('change', function (){
            var html = ''
            jQuery('#waFileName').empty();
            html = fileSelect(this);
            jQuery('#waFileName').append(html);
        });



        // 그리드 초기화
        lc.gridSetting('disorder'); // 장애처리 목록

        lc.gridSetting('measureGrid'); // 장애처리 조치사항 목록

        lc.gridSetting('workerGrid'); // 작업자 정보 목록

    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize',function () {

        lc.resizePanel();

    }).trigger('resize');
});