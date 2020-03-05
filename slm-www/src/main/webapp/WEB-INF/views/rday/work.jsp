<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">일일보고서</a></li>
  <li class="active">일일작업내역</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">일일작업내역 <small>일일 보고서에서 사용되는 "장애/요청/정책" 사항의 요약내용 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-10 form-inline">
        <div class="form-group m-r-30">
          <div class="form-group">
            <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 기간</label>
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
          <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색</button>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- end 검색 폼 panel -->
<div class="row">
  <div class="col-md-4">
<!-- begin 일일작업내역 리스트 panel -->
    <div id="workPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <h4 class="panel-title">일일작업내역</h4>
      </div>
      <table id="workList"></table>
      <div id="workPager"></div>
    </div>
  </div>
<!-- end 일일작업내역 리스트 panel -->
  <div class="col-md-8">
    <!-- begin 일일작업내역 정보 tab header -->
    <ul class="nav nav-tabs">
      <li class="active"><a href="#infoPanel" data-toggle="tab">작업 내역</a></li>
      <div class="pull-right">
        <button id="btnReg" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
        <button id="btnDel" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 삭제</button>
        <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
      </div>
    </ul>
    <!-- end 일일작업내역 정보 tab header -->

    <div class="tab-content">
      <div id="infoPanel" class="tab-pane active">
        <table class="table table-bordered">
          <tr>
            <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 제목
            </th>
            <td class="col-md-10">
                <input type="text" id="wTitle" name="wTitle" class="form-control" msg="제목을"  placeholder="제목을 입력하시기 바랍니다.">
                <input type="hidden" id="wRec_seq" name="wRec_seq" >
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 발생일자
            </th>
            <td colspan="3" class="col-md-10">
              <div class='input-group date' id='occurDateTimePicker'>
                <input type='text' id="wOccur_dt" name="wOccur_dt" msg="발생일자를" class="form-control" />
                    <span class="input-group-addon">
                        <span class="glyphicon glyphicon-calendar"></span>
                    </span>
              </div>
            </td>
          </tr>

          <tr>
            <th class="col-md-2">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;운영업무
            </th>
            <td colspan="3" class="col-md-10">
              <textarea id="wOp_issue" name="wOp_issue" class="form-control" placeholder="운영업무를 입력하세요." rows="11"></textarea>
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;요청사항
            </th>
            <td colspan="3" class="col-md-10">
              <textarea id="wReq_issue" name="wReq_issue" class="form-control" placeholder="요청사항을 입력하세요." rows="11"></textarea>
            </td>
          </tr>
          <tr>
            <th class="col-md-2">
              &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;정책추가
            </th>
            <td colspan="3" class="col-md-10">
              <textarea id="wPolicy_issue" name="wPolicy_issue" class="form-control" placeholder="정책추가를 입력하세요." rows="11"></textarea>
            </td>
          </tr>
        </table>
      </div>
    </div>
  </div>
</div>

<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  //-->
</script>