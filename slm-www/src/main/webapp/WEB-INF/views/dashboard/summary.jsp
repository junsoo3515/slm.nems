<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
    <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
    <li><a href="javascript:;">Dash 보드</a></li>
    <li class="active">요약</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">Dash 보드
    <small>SLA 평가 결과 및 점검현황에 대한 요약 기능 제공</small>
</h1>
<!-- end page-header -->
<div id="resPanel" class="tab-pane">
    <div id="resSummPanel" class="row">
        <div class="col-md-12">
            <div class="operate">
                <strong style="font-size: 15px;">운영지수</strong>
                <table class="table table-bordered" style="margin-bottom: 0; margin-top: 5px;">
                    <tr id="tr">
                        <td id="index1" class="col-md-1" style="background-color: #ff4f4f; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index2" class="col-md-1" style="background-color: #ff9347; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index3" class="col-md-1" style="background-color: #ffb245; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index4" class="col-md-1" style="background-color: #ffc846; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index5" class="col-md-1" style="background-color: #ffdb49; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index6" class="col-md-1" style="background-color: #eeeb4a; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index7" class="col-md-1" style="background-color: #ccfb4a; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index8" class="col-md-1" style="background-color: #a9fe58; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index9" class="col-md-1" style="background-color: #89eb90; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                        <td id="index10" class="col-md-1" style="background-color: #5dbcff; text-align: center; color: #fff; font-weight: bold; font-size: 15px;"></td>
                    </tr>
                </table>
                <ol style="padding-left: 0px; padding-bottom: 25px;">
                    <li id="operateLeft" class="left" style="float: left; text-align: left; color:#ff4f4f; width: 33%; overflow: hidden; font-weight: bold; font-size: 15px;">불량</li>
                    <li id="operateCent" class="cent" style="float: left; text-align: center; color: #eeeb4a; width: 33%; overflow: hidden; font-weight: bold; font-size: 15px;">보통</li>
                    <li id="operateRight" class="right" style="float: left; text-align: right; color: #5dbcff; width: 33%; overflow: hidden; font-weight: bold; font-size: 15px;">양호</li>
                </ol>
            </div>
        </div>
        <div class="col-md-9">
            <div class="row">
                <div class="col-md-12">
                </div>
                <div class="col-md-3">
                    <div class="widget widget-stats bg-green" style="margin-bottom:10px;">
                        <div class="stats-icon"><i class="fa fa-dot-circle-o"></i></div>
                        <div class="stats-info">
                            <h4>적시성 관리</h4>

                            <p id="summText0">-</p>
                        </div>
                        <div class="stats-link"><a href="#"></a></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="widget widget-stats bg-blue" style="margin-bottom:10px;">
                        <div class="stats-icon"><i class="fa fa-retweet"></i></div>
                        <div class="stats-info">
                            <h4>가용성 관리</h4>

                            <p id="summText1">-</p>
                        </div>
                        <div class="stats-link"><a href="#"></a></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="widget widget-stats bg-purple" style="margin-bottom:10px;">
                        <div class="stats-icon"><i class="fa fa-cloud"></i></div>
                        <div class="stats-info">
                            <h4>생산성 및 보안 관리</h4>

                            <p id="summText2">-</p>
                        </div>
                        <div class="stats-link"><a href="#"></a></div>
                    </div>
                </div>
                <div class="col-md-3">
                    <div class="widget widget-stats bg-black" style="margin-bottom:10px;">
                        <div class="stats-icon"><i class="fa fa-child"></i></div>
                        <div class="stats-info">
                            <h4>일반 관리</h4>

                            <p id="summText3">-</p>
                        </div>
                        <div class="stats-link"><a href="#"></a></div>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-3">
            <div class="widget widget-stats bg-red" style="margin-bottom:10px;">
                <div class="stats-icon"><i class="fa fa-thumb-tack"></i></div>
                <div class="stats-info">
                    <h4>총점</h4>

                    <p id="summTotText">-</p>
                </div>
                <div class="stats-link"><a href="#"></a></div>
            </div>
        </div>
    </div>
</div>


<div id="slaPanel" class="panel panel-inverse">
    <div class="panel-heading">
        <div class="row">
            <div class="form-inline">
                <div class="col-md-4">
                    <label class="panel-title"> 기간별 SLA 트랜드</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                <div class="col-md-8">
                    <div class="pull-right">
                        <div class="input-group input-daterange">
                            <input type="text" id="srcSDate" name="srcSDate" class="form-control" msg="기간을"
                                   placeholder="시작일을 선택하세요" readonly="readonly"/>
                            <span class="input-group-addon">~</span>
                            <input type="text" id="srcEDate" name="srcEDate" class="form-control" msg="기간을"
                                   placeholder="종료일을 선택하세요" readonly="readonly"/>
                        </div>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="panel-body">
        <div class="col-md-8">
            <div id="container" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
        </div>
        <div id="slaGridPanel" class="col-md-4">
            <table id="slaList"></table>
            <div id="slaPager"></div>
        </div>
    </div>
</div>
<div id="dailyPanel" class="panel panel-inverse">
    <div class="panel-heading">
        <div class="row">
            <div class="form-inline">
                <div class="col-md-4">
                    <label class="panel-title"> 기간별 점검현황(정상항목 비율 : %)</label>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
                </div>
                <div class="col-md-8">
                    <div class="pull-right">
                        <div class="input-group input-daterange">
                            <input type="text" id="srcSDate2" name="srcSDate2" class="form-control" msg="기간을"
                                   placeholder="시작일을 선택하세요" readonly="readonly"/>
                            <span class="input-group-addon">~</span>
                            <input type="text" id="srcEDate2" name="srcEDate2" class="form-control" msg="기간을"
                                   placeholder="종료일을 선택하세요" readonly="readonly"/>
                        </div>
                        &nbsp;&nbsp;&nbsp;&nbsp;
                        <button id="btnSrch2" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색
                        </button>
                    </div>
                </div>
            </div>
        </div>
    </div>
    <div class="panel-body">
        <div class="col-md-8">
            <div id="dailyContainer" style="min-width: 310px; height: 400px; margin: 0 auto"></div>
        </div>
        <div id="dailyGridPanel" class="col-md-4">
            <table id="dailyList"></table>
            <div id="dailyPager"></div>
        </div>
    </div>


</div>


<script type="text/javascript" charset="utf-8">
    <!--
    var member_nm = '${member_nm}';
    var authCrud = ${authCrud};
    //-->
</script>