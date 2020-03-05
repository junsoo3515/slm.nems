package slm.www.vo.rsla;

/**
 * SLA 항목별 평가기준 VO(SLM_SLA_EVAL)
 * <p/>
 * User: 현재호
 * Date: 2016.06.09
 * Time: 오후 3:25
 */
public class SlaReportEvalVO {

    public String rpt_cd; // 보고서 코드

    public String grp_nm; // 영역
    public String nm; // 서비스 측정지표

    public String eval_cd; // 평가지표 코드
    public String item_nm; // 항목 명
    public Object weight; // 가중치(int 처리는 controller에서 null 체크가 안되서)
    public String target; // 대상
    public String mea_method; // 측정방법
    public String mea_tool; // 측정 툴
    public String mea_period; // 측정 보고주기
    public String arith_expression_nm; // 계산식(한글)
    public String arith_expression; // 계산식
    public String mea_cont; // 측정내용

    public String max_lev; // 최대 기대수준
    public String min_lev; // 최소 기대수준

    public String score_exce_nm; // 측정기준평가(탁월)
    public String score_good_nm; // 측정기준평가(우수)
    public String score_normal_nm; // 측정기준평가(보통)
    public String score_insuf_nm; // 측정기준평가(미흡)
    public String score_bad_nm; // 측정기준평가(불량)

    public String score_exce; // 측정기준평가 수식(탁월)
    public String score_good; // 측정기준평가 수식(우수)
    public String score_normal; // 측정기준평가 수식(보통)
    public String score_insuf; // 측정기준평가 수식(미흡)
    public String score_bad; // 측정기준평가 수식(불량)

    public float point_exce; // 측정기준평가 배점(탁월)
    public float point_good; // 측정기준평가 배점(우수)
    public float point_normal; // 측정기준평가 배점(보통)
    public float point_insuf; // 측정기준평가 배점(미흡)
    public float point_bad; // 측정기준평가 배점(불량)

    public String mea_res; // 측정결과
    public Object mea_point; // 평가점수(float 처리는 controller에서 null 체크가 안되서)

    public String mea_res_unit; // 측정결과 단위
}
