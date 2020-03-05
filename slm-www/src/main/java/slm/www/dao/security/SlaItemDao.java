package slm.www.dao.security;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.SlaItemVO;

import java.util.List;

/**
 * SLA 항목관리
 * <p/>
 * User: 이준수
 * Date: 2017.05.24
 * Time: 오전 9:50
 */
public interface SlaItemDao {

    /**
     * 항목이름 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getTopicNmList();

    /**
     * 평가관리 기준 개수 가져오기
     *
     * @param srcCode 코드,명칭
     * @param vo      jqGrid 파라미터
     * @return int
     */
    public int getSlaEvalListCnt(@Param("srcCode") String srcCode, @Param("vo") SrcJQGridVO vo);

    /**
     * 평가기준 관리 목록 가져오기
     *
     * @param srcCode 코드,명칭
     * @param vo      jqGrid 파라미터
     * @return List<SlaItemVO>
     */
    public List<SlaItemVO> getSlaEvalList(@Param("srcCode") String srcCode, @Param("vo") SrcJQGridVO vo);

    /**
     * 평가기준 상세정보 가져오기
     *
     * @param eval_cd 평가지표 코드
     * @return SlaItemVO
     */
    public SlaItemVO getSlaEvalData(@Param("eval_cd") String eval_cd);

    /**
     * 평가기준 등록하기
     *
     * @param vo SlaItemVO
     * @return void
     */
    public void setSlaEvalAct(@Param("vo") SlaItemVO vo);

    /**
     * 평가기준 수정하기
     *
     * @param vo SlaItemVO
     * @return void
     */
    public void setSlaEvalUpdate(@Param("vo") SlaItemVO vo);

    /**
     * 보고서 연결 개수 가져오기
     *
     * @param eval_cd 평가지표 코드
     * @return int
     */
    public int getReportConnListCnt(@Param("eval_cd") String eval_cd);

    /**
     * 보고서 연결 목록 가져오기
     *
     * @param eval_cd 평가지표 코드
     * @param vo      jqGrid 파라미터
     * @return List<SlaItemVO>
     */
    public List<SlaItemVO> getReportConnList(@Param("eval_cd") String eval_cd, @Param("vo") SrcJQGridVO vo);

    /**
     * 보고서 연결 삭제하기
     *
     * @param vo SlaItemVO
     * @return void
     */
    public void setReportConnDel(@Param("vo") SlaItemVO vo);

    /**
     * 보고서 연결 등록하기
     *
     * @param vo SlaItemVO
     * @return void
     */
    public int setReportConnAct(@Param("vo") SlaItemVO vo);

    /**
     * 보고서 연결 수정하기
     *
     * @param vo SlaItemVO
     * @return void
     */
    public void setReportConnUpdate(@Param("vo") SlaItemVO vo);
}
