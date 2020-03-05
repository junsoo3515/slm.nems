package module.vo.login;

/**
 * 사용자 VO
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:21
 */
public class UserVO {
    // 사용자 관련 정보(로그인에서 사용)
    public String mem_id;
    public String nm;
    public String pwd;
    public String auth_cd;
    public int fail_pwd_cnt;
    public String use_fl;

    // 사용자 사진관련 파일 SEQ
    public long files_seq;
}
