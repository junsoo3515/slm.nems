package slm.www.ctr.onepage;

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
import module.etc.CmnEtcBiz;
import module.secure.encryption.CmnRsaOaepBiz;
import module.secure.filter.CmnFilterBiz;
import slm.www.dao.onepage.MyInfoDao;
import slm.www.vo.security.UserVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 개인정보수정
 * <p/>
 * User: 이종혁
 * Date: 2016.05.03
 * Time: 오후 3:20
 */
@Controller
@RequestMapping("/onepage")
public class MyInfoCtr {

    private static final Logger log = LoggerFactory.getLogger(MyInfoCtr.class);

    @Autowired
    private SqlSession sqlSession;

    private CmnEtcBiz etcBiz;

    private String mnu_cd ="S0B000";

    /**
     * 일일점검현황에서 사용하는 DAO
     *
     * @return InspectDao
     */
    public MyInfoDao getDao() { return sqlSession.getMapper(MyInfoDao.class); }
    /**
     * 개인정보수정 화면 처음 접근하는 Ctr
     *
     * @param req HttpServletRequest
     * @return ModelAndView
     *
     */
    @RequestMapping(value = "/myinfo", method = RequestMethod.GET)
    public ModelAndView invokeView(HttpServletRequest req) {

        if (etcBiz == null) {

            etcBiz = new CmnEtcBiz(sqlSession);
        }

        ModelAndView mv = new ModelAndView();

        try {
            String path = req.getContextPath();
            String id = req.getSession().getAttribute("id").toString();
            String mem_seq = req.getSession().getAttribute("member_profile_seq").toString();

            mv.addObject("p", path); // PATH 가져오기
            mv.addObject("member_profile_seq",mem_seq); // 헤더 - 사용자 사진 SEQ
            mv.addObject("member_nm", req.getSession().getAttribute("member_nm")); // 헤더 - 사용자 이름
            mv.addObject("id", id); // 헤더 - 사용자 이름
            mv.addObject("leftMenu", new CmnEtcBiz(sqlSession).getLeftMenu("S", req.getSession(), path, mnu_cd)); // 왼쪽메뉴 가져오기


        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return mv;
    }

    /**
     * 사용자 정보 저장(Insert, Update)
     * - 사용자 정보, 메뉴별 사용자 접근 권한 관리
     *
     * @param vo  UserVO
     * @param ses HttpSession
     * @return 0 : 에러, 1 이상 : Insert/Update 성공 Count
     */
    @RequestMapping(value = "/setMyInfoAct", method = RequestMethod.POST)
    @ResponseBody
    public int setMyInfoAct(@RequestBody UserVO vo, HttpSession ses) {

        String[] arr = {"U"};

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
            vo.mem_id = ses.getAttribute("id").toString().toUpperCase();

            // 3. 암호 알고리즘 도입
            if (StringUtils.isEmpty(vo.pwd) == false) {

                vo.pwd = CmnRsaOaepBiz.encrypt(vo.pwd);
            }

            // 4. 사용자 일련번호로 구분하여 COM_MEM_INFO Update 처리
            if (vo.mem_seq > 0) {

                resCnt += getDao().setMyInfoUpdate(vo);
            }

        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }

        return resCnt;
    }


}
