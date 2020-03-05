package slm.www.ctr.rday;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.CmnDataDao;
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
import slm.www.dao.rday.TroubleShootDao;
import slm.www.vo.rday.MeaSureVO;
import slm.www.vo.rday.TroubleShootVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 장애처리
 * <p/>
 * User: 이종혁
 * Date: 2016.05.19
 * Time: 오후 09:56
 */
@Controller
@RequestMapping("/rday")
public class RdayTroubleShootCtr {

    private static final Logger log = LoggerFactory.getLogger(RdayTroubleShootCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    @Autowired
    private SqlSession sqlBatchSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S2D003";

    /**
     * 장애처리 DAO
     *
     * @return TroubleShootDao
     */
    public TroubleShootDao getDao() {
        return sqlSession.getMapper(TroubleShootDao.class);
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
     * 공통으로 사용하는 DAO
     *
     * @return CmnDataDao
     */
    public CmnDataDao getCmnDao() {
        return sqlSession.getMapper(CmnDataDao.class);
    }

    /**
     * 장애처리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/troubleshoot", method = RequestMethod.GET)
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
            mv.addObject("disOrderTypeList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("ET"))); // 장애그룸(유형)
            mv.addObject("workTypeList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("WT"))); // 작업그룹(유형)
            mv.addObject("workStateList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("WS"))); // 작업상태그룹(유형)
            mv.addObject("meaTypeList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("AI"))); // 조치사항그룹(유형)
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return mv;
    }

    /**
     * 장애처리 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getRdayTroubleShootList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRdayTroubleShootList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<TroubleShootVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String srcHeadGrp = null;
        String fin_fl = null;
        String sDate = null;
        String eDate = null;


        String srcGrp = null;

        ObjectMapper mapper = new ObjectMapper();


        try {

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = mapper.readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

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
            }

            int dataCnt = getDao().getRdayTroubleShootListCnt(srcHeadGrp, sDate, eDate, fin_fl, srcGrp, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getRdayTroubleShootList(srcHeadGrp, sDate, eDate, fin_fl, srcGrp, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<TroubleShootVO>() {
            });
        }
    }


    /**
     * 장애처리조치사항 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getRdayTroubleShootMeasureList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRdayTroubleShootMeasureList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, String cause_seq, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<TroubleShootVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);


        List<MeaSureVO> arry = getDao().getRdayTroubleShootMeasureList(cause_seq, vo);
        int dataCnt = getDao().getRdayTroubleShootMeasureListCnt(cause_seq, vo); // 총 갯수 가져오기
        int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산
        return new ResultJQGridVO(vo.page, dataCnt, pageCnt, arry);
    }

    /**
     * 장애처리 상세정보 가져오기
     *
     * @param obj {dis_seq:장애처리 코드 seq}
     * @param ses HttpSession
     * @return TroubleShootVO
     */
    @RequestMapping(value = "/getRdayTroubleShootData", method = RequestMethod.POST)
    @ResponseBody
    public TroubleShootVO getRdayTroubleShootData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String dis_seq = "";

            if (obj.containsKey("dis_seq")) {

                if (!StringUtils.isEmpty(obj.get("dis_seq").toString())) {

                    dis_seq = CmnFilterBiz.filterPureString(obj.get("dis_seq").toString());
                }
            }
            TroubleShootVO vo = getDao().getRdayTroubleShootData(dis_seq);
            return vo;
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 장애처리조치사항 정보 저장(Insert, Update)
     * - 장애처리조치사항 저장, 수정
     *
     * @param vo  TroubleShootVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setTroubleShootMeasureAct", method = RequestMethod.POST)
    @ResponseBody
    public int setTroubleShootMeasureAct(@RequestBody MeaSureVO vo, HttpSession ses) {

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
                resCnt = getDao().setTroubleShootMeasureUpdate(vo);
            } else {
                resCnt = getDao().setTroubleShootMeasureInsert(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 장애처리 정보 저장(Insert, Update)
     * - 장애처리 정보 저장, 수정
     *
     * @param vo  TroubleShootVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setTroubleShootAct", method = RequestMethod.POST)
    @ResponseBody
    public int setTroubleShootAct(@RequestBody TroubleShootVO vo, HttpSession ses) {

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
            vo.occur_dt = CmnFilterBiz.filterPureString(vo.occur_dt);

            if (vo.cont != null && vo.cont.length() != 0) {
                vo.cont = CmnFilterBiz.filterSqlString(vo.cont);
            }
            if (vo.mea_plan_dt != null && vo.mea_plan_dt.length() != 0) {
                vo.mea_plan_dt = CmnFilterBiz.filterPureString(vo.mea_plan_dt);
            }
            if (vo.mea_fin_dt != null && vo.mea_fin_dt.length() != 0) {
                vo.mea_fin_dt = CmnFilterBiz.filterPureString(vo.mea_fin_dt);
            }
            if (vo.sv_stop_dt != null && vo.sv_stop_dt.length() != 0) {
                vo.sv_stop_dt = CmnFilterBiz.filterPureString(vo.sv_stop_dt);
            }
            if (vo.sv_start_dt != null && vo.sv_start_dt.length() != 0) {
                vo.sv_start_dt = CmnFilterBiz.filterPureString(vo.sv_start_dt);
            }
            if (vo.summ_cont != null && vo.summ_cont.length() != 0) {
                vo.summ_cont = CmnFilterBiz.filterSqlString(vo.summ_cont);
            }

            vo.fin_fl = CmnFilterBiz.filterSqlString(vo.fin_fl);
            vo.nature_fl = CmnFilterBiz.filterSqlString(vo.nature_fl);
            vo.err_fl = CmnFilterBiz.filterSqlString(vo.err_fl);

            if (vo.dis_seq != null && vo.dis_seq.length() != 0) {
                resCnt = getDao().setTroubleShootUpdate(vo);
            } else {
                resCnt = getDao().setTroubleShootInsert(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 장애처리 정보 삭제(Delete)
     * - 장애처리 정보
     *
     * @param vo  TroubleShootVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setTroubleShootDel", method = RequestMethod.POST)
    @ResponseBody
    public int setTroubleShootDel(@RequestBody TroubleShootVO vo, HttpSession ses) {

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

            if (vo.dis_seq != null && vo.dis_seq.length() != 0) {
                resCnt = getDao().setTroubleShootDelete(vo);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 장애처리조치사항 정보 삭제(Delete)
     * - 장애처리조치사항 정보
     *
     * @param vo  TroubleShootVO
     * @param ses HttpSession
     * @return resCnt : 1/-1}
     */
    @RequestMapping(value = "/setTroubleShootMeasureDel", method = RequestMethod.POST)
    @ResponseBody
    public int setTroubleShootMeasureDel(@RequestBody MeaSureVO vo, HttpSession ses) {

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
        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setTroubleShootMeasureDel-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);

        try {

            if (vo.mea_seq != null && vo.mea_seq.length() != 0) {
                resCnt = getDao().setTroubleShootMeasureDelete(vo);
                if(vo.gubun_cd != "")
                getDao().setFileDel(vo);
                transactionManager.commit(status);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
            transactionManager.rollback(status);
        }

        return resCnt;
    }

    /**
     * 보고서 출력을 위한 암호화 키 가져오기
     * <p/>
     *
     * @param reqData dis_seq : 장애처리 고유 일련번호
     * @return
     */
    @RequestMapping(value = "/getDisOrderSecureData", method = RequestMethod.POST)
    @ResponseBody
    public Map getDisOrderSecureData(@RequestBody Map<String, String> reqData) {

        String secureCd = "";

        try {

            secureCd = CmnRsaOaepBiz.encrypt(reqData.get("dis_seq").toString());

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        Map<String, String> map = new HashMap<String, String>();

        map.put("key", secureCd);

        return map;
    }

    /**
     * 작업자 정보 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getRdayTroubleShootWorkerList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getRdayTroubleShootWorkerList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses, String mem_id, String dis_seq) {

        String[] arr = {"C"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<TroubleShootVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);





        try {



            return new ResultJQGridVO(1, 0, 1, getDao().getRdayTroubleShootWorkerList(dis_seq, mem_id, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<TroubleShootVO>() {
            });
        }
    }

    /**
     * 작업자 정보 저장하기(Update)
     *
     * @param id   String
     * @param oper String
     * @param vo   TroubleShootVO
     * @param ses  HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setWorkerGridAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setWorkerGridAct(String id, String oper, TroubleShootVO vo,  HttpSession ses) {

        String[] arr = {"C","U"};

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
            if(oper.equals("edit")) getDao().setWorkerGridUpdate(vo);
            else getDao().setWorkerGridInsert(vo);


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
     * 작업자 정보 삭제하기(Delete)
     *
     * @param vo  TroubleShootVO
     * @param ses HttpSession
     * @return Map{isSuccess : [true : 성공, false : 실패]}
     */
    @RequestMapping(value = "/setWorkerGridDel", method = RequestMethod.POST)
    @ResponseBody
    public Map setWorkerGridDel(@RequestBody TroubleShootVO vo, HttpSession ses) {

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

            getDao().setWorkerGridDelete(vo);

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
     * 작업자정보 이름 가져오기(Select2)
     *
     * @param req HttpServletRequest
     * @return List<ListObjVO>
     */
    @RequestMapping(value = "/getWorkerNameSelect2", method = RequestMethod.GET)
    @ResponseBody
    public List<ListObjVO> getWorkerNameSelect2(HttpServletRequest req) throws Exception {

        String word = req.getParameter("q");
        if (word != null) word = new String(word.getBytes("8859_1"), "UTF-8");

        return getDao().getWorkerNameSelect2(word);
    }

    /**
     * 작업자정보 세부정보 가져오기
     *
     * @param vo TroubleShootVO
     * @param mem_id String
     * @return TroubleShootVO
     */
    @RequestMapping(value = "/getWorkInfoSetting", method = RequestMethod.POST)
    @ResponseBody
    public TroubleShootVO getWorkInfoSetting(TroubleShootVO vo, @RequestBody String mem_id){
        return getDao().getWorkerInfoSetting(vo, mem_id);
    }

    /**
     * 선택한 파일명 가져오기
     *
     * @param vo TroubleShootVO
     * @return TroubleShootVO
     */
    @RequestMapping(value = "/getFileName", method = RequestMethod.POST)
    @ResponseBody
    public TroubleShootVO getFileName(@RequestBody TroubleShootVO vo){
        return getDao().getFileName(vo);
    }

    /**
     * 파일 삭제하기
     *
     * @param vo TroubleShootVO
     * @return in
     */
    @RequestMapping(value = "/setFileDel", method = RequestMethod.POST)
    @ResponseBody
    public int setFileDelete(@RequestBody MeaSureVO vo){
        return getDao().setFileDel(vo);
    }

    /**
     * 최대 조치내용 일련번호 가져오기
     *
     * @return in
     */
    @RequestMapping(value = "/getMaxMeaSeq", method = RequestMethod.POST)
    @ResponseBody
    public long getMaxMeaSeq(){

        return getCmnDao().getTableMaxSeq("SLM_MEASURE","mea_seq",1);
    }


}
