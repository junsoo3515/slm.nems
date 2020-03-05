package slm.www.vo.dm;

import java.util.List;

/**
 * 장비관리(SLM_EQUIPMENT)
 * <p/>
 * User: 현재호
 * Date: 2016.04.25
 * Time: 오후 3:35
 */
public class EquipVO {

    public String eqp_cd; // 장비코드
    public String eqp_grp_cd; // 장비그룹코드
    public String eqp_nm; // 장비명
    public String eqp_serial; // 제품번호

    public String use_fl; // 사용여부

    public String str_area; // 보관장소
    public String remark; // 비고

    public String reg_mem_id; // 등록자 아이디

    public int data_cnt; // 항목들의 입력 값 상태(0 : DB에 값 없음, 1 이상 : DB에 값이 있음)

    public List<EquipGrpVO> topicData; // 그룹별로 설정되어 있는 항목들의 입력 값
}
