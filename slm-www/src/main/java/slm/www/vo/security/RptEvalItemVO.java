package slm.www.vo.security;

/**
 * 보고서 항목관리에서 사용되는 VO
 * <p/>
 * User: 이준수
 * Date: 2017.05.29
 * Time: 오후 4:49
 */
public class RptEvalItemVO {
    public String nm;
    public String prev_itm_cd;
    public String prev_nm;
    public String prev_grp_nm;
    public String itm_cd;
    public String grp_nm;
    public String grp_cd;
    public String basis_nm;
    public String eng_nm;
    public String cont;
    public String sort_seq;
    public String good;
    public String warning;
    public String wrong;
    public String chk_fl;
    public String good_expr;
    public String warning_expr;
    public String wrong_expr;
    public String crud;
    public String rpt_cd;
    public String rowIndex;

    public String cnt1;
    public String cnt2;

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }

    public void setPrev_nm(String prev_nm) {
        this.prev_nm = prev_nm;
    }

    public void setPrev_grp_nm(String prev_grp_nm) {
        this.prev_grp_nm = prev_grp_nm;
    }

    public void setRpt_cd(String rpt_cd) {
        this.rpt_cd = rpt_cd;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public void setPrev_itm_cd(String prev_itm_cd) {
        this.prev_itm_cd = prev_itm_cd;
    }
    public void setCrud(String crud) {
        this.crud = crud;
    }


    public void setGrp_nm(String grp_nm) {
        this.grp_nm = grp_nm;
    }

    public void setItm_cd(String itm_cd) {
        this.itm_cd = itm_cd;
    }

    public void setGrp_cd(String grp_cd) {
        this.grp_cd = grp_cd;
    }

    public void setBasis_nm(String basis_nm) {
        this.basis_nm = basis_nm;
    }

    public void setEng_nm(String eng_nm) {
        this.eng_nm = eng_nm;
    }

    public void setCont(String cont) {
        this.cont = cont;
    }

    public void setSort_seq(String sort_seq) {
        this.sort_seq = sort_seq;
    }

    public void setGood(String good) {
        this.good = good;
    }

    public void setWarning(String warning) {
        this.warning = warning;
    }

    public void setWrong(String wrong) {
        this.wrong = wrong;
    }

    public void setChk_fl(String chk_fl) {
        this.chk_fl = chk_fl;
    }

    public void setGood_expr(String good_expr) {
        this.good_expr = good_expr;
    }

    public void setWarning_expr(String warning_expr) {
        this.warning_expr = warning_expr;
    }

    public void setWrong_expr(String wrong_expr) {
        this.wrong_expr = wrong_expr;
    }
}
