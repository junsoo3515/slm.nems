package slm.www.dao.rsla;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import slm.www.vo.rsla.SlaReportBandWidthVO;
import slm.www.vo.rsla.SlaReportEvalVO;
import slm.www.vo.rsla.SlaReportSummaryVO;
import slm.www.vo.rsla.SlaReportVO;

import java.util.List;

/**
 * SLA 보고서 > SLA 보고서 관리
 * <p/>
 * User: 현재호
 * Date: 2016.06.08
 * Time: 오후 3:31
 */
public interface SlaReportDao {

    /**
     * SLA 보고서 목록 총 개수 가져오기
     *
     * @param sDate    시작일
     * @param eDate    종료일
     * @param vo       jqGrid 파라미터
     * @return 총 개수
     */
    public int getSLAReportListCnt(@Param("sDate") String sDate, @Param("eDate") String eDate, @Param("vo") SrcJQGridVO vo);

    /**
     * SLA 보고서 목록 가져오기
     *
     * @param sDate 시작일
     * @param eDate 종료일
     * @param vo jqGrid 파라미터
     * @return SLA 보고서 목록
     */
    public List<SlaReportVO> getSLAReportList(@Param("sDate") String sDate, @Param("eDate") String eDate, @Param("vo") SrcJQGridVO vo);

    /**
     * 대역폭 사용률 가져오기
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return SLA 대역폭 사용률 목록
     */
    public List<SlaReportBandWidthVO> getBandWidthList(@Param("key") Long key);

    /**
     * SLA 보고서 가져오기
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return SLA 보고서 정보
     */
    public SlaReportVO getSLAReportData(@Param("key") String key);

    /**
     * SLA 항목별 평가기준 가져오기
     *
     * @return SLA 항목별 평가기준 목록
     */
    public List<SlaReportEvalVO> getSLAReportEvalList();

    /**
     * SLA 항목별 평가결과 가져오기
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return SLA 항목별 평가결과 목록
     */
    public List<SlaReportSummaryVO> getSLAReportSummaryList(@Param("key") Long key);

    /**
     * SLA 보고서 Insert
     *
     * @param vo DaReportVO
     * @return 쿼리 결과
     */
    public int setSLAReportInsert(@Param("vo") SlaReportVO vo);

    /**
     * SLA 보고서 Update
     *   - 일마감 부분만 update 처리
     *
     * @param vo SlaReportVO
     * @return 쿼리 결과
     */
    public int setSLAReportUpdate(@Param("vo") SlaReportVO vo);

    /**
     * SLA 보고서 요약 생성 Insert
     *
     * @param key           보고서 고유번호
     * @param seq           요약 최대 고유번호
     * @param firstReportCD 시작 보고서 코드
     * @return 쿼리 결과
     */
    public int setSLAReportSummaryInsert(@Param("key") Long key, @Param("seq") Long seq, @Param("firstReportCD") String firstReportCD);

    /**
     * SLA 보고서 요약 결과 점수 Update
     *
     * @param vo SlaReportSummaryVO
     * @return 쿼리 결과
     */
    public int setSLAReportSummaryUpdate(@Param("vo") SlaReportSummaryVO vo);

    /**
     * SLA 백업성공율
     * : SLM_SLA_BACKSUCRATE
     * : MERGE INTO로 처리
     *
     * @param vo SlaReportVO
     * @return 쿼리 결과
     */
    public int setSLAReportBackupSuccessRateMergeAct(@Param("vo") SlaReportVO vo);

    /**
     * SLA 대역폭 사용률 정보
     * : SLM_SLA_BANDWIDTH
     * : MERGE INTO로 처리
     *
     * @param vo SlaReportBandWidthVO
     * @param firstSeq 시작 고유번호(INSERT 시 처리)
     * @return 쿼리 결과
     */
    public int setSLAReportBandWidthMergeAct(@Param("vo") SlaReportBandWidthVO vo, @Param("firstSeq") Long firstSeq);

    /**
     * 예비품 현황 Insert/Update
     * : SLM_EQUIPSPARE_DATA
     * : MERGE INTO로 처리(SLM_EQUIPSPARE_PARTS, SLM_DA_REPORTMAP, SLM_DA_REPORT, SLM_EQUIPMENT 참조)
     *
     * @param key SLA 고유번호
     * @param seq 예비품 현황 마지막 고유번호
     * @param arrDate 검색일자 배열
     * @param maxDate 최대일자(검색일자의 최대 값)
     * @return 쿼리 결과
     */
    public int setSLAReportEquipSpareDataMergeAct(@Param("key") Long key, @Param("seq") Long seq, @Param("arrDate") List<String> arrDate, @Param("maxDate") String maxDate);

    /**
     * SLA 보고서 Delete
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return 쿼리 결과
     */
    public int setSLAReportDelete(@Param("key") Long key);

    /**
     * SLA 보고서 요약 Delete
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setSLAReportSumDelete(@Param("key") Long key);

    /**
     * 예비품 현황 Delete
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return 쿼리 결과
     */
    public int setSLAReportEquipSpareDataDelete(@Param("key") Long key);

    /**
     * 백업 성공률 정보 Delete
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return 쿼리 결과
     */
    public int setSLAReportBackupSuccessRateDelete(@Param("key") Long key);

    /**
     * 대역폭 사용률 정보 Delete
     *
     * @param key 보고서 고유번호(SLA_SEQ)
     * @return 쿼리 결과
     */
    public int setSLAReportBandWidthDelete(@Param("key") Long key);

}
