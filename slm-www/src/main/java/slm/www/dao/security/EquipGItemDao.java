package slm.www.dao.security;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.EquipGItemVO;

import java.util.List;

/**
 * 장비그룹 기본항목 관리
 * <p/>
 * User: 이준수
 * Date: 2017.06.05
 * Time: 오전 9:57
 */
public interface EquipGItemDao {

    /**
     * 장비유형 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getCodeList(String eg);


    /**
     * 장비유형 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getEquipTypeList();

    /**
     * 기본항목 관리 총 개수 가져오기
     *
     * @param srcEqpType 검색어
     * @param vo         jqGrid 파라미터
     * @return int
     */
    public int getEqpGrpListCnt(@Param("srcEqpType") String srcEqpType, @Param("vo") SrcJQGridVO vo);

    /**
     * 기본항목 관리 목록 가져오기
     *
     * @param vo jqGrid 파라미터
     * @return List<EquipGItemVO>
     */
    public List<EquipGItemVO> getEqpGrpList(@Param("srcEqpType") String srcEqpType, @Param("vo") SrcJQGridVO vo);


    /**
     * 기본항목관리 Insert
     *
     * @param vo EquipGItemVO
     * @return int
     */
    public int setEqpGrpInsert(@Param("vo") EquipGItemVO vo);

    /**
     * 기본항목관리 Update
     *
     * @param vo EquipGItemVO
     * @return int
     */
    public int setEqpGrpUpdate(@Param("vo") EquipGItemVO vo);

    /**
     * 기본항목 관리 정렬순서 변경
     *
     * @param vo EquipGItemVO
     * @return int
     */
    public int setEqpGItemPosUpdate1(@Param("vo")EquipGItemVO vo);

    /**
     * 기본항목 관리 정렬순서 변경
     *
     * @param vo EquipGItemVO
     * @return int
     */
    public int setEqpGItemPosUpdate2(@Param("vo")EquipGItemVO vo);


    /**
     * 장비그룹 기본항목 관리 장비유형 가져오기(Select2)
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getEquipTypeSelect2(@Param("word")String word);
}
