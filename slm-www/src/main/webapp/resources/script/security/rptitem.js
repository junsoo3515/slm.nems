/**
 * User: 이준수
 * Date: 2017.06.01
 * Time: 오후 6:30
 */
define('local', ['common', 'formcheck', 'jqGrid.setting', 'jquery', 'jqGrid'], function (common, formcheck, jqFn, jQuery) {

    jQuery.jgrid.defaults.width = 780;
    jQuery.jgrid.defaults.responsive = true;
    jQuery.jgrid.defaults.styleUI = 'Bootstrap';

    // 리턴 스크립트 체크
    function inputCheckScript(tarID) {

        var topicNm = jQuery('#topic_nm').val().trim();
        var korCheck = /[ㄱ-ㅎ|ㅏ-ㅣ|가-힣]/;
        var engNm = jQuery('#eng_nm').val().trim();
        var engCheck = /[A-Za-z]/;

        switch (tarID) {
            case 'reportTopicInfoPanel':

                if(jQuery('#topic_cd').val() === ''){
                    common.setOSXModal('항목코드를 입력하세요.', jQuery('#topic_cd'));
                    return false;
                }

                if(jQuery('#select2-nm-container').attr('title') === '보고서 항목을 선택하세요.' || jQuery('#select2-nm-container').attr('title') === ''){
                    common.setOSXModal('보고서 항목을 선택하세요.', jQuery('#nm'));
                    return false;
                }


                if (!(korCheck.test(topicNm)) || jQuery('#topic_nm').val().trim === '') {
                    common.setOSXModal('명칭(한글)은 필수이고 한글만 가능합니다.', jQuery('#topic_nm'));
                    return false;
                }
                if (!(engNm === '' || engCheck.test(engNm))) {
                    common.setOSXModal('명칭(영어)은 영문, 공백만 가능합니다.', jQuery('#eng_nm'));
                    return false;
                }

                if(jQuery('#topic_type').val() === null){
                    common.setOSXModal('유형을 선택하세요', jQuery('#topic_type'));
                    return false;
                }

                if(jQuery('#pos').val() === null){
                    common.setOSXModal('정렬순서를 선택하세요', jQuery('#pos'));
                    return false;
                }

                break;

            default:
                break;
        }


        return formcheck.checkForm(tarID);
    }

    // 보고서 항목 select2(ajax)
    function rptTopicNmSelect2(pVal) {
        if (pVal === undefined) {
            pVal = '';
        }

        return {
            url: './getRptTopicNmSelect2',
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
                }

            }
        }
    }

    // 장비 select2(ajax)
    function equipSelect2(pVal) {
        if (pVal === undefined) {
            pVal = '';
        }

        return {
            url: './getEquipSelect2',
            type: "GET",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            delay: 250,
            data: JSON.stringify(function (params) {
                return {
                    q: params.term,
                    sv: pVal
                };
            }),
            processResults: function (data) {

                return {
                    results: data
                };
            }
        }
    }

    // 장비그룹 기본항목 select2(ajax)
    function equipGrpSelect2(pVal) {
        if (pVal === undefined) {
            pVal = '';
        }

        return {
            url: './getEquipGrpSelect2',
            type: "GET",
            dataType: "json",
            contentType: "application/json; charset=utf-8",
            delay: 250,
            data: JSON.stringify(function (params) {

                return {
                    q: params.term,
                    sv: pVal
                };
            }),
            processResults: function (data) {

                return {
                    results: data
                };
            }
        }
    }

    // jqGrid data 리프레쉬
    function dataReload(tarID) {

        var listID = tarID + "List";
        var dataGrid = jQuery("#" + listID);
        var filterData = {};

        var jqOpt = {};

        switch (listID) {

            case 'reportTopicList' :
                // 기본항목관리
                filterData = jQuery('#srcPanel :input').serializeObject();
                console.log(filterData);
                break;

            case 'swTopicGridList' :
                // S/W 점검항목 기초데이터 관리
                var obj = jQuery('#reportTopicList');
                var rowid = obj.jqGrid('getGridParam', 'selrow');
                jqOpt = {
                    url: './getSwTopicList'
                };

                if (rowid !== null) {
                    filterData = {
                        rpt_cd: obj.getRowData(rowid).rpt_cd
                    }
                }
                else {
                    filterData = {
                        rpt_cd: ''
                    }
                }

                break;
            case 'equipTopicGridList' :
                // 장비그룹 연계
                var obj = jQuery('#reportTopicList');
                var rowid = obj.jqGrid('getGridParam', 'selrow');
                jqOpt = {
                    url: './getEquipTopicList'
                };

                if (rowid !== null) {
                    filterData = {
                        rpt_topic_cd: obj.getRowData(rowid).topic_cd
                    }
                }
                else {
                    filterData = {
                        rpt_topic_cd: ''
                    }
                }
                break;
        }

        dataGrid
            .jqGrid("setGridParam", jQuery.extend(true, {
                search: true,
                postData: {
                    filters: JSON.stringify(filterData)
                }
            }, jqOpt))
            .trigger("reloadGrid", [{page: 1}]);
    }

    // 기본정보 폼 Setting
    function infoSetting(rowID, obj) {

        var regBtn = jQuery('#btnReg');

        if (rowID) {
            // 리스트 폼에 있는 객체 입력 폼 Setting
            jQuery('#topic_cd').attr("readonly", true); // 항목 코드(수정 모드에서 수정 못하게 비활성화)
            jQuery('#nm').attr("disabled", true); // 항목 코드(수정 모드에서 수정 못하게 비활성화)

            if (authCrud.MOD_FL === 'Y') {

                jQuery('#btnReg, #btnCancel').attr('disabled', false);
                regBtn.html(regBtn.html().replace("등록", "수정"));
            }

            jQuery.when(
                jQuery.ajax({
                    url: './getReportTopicInfo',
                    type: "POST",
                    dataType: "json",
                    contentType: "application/json; charset=utf-8",
                    data: JSON.stringify({
                        topic_cd: obj.topic_cd
                    })
                })
            ).then(function (data) {
                    // 그리드의 값을 가지고 입력 값 Setting
                    common.setValues({
                        topic_cd: data.topic_cd,
                        rpt_cd: data.rpt_cd,
                        topic_nm: data.topic_nm,
                        eng_nm: data.eng_nm,
                        topic_type: data.topic_type,
                        use_fl: data.use_fl,
                        pos: data.pos,
                        init_val: data.init_val,
                        link_itm_cd: data.link_itm_cd,
                        mod_lock_fl: data.mod_lock_fl,
                        rpt_print_fl: data.rpt_print_fl,
                        mrpt_print_fl: data.mrpt_print_fl,
                        topic_unit: data.topic_unit,
                        remark: data.remark
                    });
                    // 기본항목 입력 폼의 보고서 항목 AutoText Search기능
                    jQuery('#nm').select2({
                        minimumInputLength: 0,
                        width: '50%',
                        ajax: rptTopicNmSelect2()
                    });

                    // DB 값 setting하기 위한 옵션
                    jQuery('#nm')
                        .append(new Option(data.nm, data.rpt_cd, true, true))
                        .trigger('change');

                    return data;
                }).done(function () {
                })
                .fail(common.ajaxError)
                .always(function () {
                    return false;
                });
        }
    }

    // 패널 초기화
    function panelClear(isAll, objID, isListReset) {

        if (isAll === undefined) isAll = false; // 전체 reset 여부
        if (objID === undefined) objID = ''; // panelID
        if (isListReset === undefined) isListReset = true; // Master List reset 여부

        if (isAll) {
            // 모든 패널 초기화
            panelClear(false, 'reportTopicInfoPanel', false);

            var rowid = jQuery("#reportTopicList").jqGrid('getGridParam', 'selrow');

            if (rowid !== null && isListReset) {

                jQuery("#reportTopicList").jqGrid("resetSelection"); // Grid Select Reset 처리
            }
            jQuery('#myTabs a:first').tab('show'); // 처음 탭으로 강제 이동

            return false;
        }

        switch (objID) {

            case 'reportTopicInfoPanel':
                // 기본항목 입력 폼

                common.clearElement('#' + objID); // form element

                var regBtn = jQuery('#btnReg');

                regBtn.html((regBtn.html().replace("수정", "등록"))); // 등록버튼 명칭 변경
                jQuery("input[type='checkbox']").val('Y');

                jQuery('#nm')
                    .append(new Option('보고서 항목을 선택하세요.', '', true, true))
                    .trigger('change');


                break;
        }
    }

    // 기본정보 등록/수정 이벤트
    function dataSend(str) {

        switch (str) {

            case 'reportTopicInfo':
                // 로딩 시작
                jQuery.fn.loadingStart();

                var rowid = jQuery("#reportTopicList").jqGrid('getGridParam', 'selrow');
                var crud = null;
                if (rowid === null) {
                    // 등록 모드
                    if (authCrud.REG_FL === 'N') return false;
                    crud = 'C';
                } else {
                    // 수정 모드
                    if (authCrud.MOD_FL === 'N') return false;
                    crud = 'U';
                }

                var formData = jQuery('#reportTopicInfoPanel :input');
                var reqData = formData.serializeObject();

                // 기본 입력 폼의 값(key 변경 : vo 변수명에 맞춰서)

                reqData = common.changeKeys(reqData, [
                    {k: 'topic_cd', v: 'topic_cd'},
                    {k: 'nm', v: 'rpt_cd'},
                    {k: 'topic_nm', v: 'topic_nm'},
                    {k: 'eng_nm', v: 'eng_nm'},
                    {k: 'topic_type', v: 'topic_type'},
                    {k: 'init_val', v: 'init_val'},
                    {k: 'topic_unit', v: 'topic_unit'},
                    {k: 'pos', v: 'pos'},
                    {k: 'link_itm_cd', v: 'link_itm_cd'},
                    {k: 'mod_lock_fl', v: 'mod_lock_fl'},
                    {k: 'remark', v: 'remark'}
                ]);
                reqData.mod_lock_fl = jQuery("input:checkbox[id='mod_lock_fl']").is(":checked") === true ? 'Y' : 'N';
                reqData.use_fl = jQuery("input:checkbox[id='use_fl']").is(":checked") === true ? 'Y' : 'N';
                reqData.rpt_print_fl = jQuery("input:checkbox[id='rpt_print_fl']").is(":checked") === true ? 'Y' : 'N';
                reqData.mrpt_print_fl = jQuery("input:checkbox[id='mrpt_print_fl']").is(":checked") === true ? 'Y' : 'N';
                reqData.crud = crud;


                // 데이터 전송
                jQuery.when(
                    jQuery.ajax({
                        url: './setReportTopicAct',
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

                            // 입력모드일 때는 입력 폼 초기화 및 사용자계정관리 목록 그리드 reload 처리
                            panelClear(true, 'reportTopicInfoPanel');
                            dataReload('reportTopic');
                            jQuery('#topic_cd').attr("readonly", false);
                            jQuery('#nm').attr("disabled", false);
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


    // 레이아웃 변경 시 사이즈 조절 리턴 함수
    function resizePanel(id) {

        if (id === undefined) {

            id = null;
        }

        if (id !== null) {

            jQuery.each([{list: id + "List", panel: id + "Panel"}], function (sIdx, data) {

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
            case 'reportTopicList' :
                // 기본 항목 관리
                jqOpt = {
                    url: './getReportTopicList',
                    scroll: 1,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '항목코드', '보고서 항목', '한글', '영어', '항목유형', '사용', '초기 데이터', '외부연계 항목', '수정잠금', '일일', '월간', '정렬'],
                    colModel: [
                        {name: 'rpt_cd', index: 'rpt_cd', editable: true, hidden: true, width: 1, sortable: false},
                        {
                            name: 'topic_cd',
                            index: 'topic_cd',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {name: 'nm', index: 'nm', editable: true, edittype: 'text', width: 1, sortable: false},
                        {
                            name: 'topic_nm',
                            index: 'topic_nm',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {name: 'eng_nm', index: 'eng_nm', editable: true, edittype: 'text', width: 1, sortable: false},
                        {
                            name: 'topic_type',
                            index: 'topic_type',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {name: 'use_fl', index: 'use_fl', editable: true, edittype: 'text', width: 1, sortable: false},
                        {
                            name: 'init_val',
                            index: 'init_val',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {
                            name: 'link_itm_cd',
                            index: 'link_itm_cd',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {
                            name: 'mod_lock_fl',
                            index: 'mod_lock_fl',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {
                            name: 'rpt_print_fl',
                            index: 'rpt_print_fl',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {
                            name: 'mrpt_print_fl',
                            index: 'mrpt_print_fl',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {name: 'pos', index: 'pos', editable: true, edittype: 'text', width: 1, sortable: false}
                    ],
                    onInitGrid: function () {

                        dataReload(tarID);
                    },
                    onSelectRow: function (id, status, event) {
                        var ret = dataGrid.jqGrid('getRowData', id);
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
                            // 장비 연계 그리드 초기화 및 갱신
                            infoSetting(id, ret);
                            if (jQuery('#myTabs li:eq(1)').hasClass('active') === true) {
                                jQuery('#btnReg, #btnCancel').attr('disabled', true);
                                dataReload('swTopicGrid');
                                dataReload('equipTopicGrid');
                            }
                            jQuery('#btnAdd1,#btnAdd2').attr('disabled', false);
                        }
                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        var obj = jQuery(this);
                        jQuery.when(
                            // grid 크기 자동 적용
                            resizePanel('reportTopic')
                        ).then(function () {
                                // 데이터가 존재 할 경우 처음 행 선택
                                if (data.rows.length > 0) {
                                    dataGrid.jqGrid('setSelection', dataGrid.find('tr[id]:eq(0)').attr('id'));
                                }
                            }).done(function () {

                                // 위에 dataInit 부분에서 isDigitDotOnly attr추가 하면서 이벤트 처리하기 위해서 다시 호출
                                formcheck.setEvents();
                                // group Header 추가
                                // loadComplete에서 안할 시 크기가 꺠짐
                                obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                                    useColSpanStyle: true,
                                    groupHeaders: [{
                                        startColumnName: 'rpt_print_fl',
                                        numberOfColumns: 2,
                                        titleText: '보고서 출력'
                                    },{
                                        startColumnName: 'topic_nm',
                                        numberOfColumns: 2,
                                        titleText: '명칭'
                                    }]
                                });
                                obj.jqGrid('destroyGroupHeader').jqGrid('setGroupHeaders', {
                                    useColSpanStyle: true,
                                    groupHeaders: [{
                                        startColumnName: 'topic_nm',
                                        numberOfColumns: 2,
                                        titleText: '명칭'
                                    }]
                                });
                            }).always(function () {
                                jQuery.fn.loadingComplete();
                                return false;
                            });
                    }
                };

                break;

            case 'swTopicGridList':
                // S/W 점검항목 기초 데이터 관리
                jqOpt = {
                    url: '',
                    editurl: './setSwTopicAct',
                    scroll: 1,
                    scrollrows: true,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '', '', '', '보고서항목', '항목', '출력순서', '보고서유형', '장비', '초기데이터', '사용', '관리'],
                    colModel: [
                        {name: 'rpt_cd', index: 'rpt_cd', editable: true, edittype: 'text', hidden: true},
                        {name: 'topic_cd', index: 'topic_cd', editable: true, edittype: 'text', hidden: true},
                        {name: 'rpt_gubun_cd', index: 'rpt_gubun_cd', editable: true, edittype: 'text', hidden: true},
                        {name: 'sw_seq', index: 'sw_seq', editable: true, edittype: 'text', hidden: true},
                        {
                            name: 'nm',
                            index: 'nm',
                            width: 1,
                            editable: true,
                            edittype: 'select',
                            sortable: false,
                            editrules: {required: true},
                            editoptions: {
                                dataInit: function (el) {
                                    jQuery(el).attr({msg: '보고서항목을'});

                                },
                                value: common.setjqGridOpt('-선택-', rptTopicNmList)

                            }
                        },
                        {
                            name: 'topic_nm',
                            index: 'topic_nm',
                            width: 1,
                            editable: true,
                            edittype: 'select',
                            sortable: false,
                            editrules: {required: true},
                            editoptions: {
                                dataInit: function (el) {
                                    jQuery(el).attr({msg: '항목이름을'});

                                },
                                value: common.setjqGridOpt(null, topicList)

                            }
                        },
                        {
                            name: 'print_sort',
                            index: 'print_sort',
                            editable: true,
                            edittype: 'text',
                            align: 'center',
                            width: 1,
                            sortable: false,
                            editrules: {required: true, number: true},
                            editoptions: {
                                dataInit: function (el) {
                                    jQuery(el).attr({msg: '출력순서를', isDigitOnly: '1'})
                                }
                            }
                        },
                        {
                            name: 'cd_nm',
                            index: 'cd_nm',
                            width: 1,
                            editable: true,
                            edittype: 'select',
                            align: 'center',
                            sortable: false,
                            editrules: {required: true},
                            editoptions: {
                                dataInit: function (el) {
                                    jQuery(el).attr({msg: '보고서유형을'});

                                },
                                value: common.setjqGridOpt('-선택-', rptTypeList)

                            }
                        },
                        {
                            name: 'eqp_nm',
                            index: 'eqp_nm',
                            width: 2,
                            editable: true,
                            edittype: 'select',
                            sortable: false,
                            editrules: {required: true},
                            editoptions: {
                                value: {},
                                dataInit: function (el) {
                                    var obj = jQuery(el);
                                    var rowid = obj.attr('rowid');
                                    var kObj = jQuery('#' + rowid + '_eqp_nm');

                                    obj.select2({
                                        minimumInputLength: 0, // 최소 검색어 개수
                                        ajax: equipSelect2()
                                    });
                                    // DB 값 setting하기 위한 옵션
                                    obj
                                        .append(new Option(obj.parent('td').attr('title'), kObj.val(), true, true))
                                        .val(kObj.val())
                                        .trigger('change');
                                }

                            }
                        },
                        {
                            name: 'init_val',
                            index: 'init_val',
                            editable: true,
                            edittype: 'text',
                            width: 1,
                            sortable: false
                        },
                        {
                            name: 'use_fl', index: 'use_fl', width: 1, align: 'center', sortable: false,
                            editable: true,
                            edittype: 'checkbox', editoptions: {
                            dataInit: function (el) {
                                jQuery(el).addClass('checkbox').parent().addClass('form-inline');
                            },
                            value: "Y:N"
                        }
                        },
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                keys: true,
                                editbutton: (authCrud.MOD_FL === "N" ? false : true),
                                delbutton: false,
                                onEdit: function (rowid) {
                                    // 수정 버튼 클릭 시 Event
                                    lastSel = jQuery.jgrid.jqID(rowid);

                                    // 수정할 수 없는 항목 disable 처리
                                    jQuery("tr#" + lastSel).find("select").filter(':eq(0), :eq(1)').attr("disabled", true);
                                    jQuery('#btnAdd2').attr('disabled',true);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                    jQuery('#btnAdd2').attr('disabled',false);
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'swTopicGrid');
                                    dataReload('swTopicGrid');
                                },
                                restoreAfterError: true // 저장 후 입력 폼 restore 자동/수동 설정
                            }
                        }
                    ],
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
                        jQuery('#swTopictab').prop('disabled', false);
                    },
                    loadComplete: function (data) {
                        // 그리드에 모든 데이터 로딩 완료 후
                        jQuery.when(
                            // grid 크기 자동 적용
                            resizePanel('swTopicGrid')
                        ).then(function () {
                            }).done(function () {
                                formcheck.setEvents();
                            }).always(function () {
                                jQuery.fn.loadingComplete();
                                return false;
                            });
                    }
                };
                break;

            case 'equipTopicGridList':
                // 장비그룹 연계
                jqOpt = {
                    url: '',
                    editurl: './setEquipTopicAct',
                    scroll: 1,
                    scrollrows: true,
                    rowList: [10, 30, 50, 100],
                    colNames: ['', '', '', '장비그룹 기본항목', '관리'],
                    colModel: [
                        {name: 'rpt_topic_cd', index: 'rpt_topic_cd', editable: true, edittype: 'text', hidden: true},
                        {name: 'prev_topic_cd', index: 'prev_topic_cd', editable: true, edittype: 'text', hidden: true},
                        {name: 'topic_cd', index: 'topic_cd', editable: true, edittype: 'text', hidden: true},
                        {
                            name: 'topic_nm',
                            index: 'topic_nm',
                            width: 4,
                            editable: true,
                            edittype: 'select',
                            sortable: false,
                            editrules: {required: true},
                            editoptions: {
                                dataInit: function (el) {
                                    var obj = jQuery(el);
                                    var rowid = obj.attr('rowid');

                                    var kObj = jQuery('#' + rowid + '_topic_nm');

                                    obj.select2({
                                        minimumInputLength: 0, // 최소 검색어 개수
                                        ajax: equipGrpSelect2()
                                    });
                                    // DB 값 setting하기 위한 옵션
                                    obj
                                        .append(new Option(obj.parent('td').attr('title'), kObj.val(), true, true))
                                        .val(kObj.val())
                                        .trigger('change');

                                },
                                value: {}
                            }
                        },
                        {
                            name: 'myac',
                            width: 1,
                            sortable: false,
                            classes: 'text-center',
                            formatter: 'actions',
                            formatoptions: {
                                editbutton: (authCrud.MOD_FL === "N" ? false : true),
                                delbutton: (authCrud.DEL_FL === 'N' ? false : true),
                                onEdit: function (rowid) {
                                    // 수정 버튼 클릭 시 Event
                                    lastSel = jQuery.jgrid.jqID(rowid);
                                    jQuery('#btnAdd1').attr('disabled',true);
                                },
                                afterRestore: function (rowid) {
                                    // 취소 버튼 클릭 시 Event
                                    jQuery('#btnAdd1').attr('disabled',false);
                                },
                                onSuccess: function (res) {
                                    // 저장 후 리턴 결과
                                    gridResAction(jQuery.parseJSON(res.responseText), 'equipTopicGrid');
                                    dataReload('equipTopicGrid');

                                },
                                restoreAfterError: true, // 저장 후 입력 폼 restore 자동/수동 설정
                                delOptions: {
                                    url: './setEquipTopicDel',
                                    mtype: 'POST',
                                    ajaxDelOptions: {contentType: "application/json", mtype: 'POST'},
                                    serializeDelData: function () {

                                        var reqData = dataGrid.jqGrid('getRowData', lastSel);
                                        return JSON.stringify({
                                            rpt_topic_cd: reqData.rpt_topic_cd,
                                            topic_cd: reqData.topic_cd
                                        });
                                    },
                                    reloadAfterSubmit: false,
                                    afterComplete: function (res) {
                                        dataReload('equipTopicGrid');
                                        gridResAction(jQuery.parseJSON(res.responseText), 'equipTopicGrid');

                                    }
                                }
                            }
                        }
                    ],
                    onSelectRow: function (id, status, event) {
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
                    loadComplete: function (data) {

                        // 그리드에 모든 데이터 로딩 완료 후
                        resizePanel('equipTopicGrid'); // 브라우저 창 크기 변경 시 grid 크기 자동 적용
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
        rptTopicNmSelect2: rptTopicNmSelect2,
        equipSelect2: equipSelect2,
        equipGrpSelect2: equipGrpSelect2,
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

require(['common', 'darkhand', 'local', 'jquery', 'select2.lang'], function (common, darkhand, lc, jQuery) {
    // 엔터 적용
    function enterCheck(idx) {

        if (idx === undefined) idx = 0;

        var tw = [];

        switch (idx) {
            case 0:

                tw.push({
                    chk: jQuery("#srcPanel :input"),
                    script: function () {
                    },
                    ret: "btnSrch",
                    state: function () {

                        var lc = require('local');

                        jQuery.fn.loadingStart();

                        lc.panelClear(true); // 전체 폼 초기화
                        lc.dataReload('reportTopic'); // 코드 목록

                        jQuery('.nav-tabs a[href="#reportTopicInfoPanel"]').tab('show');

                    }
                });

                if (authCrud.REG_FL === 'Y' && authCrud.MOD_FL === 'Y') {

                    tw.push({
                        chk: jQuery("#reportTopicInfoPanel :input"),
                        script: function () {

                            var lc = require('local');
                            return lc.inputCheckScript('reportTopicInfoPanel');
                        },
                        ret: "btnReg",
                        state: function () {
                            // 검색 Event 처리
                            var lc = require('local');
                            lc.dataSend('reportTopicInfo');
                            lc.panelClear(false, 'reportTopicInfoPanel', false);
                        }
                    });
                }


                break;
        }

        common.enterSend(tw);
    }

    var tabObj = jQuery('#myTabs a[data-toggle="tab"]');

    // 탭 show trigger 이벤트
    tabObj.off('shown.bs.tab').on('shown.bs.tab', function (e) {
        // 해당 계통의 펌프현황 정보 가져오기(헤더 설정을 위한)
        var tabIdx = tabObj.index(e.target);
        var obj = jQuery('#reportTopicList');
        var rowid = obj.jqGrid('getGridParam', 'selrow');

        switch (tabIdx) {
            case 0:
                // 기본항목 입력 폼
                lc.infoSetting(rowid, obj.getRowData(rowid).rpt_cd);
                break;
            case 1:
                // S/W 점검항목 및 장비그룹 연결 탭

                jQuery('#btnReg, #btnCancel').attr('disabled', true);
                if (rowid === null) {
                    jQuery('#btnAdd1,#btnAdd2').attr('disabled', true);
                }
                lc.dataReload('swTopicGrid');
                lc.dataReload('equipTopicGrid');
                break;
        }
    });

    // 그리드 엔터 키 누를 경우 validation 및 저장 함수
    function gridEnterSave(listID) {

        var obj = jQuery('#' + listID);
        var id, key;

        switch (listID) {
            case 'swTopicGridList' :

                id = lastSel;
                key = 'swTopic';
                break;
        }

        if (lc.inputCheckScript(listID) === true) {

            var opers = ( jQuery("#" + id).hasClass('jqgrid-new-row') ? "add" : "edit" );

            obj.jqGrid('saveRow', id, {
                extraparam: {oper: opers},
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

            jQuery('#btnSrch').attr('disabled', true);
        }
        if (authCrud.REG_FL === 'N') {

            jQuery('#btnAdd').attr('disabled', true);
        }

        // 엔터키 이벤트 체크
        lc.setEvents();
        enterCheck(); // 엔터 적용

        var topicTypeList = [
            {"id": "text", "val": "문자열"},
            {"id": "int", "val": "숫자"},
            {"id": "select", "val": "선택"}
        ];

        // 검색 폼
        common.setSelectOpt(jQuery('#srcRptNm'), '-전체-', rptTopicNmList); // 보고서 항목
        common.setSelectOpt(jQuery('#srcType'), '-전체-', topicTypeList); // 항목유형

        // 기본항목 입력 폼

        common.setSelectOpt(jQuery('#com_cd'), '-선택-', rptTypeList); // 보고서유형
        common.setSelectOpt(jQuery('#topic_type'), '-선택-', topicTypeList); // 유형
        common.setSelectOpt(jQuery('#link_itm_cd'), '-선택-', linkTopicList); // 외부연계 항목


        // S/W 점검항목 기초데이터 관리 추가버튼 클릭시
        jQuery('#btnAdd1').on('click', function () {
            jQuery('#btnAdd2').attr('disabled',true);
            jQuery.when(
                jQuery('#swTopicGridList').jqGrid('setGridParam', { page: 1 })
            ).
                always(function(){
                    common.addRow('swTopicGridList', {
                        rpt_cd: jQuery('#reportTopicList').jqGrid('getRowData', jQuery('#reportTopicList').jqGrid('getGridParam', 'selrow')).rpt_cd,
                        topic_cd: jQuery('#reportTopicList').jqGrid('getRowData', jQuery('#reportTopicList').jqGrid('getGridParam', 'selrow')).topic_cd,
                        topic_nm: jQuery('#reportTopicList').jqGrid('getRowData', jQuery('#reportTopicList').jqGrid('getGridParam', 'selrow')).topic_nm,
                        nm: jQuery('#reportTopicList').jqGrid('getRowData', jQuery('#reportTopicList').jqGrid('getGridParam', 'selrow')).nm,
                        eqp_nm: '장비를 선택하세요.'
                    }, function () {
                        var lc = require('local');
                        jQuery("tr#" + lastSel).find("select").filter(':eq(0), :eq(1)').attr("disabled", true);

                        var rowid = jQuery('#swTopicGridList').jqGrid('getGridParam', 'selrow');
                        jQuery('#' + rowid + '_eqp_nm').select2({
                            minimumInputLength: 0,
                            ajax: lc.equipSelect2()
                        });

                        lc.setEvents();
                    });
                });
        });

        // 장비그룹 연계 추가 버튼 클릭시
        jQuery('#btnAdd2').on('click', function () {
            jQuery('#btnAdd1').attr('disabled',true);
            jQuery.when(
                jQuery('#equipTopicGridList').jqGrid('setGridParam', { page: 1})
            ).always(function(){
                    common.addRow('equipTopicGridList', {
                        rpt_topic_cd: jQuery('#reportTopicList').jqGrid('getRowData', jQuery('#reportTopicList').jqGrid('getGridParam', 'selrow')).topic_cd,
                        topic_nm: '장비그룹 기본항목을 선택하세요.'
                    }, function () {
                        var lc = require('local');
                        var rowid = jQuery('#equipTopicGridList').jqGrid('getGridParam', 'selrow');
                        jQuery('#' + rowid + '_topic_nm').select2({
                            minimumInputLength: 0,
                            ajax: lc.equipGrpSelect2()
                        });
                        lc.setEvents();
                    });
                });
        });

        // 취소 버튼 클릭 이벤트
        jQuery('#btnCancel').on('click', function () {
            lc.panelClear(true);
            jQuery('#topic_cd').attr("readonly", false);
            jQuery('#nm').attr("disabled", false);

        });

        // jqGrid의 입력/수정 모드 시 엔터 값 적용 하기 위한 key Event Catch
        jQuery("#swTopicGridList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave('swTopicGridList');
                return false;
            }
        });

        jQuery("#equipTopicGridList").on("keydown", ':input', function (e) {

            if (e.keyCode === 13) {

                gridEnterSave('equipTopicGridList');
                return false;
            }
        });

        // 그리드 초기화
        lc.gridSetting('reportTopic'); // 기본항목관리
        lc.gridSetting('swTopicGrid'); // S/W 점검항목 기초데이터 관리
        lc.gridSetting('equipTopicGrid'); // 장비그룹 연계
    });

    // 윈도우 화면 리사이즈 시 이벤트
    jQuery(window).bind('resize', function () {

        lc.resizePanel();

    }).trigger('resize');
});