package slm.www.dao.rday;

import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.ibatis.annotations.Param;
import slm.www.vo.rday.MeaSureVO;
import slm.www.vo.rday.TroubleShootVO;

import java.util.List;

/**
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
public interface TroubleShootDao {

    /**
     * 장애처리 총 개수 가져오기
     * @param srcHeadGrp 검색 기준
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param fin_fl 장애완료
     * @param srcGrp 장비구분
     * @param vo jqGrid 파라미터
     * @return int 총 개수
     */
    public int getRdayTroubleShootListCnt(@Param("srcHeadGrp") String srcHeadGrp, @Param("sDate") String sDate,@Param("eDate") String eDate,@Param("fin_fl") String fin_fl, @Param("srcGrp") String srcGrp,@Param("vo") SrcJQGridVO vo);

    /**

     * 장애처리조치사항 총 개수 가져오기
     * @param cause_seq 장애 코드
     *
     * @return int 총 개수
     */
    public int getRdayTroubleShootMeasureListCnt(@Param("cause_seq") String cause_seq,@Param("vo") SrcJQGridVO vo);

    /**
     * 장애처리 목록 가져오기
     * @param srcHeadGrp 검색 기준
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param fin_fl 장애완료
     * @param srcGrp 장비구분
     * @param vo jqGrid 파라미터
     * @return List<TroubleShootVO> 코드목록
     */
    public List<TroubleShootVO> getRdayTroubleShootList(@Param("srcHeadGrp") String srcHeadGrp, @Param("sDate") String sDate,@Param("eDate") String eDate,@Param("fin_fl") String fin_fl, @Param("srcGrp") String srcGrp,@Param("vo") SrcJQGridVO vo);

    /**
     * 장애처리조치사항 목록 가져오기
     * @param cause_seq 장애코드
     * @param vo jqGrid 파라미터
     * @return List<MeaSureVO> 코드목록
     */
    public List<MeaSureVO> getRdayTroubleShootMeasureList(@Param("cause_seq") String cause_seq,@Param("vo") SrcJQGridVO vo);


    /**
     * 장애처리 상세정보 가져오기
     *
     * @param key 장애처리 고유 키
     * @return TroubleShootVO
     */
    public TroubleShootVO getRdayTroubleShootData(@Param("key") String key);

    /**
     * 장애처리 리포트정보 가져오기
     *
     * @param key 장애처리 고유 키
     * @return String
     */
    public String getRdayTroubleShootReportFileNm(@Param("key") String key);
    /**
     * 장애처리 Insert
     *
     * @param vo TroubleShootVO
     * @return 쿼리 결과
     */
    public int setTroubleShootInsert(@Param("vo") TroubleShootVO vo);

    /**
     * 장애처리 Update
     *
     * @param vo TroubleShootVO
     * @return 쿼리 결과
     */
    public int setTroubleShootUpdate(@Param("vo") TroubleShootVO vo);
    /**
     * 장애처리조치사항 Insert
     *
     * @param vo MeaSureVO
     * @return 쿼리 결과
     */
    public int setTroubleShootMeasureInsert(@Param("vo") MeaSureVO vo);

    /**
     * 장애처리조치사항 Update
     *
     * @param vo MeaSureVO
     * @return 쿼리 결과
     */
    public int setTroubleShootMeasureUpdate(@Param("vo") MeaSureVO vo);

    /**
     * 장애처리 Delete
     *
     * @param vo TroubleShootVO
     * @return 쿼리 결과
     */
    public int setTroubleShootDelete(@Param("vo") TroubleShootVO vo);

    /**
     * 장애처리조치사항 Delete
     *
     * @param vo MeaSureVO
     * @return 쿼리 결과
     */
    public int setTroubleShootMeasureDelete(@Param("vo") MeaSureVO vo);

    /**
     * 작업자 정보 목록 가져오기
     *
     * @param vo TroubleShootVO
     * @return 쿼리 결과
     */
    public List<TroubleShootVO> getRdayTroubleShootWorkerList(@Param("dis_seq")String dis_seq, @Param("mem_id")String mem_id ,@Param("vo")SrcJQGridVO vo);


    /**
     * 작업자 정보 삭제하기(Delete)
     *
     * @param vo TroubleShootVO
     * @return 쿼리 결과
     */
    public int setWorkerGridDelete(@Param("vo")TroubleShootVO vo);

    /**
     * 작업자 정보 이름 가져오기(Select2)
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getWorkerNameSelect2(@Param("word")String word);

    /**
     * 작업자 정보 이름 선택 시 세부정보 가져오기
     *
     * @return TroubleShootVO
     */
    public TroubleShootVO getWorkerInfoSetting(@Param("vo")TroubleShootVO vo, @Param("mem_id")String mem_id);


    /**
     * 작업자 정보 저장하기(Update)
     *
     * @return 쿼리 결과
     */
    public int setWorkerGridUpdate(@Param("vo") TroubleShootVO vo);

    /**
     * 작업자 정보 저장하기(Insert)
     *
     * @return 쿼리 결과
     */
    public int setWorkerGridInsert(@Param("vo")TroubleShootVO vo);

    /**
     * 선택한 파일명 가져오기
     *
     * @return 쿼리 결과
     */
    public TroubleShootVO getFileName(@Param("vo") TroubleShootVO vo);

    /**
     * 선택한 파일명 삭제하기(Delete)
     *
     * @return 쿼리 결과
     */
    public int setFileDel(@Param("vo") MeaSureVO vo);

}
