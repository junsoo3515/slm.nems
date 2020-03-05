<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">시스템관리</a></li>
  <li class="active">사용자계정관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">사용자계정관리 <small>시스템에 접근할 수 있는 사용자의 계정을 관리</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-10 form-inline">
        <div class="form-group m-r-25">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 권한</label>
          <select id="srcAuth" name="srcAuth" class="form-control"></select>
        </div>
        <div class="form-group">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 아이디/이름/이메일</label>
          <input type="text" id="srcNm" name="srcNm" class="form-control">
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

<!-- begin 사용자계정 리스트 panel -->
<div id="userPanel" class="panel panel-inverse">
  <div class="panel-heading">
    <h4 class="panel-title">사용자계정 관리</h4>
  </div>
  <table id="userList"></table>
  <div id="userPager"></div>
</div>
<!-- end 사용자계정 리스트 panel -->

<!-- begin 사용자계정 정보 tab header -->
<ul class="nav nav-tabs">
  <li class="active"><a href="#infoPanel" data-toggle="tab">사용자계정 정보</a></li>
  <div class="pull-right">
    <button id="btnReg" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
    <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
  </div>
</ul>
<!-- end 사용자계정 정보 tab header -->

<div class="tab-content">
  <div id="infoPanel" class="tab-pane active">

    <div class="profile-container">
      <div class="profile-section">
        <form id="fileupload" action="#" method="POST" enctype="multipart/form-data">
        <div class="profile-left">
          <!-- begin profile-image -->
          <div id="profile-image" class="profile-image">
          </div>
          <!-- end profile-image -->
          <div class="row fileupload-buttonbar">
            <div class="m-b-10">
              <span class="btn btn-warning btn-block btn-sm fileinput-button">
                <i class="fa fa-plus"></i>
                <span>사진 변경</span>
                <input type="file" id="files" name="files" accept="image/*">
              </span>
            </div>
          </div>
        </div>
        </form>
        <div class="profile-right">
          <table class="table table-bordered">
            <tr>
              <th class="col-md-2">
                <i class="glyphicon glyphicon-ok"></i> 아이디
              </th>
              <td class="col-md-4">
                <div class="form-inline">
                  <input type="hidden" id="wUserIDChk" name="wUserIDChk"/>
                  <input id="wUserID" name="wUserID" type="text" class="form-control" msg="아이디를" isEngDigitOnly="1" placeholder="아이디를 입력 후 중복확인 하시기 바랍니다.">
                  <button class="btn btn-success m-l-5" id="btnUserChk" name="btnUserChk"><i class="fa fa-check-circle-o"></i> 중복확인</button>
                </div>
              </td>
              <th class="col-md-2">
                <i class="glyphicon glyphicon-ok"></i> 사용여부
              </th>
              <td class="col-md-4">
                <div class="form-inline">
                  <select id="wUseFl" name="wUseFl" class="form-control">
                    <option value="Y">사용</option>
                    <option value="N">중지</option>
                  </select>
                  <button class="btn btn-success m-l-5" id="btnPwdClear" name="btnPwdClear"><i class="fa fa-refresh"></i> 로그인 실패횟수 초기화</button>
                </div>
              </td>
            </tr>
            <tr>
            </tr>
            <tr>
              <th class="col-md-2">
                <i class="glyphicon glyphicon-ok"></i> 비밀번호
              </th>
              <td>
                <div class="row">
                  <div class="col-md-8">
                    <input type="password" id="wPwd0" name="wPwd0" class="form-control" minlen="8" maxlen="15">
                  </div>
                </div>
              </td>
              <th>
                <i class="glyphicon glyphicon-ok"></i> 비밀번호 확인
              </th>
              <td>
                <div class="row">
                  <div class="col-md-8">
                    <input type="password" id="wPwd1" name="wPwd1" class="form-control"minlen="8" maxlen="15">
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <th>
                <i class="glyphicon glyphicon-ok icon-white"></i> 권한
              </th>
              <td colspan="3">
                <div class="row">
                  <div class="col-md-3">
                    <select id="wAuth" name="wAuth" class="form-control" msg="권한을"></select>
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <th>
                <i class="glyphicon glyphicon-ok"></i> 이름
              </th>
              <td>
                <div class="row">
                  <div class="col-md-8">
                    <input id="wNm" name="wNm" type="text" class="form-control" maxlen="50" msg="이름을" placeholder="이름을 입력하세요.">
                  </div>
                </div>
              </td>
              <th>
                <i class="glyphicon glyphicon-ok"></i> E-Mail
              </th>
              <td>
                <div class="row">
                  <div class="col-md-8">
                    <input id="wEmail" name="wEmail" type="text" class="form-control" msg="E-Mail을" isEmail="isEmail" required="required" placeholder="E-Mail을 입력하세요.">
                  </div>
                </div>
              </td>

            </tr>
            <tr>
              <th>
                <i class="glyphicon glyphicon-ok"></i> 연락처
              </th>
              <td>
                <div class="row">
                  <div class="col-md-8">
                    <input id="wTelOffice" name="wTelOffice" type="text" class="form-control" msg="연락처를" isTel="isTel" pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}" placeholder="000-0000-0000">
                  </div>
                </div>
              </td>
              <th>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;핸드폰
              </th>
              <td>
                <div class="row">
                  <div class="col-md-8">
                    <input id="wTelHp" name="wTelHp" type="text" class="form-control" isTel="isTel" pattern="[0-9]{3}-[0-9]{4}-[0-9]{4}" placeholder="000-0000-0000">
                  </div>
                </div>
              </td>
            </tr>
            <tr>
              <th>
                &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;비고
              </th>
              <td colspan="3">
                <textarea id="wEtc" name="wEtc" class="form-control" placeholder="비고를 입력하세요." rows="5"></textarea>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>

    <div id="authPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <h4 class="panel-title">사용자 메뉴 접근 관리</h4>
      </div>
      <table id="authList"></table>
      <div id="authPager"></div>
    </div>
  </div>
</div>

<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  var authList = ${authList};

  var isAuthDataLoad = true; // 권한 그리드 데이터 요청을 위한 상태 값
  //-->
</script>