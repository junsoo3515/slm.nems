<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">시스템관리</a></li>
  <li class="active">권한관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">권한관리 <small>시스템에 접근할 수 있는 사용자들의 권한을 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-10 form-inline">
        <div class="form-group m-r-25">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 권한명</label>
          <input type="text" id="srcAuth" name="srcAuth" class="form-control">
        </div>
        <div class="form-group">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 권한설명</label>
          <input type="text" id="srcEtc" name="srcEtc" class="form-control">
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

<!-- begin 권한 리스트 panel -->
<div id="authPanel" class="panel panel-inverse">
  <div class="panel-heading">
    <h4 class="panel-title">권한 리스트</h4>
  </div>
  <table id="authList"></table>
  <div id="authPager"></div>
</div>
<!-- end 권한 리스트 panel -->

<!-- begin 권한 정보 tab header -->
<ul class="nav nav-tabs">
  <li class="active"><a href="#infoPanel" data-toggle="tab">권한 정보</a></li>
  <div class="pull-right">
    <button id="btnReg" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
    <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
  </div>
</ul>
<!-- end 권한 정보 tab header -->

<div class="tab-content">
  <div id="infoPanel" class="tab-pane active">
    <table class="table table-bordered">
      <tr>
        <th class="col-md-2">
          <i class="glyphicon glyphicon-ok"></i> 권한코드
        </th>
        <td class="col-md-4">
          <div class="form-inline">
            <input type="hidden" id="wAuthCdChk" name="wAuthCdChk"/>
            <input type="text" id="wAuthCd" name="wAuthCd" class="form-control" msg="권한코드를" isEngOnly="1" placeholder="권한코드를 입력 후 중복확인 하시기 바랍니다.">
            <button class="btn btn-success m-l-5" id="btnAuthChk" name="btnAuthChk"><i class="fa fa-check-circle-o"></i> 중복확인</button>
          </div>
        </td>
        <th class="col-md-2">
          <i class="glyphicon glyphicon-ok"></i> 권한명
        </th>
        <td class="col-md-4">
          <input type="text" id="wAuthNm" name="wAuthNm" class="form-control" maxlen="20" msg="권한명을" placeholder="권한명을 입력하세요.">
        </td>
      </tr>
      <tr>
        <th class="col-md-2">
          <i class="glyphicon glyphicon-ok"></i> 권한설명
        </th>
        <td colspan="3" class="col-md-10">
          <input type="text" id="wAuthDesc" name="wAuthDesc" class="form-control" maxlen="50" msg="권한설명을" placeholder="권한설명을 입력하세요.">
        </td>
      </tr>
    </table>

    <div id="authMenuPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group pull-right">
          <button type="button" id="btnAllChk" class="btn btn-warning btn-sm"><i class="fa fa-check-circle-o icon-white"></i> 전체선택</button>
        </div>
        <h4 class="panel-title">권한 메뉴접근 관리</h4>
      </div>
      <table id="authMenuList"></table>
      <div id="authMenuPager"></div>
    </div>
  </div>
</div>

<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  //-->
</script>