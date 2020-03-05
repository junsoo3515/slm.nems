package slm.www.vo.security;

/**
 * 연계시스템(NMS) 에서 사용되는 VO
 * <p/>
 * User: 이준수
 * Date: 2017.05.17
 * Time: 오후 2:25
 */
public class LinkVO {
    public String prev_host_cd;
    public String prev_col_dt;
    public String prev_itm_cd;
    public String host_cd;
    public String itm_cd;
    public String col_dt;
    public String avg_val;

    public void setPrev_col_dt(String prev_col_dt) {
        this.prev_col_dt = prev_col_dt;
    }
    public void setPrev_itm_cd(String prev_itm_cd) {
        this.prev_itm_cd = prev_itm_cd;
    }


    public void setPrev_host_cd(String prev_host_cd) {
        this.prev_host_cd = prev_host_cd;
    }

    public void setHost_cd(String host_cd) {
        this.host_cd = host_cd;
    }

    public void setItm_cd(String itm_cd) {
        this.itm_cd = itm_cd;
    }

    public void setCol_dt(String col_dt) {
        this.col_dt = col_dt;
    }

    public void setAvg_val(String avg_val) {
        this.avg_val = avg_val;
    }

}
