package slm.www.ctr.cmn.report;

import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import module.secure.encryption.CmnRsaOaepBiz;
import slm.www.dao.rday.InspectDao;
import slm.www.dao.rday.TroubleShootDao;
import slm.www.dao.rmon.ReportDao;
import slm.www.dao.rsla.SlaReportDao;
import slm.www.vo.rday.DaReportVO;
import slm.www.vo.rmon.ReportVO;
import slm.www.vo.rsla.SlaReportVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * oz Report 관련 컨트롤
 * <p/>
 * User: 현재호
 * Date: 2016.05.31
 * Time: 오후 6:03
 */
@Controller
@RequestMapping("/report")
public class OzReportCtr {

    private static final Logger log = LoggerFactory.getLogger(OzReportCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private String ozUrl = ResourceBundle.getBundle("config").getString("report.oz.url");

    /**
     * 일일점검현황에서 사용하는 DAO
     *
     * @return InspectDao
     */
    public InspectDao getInspectDao() {
        return sqlSession.getMapper(InspectDao.class);
    }

    /**
     * 월간점검현황에서 사용하는 DAO
     *
     * @return ReportDao
     */
    public ReportDao getReportDao() {
        return sqlSession.getMapper(ReportDao.class);
    }

    /**
     * 장애처리에서 사용하는 DAO
     *
     * @return ReportDao
     */
    public TroubleShootDao getTroubleShootDao() {
        return sqlSession.getMapper(TroubleShootDao.class);
    }

    /**
     * SLA 보고서 관리에서 사용하는 DAO
     *
     * @return SlaReportDao
     */
    public SlaReportDao getSlaReportDao() {

        return sqlSession.getMapper(SlaReportDao.class);
    }

    /**
     * 일일점검현황 보고서 미리보기
     * <p/>
     *
     * @param req HttpServletRequest
     * @param ses HttpSession
     * @return ModelAndView
     */
    @RequestMapping(value = "/ozReportDayPreview", method = RequestMethod.GET)
    public ModelAndView printReport(HttpServletRequest req, HttpSession ses) {

        ModelAndView mv = new ModelAndView();

        try {
            // 1. 검색 조건 가져오기
            Map<String, String> param = new HashMap<String, String>();

            for (Map.Entry<String, String[]> obj : ((Map<String, String[]>) req.getParameterMap()).entrySet()) {

                param.put(obj.getKey(), obj.getValue()[0]);
            }

            if (param.containsKey("key")) {
                // 암호화 된 보고서 일련번호 복호화
                final String daSeq = CmnRsaOaepBiz.decrypt(param.get("key"));

                // 보고서 정보 가져오기(보고서 파일명 찾기위해)
                final DaReportVO resData = getInspectDao().getReportData(Long.valueOf(daSeq));

                List<String> retData = new ArrayList<String>() {
                    {
                        add("da_seq=".concat(daSeq));
                    }
                };

                mv.addObject("p", req.getContextPath()); // PATH 가져오기
                mv.addObject("ozReportUrl", ozUrl); // oz 리포트 서버 경로
                mv.addObject("ozReportname", resData.rpt_file); // 보고서 파일 가져오기
                mv.addObject("ozOdinames", "report_day"); // ODI 명
                mv.addObject("ozPgcount", retData.size()); // 파라미터 총 개수
                mv.addObject("ozArgs", retData); // 파라미터
                mv.addObject("isSuccess", true); // 성공여부
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            mv.addObject("isSuccess", false); // 성공여부
        }

        return mv;
    }

    /**
     * 월간점검현황 보고서 미리보기
     * <p/>
     *
     * @param req HttpServletRequest
     * @param ses HttpSession
     * @return ModelAndView
     */
    @RequestMapping(value = "/ozReportMonPreview", method = RequestMethod.GET)
    public ModelAndView printMonReport(HttpServletRequest req, HttpSession ses) {

        ModelAndView mv = new ModelAndView();

        try {
            // 1. 검색 조건 가져오기
            Map<String, String> param = new HashMap<String, String>();

            for (Map.Entry<String, String[]> obj : ((Map<String, String[]>) req.getParameterMap()).entrySet()) {

                param.put(obj.getKey(), obj.getValue()[0]);
            }

            if (param.containsKey("key")) {
                // 암호화 된 보고서 일련번호 복호화
                final String daSeq = CmnRsaOaepBiz.decrypt(param.get("key"));

                // 보고서 정보 가져오기(보고서 파일명 찾기위해)
                final ReportVO resData = getReportDao().getRmonReportData(daSeq);

                List<String> retData = new ArrayList<String>() {
                    {
                        add("da_seq=".concat(daSeq));
                    }
                };

                mv.addObject("p", req.getContextPath()); // PATH 가져오기
                mv.addObject("ozReportUrl", ozUrl); // oz 리포트 서버 경로
                mv.addObject("ozReportname", resData.rpt_file); // 보고서 파일 가져오기
                mv.addObject("ozOdinames", "report_mon"); // ODI 명
                mv.addObject("ozPgcount", retData.size()); // 파라미터 총 개수
                mv.addObject("ozArgs", retData); // 파라미터
                mv.addObject("isSuccess", true); // 성공여부
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            mv.addObject("isSuccess", false); // 성공여부
        }

        return mv;
    }

    /**
     * 장애처리 보고서 미리보기
     * <p/>
     *
     * @param req HttpServletRequest
     * @param ses HttpSession
     * @return ModelAndView
     */
    @RequestMapping(value = "/ozReportDisOrderPreview", method = RequestMethod.GET)
    public ModelAndView printDisOrderReport(HttpServletRequest req, HttpSession ses) {

        ModelAndView mv = new ModelAndView();

        try {
            // 1. 검색 조건 가져오기
            Map<String, String> param = new HashMap<String, String>();

            for (Map.Entry<String, String[]> obj : ((Map<String, String[]>) req.getParameterMap()).entrySet()) {

                param.put(obj.getKey(), obj.getValue()[0]);
            }

            if (param.containsKey("key")) {
                // 암호화 된 보고서 일련번호 복호화
                final String disSeq = CmnRsaOaepBiz.decrypt(param.get("key"));

                // 보고서 정보 가져오기(보고서 파일명 찾기위해)
                final String rpt_file = getTroubleShootDao().getRdayTroubleShootReportFileNm(disSeq);

                List<String> retData = new ArrayList<String>() {
                    {
                        add("dis_seq=".concat(disSeq));
                    }
                };

                mv.addObject("p", req.getContextPath()); // PATH 가져오기
                mv.addObject("ozReportUrl", ozUrl); // oz 리포트 서버 경로
                mv.addObject("ozReportname", rpt_file); // 보고서 파일 가져오기
                mv.addObject("ozOdinames", "report_err"); // ODI 명
                mv.addObject("ozPgcount", retData.size()); // 파라미터 총 개수
                mv.addObject("ozArgs", retData); // 파라미터
                mv.addObject("isSuccess", true); // 성공여부
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            mv.addObject("isSuccess", false); // 성공여부
        }

        return mv;
    }

    /**
     * SLA 평가보고서 미리보기
     * <p/>
     *
     * @param req HttpServletRequest
     * @param ses HttpSession
     * @return ModelAndView
     */
    @RequestMapping(value = "/ozReportSLAPreview", method = RequestMethod.GET)
    public ModelAndView printSLAReport(HttpServletRequest req, HttpSession ses) {

        ModelAndView mv = new ModelAndView();

        try {
            // 1. 검색 조건 가져오기
            Map<String, String> param = new HashMap<String, String>();

            for (Map.Entry<String, String[]> obj : ((Map<String, String[]>) req.getParameterMap()).entrySet()) {

                param.put(obj.getKey(), obj.getValue()[0]);
            }

            if (param.containsKey("key")) {
                // 암호화 된 보고서 일련번호 복호화
                final String slaSeq = CmnRsaOaepBiz.decrypt(param.get("key"));

                // 보고서 정보 가져오기(보고서 파일명 찾기위해)
                final SlaReportVO resData = getSlaReportDao().getSLAReportData(slaSeq);

                List<String> retData = new ArrayList<String>() {
                    {
                        add("sla_seq=".concat(slaSeq));
                    }
                };

                mv.addObject("p", req.getContextPath()); // PATH 가져오기
                mv.addObject("ozReportUrl", ozUrl); // oz 리포트 서버 경로
                mv.addObject("ozReportname", resData.rpt_file); // 보고서 파일 가져오기
                mv.addObject("ozOdinames", "report_sla"); // ODI 명
                mv.addObject("ozPgcount", retData.size()); // 파라미터 총 개수
                mv.addObject("ozArgs", retData); // 파라미터
                mv.addObject("isSuccess", true); // 성공여부
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            mv.addObject("isSuccess", false); // 성공여부
        }

        return mv;
    }
}
