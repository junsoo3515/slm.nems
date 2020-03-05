package slm.www.ctr.rday;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.CmnDataDao;
import module.etc.CmnEtcBiz;
import module.excel.poi.CmnExcelBiz;
import module.lib.com.udojava.evalex.Expression;
import module.object.CmnDateBiz;
import module.object.CmnListBiz;
import module.secure.encryption.CmnRsaOaepBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.dm.EquipDao;
import slm.www.dao.rday.InspectDao;
import slm.www.vo.rday.DaReportSummaryVO;
import slm.www.vo.rday.DaReportVO;
import slm.www.vo.rday.EquipMainDataVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.*;

/**
 * 일일보고서 > 일일점검현황
 * <p/>
 * User: 현재호
 * Date: 2016.05.02
 * Time: 오후 4:00
 */
@Controller
@RequestMapping("/rday")
public class RdayInspectCtr {

    private static final Logger log = LoggerFactory.getLogger(RdayInspectCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S2D001";

    /**
     * 일일점검현황에서 사용하는 DAO
     *
     * @return InspectDao
     */
    public InspectDao getDao() {
        return sqlSession.getMapper(InspectDao.class);
    }

    /**
     * 장비관리에서 사용하는 DAO
     *
     * @return EquipDao
     */
    public EquipDao getEquipDao() {
        return sqlSession.getMapper(EquipDao.class);
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
     * 일일점검현황 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/inspect", method = RequestMethod.GET)
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
     * 장비 목록 jqGrid 호출 Ctr
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

            return new ResultJQGridVO(1, 0, 1, new ArrayList<DaReportVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String gg = null; // 보고서 구분 코드
            String srcSDate = null; // 시작일
            String srcEDate = null; // 종료일

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                gg = CmnFilterBiz.filterSqlString(jsonFilter.get("gg"));
                srcSDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcSDate"));
                srcEDate = CmnFilterBiz.filterPureString(jsonFilter.get("srcEDate"));
            }

            // 점검현황 개수 가져오기
            int dataCnt = getDao().getReportListCnt(gg, srcSDate, srcEDate, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getReportList(gg, srcSDate, srcEDate, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<DaReportVO>() {
            });
        }
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
     * 점검현황 저장(Insert)
     *
     * @param id   고유 아이디
     * @param oper 상태(add, edit)
     * @param vo   DaReportVO
     * @param ses  HttpSession
     * @return Map{isSuccess : [0 : 실패, 1 : 성공, 2 : 중복날짜 존재]}
     */
    @RequestMapping(value = "/setReportAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportAct(String id, String oper, DaReportVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        int tmpOK = 0;

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setReportAct-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // 1. 필터링(filterSqlClass 은 setter가 있으면 안됨)
            vo.rpt_gubun_cd = "DAY";

            // 1.1. 일일보고의 날짜를 시작일, 종료일 setting
            vo.start_dt = vo.brief_dt;
            vo.end_dt = vo.brief_dt;

            switch (oper) {
                case "add":
                    // 추가 모드
                    // 1. 기존 보고서 중복체크
                    if (getDao().getReportChk(vo.rpt_gubun_cd, vo.brief_dt) > 0) {

                        tmpOK = 2;
                    } else {
                        // 2-1. 보고서 신규 고유번호 가져오기(SLM_DA_REPORT)
                        vo.da_seq = getCmnDao().getTableMaxSeq("SLM_DA_REPORT", "da_seq", 1);

                        getDao().setReportInsert(vo); // 2-2. SLM_DA_REPORT
                        getDao().setReportMapInsert(vo.da_seq); // 2-3. SLM_DA_REPORTMAP

                        vo.start_dt = CmnDateBiz.convertString(CmnDateBiz.setMoveDay(CmnDateBiz.convertDate(vo.start_dt, "yyyyMMdd", 0, Calendar.DATE), 1, -2), "yyyyMMdd"); // 월요일이면 장애처리를 전주 토요일부터 월요일까지 계산

                        getDao().setReportSummaryInsert(vo.da_seq, getCmnDao().getTableMaxSeq("SLM_DA_REPORTSUM", "summ_seq", 0), "0101000000", vo.start_dt, vo.end_dt); // 2-4. SLM_DA_REPORTSUM
                        getDao().setReportEquipDataInsert(vo.da_seq, getCmnDao().getTableMaxSeq("SLM_EQUIPMAIN_DATA", "data_seq", 0), vo.brief_dt); // 2-5. SLM_EQUIPMAIN_DATA

                        // 데이터 복사 여부에 따른 값 Setting
                        if (vo.data_copy_fl.equals("Y")) {

                            getDao().setReportEquipDataCopyMergeUpdate(vo.da_seq, getDao().getReportLastFinSeq(vo.brief_dt)); // 현재 보고일보다 작은 보고일의 최대 고유번호 가져오기
                        } else {

                            getDao().setReportEquipDataMergeUpdate(vo.da_seq); // 2-6. SLM_EQUIPMAIN_DATA 장비그룹 별 기본항목 값 Update
                        }

                        getDao().setReportEquipDataIssueMergeUpdate(vo.da_seq, vo.start_dt, vo.end_dt); // 2-7. 장애처리에 있는 요약 항목 'issue' 기본 값 일괄 Update
                        getDao().setReportEquipDataLinkSystemMergeUpdate(vo.da_seq, vo.brief_dt); // 2-8. 외부시스템 연동 데이터 일괄 Update
                        getDao().setReportEquipDataEtcMergeUpdate(vo.da_seq); // 2-9. 나머지 eng_nm = 'name'인 항목 장비 명칭 일괄 Update

                        getDao().setReportSoftWareDataInsert(vo.da_seq, getCmnDao().getTableMaxSeq("SLM_SWMAIN_DATA", "data_seq", 0), vo.brief_dt); // 2-10. SLM_SWMAIN_DATA

                        tmpOK = 1;
                    }

                    break;
                case "edit":
                    // 수정 모드
                    getDao().setReportUpdate(vo); // 2-2. SLM_DA_REPORT

                    tmpOK = 1;
                    break;
            }

            transactionManager.commit(status);
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            transactionManager.rollback(status);
        }

        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 점검현황 삭제(Delete)
     *
     * @param reqVO DaReportVO
     * @param ses   HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setReportDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setReportDel(@RequestBody DaReportVO reqVO, HttpSession ses) {

        String[] arr = {"D"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        int tmpOK = 0;

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setReportDel-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {

            getDao().setReportSoftWareDataDelete(reqVO.da_seq);
            getDao().setReportEquipDataDelete(reqVO.da_seq);
            getDao().setReportSummaryDelete(reqVO.da_seq);
            getDao().setReportMapDelete(reqVO.da_seq);
            getDao().setReportDelete(reqVO.da_seq);

            tmpOK = 1;

            transactionManager.commit(status);
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            transactionManager.rollback(status);
        }

        Map<String, Integer> map = new HashMap<String, Integer>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 점검요약정보 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getSummaryList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getSummaryList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

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

        Long selDaSeq = null; // 보고서 고유번호


        try {
            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {});

                selDaSeq = Long.valueOf(CmnFilterBiz.filterPureString(jsonFilter.get("da_seq")));
            }
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<DaReportSummaryVO>() {
            });
        }

        // Key Sorting을 하기 위해 treemap 사용
        Map<String, DaReportSummaryVO> resData = new TreeMap<String, DaReportSummaryVO>();

        CmnListBiz listBiz = new CmnListBiz();
        Expression exp = null;

        try {
            // 1. 점검요약정보 가져오기
            for (DaReportSummaryVO obj : getDao().getReportSummaryList(selDaSeq)) {

                resData.put(obj.rpt_cd, obj);
            }

            // 2. 항목기준 가져오기
            Map<String, String> evalData = new HashMap<String, String>();

            for (final Map<String, String> obj : getDao().getReportSummaryItmExprList(null)) {

                if (obj.get("ENG_NM") != null && obj.get("RPT_CD") != null) {

                    if (!evalData.containsKey(obj.get("ENG_NM").concat(obj.get("RPT_CD")))) {

                        evalData.put(obj.get("ENG_NM").concat(obj.get("RPT_CD")), obj.get("WRONG_EXPR"));
                    }
                }
            }

            // 3. 장비별 항목현황 가져오기(int, select) :: S/W는 장비별 세부정보에 나오는 항목 수량이 완전 틀림(장비에 해당하는 점검항목이 표시되기 때문에)
            String key, evalKey;
            DaReportSummaryVO tmpObj;
            for (Map<String, String> obj : getDao().getReportSummaryItmList(selDaSeq)) {

                key = obj.get("RPT_CD");

                if (resData.containsKey(key)) {

                    tmpObj = resData.get(key);

                    tmpObj.itm_all++;

                    if (StringUtils.isEmpty(obj.get("INP_VAL")) == false) {

                        switch (obj.get("TOPIC_TYPE")) {
                            case "select":
                                // select 타입의 N이면 이상
                                if (StringUtils.equals(obj.get("INP_VAL"), "N")) {

                                    tmpObj.itm_abnormal++;
                                }
                                break;
                            case "int":
                                // int 타입의 이상범위에 포함 될 경우
                                evalKey = obj.get("ENG_NM").concat(obj.get("RPT_CD"));

                                if (evalData.containsKey(evalKey)) {

                                    exp = new Expression(CmnFilterBiz.filterBlockRemoveString(evalData.get(evalKey)));

                                    for (String itm : listBiz.getUniqueList(CmnFilterBiz.filterBlockList(evalData.get(evalKey)))) {

                                        exp.with(itm, new BigDecimal(obj.get("INP_VAL")));
                                    }

                                    if (exp.eval().toString().equals("1")) {

                                        tmpObj.itm_abnormal++;
                                    }
                                }
                                break;
                            default:
                                break;
                        }
                    }
                }
            }

            // 4. 정상 항목 적용
            for (String mapKey : resData.keySet()) {

                resData.get(mapKey).itm_normal = resData.get(mapKey).itm_all - resData.get(mapKey).itm_abnormal;
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        } finally {

            listBiz = null;
            exp = null;
        }

        return new ResultJQGridVO(1, resData.size(), 1, new ArrayList(resData.values()));
    }

    /**
     * 점검요약정보 저장(Update)
     *
     * @param vo  DaReportVO
     * @param ses HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setSummaryAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setSummaryAct(@RequestBody DaReportVO vo, HttpSession ses) {

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

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setSummaryAct-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            for (DaReportSummaryVO obj : vo.summData) {

                getDao().setReportSummaryDataUpdate(vo.da_seq, obj);
            }

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
     * 점검요약정보 동기화(Update)
     *
     * @param reqData da_seq : 보고서 고유번호
     * @param ses     HttpSession
     * @return Map {isSuccess : [true : 성공, false : 실패] }
     */
    @RequestMapping(value = "/setSummarySync", method = RequestMethod.POST)
    @ResponseBody
    public Map setSummarySync(@RequestBody Map<String, String> reqData, HttpSession ses) {

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

        String key = reqData.get("da_seq");

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setSummarySync-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {
            // 전체 동기화 시 성능 문제때문에 아래와 같이 처리
            getDao().setReportSummaryDataSyncUpdate(key); // 1. 점검요약정보 데이터 동기화(항목현황 데이터는 초기화)

            getDao().setReportSummaryDataSyncItmCntEqpUpdate(key); // 2-1. 장비_전체항목 현황, select 값이 'N'으로 적용 된 값들 항목현황 데이터 갱신
            getDao().setReportSummaryDataSyncItmCntSwUpdate(key); // 2-2. SW_전체항목 현황, select 값이 'N'으로 적용 된 값들 항목현황 데이터 갱신

            // 3. 항목기준 가져오기
            List<String> arrRptCd = new ArrayList<String>();
            List<String> arrEngNm = new ArrayList<String>();
            Map<String, String> evalData = new HashMap<String, String>();

            for (final Map<String, String> obj : getDao().getReportSummaryItmExprList(null)) {

                if (obj.get("ENG_NM") != null && obj.get("RPT_CD") != null) {

                    if (!arrRptCd.contains(obj.get("RPT_CD"))) {

                        arrRptCd.add(obj.get("RPT_CD"));
                    }

                    if (!arrEngNm.contains(obj.get("ENG_NM"))) {

                        arrEngNm.add(obj.get("ENG_NM"));
                    }

                    if (!evalData.containsKey(obj.get("ENG_NM").concat(obj.get("RPT_CD")))) {

                        evalData.put(obj.get("ENG_NM").concat(obj.get("RPT_CD")), obj.get("WRONG_EXPR"));
                    }
                }
            }

            // 4. 항목현황 가져오기(int)
            CmnListBiz listBiz = new CmnListBiz();
            Expression exp = null;
            Map<String, Map<String, Object>> resData = new HashMap<String, Map<String, Object>>(); // 이상 항목 현황 결과
            String tmpKey, evalKey;
            int tmpCnt = 0;

            for (final Map<String, String> obj : getDao().getReportSummaryItmAllList(key, arrRptCd, arrEngNm)) {

                tmpKey = String.valueOf(obj.get("DA_SEQ")).concat(obj.get("RPT_CD"));
                evalKey = obj.get("ENG_NM").concat(obj.get("RPT_CD"));

                if (evalData.containsKey(evalKey)) {

                    exp = new Expression(CmnFilterBiz.filterBlockRemoveString(evalData.get(evalKey)));

                    for (String itm : listBiz.getUniqueList(CmnFilterBiz.filterBlockList(evalData.get(evalKey)))) {

                        exp.with(itm, new BigDecimal(obj.get("INP_VAL")));
                    }

                    if (exp.eval().toString().equals("1")) {

                        if (!resData.containsKey(tmpKey)) {

                            resData.put(tmpKey, new HashMap<String, Object>() {
                                {
                                    put("da_seq", String.valueOf(obj.get("DA_SEQ")));
                                    put("rpt_cd", String.valueOf(obj.get("RPT_CD")));
                                    put("cnt", 1);
                                }
                            });
                        } else {

                            resData.get(tmpKey).put("cnt", Integer.valueOf(resData.get(tmpKey).get("cnt").toString()) + 1);
                        }
                    }
                }
            }

            // 5. 이상 항목 결과에 따른 점검요약정보 데이터 갱신
            for (Map<String, Object> tmpData : resData.values()) {

                getDao().setReportSummaryDataAbnormalItmUpdate(tmpData);
            }

            arrRptCd = null;
            arrEngNm = null;
            exp = null;
            listBiz = null;
            resData = null;

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
     * 점검요약정보의 비정상 항목현황 Update
     * <p/>
     *
     * @param reqData (da_seq : 보고서 고유 일련번호, summ_seq : 요약 교유번호, rpt_cd : 보고서 코드, cnt : 항목 이상 수량)
     * @param ses     HttpSession
     * @return Map {isSuccess : [true : 성공, false : 실패] }
     */
    @RequestMapping(value = "/setSummaryItmUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Map setSummaryItmUpdate(@RequestBody Map<String, String> reqData, HttpSession ses) {

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

            getDao().setReportSummaryDataAbnormalDirectUpdate(Long.valueOf(reqData.get("summ_seq").toString()), Long.valueOf(reqData.get("da_seq").toString()), reqData.get("rpt_cd"), Long.valueOf(reqData.get("cnt").toString()));

            tmpOK = true;

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 보고서별 기본항목 가져오기
     * <p/>
     * SLM_REPORT_TOPIC List<Map<String, String>> 가져오기
     *
     * @param reqData rptCD : 보고서 코드
     * @return List report topic list
     */
    @RequestMapping(value = "/getReportTopicList", method = RequestMethod.POST)
    @ResponseBody
    public List<Map<String, String>> getReportTopicList(@RequestBody Map<String, String> reqData) {

        try {

            return getDao().getReportTopicList(reqData.get("rptCD").toString());

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return new ArrayList<Map<String, String>>();
        }
    }

    /**
     * 장비별 세부정보 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getDetailList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getDetailList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipMainDataVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            int dataCnt = 0;
            Long daSeq = 0L; // 보고서 고유번호
            String rptCd = null; // 보고서 코드
            String swTypeFl = "N";

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                daSeq = Long.valueOf(CmnFilterBiz.filterPureString(jsonFilter.get("da_seq")));
                rptCd = CmnFilterBiz.filterSqlString(jsonFilter.get("rpt_cd"));
                dataCnt = Integer.valueOf(CmnFilterBiz.filterPureString(jsonFilter.get("cnt")));
                swTypeFl = CmnFilterBiz.filterSqlString(jsonFilter.get("sw_type_fl"));
            }

            // 1. 항목기준 가져오기
            Map<String, String> evalData = new HashMap<String, String>();

            for (Map<String, String> obj : getDao().getReportSummaryItmExprList(rptCd)) {

                if (!evalData.containsKey(obj.get("ENG_NM"))) {

                    evalData.put(obj.get("ENG_NM"), obj.get("WRONG_EXPR"));
                }
            }

            // 2. 보고서 유형 별 헤더 및 칼럼 명 설정
            Map<String, Map<String, String>> arrHeaderKey = new TreeMap<String, Map<String, String>>();

            for (final Map<String, String> obj : getDao().getReportTopicList(rptCd)) {

                arrHeaderKey.put(obj.get("TOPIC_CD"), new HashMap<String, String>() {
                    {
                        put("topic_type", obj.get("TOPIC_TYPE"));
                        put("eng_nm", obj.get("ENG_NM"));
                    }
                });
            }

            // 3. 갯수 기준 페이지 계산
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows);

            // 4. 장비별 세부정보 데이터 가져오기(설정 되어 있는 S/W유형 값에 따라 다른 테이블 쿼리)
            CmnListBiz listBiz = new CmnListBiz();
            Expression exp = null;
            List<Map<String, Object>> resData;

            switch (swTypeFl) {
                case "Y":
                    // S/W
                    resData = getDao().getSWListReport(daSeq, rptCd, "col", new ArrayList<String>(arrHeaderKey.keySet()), vo);
                    break;
                default:
                    // 장비
                    resData = getEquipDao().getEquipListReport(daSeq, rptCd, "col", new ArrayList<String>(arrHeaderKey.keySet()), vo);
                    break;
            }

            String evalKey = "";
            String inpVal = "";
            List<String> arrAbnomarCol = null;
            Map<String, Object> tmpObj = null;
            for (Map<String, Object> obj : resData) {

                arrAbnomarCol = new ArrayList<String>();
                tmpObj = new HashMap<String, Object>(obj);

                for (String key : arrHeaderKey.keySet()) {

                    if (obj.get("COL".concat(key)) != null) {

                        inpVal = obj.get("COL".concat(key)).toString();

                        if (StringUtils.isEmpty(inpVal) == false) {

                            switch (arrHeaderKey.get(key).get("topic_type")) {
                                case "select":
                                    // select 타입의 N이면 이상
                                    if (StringUtils.equals(inpVal, "N")) {

                                        arrAbnomarCol.add(key);
                                    }

                                    tmpObj.put("COL".concat(key), inpVal.equals("Y") || inpVal.equals("N") ? (inpVal.equals("Y") ? "정상" : "이상") : inpVal);
                                    break;
                                case "int":
                                    // int 타입의 이상범위에 포함 될 경우
                                    evalKey = arrHeaderKey.get(key).get("eng_nm");

                                    if (evalData.containsKey(evalKey)) {

                                        exp = new Expression(CmnFilterBiz.filterBlockRemoveString(evalData.get(evalKey)));

                                        for (String itm : listBiz.getUniqueList(CmnFilterBiz.filterBlockList(evalData.get(evalKey)))) {

                                            exp.with(itm, new BigDecimal(inpVal));
                                        }

                                        if (exp.eval().toString().equals("1")) {

                                            arrAbnomarCol.add(key);
                                        }
                                    }

                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

                tmpObj.put("ABNCOL", arrAbnomarCol);

                obj.putAll(tmpObj);
            }

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, resData);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipMainDataVO>() {
            });
        }
    }

    /**
     * 점검현황 저장(Insert)
     *
     * @param da_seq        보고서 고유번호
     * @param rpt_cd        보고서 코드
     * @param topic_cd      보고서 항목 코드
     * @param eqp_cd        장비 코드
     * @param topic_grp_seq 항목 그룹 일련번호
     * @param inp_val       입력 값
     * @param ses           HttpSession
     * @return Map {
     * isSuccess : true/false
     * }
     */
    @RequestMapping(value = "/setDetailCellUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Map setDetailCellUpdate(Long da_seq, String rpt_cd, String topic_cd, String eqp_cd, String topic_grp_seq, String inp_val, HttpSession ses) {

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
            // 1. 필터링(filterSqlClass 은 setter가 있으면 안됨)
            EquipMainDataVO vo = new EquipMainDataVO();

            vo.da_seq = da_seq;
            vo.rpt_cd = CmnFilterBiz.filterSqlString(rpt_cd);
            vo.topic_cd = CmnFilterBiz.filterSqlString(topic_cd);
            vo.eqp_cd = CmnFilterBiz.filterSqlString(eqp_cd);
            vo.topic_grp_seq = CmnFilterBiz.filterSqlString(topic_grp_seq);
            vo.inp_val = CmnFilterBiz.filterSqlString(inp_val);

            if (StringUtils.isEmpty(vo.topic_grp_seq) == true) {
                // 2. SLM_EQUIPMAIN_DATA
                getDao().setDetailEquipCellUpdate(vo);
            } else {
                // 2. SLM_SWMAIN_DATA
                getDao().setDetailSoftwareCellUpdate(vo);
            }

            tmpOK = true;
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 장비별 세부정보 엑셀 일괄등록
     *
     * @param req MultipartHttpServletRequest
     * @param res HttpServletResponse
     * @return the int
     */
    @RequestMapping(value = "/importDetailEquipExcel", method = RequestMethod.POST)
    // , headers = "Accept=application/json"
    @ResponseBody
    public int importDetailEquipExcel(MultipartHttpServletRequest req, HttpServletResponse res, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return -1;
        }

        int resCnt = -1;

        // 0. Transaction 처리
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("importDetailEquipExcel-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        // 1. 검색 조건 가져오기
        Map<String, String> param = new HashMap<String, String>();

        for (Map.Entry<String, String[]> obj : ((Map<String, String[]>) req.getParameterMap()).entrySet()) {

            param.put(obj.getKey(), CmnFilterBiz.filterSqlString(obj.getValue()[0]));
        }

        Long daSeq = Long.valueOf(CmnFilterBiz.filterPureString(param.get("daSeq")));

        // 2. Iterator 생성
        Iterator<String> itr = req.getFileNames();
        MultipartFile mf;

        try {
            // 3. 항목 가져오기(수정할 수 있는 항목)
            List<String> arrCol = new ArrayList<String>();

            for (Map<String, String> obj : getDao().getReportTopicList(param.get("rptCd"))) {

                if (obj.get("MOD_LOCK_FL").equals("N")) arrCol.add(obj.get("TOPIC_CD"));
            }

            // 4. Iterator 첨부파일 갯수만큼 반복
            while (itr.hasNext()) {

                // 4.1 MultipartFile 변환
                mf = req.getFile(itr.next());

                // 4.2 엑셀파일(Sheet = 각각의 보고서)
                OPCPackage opcPackage = OPCPackage.open(mf.getInputStream());
                XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
                XSSFSheet codeSheet = workbook.getSheet("코드설정");
                XSSFSheet sheet = null;
                EquipMainDataVO vo = null;

                // 4.3.1 코드설정에서 현재 장비별세부정보 폼에 해당하는 시트 찾기
                for (int i = 1; i < codeSheet.getPhysicalNumberOfRows(); i++) {

                    if (StringUtils.equals(codeSheet.getRow(i).getCell(0).toString(), param.get("rptCd"))) {

                        sheet = workbook.getSheet(codeSheet.getRow(i).getCell(1).toString());
                        break;
                    }
                }

                // 4.3.2 해당 데이터 Update 처리(단, 항목 설정에 잠금 설정이 풀린 항목 만)
                if (sheet.getPhysicalNumberOfRows() > 2) {
                    // 엑셀 Data가 존재 할 경우
                    int cellCnt = sheet.getRow(0).getPhysicalNumberOfCells();

                    vo = new EquipMainDataVO();

                    vo.da_seq = daSeq;
                    vo.rpt_cd = param.get("rptCd");

                    for (int row = 2; row < sheet.getPhysicalNumberOfRows(); row++) {

                        if (sheet.getRow(row).getCell(0) != null) {

                            vo.eqp_cd = CmnFilterBiz.filterSqlString(sheet.getRow(row).getCell(0).toString());

                            for (int col = 1; col < cellCnt; col++) {

                                if (sheet.getRow(0).getCell(col) != null) {

                                    vo.topic_cd = sheet.getRow(0).getCell(col).toString();

                                    if (arrCol.contains(vo.topic_cd)) {

                                        vo.inp_val = (sheet.getRow(row).getCell(col) != null ? CmnFilterBiz.filterSqlString(sheet.getRow(row).getCell(col).toString()) : "");

                                        if (StringUtils.isEmpty(vo.inp_val) == false) {

                                            switch (vo.inp_val) {
                                                case "정상":
                                                    vo.inp_val = "Y";
                                                    break;
                                                case "이상":
                                                    vo.inp_val = "N";
                                                    break;
                                            }
                                        }

                                        getDao().setDetailEquipCellUpdate(vo);
                                    }
                                }
                            }
                        }
                    }
                }

                resCnt = 0;
            }

            transactionManager.commit(status);

        } catch (Exception e) {

            log.error("첨부파일 등록 실패", e);
            transactionManager.rollback(status);
        }

        return resCnt;
    }

    /**
     * 장비별 세부정보 엑셀 내보내기
     *
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param ses HttpSession
     */
    @RequestMapping(value = "/exportDetailEquipExcel", method = RequestMethod.POST)
    @ResponseBody
    public void getDetailEquipExcel(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {

        // 0. 사용자 권한 체크
        String[] arr = {"R"};

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

            Long daSeq = Long.valueOf(CmnFilterBiz.filterPureString(param.get("daSeq")));

            // 2. 저장할 엑셀 파일명 설정
            Map<String, String> titleData = getDao().getExportExcelTitle(daSeq, param.get("rptCd"));

            // 3. 엑셀 헤더 및 칼럼 명 설정
            Map<String, Map<String, String>> arrHeaderData = new TreeMap<String, Map<String, String>>();

            List<String> arrHeader = new ArrayList<String>() {
                {
                    add("EQP_CD");
                }
            };
            List<String> arrHeaderKey = new ArrayList<String>() {
                {
                    add("EQP_CD");
                }
            };
            List<String> arrDataKey = null;

            for (final Map<String, String> obj : getDao().getReportTopicList(param.get("rptCd"))) {

                arrHeaderData.put(obj.get("TOPIC_CD"), new HashMap<String, String>() {
                    {
                        put("topic_type", obj.get("TOPIC_TYPE"));
                        put("eng_nm", obj.get("ENG_NM"));
                    }
                });

                arrHeader.add(obj.get("TOPIC_NM"));
                arrHeaderKey.add("K".concat(obj.get("TOPIC_CD")));
            }

            arrDataKey = new ArrayList<String>(arrHeaderData.keySet());

            // 4. 장비별 세부정보 데이터 가져오기(설정 되어 있는 S/W유형 값에 따라 다른 테이블 쿼리)
            List<Map<String, Object>> resData;

            switch (param.get("swTypeFl")) {
                case "Y":
                    // S/W
                    resData = getDao().getSWListReport(daSeq, param.get("rptCd"), "k", arrDataKey, null);
                    break;
                default:
                    // 장비
                    resData = getEquipDao().getEquipListReport(daSeq, param.get("rptCd"), "k", arrDataKey, null);
                    break;
            }

            String inpVal = "";
            Map<String, Object> tmpObj = null;
            for (Map<String, Object> obj : resData) {

                tmpObj = new HashMap<String, Object>(obj);

                for (String key : arrHeaderData.keySet()) {

                    if (obj.get("K".concat(key)) != null) {

                        inpVal = obj.get("K".concat(key)).toString();

                        if (StringUtils.isEmpty(inpVal) == false) {

                            switch (arrHeaderData.get(key).get("topic_type")) {
                                case "select":

                                    tmpObj.put("K".concat(key), inpVal.equals("Y") || inpVal.equals("N") ? (inpVal.equals("Y") ? "정상" : "이상") : inpVal);
                                    break;
                                default:
                                    break;
                            }
                        }
                    }
                }

                obj.putAll(tmpObj);
            }

            // 5. 엑셀 Export 호출
            CmnExcelBiz.generateXlsxList("/slm/excel/cmn/보고서_리스트유형.xlsx", "Sheet1", "slm", "tmp", String.format("%s-%s-%s-장비별세부정보.xlsx", CmnFilterBiz.filterPureString(param.get("briefDt")), titleData.get("GRP_NM"), CmnFilterBiz.filterSpecialRemoveString(titleData.get("RPT_NM"))), arrHeader, arrHeaderKey, resData, false, req, res);

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }
}
