/**
 * 세션인증 패키지에서 사용하는 정보 보관소
 */
package base.auth.sessions.store;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.Hashtable;
import java.util.Map;

/**
 * 세션 보관 객체 (in-memory)
 * <p/>
 * User: h9911120@gmail.com
 * Date: 16. 4. 14 오후 3:45
 */
public class SessionStore {

    //SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(SessionStore.class);

    //동기화를 위해서 Hashtable 사용
    private static Map<String, HttpSession> map;

    private static HttpContext httpContext;

    //PSFSessionStore 인스턴스 반환 객체 (Place Holder)
    private static final class PSFSessionStoreInstanceHolder {
        static final SessionStore instance = new SessionStore();
    }

    //싱글턴 객체 생성을 위한 private형 생성자
    private SessionStore() {

        map = new Hashtable<String, HttpSession>();
        httpContext = new BasicHttpContext();

        CookieStore cookieStore = new BasicCookieStore();
        httpContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
    }

    /**
     * PSFSessionStore 인스턴스 (싱글턴) 생성
     *
     * @return PSFSessionStore 객체의 인스턴스
     */
    public static SessionStore getInstance() {
        return PSFSessionStoreInstanceHolder.instance;
    }

    /**
     * 세션 등록
     *
     * @param sessionid 세션 ID
     * @param session   세션 객체
     */
    public static void add(String sessionid, HttpSession session) {
        map.put(sessionid, session);
    }

    /**
     * 세션 객체 반환
     *
     * @param sessionid 세션 ID
     * @return HttpSession 객체
     */
    public static HttpSession get(String sessionid) {

        return map.get(sessionid);
    }

    /**
     * 세션 어트리뷰트 값 설정
     *
     * @param sessionid  세션 ID
     * @param attributes 세션의 어트리뷰트 (Map<어트리뷰트명, 값>)
     * @return HttpSession 객체
     */
    public static HttpSession set(String sessionid, Map<String, String> attributes) {

        HttpSession session = map.get(sessionid);

        for (Map.Entry<String, String> entry : attributes.entrySet()) {

            session.setAttribute(entry.getKey(), entry.getValue());
        }

        return session;
    }

    /**
     * 세션 삭제
     *
     * @param sessionid 세션 ID
     */
    public static void remove(String sessionid) {

        HttpSession session = map.get(sessionid);

        if (session != null) {
            map.remove(sessionid);

            session.invalidate();
        }
    }

    /**
     * HttpContext 객체 반환
     *
     * @return HttpContext 객체
     */
    public static HttpContext getHttpContext() {
        return httpContext;
    }
}
