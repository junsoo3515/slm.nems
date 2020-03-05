package slm.www.ctr.rday;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.DropDownDataDao;
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
import slm.www.dao.rday.RequestDao;
import slm.www.vo.rday.MeaSureVO;
import slm.www.vo.rday.RequestVO;
import slm.www.vo.security.AuthorityVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 요청사항
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
@Controller
@RequestMapping("/rday")
public class RdayRequestCtr {

    private static final Logger log = LoggerFactory.getLogger(RdayRequestCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private SqlSession sqlBatchSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S2D004";

    /**
     * 요청사항 DAO
     *
     * @return RequestDao
     */
    public RequestDao getDao() {
        return sqlSession.getMapper(RequestDao.class);
    }

    /**
     * 공통 드롭다운 DAO
     *
     * @return DropDownDataDao
     */
    public DropDownDataDao getDropDownDataDao() {

        return sqlSession.getMapper(DropDownDataDao.class);
    }

    /**
     * 요청사항 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/request", method = RequestMethod.GET)
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
            mv.addObject("grpList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("EG"))); // 장비그룹(유형)
            mv.addObject("reQuestTypeList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("RT"))); // 요청그룹(유형)
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return mv;
    }

    /**
     * 요청사항 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getRdayRequestList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRdayRequestList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

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

        String srcHeadGrp = null;
        String fin_fl = null;
        String sDate = null;
        String eDate = null;
        String srcGrp = null;
        String srcRequestGrp = null;

        ObjectMapper mapper = new ObjectMapper();

        try {

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>(){});

                if (jsonFilter.containsKey("srcHeadGrp")) {
                    srcHeadGrp = CmnFilterBiz.filterSqlString(jsonFilter.get("srcHeadGrp")).toUpperCase();
                }
                if (jsonFilter.containsKey("srcSDate")) {
                    sDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcSDate"));
                    sDate = sDate + "000000";
                }
                if (jsonFilter.containsKey("srcEDate")) {
                    eDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcEDate"));
                    eDate = eDate + "999999";
                }
                if (jsonFilter.containsKey("fin_fl")) {
                    fin_fl = "Y";
                }
                if (jsonFilter.containsKey("srcGrp")) {
                    srcGrp = CmnFilterBiz.filterSqlString(jsonFilter.get("srcGrp"));
                    if (srcGrp.equals("")) {
                        srcGrp = null;
                    }
                }
                if (jsonFilter.containsKey("srcRequestGrp")) {
                    srcRequestGrp = CmnFilterBiz.filterSqlString(jsonFilter.get("srcRequestGrp"));
                    if (srcRequestGrp.equals("")) {
                        srcRequestGrp = null;
                    }
                }
            }

            int dataCnt = getDao().getRdayRequestListCnt(srcHeadGrp, sDate, eDate, fin_fl, srcGrp, srcRequestGrp, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getRdayRequestList(srcHeadGrp, sDate, eDate, fin_fl, srcGrp, srcRequestGrp, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<AuthorityVO>() {
            });
        }
    }


    /**
     * 요청사항조치사항 목록 jqGrid 호출 Ctr
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
     * @param cause_seq    장애 코드
     * @param ses          HttpSession
     * @return ResultJQGridVO
     */
    @RequestMapping(value = "/getRdayRequestMeasureList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRdayRequestMeasureList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, String cause_seq, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<RequestVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);


        List<MeaSureVO> arry = getDao().getRdayRequestMeasureList(cause_seq, vo);
        int dataCnt = getDao().getRdayRequestMeasureListCnt(cause_seq, vo); // 총 갯수 가져오기
        int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산
        return new ResultJQGridVO(vo.page, dataCnt, pageCnt, arry);
    }

    /**
     * 요청사항 상세정보 가져오기
     *
     * @param obj {req_seq:요청사항 코드 seq}
     * @param ses HttpSession
     * @return RequestVO
     */
    @RequestMapping(value = "/getRdayRequestData", method = RequestMethod.POST)
    @ResponseBody
    public RequestVO getRdayRequestData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String req_seq = "";

            if (obj.containsKey("req_seq")) {

                if (!StringUtils.isEmpty(obj.get("req_seq").toString())) {

                    req_seq = CmnFilterBiz.filterPureString(obj.get("req_seq").toString());
                }
            }
            RequestVO vo = getDao().getRdayRequestData(req_seq);
            return vo;
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 요청사항조치사항 정보 저장(Insert, Update)
     * - 요청사항조치사항 저장, 수정
     *
     * @param vo  RequestVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setRequestMeasureAct", method = RequestMethod.POST)
    @ResponseBody
    public int setRequestMeasureAct(@RequestBody MeaSureVO vo, HttpSession ses) {

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

            vo.reg_mem_id = ses.getAttribute("id").toString();
            vo.reg_mem_nm = ses.getAttribute("member_nm").toString();
            vo.mea_dt = CmnFilterBiz.filterPureString(vo.mea_dt);
            vo.measure_cont = CmnFilterBiz.filterSqlString(vo.measure_cont);

            if (vo.mea_seq != null && vo.mea_seq.length() != 0) {
                resCnt = getDao().setRequestMeasureUpdate(vo);
            } else {
                resCnt = getDao().setRequestMeasureInsert(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 요청사항 정보 저장(Insert, Update)
     * - 요청사항 정보 저장, 수정
     *
     * @param vo  RequestVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setRequestAct", method = RequestMethod.POST)
    @ResponseBody
    public int setRequestAct(@RequestBody RequestVO vo, HttpSession ses) {

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

            vo.reg_mem_id = ses.getAttribute("id").toString();
//            vo.reg_mem_nm = ses.getAttribute("member_nm").toString();
//            vo.reg_mem_nm = CmnFilterBiz.filterSqlString(vo.reg_mem_nm);
            vo.req_dt = CmnFilterBiz.filterPureString(vo.req_dt);
            vo.mea_plan_dt = CmnFilterBiz.filterPureString(vo.mea_plan_dt);
            vo.mea_fin_dt = CmnFilterBiz.filterPureString(vo.mea_fin_dt);
            vo.request_type = CmnFilterBiz.filterSqlString(vo.request_type);
            vo.equip_type = CmnFilterBiz.filterPureString(vo.equip_type);
            vo.eqp_cd = CmnFilterBiz.filterPureString(vo.eqp_cd);
            vo.fin_fl = CmnFilterBiz.filterSqlString(vo.fin_fl);
            if (vo.cont != null && vo.cont.length() != 0) {
                vo.cont = CmnFilterBiz.filterSqlString(vo.cont);
            }
            if (vo.reg_mem_nm != null && vo.reg_mem_nm.length() != 0) {
                vo.reg_mem_nm = CmnFilterBiz.filterSqlString(vo.reg_mem_nm);
            }


            if (vo.req_seq != null && vo.req_seq.length() != 0) {
                resCnt = getDao().setRequestUpdate(vo);
            } else {
                resCnt = getDao().setRequestInsert(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 요청사항 정보 삭제(Delete)
     * - 요청사항 정보
     *
     * @param vo  RequestVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setRequestDel", method = RequestMethod.POST)
    @ResponseBody
    public int setRequestDel(@RequestBody RequestVO vo, HttpSession ses) {

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

            if (vo.req_seq != null && vo.req_seq.length() != 0) {
                resCnt = getDao().setRequestDelete(vo);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 요청사항조치사항 정보 삭제(Delete)
     * - 요청사항조치사항 정보
     *
     * @param vo  RequestVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setRequestMeasureDel", method = RequestMethod.POST)
    @ResponseBody
    public int setRequestMeasureDel(@RequestBody MeaSureVO vo, HttpSession ses) {

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

            if (vo.mea_seq != null && vo.mea_seq.length() != 0) {
                resCnt = getDao().setRequestMeasureDelete(vo);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

}
