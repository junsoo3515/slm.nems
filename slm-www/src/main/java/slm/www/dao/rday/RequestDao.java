package slm.www.dao.rday;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import slm.www.vo.rday.MeaSureVO;
import slm.www.vo.rday.RequestVO;

import java.util.List;

/**
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
public interface RequestDao {

    /**
     * 요청사항 총 개수 가져오기
     * @param srcHeadGrp 검색 기준
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param fin_fl 장애완료
     * @param srcGrp 장비구분
     * @param vo jqGrid 파라미터
     * @return int 총 개수
     */
    public int getRdayRequestListCnt(@Param("srcHeadGrp") String srcHeadGrp, @Param("sDate") String sDate, @Param("eDate") String eDate, @Param("fin_fl") String fin_fl, @Param("srcGrp") String srcGrp,@Param("srcRequestGrp") String srcRequestGrp, @Param("vo") SrcJQGridVO vo);

    /**

     * 요청사항조치사항 총 개수 가져오기
     * @param cause_seq 요청 코드
     *
     * @return int 총 개수
     */
    public int getRdayRequestMeasureListCnt(@Param("cause_seq") String cause_seq, @Param("vo") SrcJQGridVO vo);

    /**
     * 요청사항 목록 가져오기
     * @param srcHeadGrp 검색 기준
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param fin_fl 장애완료
     * @param srcGrp 장비구분
     * @param vo jqGrid 파라미터
     * @return List<RequestVO> 코드목록
     */
    public List<RequestVO> getRdayRequestList(@Param("srcHeadGrp") String srcHeadGrp, @Param("sDate") String sDate, @Param("eDate") String eDate, @Param("fin_fl") String fin_fl, @Param("srcGrp") String srcGrp,@Param("srcRequestGrp") String srcRequestGrp, @Param("vo") SrcJQGridVO vo);

    /**
     * 요청사항조치사항 목록 가져오기
     * @param cause_seq 장애코드
     * @param vo jqGrid 파라미터
     * @return List<MeaSureVO> 코드목록
     */
    public List<MeaSureVO> getRdayRequestMeasureList(@Param("cause_seq") String cause_seq, @Param("vo") SrcJQGridVO vo);


    /**
     * 요청사항 상세정보 가져오기
     *
     * @param key 요청사항 고유 키
     * @return RequestVO
     */
    public RequestVO getRdayRequestData(@Param("key") String key);
    /**
     * 요청사항 Insert
     *
     * @param vo RequestVO
     * @return 쿼리 결과
     */
    public int setRequestInsert(@Param("vo") RequestVO vo);

    /**
     * 요청사항 Update
     *
     * @param vo RequestVO
     * @return 쿼리 결과
     */
    public int setRequestUpdate(@Param("vo") RequestVO vo);
    /**
     * 요청사항조치사항 Insert
     *
     * @param vo MeaSureVO
     * @return 쿼리 결과
     */
    public int setRequestMeasureInsert(@Param("vo") MeaSureVO vo);

    /**
     * 요청사항조치사항 Update
     *
     * @param vo MeaSureVO
     * @return 쿼리 결과
     */
    public int setRequestMeasureUpdate(@Param("vo") MeaSureVO vo);

    /**
     * 요청사항 Delete
     *
     * @param vo RequestVO
     * @return 쿼리 결과
     */
    public int setRequestDelete(@Param("vo") RequestVO vo);
    /**
     * 요청사항조치사항 Delete
     *
     * @param vo MeaSureVO
     * @return 쿼리 결과
     */
    public int setRequestMeasureDelete(@Param("vo") MeaSureVO vo);
}
