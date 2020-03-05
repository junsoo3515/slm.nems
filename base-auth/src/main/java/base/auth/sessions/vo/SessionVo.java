package base.auth.sessions.vo;

import javax.servlet.http.HttpSession;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

//TODO HttpSession이 Serialize되지 않는 관계로 VO를 생성하여 대체함. 더 나은 방법 모색 필요.

/**
 * 세션 객체 VO
 * <p/>
 * 주의
 * getter함수가 하나도 없으면, org.codehaus.jackson.map.ObjectMapper 에서 에러발생
 * ObjectMapper를 환경설정 파일에서 getter 함수가 하나도 없어도 에러발생을 하지 않도록 할 수 있음.
 * <p/>
 * User: h9911120@gmail.com
 * Date: 16. 4. 14 오후 3:45
 */
public class SessionVo {

    /**
     * 기본 생성자
     * HttpSession 객체를 인자로 받아 변수에 설정
     *
     * @param session HttpSession 객체
     */
    public SessionVo(HttpSession session) {

        if (session == null) {
            setInvalidated(true);
        } else {
            setSessionId(session.getId());
            setInvalidated(false);

            setCreationTime(session.getCreationTime());
            setLastAccessedTime(session.getLastAccessedTime());

            //todo 세션 접근 시각(Time)에 대한 좀더 정확한 방법을 모색해야 함.
            setCurrentAccessedTime(System.currentTimeMillis());

            setMaxInactiveInterval(session.getMaxInactiveInterval());

            Enumeration e = session.getAttributeNames();

            Map<String, Object> attributes = new HashMap<String, Object>();

            while (e.hasMoreElements()) {
                String k = (String) e.nextElement();
                Object v = session.getAttribute(k);

                attributes.put(k, v);
            }

            setAttributes(attributes);
        }
    }

    private String sessionId;
    private boolean invalidated;
    private long creationTime;
    private long lastAccessedTime;
    private long currentAccessedTime;
    private int maxInactiveInterval;
    private Map<String, Object> attributes;

    /**
     * Session ID 반환
     *
     * @return Session ID
     */
    public String getSessionId() {
        return sessionId;
    }

    /**
     * Session ID 등록
     *
     * @param sessionId Session ID
     */
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    /**
     * 세션 소멸 여부 반환
     *
     * @return 세션 소멸 여부
     */
    public boolean isInvalidated() {
        return invalidated;
    }

    /**
     * 세션 소멸 여부 등록
     *
     * @param invalidated 세션 소멸 여부
     */
    public void setInvalidated(boolean invalidated) {
        this.invalidated = invalidated;
    }

    /**
     * 세션 생성시각 반환
     *
     * @return 세션 생성 시각 (long)
     */
    public long getCreationTime() {
        return creationTime;
    }

    /**
     * 세션 생성시각 등록
     *
     * @param creationTime 세션 생성 시각 (long)
     */
    public void setCreationTime(long creationTime) {
        this.creationTime = creationTime;
    }

    /**
     * 세션에 직전에 Access한 시간 반환
     *
     * @return 세션에 직전에 Access한 시간 (long)
     */
    public long getLastAccessedTime() {
        return lastAccessedTime;
    }

    /**
     * 세션에 직전에 Access한 시간 등록
     *
     * @param lastAccessedTime 세션에 직전에 Access한 시간 (long)
     */
    public void setLastAccessedTime(long lastAccessedTime) {
        this.lastAccessedTime = lastAccessedTime;
    }

    /**
     * 세션에 마지막으로 Access한 시간 반환
     *
     * @return 세션에 마지막으로 Access한 시간 (long)
     */
    public long getCurrentAccessedTime() {
        return currentAccessedTime;
    }

    /**
     * 세션에 마지막으로 Access한 시간 등록
     *
     * @param currentAccessedTime 세션에 마지막으로 Access한 시간 (long)
     */
    public void setCurrentAccessedTime(long currentAccessedTime) {
        this.currentAccessedTime = currentAccessedTime;
    }

    /**
     * Session Attribute 객체 반환
     *
     * @return <pre>Map<String, Object> 형 Session Attribute</pre>
     */
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    /**
     * Session Attribute 등록
     *
     * @param attributes <pre>Map<String, Object> 형 Session Attribute</pre>
     */
    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    /**
     * Web 서버에 설정된 세션 유지 시간 (초) 반환
     * @return 세션 유지 시간 (초)
     */
    public int getMaxInactiveInterval() {
        return maxInactiveInterval;
    }

    /**
     * Web 서버에 설정된 세션 유지 시간 (초) 설정
     * @param maxInactiveInterval 세션 유지 시간
     */
    public void setMaxInactiveInterval(int maxInactiveInterval) {
        this.maxInactiveInterval = maxInactiveInterval;
    }
}
