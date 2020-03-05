package slm.www.ctr.dm;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.DropDownDataDao;
import module.etc.CmnEtcBiz;
import module.excel.poi.CmnExcelBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.dm.EquipDao;
import slm.www.vo.dm.EquipGrpVO;
import slm.www.vo.dm.EquipVO;
import slm.www.vo.dm.SparePartVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.*;

/**
 * 운영대상 > 장비관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.25
 * Time: 오전 10:57
 */
@Controller
@RequestMapping("/dm")
public class EquipCtr {

    private static final Logger log = LoggerFactory.getLogger(EquipCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private SqlSession sqlBatchSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S1O001";

    /**
     * 장비관리에서 사용하는 DAO
     *
     * @return EquipDao
     */
    public EquipDao getDao() {
        return sqlSession.getMapper(EquipDao.class);
    }

    /**
     * 장비관리에서 사용하는 DAO
     *
     * @return EquipDao
     */
    public EquipDao getBatchDao() {
        return sqlBatchSession.getMapper(EquipDao.class);
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
     * 장비관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/equip", method = RequestMethod.GET)
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

            mv.addObject("grpList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("EG"))); // 장비그룹(유형)

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
    @RequestMapping(value = "/getEquipList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getEquipList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipVO>() {
            });
        }

        try {
            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

            String srcGrp = null; // 장비유형
            String srcNm = null; // 장비 명칭

            ObjectMapper mapper = new ObjectMapper();

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>(){});

                srcGrp = CmnFilterBiz.filterSqlString(jsonFilter.get("srcGrp"));
                srcNm = CmnFilterBiz.filterSqlString(jsonFilter.get("srcNm"));
            }

            // 1. 장비 개수 가져오기
            int dataCnt = getDao().getEquipListCnt(srcGrp, srcNm, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            // 2. 장비유형 별 헤더 및 칼럼 명 설정
            List<String> arrHeaderKey = new ArrayList<String>();

            int colCnt = 0;
            for (ListObjVO obj : getDropDownDataDao().getEquipGrpTopicList(srcGrp)) {

                if (colCnt > 4) {

                    break;
                }

                arrHeaderKey.add(obj.id);
                colCnt++;
            }

            // 3. 장비 데이터 가져오기
            TreeMap<String, Map<String, Object>> resData = new TreeMap<>();

            for (final EquipVO obj : getDao().getEquipList(srcGrp, srcNm, vo)) {

                resData.put(obj.eqp_cd, new HashMap<String, Object>() {
                    {
                        put("eqp_cd", obj.eqp_cd);
                        put("eqp_grp_cd", obj.eqp_grp_cd);
                        put("eqp_nm", obj.eqp_nm);
                        put("eqp_serial", obj.eqp_serial);
                    }
                });
            }

            // 4. 장비유형 데이터 가져오기
            List<Map<String, Object>> typeList = getDao().getEquipListExport(srcGrp, "col", new ArrayList(resData.keySet()), arrHeaderKey);
            for (Map<String, Object> obj : typeList) {

                if (resData.containsKey(obj.get("EQP_CD"))) {

                    for (String colKey : arrHeaderKey) {

                        String col = "COL".concat(colKey);

                        if (obj.get(col) != null) {

                            resData.get(obj.get("EQP_CD").toString()).put(col.toLowerCase(), obj.get(col).toString());
                        }
                    }
                }
            }
            typeList = null;

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, new ArrayList(resData.descendingMap().values()));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipVO>() {
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
    @RequestMapping(value = "/getEquipData", method = RequestMethod.POST)
    @ResponseBody
    public EquipVO getEquipData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String eqpCD = "";

            if (obj.containsKey("eqp_cd")) {

                if (!StringUtils.isEmpty(obj.get("eqp_cd").toString())) {

                    eqpCD = CmnFilterBiz.filterSqlString(obj.get("eqp_cd").toString()).toUpperCase();
                }
            }

            return getDao().getEquipData(CmnFilterBiz.filterPureString(eqpCD));
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 기본항목 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getDataList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getDataList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipGrpVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String selEqpCd = null; // 장비 코드
        String selEqpGrpCd = null; // 장비유형 코드

        ObjectMapper mapper = new ObjectMapper();

        List<EquipGrpVO> resData = new ArrayList<EquipGrpVO>();

        try {

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>(){});

                if (jsonFilter.get("eqp_cd") != null) {

                    selEqpCd = CmnFilterBiz.filterSqlString(jsonFilter.get("eqp_cd"));
                }

                selEqpGrpCd = CmnFilterBiz.filterSqlString(jsonFilter.get("eqp_grp_cd"));
            }

            resData = getDao().getEquipGrpDataList(selEqpCd, selEqpGrpCd, vo);
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
    @RequestMapping(value = "/setEquipAct", method = RequestMethod.POST)
    @ResponseBody
    public EquipVO setEquipAct(@RequestBody EquipVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        int resCnt = 0;

        try {
            // 1. 필터링
            CmnFilterBiz.filterSqlClass(vo);

            vo.eqp_nm = CmnFilterBiz.filterSqlString(vo.eqp_nm);
            vo.eqp_serial = CmnFilterBiz.filterSqlString(vo.eqp_serial);
            vo.str_area = CmnFilterBiz.filterSqlString(vo.str_area);
            vo.remark = CmnFilterBiz.filterSqlString(vo.remark);

            // 2. 사용여부 / 등록자 아이디 set
            vo.use_fl = "Y";
            vo.reg_mem_id = ses.getAttribute("id").toString();

            // 3. 장비코드로 구분하여 SLM_EQUIPMENT Insert/Update 처리
            if (StringUtils.isEmpty(vo.eqp_cd) == false) {

                resCnt += getDao().setEquipUpdate(vo);
            } else {

                vo.eqp_cd = getDao().getEquipCreateCd();
                resCnt += getDao().setEquipInsert(vo);
            }

            // 4. 장비유형에 따른 기본항목(필터링 처리를 위해 배치 프로세스로 INSERT / UPDATE 처리
            try {
                vo.data_cnt = getDao().getEquipGrpDataCnt(vo.eqp_cd);

                for (EquipGrpVO obj : vo.topicData) {

                    obj.input_val = CmnFilterBiz.filterSqlString(obj.input_val); // 필터링

                    resCnt += (vo.data_cnt > 0 ? getBatchDao().setEquipGrpDataUpdate(vo.eqp_cd, obj) : getBatchDao().setEquipGrpDataInsert(vo.eqp_cd, obj));
                }
            } catch (Exception ex) {

                log.error(ex.toString(), ex);
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        } finally {
            // 클라이언트에서 그리드 조회로 값을 표시하기 위해 불 필요한 vo 객체 초기화
            vo.topicData = null;
        }

        return resCnt > -1 ? vo : null;
    }

    /**
     * 장비 삭제(Delete)
     * - 사용유무 칼럼을 'N'으로 Update 처리
     *
     * @param reqVO {eqp_cd:장비코드}
     * @param ses   HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setEquipDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setEquipDel(@RequestBody EquipVO reqVO, HttpSession ses) {

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
        String eqpCd = "";

        if (!StringUtils.isEmpty(reqVO.eqp_cd)) {

            eqpCd = CmnFilterBiz.filterSqlString(reqVO.eqp_cd);
        }

        try {

            tmpOK = (getDao().setEquipDelete(eqpCd) > 0 ? true : false);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 예비품 현황관리 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getSpareList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getSpareList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SparePartVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String selEqpCd = null; // 장비 코드

        ObjectMapper mapper = new ObjectMapper();

        try {

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>(){});

                if (jsonFilter.get("eqp_cd") != null) {

                    selEqpCd = CmnFilterBiz.filterSqlString(jsonFilter.get("eqp_cd"));
                }
            }

            int dataCnt = getDao().getEquipSparePartListCnt(selEqpCd, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getEquipSparePartList(selEqpCd, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<SparePartVO>() {
            });
        }
    }

    /**
     * 예비품 현황관리 저장(Insert)
     *
     * @param id   고유 아이디
     * @param oper 상태(add, edit)
     * @param vo   SparePartVO
     * @param ses  HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setSpareAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setSpareAct(String id, String oper, SparePartVO vo, HttpSession ses) {

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
        int resCnt = -1;

        try {

            // 1. 필터링(filterSqlClass 은 setter가 있으면 안됨)
            vo.setReg_dt(CmnFilterBiz.filterPureString(vo.reg_dt));

            switch (oper) {
                case "add":
                    // 추가 모드
                    resCnt = getDao().setEquipSparePartInsert(vo);

                    break;
            }

            tmpOK = (resCnt > 0 ? true : false);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 장비관리 목록 보고서
     *
     * @param req HttpServletRequest
     * @param res HttpServletResponse
     * @param ses HttpSession
     */
    @RequestMapping(value = "/exportEquipExcel", method = RequestMethod.POST)
    @ResponseBody
    public void getEquipExcelList(HttpServletRequest req, HttpServletResponse res, HttpSession ses) {

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

                param.put(obj.getKey(), obj.getValue()[0]);
            }

            // 2. 엑셀 헤더 및 칼럼 명 설정
            List<String> arrHeader = new ArrayList<String>();
            List<String> arrHeaderKey = new ArrayList<String>();
            List<String> arrDataKey = new ArrayList<String>();

            for (ListObjVO obj : getDropDownDataDao().getEquipGrpTopicList(param.get("srcGrp"))) {

                arrHeader.add(obj.val);
                arrDataKey.add(obj.id);
                arrHeaderKey.add("K".concat(obj.id));
            }
            // 3. 엑셀 Export 호출
            CmnExcelBiz.generateXlsxList("/slm/excel/cmn/보고서_리스트유형.xlsx", "Sheet1", "slm", "tmp", "장비관리.xlsx", arrHeader, arrHeaderKey, getDao().getEquipListExport(param.get("srcGrp"), "k", new ArrayList<String>() {
            }, arrDataKey), false, req, res);

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * 엑셀 일괄등록
     *
     * @param req MultipartHttpServletRequest
     * @param res HttpServletResponse
     * @return the long
     */
    @RequestMapping(value = "/importEquipExcel", method = RequestMethod.POST) // , headers = "Accept=application/json"
    @ResponseBody
    public int importEquipExcel(MultipartHttpServletRequest req, HttpServletResponse res, HttpSession ses) {

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

        // 1. Iterator 생성
        Iterator<String> itr = req.getFileNames();
        MultipartFile mf;

        // 2. Iterator 첨부파일 갯수만큼 반복
        while (itr.hasNext()) {

            // 2.1 MultipartFile 변환
            mf = req.getFile(itr.next());

            try {
                // 2.2 전체 장비유형 가져오기
                List<ListObjVO> grpData = getDropDownDataDao().getCodeList("EG");

                // 2.3 전체 장비유형별 항목 가져오기
                Map<String, List<String>> itemData = new HashMap<String, List<String>>();
                for (Map<String, String> obj : getDao().getEquipGrpTopicList(null)) {

                    if (!itemData.containsKey(obj.get("GRPCD"))) {

                        itemData.put(obj.get("GRPCD"), new ArrayList<String>());
                    }

                    itemData.get(obj.get("GRPCD")).add(obj.get("ID"));
                }

                for (String key : itemData.keySet()) {

                    Collections.sort(itemData.get(key));
                }

                // 2.4 전체 장비 상태 가져오기(엑셀 데이터와 비교하기 위한 것)
                String tmpData = "";
                Map<String, Map<String, Map<String, Object>>> eqpData = new HashMap<String, Map<String, Map<String, Object>>>();

                for (final EquipVO obj : getDao().getEquipAllList()) {

                    tmpData = obj.eqp_serial.concat("_").concat(obj.eqp_nm);

                    if (!eqpData.containsKey(obj.eqp_grp_cd)) {

                        eqpData.put(obj.eqp_grp_cd, new HashMap<String, Map<String, Object>>());
                    }

                    if (!eqpData.get(obj.eqp_grp_cd).containsKey(tmpData)) {

                        eqpData.get(obj.eqp_grp_cd).put(tmpData, new HashMap<String, Object>() {
                            {
                                put("cd", obj.eqp_cd);
                                put("data_cnt", obj.data_cnt);
                            }
                        });
                    }
                }

                // 2.5 엑셀파일의 Sheet 수 만큼 반복(각각 Sheet = 각각의 장비유형)
                OPCPackage opcPackage = OPCPackage.open(mf.getInputStream());
                XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
                XSSFSheet sheet = null;
                String sheetNm = "";
                EquipVO vo = null;

                for (int i = 0; i < workbook.getNumberOfSheets(); i++) {

                    sheet = workbook.getSheetAt(i);

                    sheetNm = sheet.getSheetName();

                    for (ListObjVO obj : grpData) {

                        if (StringUtils.equals(sheetNm, obj.val)) {
                            // 해당 Sheet일 경우
                            Map<String, Map<String, Object>> data = new HashMap<String, Map<String, Object>>(eqpData.get(obj.id));

                            if (sheet.getPhysicalNumberOfRows() > 2) {
                                // 엑셀 Data가 존재 할 경우

                                int cellCnt = sheet.getRow(0).getPhysicalNumberOfCells();

                                vo = new EquipVO();

                                vo.eqp_grp_cd = obj.id;
                                vo.use_fl = "Y";
                                vo.reg_mem_id = ses.getAttribute("id").toString();

                                for (int row = 2; row < sheet.getPhysicalNumberOfRows(); row++) {

                                    if (sheet.getRow(row).getCell(0) != null) {

                                        vo.eqp_nm = CmnFilterBiz.filterSqlString(sheet.getRow(row).getCell(0).toString());
                                        vo.eqp_serial = CmnFilterBiz.filterSqlString(sheet.getRow(row).getCell(1).toString());

                                        tmpData = vo.eqp_serial.concat("_").concat(vo.eqp_nm);

                                        if (data.containsKey(tmpData)) {
                                            // DB에 존재
                                            vo.data_cnt = Integer.valueOf(data.get(tmpData).get("data_cnt").toString());
                                            vo.eqp_cd = data.get(tmpData).get("cd").toString();
                                            getBatchDao().setEquipUpdate(vo);
                                        } else {
                                            // DB에 없음
                                            vo.data_cnt = 0;
                                            vo.eqp_cd = getBatchDao().getEquipCreateCd();
                                            getBatchDao().setEquipInsert(vo);
                                        }

                                        // 엑셀 topic 데이터 갱신
                                        vo.topicData = new ArrayList<EquipGrpVO>();

                                        EquipGrpVO egVo;
                                        List<String> defItem = new ArrayList<String>(itemData.get(vo.eqp_grp_cd));

                                        for (int col = 2; col < cellCnt; col++) {

                                            egVo = new EquipGrpVO();

                                            egVo.topic_cd = CmnFilterBiz.filterSqlString(sheet.getRow(0).getCell(col).toString());
                                            egVo.input_val = (sheet.getRow(row).getCell(col) != null ? CmnFilterBiz.filterSqlString(sheet.getRow(row).getCell(col).toString()) : "");

                                            vo.topicData.add(egVo);
                                            defItem.remove(egVo.topic_cd);
                                        }
                                        // 엑셀에 코드가 없는 것들은 빈 값으로 넣기 위해
                                        for (String items : defItem) {

                                            egVo = new EquipGrpVO();

                                            egVo.topic_cd = items;
                                            egVo.input_val = "";

                                            vo.topicData.add(egVo);
                                        }

                                        // 삭제 후 일괄 Insert(성능 향상 때문에)
                                        getBatchDao().setEquipGrpDataDelete(vo.eqp_cd);
                                        getBatchDao().setEquipGrpDataBatchInsert(vo.eqp_cd, vo.topicData);
                                    }
                                }
                            }

                            break;
                        }
                    }
                }

                resCnt = 0;

            } catch (Exception e) {

                log.error("첨부파일 등록 실패" + mf.getOriginalFilename(), e);
                return -1;
            }
        }

        return resCnt;
    }
}
