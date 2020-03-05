package slm.www.vo.dashboard;

/**
 * 장애처리에서 사용되는 VO
 * <p/>
 * User: 이종혁
 * Date: 2016.06.22
 * Time: 오후 09:56
 */
public class DashBoardSlaVO {

    public String brief_dt;
    public String sla_seq;
    public long timeliness;
    public long availability;
    public long productivity;
    public long general;
    public long slatotal;
    public long mea_point;
    public String hig_rpt_cd;
    public float operateIndex;
}
