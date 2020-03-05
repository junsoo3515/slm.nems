package slm.www.dao.rday;

import slm.www.vo.rday.ReferenceVO;

import java.util.List;

/**
 * 일일보고서 관리
 * <p/>
 * User: 이종혁
 * Date: 2016.04.29
 * Time: 오전 11:17
 */
public interface ReferenceDao {

    /**
     * 일일보고서 점검 기준 목록 가져오기
     *
     * @return 일일보고서 점검 기준 목록
     */
    public List<ReferenceVO> getRdayReferenceList();


}
