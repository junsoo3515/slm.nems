package slm.www.dao.security;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.RptItemVO;

import java.util.List;

/**
 * <p/>
 * User: 이준수
 * Date: 2017.06.01
 * Time: 오전 9:31
 */
public interface RptItemDao {

    /**
     * 보고서 항목 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getRptTopicNmList();

    /**
     * Select2 보고서 항목 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getRptTopicNmSelect2(@Param("word") String word);

    /**
     * 외부연계 항목 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getLinkTopicList();

    /**
     * 항목 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getTopicList();

    /**
     * 장비  가져오기(Select2)
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getEquipSelect2(@Param("word") String word);

    /**
     * 장비그룹 연계 장비그룹 기본항목 가져오기(Select2)
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getEquipGrpSelect2(@Param("word") String word);

    /**
     * 보고서 유형 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getRptTypeList();

    /**
     * 기본항목관리 총 개수 가져오기
     *
     * @param srcWord  검색어
     * @param srcRptNm 보고서항목
     * @param srcType  항목유형
     * @return int
     */
    public int getReportTopicListCnt(@Param("srcWord") String srcWord, @Param("srcRptNm") String srcRptNm, @Param("srcType") String srcType, @Param("vo") SrcJQGridVO vo);

    /**
     * 기본항목관리 가져오기
     *
     * @param srcWord  검색어
     * @param srcRptNm 보고서항목
     * @param srcType  항목유형
     * @param vo       jqGrid 파라미터
     * @return List<RptItemVO>
     */
    List<RptItemVO> getReportTopicList(@Param("srcWord") String srcWord, @Param("srcRptNm") String srcRptNm, @Param("srcType") String srcType, @Param("vo") SrcJQGridVO vo);

    /**
     * 기본항목 상세정보 가져오기
     *
     * @param vo RptItemVO
     * @return RptItemVO
     */
    public RptItemVO getReportTopicInfo(@Param("vo") RptItemVO vo);

    /**
     * 기본항목 저장하기(Insert)
     *
     * @param vo RptItemVO
     * @return RptItemVO
     */
    public int setReportTopicAct(@Param("vo") RptItemVO vo);

    /**
     * 기본항목 저장하기(Update)
     *
     * @param vo RptItemVO
     * @return RptItemVO
     */
    public int setReportTopicUpdate(@Param("vo") RptItemVO vo);

    /**
     * S/W 점검항목 기초데이터 관리 총 개수 가져오기
     *
     * @param rpt_cd 보고서코드
     * @return RptItemVO
     */
    public int getSwTopicListCnt(@Param("rpt_cd") String rpt_cd);

    /**
     * S/W 점검항목 기초데이터 관리 가져오기
     *
     * @param rpt_cd 보고서코드
     * @param vo     jqGrid 파라미터
     * @return List<RptItemVO>
     */
    public List<RptItemVO> getSwTopicList(@Param("rpt_cd") String rpt_cd, @Param("vo") SrcJQGridVO vo);

    /**
     * S/W 점검항목 기초데이터 관리 저장하기(Insert)
     *
     * @param vo RptItemVO
     * @return int
     */
    public int setSwTopicAct(@Param("vo") RptItemVO vo);

    /**
     * S/W 점검항목 기초데이터 관리 저장하기(Update)
     *
     * @param vo RptItemVO
     * @return int
     */
    public int setSwTopicUpdate(@Param("vo") RptItemVO vo);


    /**
     * 장비그룹 연계 총 개수 가져오기
     *
     * @param rpt_topic_cd 항목코드
     * @return int
     */
    public int getEquipTopicListCnt(@Param("rpt_topic_cd") String rpt_topic_cd);


    /**
     * 장비그룹 연계 가져오기
     *
     * @param rpt_topic_cd 항목코드
     * @param vo           jqGrid 파라미터
     * @return List<RptItemVO>
     */
    List<RptItemVO> getEquipTopicList(@Param("rpt_topic_cd") String rpt_topic_cd, @Param("vo") SrcJQGridVO vo);

    /**
     * 장비그룹 연계 저장하기(Insert)
     *
     * @param vo RptItemVO
     * @return int
     */
    public int setEquipTopicAct(@Param("vo") RptItemVO vo);

    /**
     * 장비그룹 연계 저장하기(Update)
     *
     * @param vo RptItemVO
     * @return int
     */
    public int setEquipTopicUpdate(@Param("vo") RptItemVO vo);

    /**
     * 장비그룹 연계 삭제하기(Delete)
     *
     * @param vo RptItemVO
     * @return int
     */
    public int setEquipTopicDel(@Param("vo") RptItemVO vo);


}
