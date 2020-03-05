package slm.www.dao.security;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.RptEvalItemVO;

import java.util.List;

/**
 * 보고서 평가항목관리
 * <p/>
 * User: 이준수
 * Date: 2017.05.29
 * Time: 오후 4:45
 */
public interface RptEvalItemDao {

    /**
     * 그룹관리 항목 가져오기
     *
     * @return ListObjVO
     */
    public List<ListObjVO> getGroupNmList();

    /**
     * 보고서 항목 가져오기
     *
     * @return ListObjVO
     */
    public List<ListObjVO> getReportNmList();


    /**
     * 그룹관리 총 개수 가져오기
     *
     * @param srcWord 검색어
     * @param vo      jqGrid 파라미터
     * @return int
     */
    public int getEvalGroupListCnt(@Param("srcWord") String srcWord, @Param("vo") SrcJQGridVO vo);

    /**
     * 그룹관리 목록 가져오기
     *
     * @param srcWord 검색어
     * @param vo      jqGrid 파라미터
     * @return List<RptEvalItemVO>
     */
    public List<RptEvalItemVO> getEvalGroupList(@Param("srcWord") String srcWord, @Param("vo") SrcJQGridVO vo);

    /**
     * 그룹관리 추가하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setEvalGroupAct(@Param("vo") RptEvalItemVO vo);

    /**
     * 그룹관리 수정하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setEvalGroupUpdate(@Param("vo") RptEvalItemVO vo);

    /**
     * 그룹관리 삭제하기
     *
     * @param vo RptEvalItemVO
     * @return void
     */
    public void setEvalGroupDel(@Param("vo") RptEvalItemVO vo);

    /**
     * 그룹관리 삭제하기
     *
     * @param vo RptEvalItemVO
     * @return void
     */
    public void setEvalGroupMapDel(@Param("vo") RptEvalItemVO vo);


    /**
     * 평가기준 관리 총 개수 가져오기
     *
     * @param grp_cd 그룹코드
     * @param vo     jqGrid 파라미터
     * @return int
     */
    public int getEvalItemListCnt(@Param("grp_cd") String grp_cd, @Param("vo") SrcJQGridVO vo);

    /**
     * 평가기준 관리 목록 가져오기
     *
     * @param grp_cd 그룹코드
     * @param vo     jqGrid 파라미터
     * @return List<RptEvalItemVO>
     */
    public List<RptEvalItemVO> getEvalItemList(@Param("grp_cd") String grp_cd, @Param("vo") SrcJQGridVO vo);

    /**
     * 평가기준 상세정보 가져오기
     *
     * @param vo RptEvalItemVO
     * @return RptEvalItemVO
     */
    public RptEvalItemVO getEvalItemInfoData(@Param("vo") RptEvalItemVO vo);


    /**
     * 평가기준 수정하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setEvalItemInfoUpdate(@Param("vo") RptEvalItemVO vo);

    /**
     * 평가기준 등록하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setEvalItemInfoAct(@Param("vo") RptEvalItemVO vo);

    /**
     * 평가기준 삭제하기
     *
     * @param vo RptEvalItemVO
     * @return void
     */
    public void setEvalItemDel(@Param("vo") RptEvalItemVO vo);

    /**
     * 평가기준 삭제하기
     *
     * @param vo RptEvalItemVO
     * @return void
     */
    public void setEvalGroupItemDel(@Param("vo") RptEvalItemVO vo);


    /**
     * 보고서 연계 총 개수 가져오기
     *
     * @param grp_cd 그룹코드
     * @param vo     jqGrid 파라미터
     * @return int
     */
    public int getReportMapListCnt(@Param("grp_cd") String grp_cd, SrcJQGridVO vo);

    /**
     * 보고서 연계 목록 가져오기
     *
     * @param grp_cd 그룹코드
     * @param vo     jqGrid 파라미터
     * @return List<RptEvalItemVO>
     */
    public List<RptEvalItemVO> getReportMapList(@Param("grp_cd") String grp_cd, SrcJQGridVO vo);

    /**
     * 보고서연계 등록하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setReportMapAct(@Param("vo") RptEvalItemVO vo);


    /**
     * 보고서연계 수정하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setReportMapUpdate(@Param("vo") RptEvalItemVO vo);


    /**
     * 보고서연계 삭제하기
     *
     * @param vo RptEvalItemVO
     * @return int
     */
    public int setReportMapDel(@Param("vo") RptEvalItemVO vo);

    /**
     * 평가기준목록 정렬순서 변경
     *
     * @param vo  RptEvalItemVO
     * @return int
     */
    public int setEvalItemPosUpdate1(@Param("vo")RptEvalItemVO vo);

    /**
     * 평가기준목록 정렬순서 변경
     *
     * @param vo  RptEvalItemVO
     * @return int
     */
    public int setEvalItemPosUpdate2(@Param("vo")RptEvalItemVO vo);

}
