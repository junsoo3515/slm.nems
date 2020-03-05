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
import slm.www.dao.security.RptCodeDao;
import slm.www.vo.security.RptCodeVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 보고서 코드 관리
 * <p/>
 * User: 이준수
 * Date: 2017.06.07
 * Time: 오전 09:27
 */

@Controller
@RequestMapping("/security")
public class RptCodeCtr {

    private static final Logger log = LoggerFactory.getLogger(RptCodeCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S004";

    public RptCodeDao getDao() {
        return sqlSession.getMapper(RptCodeDao.class);
    }

    @RequestMapping(value = "/rptcode", method = RequestMethod.GET)
    public ModelAndView invoke(HttpServletRequest req) {

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
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); // 사용자별 Crud
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기
            mv.addObject("higRpgCdList", new ObjectMapper().writeValueAsString(getDao().getHigRptCdList())); // 부모 코드 가져오기

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 보고서 코드 관리 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getReportCodeList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getReportCodeList(String sidx, String sord, int rows, Boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};
        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);

            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception e) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptCodeVO>());
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);
            String srcWord = null; // 검색어(코드/명칭)

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcWord = CmnFilterBiz.filterSqlString(jsonFilter.get("srcWord"));
            }

            // 보고서 코드 관리 개수 가져오기
            int dataCnt = getDao().getReportCodeListCnt(srcWord, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getReportCodeList(srcWord, vo));
        } catch (Exception e) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptCodeVO>());
        }
    }

    /**
     * 보고서 코드관리 저장하기(Insert/Update)
     *
     * @param id   String
     * @param oper String
     * @param vo   RptCodeVO
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setReportCodeAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportCodeAct(String id, String oper, RptCodeVO vo, HttpSession ses) {

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

            if (oper.equals("add")) getDao().setReportCodeInsert(vo);
            else getDao().setReportCodeUpdate(vo);

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
     * 장비 연계 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getReportEquipMapList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getReportEquipMapList(String sidx, String sord, int rows, Boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};
        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);

            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception e) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptCodeVO>());
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);
            String rpt_cd = null; // 보고서 코드

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                rpt_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("rpt_cd"));
            }

            // 장비연계 개수 가져오기
            int dataCnt = getDao().getReportEquipMapListCnt(vo, rpt_cd); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getReportEquipMapList(vo, rpt_cd));
        } catch (Exception e) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RptCodeVO>());
        }

    }

    /**
     * 장비연계 저장하기(Insert/Update)
     *
     * @param id   String
     * @param oper String
     * @param vo   RptCodeVO
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setReportEquipMapAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportEquipMapAct(String id, String oper, RptCodeVO vo, HttpSession ses) {

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
            if (oper.equals("add")) getDao().setReportEquipMapInsert(vo);
            else getDao().setReportEquipMapUpdate(vo);
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
     * 장비연계 삭제하기(Delete)
     *
     * @param vo  RptCodeVO
     * @param ses HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setReportEquipMapDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportEquipMapDel(@RequestBody RptCodeVO vo, HttpSession ses) {

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

            getDao().setReportEquipMapDelete(vo);

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
     * 장비연계 장비 가져오기(Select2)
     *
     * @param req HttpServletRequest
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getEquipSelect2(HttpServletRequest req) throws Exception {

        String word = req.getParameter("q");
        if (word != null) word = new String(word.getBytes("8859_1"), "UTF-8");

        return getDao().getEquipSelect2(word);
    }

    /**
     * 장비연계 정렬 순서 변경
     *
     * @param vo   RptCodeVO
     * @param ses  HttpSession
     * @return Map
     */
    @RequestMapping(value = "/setEqpMapPosUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Map setEqpMapPosUpdate(@RequestBody RptCodeVO vo, HttpSession ses){

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

        def.setName("setEqpMapPosUpdate-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {

            getDao().setEqpMapPosUpdate1(vo);
            getDao().setEqpMapPosUpdate2(vo);

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
