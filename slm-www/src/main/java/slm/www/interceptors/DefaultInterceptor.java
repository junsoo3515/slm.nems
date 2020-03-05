package slm.www.interceptors;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import module.etc.CmnEtcBiz;
import module.session.CmnSessionDevBiz;
import module.vo.session.SessionVo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;

/**
 * request 전처리
 * <p/>
 * <p/>
 * 이하 override 가능한 함수 목록
 * preHandle - Controller 실행 요청전 호출
 * postHandle - view 로 forward 되기 전 호출
 * afterCompletion - 처리가 끝난뒤 호출
 * <p/>
 * User: h9911120@gmail.com, 현재호
 * Date: 16. 4. 12 오후 3:51
 */
@Service
public class DefaultInterceptor extends HandlerInterceptorAdapter {

    //SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(DefaultInterceptor.class);

    @Autowired
    private SqlSession sqlSession;

    // Session 환경설정 값
    @Value("#{cfg['login.url']}")
    private String loginUrlPath;

    // WAS 설정 값
    @Value("#{cfg['was.id']}")
    private String wasType;

    /**
     * request 전처리
     *
     * @param request  Http Request 객체
     * @param response Http Response 객체
     * @param handler  이벤트 핸들러
     * @return 성공/실패 여부
     * @throws java.io.IOException Redirection 경로를 찾지 못하는 경우
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {

        /*
         * 로직 구현시 이슈사항
         *
         * 1 Redirection 로직 구현
         * 1-1 response.encodeRedirectURL(loginUrlPath);
         *          기본적인 Redirection 로직
         *          하지만 해당 encodeRedirectURL 이후에 브라우저에서 세션, 쿠키, http 헤더 조작 로직이 있다면
         *          'Cannot create a session after the response has been committed' 에러가 발생함.
         * 1-2 request.getRequestDispatcher(loginUrlPath).forward(request, response) URL 주소가 변경이 안되기 때문에 여기에서는 사용 안함(URL 체크 들어감)
         *          강제 Redirection 로직, 브라우저에게 권한 없음.
         */

        try {

            HttpSession session = request.getSession();

            String urlPath = request.getRequestURI().substring(request.getContextPath().length()); // 2. 접근 URL의 Path 경로

//            log.debug(session.getAttribute("id") + ", " + session.getMaxInactiveInterval() + ", " + urlPath + " : " + request.getContextPath() + " : " + request.getHeader("X-Requested-With"));

            CmnSessionDevBiz sessBiz = new CmnSessionDevBiz();

            if (session.getAttribute("id") != null) {

                // 3. 세션 검사 (로그인 유무검사)
                switch (wasType) {

                    case "jeus":

                        if (StringUtils.isEmpty(session.getAttribute("token").toString()) == true) {

                            session.invalidate();
                            response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                            return false;
                        }
                        break;
                    case "tomcat":

                        if (StringUtils.isEmpty(session.getAttribute("token").toString()) == true || sessBiz.getSession(session.getAttribute("token").toString()).isInvalidated() == true) {

                            session.invalidate();
                            response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                            return false;
                        }
                        break;
                }
            } else {

                boolean isChk = true;

                // 클러스터링 지원을 위해 사용
                switch (wasType) {

                    case "jeus":

                        if (session.getAttribute("token") == null) {

                            session.invalidate();
                            response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                            isChk = false;
                        } else {

                            if (StringUtils.isEmpty(session.getAttribute("token").toString()) == true) {

                                session.invalidate();
                                response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                                isChk = false;
                            }
                        }
                        break;
                    case "tomcat":

                        String tok = request.getParameter("token");

                        if (StringUtils.isEmpty(tok) == false) {

                            SessionVo sVO = sessBiz.getSession(tok);

                            if (sVO.isInvalidated() == true) {

                                response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                                isChk = false;
                            } else {

                                sessBiz.setLocalSession(sVO, session); // Local Session 생성

                                log.debug("expireIn", session.getMaxInactiveInterval());
                            }

                        } else {

                            session.invalidate();
                            response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                            isChk = false;
                        }
                        break;
                }

                if (!isChk) {

                    return isChk;
                }
            }

            // 비정상적인 사용자 접근 체크 후 session 소멸 및 로그인 페이지 강제 이동(만약 이전 페이지가 존재하면 이전 페이지로 이동)
            if (request.getHeader("X-Requested-With") == null && (!StringUtils.startsWith(urlPath, "/upload") && !StringUtils.startsWith(urlPath, "/report") && !StringUtils.startsWith(urlPath, "/files") && !StringUtils.contains(urlPath, "Excel"))) {
                // ajax로 보내지 않은 패턴 검색(접근 URL)
                try {

                    String redirectUrl = loginUrlPath;

                    CmnEtcBiz etcBiz = new CmnEtcBiz(sqlSession);

                    switch (urlPath) {

                        case "/":
                        case "/slm/":

                            HashMap<String, String> firstMenuData = etcBiz.getMenuDao().getAccessFirstMenu("S", session.getAttribute("id").toString());

                            if (firstMenuData == null) {

                                firstMenuData = etcBiz.getMenuDao().getAccessFirstMenu(null, session.getAttribute("id").toString());
                            }

                            String gKey = firstMenuData.get("G_KEY").toLowerCase();

                            switch (gKey) {
                                case "s":

                                    redirectUrl = String.format("%s%s", request.getContextPath(), firstMenuData.get("URL"));
                                    break;
                                default:

                                    redirectUrl = String.format("/%s%s/?q=%s", gKey, "ms", session.getAttribute("token").toString());
                                    break;
                            }

                            if (StringUtils.equals(redirectUrl, loginUrlPath) == true) session.invalidate();

                            if (StringUtils.isEmpty(redirectUrl) == false) {

                                response.sendRedirect(response.encodeRedirectURL(redirectUrl));
                                return false;
                            }
                            break;
                        default:

                            switch (urlPath) {

                                case "/onepage/myinfo":
                                    // 우측상단 내 정보 메뉴(DB에 접근권한 체크하는게 없어서..)
                                    break;

                                case "/errors":
                                    // 서버 에러 발생 시
                                    break;

                                default:
                                    // TOMCAT에서 session 사용 시 처음 sessionid를 강제로 선언해서 사용하는 것 때문에 URL 찾아가는 버그 발생되어 강제 수정 함
                                    if (urlPath.contains("jsessionid")) {

                                        urlPath = urlPath.replace(";jsessionid=" + request.getRequestedSessionId(), "");

                                        response.sendRedirect(response.encodeRedirectURL(String.format("%s%s", request.getContextPath(), urlPath)));
                                    }

                                    if (etcBiz.getMenuDao().getAccessMenu(session.getAttribute("id").toString(), urlPath) == 0) {

                                        if (request.getHeader("referer") != null) redirectUrl = request.getHeader("referer").toString();

                                        if (StringUtils.equals(redirectUrl, loginUrlPath) == true) session.invalidate();

                                        response.sendRedirect(response.encodeRedirectURL(redirectUrl));
                                        return false;
                                    }
                                    break;
                            }

                            break;
                    }
                } catch (Exception ex) {

                    log.error(ex.toString(), ex);

                    session.invalidate();
                    response.sendRedirect(response.encodeRedirectURL(loginUrlPath));

                    return false;
                }
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            return false;
        }

        // prehandle 함수내 로직의 성공/실패 유무 반환
        return true;
    }
}
