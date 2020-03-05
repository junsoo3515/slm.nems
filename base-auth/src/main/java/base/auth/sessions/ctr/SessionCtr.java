/**
 * 세션인증 컨트롤러 패키지
 */
package base.auth.sessions.ctr;

import base.auth.sessions.store.SessionStore;
import base.auth.sessions.vo.SessionVo;
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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * HTTP 세션 관리 Controller
 * 세션의 생성, 수정, 삭제를 관리.
 * <p/>
 * User: h9911120@gmail.com
 * Date: 16. 4. 14 오후 3:45
 */
@Controller
@RequestMapping("/session")
public class SessionCtr {

    /*
    todo request mapping 외부 접근제어
     */
    //SLF4J Logger
    private static final Logger log = LoggerFactory.getLogger(SessionCtr.class);

    /**
     * 세션 등록
     * <p/>
     *
     * @param userid  사용자 ID
     * @param session HttpSession 객체
     * @param req     Get 방식의 Request 객체
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/internal/add/{userid:.+}", method = RequestMethod.GET)
    @ResponseBody
    private String addSession(@PathVariable String userid, HttpSession session, HttpServletRequest req) throws Exception {

        if (session != null) {

            Enumeration enu = req.getParameterNames();
            while (enu.hasMoreElements()) {
                String k = (String) enu.nextElement();
                String v = req.getParameter(k);

                if (k != null && v != null) {
                    session.setAttribute(k, v);
                }
            }

            session.setAttribute("id", userid);
            SessionStore.add(session.getId(), session);
        }

        return getJsonString(new SessionVo(session));
    }

    /**
     * 세션 등록
     * <p/>
     *
     * @param userid 사용자 ID
     * @param req    Get 방식의 Request 객체
     * @return 세션정보 (JSON)
     */
    @RequestMapping(value = "/add/{userid:.+}", method = RequestMethod.GET)
    @ResponseBody
    public String add(@PathVariable String userid, HttpServletRequest req) {

        String url = String.format("%s/session/internal/add/%s", getUrlPrefix(req), userid);
        return getHttpGetResponse(url, null);
    }

    /**
     * sessionid별 session 정보조회
     *
     * @param sessionid HttpSession의 ID
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/internal/get/{sessionid:.+}", method = RequestMethod.GET)
    @ResponseBody
    private String getSession(@PathVariable String sessionid) throws Exception {

        HttpSession session = SessionStore.get(sessionid);
        return getJsonString(new SessionVo(session));
    }

    /**
     * sessionid별 session 정보조회
     *
     * @param sessionid HttpSession의 ID
     * @param req       Get 방식의 Request 객체
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/get/{sessionid:.+}", method = RequestMethod.GET)
    @ResponseBody
    public String get(@PathVariable String sessionid, HttpServletRequest req) throws Exception {

        String url = String.format("%s/session/internal/get/%s", getUrlPrefix(req), sessionid);
        return getHttpGetResponse(url, null);
    }

    /**
     * 세션 ID 별 Attribute 설정
     *
     * @param sessionid HttpSession의 ID
     * @param req       request 객체
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/internal/set/{sessionid:.+}", method = RequestMethod.POST)
    @ResponseBody
    private String setSession(@PathVariable String sessionid, HttpServletRequest req) throws Exception {

        HttpSession session = SessionStore.get(sessionid);

        if (session != null) {
            Map<String, String> attributes = new HashMap<String, String>();
            Enumeration enu = req.getParameterNames();
            while (enu.hasMoreElements()) {
                String k = (String) enu.nextElement();
                String v = req.getParameter(k);
                attributes.put(k, v);
            }
            SessionStore.set(sessionid, attributes);
        }

        return getJsonString(new SessionVo(session));
    }

    /**
     * 세션 ID 별 Attribute 설정
     * <p/>
     * POST 방식으로 Session Attribute에 설정될 파라미터 설정
     * <p/>
     * <pre>
     *     //HttpClient 사용법 (예제)
     *
     *     HttpClient client = new DefaultHttpClient();
     *     HttpPost post = new HttpPost("http://<ip>/auth/set/<sessionid>");
     *     List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
     *     nameValuePairs.add(new BasicNameValuePair("auth", "A01"));
     *     nameValuePairs.add(new BasicNameValuePair("email", "john@mail.com"));
     *     HttpResponse response = client.execute(post);
     * </pre>
     *
     * @param sessionid HttpSession의 ID
     * @param req       request 객체
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/set/{sessionid:.+}", method = RequestMethod.POST)
    @ResponseBody
    public String set(@PathVariable String sessionid, HttpServletRequest req) throws Exception {

        HttpSession session = SessionStore.get(sessionid);
        Map<String, String> attributes = new HashMap<String, String>();

        if (session != null) {
            Enumeration enu = req.getParameterNames();
            while (enu.hasMoreElements()) {
                String k = (String) enu.nextElement();
                String v = req.getParameter(k);
                attributes.put(k, v);
            }
        }

        String url = String.format("%s/session/internal/set/%s", getUrlPrefix(req), sessionid);
        return getHttpPostResponse(url, attributes);
    }

    /**
     * 세션 삭제(invalidate)
     *
     * @param sessionid HttpSession의 ID
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/internal/invalidate/{sessionid:.+}", method = RequestMethod.GET)
    @ResponseBody
    private String invalidateSession(@PathVariable String sessionid) throws Exception {
        SessionStore.remove(sessionid);
        return getJsonString(new SessionVo(null));
    }

    /**
     * 세션 삭제(invalidate)
     *
     * @param sessionid HttpSession의 ID
     * @param req       request 객체
     * @return 세션정보 (JSON)
     * @throws Exception JSON 변경시 발생하는 Exception객체(외)
     */
    @RequestMapping(value = "/invalidate/{sessionid:.+}", method = RequestMethod.GET)
    @ResponseBody
    public String invalidate(@PathVariable String sessionid, HttpServletRequest req) throws Exception {

        String url = String.format("%s/session/internal/invalidate/%s", getUrlPrefix(req), sessionid);
        return getHttpGetResponse(url, null);
    }

    /**
     * Session객체를 JSON 문자열형태로 반환
     *
     * @param vo SessionVo 객체
     * @return JSON 문자열
     * @throws Exception 객체에서 JSON 문자열 변환시 발생하는 에러
     */
    private String getJsonString(SessionVo vo) throws Exception {

        return new ObjectMapper().writeValueAsString(vo);
    }

    /**
     * URL String의 앞부분 생성
     *
     * @param req HttpServletRequest 객체
     * @return url prefix (예:http://localhost:8080/auth)
     */
    private String getUrlPrefix(HttpServletRequest req) {

        String protocol = req.getScheme();
        // TODO 왜 Remote로 하면 안되지....
//        String address = req.getRemoteAddr();
//        int port = req.getRemotePort();
        String address = req.getLocalAddr();
        int port = req.getLocalPort();
        String context = req.getContextPath();
        return String.format("%s://%s:%s%s", protocol, address, port, context);
    }

    /**
     * Get 방식의 Request 수행
     *
     * @param url        접근경로
     * @param attributes Map(K, V) 형태의 Attribute
     * @return json 형태의 응답 객체
     */
    private String getHttpGetResponse(String url, Map<String, String> attributes) {

        HttpClient httpclient = new DefaultHttpClient();
        List<NameValuePair> params = new ArrayList<NameValuePair>();
        InputStream is = null;
        StringBuilder sb = new StringBuilder();

        try {
            if (attributes != null) {
                for (Map.Entry<String, String> entry : attributes.entrySet()) {
                    params.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
                }
            }

            HttpGet httpGet = new HttpGet(String.format("%s?%s", url, URLEncodedUtils.format(params, "utf-8")));
            HttpResponse res = httpclient.execute(httpGet, SessionStore.getHttpContext());
            HttpEntity entity = res.getEntity();

            if (entity != null) {
                is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (is != null) is.close();

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            if (httpclient.getConnectionManager() != null)
                httpclient.getConnectionManager().shutdown();
        }

        return sb.toString();
    }

    /**
     * Post 방식의 Request 수행
     *
     * @param url        접근경로
     * @param attributes Map(K, V) 형태의 Attribute
     * @return json 형태의 응답 객체
     */
    private String getHttpPostResponse(String url, Map<String, String> attributes) {

        HttpClient httpclient = new DefaultHttpClient();
        InputStream is = null;
        StringBuilder sb = new StringBuilder();

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

            HttpResponse res = httpclient.execute(httpPost, SessionStore.getHttpContext());
            HttpEntity entity = res.getEntity();

            if (entity != null) {
                is = entity.getContent();
                BufferedReader br = new BufferedReader(new InputStreamReader(is));
                String line;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
            }

            if (is != null) is.close();

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        } finally {
            if (httpclient.getConnectionManager() != null)
                httpclient.getConnectionManager().shutdown();
        }

        return sb.toString();
    }
}
