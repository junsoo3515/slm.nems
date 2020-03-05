package slm.www.vo.security;

import module.vo.menu.MenuAuthVO;

import java.util.Date;
import java.util.List;

/**
 * 권한관리에서 사용되는 VO
 * <p/>
 * User: 현재호
 * Date: 2016.04.21
 * Time: 오후 3:14
 */
public class AuthorityVO {
    // 권한관리
    public String auth_cd;
    public String nm;
    public String etc;
    public Date reg_dts;
    public long reg_dts_ux;

    // 권한 메뉴접근 관리
    public List<MenuAuthVO> authData;
}
