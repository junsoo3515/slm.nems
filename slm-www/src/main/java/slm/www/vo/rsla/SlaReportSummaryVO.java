package slm.www.vo.rsla;

/**
 * SLA 보고서 평가결과 VO
 * <p/>
 * User: 현재호
 * Date: 2016.06.09
 * Time: 오후 5:37
 */
public class SlaReportSummaryVO {

    public long summ_seq; // 요약 고유번호
    public long sla_seq; // SLA 고유번호

    public String rpt_cd; // 보고서 코드

    public String mea_res; // 평가결과
    public Object mea_point; // 평가점수(float 처리는 controller에서 null 체크가 안되서)

    public String arith_expression_nm; // 계산식 한글
    public String mea_cont; // 측정 내용
}
