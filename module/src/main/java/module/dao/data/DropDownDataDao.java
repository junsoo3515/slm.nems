package module.dao.data;

import org.apache.ibatis.annotations.Param;
import module.vo.list.ListObjVO;

import java.util.List;

/**
 * DropDown 컴포넌트에서 사용하기 위한 데이터 유형 가져오기
 * <p/>
 * User: 현재호
 * Date: 2016.04.25
 * Time: 오전 11:03
 */
public interface DropDownDataDao {

    /**
     * 권한 가져오기
     *
     * @return List
     */
    public List<ListObjVO> getAuthList();

    /**
     * COM_CODE 코드명/코드값 List 가져오기(DropDown에서 주로 사용)
     *
     * @param jongCD 종류 코드
     * @return List
     */
    public List<ListObjVO> getCodeList(@Param("jongCD") String jongCD);

    /**
     * SLM_EQUIPGRP_TOPIC 코드명/코드값 List 가져오기(DropDown에서 주로 사용)
     *
     * @param grpCD 장비유형 코드
     * @return List
     */
    public List<ListObjVO> getEquipGrpTopicList(@Param("grpCD") String grpCD);

    /**
     * SLM_EQUIPMENT 코드명/코드값 List 가져오기(DropDown에서 주로 사용)
     *
     * @param grpCD 장비유형 코드
     * @return List
     */
    public List<ListObjVO> getEquipList(@Param("grpCD") String grpCD);
}
