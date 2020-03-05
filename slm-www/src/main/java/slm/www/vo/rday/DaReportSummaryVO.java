package slm.www.vo.rday;

import module.file.CmnFileBiz;
import module.secure.filter.CmnFilterBiz;

/**
 * 점검요약정보
 * <p/>
 * User: 현재호
 * Date: 2016.05.16
 * Time: 오후 5:32
 */
public class DaReportSummaryVO {

    public long summ_seq; // 요약 고유번호

    public String grp_nm; // 분류(상위 보고서 명칭)

    public String rpt_cd; // 구분 코드(보고서 코드)
    public String rpt_nm; // 구분 명칭(보고서 명칭)

    public int cnt_all; // 전체 장비 수량

    public String inspect_nm; // 점검자
    public String confirm_nm; // 점검확인자

    public String res_fl; // 완료 여부

    public int itm_all; // 전체 점검항목 수량
    public int itm_normal; // 정상 항목 수량
    public int itm_abnormal; // 이상 항목 수량

    public String sw_type_fl; // S/W 유형 여부

    // jqGrid C/R/U/D에서 사용됨
    public void setInspect_nm(String inspect_nm) { this.inspect_nm = CmnFilterBiz.filterSqlString(inspect_nm); }

    public void setConfirm_nm(String confirm_nm) { this.confirm_nm = CmnFilterBiz.filterSqlString(confirm_nm); }

    public void setRes_fl(String res_fl) {
        this.res_fl = CmnFilterBiz.filterSqlString(res_fl);
    }
}
