package slm.www.dao.onepage;

import org.apache.ibatis.annotations.Param;
import slm.www.vo.security.UserVO;

/**
 * 개인정보수정 관리
 * <p/>
 * User: 이종혁
 * Date: 2016.06.20
 * Time: 오후 3:25
 */
public interface MyInfoDao {


    /**
     * 개인정보 Update.
     *
     * @param vo UserVO
     * @return 쿼리 결과
     */
    public int setMyInfoUpdate(@Param("vo") UserVO vo);
}
