package slm.www.vo.rday;

import module.secure.filter.CmnFilterBiz;

import java.util.Date;

/**
 * 장비별 유지보수 입력 / S/W 유지보수 입력 VO
 * <p/>
 * User: 현재호
 * Date: 2016.05.20
 * Time: 오후 3:27
 */
public class EquipMainDataVO {

    public long data_seq; // 입력 고유번호
    public String eqp_cd; // 장비 코드
    public String eqp_grp_cd; // 장비 그룹 코드
    public String topic_cd; // 항목 코드
    public long da_seq; // 보고서 고유번호
    public String rpt_cd; // 보고서 코드
    public String rpt_gubun_cd; // 보고서 구분 코드
    public String topic_grp_seq; // 항목 그룹 일련번호

    public String inp_val; // 입력 값
    public String occur_dt; // 발생 일

    // 등록일자
    public Date reg_dts;
    public long reg_dts_ux;

    // jqGrid C/R/U/D에서 사용됨
    public void setInp_val(String inp_val) {
        this.inp_val = CmnFilterBiz.filterSqlString(inp_val);
    }
}
