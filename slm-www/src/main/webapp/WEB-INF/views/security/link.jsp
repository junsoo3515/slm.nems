<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">시스템 관리</a></li>
  <li class="active">NMS 연계 관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">NMS연계관리 <small>NMS 데이터 수집 배치 프로그램을 통해 수집된 NMS 데이터 및 코드를 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-2 form-inline">
        <div class="form-group m-r-50">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 검색어</label>
          <input type="text" id="srcCode" name="srcCode" class="form-control">
        </div>
      </div>
      <div class="col-md-7 form-inline">
        <div class="form-group">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 기간</label>
          <div class="input-group input-daterange">
            <input type="text" id="srcSDate" name="srcSDate" class="form-control" msg="기간을" placeholder="시작일을 선택하세요" readonly="readonly" />
            <span class="input-group-addon">~</span>
            <input type="text" id="srcEDate" name="srcEDate" class="form-control" msg="기간을" placeholder="종료일을 선택하세요" readonly="readonly" />
          </div>
        </div>
      </div>
      <div class="col-md-3">
        <div class="pull-right">
          <button id="btnReport" class="btn btn-sm btn-primary" disabled="true"><i class="fa fa-download icon-white"></i> 엑셀 내보내기</button>
          <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색</button>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- end 검색 폼 panel -->

<div class="row">
  <div class="col-md-5">
    <!-- begin 연계시스템(NMS) 코드 리스트 panel -->
    <div id="nmsCodePanel" class="panel panel-inverse">
      <div class="panel-heading">
        <h4 class="panel-title">연계시스템(NMS) 코드</h4>
      </div>
      <table id="nmsCodeList"></table>
      <div id="nmsCodePager"></div>
    </div>
    <!-- end 연계시스템(NMS) 코드 리스트 panel -->
  </div>
  <div class="col-md-7">
    <!-- begin 연계시스템(NMS) 데이터 리스트 panel -->
    <div id="nmsDataPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <h4 class="panel-title">연계시스템(NMS) 데이터</h4>
      </div>
      <table id="nmsDataList"></table>
      <div id="nmsDataPager"></div>
    </div>
    <!-- end 연계시스템(NMS) 데이터 리스트 panel -->
  </div>
</div>
<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};

  var lastSel; // 연계시스템(NMS) 코드 그리드에서 선택된 rowID
  var lastSel2;
  var dataLastSel; // 연계시스템(NMS) 데이터 그리드에서 선택된 rowID

  var excelInterId; // 엑셀 export 완료 체크를 위한 interval id
  //-->
</script>
