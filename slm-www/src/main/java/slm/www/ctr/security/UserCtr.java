package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.CmnDataDao;
import module.dao.data.DropDownDataDao;
import module.etc.CmnEtcBiz;
import module.secure.encryption.CmnRsaOaepBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.menu.MenuAuthVO;
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
import slm.www.dao.security.UserDao;
import slm.www.vo.security.UserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 사용자 계정 관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.15
 * Time: 오후 3:20
 */
@Controller
@RequestMapping("/security")
public class UserCtr {

    private static final Logger log = LoggerFactory.getLogger(UserCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private SqlSession sqlBatchSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S001";

    /**
     * 사용자계정관리에서 사용하는 DAO
     *
     * @return UserDao
     */
    public UserDao getDao() {
        return sqlSession.getMapper(UserDao.class);
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
     * 공통 드롭다운 DAO
     *
     * @return DropDownDataDao
     */
    public DropDownDataDao getDropDownDataDao() {

        return sqlSession.getMapper(DropDownDataDao.class);
    }

    /**
     * 사용자 관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/user", method = RequestMethod.GET)
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
            mv.addObject("authCrud", new ObjectMapper().writeValueAsString(etcBiz.getMenuDao().getAuthCrud(id, mnu_cd))); // 사용자별 Crud 권한 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

            mv.addObject("authList", new ObjectMapper().writeValueAsString(getDropDownDataDao().getAuthList())); // 권한 가져오기(Src)

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 사용자 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getUserList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<UserVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        String srcAuth = null;
        String srcId = null;
        String srcEtc = null;


        try {

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcAuth = CmnFilterBiz.filterSqlString(jsonFilter.get("srcAuth"));
                srcId = CmnFilterBiz.filterSqlString(jsonFilter.get("srcNm")).toUpperCase(); // 아이디는 무조건 대문자
                srcEtc = CmnFilterBiz.filterSqlString(jsonFilter.get("srcNm")); // 아이디는 무조건 대문자
            }

            int dataCnt = getDao().getUserListCnt(srcAuth, srcId, srcEtc, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getUserList(srcAuth, srcId, srcEtc, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<UserVO>() {
            });
        }
    }

    /**
     * 사용자 아이디 중복확인
     *
     * @param obj {mem_id:사용자 아이디}
     * @param ses HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/getIDCheck", method = RequestMethod.POST)
    @ResponseBody
    public Map getIDCheck(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"C"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        boolean tmpOK = false;
        String memId = "";

        if (obj.containsKey("mem_id")) {

            if (!StringUtils.isEmpty(obj.get("mem_id").toString())) {

                memId = CmnFilterBiz.filterSqlString(obj.get("mem_id").toString()).toUpperCase();
            }
        }

        try {

            tmpOK = (getDao().getIDChk(memId) == 0 ? true : false);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 로그인 실패횟수 초기화
     *
     * @param obj {mem_id:사용자 아이디}
     * @param ses HttpSession
     * @return the pwd fail clear
     */
    @RequestMapping(value = "/setPwdFailClear", method = RequestMethod.POST)
    @ResponseBody
    public Map setPwdFailClear(@RequestBody Map obj, HttpSession ses) {

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
        String memId = "";

        if (obj.containsKey("mem_id")) {

            if (!StringUtils.isEmpty(obj.get("mem_id").toString())) {

                memId = CmnFilterBiz.filterSqlString(obj.get("mem_id").toString()).toUpperCase();
            }
        }

        try {

            tmpOK = (getDao().setUserFailPwdClear(memId) > 0 ? true : false);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 사용자 상세정보 가져오기
     *
     * @param obj {mem_seq:사용자 고유 SEQ}
     * @param ses HttpSession
     * @return UserVO
     */
    @RequestMapping(value = "/getUserData", method = RequestMethod.POST)
    @ResponseBody
    public UserVO getUserData(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String mem_seq = "";

            if (obj.containsKey("mem_seq")) {

                if (!StringUtils.isEmpty(obj.get("mem_seq").toString())) {

                    mem_seq = CmnFilterBiz.filterSqlString(obj.get("mem_seq").toString()).toUpperCase();
                }
            }

            return getDao().getUserData(CmnFilterBiz.filterPureString(mem_seq));
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 사용자 정보 가져오기
     *
     * @param obj {mem_seq:사용자 고유 SEQ}
     * @param ses HttpSession
     * @return UserVO
     */
    @RequestMapping(value = "/getUserDataId", method = RequestMethod.POST)
    @ResponseBody
    public UserVO getUserDataId(@RequestBody Map obj, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);

            String mem_id = "";

            if (obj.containsKey("mem_id")) {

                if (!StringUtils.isEmpty(obj.get("mem_id").toString())) {

                    mem_id = CmnFilterBiz.filterSqlString(obj.get("mem_id").toString()).toUpperCase();
                }
            }

            return getDao().getUserDataId(mem_id);
        } catch (Exception ex) {

            return null;
        }
    }

    /**
     * 사용자 정보 저장(Insert, Update)
     * - 사용자 정보, 메뉴별 사용자 접근 권한 관리
     *
     * @param vo  UserVO
     * @param ses HttpSession
     * @return 0 : 에러, 1 이상 : Insert/Update 성공 Count
     */
    @RequestMapping(value = "/setUserAct", method = RequestMethod.POST)
    @ResponseBody
    public int setUserAct(@RequestBody UserVO vo, HttpSession ses) {

        String[] arr = {"C", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return 0;
        }

        int resCnt = 0;

        try {
            // 1. 필터링
            CmnFilterBiz.filterSqlClass(vo);

            // 2. 아이디 대문자 변환
            vo.mem_id = vo.mem_id.toUpperCase();

            // 3. 암호 알고리즘 도입
            if (StringUtils.isEmpty(vo.pwd) == false) {

                vo.pwd = CmnRsaOaepBiz.encrypt(vo.pwd);
            }

            // 4. 등록자 아이디 set
            vo.reg_mem_id = ses.getAttribute("id").toString();

            // 5. 사용자 일련번호로 구분하여 COM_MEM_INFO Insert/Update 처리
            if (vo.mem_seq > 0) {

                resCnt += getDao().setUserUpdate(vo);
            } else {

                resCnt += getDao().setUserInsert(vo);
            }

            // 5. 메뉴별 사용자 접근 권한 관리
            if (getDao().getUserAuthCnt(vo.mem_id) > 0) {

                try {

                    UserDao dao = sqlBatchSession.getMapper(UserDao.class);

//                    sqlBatchSession.commit(false);

                    for (MenuAuthVO obj : vo.authData) {

                        dao.setUserAuthUpdate(vo.auth_cd, vo.mem_id, obj);
                    }

                    dao = null;

//                    sqlBatchSession.commit();
                } catch (Exception ex) {

                    //sqlBatchSession.rollback();
                } finally {

                    //sqlBatchSession.close();
                }
            } else {

                long maxSeq = getCmnDao().getTableMaxSeq("COM_MEM_MNU", "seq", 1);

                resCnt += getDao().setUserAuthInsert(maxSeq, vo.auth_cd, vo.mem_id, vo.authData);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }
}
