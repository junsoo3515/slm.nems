/**
 * jqGrid 기본 설정
 * User: 현재호
 * Date: 13. 8. 20
 * Time: 오후 2:02
*/
(function( factory ) {
    "use strict";
    if ( typeof define === "function" && define.amd ) {
        // AMD. Register as an anonymous module.
        define([
            "jquery"
        ], factory );
    } else {
        // Browser globals
        factory( jQuery );
    }
}(function( $ ) {
    /*--------------------------------------------------
     기능   : jqGrid 리스트에서 CRUD 처리 시 formatter: 'actions'에서 사용하는 아이콘 변경
     INPUT  :
       - tbID   : jqGrid의 테이블 ID
       - lastSel: jqGrid의 TR의 고유 ID
       - stat   : add / edit / list(추가 / 수정/ 목록 구분 코드)
     RETURN : null
     ----------------------------------------------------*/
    function jqGridListIcon(tbID, lastSel, stat) {

        if ( stat === undefined ) {
            stat = 'list';
        }

        var lrid = jQuery.jgrid.jqID(lastSel);

        switch(stat) {
            case 'add' :

                jQuery("tr#" + lrid + " div.ui-inline-edit, " + "tr#" + lrid + " div.ui-inline-del", "#" + tbID + ".ui-jqgrid-btable:first").hide();
                jQuery("tr#" + lrid + " div.ui-inline-save, " + "tr#" + lrid + " div.ui-inline-cancel", "#" + tbID + ".ui-jqgrid-btable:first").show();
                break;

            case 'edit' :

                jQuery("tr#" + lrid + " div.ui-inline-edit, " + "tr#" + lrid + " div.ui-inline-del").hide();
                jQuery("tr#" + lrid + " div.ui-inline-save, " + "tr#" + lrid + " div.ui-inline-cancel").show();
                break;
                break;
            default :

                jQuery("tr#" + lrid + " div.ui-inline-edit, " + "tr#" + lrid + " div.ui-inline-del").show();
                jQuery("tr#" + lrid + " div.ui-inline-save, " + "tr#" + lrid + " div.ui-inline-cancel").hide();
                break;
        }
    }

    return {
        version : "1.0.1",
        author : "darkhand",
        jqGridListIcon : jqGridListIcon
    }
}));