package slm.www.ctr.rmon;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.DropDownDataDao;
import module.etc.CmnEtcBiz;
import module.secure.encryption.CmnRsaOaepBiz;
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
import slm.www.dao.rmon.ReportDao;
import slm.www.vo.rmon.CheckVO;
import slm.www.vo.rmon.ReportVO;
import slm.www.vo.rmon.TroubleVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 월간주요현황
 * <p/>
 * User: 이종혁
 * Date: 2016.06.09
 * Time: 오후 09:56
 */
@Controller
@RequestMapping("/rmon")
public class RmonReportCtr {

    private static final Logger log = LoggerFactory.getLogger(RmonReportCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S3M001";

    /**
     * 월간주요현황 DAO
     *
     * @return ReportDao
     */
    public ReportDao getDao() {
        return sqlSession.getMapper(ReportDao.class);
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
     * 월간주요현황 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/report", method = RequestMethod.GET)
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
     * 월간주요현황 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getRmonReportList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRmonReportList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<ReportVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);


        try {

            String sDate = null;
            String eDate = null;

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
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

            int dataCnt = getDao().getRmonReportListCnt(sDate, eDate, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getRmonReportList(sDate, eDate, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<ReportVO>() {
            });
        }
    }


    /**
     * 월간주요현황점검현황 목록 jqGrid 호출 Ctr
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
     * @param da_seq       리포트 코드
     * @param ses          HttpSession
     * @return ResultJQGridVO
     */
    @RequestMapping(value = "/getRmonReportCheckList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRmonReportCheckList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, String da_seq, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<ReportVO>() {
            });
        }
        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        da_seq = CmnFilterBiz.filterPureString(da_seq);

        List<ReportVO> reportvo = getDao().getRmonReportDate(da_seq);

        ReportVO tempvo = reportvo.get(0);

        List<CheckVO> arry = null;

        int dataCnt = getDao().getRmonReportCheckListCnt(tempvo.start_dt, tempvo.end_dt, vo); // 총 갯수 가져오기
        if (dataCnt == 0) {
        } else {

            arry = getDao().getRmonReportCheckList(tempvo.start_dt, tempvo.end_dt, vo);
        }
        int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산
        return new ResultJQGridVO(vo.page, dataCnt, pageCnt, arry);
    }

    /**
     * 월간주요현황 상세정보 가져오기
     *
     * @param obj {da_seq:월간주요현황 코드 seq}
     * @param ses HttpSession
     * @return ReportVO
     */
    @RequestMapping(value = "/getRmonReportData", method = RequestMethod.POST)
    @ResponseBody
    public ReportVO getRmonReportData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String da_seq = "";

            if (obj.containsKey("da_seq")) {

                if (!StringUtils.isEmpty(obj.get("da_seq").toString())) {

                    da_seq = CmnFilterBiz.filterPureString(obj.get("da_seq").toString());
                }
            }
            ReportVO vo = getDao().getRmonReportData(da_seq);
            return vo;
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 월간주요현황 점검현황 등락 정보 가져오기
     *
     * @param obj {da_seq:월간주요현황 코드 seq}
     * @param ses HttpSession
     * @return ReportVO
     */
    @RequestMapping(value = "/getRmonReportCheckDateUpDown", method = RequestMethod.POST)
    @ResponseBody
    public CheckVO getRmonReportCheckDateUpDown(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String da_seq = "";

            if (obj.containsKey("da_seq")) {

                if (!StringUtils.isEmpty(obj.get("da_seq").toString())) {

                    da_seq = CmnFilterBiz.filterPureString(obj.get("da_seq").toString());
                }
            }

            List<ReportVO> reportvo = getDao().getRmonReportDate(da_seq);


            // 해당 월에 대한
            ReportVO tempvo = reportvo.get(0);
            ReportVO tempvo2 = reportvo.get(1);

            CheckVO tempCheck = getDao().getRmonReportCheckDateUpDown(tempvo.start_dt, tempvo.end_dt);
            CheckVO tempCheck2 = getDao().getRmonReportCheckDateUpDown(tempvo2.start_dt, tempvo2.end_dt);
            if (tempCheck.error_time_sum_second > tempCheck2.error_time_sum_second) {
                tempCheck.up_down_error = "up";
            } else if (tempCheck.error_time_sum_second == tempCheck2.error_time_sum_second) {
                tempCheck.up_down_error = "eq";
            } else {
                tempCheck.up_down_error = "down";
            }
            if (tempCheck.hold_time_sum_second > tempCheck2.hold_time_sum_second) {
                tempCheck.up_down_hold = "up";
            } else if (tempCheck.hold_time_sum_second == tempCheck2.hold_time_sum_second) {
                tempCheck.up_down_hold = "eq";
            } else {
                tempCheck.up_down_hold = "down";
            }


            return tempCheck;
        } catch (Exception ex) {

            return null;
        }
    }


    /**
     * 월간주요현황 정보 저장(Insert, Update)
     * - 월간주요현황 정보 저장, 수정
     *
     * @param vo  ReportVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setReportAct", method = RequestMethod.POST)
    @ResponseBody
    public int setReportAct(@RequestBody ReportVO vo, HttpSession ses) {

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

            vo.da_seq = CmnFilterBiz.filterPureString(vo.da_seq);
            vo.brief_dt = CmnFilterBiz.filterPureString(vo.brief_dt);
            vo.start_dt = CmnFilterBiz.filterPureString(vo.start_dt);
            vo.end_dt = CmnFilterBiz.filterPureString(vo.end_dt);
            vo.pfmc = CmnFilterBiz.filterSqlString(vo.pfmc);
            vo.plan = CmnFilterBiz.filterSqlString(vo.plan);

            if (vo.da_seq != null && vo.da_seq.length() != 0) {
                resCnt = getDao().setReportUpdate(vo);
            } else {
                resCnt = getDao().setReportInsert(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 월간주요현황 정보 삭제(Delete)
     * - 월간주요현황 정보
     *
     * @param vo  ReportVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setReportDel", method = RequestMethod.POST)
    @ResponseBody
    public int setReportDel(@RequestBody ReportVO vo, HttpSession ses) {

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

        vo.da_seq = CmnFilterBiz.filterPureString(vo.da_seq);

        try {

            if (vo.da_seq != null && vo.da_seq.length() != 0) {
                resCnt = getDao().setReportDelete(vo);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 보고서 출력을 위한 암호화 키 가져오기
     * <p/>
     *
     * @param reqData da_seq : 보고서 고유 일련번호
     * @return
     */
    @RequestMapping(value = "/getReportSecureData", method = RequestMethod.POST)
    @ResponseBody
    public Map getReportSecureData(@RequestBody Map<String, String> reqData) {

        String secureCd = "";

        try {

            secureCd = CmnRsaOaepBiz.encrypt(reqData.get("da_seq").toString());

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        Map<String, String> map = new HashMap<String, String>();

        map.put("key", secureCd);

        return map;
    }

    /**
     * 월간주요현황장애세부내역 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getTroubleDetailList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getTroubleDetailList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, String da_seq, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<ReportVO>() {
            });
        }
        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        da_seq = CmnFilterBiz.filterPureString(da_seq);

        List<ReportVO> reportvo = getDao().getRmonReportDate(da_seq);

        ReportVO tempvo = reportvo.get(0);

        List<TroubleVO> arry = null;

        int dataCnt = getDao().getTroubleDetailListCnt(tempvo.start_dt, tempvo.end_dt, vo); // 총 갯수 가져오기
        if (dataCnt == 0) {
            arry = null;
        } else {
            arry = getDao().getTroubleDetailList(tempvo.start_dt, tempvo.end_dt, vo);
        }
        int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산
        return new ResultJQGridVO(vo.page, dataCnt, pageCnt, arry);
    }

    /**
     * 월별 헤더 및 컬럼명 가져오기
     * @return List<ListObjVO>
     */
    @RequestMapping(value = "/getTroubleAmt", method = RequestMethod.POST)
    @ResponseBody
    public List<ListObjVO> getTroubleAmt(@RequestBody Map<String, Object> reqData) {

        try {

            List<ReportVO> reportvo = getDao().getRmonReportDate(reqData.get("da_seq").toString());

            ReportVO tempvo = reportvo.get(0);
            String subStart_dt = tempvo.start_dt.substring(4,6);
            if(subStart_dt.equals("04") || subStart_dt.equals("06") || subStart_dt.equals("09") || subStart_dt.equals("11"))
                subStart_dt = "30";
            else if(subStart_dt.equals("02"))
                subStart_dt = "28";
            else
                subStart_dt = "31";

            return getDao().getTroubleAmt(tempvo.start_dt, subStart_dt);

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return new ArrayList<ListObjVO>();
        }
    }

    /**
     * 월간주요현황 장애현황표 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getTroubleGridList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getTroubleGridList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, String da_seq, HttpSession ses) {
        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<ReportVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            if (da_seq != null)
                da_seq = CmnFilterBiz.filterPureString(da_seq);

            List<String> arrHeaderKey = new ArrayList<String>();

            List<ReportVO> reportvo = getDao().getRmonReportDate(da_seq);

            ReportVO tempvo = reportvo.get(0);

            String subStart_dt = tempvo.start_dt.substring(4, 6);

            if (subStart_dt.equals("04") || subStart_dt.equals("06") || subStart_dt.equals("09") || subStart_dt.equals("11"))
                subStart_dt = "30";
            else if (subStart_dt.equals("02"))
                subStart_dt = "28";
            else
                subStart_dt = "31";

            // 월별 헤더 및 칼럼 명 설정
            int colCnt = 0;
            for (ListObjVO obj : getDao().getTroubleAmt(tempvo.start_dt, subStart_dt)) {
                if (colCnt > Integer.parseInt(subStart_dt.trim())) {
                    break;
                }

                arrHeaderKey.add(obj.id);
                colCnt++;
            }

            // 장애현황 데이터 가져오기
            TreeMap<String, Map<String, Object>> resData = new TreeMap<>();

            for (final TroubleVO obj : getDao().getTroubleGridList(tempvo.start_dt, tempvo.end_dt, vo)) {
                resData.put(obj.rpt_cd, new HashMap<String, Object>() {
                    {
                        put("eqp_type", obj.eqp_type);
                        put("eqp_nm", obj.eqp_nm);
                        put("cnt_err", obj.cnt_err);
                    }
                });
            }

            List<Map<String, Object>> troubleDayList = getDao().getTroubleDayList(tempvo.start_dt,tempvo.end_dt, "col", new ArrayList(resData.keySet()), arrHeaderKey);
            for (Map<String, Object> obj : troubleDayList) {

                if (resData.containsKey(obj.get("RPT_CD"))) {

                    for (String colKey : arrHeaderKey) {

                        String col = "COL".concat(colKey);

                        if (obj.get(col) != null) {
                            resData.get(obj.get("RPT_CD").toString()).put(col.toLowerCase(), obj.get(col).toString());
                        }
                    }
                }
            }
            troubleDayList = null;

            return new ResultJQGridVO(1, 0, 1, new ArrayList(resData.descendingMap().values()));
        } catch (Exception ex){

            return new ResultJQGridVO(1,0,1, new ArrayList<TroubleVO>(){
            });

        }
    }

}
