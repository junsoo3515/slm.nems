package module.dao.menu;

import org.apache.ibatis.annotations.Param;
import module.vo.menu.MenuAuthVO;
import module.vo.menu.MenuVO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MenuDao {
    /**
     * 전체 메뉴 가져오기
     *
     * @return List<MenuVO>  전체 시스템 메뉴
     */
    public List<MenuVO> getAllMenu();

    /**
     * 사용자 계정별 왼쪽 메뉴 가져오기
     *
     * @param folKey 시스템 코드
     * @param memId  사용자 아이디
     * @return List<MenuVO>  사용자 메뉴
     */
    public List<MenuVO> getLeftMenu(@Param("folKey") String folKey, @Param("memId") String memId);

    /**
     * 사용자 별 + 해당 메뉴 접근 가능 체크
     *
     * @param memID 사용자 아이디
     * @param url   접근 URL
     * @return 체크 여부
     */
    public int getAccessMenu(@Param("memID") String memID, @Param("url") String url);

    /**
     * 사용자 별 + 시스템 처음 접근 메뉴 찾기
     *
     * @param folKey 접근 폴더
     * @param memID  사용자 아이디
     * @return 체크 여부
     */
    public HashMap<String, String> getAccessFirstMenu(@Param("folKey") String folKey, @Param("memID") String memID);

    /**
     * 해당 CRUD 권한 가져오기
     *
     * @param id     사용자 아이디
     * @param mnu_cd 메뉴코드
     * @return HashMap<String, String> 권한
     */
    public Map<String, String> getAuthCrud(@Param("id") String id, @Param("mnu_cd") String mnu_cd);

    /**
     * 권한/사용자별 메뉴 접근 설정 가져오기
     *
     * @param jongCd 종류 코드(A : 권한, B : 사용자)
     * @param val  값
     * @return List<MenuAuthVO>  사용자 메뉴
     */
    public List<MenuAuthVO> getMenuData(@Param("jongCd") String jongCd, @Param("val") String val);

    /**
     * 사용자 접속 Log Insert
     *
     * @param id     사용자 아이디
     * @param mnu_cd 메뉴코드
     * @return 쿼리 결과
     */
    public int setUserLogInsert(@Param("id") String id, @Param("mnu_cd") String mnu_cd);
}
