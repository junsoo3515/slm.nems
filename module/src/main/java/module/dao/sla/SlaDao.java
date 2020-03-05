package module.dao.sla;

import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SlaDao {

    /**
     * 장애발생건수 가져오기
     *   - 천재지변으로 인한 장애는 제외
     *
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @param sDate 시작일(yyyyMMdd)
     * @param eDate 종료일(yyyyMMdd)
     * @return 데이터 값(ALL_CNT : 전체 값, NORMAL_CNT : 정상 값)
     */
    public Map<String, Integer> getDisOrderMngData(@Param("arrDate") List<String> arrDate, @Param("sDate") String sDate, @Param("eDate") String eDate);

    /**
     * 중복장애건수 가져오기
     *   - 천재지변으로 인한 장애는 제외
     *
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @param sDate 시작일(yyyyMMdd)
     * @param eDate 종료일(yyyyMMdd)
     * @param minCnt 최소 중복개수 이상
     * @return 데이터 값
     */
    public Map<String, String> getDupDisOrderNumData(@Param("arrDate") List<String> arrDate, @Param("sDate") String sDate, @Param("eDate") String eDate, @Param("minCnt") int minCnt);

    /**
     * 백업성공률
     *   - 천재지변으로 인한 장애는 제외
     *
     * @param key SLA 고유번호
     * @return 데이터 값
     */
    public Map<String, Object> getBackupSuccessRateData(@Param("key") Long key);

    /**
     * 장애 규명율 : 원인규명완료 장애 건수 가져오기
     *   - 천재지변으로 인한 장애는 제외
     *
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @param sDate 시작일(yyyyMMdd)
     * @param eDate 종료일(yyyyMMdd)
     * @return 데이터 값
     */
    public int getDisOrderExamRateData(@Param("arrDate") List<String> arrDate, @Param("sDate") String sDate, @Param("eDate") String eDate);

    /**
     * 시스템 성능관리 : 총 장애 시간 가져오기
     *
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @param sDate 시작일(yyyyMMdd)
     * @param eDate 종료일(yyyyMMdd)
     * @return 데이터 값
     */
    public double getDisOrderAllHourData(@Param("arrDate") List<String> arrDate, @Param("sDate") String sDate, @Param("eDate") String eDate);

    /**
     * 대역폭 사용률
     *
     * @param key SLA 고유번호
     * @return 데이터 값
     */
    public List<Map<String, Object>> getBandWidthUseData(@Param("key") Long key);

    /**
     * 서비스요청 사항(일반유형) 가져오기
     *
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @param sDate 시작일(yyyyMMdd)
     * @param eDate 종료일(yyyyMMdd)
     * @return 데이터 값(REQ_CNT : 요청 건수, FIN_CNT : 완료 건수)
     */
    public Map<String, Integer> getServiceReqMngRateData(@Param("arrDate") List<String> arrDate, @Param("sDate") String sDate, @Param("eDate") String eDate);

    /**
     * 현장점검 장비현황 가져오기
     *
     * @param arrDate 검색일자(일일보고에 해당하는) 배열
     * @return 데이터 값(TOT_CNT : 전체 건수, PRO_CNT : 현장점검 실시 장비 건수)
     */
    public Map<String, Integer> getSiteCheckRateData(@Param("arrDate") List<String> arrDate);
}
