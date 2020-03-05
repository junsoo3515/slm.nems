package slm.www.ctr.cmn.login;

import module.secure.encryption.CmnRsaBiz;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import module.dao.login.AuthLoginDao;
import module.secure.encryption.CmnRsaOaepBiz;
import module.secure.filter.CmnFilterBiz;
import module.session.CmnSessionBiz;
import module.session.CmnSessionDevBiz;
import module.vo.login.AuthLoginVO;
import module.vo.login.UserVO;
import module.vo.session.SessionVo;
import slm.www.dao.security.UserDao;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.security.PrivateKey;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * 사용자 로그인
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 6:38
 */
@Controller
@RequestMapping("/login")
public class AuthLoginCtr {

    private static final Logger log = LoggerFactory.getLogger(AuthLoginCtr.class);

    // autowire된 sqlSession 객체 : servlet-context.xml의 SqlSessionTemplate 참조
    @Autowired
    private SqlSession sqlSession;

    private String wasType = ResourceBundle.getBundle("config").getString("was.id");

    /**
     * 인증 관련 DAO 클래스 선언
     *
     * @return BaseAuthLoginDao
     */
    public AuthLoginDao getDao() {

        return sqlSession.getMapper(AuthLoginDao.class);
    }

    /**
     * 사용자계정관리에서 사용하는 DAO
     *
     * @return UserDao
     */
    public UserDao getUserDao() {
        return sqlSession.getMapper(UserDao.class);
    }

    /**
     * 로그인 화면 호출 Controller
     *
     * @return View 객체
     */
    @RequestMapping(value = "/view", method = RequestMethod.GET)
    public ModelAndView invokeView(HttpServletRequest req) {

        ModelAndView mv = new ModelAndView();

        try {
            // RSA 키 생성
            CmnRsaBiz.initRsa(req);

            mv.addObject("p", req.getContextPath());
            mv.addObject("rsaModulus", req.getAttribute("RSAModulus"));
            mv.addObject("rsaExponent", req.getAttribute("RSAExponent"));

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 로그인 Process 처리
     * <p/>
     * RequestBody는 JSON 수신시 사용
     *
     * @param vo 입력내역이 담긴 JSON형태의 VO
     * @return Map
     * - idState : 0 : 사용중, 1 : 없음, 2 : 탈퇴, 3 : 에러(DB 오류 및 Exception에 걸릴 경우), 4 : 비밀번호 실패 5회 사용자
     * - isSuccess : 정상 로그인 결과(true / false)
     * - returnUrl : 액션 처리 후의 이동 할 페이지 URL(이전 URL)
     */
    @RequestMapping(value = "/act", method = RequestMethod.POST)
    @ResponseBody
    public Map act(@RequestBody AuthLoginVO vo, HttpSession ses) {

        Map<String, Object> map = new HashMap<String, Object>();

        boolean isSuccess = false;
        int idState = 0;
        String retURL = vo.returnUrl;
        int fail_pwd_cnt = 0;

        try {
            // 1. 필터링
            CmnFilterBiz.filterSqlClass(vo);

            // 1-1. RSA 복호화
            PrivateKey privateKey = (PrivateKey) ses.getAttribute("_RSA_WEB_Key_");

            vo.memID = CmnRsaBiz.decryptRsa(privateKey, vo.memID);
            vo.memPWD = CmnRsaBiz.decryptRsa(privateKey, vo.memPWD);

            // 2. 아이디 대문자 변환
            if (StringUtils.isEmpty(vo.memID) == false) {

                vo.memID = vo.memID.toUpperCase();
            }

            // 3. 사용자 정보 가져오기
            UserVO retVO = getDao().getMemberData(vo);

            // 4. 회원 사용 상태 체크
            if (retVO != null) {

                if (StringUtils.equals(retVO.use_fl, "N") == true) {

                    idState = 2;
                }

                if (retVO.fail_pwd_cnt > 4) {

                    idState = 4;
                }

                fail_pwd_cnt = retVO.fail_pwd_cnt;
            } else {

                idState = 1;
            }

            // 5. 사용자 등급 및 정보 Setting
            if (idState == 0) {

                if (StringUtils.equals(retVO.pwd, CmnRsaOaepBiz.encrypt(vo.memPWD)) == true) {

                    if (!StringUtils.isEmpty(vo.returnUrl) == false) {

                        retURL = "/";
                    }

                    try {
                        // 클러스터링 지원을 위해 사용
                        Map<String, String> params = new HashMap<String, String>();

                        switch (wasType) {
                            case "jeus":

                                params.put("id", retVO.mem_id);
                                params.put("authCD", retVO.auth_cd);

                                // 헤더에서 이름/프로필 사진용 SEQ
                                params.put("member_profile_seq", String.valueOf(retVO.files_seq));
                                params.put("member_nm", retVO.nm);

                                CmnSessionBiz.setSessionAttribute(ses, params);

                                params.clear();
                                params.put("token", ses.getId());

                                CmnSessionBiz.setSessionAttribute(ses, params);

                                map.put("token", ses.getId());
                                map.put("expireIn", ses.getMaxInactiveInterval()); // 초
                                break;
                            case "tomcat":

                                CmnSessionDevBiz sessBiz = new CmnSessionDevBiz();

                                String token = sessBiz.createSession(retVO.mem_id); // Session 생성

                                params.put("authCD", retVO.auth_cd);

                                SessionVo sVO = sessBiz.setSessionAttribute(token, params); // Session 객체 추가 후 결과 받아오기

                                // 헤더에서 이름/프로필 사진용 SEQ
                                ses.setAttribute("member_profile_seq", String.valueOf(retVO.files_seq));
                                ses.setAttribute("member_nm", retVO.nm);
                                sessBiz.setLocalSession(sVO, ses); // Local Session 생성

                                map.put("token", sVO.getSessionId());
                                map.put("expireIn", sVO.getMaxInactiveInterval()); // 초
                                break;
                        }

                        ses.removeAttribute("_RSA_WEB_Key_"); // 개인키 삭제

                        getUserDao().setUserFailPwdClear(retVO.mem_id.toUpperCase()); // 로그인 성공 시 비밀번호 틀린 횟수 초기화

                        isSuccess = true;
                    } catch (Exception ex) {

                        idState = 3;
                        log.error(ex.toString(), ex);
                    }
                } else {
                    // 비밀번호 틀릴 경우
                    getDao().setLoginFail(vo.memID);

                    fail_pwd_cnt ++;
                }
            }
        } catch (Exception ex) {

            idState = 3;
            log.error(ex.toString(), ex);
        }

        map.put("isSuccess", isSuccess);
        map.put("idState", idState);
        map.put("fail_cnt", fail_pwd_cnt);
        map.put("returnUrl", retURL);

        return map;
    }

    /**
     * 로그인 상태 체크
     *
     * @param sessionid token 키
     * @return Map
     * - invalidated : OK || DIE(session이 유효한 상태 체크)
     */
    @RequestMapping(value = "/live/{sessionid:.+}", method = RequestMethod.GET)
    @ResponseBody
    public Map get(@PathVariable String sessionid, HttpSession ses) {

        boolean isDie = true;
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            // TODO 클러스터링 지원을 위해 사용
            switch (wasType) {
                case "jeus":

                    if (ses.getAttribute("id") != null) {

                        if (StringUtils.equals(ses.getId(), sessionid)) isDie = false;
                    }
                    break;
                case "tomcat":

                    CmnSessionDevBiz sessBiz = new CmnSessionDevBiz();

                    SessionVo sVO = sessBiz.getSession(sessionid); // Session 객체 받아오기

                    isDie = sVO.isInvalidated();
                    break;
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        } finally {

            map.put("invalidated", isDie);
        }

        return map;
    }

    /**
     * 로그아웃
     *
     * @param req HttpServletRequest
     * @return Map
     * - invalidated : OK || DIE(session이 유효한 상태 체크)
     */
    @RequestMapping(value = "/invalidate", method = RequestMethod.GET)
    @ResponseBody
    public Map invalidate(HttpServletRequest req) {

        boolean isDie = false;
        Map<String, Object> map = new HashMap<String, Object>();

        try {
            // TODO 클러스터링 지원을 위해 사용
            switch (wasType) {
                case "jeus":

                    CmnSessionBiz.invalidateSession(req.getSession()); // Session 소멸

                    isDie = true;
                    break;
                case "tomcat":

                    CmnSessionDevBiz sessBiz = new CmnSessionDevBiz();

                    SessionVo sVO = sessBiz.invalidateSession(req.getSession()); // Session 객체 받아오기

                    isDie = (sVO.getSessionId() != null ? sVO.isInvalidated() : true);
                    break;
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        } finally {

            map.put("invalidated", isDie);
        }

        return map;
    }
}
