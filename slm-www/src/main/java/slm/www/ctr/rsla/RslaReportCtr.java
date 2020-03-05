package slm.www.ctr.rsla;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.CmnDataDao;
import module.etc.CmnEtcBiz;
import module.lib.com.udojava.evalex.Expression;
import module.object.CmnDateBiz;
import module.object.CmnListBiz;
import module.object.CmnMathBiz;
import module.object.CmnSLAMathBiz;
import module.secure.encryption.CmnRsaOaepBiz;
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
import slm.www.dao.rsla.SlaReportDao;
import slm.www.vo.rsla.SlaReportBandWidthVO;
import slm.www.vo.rsla.SlaReportEvalVO;
import slm.www.vo.rsla.SlaReportSummaryVO;
import slm.www.vo.rsla.SlaReportVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;
import java.util.regex.Pattern;

/**
 * <p/>
 * User: 현재호
 * Date: 2016.06.07
 * Time: 오후 4:19
 */
@Controller
@RequestMapping("/rsla")
public class RslaReportCtr {

    private static final Logger log = LoggerFactory.getLogger(RslaReportCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S4A001";

    /**
     * SLA 보고서 관리에서 사용하는 DAO
     *
     * @return SlaReportDao
     */
    public SlaReportDao getDao() {

        return sqlSession.getMapper(SlaReportDao.class);
    }

    /**
     * 공통으로 사용하는 DAO
     *
     * @return CmnDataDao
     */
    public CmnDataDao getCmnDao() {

        return sqlSession.getMapper(CmnDataDao.class);
    }

    /**
     * SLA 보고서 화면 처음 접근하는 Ctr
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

            // 사용자 접속 로그 Insert
            etcBiz.getMenuDao().setUserLogInsert(id, mnu_cd);

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
     * SLA 보고서 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getReportList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getReportList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaReportVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcSDate = null; // 시작일
            String srcEDate = null; // 종료일

            ObjectMapper mapper = new ObjectMapper();

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcSDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcSDate"));
                srcEDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcEDate"));
            }

            // 점검현황 개수 가져오기
            int dataCnt = getDao().getSLAReportListCnt(srcSDate, srcEDate, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getSLAReportList(srcSDate, srcEDate, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaReportVO>() {
            });
        }
    }

    /**
     * SLA 보고서 상세정보 가져오기
     *
     * @param obj {mem_seq:사용자 고유 SEQ}
     * @param ses HttpSession
     * @return UserVO
     */
    @RequestMapping(value = "/getReportData", method = RequestMethod.POST)
    @ResponseBody
    public SlaReportVO getReportData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String sla_seq = "";

            if (obj.containsKey("sla_seq")) {

                if (!StringUtils.isEmpty(obj.get("sla_seq").toString())) {

                    sla_seq = CmnFilterBiz.filterSqlString(obj.get("sla_seq").toString()).toUpperCase();
                }
            }

            return getDao().getSLAReportData(CmnFilterBiz.filterPureString(sla_seq));
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * SLA 서비스 평가결과 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getResultList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getResultList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaReportEvalVO>() {
            });
        }

        // Key Sorting을 하기 위해 treemap 사용
        Map<String, SlaReportEvalVO> resData = new TreeMap<String, SlaReportEvalVO>();

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            Long slaSeq = 0L; // SLA 보고서 고유번호

            ObjectMapper mapper = new ObjectMapper();

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                if (jsonFilter.get("sla_seq") != null) {

                    slaSeq = Long.valueOf(CmnFilterBiz.filterPureString(jsonFilter.get("sla_seq")));
                }
            }

            // 1. SLA 항목별 평가기준 가져오기
            for (SlaReportEvalVO obj : getDao().getSLAReportEvalList()) {

                if (obj.weight != null) {

                    obj.weight = Integer.valueOf(obj.weight.toString());
                }

                resData.put(obj.rpt_cd, obj);
            }

            // 2. SLA 항목별 평가결과 가져오기
            SlaReportEvalVO tmpVO = null;
            for (SlaReportSummaryVO obj : getDao().getSLAReportSummaryList(slaSeq)) {

                if (resData.containsKey(obj.rpt_cd)) {

                    tmpVO = resData.get(obj.rpt_cd);

                    if (obj.mea_res != null) {

                        tmpVO.mea_res = obj.mea_res.concat(tmpVO.mea_res_unit);
                    }

                    if (obj.mea_point != null) {

                        tmpVO.mea_point = Float.valueOf(obj.mea_point.toString());
                    }
                }
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return new ResultJQGridVO(1, resData.size(), 1, new ArrayList(resData.values()));
    }

    /**
     * 대역폭 사용률 정보 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getBandWidthList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getBandWidthList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SlaReportBandWidthVO>() {
            });
        }

        // Key Sorting을 하기 위해 treemap 사용
        List<SlaReportBandWidthVO> resData = new ArrayList<SlaReportBandWidthVO>();

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            Long slaSeq = 0L; // SLA 보고서 고유번호

            ObjectMapper mapper = new ObjectMapper();

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                if (jsonFilter.get("sla_seq") != null) {

                    slaSeq = Long.valueOf(CmnFilterBiz.filterPureString(jsonFilter.get("sla_seq")));
                }
            }

            resData = getDao().getBandWidthList(slaSeq);

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return new ResultJQGridVO(1, resData.size(), 1, resData);
    }

    /**
     * 장비 기본정보 저장(Insert, Update)
     * - 장비 정보, 장비유형에 따른 기본항목
     *
     * @param vo  UserVO
     * @param ses HttpSession
     * @return 0 : 에러, 1 이상 : Insert/Update 성공 Count
     */
    @RequestMapping(value = "/setSLAReportAct", method = RequestMethod.POST)
    @ResponseBody
    public SlaReportVO setSLAReportAct(@RequestBody SlaReportVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setSLAReportAct-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // 1. 필터링
            CmnFilterBiz.filterSqlClass(vo);

            vo.isState = 0;

            vo.brief_dt = CmnFilterBiz.filterPureString(vo.brief_dt); // 보고 일
            vo.start_dt = CmnFilterBiz.filterPureString(vo.start_dt); // 시작 일
            vo.end_dt = CmnFilterBiz.filterPureString(vo.end_dt); // 종료 일

            if (StringUtils.isEmpty(vo.remove_dt) == false) {

                vo.remove_dt = CmnFilterBiz.filterString(Pattern.compile("[-]"), vo.remove_dt); // 제외 일
            }

            // 2. 보고서 고유번호로 구분하여 SLM_SLA_REPORT Insert/Update 처리
            if (vo.sla_seq > 0) {

                getDao().setSLAReportUpdate(vo); // 2-1. SLM_SLA_REPORT
            } else {

                vo.sla_seq = getCmnDao().getTableMaxSeq("SLM_SLA_REPORT", "sla_seq", 1);

                getDao().setSLAReportInsert(vo); // 2-1. SLM_SLA_REPORT
                getDao().setSLAReportSummaryInsert(vo.sla_seq, getCmnDao().getTableMaxSeq("SLM_SLA_REPORTSUM", "summ_seq", 0), "0103000000"); // 2-2. SLM_SLA_REPORTSUM
            }

            getDao().setSLAReportBackupSuccessRateMergeAct(vo); // 2-6. SLM_SLA_BACKSUCRATE

            // 2-7. SLM_SLA_BANDWIDTH
            long firstSeq = getCmnDao().getTableMaxSeq("SLM_SLA_BANDWIDTH", "bw_seq", 1);

            for (SlaReportBandWidthVO obj : vo.bandWidthData) {

                if (obj.use_rate != null) {

                    obj.use_rate = CmnFilterBiz.filterSqlString(obj.use_rate); // 필터링
                }

                if (obj.sla_seq == 0) {

                    obj.sla_seq = vo.sla_seq;
                }

                getDao().setSLAReportBandWidthMergeAct(obj, firstSeq++);
            }

            // 2-3. 예비품 현황 기간별 수량,보관장소,비고 생성
            // 2-3-1. 날짜 배열 설정(시작일 ~ 종료일, 제외일 제거 하고)
            Calendar sDate = Calendar.getInstance();
            Calendar eDate = Calendar.getInstance();

            sDate.setTime(CmnDateBiz.convertDate(vo.start_dt, "yyyyMMdd", 0, Calendar.DAY_OF_MONTH));
            eDate.setTime(CmnDateBiz.convertDate(vo.end_dt, "yyyyMMdd", 0, Calendar.DAY_OF_MONTH));

            String tmpDate = null;
            List<String> arrDate = new ArrayList<String>();
            List<String> removeDate = new ArrayList<String>();

            if (StringUtils.isEmpty(vo.remove_dt) == false) {

                removeDate = Arrays.asList(vo.remove_dt.split(","));
            }

            while (sDate.compareTo(eDate) != 1) {

                tmpDate = CmnDateBiz.convertString(sDate.getTime(), "yyyyMMdd");

                if (!removeDate.contains(tmpDate)) {

                    arrDate.add(tmpDate);
                }

                sDate.add(Calendar.DAY_OF_MONTH, 1);
            }

            getDao().setSLAReportEquipSpareDataMergeAct(vo.sla_seq, getCmnDao().getTableMaxSeq("SLM_EQUIPSPARE_DATA", "spare_seq", 0), arrDate, Collections.max(arrDate)); // 2-3-2. SLM_EQUIPSPARE_DATA Merge Insert/Update

            sDate = null;
            eDate = null;

            // 2-4. SLA 항목별 평가기준 가져오기
            Map<String, SlaReportEvalVO> evalData = new TreeMap<String, SlaReportEvalVO>();
            for (SlaReportEvalVO obj : getDao().getSLAReportEvalList()) {

                if (obj.weight != null) {

                    obj.weight = Integer.valueOf(obj.weight.toString());
                }

                evalData.put(obj.rpt_cd, obj);
            }

            // 2-5. SLA 항목별 평가결과 가져오기
            List<String> arrStat = new ArrayList<String>() {
                {
                    add("exce");
                    add("good");
                    add("normal");
                    add("insuf");
                    add("bad");
                }
            };

            Map<String, Object> tmpMap = null;
            Expression exp = null;
            CmnListBiz listBiz = new CmnListBiz();
            CmnSLAMathBiz slaMathBiz = new CmnSLAMathBiz(sqlSession);
            int resPosit = 1; // 평가결과 자리수

            for (SlaReportSummaryVO obj : getDao().getSLAReportSummaryList(vo.sla_seq)) {

                Double tmpV = Double.NaN;

                if (evalData.containsKey(obj.rpt_cd)) {
                    // 2-5-1. 각 SLA 유형에 따른 계산 결과 로직 처리
                    resPosit = 1;

                    String itmCd = evalData.get(obj.rpt_cd).eval_cd;

                    switch (itmCd) {
                        case "0101":
                            // 장애 적기처리율
                            tmpV = slaMathBiz.calcDisOrderMngRate(itmCd, arrDate);
                            break;
                        case "0102":
                            // 중복 장애건수
                            tmpV = slaMathBiz.calcDupDisOrderNum(itmCd, arrDate, 3);
                            resPosit = 0;
                            break;
                        case "0103":
                            // 백업성공률
                            tmpV = slaMathBiz.calcBackupSuccessRate(itmCd, vo.sla_seq);
                            break;
                        case "0104":
                            // 예비품 확보
                            //tmpV = slaMathBiz.calcSpareSecure(itmCd, vo.sla_seq);
                            break;
                        case "0105":
                            // 장애 규명율
                            tmpV = slaMathBiz.calcDisOrderExamRate(itmCd, arrDate);
                            break;
                        case "0201":
                            // 시스템 성능관리
                            tmpV = slaMathBiz.calcSysEfficiencyMng(itmCd, arrDate);
                            break;
                        case "0202":
                            // 대역폭 사용률
                            tmpV = slaMathBiz.calcBandWidthUseRate(itmCd, vo.sla_seq);
                            break;
                        case "0301":
                            // 서비스요청 적기 처리율
                            tmpV = slaMathBiz.calcServiceReqMngRate(itmCd, arrDate);
                            break;
                        case "0302":
                            // 보안사고 발생건수
                            tmpV = slaMathBiz.calcSecurityAccNum(itmCd, arrDate);
                            break;
                        case "0401":
                            // 현장점검 실시율
                            tmpV = slaMathBiz.calcSiteCheckRate(itmCd, arrDate);
                            break;
                        case "0402":
                            // 버전관리
                            //tmpV = slaMathBiz.calcVersionMng(itmCd, arrDate);
                            break;
                    }

                    // 2-5-2. 위에 결과 값이 존재 할 경우 SLM_SLA_REPORTSUM Update 처리
                    if (tmpV.isNaN() == false) {

                        tmpMap = etcBiz.convertObjectToMap(evalData.get(obj.rpt_cd)); // VO 객체 Map으로 컨버팅

                        for (String ke : arrStat) {

                            if (tmpMap.get("score_".concat(ke)) != null) {

                                exp = new Expression(CmnFilterBiz.filterBlockRemoveString(tmpMap.get("score_".concat(ke)).toString()));

                                for (String itm : listBiz.getUniqueList(CmnFilterBiz.filterBlockList(tmpMap.get("score_".concat(ke)).toString()))) {

                                    exp.with(itm, new BigDecimal(tmpV));
                                }

                                if (exp.eval().toString().equals("1")) {

                                    obj.mea_res = CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.ROUND, tmpV, resPosit);
                                    obj.mea_point = CmnMathBiz.calcMath(CmnMathBiz.calcMathOpt.ROUND, ((Number) tmpMap.get("weight")).doubleValue() * ((Number) tmpMap.get("point_".concat(ke))).doubleValue(), 1);

                                    if (slaMathBiz.calcResult.containsKey(itmCd) == true) {

                                        if (slaMathBiz.calcResult.get(itmCd).arith_expression_nm != null) {

                                            obj.arith_expression_nm = slaMathBiz.calcResult.get(itmCd).arith_expression_nm;
                                        }

                                        if (slaMathBiz.calcResult.get(itmCd).mea_cont != null) {

                                            obj.mea_cont = slaMathBiz.calcResult.get(itmCd).mea_cont;
                                        }
                                    }

                                    getDao().setSLAReportSummaryUpdate(obj);
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            vo.isState = 1;

            transactionManager.commit(status);
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            transactionManager.rollback(status);
        }

        return (vo.isState == 1 ? vo : null);
    }

    /**
     * SLA 보고서 삭제(Delete)
     *
     * @param reqVO DaReportVO
     * @param ses   HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setSLAReportDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setSLAReportDel(@RequestBody SlaReportVO reqVO, HttpSession ses) {

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

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setSLAReportDel-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {

            getDao().setSLAReportEquipSpareDataDelete(reqVO.sla_seq);
            getDao().setSLAReportSumDelete(reqVO.sla_seq);
            getDao().setSLAReportDelete(reqVO.sla_seq);
            getDao().setSLAReportBackupSuccessRateDelete(reqVO.sla_seq);
            getDao().setSLAReportBandWidthDelete(reqVO.sla_seq);

            tmpOK = true;

            transactionManager.commit(status);
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            transactionManager.rollback(status);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
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

            secureCd = CmnRsaOaepBiz.encrypt(reqData.get("sla_seq").toString());

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        Map<String, String> map = new HashMap<String, String>();

        map.put("key", secureCd);

        return map;
    }
}
