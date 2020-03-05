package module.session;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import module.vo.session.SessionVo;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

/**
 * Session Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:06
 */
public class CmnSessionDevBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnSessionBiz.class); // SLF4J Logger
    private String authUrlPrefix = ResourceBundle.getBundle("config").getString("auth.session.url"); // 세션 처리하기 위한 URL 주소

    // 로깅
    private void makeSessionLog(String url, SessionVo vo) {

        //테스트용 로그
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        log.debug("[URL (GET)                   : " + url + "]");
        log.debug("[Session ID                  : " + vo.getSessionId() + "]");
        log.debug("[Session Invalidated         : " + vo.isInvalidated() + "]");
        log.debug("[Session CreationTime        : " + df.format(vo.getCreationTime()) + "]");
        log.debug("[Session LastAccessedTime    : " + df.format(vo.getLastAccessedTime()) + "]");
        log.debug("[Session CurrentAccessedTime : " + df.format(vo.getCurrentAccessedTime()) + "]");
    }

    /**
     * 세션 생성
     *
     * @param userId 사용자 아이디
     * @return the string
     * @throws java.io.IOException the iO exception
     * @throws java.io.IOException the iO exception
     */
    public String createSession(String userId) throws IOException, URISyntaxException {

        String url = String.format("%s/session/add/%s", authUrlPrefix, userId);

//        Map<String, String> params = new HashMap<>();
//        params.put("k1", "v1");
//        params.put("k2", "v2");

        SessionVo vo = getHttpGetResponse(url, null);

        return vo.getSessionId();
    }

    /**
     * 세션 존재유무 검사
     *
     * @param sessionId 사용자 아이디
     * @return the session
     * @throws java.io.IOException the iO exception
     * @throws java.io.IOException the iO exception
     */
    public SessionVo getSession(String sessionId) throws IOException, URISyntaxException {

        String url = String.format("%s/session/get/%s", authUrlPrefix, sessionId);
        return getHttpGetResponse(url, null);
    }

    /**
     * 세션 invalidate 여부 검사
     *
     * @param vo the vo
     * @return the boolean
     */
    public boolean isSessionAlive(SessionVo vo) {

        return !vo.isInvalidated();
    }

    /**
     * 세션값 변경
     *
     * @param sessionId 사용자 아이디
     * @param params    the params
     * @return the session attribute
     * @throws java.io.IOException the iO exception
     */
    public SessionVo setSessionAttribute(String sessionId, Map<String, String> params) throws IOException {

        String url = String.format("%s/session/set/%s", authUrlPrefix, sessionId);
        return getHttpPostResponse(url, params);
    }

    /**
     * 세션 삭제
     *
     * @param ses 세션
     * @return the string
     * @throws java.io.IOException the iO exception
     * @throws java.io.IOException the iO exception
     */
    public SessionVo invalidateSession(HttpSession ses) throws IOException {

        String userID = "";

        if (ses.getAttribute("id") != null) userID = ses.getAttribute("id").toString();

        ses.invalidate();

        String url = String.format("%s/session/invalidate/%s", authUrlPrefix, userID);

        return getHttpGetResponse(url, null);
    }

    /**
     * Context의 Local Session 관리
     *
     * @param vo  the SessionVo
     * @param ses the ses
     */
    public void setLocalSession(SessionVo vo, HttpSession ses) {

        Map<String, Object> attr = vo.getAttributes();

        ses.setAttribute("id", attr.get("id").toString());
        ses.setAttribute("authCD", attr.get("authCD").toString());

        ses.setAttribute("token", vo.getSessionId());
        ses.setAttribute("expireIn", vo.getMaxInactiveInterval());
    }

    /**
     * GET 방식의 Request, Response 받기
     *
     * @param url        the url
     * @param attributes the attributes
     * @return the http get response
     */
    public SessionVo getHttpGetResponse(String url, Map<String, String> attributes) {

        HttpClient httpclient = new DefaultHttpClient();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        InputStream is = null;
        SessionVo vo = new SessionVo();

        try {
            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }
            url = String.format("%s?%s", url, URLEncodedUtils.format(params, "utf-8"));
            HttpGet httpGet = new HttpGet(url);
            HttpResponse res = httpclient.execute(httpGet);
            HttpEntity entity = res.getEntity();

            if (entity != null) {
                is = entity.getContent();
                vo = new ObjectMapper().readValue(is, SessionVo.class);
            }

            if (is != null) is.close();

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            if (httpclient.getConnectionManager() != null)
                httpclient.getConnectionManager().shutdown();
        }

        //반환값 로깅
        makeSessionLog(url, vo);

        return vo;
    }

    /**
     * POST 방식의 Request, Response 받기
     *
     * @param url        the url
     * @param attributes the attributes
     * @return the http post response
     * @throws java.io.IOException the iO exception
     */
    public SessionVo getHttpPostResponse(String url, Map<String, String> attributes) throws IOException {

        HttpClient httpclient = new DefaultHttpClient();
        InputStream is = null;
        SessionVo vo = new SessionVo();

        try {
            HttpPost httpPost = new HttpPost(url);

            List<NameValuePair> params = new ArrayList<NameValuePair>();

            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {

                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }

                //URL Encoding 적용
                httpPost.setEntity(new UrlEncodedFormEntity(params));
            }

            HttpResponse res = httpclient.execute(httpPost);
            HttpEntity entity = res.getEntity();

            if (entity != null) {
                is = entity.getContent();
                vo = new ObjectMapper().readValue(is, SessionVo.class);
            }

            if (is != null) is.close();

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            if (httpclient.getConnectionManager() != null)
                httpclient.getConnectionManager().shutdown();
        }

        //반환값 로깅
        makeSessionLog(url, vo);

        return vo;
    }
}
