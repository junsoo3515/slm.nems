package slm.www.interceptors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

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
 * Date: 16. 4. 12 오후 3:50
 */
@Service
public class LoginInterceptor extends HandlerInterceptorAdapter {

    //SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(LoginInterceptor.class);

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

            if (session.getAttribute("id") != null) {

                if (request.getHeader("X-Requested-With") == null) {
                    response.sendRedirect(response.encodeRedirectURL(request.getContextPath()));
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
