package slm.www.dao.rmon;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.rmon.CheckVO;
import slm.www.vo.rmon.ReportVO;
import slm.www.vo.rmon.TroubleVO;

import java.util.List;
import java.util.Map;

/**
 * <p/>
 * User: 이종혁
 * Date: 2016.06.09
 * Time: 오후 09:56
 */
public interface ReportDao {

    /**
     * 월간주요현황 총 개수 가져오기
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param vo jqGrid 파라미터
     * @return int 총 개수
     */
    public int getRmonReportListCnt(@Param("sDate") String sDate, @Param("eDate") String eDate,@Param("vo") SrcJQGridVO vo);

    /**

     * 월간주요현황점검현황 목록 총개수가져오기
     * @param start_dt 시작일
     * @param end_dt 끝일
     * @return int 총 개수
     */
    public int getRmonReportCheckListCnt(@Param("start_dt") String start_dt,@Param("end_dt") String end_dt,  @Param("vo") SrcJQGridVO vo);

    /**
     * 월간주요현황 목록 가져오기
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param vo jqGrid 파라미터
     * @return List<ReportVO> 코드목록
     */
    public List<ReportVO> getRmonReportList(@Param("sDate") String sDate, @Param("eDate") String eDate,@Param("vo") SrcJQGridVO vo);

    /**
     * 월간주요현황점검현황 목록 가져오기
     * @param start_dt 시작일
     * @param end_dt 끝일
     * @param vo jqGrid 파라미터
     * @return List<CheckVO> 코드목록
     */
    public List<CheckVO> getRmonReportCheckList(@Param("start_dt") String start_dt,@Param("end_dt") String end_dt, @Param("vo") SrcJQGridVO vo);
    /**
     * 점검현황 등락 정보 가져오기
     * @param start_dt 시작일
     * @param end_dt 끝일
     * @return List<CheckVO> 코드목록
     */
    public CheckVO getRmonReportCheckDateUpDown(@Param("start_dt") String start_dt,@Param("end_dt") String end_dt);
    /**
     * 월간주요현황 당월,전월 시작일,끝일 가져오기
     * @param da_seq 리포트 코드
     * @return List<ReportVO> 코드목록
     */
    public List<ReportVO> getRmonReportDate(@Param("da_seq") String da_seq);


    /**
     * 월간주요현황 상세정보 가져오기
     *
     * @param key 월간주요현황 고유 키
     * @return ReportVO
     */
    public ReportVO getRmonReportData(@Param("key") String key);

    /**
     * 월간주요현황 Insert
     *
     * @param vo ReportVO
     * @return 쿼리 결과
     */
    public int setReportInsert(@Param("vo") ReportVO vo);

    /**
     * 월간주요현황 Update
     *
     * @param vo ReportVO
     * @return 쿼리 결과
     */
    public int setReportUpdate(@Param("vo") ReportVO vo);


    /**
     * 월간주요현황 Delete
     *
     * @param vo ReportVO
     * @return 쿼리 결과
     */
    public int setReportDelete(@Param("vo") ReportVO vo);

    /**
     * 월간주요현황 장애세부내역 목록 총 개수 가져오기
     * @return int 총 개수
     */
    public int getTroubleDetailListCnt(@Param("start_dt") String start_dt, @Param("end_dt") String end_dt, @Param("vo") SrcJQGridVO vo);


    /**
     * 월간주요현황 장애세부내역 목록 가져오기
     * @return List<TroubleVO>
     */
    public List<TroubleVO> getTroubleDetailList(@Param("start_dt") String start_dt, @Param("end_dt") String end_dt, @Param("vo") SrcJQGridVO vo);

    /**
     * 월별 헤더 및 컬럼명 가져오기
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getTroubleAmt(@Param("start_dt") String start_dt, @Param("subStart_dt") String subStart_dt);

    /**
     * 월간주요현황 장애현황표 목록 가져오기
     * @return List<TroubleVO>
     */
    public List<TroubleVO> getTroubleGridList(@Param("start_dt")String start_dt, @Param("end_dt")String end_dt, @Param("vo")SrcJQGridVO vo);

    /**
     * 월간주요현황 장애현황표(장애 발생일 포함) 목록 가져오기
     * @return List<TroubleVO>
     */
    public List<Map<String,Object>> getTroubleDayList(@Param("start_dt")String start_dt,@Param("end_dt")String end_dt, @Param("colPrefix")String colPrefix, @Param("arrRptCD") List<String> arrRptCD,@Param("arrDayKey") List<String> arrDayKey);
}
