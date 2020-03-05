<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
    <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
    <li><a href="javascript:;">시스템 관리</a></li>
    <li class="active">보고서 평가항목 관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">보고서평가항목관리
    <small>일일/월간 보고서 평가를 위한 설정 정보를 관리(보고서 평가 그룹/보고서 코드 매핑/항목별 평가기준)</small>
</h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
    <div class="panel-body">
        <div class="row">
            <div class="col-md-10 form-inline">
                <div class="form-group m-r-25">
                    <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 검색어</label>
                    <input type="text" id="srcWord" name="srcWord" class="form-control">
                </div>
            </div>
            <div class="col-md-2">
                <div class="pull-right">
                    <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- end 검색 폼 panel -->

<div class="row">
    <!-- begin 그룹관리 리스트 panel -->
    <div class="col-md-3">
        <div id="evalGroupPanel" class="panel panel-inverse">
            <div class="panel-heading">
                <div class="btn-group-custom pull-right">
                    <button id="btnAdd1" class="btn btn-primary btn-sm"><i
                            class="fa fa-plus icon-white"></i> 추가
                    </button>
                </div>
                <h4 class="panel-title">그룹관리</h4>
            </div>
            <table id="evalGroupList"></table>
            <div id="evalGroupPager"></div>
        </div>
        <!-- end 그룹관리 리스트 panel -->
    </div>

    <div class="col-md-9">
        <!-- begin 평가항목 관리 정보 tab header -->
        <ul class="nav nav-tabs" id="myTabs">
            <li class="active"><a href="#evalPanel" data-toggle="tab">평가항목관리</a></li>
            <li class=""><a href="#reportConnPanel" data-toggle="tab" id="reportConntab">보고서연결</a></li>
        </ul>
        <!-- end 평가항목 관리 정보 tab header -->

        <div class="tab-content">
            <div id="evalPanel" class="tab-pane active">
                <div class="row">
                    <div class="col-md-5">
                        <div id="evalItemPanel" class="panel panel-inverse">
                            <div class="nav panel-inverse">
                                <div class="panel-heading">
                                    <div class="btn-group-custom pull-right">
                                        <button id="btnAdd2" class="btn btn-primary btn-sm"><i
                                                class="fa fa-plus icon-white"></i> 추가
                                        </button>
                                    </div>
                                    <li class="panel-title">평가기준목록</li>
                                </div>
                            </div>
                            <table id="evalItemList"></table>
                            <div id="evalItemPager"></div>
                        </div>
                    </div>
                <div id="evalItemInfoPanel" class="panel panel-inverse">
                    <div class="col-md-7">
                        <div class="nav panel-inverse">
                            <div class="panel-heading">
                                <div class="btn-group-custom pull-right">
                                    <button id="btnReg" class="btn btn-primary btn-sm" disabled="true"><i
                                            class="glyphicon glyphicon-ok icon-white"></i> 등록
                                    </button>
                                    <button id="btnCancel" class="btn btn-danger btn-sm m-l-3" disabled="true"><i
                                            class="glyphicon glyphicon-ok icon-white"></i> 취소
                                    </button>
                                </div>
                                <li class="panel-title"><a href="#evalItemInfoPanel" data-toggle="tab">평가기준 입력</a></li>
                            </div>
                        </div>
                            <table class="table table-bordered">
                                <tr>
                                    <th class="col-md-3">
                                        <i class="glyphicon glyphicon-ok"></i> 코드
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="itm_cd" name="itm_cd" class="form-control" msg="코드를"
                                               maxlen="4" isEngDigitOnly="1">
                                    </td>
                                    <th class="col-md-3">
                                        <i class="glyphicon glyphicon-ok"></i> 그룹명
                                    </th>
                                    <td class="col-md-3">
                                        <div class="form-inline">
                                            <select id="grp_nm" name="grp_nm" msg="그룹명을" class="form-control"></select>
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="col-md-3">
                                        <i class="glyphicon glyphicon-ok"></i> 명칭
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="basis_nm" name="basis_nm" class="form-control" msg="명칭을">
                                    </td>
                                    <th class="col-md-3">
                                        <i></i>&nbsp수식코드
                                    </th>
                                    <td class="col-md-3">
                                        <div class="form-inline">
                                            <input type="text" id="eng_nm" name="eng_nm" class="form-control"
                                                   >
                                        </div>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="col-md-3">
                                        <i></i>&nbsp점검내용
                                    </th>
                                    <td colspan="3" class="col-md-9">
                                        <textarea id="cont" name="cont" class="form-control" placeholder="점검내용을 입력하세요."
                                                  rows="2"></textarea>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="col-md-3">
                                        <i></i>&nbsp점검대상
                                    </th>
                                    <td colspan="3" class="col-md-9">
                                        <input type="checkbox" id="chk_fl" name="chk_fl" value="Y"/>
                                    </td>
                                </tr>
                                <tr>
                                    <th class="col-md-3">
                                        <i></i>&nbsp양호범위
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="good" name="good" class="form-control">
                                    </td>
                                    <th class="col-md-3">
                                        <i></i>&nbsp양호수식
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="good_expr" name="good_expr" class="form-control">
                                    </td>
                                </tr>
                                <tr>
                                    <th class="col-md-3">
                                        <i></i>&nbsp주의범위
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="warning" name="warning" class="form-control">
                                    </td>
                                    <th class="col-md-3">
                                        <i></i>&nbsp주의수식
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="warning_expr" name="warning_expr" class="form-control">
                                    </td>
                                </tr>
                                <tr>
                                    <th class="col-md-3">
                                        <i></i>&nbsp이상범위
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="wrong" name="wrong" class="form-control">
                                    </td>
                                    <th class="col-md-3">
                                        <i></i>&nbsp이상수식
                                    </th>
                                    <td class="col-md-3">
                                        <input type="text" id="wrong_expr" name="wrong_expr" class="form-control">
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </div>
                    </div>
                </div>

            <div id="reportConnPanel" class="tab-pane fade">
                <div class="row">
                    <div class="col-md-12">
                        <div id="reportMapPanel" class="panel panel-inverse">
                            <div class="panel-heading">
                                <div class="btn-group-custom pull-right">
                                    <button id="btnAdd3" class="btn btn-primary btn-sm"><i
                                            class="fa fa-plus icon-white"></i> 추가
                                    </button>
                                </div>
                                <h4 class="panel-title">보고서연계</h4>
                            </div>
                            <table id="reportMapList"></table>
                            <div id="reportMapPager"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
<script type="text/javascript" charset="utf-8">
    <!--
    var authCrud = ${authCrud};
    var groupNmList = ${groupNmList};
    var lastSel;
    var lastSel2;
    var reportNmList = ${reportNmList};

    //-->
</script>
