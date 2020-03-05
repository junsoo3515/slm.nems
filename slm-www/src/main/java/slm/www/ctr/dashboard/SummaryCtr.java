package slm.www.ctr.dashboard;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.etc.CmnEtcBiz;
import module.object.CmnMathBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.dashboard.SummaryDao;
import slm.www.dao.rday.InspectDao;
import slm.www.vo.dashboard.DashBoardDailyVO;
import slm.www.vo.dashboard.DashBoardSlaVO;
import slm.www.vo.rday.DaReportSummaryVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dash 보드 요약
 * <p/>
 * User: 이종혁
 * Date: 2016.06.21
 * Time: 오후 5:30
 */

@Controller
@RequestMapping("/dashboard")
public class SummaryCtr {

    private static final Logger log = LoggerFactory.getLogger(SummaryCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S0B001";

    /**
     * 대쉬보드 summary DAO
     *
     * @return ReportDao
     */
    public SummaryDao getDao() {
        return sqlSession.getMapper(SummaryDao.class);


    }

    /**
     * 일일점검현황에서 사용하는 DAO
     *
     * @return InspectDao
     */
    public InspectDao getInSpectDao() {
        return sqlSession.getMapper(InspectDao.class);
    }

    @RequestMapping(value = "/summary", method = RequestMethod.GET)
    public ModelAndView invokeView(HttpServletRequest req) {

        if (etcBiz == null) {

            etcBiz = new CmnEtcBiz(sqlSession);
        }

        ModelAndView mv = new ModelAndView();

        try {
            String path = req.getContextPath();
            String id = req.getSession().getAttribute("id").toString();

            mv.addObject("p", path); // PATH 가져오기
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd)));      //사용자별 Crud 권한 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd));             // 왼쪽메뉴 가져오기
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 대쉬보드 기간별SLA현황 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getDashSlaSummaryList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getDashSlaSummaryList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<DaReportSummaryVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String sDate = null;
        String eDate = null;

        ObjectMapper mapper = new ObjectMapper();

        Map<String, DashBoardSlaVO> resData = new LinkedHashMap<String, DashBoardSlaVO>();

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

            // 대쉬보드 SLA 리포트 정보 가져오기
            for (DashBoardSlaVO dashBoardSlaVO : getDao().getDashSlaReportInfo(sDate, eDate)) {
                resData.put(dashBoardSlaVO.sla_seq, dashBoardSlaVO);
            }

            String tempKey;
            DashBoardSlaVO tmpObj;

            //대쉬보드 SLA 리포트 항목현황 가져오기
            for (DashBoardSlaVO obj : getDao().getDashSlaSummaryInfo(sDate, eDate)) {


                tempKey = obj.sla_seq;


                if (resData.containsKey(tempKey)) {

                    tmpObj = resData.get(tempKey);

                    switch (obj.hig_rpt_cd) {
                        case "0103010000":

                            tmpObj.timeliness = obj.mea_point;

                            break;
                        case "0103020000":

                            tmpObj.availability = obj.mea_point;

                            break;
                        case "0103030000":

                            tmpObj.productivity = obj.mea_point;

                            break;
                        case "0103040000":

                            tmpObj.general = obj.mea_point;

                            break;

                        default:
                            break;
                    }

                }
            }

            for (String mapKey : resData.keySet()) {

                resData.get(mapKey).slatotal = resData.get(mapKey).timeliness + resData.get(mapKey).availability + resData.get(mapKey).productivity + resData.get(mapKey).general;
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return new ResultJQGridVO(1, resData.size(), 1, new ArrayList(resData.values()));
    }

    /**
     * 대쉬보드 기간별점검현황 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getDashDailySummaryList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getDashDailySummaryList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<DaReportSummaryVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String sDate = null;
        String eDate = null;

        Map<String, DashBoardDailyVO> resData = new LinkedHashMap<String, DashBoardDailyVO>();

        try {


            ObjectMapper mapper = new ObjectMapper();

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                if (jsonFilter.containsKey("srcSDate2")) {
                    sDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcSDate2"));
                    sDate = sDate + "000000";
                }
                if (jsonFilter.containsKey("srcEDate2")) {
                    eDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcEDate2"));
                    eDate = eDate + "999999";
                }

            }

            //대쉬보드 일일보고서 리포트 정보 가져오기
            for (DashBoardDailyVO dashBoardDailyVO : getDao().getDashDailyReportInfo(sDate, eDate)) {
                resData.put(dashBoardDailyVO.da_seq, dashBoardDailyVO);
            }

            DecimalFormat df = new DecimalFormat(".#");
            String tempKey;
            DashBoardDailyVO tmpObj;

            // 대쉬보드 일일보고서 리포트 항목현황 가져오기
            for (DashBoardDailyVO obj : getDao().getDashDailySummaryInfo(sDate, eDate)) {


                tempKey = obj.da_seq;

                if (resData.containsKey(tempKey)) {

                    tmpObj = resData.get(tempKey);
                    switch (obj.hig_rpt_cd) {
                        case "0101010000":

                            tmpObj.daily_center_normal = obj.itm_normal_cnt;
                            tmpObj.daily_center_tot = obj.itm_all_cnt;

                            break;
                        case "0101020000":

                            tmpObj.daily_infra_normal = obj.itm_normal_cnt;
                            tmpObj.daily_infra_tot = obj.itm_all_cnt;

                            break;
                        case "0101030000":

                            tmpObj.daily_develop_normal = obj.itm_normal_cnt;
                            tmpObj.daily_develop_tot = obj.itm_all_cnt;

                            break;
                        case "0101040000":

                            tmpObj.daily_solution_normal = obj.itm_normal_cnt;
                            tmpObj.daily_solution_tot = obj.itm_all_cnt;

                            break;

                        default:
                            break;
                    }
                }

            }

            for (String mapKey : resData.keySet()) {
//                resData.get(mapKey).daily_center_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((resData.get(mapKey).daily_center_normal / resData.get(mapKey).daily_center_tot) * 100.0), 1));
//                resData.get(mapKey).daily_infra_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((resData.get(mapKey).daily_infra_normal / resData.get(mapKey).daily_infra_tot) * 100.0), 1));
//                resData.get(mapKey).daily_develop_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((resData.get(mapKey).daily_develop_normal / resData.get(mapKey).daily_develop_tot) * 100.0), 1));
//                resData.get(mapKey).daily_solution_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((resData.get(mapKey).daily_solution_normal / resData.get(mapKey).daily_solution_tot) * 100.0), 1));
//                resData.get(mapKey).daily_tot_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, (((resData.get(mapKey).daily_center_normal + resData.get(mapKey).daily_infra_normal + resData.get(mapKey).daily_develop_normal + resData.get(mapKey).daily_solution_normal) /
//                        (resData.get(mapKey).daily_center_tot + resData.get(mapKey).daily_infra_tot + resData.get(mapKey).daily_develop_tot + resData.get(mapKey).daily_solution_tot)) * 100.0), 1));

                resData.get(mapKey).daily_center_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((Double.isNaN((resData.get(mapKey).daily_center_normal / resData.get(mapKey).daily_center_tot)) ? 0 : (resData.get(mapKey).daily_center_normal / resData.get(mapKey).daily_center_tot)) * 100.0), 1));
                resData.get(mapKey).daily_infra_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((Double.isNaN((resData.get(mapKey).daily_infra_normal / resData.get(mapKey).daily_infra_tot)) ? 0 : (resData.get(mapKey).daily_infra_normal / resData.get(mapKey).daily_infra_tot)) * 100.0), 1));
                resData.get(mapKey).daily_develop_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((Double.isNaN((resData.get(mapKey).daily_develop_normal / resData.get(mapKey).daily_develop_tot)) ? 0 : (resData.get(mapKey).daily_develop_normal / resData.get(mapKey).daily_develop_tot)) * 100.0), 1));
                resData.get(mapKey).daily_solution_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((Double.isNaN((resData.get(mapKey).daily_solution_normal / resData.get(mapKey).daily_solution_tot)) ? 0 : (resData.get(mapKey).daily_solution_normal / resData.get(mapKey).daily_solution_tot)) * 100.0), 1));
                resData.get(mapKey).daily_tot_percent = Double.parseDouble(CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.FLOOR, ((Double.isNaN(((resData.get(mapKey).daily_center_normal + resData.get(mapKey).daily_infra_normal + resData.get(mapKey).daily_develop_normal + resData.get(mapKey).daily_solution_normal) /
                        (resData.get(mapKey).daily_center_tot + resData.get(mapKey).daily_infra_tot + resData.get(mapKey).daily_develop_tot + resData.get(mapKey).daily_solution_tot))) ? 0 : ((resData.get(mapKey).daily_center_normal + resData.get(mapKey).daily_infra_normal + resData.get(mapKey).daily_develop_normal + resData.get(mapKey).daily_solution_normal) /
                        (resData.get(mapKey).daily_center_tot + resData.get(mapKey).daily_infra_tot + resData.get(mapKey).daily_develop_tot + resData.get(mapKey).daily_solution_tot))) * 100.0), 1));
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return new ResultJQGridVO(1, resData.size(), 1, new ArrayList(resData.values()));
    }

    // 운영지수 가져오기
    @RequestMapping(value = "/getOpIndexData", method = RequestMethod.POST)
    @ResponseBody
    public DashBoardSlaVO getOpIndexData(){

        DashBoardSlaVO dashBoardSlaVO;

        try {

            dashBoardSlaVO = getDao().getOpIndexData();

            if(dashBoardSlaVO == null) {

                dashBoardSlaVO = new DashBoardSlaVO();

                Calendar cal = Calendar.getInstance();

                SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");

                String format_time = format.format(cal.getTime());

                dashBoardSlaVO.operateIndex = -1;
                dashBoardSlaVO.brief_dt = format_time;
            }

            return dashBoardSlaVO;
        }catch (Exception ex) {

            return null;
        }
    }
}
