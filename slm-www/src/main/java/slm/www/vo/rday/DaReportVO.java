package slm.www.vo.rday;

import module.secure.filter.CmnFilterBiz;

import java.util.Date;
import java.util.List;

/**
 * 일/월 보고서 VO(SLM_DA_REPORT)
 * <p/>
 * User: 현재호
 * Date: 2016.05.02
 * Time: 오후 4:09
 */
public class DaReportVO {

    public long da_seq; // 보고서 고유번호
    public String brief_dt; // 보고 일

    public String rpt_gubun_cd; // 보고서 구분 코드
    public String rpt_gubun_nm; // 보고서 구분 명칭

    public String start_dt; // 시작 일
    public String end_dt; // 종료 일

    public String pfmc; // 실적
    public String plan; // 계획
    public String rpt_file; // 보고서 파일

    public String data_copy_fl; // 기존 데이터 복사 여부
    public String fin_fl; // 일일 마감 여부

    // 등록일자
    public Date reg_dts;
    public long reg_dts_ux;

    public List<DaReportSummaryVO> summData; // 요약정보

    // jqGrid C/R/U/D에서 사용됨
    public void setDa_seq(long da_seq) {
        this.da_seq = da_seq;
    }

    public void setBrief_dt(String brief_dt) {
        this.brief_dt = CmnFilterBiz.filterPureString(brief_dt);
    }

    public void setRpt_gubun_nm(String rpt_gubun_nm) {
        this.rpt_gubun_nm = rpt_gubun_nm;
    }

    public void setData_copy_fl(String data_copy_fl) { this.data_copy_fl = data_copy_fl; }

    public void setFin_fl(String fin_fl) { this.fin_fl = fin_fl; }
}
