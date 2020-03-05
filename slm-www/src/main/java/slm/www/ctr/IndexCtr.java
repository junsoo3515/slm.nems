package slm.www.ctr;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import module.dao.data.DropDownDataDao;
import module.etc.CmnEtcBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.list.ListObjVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 처음 Index 페이지 설정
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 6:36
 * To change this template use File | Settings | File Templates.
 */

@Controller
@RequestMapping("/")
public class IndexCtr {

    private static final Logger log = LoggerFactory.getLogger(IndexCtr.class);

    // autowire된 sqlSession 객체 : servlet-context.xml의 SqlSessionTemplate 참조
    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;

    /**
     * 공통 코드 DAO 모듈
     *
     * @return DropDownDataDao
     */
    public DropDownDataDao getDropDownDataDao() {

        return sqlSession.getMapper(DropDownDataDao.class);
    }

    /**
     * 인덱스 화면 호출 Controller
     *
     * @return View 객체
     */
    @RequestMapping(value = "/", method = RequestMethod.GET)
    public ModelAndView invokeView() {
        // 리턴될 view 객체
        // Exception이 발생해도 View는 표출될 수 있도록
        // try-catch 문 밖에서 선언과 초기화.
        ModelAndView mv = new ModelAndView("index");

        // Exception 처리는 컨트롤러 함수 내에서 (권장)
        try {

        } catch (Exception ex) {
            // error를 로깅할 시에는, 반드시 throwable이 가능하도록 인자 2개짜리 error 함수를 사용.
            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 에러 페이지 나올 경우 #1 - colorAdmin Style
     *
     * @param httpRequest the http request
     * @return the model and view
     */
    @RequestMapping(value = "errors", method = RequestMethod.GET)
    public ModelAndView renderErrorPage(HttpServletRequest httpRequest) {

        ModelAndView errorPage = new ModelAndView("errors");

        String errorMsg = "";
        int httpErrorCode = (Integer)httpRequest.getAttribute("javax.servlet.error.status_code");

        switch (httpErrorCode) {
            case 400:
                errorMsg = "Http Error Code: 400. Bad Request";
                break;
            case 401:
                errorMsg = "Http Error Code: 401. Unauthorized";
                break;
            case 404:
                errorMsg = "Http Error Code: 404. Resource not found";
                break;
            case 500:
                errorMsg = "Http Error Code: 500. Internal Server Error";
                break;
        }

        errorPage.addObject("p", httpRequest.getContextPath()); // PATH 가져오기
        errorPage.addObject("errorCode", httpErrorCode);
        errorPage.addObject("errorMsg", errorMsg);

        return errorPage;
    }

    /**
     * 장비유형에 해당되는 항목 가져오기
     * - SLM_EQUIPGRP_TOPIC 코드명/코드값
     *
     * @return View 객체
     */
    @RequestMapping(value = "/getGrpTopic", method = RequestMethod.POST)
    @ResponseBody
    public List<ListObjVO> getGrpTopic(@RequestBody Map<String, Object> reqData, HttpSession ses) {

        try {

            return getDropDownDataDao().getEquipGrpTopicList(reqData.get("grpCD").toString());

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return new ArrayList<ListObjVO>();
        }
    }

    /**
     * 장비 코드 리스트 가져오기
     *
     * SLM_EQUIPMENT 코드명/코드값 List 가져오기(DropDown에서 주로 사용)
     *
     * @return List
     */
    @RequestMapping(value = "/getEquipList", method = RequestMethod.POST)
    @ResponseBody
    public String getEquipList(@RequestBody Map obj, HttpSession ses) {

        String grpCD ="";

        if (obj.containsKey("eqp_grp_cd")) {

            if (!StringUtils.isEmpty(obj.get("eqp_grp_cd").toString())) {

                grpCD = CmnFilterBiz.filterSqlString(obj.get("eqp_grp_cd").toString());
            }
        }

        String arrays ="";
        try {
            if(!grpCD.equals("")){
                arrays = new ObjectMapper().writeValueAsString(getDropDownDataDao().getEquipList(grpCD));
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return arrays;
    }
}
