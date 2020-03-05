package slm.www.vo.security;

/**
 * 보고서 코드 관리 에서 사용되는 VO
 * <p/>
 * User: 이준수
 * Date: 2017.05.17
 * Time: 오후 2:25
 */
public class RptCodeVO {
    public String rpt_cd;
    public String hig_rpt_cd;
    public String nm;
    public int rpt_depth;
    public String inspector;
    public String confirmor;
    public char use_fl;
    public char res_fl;
    public char sw_type_fl;
    public char mrpt_use_fl;
    public int rpt_sort_seq;
    public int mrpt_sort_seq;
    public String eqp_cd;
    public int sort_seq;
    public String eqp_nm;
    public String prev_eqp_cd;
    public int prev_sort_seq;
    public String rowIndex;

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setRpt_cd(String rpt_cd) {
        this.rpt_cd = rpt_cd;
    }

    public void setHig_rpt_cd(String hig_rpt_cd) {
        this.hig_rpt_cd = hig_rpt_cd;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public void setRpt_depth(int rpt_depth) {
        this.rpt_depth = rpt_depth;
    }

    public void setInspector(String inspector) {
        this.inspector = inspector;
    }

    public void setConfirmor(String confirmor) {
        this.confirmor = confirmor;
    }

    public void setUse_fl(char use_fl) {
        this.use_fl = use_fl;
    }

    public void setRes_fl(char res_fl) {
        this.res_fl = res_fl;
    }

    public void setSw_type_fl(char sw_type_fl) {
        this.sw_type_fl = sw_type_fl;
    }

    public void setMrpt_use_fl(char mrpt_use_fl) {
        this.mrpt_use_fl = mrpt_use_fl;
    }

    public void setRpt_sort_seq(int rpt_sort_seq) {
        this.rpt_sort_seq = rpt_sort_seq;
    }

    public void setMrpt_sort_seq(int mrpt_sort_seq) {
        this.mrpt_sort_seq = mrpt_sort_seq;
    }

    public void setEqp_cd(String eqp_cd) {
        this.eqp_cd = eqp_cd;
    }

    public void setSort_seq(int sort_seq) {
        this.sort_seq = sort_seq;
    }

    public void setEqp_nm(String eqp_nm) {
        this.eqp_nm = eqp_nm;
    }

    public void setPrev_eqp_cd(String prev_eqp_cd) {
        this.prev_eqp_cd = prev_eqp_cd;
    }

    public void setPrev_sort_seq(int prev_sort_seq) {
        this.prev_sort_seq = prev_sort_seq;
    }
}
