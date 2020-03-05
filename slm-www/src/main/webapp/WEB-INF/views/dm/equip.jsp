<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">운영대상</a></li>
  <li class="active">장비관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">장비관리 <small>SLA 평가를 위한 운영중인 장비들의 기본 정보 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-8 form-inline">
        <div class="form-group m-r-25">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 장비유형</label>
          <select id="srcGrp" name="srcGrp" class="form-control"></select>
        </div>
        <div class="form-group">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 명칭</label>
          <input type="text" id="srcNm" name="srcNm" class="form-control">
        </div>
      </div>
      <div class="col-md-4">
        <div class="pull-right">
          <button type="button" id="btnDetailReportForm" class="btn btn-sm btn-success"><i class="fa fa-download icon-white"></i> 엑셀양식 다운로드</button>
          <button id="btnReport" class="btn btn-sm btn-primary"><i class="fa fa-download icon-white"></i> 엑셀 내보내기</button>
          <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색</button>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- end 검색 폼 panel -->

<!-- begin 장비관리 리스트 panel -->
<div id="equipPanel" class="panel panel-inverse">
  <div class="panel-heading">
    <div class="btn-group pull-right">
      <form id="fileupload" action="#" method="POST" enctype="multipart/form-data">
      <div class="fileupload-buttonbar">
        <span id="btnFiles" class="btn btn-warning btn-sm fileinput-button">
          <i class="fa fa-upload"></i>
          <span>엑셀 일괄등록</span>
          <input type="file" id="files" name="files" accept="application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
        </span>
      </div>
      </form>
    </div>
    <h4 class="panel-title">장비 관리</h4>
  </div>
  <table id="equipList"></table>
  <div id="equipPager"></div>
</div>
<!-- end 장비관리 리스트 panel -->

<!-- begin 장비관리 정보 tab header -->
<ul class="nav nav-tabs">
  <li class="active"><a href="#infoPanel" data-toggle="tab">기본정보</a></li>
  <div class="pull-right">
    <button id="btnSave" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
    <button id="btnDel" class="btn btn-primary" disabled="true"><i class="glyphicon glyphicon-trash icon-white"></i>삭제</button>
    <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
  </div>
</ul>
<!-- end 장비관리 정보 tab header -->

<div class="tab-content">
  <div id="infoPanel" class="tab-pane active">
    <div class="row">
      <div class="col-md-5">
        <table class="table table-bordered">
          <tr>
            <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 장비
            </th>
            <td class="col-md-10">
              <div class="form-inline">
                <select id="wGrp" name="wGrp" class="form-control" msg="장비유형을"></select>
                <input type="text" id="wNm" name="wNm" class="form-control" msg="장비명을" placeholder="장비명을 입력 하시기 바랍니다.">
              </div>
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 제품번호
            </th>
            <td class="col-md-10">
              <input type="text" id="wSerial" name="wSerial" class="form-control" msg="제품번호를" placeholder="제품번호를 입력하세요.">
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;보관장소
            </th>
            <td class="col-md-10">
              <input type="text" id="wStrArea" name="wStrArea" class="form-control" placeholder="보관장소를 입력하세요.">
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;비고
            </th>
            <td class="col-md-10">
              <textarea id="wRemark" name="wRemark" class="form-control" placeholder="비고를 입력하세요." rows="7"></textarea>
            </td>
          </tr>
        </table>
      </div>
      <div class="col-md-4">
        <div id="dataPanel" class="panel panel-inverse">
          <div class="panel-heading">
            <h4 class="panel-title">기본항목</h4>
          </div>
          <table id="dataList"></table>
          <div id="dataPager"></div>
        </div>
      </div>
      <div class="col-md-3">
        <div id="sparePanel" class="panel panel-inverse">
          <div class="panel-heading">
            <div class="btn-group pull-right">
              <button id="btnSpareAdd" class="btn btn-warning btn-sm" disabled="true"><i class="fa fa-plus icon-white"></i> 추가</button>
            </div>
            <h4 class="panel-title">예비품 현황관리</h4>
          </div>
          <table id="spareList"></table>
          <div id="sparePager"></div>
        </div>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  var grpList = ${grpList};

  var spareLastSel; // 예비품 현황관리 그리드에서 선택된 rowID
  var excelInterId; // 엑셀 export 완료 체크를 위한 interval id
  //-->
</script>