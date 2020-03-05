<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">일일보고서</a></li>
  <li class="active">일일점검현황</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">일일점검현황 <small>매일 발생하는 장비의 현황(상태) 정보 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-8 form-inline">
        <div class="form-group">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 기간</label>
          <div class="input-group input-daterange">
            <input type="text" id="srcSDate" name="srcSDate" class="form-control" msg="기간을" placeholder="시작일을 선택하세요" readonly="readonly" />
            <span class="input-group-addon">~</span>
            <input type="text" id="srcEDate" name="srcEDate" class="form-control" msg="기간을" placeholder="종료일을 선택하세요" readonly="readonly" />
          </div>
        </div>
      </div>
      <div class="col-md-4">
        <div class="pull-right">
          <button type="button" id="btnDetailReportForm" class="btn btn-sm btn-success"><i class="fa fa-download icon-white"></i> 엑셀양식 다운로드</button>
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
    <!-- begin 점검현황 리스트 panel -->
    <div id="reportPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group-custom pull-right">
          <button id="btnSync" class="btn btn-success btn-sm"><i class="fa fa-refresh"></i> 전체요약정보 동기화</button>
          <button id="btnAdd" class="btn btn-warning btn-sm m-l-3" disabled="true"><i class="fa fa-plus icon-white"></i> 추가</button>
        </div>
        <h4 class="panel-title">점검현황</h4>
      </div>
      <table id="reportList"></table>
      <div id="reportPager"></div>
    </div>
    <!-- end 점검현황 리스트 panel -->
  </div>
  <div class="col-md-7">
    <!-- begin 점검요약정보 리스트 panel -->
    <div id="summaryPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group pull-right">
          <button id="btnApply" class="btn btn-warning btn-sm" disabled="true"><i class="glyphicon glyphicon-ok icon-white"></i> 적용</button>
        </div>
        <h4 class="panel-title">점검요약정보</h4>
      </div>
      <table id="summaryList"></table>
      <div id="summaryPager"></div>
    </div>
    <!-- end 점검요약정보 리스트 panel -->
  </div>
</div>
<div class="row">
  <div class="col-md-12">
    <!-- begin 장비별 세부정보 리스트 panel -->
    <div id="detailPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group pull-right">
          <form id="fileupload" action="#" method="POST" enctype="multipart/form-data">
            <div class="fileupload-buttonbar">
              <span id="btnFiles" class="btn btn-warning btn-sm fileinput-button" disabled="true">
                <i class="fa fa-upload"></i>
                <span>엑셀 일괄등록</span>
                <input type="file" id="files" name="files" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" disabled="true">
              </span>
              <button type="button" id="btnDetailReport" class="btn btn-sm btn-primary" disabled="true"><i class="fa fa-download icon-white"></i> 엑셀 내보내기</button>
            </div>
          </form>
        </div>
        <h4 class="panel-title">장비별 세부정보</h4>
      </div>
      <table id="detailList"></table>
      <div id="detailPager"></div>
    </div>
    <!-- end 장비별 세부정보 리스트 panel -->
  </div>
</div>

<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};

  var lastSel; // 점검현황 그리드에서 선택된 rowID
  var summLastSel; // 점검요약정보 그리드에서 선택된 rowID

  var isAddState = false; // 점검현황 추가 버튼 클릭 시 상태 체크
  var excelInterId; // 엑셀 export 완료 체크를 위한 interval id
  //-->
</script>