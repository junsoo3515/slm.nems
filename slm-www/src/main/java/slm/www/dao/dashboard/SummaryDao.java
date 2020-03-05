package slm.www.dao.dashboard;

import org.apache.ibatis.annotations.Param;
import slm.www.vo.dashboard.DashBoardDailyVO;
import slm.www.vo.dashboard.DashBoardSlaVO;

import java.util.List;

/**
 * <p/>
 * User: 이종혁
 * Date: 2016.06.22
 * Time: 오후 09:56
 */
public interface SummaryDao {


    /**
     * 대쉬보드 일일보고서 리포트 항목현황 가져오기
     *
     * @param sDate 시작일
     * @param eDate 끝일
     * @return 점검요약정보 보고서 유형 별 항목 현황 목록
     */
    public List<DashBoardDailyVO> getDashDailySummaryInfo(@Param("sDate") String sDate,@Param("eDate") String eDate);

    /**
     * 대쉬보드 일일보고서 리포트 정보 가져오기
     *
     * @param sDate 시작일
     * @param eDate 끝일
     * @return 점검요약정보 보고서 유형 별 항목 현황 목록
     */
    public List<DashBoardDailyVO> getDashDailyReportInfo(@Param("sDate") String sDate,@Param("eDate") String eDate);

    /**
     * 대쉬보드 SLA 리포트 항목현황 가져오기
     *
     * @param sDate 시작일
     * @param eDate 끝일
     * @return 점검요약정보 보고서 유형 별 항목 현황 목록
     */
    public List<DashBoardSlaVO> getDashSlaSummaryInfo(@Param("sDate") String sDate,@Param("eDate") String eDate);
    /**
     * 대쉬보드 SLA 리포트 정보 가져오기
     *
     * @param sDate 시작일
     * @param eDate 끝일
     * @return 점검요약정보 보고서 유형 별 항목 현황 목록
     */
    public List<DashBoardSlaVO> getDashSlaReportInfo(@Param("sDate") String sDate,@Param("eDate") String eDate);

    /**
     * 대쉬보드 운영지수 가져오기
     *
     * @return 운영지수
     */
    public DashBoardSlaVO getOpIndexData();
}
