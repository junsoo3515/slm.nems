<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
    <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
    <li><a href="javascript:;">월간보고서</a></li>
    <li class="active">월간주요현황</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">월간주요현황 <small>일일 점검현황 결과 데이터를 이용한 월간 보고서 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
    <div class="panel-body">
        <div class="row">
            <div class="col-md-10 form-inline">
                <div class="form-group m-r-30">
                    <div class="form-group">
                        <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 보고일기간</label>
                        <div class="input-group input-daterange">
                            <input type="text" id="srcSDate" name="srcSDate" class="form-control" msg="기간을" placeholder="시작일을 선택하세요" readonly="readonly" />
                            <span class="input-group-addon">~</span>
                            <input type="text" id="srcEDate" name="srcEDate" class="form-control" msg="기간을" placeholder="종료일을 선택하세요" readonly="readonly" />
                        </div>
                    </div>
                </div>
            </div>
            <div class="col-md-2">
                <div class="pull-right">
                    <button id="btnReport" class="btn btn-sm btn-primary"><i class="fa fa-download icon-white"></i> 보고서 내보내기</button>
                    <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색</button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- end 검색 폼 panel -->
<div class="row">
    <div class="col-md-4">
        <!-- begin 월간주요현황 리스트 panel -->
        <div id="reportPanel" class="panel panel-inverse">
            <div class="panel-heading">
                <h4 class="panel-title">월간주요현황목록</h4>
            </div>
            <table id="reportList"></table>
            <div id="reportPager"></div>
        </div>
        <!-- end 권한 리스트 panel -->
    </div>
    <div class="col-md-8">
        <!-- begin 월간주요현황 정보 tab header -->
        <ul class="nav nav-tabs">
            <li class="active"><a href="#infoPanel" data-toggle="tab" >주요현황</a></li>
            <li class=""><a href="#checkPanel" data-toggle="tab" id="checktab" >점검현황</a></li>
            <li class=""><a href="#troublePanel" data-toggle="tab" id="troubletab" >장애현황</a></li>
            <li class=""><a href="#troubleDetailPanel" data-toggle="tab" id="troubleDetailtab" >장애세부내역</a></li>
            <div class="pull-right">
                <button id="btnReg" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
                <button id="btnDel" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 삭제</button>
                <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
            </div>
        </ul>
        <!-- end 권한 정보 tab header -->


        <div class="tab-content">
            <div id="infoPanel" class="tab-pane active">
                <table class="table table-bordered">
                    <tr>
                        <th class="col-md-2">
                            <i class="glyphicon glyphicon-ok"></i> 보고일
                        </th>
                        <td class="col-md-10">
                          <div class="form-inline">
                            <div class='input-group date' id='briefDateTimePicker'>
                                <input type='text' id="wBrief_dt" name="wBrief_dt" msg="보고일을" class="form-control" />
                                 <span class="input-group-addon">
                                     <span class="glyphicon glyphicon-calendar"></span>
                                 </span>
                            </div>
                          </div>
                        </td>
                    </tr>
                    <tr>
                        <th class="col-md-2">
                            <i class="glyphicon glyphicon-ok"></i> 기간
                        </th>
                        <td colspan="3" class="col-md-10">
                            <div class="input-group input-daterange">
                                <input type="text" id="wStart_dt" name="wStart_dt" class="form-control" msg="기간을" placeholder="시작일을 선택하세요" readonly="readonly" />
                                <span class="input-group-addon">~</span>
                                <input type="text" id="wEnd_dt" name="wEnd_dt" class="form-control" msg="기간을" placeholder="종료일을 선택하세요" readonly="readonly" />
                            </div>
                        </td>
                    </tr>
                    <tr>
                        <th>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;실적
                        </th>
                        <td colspan="3" class="col-md-10">
                            <textarea id="wPfmc" name="wPfmc" class="form-control" placeholder="실적을 입력하세요." rows="19"></textarea>
                            <input type="hidden" id="wDa_seq" name="wDa_seq" >
                        </td>
                    </tr>
                    <tr>
                        <th>
                            &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;계획
                        </th>
                        <td colspan="3" class="col-md-10">
                            <textarea id="wPlan" name="wPlan" class="form-control" placeholder="계획을 입력하세요." rows="19"></textarea>
                        </td>
                    </tr>

                </table>
            </div>
            <div  id="checkPanel" class="tab-pane fade">
                <div class="row">
                    <div class="col-md-4">
                        <!-- begin 월간주요현황 장애및 중단시간 panel -->
                        <div id="updownPanel" class="panel panel-inverse">
                            <div class="panel-heading">
                                <div class="btn-group pull-right">
                                    <span class="btn btn-primary btn-xs m-r-5" id="wMonthDay" name="wMonthDay"></span>
                                </div>
                                <h4 class="panel-title">총 장애/서비스 중단 시간</h4>
                            </div>
                            <table class="table table-bordered">
                                <div class="col-md-6">
                                    <h3 id="wError_time_sum" name="wError_time_sum"></h3><i id="disorderupdown" >&nbsp;장애시간</i>
                                </div>
                                <div class="col-md-6">
                                    <h3 id="wHold_time_sum" name="wHold_time_sum"></h3><i id="holdupdown">&nbsp;서비스 중단 시간</i>
                                </div>
                            </table>
                        </div>
                    </div>


                    <div class="col-md-2 col-sm-2">
                        <div class="widget widget-stats bg-green">
                            <div class="stats-icon"><i class="fa fa-1x fa-exclamation-triangle"></i></div>
                            <div class="stats-info">
                                <h4>장애건수</h4>
                                <p id="wCnt_err_sum" name="wCnt_err_sum"></p>
                            </div>
                            <div class="stats-link">
                                <a href="javascript:;"></a>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 col-sm-2">
                        <div class="widget widget-stats bg-blue">
                            <div class="stats-icon"><i class="fa fa-1x fa-cloud"></i></div>
                            <div class="stats-info">
                                <h4>외부요인</h4>
                                <p id="wCnt_out_sum" name="wCnt_out_sum"></p>
                            </div>
                            <div class="stats-link">
                                <a href="javascript:;"></a>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 col-sm-2">
                        <div class="widget widget-stats bg-purple">
                            <div class="stats-icon"><i class="fa fa-1x fa-retweet"></i></div>
                            <div class="stats-info">
                                <h4>내부요인</h4>
                                <p id="wCnt_inn_sum" name="wCnt_inn_sum"></p>
                            </div>
                            <div class="stats-link">
                                <a href="javascript:;"></a>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-2 col-sm-2">
                        <div class="widget widget-stats bg-red">
                            <div class="stats-icon"><i class="fa fa-1x fa-check"></i></div>
                            <div class="stats-info">
                                <h4>조치완료</h4>
                                <p id="wCnt_fin_sum" name="wCnt_fin_sum"></p>
                            </div>
                            <div class="stats-link">
                                <a href="javascript:;"></a>
                            </div>
                        </div>
                    </div>
                    <div  class="col-md-12">
                        <div id="checkGridPanel" class="panel panel-inverse">
                            <div class="panel-heading">
                                <h4 class="panel-title">월간 점검 현황</h4>
                            </div>
                            <table id="checkGridList"></table>
                            <div id="checkGridPager"></div>
                        </div>
                    </div>
                </div>
            </div>
            <div id="troublePanel" class="tab-pane fade">
                <div class="row">
                    <!-- begin 월간주요현황 장애분포도  panel -->
                    <div class="col-md-12">
                        <div id="troubleChartPanel" class="panel panel-inverse">
                            <div class="panel-heading">
                                <h4 class="panel-title">장애분포도</h4>
                            </div>
                            <div id="troubleCharts"></div>
                        </div>
                    </div>
                    <!-- begin 월간주요현황 장애현황표 panel -->
                    <div class="col-md-12">
                        <div id="troubleGridPanel" class="panel panel-inverse">
                            <div class="panel-heading">
                                <h4 class="panel-title">장애현황표</h4>
                            </div>
                            <table id="troubleGridList"></table>
                        </div>
                    </div>
                </div>
            </div>

            <div  id="troubleDetailPanel" class="tab-pane fade">
                <div class="row">
                        <!-- begin 월간주요현황 장애세부내역 panel -->
                    <div  class="col-md-12">
                        <div id=" troubleDetailGridPanel" class="panel panel-inverse">
                            <div class="panel-heading">
                                <h4 class="panel-title">장애내역</h4>
                            </div>
                            <table id="troubleDetailGridList"></table>
                            <div id="troubleDetailGridPager"></div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>



</div>


<script type="text/javascript" charset="utf-8">
    <!--
    var member_nm = '${member_nm}';
    var authCrud = ${authCrud};
    var grpList = ${grpList};
    var reQuestTypeList = ${reQuestTypeList};
    //-->
</script>