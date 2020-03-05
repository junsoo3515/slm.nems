package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.DropDownDataDao;
import module.etc.CmnEtcBiz;
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
import slm.www.dao.security.EquipGItemDao;
import slm.www.vo.security.EquipGItemVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 장비그룹 기본항목 관리
 * <p/>
 * User: 이준수
 * Date: 2017.06.05
 * Time: 오전 09:52
 */
@Controller
@RequestMapping("/security")
public class EquipGItemCtr {

    private static final Logger log = LoggerFactory.getLogger(EquipGItemCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private DataSourceTransactionManager transactionManager;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S005";

    /**
     * 공통 드롭다운 DAO
     *
     * @return DropDownDataDao
     */
    public DropDownDataDao getDropDownDataDao() {

        return sqlSession.getMapper(DropDownDataDao.class);
    }

    public EquipGItemDao getDao() {
        return sqlSession.getMapper(EquipGItemDao.class);
    }

    /**
     * 장비그룹 기본항목관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/equipgitem", method = RequestMethod.GET)
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
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); //사용자별 Crud 권한 가져오기
            mv.addObject("member_profile_seq", req.getSession().getAttribute("member_profile_seq")); // 헤더 - 사용자 사진 SEQ
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기
            mv.addObject("equipTypeSrchList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getCodeList("EG"))); // 장비그룹(유형)


        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
        return mv;
    }

    /**
     * 기본항목관리 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getEqpGrpList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getEqpGrpList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipGItemVO>() {
            });
        }

        try {

            SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);
            String srcEqpType = null;

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcEqpType = CmnFilterBiz.filterSqlString(jsonFilter.get("srcEqpType"));
            }

            int dataCnt = getDao().getEqpGrpListCnt(srcEqpType, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getEqpGrpList(srcEqpType, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<EquipGItemVO>() {
            });
        }
    }

    /**
     * 기본항목 관리 저장(Insert, Update)
     *
     * @param id   고유 아이디
     * @param oper 상태(add, edit)
     * @param vo   EquiipGItemVO
     * @param ses  HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setEqpGrpAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setEqpGrpAct(String id, String oper, EquipGItemVO vo, HttpSession ses) {

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

            switch (oper) {
                case "add":
                    // 추가 모드
                    resCnt = getDao().setEqpGrpInsert(vo);
                    break;

                case "edit":
                    // 수정 모드
                    resCnt = getDao().setEqpGrpUpdate(vo);
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
     * 기본항목 관리 정렬순서 변경(Update)
     * @param vo   EquipGItemVO
     * @param ses  HttpSession
     * @return Map
     */
    @RequestMapping(value = "/setEqpGItemPosUpdate", method = RequestMethod.POST)
    @ResponseBody
    public Map setEqpGItemPosUpdate(@RequestBody EquipGItemVO vo, HttpSession ses){

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

        DefaultTransactionDefinition def = new DefaultTransactionDefinition();

        def.setName("setEqpGItemPosUpdate-transaction");
        def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

        TransactionStatus status = transactionManager.getTransaction(def);


        try {
            getDao().setEqpGItemPosUpdate1(vo);
            getDao().setEqpGItemPosUpdate2(vo);

            transactionManager.commit(status);

            tmpOK = true;
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
            transactionManager.rollback(status);
            tmpOK = false;
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("isSuccess",tmpOK);

        return map;
    }


    /**
     * 장비그룹 기본항목 관리 장비유형 가져오기(Select2)
     *
     * @param req HttpServletRequest
     * @return List<ListObjVO>
     */
    @RequestMapping(value = "/getEquipTypeSelect2", method = RequestMethod.GET)
    @ResponseBody
    public List<ListObjVO> getEquipTypeSelect2 (HttpServletRequest req) throws Exception{

        String word = req.getParameter("q");
        if(word != null) word = new String(word.getBytes("8859_1"), "UTF-8");

        return getDao().getEquipTypeSelect2(word);
    }

}
