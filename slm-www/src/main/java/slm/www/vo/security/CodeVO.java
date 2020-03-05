package slm.www.vo.security;

import java.util.Date;

/**
 * 코드관리에서 사용되는 VO
 * <p/>
 * User: 현재호
 * Date: 2016.04.22
 * Time: 오전 10:50
 */
public class CodeVO {

    public String com_cd;
    public String jong_cd;
    public String jong_nm;
    public String cd_nm;
    public String cd_unit;
    public int prime;
    public int sort_seq;
    public String hig_cd;
    public String link_cd;
    public String use_fl;
    public String etc;
    public Date reg_dts;
    public long reg_dts_ux;

    // jqGrid C/R/U/D에서 사용됨
    public void setCom_cd(String com_cd) {
        this.com_cd = com_cd;
    }

    public void setJong_cd(String jong_cd) {
        this.jong_cd = jong_cd;
    }

    public void setJong_nm(String jong_nm) {
        this.jong_nm = jong_nm;
    }

    public void setCd_nm(String cd_nm) {
        this.cd_nm = cd_nm;
    }

    public void setUse_fl(String use_fl) {
        this.use_fl = use_fl;
    }
}
