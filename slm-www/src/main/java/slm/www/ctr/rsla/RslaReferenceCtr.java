package slm.www.ctr.rsla;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import module.etc.CmnEtcBiz;
import module.vo.jqgrid.ResultJQGridVO;
import slm.www.dao.rsla.ReferenceDao;
import slm.www.vo.rsla.ReferenceVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;

/**
 * SLA 보고서 관리
 * <p/>
 * User: 이종혁
 * Date: 2016.04.28
 * Time: 오후 1:55
 */
@Controller
@RequestMapping("/rsla")
public class RslaReferenceCtr {

    private static final Logger log = LoggerFactory.getLogger(RslaReferenceCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;
    private String mnu_cd = "S4A002";

    public ReferenceDao getDao() {
        return sqlSession.getMapper(ReferenceDao.class);
    }


    /**
     * SLA 항목평가기준 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     */
    @RequestMapping(value = "/reference", method = RequestMethod.GET)
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
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * SLA 항목평가 리스트 jqGrid 호출 Ctr
     *
     * @param sidx 소팅 헤더 아이디
     * @param sord 소팅 (asc / desc)
     * @param rows 표출 Row 수
     * @param _search 검색 여부
     * @param searchField 검색어 필드 아이디
     * @param searchString 검색어 값
     * @param searchOper 검색어 조건
     * @param filters 필터(Model로 컨버팅 하기 위한 기타 조건들..)
     * @param page 현재 페이지 No
     * @param ses HttpSession
     * @return ResultJQGridVO
     */
    @RequestMapping(value = "/getSlaEvaList", method = RequestMethod.POST)
    @ResponseBody
    public ResultJQGridVO getSlaEva(String sidx, String sord, int rows, boolean _search, String searchField, String searchString, String searchOper, String filters, int page, HttpSession ses) {

        String[] arr = {"R"};

        try {

            if (etcBiz == null) {

                etcBiz = new CmnEtcBiz(sqlSession);
            }

            etcBiz.isUserAccessValidate(ses, mnu_cd, arr);
        } catch (Exception ex) {

            return new ResultJQGridVO(1, 0, 1, new ArrayList<ReferenceVO>() {});
        }

        List<ReferenceVO> arry =  getDao().getSlaEva();

        return new ResultJQGridVO(1, arry.size(), 1,arry);
    }


}
