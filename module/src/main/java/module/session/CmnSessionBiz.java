package module.session;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * Session Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:06
 */
public class CmnSessionBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnSessionBiz.class); // SLF4J Logger

    /**
     * 세션값 변경
     *
     * @param ses    HttpSession
     * @param params the params
     */
    public static void setSessionAttribute(HttpSession ses, Map<String, String> params) {

        for (Map.Entry<String, String> obj : params.entrySet()) {

            ses.setAttribute(obj.getKey(), obj.getValue());
        }
    }

    /**
     * 세션 삭제
     *
     * @param ses HttpSession
     */
    public static void invalidateSession(HttpSession ses) {

        ses.invalidate();
    }
}
