package slm.www.vo.dm;

/**
 * 예비품 현황
 * <p/>
 * User: 현재호
 * Date: 2016.04.26
 * Time: 오후 4:06
 */
public class SparePartVO {

    public long spare_seq; // 예비품 고유번호
    public String eqp_cd; // 장비코드
    public String reg_dt; // 등록 일
    public int qnt; // 수량

    // jqGrid C/R/U/D에서 사용됨
    public void setSpare_seq(long spare_seq) {
        this.spare_seq = spare_seq;
    }

    public void setEqp_cd(String eqp_cd) {
        this.eqp_cd = eqp_cd;
    }

    public void setReg_dt(String reg_dt) {
        this.reg_dt = reg_dt;
    }

    public void setQnt(int qnt) {
        this.qnt = qnt;
    }
}
