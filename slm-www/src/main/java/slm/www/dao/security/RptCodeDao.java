package slm.www.dao.security;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.RptCodeVO;

import java.util.List;

/**
 * 보고서 코드 관리
 * <p/>
 * User: 이준수
 * Date: 2017.06.07
 * Time: 오전 9:35
 */
public interface RptCodeDao {

    /**
     * 보고서 코드 총 개수 가져오기
     *
     * @param srcWord 검색어
     * @param vo      jqGrid 파라미터
     * @return int
     */
    public int getReportCodeListCnt(@Param("srcWord") String srcWord, @Param("vo") SrcJQGridVO vo);

    /**
     * 보고서 코드 목록 가져오기
     *
     * @param srcWord 검색어
     * @param vo      jqGrid 파라미터
     * @return List<RptCodeVO>
     */
    public List<RptCodeVO> getReportCodeList(@Param("srcWord") String srcWord, @Param("vo") SrcJQGridVO vo);

    /**
     * 장비연계 총 개수 가져오기
     *
     * @param vo jqGrid 파라미터
     * @return int
     */
    public int getReportEquipMapListCnt(@Param("vo") SrcJQGridVO vo, @Param("rpt_cd") String rpt_cd);

    /**
     * 장비연계 목록 가져오기
     *
     * @param vo jqGrid 파라미터
     * @return List<RptCodeVO>
     */
    public List<RptCodeVO> getReportEquipMapList(@Param("vo") SrcJQGridVO vo, @Param("rpt_cd") String rpt_cd);

    /**
     * 보고서 코드 관리 Insert
     *
     * @param vo RptCodeVO
     * @return 쿼리 결과
     */
    public int setReportCodeInsert(@Param("vo") RptCodeVO vo);

    /**
     * 보고서 코드 관리 Update
     *
     * @param vo RptCodeVO
     * @return 쿼리 결과
     */
    public int setReportCodeUpdate(@Param("vo") RptCodeVO vo);

    /**
     * 장비연계 Insert
     *
     * @param vo RptCodeVO
     * @return 쿼리 결과
     */
    public int setReportEquipMapInsert(@Param("vo") RptCodeVO vo);

    /**
     * 장비연계 Update
     *
     * @param vo RptCodeVO
     * @return 쿼리 결과
     */
    public int setReportEquipMapUpdate(@Param("vo") RptCodeVO vo);

    /**
     * SLA 보고서 요약 Delete
     *
     * @param vo RptCodeVO
     * @return 쿼리 결과
     */
    public int setReportEquipMapDelete(@Param("vo") RptCodeVO vo);

    /**
     * 부모 코드 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getHigRptCdList();

    /**
     * 장비 가져오기(Select2)
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getEquipSelect2(@Param("word") String word);

    /**
     * 장비연계 정렬순서 변경
     *
     * @param vo  RptCodeVO
     * @return int
     */
    public int setEqpMapPosUpdate1(@Param("vo")RptCodeVO vo);

    /**
     * 장비연계 정렬순서 변경
     *
     * @param vo  RptCodeVO
     * @return int
     */
    public int setEqpMapPosUpdate2(@Param("vo")RptCodeVO vo);
}
