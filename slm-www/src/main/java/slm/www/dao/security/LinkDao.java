package slm.www.dao.security;

import module.vo.jqgrid.SrcJQGridVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.LinkVO;

import java.util.List;
import java.util.Map;

/**
 * NMS 연계 관리
 * <p/>
 * User: 이준수
 * Date: 2017.05.17
 * Time: 오후 2:28
 */
public interface LinkDao {

    /**
     * 연계서비스(NMS) 코드 총 개수 가져오기
     *
     * @param srcCode 검색코드
     * @param vo      jqGrid 파라미터
     * @return 총 개수
     */
    public int getNmsCodeListCnt(@Param("srcCode") String srcCode, @Param("vo") SrcJQGridVO vo);

    /**
     * 연계서비스(NMS) 코드 목록 가져오기
     *
     * @param srcCode 검색코드
     * @param vo      jqGrid 파라미터
     * @return 연계서비스(NMS) 코드 목록
     */
    public List<LinkVO> getNmsCodeList(@Param("srcCode") String srcCode, @Param("vo") SrcJQGridVO vo);

    /**
     * 연계서비스(NMS) 코드 데이터 Update
     *
     * @param vo LinkVO
     * @return 쿼리 결과
     */
    public int setNmsCodeUpdate(@Param("vo") LinkVO vo);

    /**
     * 연계서비스(NMS) 코드 데이터 Update
     *
     * @param vo LinkVO
     * @return 쿼리 결과
     */
    public int setNmsDataCodeUpdate(@Param("vo") LinkVO vo);

    /**
     * 연계서비스(NMS) 데이터 목록 가져오기
     *
     * @param srcCode  검색코드
     * @param srcSDate 시작일
     * @param srcEDate 종료일
     * @param vo       jqGrid 파라미터
     * @return 연계서비스(NMS) 데이터 목록
     */
    public List<LinkVO> getNmsDataList(@Param("srcCode") String srcCode, @Param("srcSDate") String srcSDate, @Param("srcEDate") String srcEDate, @Param("vo") SrcJQGridVO vo, @Param("host_cd") String host_cd);

    /**
     * 연계서비스(NMS) 데이터 Update
     *
     * @param vo LinkVO
     * @return 쿼리 결과
     */
    public int setNmsDataUpdate(@Param("vo") LinkVO vo);


    /**
     * 연계서비스(NMS) 데이터 가져오기
     * - 엑셀로 출력할때 활용
     *
     * @param host_cd  HOST 코드
     * @param itm_cd   항목 코드
     * @param srcSDate 시작일
     * @param srcEDate 종료일
     * @param vo       jqGrid 파라미터
     * @return List<Map>
     */
    public List<Map<String, Object>> getNmsDataListReport(@Param("host_cd") String host_cd, @Param("itm_cd") String itm_cd, @Param("srcSDate") String srcSDate, @Param("srcEDate") String srcEDate, @Param("vo") SrcJQGridVO vo);


    /**
     * 연계서비스(NMS) 데이터 총 개수 가져오기
     *
     * @param srcCode  HOST 코드/항목 코드
     * @param vo       jqGrid 파라미터
     * @param srcSDate 시작일
     * @param srcEDate 종료일
     * @return 총 개수
     */
    public int getNmsDataListCnt(@Param("srcCode") String srcCode, @Param("vo") SrcJQGridVO vo, @Param("host_cd") String host_cd, @Param("srcSDate") String srcSDate, @Param("srcEDate") String srcEDate);
}
