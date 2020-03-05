package slm.www.vo.rsla;

import java.util.Date;

/**
 * SLA 대역폭 사용률 VO(SLM_SLA_BANDWIDTH)
 * <p/>
 * User: 현재호
 * Date: 2016.06.22
 * Time: 오후 5:33
 */
public class SlaReportBandWidthVO {

    public long bw_seq; // 대역폭 고유번호
    public long sla_seq; // SLA 고유번호

    public String grp_nm; // 그룹 명

    public String bw_itm_cd; // 항목코드
    public String bw_itm_nm; // 항목 명
    public String use_rate; // 사용율

    public Date reg_dts; // 등록일자
    public long reg_dts_ux; // 등록일자(Unixtime)
}
