package module.vo.menu;

import java.util.Date;

/**
 * 메뉴 VO
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:16
 */
public class MenuVO {

    public String mnu_cd; // 메뉴 코드
    public String mnu_nm1; // 메뉴명1
    public String mnu_nm2; // 메뉴명2
    public String mnu_nm3; // 메뉴명3
    public String url; // URL
    public String etc; // 기타 정보(메뉴 아이콘 CSS 코드)
    public String grp_cd; // 메뉴 그룹 코드
    public Date reg_dts; // 등록일자
}
