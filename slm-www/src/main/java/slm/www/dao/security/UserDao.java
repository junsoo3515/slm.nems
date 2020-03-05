package slm.www.dao.security;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.menu.MenuAuthVO;
import slm.www.vo.security.UserVO;

import java.util.List;

/**
 * 사용자 계정 관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.15
 * Time: 오후 3:25
 */
public interface UserDao {

    /**
     * 사용자 총 개수 가져오기
     *
     * @param srcAuth 권한
     * @param srcId 아이디
     * @param srcEtc 이름/이메일
     * @param vo jqGrid 파라미터
     * @return 총 개수
     */
    public int getUserListCnt(@Param("srcAuth") String srcAuth, @Param("srcId") String srcId, @Param("srcEtc") String srcEtc, @Param("vo") SrcJQGridVO vo);

    /**
     * 사용자 목록 가져오기
     *
     * @param srcAuth 권한
     * @param srcId 아이디
     * @param srcEtc 이름/이메일
     * @param vo      jqGrid 파라미터
     * @return 사용자 목록
     */
    public List<UserVO> getUserList(@Param("srcAuth") String srcAuth, @Param("srcId") String srcId, @Param("srcEtc") String srcEtc, @Param("vo") SrcJQGridVO vo);

    /**
     * 사용자 상세정보 가져오기
     *
     * @param key 사용자 고유번호
     * @return UserVO
     */
    public UserVO getUserData(@Param("key") String key);

    /**
     * 사용자 상세정보 가져오기
     *
     * @param mem_id 사용자 아이디
     * @return UserVO
     */
    public UserVO getUserDataId(@Param("mem_id") String mem_id);

    /**
     * 아이디 중복확인
     *
     * @param key 아이디
     * @return 아이디 존재 개수
     */
    public int getIDChk(@Param("key") String key);

    /**
     * 로그인 실패 횟수 초기화 Update
     *
     * @param key 아이디
     * @return 쿼리 결과
     */
    public int setUserFailPwdClear(@Param("key") String key);

    /**
     * 사용자정보 Insert
     *
     * @param vo UserVO
     * @return 쿼리 결과
     */
    public int setUserInsert(@Param("vo") UserVO vo);

    /**
     * 사용자정보 Update.
     *
     * @param vo UserVO
     * @return 쿼리 결과
     */
    public int setUserUpdate(@Param("vo") UserVO vo);

    /**
     * 메뉴접근 설정 데이터 존재 유무 판단
     *
     * @param key 사용자 아이디
     * @return COM_MEM_MNU 존재 개수
     */
    public int getUserAuthCnt(@Param("key") String key);

    /**
     * 메뉴접근 설정 Insert
     *
     * @param firstSeq 가장 처음 들어가는 고유 SEQ
     * @param authCd 권한 코드
     * @param memId 사용자 코드
     * @param listData List<MenuAuthVO>
     * @return 쿼리 결과
     */
    public int setUserAuthInsert(@Param("firstSeq") long firstSeq, @Param("authCd") String authCd, @Param("memId") String memId, @Param("listData") List<MenuAuthVO> listData);

    /**
     * 메뉴접근 설정 Update
     *
     * @param authCd 권한 코드
     * @param memId 사용자 코드
     * @param vo MenuAuthVO
     * @return 쿼리 결과
     */
    public int setUserAuthUpdate(@Param("authCd") String authCd, @Param("memId") String memId, @Param("vo") MenuAuthVO vo);
}
