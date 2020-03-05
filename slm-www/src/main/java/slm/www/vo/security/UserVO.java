package slm.www.vo.security;

import module.vo.menu.MenuAuthVO;

import java.util.List;

/**
 * <p/>
 * User: 현재호
 * Date: 2016.04.15
 * Time: 오후 3:26
 */
public class UserVO {
    // 사용자 계정관리
    public int mem_seq;
    public String mem_id;
    public String nm;
    public String pwd;
    public String email;
    public String tel_hp;
    public String tel_office;

    public String auth_nm;
    public String auth_cd;

    public String use_fl;
    public String etc;

    public int fail_pwd_cnt;
    public String reg_mem_id;

    // 권한 관리
    public List<MenuAuthVO> authData;

    // 사용자 사진관련 파일 SEQ
    public long files_seq;
}
