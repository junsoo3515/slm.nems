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
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.security.SlaItemDao;
import slm.www.vo.security.SlaItemVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * SLA 항목관리
 * <p/>
 * User: 이준수
 * Date: 2017.05.24
 * Time: 오전 9:45
 */
@Controller
@RequestMapping("/security")
public class SlaItemCtr {

    private static final Logger log = LoggerFactory.getLogger(SlaItemCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S008";

    /**
     * SLA 항목관리
     *
     * @return SlaItemDao
     */
    public SlaItemDao getDao() {
        return sqlSession.getMapper(SlaItemDao.class);
    }


    /**
     * SLA 항목관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/slaitem", method = RequestMethod.GET)
    public ModelAndView slaitemView(HttpServletRequest req) {

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
            mv.addObject("nmList", new ObjectMapper().writeValueAsString(getDao().getTopicNmList())); //보고서 항목이름 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 평가기준 관리 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getSlaEvalList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getSlaEvalList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaItemVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcCode = null; // 검색어
            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcCode = CmnFilterBiz.filterSqlString(jsonFilter.get("srcCode"));
            }

            // 평가기준관리 개수 가져오기
            int dataCnt = getDao().getSlaEvalListCnt(srcCode, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getSlaEvalList(srcCode, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaItemVO>() {
            });
        }
    }

    /**
     * 평가기준 상세정보 가져오기
     *
     * @param vo  {eval_cd:평가지표 코드}
     * @param ses HttpSession
     * @return SlaItemVO
     */
    @RequestMapping(value = "/getSlaEvalData", method = RequestMethod.POST)
    @ResponseBody
    public SlaItemVO getSlaEvalData(@RequestBody SlaItemVO vo, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);


            return getDao().getSlaEvalData(vo.eval_cd);

        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 평가기준 등록(Insert/Update)
     *
     * @param vo  SlaItemVO
     * @param ses HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setSlaEvalAct", method = RequestMethod.POST)
    @ResponseBody
    public int setSlaEvalAct(String oper, @RequestBody SlaItemVO vo, HttpSession ses) {
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
            if (vo.divCd == 0) getDao().setSlaEvalAct(vo);
            else getDao().setSlaEvalUpdate(vo);

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            return 1;
        }

        return 2;
    }

    /**
     * 보고서 연결  목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getReportConnList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getReportConnList(String id, String sidx, String sord, int rows, boolean _search, String
            searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaItemVO>() {
            });
        }

        String eval_cd = null; // 평가지표 코드

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                eval_cd = CmnFilterBiz.filterSqlString(jsonFilter.get("eval_cd"));
            }

            // 보고서 연결 개수 가져오기
            int dataCnt = getDao().getReportConnListCnt(eval_cd); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getReportConnList(eval_cd, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaItemVO>() {
            });
        }
    }

    /**
     * 보고서연결 삭제(Delete)
     *
     * @param vo  SlaItemVO
     * @param ses HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setReportConnDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportConnDel(@RequestBody SlaItemVO vo, HttpSession ses) {

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
            getDao().setReportConnDel(vo);
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
     * 보고서 연결 데이터 저장(Insert/Update)
     *
     * @param id   String
     * @param oper String
     * @param vo   SlaItemVO
     * @param ses  HttpSession
     * @return int
     */
    @RequestMapping(value = "/setReportConnAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportConnAct(String id, String oper, SlaItemVO vo, HttpSession ses) {
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
            if (oper.equals("add")) getDao().setReportConnAct(vo);
            else getDao().setReportConnUpdate(vo);

            tmpOK = true;


        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            tmpOK = false;

        }
        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }
}
