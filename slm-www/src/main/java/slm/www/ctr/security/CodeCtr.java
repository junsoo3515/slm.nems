package slm.www.ctr.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import module.etc.CmnEtcBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.jqgrid.ResultJQGridVO;
import module.vo.jqgrid.SrcJQGridVO;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import slm.www.dao.security.CodeDao;
import slm.www.vo.security.CodeVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * 코드관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.22
 * Time: 오전 9:56
 */
@Controller
@RequestMapping("/security")
public class CodeCtr {

    private static final Logger log = LoggerFactory.getLogger(AuthorityCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S5S003";

    /**
     * 코드 DAO
     *
     * @return CodeDao
     */
    public CodeDao getDao() {
        return sqlSession.getMapper(CodeDao.class);
    }

    /**
     * 권한관리 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/code", method = RequestMethod.GET)
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
            mv.addObject("codeJongList", new ObjectMapper().writeValueAsString(getDao().getCodeGubunList())); // 코드 종류 가져오기
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 코드 목록 jqGrid 호출 Ctr
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
    @RequestMapping(value = "/getCodeList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getList(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, new String[]{"R"});
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<CodeVO>() {
            });
        }

        SrcJQGridVO vo = new SrcJQGridVO(sidx, sord, rows, _search, searchField, searchString, searchOper, filters, page);

        try {
            String srcJong = null;

            if (StringUtils.hasText(vo.filters)) {

                Map<String, String> jsonFilter = new ObjectMapper().readValue(vo.filters, new TypeReference<Map<String, String>>() {
                });

                srcJong = CmnFilterBiz.filterSqlString(jsonFilter.get("srcJong"));
            }

            int dataCnt = getDao().getCodeListCnt(srcJong, vo); // 총 갯수 가져오기
            int pageCnt = (int) Math.ceil((double) dataCnt / (double) vo.rows); // 갯수 기준 페이지 계산

            return new ResultJQGridVO(vo.page, dataCnt, pageCnt, getDao().getCodeList(srcJong, vo));
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<CodeVO>() {
            });
        }
    }

    /**
     * 코드 정보 저장(Insert, Update)
     * - 코드 정보, 권한별 메뉴 접근 관리
     *
     * @param id   고유 아이디
     * @param oper 상태(add, edit)
     * @param vo   WorkVO
     * @param ses  HttpSession
     * @return Map{isSuccess : true/false}
     */
    @RequestMapping(value = "/setCodeAct", method = RequestMethod.POST)
    @ResponseBody
    public Map setCodeAct(String id, String oper, CodeVO vo, HttpSession ses) {

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
                    resCnt = getDao().setCodeInsert(vo);

                    break;
                case "edit":
                    // 수정 모드
                    resCnt = getDao().setCodeUpdate(vo);
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
}
