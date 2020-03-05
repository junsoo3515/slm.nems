<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">시스템관리</a></li>
  <li class="active">보고서 기본 항목관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">보고서기본항목관리 <small>보고서 코드 별 하드웨어 및 S/W에 대한 점검항목 관리 및 보고서항목과 장비항목 매핑하는 기능을 제공</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-10 form-inline">
        <div class="form-group m-r-40">
          <div class="form-group m-r-25">
            <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 보고서 항목</label>
            <select id="srcRptNm" name="srcRptNm" class="form-control"></select>
          </div>
          <div class="form-group  m-r-25">
              <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i>  항목 유형</label>
              <select id="srcType" name="srcType" class="form-control"></select>
          </div>
            <div class="form-group m-r-25">
                <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 검색어</label>
                <input type="text" id="srcWord" name="srcWord" class="form-control">
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

<!-- begin 요청사항 리스트 panel -->
<div id="reportTopicPanel" class="panel panel-inverse">
  <div class="panel-heading">
    <h4 class="panel-title">기본항목관리</h4>
  </div>
  <table id="reportTopicList"></table>
  <div id="reportTopicPager"></div>
</div>
<!-- end 권한 리스트 panel -->

<!-- begin 요청사항 정보 tab header -->
<ul class="nav nav-tabs" id="myTabs">
  <li class="active"><a href="#reportTopicInfoPanel" data-toggle="tab" >기본항목 입력</a></li>
  <li class=""><a href="#swTopicPanel" data-toggle="tab" id="swTopictab" >S/W 점검항목 및 장비그룹 연결</a></li>
  <div class="pull-right">
    <button id="btnReg" class="btn btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
    <button id="btnCancel" class="btn btn-danger"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
  </div>
</ul>
<!-- end 권한 정보 tab header -->


<div class="tab-content">
  <div id="reportTopicInfoPanel" class="tab-pane active">

    <table class="table table-bordered">

      <tr>
        <th class="col-md-2">
          <i class="glyphicon glyphicon-ok"></i> 항목코드
        </th>
        <td class="col-md-4">
          <div class="form-inline">
              <input type="text" id="topic_cd" name="topic_cd" msg="항목코드를" maxlen="4"  class="form-control" >
          </div>
        </td>
        <th class="col-md-2">
            <i class="glyphicon glyphicon-ok"></i> 보고서 항목
        </th>
        <td class="col-md-4">
            <div class="form-inline">
                <select id="nm" name="nm" msg="보고서 항목을" class="form-control"></select>
            </div>
        </td>
      </tr>

      <tr>
          <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 명칭(한글)
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="text" id="topic_nm" name="topic_nm" msg="명칭(한글)을" class="form-control" >
              </div>
          </td>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;명칭(영문)
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="text" id="eng_nm" name="eng_nm"  class="form-control" >
              </div>
          </td>
      </tr>

      <tr>
          <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 유형
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <select id="topic_type" name="topic_type" msg="유형을" class="form-control"></select>
              </div>
          </td>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;사용
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="checkbox" class="js-switch" id="use_fl" name="use_fl" value="N"/>
              </div>
          </td>
      </tr>

      <tr>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;초기데이터
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="text" id="init_val" name="init_val" class="form-control" >
              </div>
          </td>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;단위
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="text" id="topic_unit" name="topic_unit" class="form-control" >
              </div>
          </td>
      </tr>

      <tr>
          <th class="col-md-2">
              <i class="glyphicon glyphicon-ok"></i> 정렬순서
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input id="pos" name="pos" msg="정렬순서" isNumericOnly="1" class="form-control"/>
              </div>
          </td>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;외부연계 항목
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <select id="link_itm_cd" name="link_itm_cd" class="form-control"></select>
              </div>
          </td>
      </tr>

      <tr>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;보고서 출력
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="checkbox" class="js-switch" id="rpt_print_fl" name="rpt_print_fl" value="Y"/>&nbsp&nbsp 일일보고서
                  <input type="checkbox" class="js-switch" id="mrpt_print_fl" name="mrpt_print_fl" value="Y"/>&nbsp&nbsp 월간보고서

              </div>
          </td>
          <th class="col-md-2">
              <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;수정 잠금
          </th>
          <td class="col-md-4">
              <div class="form-inline">
                  <input type="checkbox" class="js-switch" id="mod_lock_fl" name="mod_lock_fl" value="Y"/>
              </div>
          </td>
      </tr>

        <tr>
            <th class="col-md-2">
                <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;비고
            </th>
            <td class="col-md-4" colspan="3">
                <div class="form-inline">
                    <textarea id="remark" name="remark" class="form-control"  rows="5" style="width: 100%; resize: none; outline: none;"></textarea>
                </div>
            </td>
        </tr>
    </table>

  </div>

  <div  id="swTopicPanel" class="tab-pane fade">
    <div class="row">
        <div  class="col-md-8">
            <div id="swTopicGridPanel" class="panel panel-inverse">
              <div class="panel-heading">
                  <div class="btn-group-custom pull-right">
                      <button id="btnAdd1" class="btn btn-primary btn-sm"><i class="fa fa-plus icon-white"></i> 추가</button>
                  </div>
                <h4 class="panel-title">S/W 점검항목 기초데이터 관리</h4>
              </div>
              <table id="swTopicGridList"></table>
              <div id="swTopicGridPager"></div>
            </div>
        </div>
        <div class="col-md-4">
            <div id="equipTopicGridPanel" class="nav panel-inverse">
                <div class="panel-heading">
                    <div class="btn-group-custom pull-right">
                        <button id="btnAdd2" class="btn btn-primary btn-sm"><i class="fa fa-plus icon-white"></i> 추가</button>
                    </div>
                    <h4 class="panel-title">장비그룹 연계</h4>
                </div>
                <table id="equipTopicGridList"></table>
                <div id="equipTopicGridPager"></div>
            </div>
            </div>
        </div>
    </div>
  </div>



</div>


<script type="text/javascript" charset="utf-8">
  <!--
  var authCrud = ${authCrud};
  var lastSel;
  var rptTopicNmList = ${rptTopicNmList};
  var linkTopicList = ${linkTopicList};
  var rptTypeList = ${rptTypeList};
  var topicList = ${topicList};
  //-->
</script>