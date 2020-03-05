package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.etc.CmnEtcBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
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
import slm.www.dao.security.RptItemDao;
import slm.www.vo.security.RptItemVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 보고서 기본항목 관리
 * <p/>
 * User: 이준수
 * Date: 2017.06.01
 * Time: 오전 9:27
 */
@Controller
@RequestMapping("/security")
public class RptItemCtr {

    private static final Logger log = LoggerFactory.getLogger(RptItemCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S006";

    public RptItemDao getDao() {
        return sqlSession.getMapper(RptItemDao.class);
    }

    /**
     * 보고서 기본항목관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/rptitem", method = RequestMethod.GET)
    public ModelAndView invokeView(HttpServletRequest req) {

        if (etcBiz == null) {

            etcBiz = new CmnEtcBiz(sqlSession);
        }

        ModelAndView mv = new ModelAndView();

        try {
            String path = req.getContextPath();
            String id = req.getSession().getAttribute("id").toString();

            // 사용자 접속 로그 Insert
            etcBiz.getMenuDao().setUserLogInsert(id, mnu_cd);

            mv.addObject("p", path); // PATH 가져오기
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); //사용자별 Crud 권한 가져오기
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기
            mv.addObject("rptTopicNmList", new ObjectMapper().writeValueAsString(getDao().getRptTopicNmList())); //보고서 항목 가져오기
            mv.addObject("linkTopicList", new ObjectMapper().writeValueAsString(getDao().getLinkTopicList())); //외부연계 항목 가져오기
            mv.addObject("topicList", new ObjectMapper().writeValueAsString(getDao().getTopicList())); //항목 가져오기
            mv.addObject("rptTypeList", new ObjectMapper().writeValueAsString(getDao().getRptTypeList())); //보고서 유형 가져오기

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return mv;
    }

    /**
     * 기본항목관리 목록 jqGrid 호출 Ctr
     *
     * @param sidx         소팅 헤더 아이디
     * @param sord         소팅 (asc / desc)
     * @param rows         표출 Row 수
     * @param _search      검색 여부
     * @param searchField  검색어 필드 아이디
     * @param searchString 검색어 값
     * @param searchOper   검색어 조건
     * @param filters      필터(Model로 컨버팅 하기 위한 기타 조건들..)
     * @param page         현재 페이지 No
     * @param ses          HttpSession
     * @return ResultJQGridVO
     */
    @RequestMapping(value = "/getReportTopicList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getReportTopicList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptItemVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcWord = null; // 검색어
            String srcRptNm = null; // 보고서항목
            String srcType = null; // 항목 유형

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcWord = CmnFilterBiz.filterSqlString(jsonFilter.get("srcWord"));
                srcRptNm = CmnFilterBiz.filterSqlString(jsonFilter.get("srcRptNm"));
                srcType = jsonFilter.get("srcType");
            }

            int dataCnt = getDao().getReportTopicListCnt(srcWord, srcRptNm, srcType, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getReportTopicList(srcWord, srcRptNm, srcType, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptItemVO>() {
            });
        }
    }

    /**
     * 기본항목 상세정보 가져오기
     *
     * @param vo  {topic_cd:항목코드}
     * @param ses HttpSession
     * @return RptItemVO
     */
    @RequestMapping(value = "/getReportTopicInfo", method = RequestMethod.POST)
    @ResponseBody
    public RptItemVO getReportTopicInfo(@RequestBody RptItemVO vo, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            return getDao().getReportTopicInfo(vo);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 기본항목 저장(Insert/Update)
     *
     * @param vo  RptItemVO
     * @param ses HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setReportTopicAct", method = RequestMethod.POST)
    @ResponseBody
    public int setReportTopicAct(@RequestBody RptItemVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return 0;
        }


        try {
            if (vo.crud.equals("C")) getDao().setReportTopicAct(vo);
            else getDao().setReportTopicUpdate(vo);


        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            return 0;
        }

        return 1;
    }

    /**
     * S/W 점검항목 기초데이터 관리 목록 jqGrid 호출 Ctr
     *
     * @param sidx         소팅 헤더 아이디
     * @param sord         소팅 (asc / desc)
     * @param rows         표출 Row 수
     * @param _search      검색 여부
     * @param searchField  검색어 필드 아이디
     * @param searchString 검색어 값
     * @param searchOper   검색어 조건
     * @param filters      필터(Model로 컨버팅 하기 위한 기타 조건들..)
     * @param page         현재 페이지 No
     * @param ses          HttpSession
     * @return ResultJQGridVO
     */
    @RequestMapping(value = "/getSwTopicList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getSwTopicList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptItemVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);
            String rpt_cd = null; // 보고서 코드

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                rpt_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("rpt_cd"));
            }

            int dataCnt = getDao().getSwTopicListCnt(rpt_cd); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getSwTopicList(rpt_cd, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptItemVO>() {
            });
        }
    }


    /**
     * S/W 점검항목 기초데이터 저장(Insert/Update)
     *
     * @param vo   RptItemVO
     * @param oper String
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setSwTopicAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setSwTopicAct(RptItemVO vo, String oper, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        boolean tmpOk = false;

        try {

            if (oper.equals("add")) getDao().setSwTopicAct(vo);
            else getDao().setSwTopicUpdate(vo);
            tmpOk = true;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            tmpOk = false;
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOk);

        return map;
    }

    /**
     * 장비그룹 연계 목록 jqGrid 호출 Ctr
     *
     * @param sidx         소팅 헤더 아이디
     * @param sord         소팅 (asc / desc)
     * @param rows         표출 Row 수
     * @param _search      검색 여부
     * @param searchField  검색어 필드 아이디
     * @param searchString 검색어 값
     * @param searchOper   검색어 조건
     * @param filters      필터(Model로 컨버팅 하기 위한 기타 조건들..)
     * @param page         현재 페이지 No
     * @param ses          HttpSession
     * @return ResultJQGridVO
     */
    @RequestMapping(value = "/getEquipTopicList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getEquipTopicList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptItemVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String rpt_topic_cd = null; // 항목 코드

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                rpt_topic_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("rpt_topic_cd"));
            }

            int dataCnt = getDao().getEquipTopicListCnt(rpt_topic_cd); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getEquipTopicList(rpt_topic_cd, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptItemVO>() {
            });
        }
    }

    /**
     * 장비그룹 연계 저장(Insert/Update)
     *
     * @param vo   RptItemVO
     * @param oper String
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setEquipTopicAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setEquipTopicAct(RptItemVO vo, String oper, HttpSession ses) {
        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        boolean tmpOk = false;


        try {

            if (oper.equals("add")) getDao().setEquipTopicAct(vo);
            else getDao().setEquipTopicUpdate(vo);
            tmpOk = true;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            tmpOk = false;
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOk);

        return map;
    }

    /**
     * 장비그룹연계 삭제하기(Delete)
     *
     * @param vo  RptItemVO
     * @param ses HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setEquipTopicDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setEquipTopicDel(@RequestBody RptItemVO vo, HttpSession ses) {

        String[] arr = {"D"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        boolean tmpOK = false;


        try {
            getDao().setEquipTopicDel(vo);
            tmpOK = true;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            tmpOK = false;
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 기본항목 입력 보고서 항목 가져오기(Select2)
     *
     * @param req HttpServletRequest
     * @return List<ListObjVO>
     */
    @RequestMapping(value = "/getRptTopicNmSelect2", method = RequestMethod.GET)
    @ResponseBody
    public List<ListObjVO> getRptTopicNmSelect2(HttpServletRequest req) throws Exception {

        String word = req.getParameter("q");

        if (word != null) word = new String(word.getBytes("8859_1"), "UTF-8");
        return getDao().getRptTopicNmSelect2(word);
    }

    /**
     * S/W점검항목 기초데이터 관리 장비 가져오기(Select2)
     *
     * @param req HttpServletRequest
     * @return List<ListObjVO>
     */
    @RequestMapping(value = "/getEquipSelect2", method = RequestMethod.GET)
    @ResponseBody
    public List<ListObjVO> getEquipSelect2(HttpServletRequest req) throws Exception {

        String word = req.getParameter("q");

        if (word != null) word = new String(word.getBytes("8859_1"), "UTF-8");
        return getDao().getEquipSelect2(word);
    }

    /**
     * 장비그룹 연계 장비그룹 기본항목 가져오기(Select2)
     *
     * @param req HttpServletRequest
     * @return List<ListObjVO>
     */
    @RequestMapping(value = "/getEquipGrpSelect2", method = RequestMethod.GET)
    @ResponseBody
    public List<ListObjVO> getEquipGrpSelect2(HttpServletRequest req) throws Exception {

        String word = req.getParameter("q");
        if (word != null) word = new String(word.getBytes("8859_1"), "UTF-8");

        return getDao().getEquipGrpSelect2(word);
    }
}
