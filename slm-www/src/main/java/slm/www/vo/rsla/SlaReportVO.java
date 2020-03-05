package slm.www.vo.rsla;

import java.util.Date;
import java.util.List;

/**
 * SLA 보고서 VO(SLM_SLA_REPORT)
 * <p/>
 * User: 현재호
 * Date: 2016.06.08
 * Time: 오후 4:09
 */
public class SlaReportVO {
    // 0. 공통 정보
    public long sla_seq; // 보고서 고유번호

    public Date reg_dts; // 등록일자
    public long reg_dts_ux; // 등록일자(Unixtime)

    public int isState; // 입력/수정 시 사용하는 상태값으로 사용

    // 1. SLM_SLA_REPORT 테이블 정보
    public String sla_title; // 제목
    public String brief_dt; // 보고 일

    public String remove_dt; // 제외 일
    public String start_dt; // 시작 일
    public String end_dt; // 종료 일

    public String rpt_file; // 보고서 파일

    // 2. SLM_BACKSUC_RATE 테이블 정보
    public int tot_time; // 총 기간(시간)
    public int plan_cnt; // 계획 수량
    public int suc_cnt; // 성공 수량
    public int fail_cnt; // 실패 수량
    public int stop_cnt; // 중단 수량
    public int res_plan_cnt; // 복구 계획 수량
    public int res_suc_cnt; // 복구 성공 수량

    public String remark; // 비고

    // 3. SLM_SLA_BANDWIDTH
    public List<SlaReportBandWidthVO> bandWidthData; // 대역폭 사용률
}
