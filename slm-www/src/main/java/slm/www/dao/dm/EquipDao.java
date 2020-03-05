package slm.www.dao.dm;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import slm.www.vo.dm.EquipGrpVO;
import slm.www.vo.dm.EquipVO;
import slm.www.vo.dm.SparePartVO;

import java.util.List;
import java.util.Map;

/**
 * 운영대상 > 장비관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.25
 * Time: 오전 11:07
 */
public interface EquipDao {

    /**
     * 전체 장비 목록 가져오기
     * - 엑셀 일괄등록 기능에서 DB에 엑셀 ROW 별 장비가 존재하는지 판단하기 위해서 사용
     *
     * @return 장비 목록
     */
    public List<EquipVO> getEquipAllList();

    /**
     * 장비 총 개수 가져오기
     *
     * @param srcGrp 장비유형
     * @param srcNm 장비 명
     * @param vo     jqGrid 파라미터
     * @return 총 개수
     */
    public int getEquipListCnt(@Param("srcGrp") String srcGrp, @Param("srcNm") String srcNm, @Param("vo") SrcJQGridVO vo);

    /**
     * 장비 목록 가져오기
     *
     * @param srcGrp 장비유형
     * @param srcNm 장비 명
     * @param vo jqGrid 파라미터
     * @return 장비 목록
     */
    public List<EquipVO> getEquipList(@Param("srcGrp") String srcGrp, @Param("srcNm") String srcNm, @Param("vo") SrcJQGridVO vo);

    /**
     * 장비 상세정보 가져오기
     *
     * @param key 장비 코드(EQP_CD)
     * @return 장비 상세정보 정보
     */
    public EquipVO getEquipData(@Param("key") String key);

    /**
     * 장비 목록 가져오기(장비세부목록 & 엑셀 내보내기)
     *
     * @param grpCD       장비유형 코드
     * @param colPrefix   항목 앞에 prefix
     * @param arrEquipCD  장비 코드
     * @param arrTopicKey 항목 코드
     * @return List<Map> equip list export
     */
    public List<Map<String, Object>> getEquipListExport(@Param("grpCD") String grpCD, @Param("colPrefix") String colPrefix, @Param("arrEquipCD") List<String> arrEquipCD, @Param("arrTopicKey") List<String> arrTopicKey);

    /**
     * 장비 목록 가져오기(장비세부정보)
     *   - 일일점검현황의 장비별 세부정보에서 활용
     *
     * @param daSeq 보고서 고유번호
     * @param rptCD 보고서 코드
     * @param colPrefix 항목 앞에 prefix
     * @param arrTopicKey 항목 코드
     * @param vo    jqGrid 파라미터
     * @return List<Map>
     */
    public List<Map<String, Object>> getEquipListReport(@Param("daSeq") Long daSeq, @Param("rptCD") String rptCD, @Param("colPrefix") String colPrefix, @Param("arrTopicKey") List<String> arrTopicKey, @Param("vo") SrcJQGridVO vo);

    /**
     * 장비 기본항목 데이터 가져오기
     * - 장비그룹에 포함된 기본항목 및 입력 값
     *
     * @param eqpCd 장비 코드
     * @param grpCd 장비유형 코드
     * @param vo    jqGrid 파라미터
     * @return 기본항목 및 입력 데이터
     */
    public List<EquipGrpVO> getEquipGrpDataList(@Param("eqpCd") String eqpCd, @Param("grpCd") String grpCd, @Param("vo") SrcJQGridVO vo);

    /**
     * 장비 신규 코드 생성
     *
     * @return SLM_EQUIPMENT
     */
    public String getEquipCreateCd();

    /**
     * 장비 기본정보 저장 Insert
     *
     * @param vo EquipVO
     * @return 쿼리 결과
     */
    public int setEquipInsert(@Param("vo") EquipVO vo);

    /**
     * 장비 기본정보 저장 Update.
     *
     * @param vo EquipVO
     * @return 쿼리 결과
     */
    public int setEquipUpdate(@Param("vo") EquipVO vo);

    /**
     * 장비 기본정보 삭제 Update.
     *
     * @param key 장비코드
     * @return 쿼리 결과
     */
    public int setEquipDelete(@Param("key") String key);

    /**
     * 장비유형별 기본항목 데이터 존재 유무 판단
     *
     * @param key 장비코드
     * @return SLM_EQUIPGRP_DATA 존재 개수
     */
    public int getEquipGrpDataCnt(@Param("key") String key);

    /**
     * 장비유형별 기본항목 데이터 Insert
     *
     * @param key 장비 코드
     * @param vo  EquipGrpVO
     * @return 쿼리 결과
     */
    public int setEquipGrpDataInsert(@Param("key") String key, @Param("vo") EquipGrpVO vo);

    /**
     * 장비유형별 기본항목 데이터 Insert(엑셀 일괄 등록에서 사용)
     *
     * @param key      장비 코드
     * @param listData List<EquipGrpVO>
     * @return 쿼리 결과
     */
    public int setEquipGrpDataBatchInsert(@Param("key") String key, @Param("listData") List<EquipGrpVO> listData);

    /**
     * 장비유형별 기본항목 데이터 Update
     *
     * @param key 장비 코드
     * @param vo  EquipGrpVO
     * @return 쿼리 결과
     */
    public int setEquipGrpDataUpdate(@Param("key") String key, @Param("vo") EquipGrpVO vo);

    /**
     * 장비유형별 기본항목 데이터 Delete
     *
     * @param key 장비코드
     * @return 쿼리 결과
     */
    public int setEquipGrpDataDelete(@Param("key") String key);

    /**
     * 예비품 현황 총 개수 가져오기
     *
     * @param srcEquipCd 장비코드
     * @param vo         jqGrid 파라미터
     * @return 총 개수
     */
    public int getEquipSparePartListCnt(@Param("srcEquipCd") String srcEquipCd, @Param("vo") SrcJQGridVO vo);

    /**
     * 예비품 현황 목록 가져오기
     *
     * @param srcEquipCd 장비코드
     * @param vo         jqGrid 파라미터
     * @return 예비품 현황 목록
     */
    public List<SparePartVO> getEquipSparePartList(@Param("srcEquipCd") String srcEquipCd, @Param("vo") SrcJQGridVO vo);

    /**
     * 예비품 현황 Insert
     *
     * @param vo SparePartVO
     * @return 쿼리 결과
     */
    public int setEquipSparePartInsert(@Param("vo") SparePartVO vo);

    /**
     * SLM_EQUIPGRP_TOPIC List 가져오기
     * - 장비유형 / 코드명 / 코드값
     *
     * @param grpCD 장비유형 코드
     * @return List
     */
    public List<Map<String, String>> getEquipGrpTopicList(@Param("grpCD") String grpCD);
}
