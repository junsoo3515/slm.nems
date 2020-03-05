package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.etc.CmnEtcBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.security.RptEvalItemDao;
import slm.www.vo.security.RptEvalItemVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 보고서 평가항목관리
 * <p/>
 * User: 이준수
 * Date: 2017.05.29
 * Time: 오후 4:43
 */
@Controller
@RequestMapping("/security")
public class RptEvalItemCtr {

    private static final Logger log = LoggerFactory.getLogger(RptEvalItemCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S007";

    /**
     * 보고서 평가항목관리 DAO
     *
     * @return RptEvalItemDao
     */
    public RptEvalItemDao getDao() {
        return sqlSession.getMapper(RptEvalItemDao.class);
    }

    /**
     * 보고서 평가항목관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/rptevalitem", method = RequestMethod.GET)
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
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); //사용자별 Crud 권한 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기
            mv.addObject("groupNmList", new ObjectMapper().writeValueAsString(getDao().getGroupNmList())); //그룹관리 항목 가져오기
            mv.addObject("reportNmList", new ObjectMapper().writeValueAsString(getDao().getReportNmList())); //보고서항목 가져오기

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return mv;
    }

    /**
     * 그룹관리 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getEvalGroupList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getEvalGroupList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptEvalItemVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcWord = null;

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcWord = CmnFilterBiz.filterSqlString(jsonFilter.get("srcWord"));
            }

            int dataCnt = getDao().getEvalGroupListCnt(srcWord, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getEvalGroupList(srcWord, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptEvalItemVO>() {
            });
        }
    }


    /**
     * 평가기준 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getEvalItemList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getEvalItemList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptEvalItemVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);
            String grp_cd = null; // 그룹코드

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                grp_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("grp_cd"));
            }

            int dataCnt = getDao().getEvalItemListCnt(grp_cd, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getEvalItemList(grp_cd, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptEvalItemVO>() {
            });
        }
    }


    /**
     * 평가기준 상세정보 가져오기
     *
     * @param vo  {grp_cd:그룹 코드}
     * @param ses HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/getEvalItemInfoData", method = RequestMethod.POST)
    @ResponseBody
    public RptEvalItemVO getEvalItemInfoData(@RequestBody RptEvalItemVO vo, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
            return getDao().getEvalItemInfoData(vo);

        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 그룹관리 삭제하기(Delete)
     *
     * @param vo  RptEvalItemVO
     * @param ses HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/setEvalGroupDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setEvalGroupDel(@RequestBody RptEvalItemVO vo, HttpSession ses) {
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

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setReportAct-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            getDao().setEvalGroupMapDel(vo);
            getDao().setEvalGroupItemDel(vo);
            getDao().setEvalGroupDel(vo);

            transactionManager.commit(status);

            tmpOK = true;


        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            tmpOK = false;
            transactionManager.rollback(status);
        }
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 그룹관리 추가하기(Insert/Update)
     *
     * @param oper String
     * @param vo   RptEvalItemVO
     * @param ses  HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/setEvalGroupAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setEvalGroupAct(String oper, RptEvalItemVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

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
            if (oper.equals("add")) getDao().setEvalGroupAct(vo);
            else getDao().setEvalGroupUpdate(vo);

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
     * 평가기준 추가하기(Insert/Update)
     *
     * @param vo  RptEvalItemVO
     * @param ses HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/setEvalItemInfoAct", method = RequestMethod.POST)
    @ResponseBody
    public int setEvalItemInfoAct(@RequestBody RptEvalItemVO vo, HttpSession ses) {
        String[] arr = {"C", "U"};
        int res = 0;
        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

        } catch (Exception ex) {

            return res;
        }


        try {
            if (vo.crud.equals("C")) res = getDao().setEvalItemInfoAct(vo);
            else res = getDao().setEvalItemInfoUpdate(vo);


        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return res;
    }

    /**
     * 평가기준 삭제하기(Delete)
     *
     * @param vo  RptEvalItemVO
     * @param ses HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/setEvalItemDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setEvalItemDel(@RequestBody RptEvalItemVO vo, HttpSession ses) {

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
            getDao().setEvalItemDel(vo);
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
     * 보고서연계 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getReportMapList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getReportMapList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptEvalItemVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);
            String grp_cd = null; // 그룹코드

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                grp_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("grp_cd"));
            }

            int dataCnt = getDao().getReportMapListCnt(grp_cd, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getReportMapList(grp_cd, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptEvalItemVO>() {
            });
        }
    }

    /**
     * 보고서연계 추가하기(Insert/Update)
     *
     * @param oper String
     * @param vo   RptEvalItemVO
     * @param ses  HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/setReportMapAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportMapAct(String oper, RptEvalItemVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};
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
            if (oper.equals("add")) getDao().setReportMapAct(vo);
            else getDao().setReportMapUpdate(vo);

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
     * 보고서 연결 삭제하기(Delete)
     *
     * @param vo  RptEvalItemVO
     * @param ses HttpSession
     * @return RptEvalItemVO
     */
    @RequestMapping(value = "/setReportMapDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportMapDel(@RequestBody RptEvalItemVO vo, HttpSession ses) {

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
            getDao().setReportMapDel(vo);
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
     * 평가기준 목록 정렬 순서 변경
     *
     * @param vo   RptEvalItemVO
     * @param ses  HttpSession
     * @return Map
     */
    @RequestMapping(value = "/setEvalItemPosUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Map setEvalItemPosUpdate(@RequestBody RptEvalItemVO vo, HttpSession ses){

        String[] arr = {"U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

        } catch (Exception ex) {

            return null;
        }

        boolean tmpOK = false;

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("changeEvalItemPos-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {

            getDao().setEvalItemPosUpdate1(vo);
            getDao().setEvalItemPosUpdate2(vo);

            transactionManager.commit(status);

            tmpOK = true;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            transactionManager.rollback(status);

            tmpOK = false;

        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("isSuccess",tmpOK);

        return map;
    }
}


