package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.etc.CmnEtcBiz;
import module.excel.poi.CmnExcelBiz;
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.security.LinkDao;
import slm.www.vo.security.LinkVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * NMS 연계 관리
 * <p/>
 * User: 이준수
 * Date: 2017.05.17
 * Time: 오전 10:21
 */
@Controller
@RequestMapping("/security")
public class LinkCtr {

    private static final Logger log = LoggerFactory.getLogger(LinkCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S009";

    /**
     * 연계서비스(NMS) 코드와 데이터에서 사용하는 DAO
     *
     * @return LinkDao
     */
    public LinkDao getDao() {
        return sqlSession.getMapper(LinkDao.class);
    }

    /**
     * NMS 연계 관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/link", method = RequestMethod.GET)
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
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); // 사용자별 Crud 권한 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;

    }

    /**
     * 연계서비스(NMS) 코드 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getNmsCodeList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getNmsCodeList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};
        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<LinkVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcCode = null; // HOST 코드/항목코드

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcCode = CmnFilterBiz.filterSqlString(jsonFilter.get("srcCode"));
            }

            // 연계시스템(NMS) 코드 개수 가져오기
            int dataCnt = getDao().getNmsCodeListCnt(srcCode, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getNmsCodeList(srcCode, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<LinkVO>() {
            });
        }
    }

    /**
     * 연계서비스(NMS) 코드 저장(Update)
     *
     * @param id   String
     * @param oper String
     * @param vo   LinkVO
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setNmsCodeAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setNmsCodeAct(String id, String oper, LinkVO vo, HttpSession ses) {

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

        def.setName("setReportAct-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {

            getDao().setNmsCodeUpdate(vo);
            getDao().setNmsDataCodeUpdate(vo);

            tmpOK = true;

            transactionManager.commit(status);


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
     * 연계서비스(NMS) 데이터 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getNmsDataList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getNmsDataList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<LinkVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcCode = null; // 검색 코드
            String srcSDate = null; // 시작일
            String srcEDate = null; // 종료일
            String host_cd = null; // HOST 코드


            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcCode = CmnFilterBiz.filterSqlString(jsonFilter.get("srcCode"));
                srcSDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcSDate"));
                srcEDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcEDate"));
                host_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("host_cd"));
            }

            // 연계시스템(NMS) 코드 개수 가져오기
            int dataCnt = getDao().getNmsDataListCnt(srcCode, vo, host_cd, srcSDate, srcEDate); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getNmsDataList(srcCode, srcSDate, srcEDate, vo, host_cd));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<LinkVO>() {
            });
        }
    }

    /**
     * 연계서비스(NMS) 데이터 저장(Update)
     *
     * @param id   String
     * @param oper String
     * @param vo   LinkVO
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setNmsDataAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setNmsDataAct(String id, String oper, LinkVO vo, HttpSession ses) {


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


        try {
            getDao().setNmsDataUpdate(vo);

            tmpOK = true;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 연계서비스(NMS) 데이터 엑셀 내보내기
     *
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param ses HttpSession
     */
    @RequestMapping(value = "/exportNmsDataExcel", method = RequestMethod.POST)
    @ResponseBody
    public void getNmsDataExcel(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {


        // 0. 사용자 권한 체크
        String[] arr = {"R"};

        long time = System.currentTimeMillis();
        SimpleDateFormat currentTime = new SimpleDateFormat("yyyy-MM-dd");
        String str = currentTime.format(new Date(time));
        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            // 1. 검색 조건 가져오기
            Map<String, String> param = new HashMap<String, String>();

            for (Map.Entry<String, String[]> obj : ((Map<String, String[]>) req.getParameterMap()).entrySet()) {

                param.put(obj.getKey(), CmnFilterBiz.filterSqlString(obj.getValue()[0]));
            }

            String host_cd = req.getParameter("host_cd");
            String itm_cd = req.getParameter("itm_cd");
            String srcSDate = req.getParameter("srcSDate");
            String srcEDate = req.getParameter("srcEDate");


            // 2. 엑셀 헤더 및 칼럼 명 설정
            List<String> arrHeader = new ArrayList<String>() {
                {
                    add("HOST 코드");
                    add("항목 코드");
                    add("보고일");
                    add("평균 값");
                }
            };
            List<String> arrHeaderKey = new ArrayList<String>() {
                {
                    add("HOST_CD");
                    add("ITM_CD");
                    add("COL_DT");
                    add("AVG_VAL");
                }
            };

            // 3. 연계시스템(NMS) 데이터 가져오기(선택 되어 있는 HOST 코드 값에 따라 데이터 출력)
            List<Map<String, Object>> resData;

            resData = getDao().getNmsDataListReport(host_cd, itm_cd, srcSDate, srcEDate, null);

            CmnExcelBiz.generateXlsxList("/slm/excel/cmn/보고서_리스트유형.xlsx", "Sheet1", "slm", "tmp", "연계서비스데이터" + str + ".xlsx", arrHeader, arrHeaderKey, resData, false, req, res);
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }
}
