package base.auth.listeners;

import base.auth.sessions.store.SessionStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

/**
 * 세션 상태 리스너
 * 세션의 생성과 소멸에 대한 이벤트를 감지
 * <p/>
 * 본 리스너가 동작하기 위해서는 web.xml에 다음의 사항이 기술되어야 함.
 * <pre>
 * <listener>
 * &nbsp;&nbsp;&nbsp;&nbsp;<listener-class>base.auth.listeners.SessionListener</listener-class>
 * </listener>
 * </pre>
 * <p/>
 * User: 현재호
 * Date: 2016.04.14
 * Time: 오후 3:50
 */
public class SessionListener implements HttpSessionListener {

    //SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(SessionListener.class);

    /**
     * 기본 생성자
     * PSFSessionStore의 인스턴스를 생성
     */
    public SessionListener() {

        SessionStore.getInstance();
    }

    /**
     * 세션 생성 이벤트 처리
     *
     * @param httpSessionEvent HTTP 세션 이벤트
     */
    @Override
    public void sessionCreated(HttpSessionEvent httpSessionEvent) {

        log.debug(String.format("[세션생성:%s]", httpSessionEvent.getSession().getId()));
    }

    /**
     * 세션 소멸 이벤트 처리
     *
     * @param httpSessionEvent HTTP 세션 이벤트
     */
    @Override
    public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {

        HttpSession session = httpSessionEvent.getSession();
        SessionStore.remove(session.getId());
        log.debug(String.format("[세션소멸:%s]", session.getId()));
    }
}
