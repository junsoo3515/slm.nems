<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<!-- begin breadcrumb -->
<ol class="breadcrumb pull-right">
    <li><a class="h_icon" href="javascript:;"><img src="${p}/res/assets/img/home_btn.png" alt="홈버튼"></a></li>
    <li><a href="javascript:;">시스템관리</a></li>
    <li class="active">장비그룹 기본항목 관리</li>
</ol>
<!-- end breadcrumb -->
<!-- begin page-header -->
<h1 class="page-header">장비그룹 기본항목 관리
    <small>장비 그룹에 해당하는 입력 항목 관리</small>
</h1>
<!-- end page-header -->

<!-- begin 검색 폼 panel -->
<div id="srcPanel" class="panel">
    <div class="panel-body">
        <div class="row">
            <div class="col-md-10 form-inline">
                <div class="form-group m-r-25">
                    <label class="control-label m-r-10"><i class="fa fa-arrow-circle-right"></i> 장비유형</label>
                    <select id="srcEqpType" name="srcEqpType" class="form-control"></select>
                </div>
            </div>
            <div class="col-md-2">
                <div class="pull-right">
                    <button id="btnSrch" class="btn btn-sm btn-primary"><i class="fa fa-search icon-white"></i> 검색
                    </button>
                </div>
            </div>
        </div>
    </div>
</div>
<!-- end 검색 폼 panel -->

<div class="row">
    <!-- begin 기본항목관리 리스트 panel -->
    <div id="eqpGrpPanel" class="panel panel-inverse">
        <div class="panel-heading">
            <div class="btn-group-custom pull-right">
                <button id="btnAdd" class="btn btn-primary btn-sm"><i
                        class="fa fa-plus icon-white"></i> 추가
                </button>
            </div>
            <h4 class="panel-title">기본항목 관리</h4>
        </div>
        <table id="eqpGrpList"></table>
        <div id="eqpGrpPager"></div>
    </div>
    <!-- end 기본항목관리 리스트 panel -->
</div>

<script type="text/javascript" charset="utf-8">
    <!--
    var authCrud = ${authCrud};
    var equipTypeSrchList = ${equipTypeSrchList};

    var lastSel; // 기본항목관리 그리드에서 선택된 rowID
    //-->
</script>