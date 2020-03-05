<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
  <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
  <li><a href="javascript:;">시스템 관리</a></li>
  <li class="active">SLA 항목 관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">SLA항목관리 <small>SLA 평가를 위한 설정 정보를 관리(항목별 평가기준/보고서 코드 매핑)</small></h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
  <div class="panel-body">
    <div class="row">
      <div class="col-md-9 form-inline">
        <div class="form-group m-r-50">
          <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 검색어</label>
          <input type="text" id="srcCode" name="srcCode" class="form-control">
        </div>
      </div>
      <div class="col-md-3">
        <div class="pull-right">
          <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색</button>
        </div>
      </div>
    </div>
  </div>
</div>
<!-- end 검색 폼 panel -->

<!-- begin 평가기준 관리 리스트 panel -->
<div id="slaEvalPanel" class="panel panel-inverse">
  <div class="panel-heading">
    <h4 class="panel-title">평가기준 관리</h4>
  </div>
  <table id="slaEvalList"></table>
  <div id="slaEvalPager"></div>
</div>
<!-- end 평가기준 관리 리스트 panel -->


<div class="row">
  <div id="slaItemPanel" class="panel panel-inverse">
  <!-- begin 평가기준 정보 panel -->
  <div class="col-md-8">
    <div id="slaEvalInfoPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group-custom pull-right">
          <button id="btnReg" class="btn btn-sm btn-primary"><i class="glyphicon glyphicon-ok icon-white"></i> 등록</button>
          <button id="btnCancel" class="btn btn-sm btn-danger m-l-3"><i class="glyphicon glyphicon-ban-circle icon-white"></i> 취소</button>
        </div>
        <h4 class="panel-title">평가기준 입력</h4>
      </div>

      <table class="table table-bordered">
        <tr>
          <th class="col-md-2">
            <i class="glyphicon glyphicon-ok"></i> 코드
          </th>
          <td class="col-md-2">
            <input type="text" id="eval_cd" name="eval_cd" class="form-control" msg="코드를" maxlen="4" isEngDigitOnly="1"/>
          </td>
          <th class="col-md-2">
            <i class="glyphicon glyphicon-ok"></i> 명칭
          </th>
          <td class="col-md-2">
            <input type="text" id="item_nm" name="item_nm" class="form-control" msg="명칭을"/>
          </td>
          <th class="col-md-2">
            <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;가중치
          </th>
          <td class="col-md-2">
            <input type="text" id="weight" name="weight" class="form-control" isNumericOnly="1"/>
          </td>
        </tr>

        <tr>
          <th class="col-md-2">
            <i></i> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;대상
          </th>
          <td colspan="3" class="col-md-5">
            <input type="text" id="target" name="target" class="form-control"/>
          </td>
          <th class="col-md-2">
            <i></i> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;결과&nbsp;단위
          </th>
          <td class="col-md-3">
            <input type="text" id="mea_res_unit" name="mea_res_unit" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th class="col-md-2">
            <i></i> 측정방법
          </th>
          <td class="col-md-2">
            <input type="text" id="mea_method" name="mea_method" class="form-control"/>
          </td>
          <th class="col-md-2">
            <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;측정툴
          </th>
          <td class="col-md-2">
            <input type="text" id="mea_tool" name="mea_tool" class="form-control"/>
          </td>
          <th class="col-md-2">
            <i></i>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;보고주기
          </th>
          <td class="col-md-2">
            <input type="text" id="mea_period" name="mea_period" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th class="col-md-2">
            <i></i> 계산식(한글)
          </th>
          <td colspan="5" class="col-md-10">
            <input type="text" id="arith_expression_nm" name="arith_expression_nm" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th class="col-md-2">
            <i></i> 계산식(수식)
          </th>
          <td colspan="5" class="col-md-10">
            <input type="text" id="arith_expression" name="arith_expression" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th rowspan="2" class="col-md-2">
            <i></i> 측정내용
          </th>
          <td rowspan="2" colspan="5" class="col-md-10">
            <textarea id="mea_cont" name="mea_cont" class="form-control" placeholder="측정내용을 입력하세요." rows="5"></textarea>
          </td>
        </tr>

        <tr>
        </tr>

        <tr>
          <th rowspan="2" class="col-md-2">
            <i></i> 목표수준
          </th>
          <th colspan="3" class="col-md-5">
            <i></i> <center>최대 기대수준</center>
          </th>
          <th colspan="2" class="col-md-5">
            <i></i> <center>최소 기대수준</center>
          </th>
        </tr>

        <tr>
          <td colspan="3" class="col-md-5">
            <input type="text" id="max_lev" name="max_lev" class="form-control"/>
          </td>
          <td colspan="2" class="col-md-5">
            <input type="text" id="min_lev" name="min_lev" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th rowspan="6" class="col-md-2">
            <i></i> 측정평가<br/>기준
          </th>
          <th class="col-md-2">
            <i></i> <center>항목</center>
          </th>
          <th class="col-md-2">
            <i></i> <center>값</center>
          </th>
          <th class="col-md-2">
            <i></i> <center>배점</center>
          </th>
          <th colspan="2" class="col-md-4">
            <i></i> <center>수식</center>
          </th>

        <tr>
          <th class="col-md-2">
            <i></i> <center>탁월</center>
          </th>
          <td class="col-md-2">
            <input type="text" id="score_exce_nm" name="score_exce_nm" class="form-control"/>
          </td>
          <td  class="col-md-2">
            <input type="text" id="point_exce" name="point_exce" class="form-control" isNumericDotOnly="1"/>
          </td>
          <td colspan="2" class="col-md-4">
            <input type="text" id="score_exce" name="score_exce" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th align="center" class="col-md-2">
            <i></i> <center>우수</center>
          </th>
          <td class="col-md-2">
            <input type="text" id="score_good_nm" name="score_good_nm" class="form-control"/>
          </td>
          <td  class="col-md-2">
            <input type="text" id="point_good" name="point_good" class="form-control" isNumericDotOnly="1" />
          </td>
          <td colspan="2" class="col-md-4">
            <input type="text" id="score_good" name="score_good" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th align="center" class="col-md-2">
            <i></i> <center>보통</center>
          </th>
          <td class="col-md-2">
            <input type="text" id="score_normal_nm" name="score_normal_nm" class="form-control"/>
          </td>
          <td  class="col-md-2">
            <input type="text" id="point_normal" name="point_normal" class="form-control" isNumericDotOnly="1"/>
          </td>
          <td colspan="2" class="col-md-4">
            <input type="text" id="score_normal" name="score_normal" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th class="col-md-2">
            <i></i> <center>미흡</center>
          </th>
          <td class="col-md-2">
            <input type="text" id="score_insuf_nm" name="score_insuf_nm" class="form-control"/>
          </td>
          <td  class="col-md-2">
            <input type="text" id="point_insuf" name="point_insuf" class="form-control" isNumericDotOnly="1"/>
          </td>
          <td colspan="2" class="col-md-4">
            <input type="text" id="score_insuf" name="score_insuf" class="form-control"/>
          </td>
        </tr>

        <tr>
          <th class="col-md-2">
            <i></i> <center>불량</center>
          </th>
          <td class="col-md-2">
            <input type="text" id="score_bad_nm" name="score_bad_nm" class="form-control"/>
          </td>
          <td  class="col-md-2">
            <input type="text" id="point_bad" name="point_bad" class="form-control" isNumericDotOnly="1"/>
          </td>
          <td colspan="2" class="col-md-2">
            <input type="text" id="score_bad" name="score_bad" class="form-control"/>
          </td>
        </tr>

      </table>

    </div>
  </div>

  <!-- end 평가기준 정보 panel -->

  <!-- begin 보고서 연결 panel -->
  <div class="col-md-4">
    <div id="reportConnPanel" class="panel panel-inverse">
      <div class="panel-heading">
        <div class="btn-group-custom pull-right">
          <button id="btnAdd" class="btn btn-sm btn-primary" disabled="true"><i class="fa fa-plus icon-white"></i> 추가</button>
        </div>
        <h4 class="panel-title">보고서 연결</h4>
      </div>
      <table id="reportConnList"></table>
      <div id="reportConnPager"></div>
    </div>
  </div>
  <!-- end 보고서 연결 panel -->
 </div>
</div>


<script type="text/javascript" charset="utf-8">
  var lastSel; // 평가기준 관리 그리드에서 선택된 rowID
  var isAddState = false;
  var authCrud = ${authCrud};
  var nmList = ${nmList};
</script>
