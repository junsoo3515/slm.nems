<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
    <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
    <li><a href="javascript:;">일일보고서</a></li>
    <li class="active">장애처리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">장애처리
    <small>장비에 대한 장애처리 정보 관리</small>
</h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
    <div class="panel-body">
        <div class="row">
            <div class="col-md-10 form-inline">
                <div class="form-group m-r-40">
                    <div class="form-group m-r-25">
                        <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 기간항목</label>
                        <select id="srcHeadGrp" name="srcHeadGrp" class="form-control"></select>
                    </div>
                    <div class="form-group m-r-25">
                        <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 기간</label>

                        <div class="input-group input-daterange">
                            <input type="text" id="srcSDate" name="srcSDate" class="form-control" msg="기간을"4
                                   placeholder="시작일을 선택하세요" readonly="readonly"/>
                            <span class="input-group-addon">~</span>
                            <input type="text" id="srcEDate" name="srcEDate" class="form-control" msg="기간을"
                                   placeholder="종료일을 선택하세요" readonly="readonly"/>
                        </div>
                    </div>
                    <div class="form-group m-r-25">
                        <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 장애완료여부</label>

                        <div class="form-group m-r-25">
                            <input type="checkbox" data-render="switchery" data-theme="default" id="fin_fl"
                                   name="fin_fl">
                        </div>
                    </div>
                    <div class="form-group  m-r-25">
                        <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 장비유형</label>
                        <select id="srcGrp" name="srcGrp" class="form-control"></select>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="pull-right">
                    <button id="btnReport" class="btn btn-sm btn-primary"><i class="fa fa-download icon-white"></i> 보고서
                        내보내기
                    </button>
                    <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- end 검색 폼 panel -->

<!-- begin 장애처리 리스트 panel -->
<div id="disorderPanel" class="panel panel-inverse">
    <div class="panel-heading">
        <h4 class="panel-title">장애처리목록</h4>
    </div>
    <table id="disorderList"></table>
    <div id="disorderPager"></div>
</div>
<!-- end 권한 리스트 panel -->

<!-- begin 장애처리 정보 tab header -->
<ul class="nav nav-tabs">
    <li class="active"><a href="#infoPanel" data-toggle="tab">장애처리</a></li>
    <li class=""><a href="#measurePanel" data-toggle="tab" id="measuretab">조치사항</a></li>
    <div class="pull-right">
        <button id="btnReg" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
        <button id="btnDel" class="btn btn-primary" disabled="true"><i class="glyphicon glyphicon-ok icon-white"></i> 삭제</button>
        <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소
        </button>
    </div>
</ul>
<!-- end 권한 정보 tab header -->


<div class="tab-content">
    <div id="infoPanel" class="tab-pane active">
        <table class="table table-bordered">
            <tr>
                <th class="col-md-2">
                    <i class="glyphicon glyphicon-ok"></i> 장애유형
                </th>
                <td class="col-md-4">
                    <div class="form-inline">
                        <select id="disorderGroup" name="disorderGroup" msg="장애유형을" class="form-control"></select>
                        <input type="hidden" id="wDis_seq" name="wDis_seq">
                    </div>
                </td>

                <th class="col-md-2">
                    <i class="glyphicon glyphicon-ok"></i> 장비
                </th>
                <td class="col-md-4">
                    <div class="form-inline">
                        <select id="equipTypeGroup" name="equipTypeGroup" msg="장비유형을" class="form-control"></select>
                        <select id="equipGroup" name="equipGroup" msg="장비를" class="form-control"></select>
                    </div>
                </td>

            </tr>
            <tr>
                <th class="col-md-2">
                    <i class="glyphicon glyphicon-ok"></i> 작업유형
                </th>
                <td class="col-md-4">
                    <div class="form-inline">
                        <select id="workTypeGroup" name="workTypeGroup" msg="작업유형을" class="form-control"></select>
                    </div>
                </td>
                <th class="col-md-2">
                    <i class="glyphicon glyphicon-ok"></i> 발생일자
                </th>
                <td class="col-md-4">
                    <div class='input-group date' id='occurDateTimePicker'>
                        <input type='text' id="wOccur_dt" name="wOccur_dt" msg="발생일자를" class="form-control"/>
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                    </div>
                </td>
            </tr>
            <tr>
                <th class="col-md-2">
                    <i class="glyphicon glyphicon-ok"></i>조치예정일
                </th>
                <td class="col-md-10" colspan="3">
                    <div class="form-inline">
                        <div class='input-group date' id='meaPlanDateTimePicker'>
                            <input type='text' id="wMea_plan_dt" name="wMea_plan_dt" class="form-control"/>
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                        </div>
                    </div>
                </td>
            </tr>
            <tr>
                <th class="col-md-2">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;장애내용
                </th>
                <td class="col-md-10" colspan="3">
                    <textarea id="wCont" name="wCont" class="form-control" placeholder="장애내용을 입력하세요."
                              rows="5"></textarea>
                </td>
            </tr>
            <tr>
                <th class="col-md-2">
                    &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;장애요약내용
                </th>
                <td class="col-md-10" colspan="3">
                    <textarea id="wSumm_cont" name="wSumm_cont" class="form-control" placeholder="장애요약내용을 입력하세요."
                              rows="5"></textarea>
                </td>

            </tr>
        </table>
        <div id="workerPanel" class="panel panel-inverse">
            <div class="panel-heading">
                <div class="btn-group-custom pull-right">
                    <button id="btnAdd" class="btn btn-primary btn-sm" disabled="true"><i
                            class="fa fa-plus icon-white"></i> 추가
                    </button>
                </div>
                <h4 class="panel-title">작업자 정보</h4>
            </div>
            <table id="workerGridList"></table>
        </div>

        <div id="resultPanel" class="panel panel-inverse">
            <div class="panel-heading">
                <h4 class="panel-title">처리 결과</h4>
            </div>
            <table class="table table-bordered">
                <tr>
                    <th class="col-md-2">
                        <i class="glyphicon glyphicon-ok"></i> 작업상태
                    </th>
                    <td class="col-md-4">
                        <div class="form-inline">
                            <select id="workStateGroup" name="workStateGroup" msg="작업상태를" class="form-control"></select>
                        </div>
                    </td>

                    <th class="col-md-2">
                        <i class="glyphicon glyphicon-ok"></i> 조치사항
                    </th>
                    <td class="col-md-4">
                        <div class="form-inline">
                            <select id="meaTypeGroup" name="meaTypeGroup" msg="조치사항을" class="form-control"></select>
                        </div>
                    </td>
                </tr>

                <tr>
                    <th class="col-md-2">
                        <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;장애확정여부
                    </th>
                    <td class="col-md-4">
                        <input type="checkbox" id="wErr_fl" name="wErr_fl"/>
                    </td>
                    <th class="col-md-2">
                        <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;조치완료일
                    </th>
                    <td class="col-md-4">
                        <div class="form-inline">
                            <div class='input-group date' id='meaFinDateTimePicker'>
                                <input type='text' id="wMea_fin_dt" name="wMea_fin_dt" class="form-control"/>
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                            </div>
                        </div>
                    </td>
                </tr>

                <tr>
                    <th class="col-md-2">
                        <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;자연재해여부
                    </th>
                    <td class="col-md-4">
                        <input type="checkbox" id="wNature_fl" name="wNature_fl"/>
                    </td>
                    <th class="col-md-2">
                        <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;완료여부
                    </th>
                    <td class="col-md-4">
                        <input type="checkbox" id="wFin_fl" name="wFin_fl"/>
                    </td>
                </tr>
                <tr>
                    <th class="col-md-2">
                        <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;서비스 정지일
                    </th>
                    <td class="col-md-4">
                        <div class="form-inline">
                            <div class='input-group date' id='svStopDateTimePicker'>
                                <input type='text' id="wSv_stop_dt" name="wSv_stop_dt" class="form-control"/>
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                            </div>
                        </div>
                    </td>
                    <th class="col-md-2">
                        <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;서비스 시작일
                    </th>
                    <td class="col-md-4">
                        <div class="form-inline">
                            <div class='input-group date' id='svStartDateTimePicker'>
                                <input type='text' id="wSv_start_dt" name="wSv_start_dt" class="form-control"/>
                <span class="input-group-addon">
                    <span class="glyphicon glyphicon-calendar"></span>
                </span>
                            </div>
                        </div>
                    </td>
                </tr>
            </table>
        </div>
    </div>
<div id="measurePanel" class="tab-pane fade">
    <div class="row">
        <div class="col-md-6">
            <div id="measureGridPanel" class="panel panel-inverse">
                <div class="panel-heading">
                    <h4 class="panel-title">조치사항</h4>
                </div>
                <table id="measureGridList"></table>
                <div id="measureGridPager"></div>
            </div>
        </div>
        <div class="col-md-6">
            <div class="nav panel-inverse">
                <div class="panel-heading">
                    <div class="btn-group-custom pull-right">
                        <button id="btnReg2" class="btn btn-primary btn-sm"><i
                                class="glyphicon glyphicon-ok icon-white"></i> 저장
                        </button>
                        <button id="btnCancel2" class="btn btn-danger btn-sm m-l-3"><i
                                class="glyphicon glyphicon-ok icon-white"></i> 취소
                        </button>
                    </div>
                    <li class="panel-title"><a href="#measureInfoPanel" data-toggle="tab">작업내역</a></li>
                </div>
            </div>
            <div id="measureInfoPanel" class="tab-pane active">
                <table class="table table-bordered">
                    <tr>
                        <th class="col-md-2">
                            <i class="glyphicon glyphicon-ok"></i> 조치일자
                        </th>
                        <td colspan="3" class="col-md-10">
                            <div class='input-group date' id='measureDateTimePicker'>
                                <input type='text' id="wMeasure_dt" name="wMeasure_dt" msg="조치일자를"
                                       class="form-control"/>
                        <span class="input-group-addon">
                            <span class="glyphicon glyphicon-calendar"></span>
                        </span>
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th class="col-md-2">
                            <i class="glyphicon glyphicon-ok"></i> 조치내용
                        </th>
                        <td colspan="3">
                            <textarea id="wMeasureCont" name="wMeasureCont" class="form-control"
                                      placeholder="조치내용을 입력하세요." rows="15"></textarea>
                            <input type="hidden" id="wMea_seq" name="wMea_seq">
                        </td>
                    </tr>
                    <tr>
                        <th class="col-md-2">
                            <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;작업 전 사진
                        </th>
                        <td colspan="3">
                            <input type="file" id="wBeforeFiles" name="wBeforeFiles" accept="image/*" style="display: none;">
                            <span id="wbFileName"></span>
                            <button id="wbUpload">파일선택</button>


                        </td>
                    </tr>
                    <tr>
                        <th class="col-md-2">
                            <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;작업 후 사진
                        </th>
                        <td colspan="3">
                            <input type="file" id="wAfterFiles" name="wAfterFiles" accept="image/*" style="display: none;">
                            <span id="waFileName"></span>
                            <button id="waUpload">파일선택</button>
                        </td>
                    </tr>
                </table>
            </div>
        </div>
    </div>
</div>
</div>

</div>


<script type="text/javascript" charset="utf-8">
    <!--
    var authCrud = ${authCrud};
    var grpList = ${grpList};
    var disOrderTypeList = ${disOrderTypeList};
    var workTypeList = ${workTypeList};
    var workStateList = ${workStateList};
    var meaTypeList = ${meaTypeList};
    var lastSel;
    //-->
</script>