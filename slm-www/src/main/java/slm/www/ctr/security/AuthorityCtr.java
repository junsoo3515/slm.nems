package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.dao.data.CmnDataDao;
import module.etc.CmnEtcBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.menu.MenuAuthVO;
import module.vo.menu.MenuVO;
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
import slm.www.dao.security.AuthorityDao;
import slm.www.vo.security.AuthorityVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 권한관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.21
 * Time: 오전 10:28
 */
@Controller
@RequestMapping("/security")
public class AuthorityCtr {

    private static final Logger log = LoggerFactory.getLogger(AuthorityCtr.class);

    @Autowired
    private SqlSession sqlSession;

    @Autowired
    private SqlSession sqlBatchSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S002";

    /**
     * 권한 DAO
     *
     * @return AuthorityDao
     */
    public AuthorityDao getDao() {
        return sqlSession.getMapper(AuthorityDao.class);
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
     * 권한관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/authority", method = RequestMethod.GET)
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
     * 권한 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getAuthList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

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

        try {

            String srcAuth = null;
            String srcEtc = null;

            if (!StringUtils.isEmpty(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcAuth = CmnFilterBiz.filterSqlString(jsonFilter.get("srcAuth")).toUpperCase(); // 권한 코드는 대문자로 저장되기 때문에 치환해서 검색
                srcEtc = CmnFilterBiz.filterSqlString(jsonFilter.get("srcEtc"));
            }

            int dataCnt = getDao().getAuthListCnt(srcAuth, srcEtc, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getAuthList(srcAuth, srcEtc, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<AuthorityVO>() {
            });
        }
    }

    /**
     * 권한 코드 중복확인
     *
     * @param obj {auth_cd:권한 코드}
     * @param ses HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/getAuthCheck", method = RequestMethod.POST)
    @ResponseBody
    public Map getAuthCheck(@RequestBody Map obj, HttpSession ses) {

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
        String authCd = "";

        if (obj.containsKey("auth_cd")) {

            if (!StringUtils.isEmpty(obj.get("auth_cd").toString())) {

                authCd = CmnFilterBiz.filterSqlString(obj.get("auth_cd").toString()).toUpperCase();
            }
        }

        try {

            tmpOK = (getDao().getAuthChk(authCd) == 0 ? true : false);
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        Map<String, Boolean> map = new HashMap<String, Boolean>();

        map.put("isSuccess", tmpOK);

        return map;
    }

    /**
     * 권한 정보 저장(Insert, Update)
     * - 권한 정보, 권한별 메뉴 접근 관리
     *
     * @param vo  AuthVO
     * @param ses HttpSession
     * @return 0 : 에러, 1 이상 : Insert/Update 성공 Count
     */
    @RequestMapping(value = "/setAuthAct", method = RequestMethod.POST)
    @ResponseBody
    public int setAuthAct(@RequestBody AuthorityVO vo, HttpSession ses) {

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

            // 2. 권한 코드 대문자 변환
            vo.auth_cd = vo.auth_cd.toUpperCase();

            // 3. COM_MEM_AUTH Insert/Update 처리
            resCnt += getDao().setAuthAct(vo);

            // 4. 메뉴별 사용자 접근 권한 관리
            if (getDao().getAuthMenuCnt(vo.auth_cd) > 0) {

                try {

                    AuthorityDao dao = sqlBatchSession.getMapper(AuthorityDao.class);

//                    sqlBatchSession.commit(false);

                    for (MenuAuthVO obj : vo.authData) {

                        dao.setAuthMenuUpdate(vo.auth_cd, obj);
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

                resCnt += getDao().setAuthMenuInsert(maxSeq, vo.auth_cd, vo.authData);
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }

    /**
     * 메뉴별 접근 권한 목록 Ctr
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
    @RequestMapping(value = "/getMenuList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getMenuList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<MenuVO>() {
            });
        }

        List<MenuVO> resData = new ArrayList<MenuVO>();

        try {

            resData = etcBiz.getAllMenu();
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return new ResultJQGridVO(1, resData.size(), 1, resData);
    }

    /**
     * 권한/사용자별 메뉴별 접근 설정 가져오기
     *
     * @param data {jongCd : A(권한) / B(사용자), val: 해당 코드 값}
     * @param ses  HttpSession
     * @return the menu data
     */
    @RequestMapping(value = "/getMenuData", method = RequestMethod.POST)
    @ResponseBody
    public List<MenuAuthVO> getMenuData(@RequestBody Map<String, Object> data, HttpSession ses) {

        String[] arr = {"C", "R", "U"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return null;
        }

        try {

            return etcBiz.getMenuDao().getMenuData(data.get("jongCd").toString(), data.get("val").toString());

        } catch (Exception ex) {

            log.error(ex.toString(), ex);

            return new ArrayList<MenuAuthVO>();
        }
    }
}
