package slm.www.dao.rday;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import slm.www.vo.rday.WorkVO;

import java.util.List;

/**
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
public interface WorkDao {


    /**
     * 일일작업내역 총 개수 가져오기
     *
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param vo jqGrid 파라미터
     * @return int 총 개수
     */
    public int getRdayWorkListCnt(@Param("sDate") String sDate,@Param("eDate") String eDate,@Param("vo") SrcJQGridVO vo);

    /**
     * 일일작업내역 목록 가져오기
     *
     * @param sDate 검색시작날짜
     * @param eDate 검색끝날짜
     * @param vo jqGrid 파라미터
     * @return List<WorkVO> 일일작업내역목록
     */
    public List<WorkVO> getRdayWorkList(@Param("sDate") String sDate,@Param("eDate") String eDate,@Param("vo") SrcJQGridVO vo);
    /**
     * 일일작업내역 상세정보 가져오기
     *
     * @param key 일일작업내역 고유 키
     * @return WorkVO
     */
    public WorkVO getRdayWorkData(@Param("key") String key);
    /**
     * 일일작업내역 Insert
     *
     * @param vo WorkVO
     * @return 쿼리 결과
     */
    public int setWorkInsert(@Param("vo") WorkVO vo);

    /**
     * 일일작업내역 Update
     *
     * @param vo WorkVO
     * @return 쿼리 결과
     */
    public int setWorkUpdate(@Param("vo") WorkVO vo);

    /**
     * 일일작업내역 Delete
     *
     * @param vo WorkVO
     * @return 쿼리 결과
     */
    public int setWorkDelete(@Param("vo") WorkVO vo);
}
