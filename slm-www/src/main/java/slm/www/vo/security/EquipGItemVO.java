package slm.www.vo.security;

/**
 * 장비그룹 기본항목 관리에서 사용하는 VO
 * <p/>
 * User: 이준수
 * Date: 2017.06.06
 * Time: 오전 11:14
 */
public class EquipGItemVO {
    public String com_cd;
    public String cd_nm;
    public String topic_cd;
    public String eqp_grp_cd;
    public String eqp_nm;
    public String topic_nm;
    public String eng_nm;
    public String use_fl;
    public String pos;
    public String prev_topic_nm;
    public String prev_eng_nm;
    public String prev_use_fl;
    public String prev_pos;
    public String rowIndex;

    public void setRowIndex(String rowIndex) {
        this.rowIndex = rowIndex;
    }
    public void setPrev_topic_nm(String prev_topic_nm) {
        this.prev_topic_nm = prev_topic_nm;
    }

    public void setPrev_eng_nm(String prev_eng_nm) {
        this.prev_eng_nm = prev_eng_nm;
    }

    public void setPrev_use_fl(String prev_use_fl) {
        this.prev_use_fl = prev_use_fl;
    }

    public void setPrev_pos(String prev_pos) {
        this.prev_pos = prev_pos;
    }

    public void setCom_cd(String com_cd) {
        this.com_cd = com_cd;
    }

    public void setCd_nm(String cd_nm) {
        this.cd_nm = cd_nm;
    }

    public void setTopic_cd(String topic_cd) {
        this.topic_cd = topic_cd;
    }

    public void setEqp_grp_cd(String eqp_grp_cd) {
        this.eqp_grp_cd = eqp_grp_cd;
    }

    public void setEqp_nm(String eqp_nm) {
        this.eqp_nm = eqp_nm;
    }

    public void setTopic_nm(String topic_nm) {
        this.topic_nm = topic_nm;
    }

    public void setEng_nm(String eng_nm) {
        this.eng_nm = eng_nm;
    }

    public void setUse_fl(String use_fl) {
        this.use_fl = use_fl;
    }

    public void setPos(String pos) {
        this.pos = pos;
    }
}
