<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li class="active">개인정보수정</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">개인정보수정 <small>로그인 한 운영자 본인의 개인정보 조회 / 수정</small></h1>
<!-- end page-header -->

<!-- begin 개인정보수정 tab header -->

<%--<div id="codePanel" class="panel panel-inverse">--%>
<div id="codePanel" class="nav panel-inverse">
  <div class="panel-heading">
    <div class="btn-group-custom pull-right">
      <button id="btnReg" class="btn btn-primary btn-sm"><i class="glyphicon glyphicon-ok icon-white"></i> 수정</button>
      <button id="btnCancel" class="btn btn-danger btn-sm m-l-3"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
    </div>
    <li class="panel-title"><a href="#infoPanel" data-toggle="tab">개인정보 수정</a></li>
  </div>
</div>


<!-- end 개인정보수정 tab header -->

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
                  <input id="wUserID" name="wUserID" type="text" class="form-control" disabled >
                </div>
              </td>
              <th class="col-md-2">
                <i class="glyphicon glyphicon-ok icon-white"></i> 권한
              </th>
              <td class="col-md-4">
                <div class="form-inline">
                  <input id="wAuth" name="wAuth" class="form-control" disabled />
                </div>
              </td>
            </tr>
            <tr>
              <th >
                <i class="glyphicon glyphicon-ok"></i> 비밀번호
              </th>
              <td >
                <div class="row">
                  <div class="col-md-8">
                    <input type="password" id="wPwd0" name="wPwd0" class="form-control"  minlen="8" maxlen="15">
                  </div>
                </div>
              </td>
              <th >
                <i class="glyphicon glyphicon-ok"></i> 비밀번호 확인
              </th>
              <td >
                <div class="row">
                  <div class="col-md-8">
                    <input type="password" id="wPwd1" name="wPwd1" class="form-control" minlen="8" maxlen="15">
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
                <textarea id="wEtc" name="wEtc" class="form-control" placeholder="비고를 입력하세요." rows="20"></textarea>
              </td>
            </tr>
          </table>
        </div>
      </div>
    </div>
  </div>
</div>


<script type="text/javascript" charset="utf-8">

  var memb_id = '${id}';
  var isAuthDataLoad = true; // 권한 그리드 데이터 요청을 위한 상태 값

</script>