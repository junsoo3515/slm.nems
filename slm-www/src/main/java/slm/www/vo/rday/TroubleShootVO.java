package slm.www.vo.rday;

/**
 * 장애처리에서 사용되는 VO
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
public class TroubleShootVO {

    public String disorder_type;
    public String disorder_type_nm;
    public String equip_type;
    public String equip_type_nm;
    public String dis_seq;
    public String eqp_nm;
    public String eqp_cd;
    public String occur_dt;
    public String mea_plan_dt;
    public String mea_fin_dt;
    public String cont;
    public String fin_fl;
    public String fin_nm;
    public String reg_mem_id;
    public String reg_mem_nm;
    public String nature_fl;
    public String rpt_file;
    public String sv_stop_dt;
    public String sv_start_dt;
    public String reg_dts;
    public String summ_cont;
    public String work_seq;
    public String work_mem_id;
    public String nm;
    public String comp_nm;
    public String tel;
    public String tel_hp;
    public String email;
    public String mod_dts;
    public String mem_id;
    public String work_type;
    public String work_state;
    public String mea_type;
    public String wbFileName;
    public String waFileName;
    public String mea_seq;
    public String real_seq;
    public String err_fl;

    public void setErr_fl(String err_fl) {
        this.err_fl = err_fl;
    }

    public void setReal_seq(String real_seq) {
        this.real_seq = real_seq;
    }

    public void setMea_seq(String mea_seq) {
        this.mea_seq = mea_seq;
    }

    public void setWbFileName(String wbFileName) {
        this.wbFileName = wbFileName;
    }

    public void setWaFileName(String waFileName) {
        this.waFileName = waFileName;
    }


    public void setMea_type(String mea_type) {
        this.mea_type = mea_type;
    }

    public void setWork_state(String work_state) {
        this.work_state = work_state;
    }

    public void setWork_type(String work_type) {
        this.work_type = work_type;
    }

    public void setMem_id(String mem_id) {
        this.mem_id = mem_id;
    }

    public void setMod_dts(String mod_dts) {
        this.mod_dts = mod_dts;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setTel_hp(String tel_hp) {
        this.tel_hp = tel_hp;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public void setComp_nm(String comp_nm) {
        this.comp_nm = comp_nm;
    }

    public void setNm(String nm) {
        this.nm = nm;
    }

    public void setWork_mem_id(String work_mem_id) {
        this.work_mem_id = work_mem_id;
    }

    public void setWork_seq(String work_seq) {
        this.work_seq = work_seq;
    }

    public void setReg_dts(String reg_dts) {
        this.reg_dts = reg_dts;
    }

    public void setDis_seq(String dis_seq) {
        this.dis_seq = dis_seq;
    }
}
