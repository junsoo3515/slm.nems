package slm.www.dao.rday;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import slm.www.vo.rday.DaReportSummaryVO;
import slm.www.vo.rday.DaReportVO;
import slm.www.vo.rday.EquipMainDataVO;

import java.util.List;
import java.util.Map;

/**
 * 일일보고서 > 일일점검현황
 * <p/>
 * User: 현재호
 * Date: 2016.05.02
 * Time: 오후 4:03
 */
public interface InspectDao {

    /**
     * 일일점검현황 총 개수 가져오기
     *
     * @param gubunKey 구분코드
     * @param sDate    시작일
     * @param eDate    종료일
     * @param vo       jqGrid 파라미터
     * @return 총 개수
     */
    public int getReportListCnt(@Param("gubunKey") String gubunKey, @Param("sDate") String sDate, @Param("eDate") String eDate, @Param("vo") SrcJQGridVO vo);

    /**
     * 일일점검현황 목록 가져오기
     *
     * @param gubunKey 구분코드
     * @param sDate    시작일
     * @param eDate    종료일
     * @param vo       jqGrid 파라미터
     * @return 일일점검현황 목록
     */
    public List<DaReportVO> getReportList(@Param("gubunKey") String gubunKey, @Param("sDate") String sDate, @Param("eDate") String eDate, @Param("vo") SrcJQGridVO vo);

    /**
     * 일일점검 데이터 상세정보 가져오기
     *
     * @param key 보고서 일련번호
     * @return 일일점검현황 상세정보
     */
    public DaReportVO getReportData(@Param("key") Long key);

    /**
     * 점검현황 중복확인
     *
     * @param gubunKey  구분코드
     * @param briefDate 보고일
     * @return 점검현황 존재 개수
     */
    public int getReportChk(@Param("gubunKey") String gubunKey, @Param("briefDate") String briefDate);

    /**
     * 점검현황 중 일 마감 처리된 마지막 보고서 일련번호 가져오기
     *
     * @param briefDate 발생일(yyyymmdd)
     * @return 보고서 일련번호
     */
    public long getReportLastFinSeq(@Param("briefDate") String briefDate);

    /**
     * 점검현황 Insert
     *
     * @param vo DaReportVO
     * @return 쿼리 결과
     */
    public int setReportInsert(@Param("vo") DaReportVO vo);

    /**
     * 점검현황 Update
     *   - 일마감 부분만 update 처리
     *
     * @param vo DaReportVO
     * @return 쿼리 결과
     */
    public int setReportUpdate(@Param("vo") DaReportVO vo);

    /**
     * 보고서-장비 매핑 생성 Insert
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportMapInsert(@Param("key") Long key);

    /**
     * 일/월 보고서 요약 생성 Insert
     *
     * @param key           보고서 고유번호
     * @param seq           요약 최대 고유번호
     * @param firstReportCD 시작 보고서 코드
     * @param sDate 시작일(yyyymmdd)
     * @param eDate 종료일(yyyymmdd)
     * @return 쿼리 결과
     */
    public int setReportSummaryInsert(@Param("key") Long key, @Param("seq") Long seq, @Param("firstReportCD") String firstReportCD, @Param("sDate") String sDate, @Param("eDate") String eDate);

    /**
     * 장비별 유지보수 입력 초기 값 Insert
     *
     * @param key       보고서 고유번호
     * @param seq       장비별 유지보수 입력 마지막 고유번호
     * @param briefDate 발생일(yyyymmdd)
     * @return 쿼리 결과
     */
    public int setReportEquipDataInsert(@Param("key") Long key, @Param("seq") Long seq, @Param("briefDate") String briefDate);

    /**
     * 기본항목 매핑 테이블에 있는 자료 기본 값 일괄 Update
     * : SLM_EQUIPMAIN_DATA
     * : MERGE INTO로 처리(SLM_TOPIC_MAP 참조)
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportEquipDataMergeUpdate(@Param("key") Long key);

    /**
     * 기존 일마감 처리된 기초데이터 기준으로 값 일괄 Update
     * : SLM_EQUIPMAIN_DATA
     * : MERGE INTO로 처리(SLM_EQUIPMAIN_DATA 참조)
     *
     * @param key 보고서 고유번호
     * @param oldKey 보고서 고유번호(fin_fl = 'Y'의 마지막)
     * @return 쿼리 결과
     */
    public int setReportEquipDataCopyMergeUpdate(@Param("key") Long key, @Param("oldKey") Long oldKey);

    /**
     * 장애처리에 있는 요약 항목 'issue' 기본 값 일괄 Update
     * : SLM_EQUIPMAIN_DATA
     * : MERGE INTO로 처리(SLM_DISORDER 참조)
     *
     * @param key   보고서 고유번호
     * @param sDate 시작일(yyyymmdd)
     * @param eDate 종료일(yyyymmdd)
     * @return 쿼리 결과
     */
    public int setReportEquipDataIssueMergeUpdate(@Param("key") Long key, @Param("sDate") String sDate, @Param("eDate") String eDate);

    /**
     * 외부시스템 연동 데이터 일괄 Update
     * : SLM_EQUIPMAIN_DATA
     * : MERGE INTO로 처리(LINK_SYSTEM_CODE, LINK_SYSTEM_DATA 참조)
     *
     * @param key     보고서 고유번호
     * @param srcDate 검색일(yyyymmdd)
     * @return 쿼리 결과
     */
    public int setReportEquipDataLinkSystemMergeUpdate(@Param("key") Long key, @Param("srcDate") String srcDate);

    /**
     * 나머지 eng_nm = 'name'인 항목 장비 명칭 일괄 Update
     * : SLM_EQUIPMAIN_DATA
     * : MERGE INTO로 처리(SLM_EQUIPMAIN_DATA 참조)
     *
     * @param key     보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportEquipDataEtcMergeUpdate(@Param("key") Long key);

    /**
     * S/W 유지보수 입력 초기 값 Insert
     *
     * @param key       보고서 고유번호
     * @param seq       S/W 유지보수 입력 마지막 고유번호
     * @param briefDate 발생일(yyyymmdd)
     * @return 쿼리 결과
     */
    public int setReportSoftWareDataInsert(@Param("key") Long key, @Param("seq") Long seq, @Param("briefDate") String briefDate);

    /**
     * 점검현황 Delete
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportDelete(@Param("key") Long key);

    /**
     * 보고서-장비 매핑 Delete
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportMapDelete(@Param("key") Long key);

    /**
     * 일/월 보고서 요약 Delete
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportSummaryDelete(@Param("key") Long key);

    /**
     * 장비별 유지보수 입력 값 Delete
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportEquipDataDelete(@Param("key") Long key);

    /**
     * S/W 유지보수 입력 값 Delete
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportSoftWareDataDelete(@Param("key") Long key);

    /**
     * 점검요약정보 목록 가져오기
     *
     * @param key 보고서 고유번호
     * @return 점검요약정보 목록
     */
    public List<DaReportSummaryVO> getReportSummaryList(@Param("key") Long key);

    /**
     * 점검요약정보 보고서 유형 별 항목 현황 가져오기
     *
     * @param key 보고서 고유번호
     * @return 점검요약정보 보고서 유형 별 항목 현황 목록
     */
    public List<Map<String, String>> getReportSummaryItmList(@Param("key") Long key);

    /**
     * 점검요약정보 보고서 유형 별 항목 현황 가져오기
     *
     * @param key 보고서 고유번호
     * @param arrRptCD 보고서 코드
     * @param arrEngNm 항목 코드(영어 이름)
     * @return 점검요약정보 보고서 유형 별 항목 현황 목록
     */
    public List<Map<String, String>> getReportSummaryItmAllList(@Param("key") String key, @Param("arrRptCD") List<String> arrRptCD, @Param("arrEngNm") List<String> arrEngNm);

    /**
     * 점검요약정보 보고서 유형 별 항목 현황 별 항목기준 가져오기
     *
     * @param rptCD 보고서 코드
     * @return 점검요약정보 보고서 유형 별 항목 현황 별 항목기준 목록
     */
    public List<Map<String, String>> getReportSummaryItmExprList(@Param("rptCD") String rptCD);

    /**
     * 점검요약정보 데이터 Update
     *
     * @param key 보고서 고유번호
     * @param vo  DaReportSummaryVO
     * @return 쿼리 결과
     */
    public int setReportSummaryDataUpdate(@Param("key") Long key, @Param("vo") DaReportSummaryVO vo);

    /**
     * 점검요약정보 데이터(비정상 항목 현황) Update
     *
     * @param vo 결과
     * @return 쿼리 결과
     */
    public int setReportSummaryDataAbnormalItmUpdate(@Param("vo") Map<String, Object> vo);

    /**
     * 점검요약정보 데이터(비정상 항목 현황) 장비별 세부정보에서 데이터 수정 후 바로 Update
     *
     * @param summ_seq 요약 고유번호
     * @param da_seq 보고서 고유번호
     * @param rpt_cd 보고서 코드
     * @param cnt 비정상 항목 수
     * @return 쿼리 결과
     */
    public int setReportSummaryDataAbnormalDirectUpdate(@Param("summ_seq") Long summ_seq, @Param("da_seq") Long da_seq, @Param("rpt_cd") String rpt_cd, @Param("cnt") Long cnt);

    /**
     * 점검요약정보 데이터 동기화 Update
     * : SLM_DA_REPORTSUM
     * : MERGE INTO로 처리(SLM_DISORDER, SLM_DA_REPORTMAP 참조)
     * : 항목현황 데이터는 초기화(다른 곳에서 동기화 처리 : 성능문제 때문에)
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportSummaryDataSyncUpdate(@Param("key") String key);

    /**
     * 점검요약정보 데이터 동기화 Update(장비)
     * : SLM_DA_REPORTSUM
     * : MERGE INTO로 처리(SLM_EQUIPMAIN_DATA, SLM_REPORT_TOPIC 참조)
     * : 전체항목 현황, select 값이 'N'으로 적용 된 값들 항목현황 데이터 갱신
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportSummaryDataSyncItmCntEqpUpdate(@Param("key") String key);

    /**
     * 점검요약정보 데이터 동기화 Update(SW)
     * : SLM_DA_REPORTSUM
     * : MERGE INTO로 처리(SLM_EQUIPMAIN_DATA, SLM_REPORT_TOPIC 참조)
     * : 전체항목 현황, select 값이 'N'으로 적용 된 값들 항목현황 데이터 갱신
     *
     * @param key 보고서 고유번호
     * @return 쿼리 결과
     */
    public int setReportSummaryDataSyncItmCntSwUpdate(@Param("key") String key);

    /**
     * S/W 목록 가져오기(장비세부정보)
     *   - 일일점검현황의 장비별 세부정보에서 활용
     *
     * @param daSeq 보고서 고유번호
     * @param rptCD 보고서 코드
     * @param colPrefix 항목 앞에 prefix
     * @param arrTopicKey 항목 코드
     * @param vo    jqGrid 파라미터
     * @return List<Map>
     */
    public List<Map<String, Object>> getSWListReport(@Param("daSeq") Long daSeq, @Param("rptCD") String rptCD, @Param("colPrefix") String colPrefix, @Param("arrTopicKey") List<String> arrTopicKey, @Param("vo") SrcJQGridVO vo);

    /**
     * 보고서별 기본항목 가져오기 List 가져오기
     *   - SLM_REPORT_TOPIC
     *
     * @param rptCD 장비유형 코드
     * @return List {TOPIC_CD, TOPIC_NM, TOPIC_TYPE, MOD_LOCK_FL}
     */
    public List<Map<String, String>> getReportTopicList(@Param("rptCD") String rptCD);

    /**
     * 장비별 유지보수 입력 값 Update
     *
     * @param vo EquipMainDataVO
     * @return 쿼리 결과
     */
    public int setDetailEquipCellUpdate(@Param("vo") EquipMainDataVO vo);

    /**
     * S/W 유지보수 입력 값 Update
     *
     * @param vo EquipMainDataVO
     * @return 쿼리 결과
     */
    public int setDetailSoftwareCellUpdate(@Param("vo") EquipMainDataVO vo);

    /**
     * 장비별 세부정보 엑셀 내보내기 엑셀 파일명 생성을 위한 데이터 추출
     *   - SLM_REPORT_CODE
     *
     * @param daSeq 보고서 고유번호
     * @param rptCD 보고서 코드
     * @return List { BRIEF_DT, GRP_NM, RPT_NM }
     */
    public Map<String, String> getExportExcelTitle(@Param("daSeq") Long daSeq, @Param("rptCD") String rptCD);
}
