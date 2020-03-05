<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">SLA보고서</a></li>
  <li class="active">보고서 관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">보고서 관리 <small>일일 점검현황 결과 데이터를 이용한 SLA 보고서 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-10 form-inline">
        <div class="form-group">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 보고일 기간</label>
          <div class="input-group input-daterange">
            <input type="text" id="srcSDate" name="srcSDate" class="form-control" msg="기간을" placeholder="시작일을 선택하세요" readonly="readonly" />
            <span class="input-group-addon">~</span>
            <input type="text" id="srcEDate" name="srcEDate" class="form-control" msg="기간을" placeholder="종료일을 선택하세요" readonly="readonly" />
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
  <div class="col-md-5">
    <!-- begin SLA 보고서 목록 panel -->
    <div id="reportPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <h4 class="panel-title">SLA 보고서 목록</h4>
      </div>
      <table id="reportList"></table>
      <div id="reportPager"></div>
    </div>
    <!-- end SLA 보고서 목록 panel -->
  </div>
  <div class="col-md-7">
    <!-- begin tab header -->
    <ul class="nav nav-tabs" id="myTabs">
      <li class="active"><a href="#infoPanel" data-toggle="tab">SLA 보고서</a></li>
      <li><a href="#resPanel" data-toggle="tab">평가결과</a></li>
      <div class="pull-right">
        <button id="btnSave" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
        <button id="btnDel" class="btn btn-primary" disabled="true"><i class="glyphicon glyphicon-trash icon-white"></i>삭제</button>
        <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
      </div>
    </ul>
    <!-- end tab header -->

    <div class="tab-content">
      <div id="infoPanel" class="tab-pane active">

        <legend>보고서 생성 기본정보</legend>

        <table class="table table-bordered">
          <tr>
            <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 보고일
            </th>
            <td class="col-md-10">
              <div class="form-inline">
                <div id='wBriefDtGrp' class='input-group date'>
                  <input type='text' id="wBriefDt" name="wBriefDt" msg="보고일을" class="form-control" readonly="readonly" />
                  <span class="input-group-addon">
                      <i class="fa fa-calendar"></i>
                  </span>
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 기간
            </th>
            <td class="col-md-10">
              <div class="form-inline">
                <div class="input-group input-daterange">
                  <input type="text" id="wSDate" name="wSDate" class="form-control" msg="기간을" placeholder="시작일을 선택하세요" readonly="readonly" />
                  <span class="input-group-addon">~</span>
                  <input type="text" id="wEDate" name="wEDate" class="form-control" msg="기간을" placeholder="종료일을 선택하세요" readonly="readonly" />
                </div>
              </div>
            </td>
          </tr>
          <tr>
            <th class="col-md-2">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;월간휴무일</th>
            <td class="col-md-10">
              <div id='wRemoveDtGrp' class='input-group date'>
                <input type='text' id="wRemoveDt" name="wRemoveDt" class="form-control" readonly="readonly" />
                <span class="input-group-addon">
                    <i class="fa fa-calendar"></i>
                </span>
              </div>
            </td>
          </tr>
        </table>

        <div class="row">
          <div class="col-md-6">
            <legend>백업 성공율 정보</legend>

            <table class="table table-bordered">
              <tr>
                <th class="col-md-4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;총 기간</th>
                <td class="col-md-8">
                  <div class="form-inline">
                    <div class="input-group">
                      <input type='number' id="wTotTime" name="wTotTime" min="0" class="form-control" placeholder="총 기간(시간)을 입력하세요." />
                      <span class="input-group-addon">시간</span>
                    </div>
                  </div>
                </td>
              </tr>
              <tr>
                <th class="col-md-4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;계획수량</th>
                <td class="col-md-8">
                  <input type='number' id="wPlanCnt" name="wPlanCnt" min="0" class="form-control" placeholder="계획수량을 입력하세요." />
                </td>
              </tr>
              <tr>
                <th class="col-md-4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;백업수량</th>
                <td class="col-md-8">
                  <div class="input-group">
                    <span class="input-group-addon">성공</span>
                    <input type='number' id="wSucCnt" name="wSucCnt" min="0" class="form-control" placeholder="성공수량을 입력하세요." />
                  </div>
                  <div class="input-group">
                    <span class="input-group-addon">실패</span>
                    <input type='number' id="wFailCnt" name="wFailCnt" min="0" class="form-control" placeholder="실패수량을 입력하세요." />
                  </div>
                  <div class="input-group">
                    <span class="input-group-addon">중단</span>
                    <input type='number' id="wStopCnt" name="wStopCnt" min="0" class="form-control" placeholder="중단수량을 입력하세요." />
                  </div>
                </td>
              </tr>
              <tr>
                <th class="col-md-4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;복구수량</th>
                <td class="col-md-8">
                  <div class="input-group">
                    <span class="input-group-addon">계획</span>
                    <input type='number' id="wResPlanCnt" name="wResPlanCnt" min="0" class="form-control" placeholder="복구계획 수량을 입력하세요." />
                  </div>
                  <div class="input-group">
                    <span class="input-group-addon">성공</span>
                    <input type='number' id="wResSucCnt" name="wResSucCnt" min="0" class="form-control" placeholder="복구성공 수량을 입력하세요." />
                  </div>
                </td>
              </tr>
              <tr>
                <th class="col-md-4">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;비고</th>
                <td class="col-md-8">
                  <textarea id="wRemark" name="wRemark" class="form-control" placeholder="비고를 입력하세요." rows="4"></textarea>
                </td>
              </tr>
            </table>
          </div>
          <div id="bwPanel" class="col-md-6">
            <legend>대역폭 사용률 정보</legend>

            <table id="bwList"></table>
            <div id="bwPager"></div>
          </div>
        </div>
      </div>
      <div id="resPanel" class="tab-pane">
        <div id="resSummPanel" class="row">
          <div class="col-md-9">
            <div class="row">
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
        <!-- begin SLA 서비스 평가결과 리스트 panel -->
        <div id="resGridPanel" class="panel panel-inverse">
          <div class="panel-heading">
            <h4 class="panel-title">SLA 서비스 평가결과</h4>
          </div>
          <table id="resGridList"></table>
          <div id="resGridPager"></div>
        </div>
        <!-- end SLA 서비스 평가결과 리스트 panel -->
      </div>
    </div>
  </div>
</div>

<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  //-->
</script>