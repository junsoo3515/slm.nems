<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">시스템 관리</a></li>
  <li class="active">보고서 코드 관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">보고서코드관리 <small>SLM을 사용하기위한 보고서 코드에 대한 설정 정보를 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-10 form-inline">
        <div class="form-group m-r-50">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 검색어</label>
          <input type="text" id="srcWord" name="srcWord" class="form-control">
        </div>
      </div>
      <div class="col-md-2">
        <div class="pull-right">
          <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색</button>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- end 검색 폼 panel -->

<div class="row">
  <div class="col-md-8">
    <!-- begin 보고서 코드 관리 리스트 panel -->
    <div id="reportCodePanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group-custom pull-right">
          <button id="btnAdd1" class="btn btn-primary btn-sm"><i
                  class="fa fa-plus icon-white"></i> 추가
          </button>
        </div>
        <h4 class="panel-title">보고서 코드 관리</h4>
      </div>
      <table id="reportCodeList"></table>
      <div id="reportCodePager"></div>
    </div>
    <!-- end 보고서 코드 관리 리스트 panel -->
  </div>
  <div class="col-md-4">
    <!-- begin 장비연계 리스트 panel -->
    <div id="reportEquipMapPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group-custom pull-right">
        <button id="btnAdd2" class="btn btn-primary btn-sm"><i
                class="fa fa-plus icon-white"></i> 추가
        </button>
      </div>

        <h4 class="panel-title">장비연계</h4>
      </div>
      <table id="reportEquipMapList"></table>
      <div id="reportEquipMapPager"></div>
    </div>
    <!-- end 장비연계 리스트 panel -->
  </div>
</div>
<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  var lastSel;
  var lastSel2;
  var higRptCdList = ${higRpgCdList};
  //-->
</script>
