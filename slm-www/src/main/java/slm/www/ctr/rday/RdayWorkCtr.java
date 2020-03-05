package slm.www.ctr.rday;

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
import slm.www.dao.rday.WorkDao;
import slm.www.vo.rday.WorkVO;
import slm.www.vo.security.AuthorityVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;

/**
 * 일일작업내역
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
@Controller
@RequestMapping("/rday")
public class RdayWorkCtr {

    private static final Logger log = LoggerFactory.getLogger(RdayWorkCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private SqlSession sqlBatchSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S2D002";

    /**
     * 일일작업내역 DAO
     *
     * @return WorkDao
     */
    public WorkDao getDao() {
        return sqlSession.getMapper(WorkDao.class);
    }


    /**
     * 일일작업내역 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/work", method = RequestMethod.GET)
    public ModelAndView invokeView(HttpServletRequest req) {

        if (etcBiz == null) {

            etcBiz = new CmnEtcBiz(sqlSession);
        }

        ModelAndView mv = new ModelAndView();

        try {
            String path = req.getContextPath();
            String id = req.getSession().getAttribute("id").toString();


            mv.addObject("p", path); // PATH 가져오기
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); //사용자별 Crud 권한 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return mv;
    }

    /**
     * 일일작업내역 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getRdayWorkList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRdayWorkList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<AuthorityVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String sDate = null;
        String eDate = null;

        ObjectMapper mapper = new ObjectMapper();

        try {

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                if (jsonFilter.containsKey("srcSDate")) {
                    sDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcSDate"));
                    sDate = sDate + "000000";
                }
                if (jsonFilter.containsKey("srcEDate")) {
                    eDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcEDate"));
                    eDate = eDate + "999999";
                }
            }

            int dataCnt = getDao().getRdayWorkListCnt(sDate, eDate, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getRdayWorkList(sDate, eDate, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<AuthorityVO>() {
            });
        }
    }

    /**
     * 일일작업내역 상세정보 가져오기
     *
     * @param obj {rec_seq:일일작업내역 코드}
     * @param ses HttpSession
     * @return WorkVO
     */
    @RequestMapping(value = "/getRdayWorkData", method = RequestMethod.POST)
    @ResponseBody
    public WorkVO getRdayWorkData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        String rec_seq = "";

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);


            if (obj.containsKey("rec_seq")) {

                if (!StringUtils.isEmpty(obj.get("rec_seq").toString())) {

                    rec_seq = CmnFilterBiz.filterPureString(obj.get("rec_seq").toString());
                }
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);

        }

        return getDao().getRdayWorkData(rec_seq);
    }

    /**
     * 일일작업내역 정보 저장(Insert, Update)
     * - 일일작업내역 정보 저장,수정
     *
     * @param vo  WorkVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setWorkAct", method = RequestMethod.POST)
    @ResponseBody
    public int setWorkAct(@RequestBody WorkVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return 0;
        }

        int resCnt = -1;

        try {
            vo.title = CmnFilterBiz.filterSqlString(vo.title);
            vo.occur_dt = CmnFilterBiz.filterPureString(vo.occur_dt);
            if (vo.op_issue != null && vo.op_issue.length() != 0) {
                vo.op_issue = CmnFilterBiz.filterSqlString(vo.op_issue);
            }
            if (vo.req_issue != null && vo.req_issue.length() != 0) {
                vo.req_issue = CmnFilterBiz.filterSqlString(vo.req_issue);
            }
            if (vo.policy_issue != null && vo.policy_issue.length() != 0) {
                vo.policy_issue = CmnFilterBiz.filterSqlString(vo.policy_issue);
            }
            if (vo.rec_seq != null && vo.rec_seq.length() != 0) {
                resCnt = getDao().setWorkUpdate(vo);
            } else {
                resCnt = getDao().setWorkInsert(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 일일작업내역 정보 삭제(Delete)
     * - 일일작업내역 정보
     *
     * @param vo  WorkVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setWorkDel", method = RequestMethod.POST)
    @ResponseBody
    public int setWorkDel(@RequestBody WorkVO vo, HttpSession ses) {

        String[] arr = {"D"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return 0;
        }

        int resCnt = -1;

        try {

            if (vo.rec_seq != null && vo.rec_seq.length() != 0) {
                resCnt = getDao().setWorkDelete(vo);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

}
