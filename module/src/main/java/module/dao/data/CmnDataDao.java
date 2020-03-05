package module.dao.data;

import org.apache.ibatis.annotations.Param;

/**
 * 공통으로 사용하는 DAO
 * <p/>
 * User: 현재호
 * Date: 2016.05.11
 * Time: 오전 11:51
 */
public interface CmnDataDao {

    /**
     * 특정 테이블의 최대 고유번호 가져오기
     *
     * @param tb 테이블
     * @param col 고유 칼럼
     * @param addCnt 최대 + a
     * @return 고유번호
     */
    public long getTableMaxSeq(@Param("tb") String tb, @Param("col") String col, @Param("addCnt") Integer addCnt);
}
