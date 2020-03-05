package module.etc;

import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import module.dao.menu.MenuDao;
import module.object.CmnListBiz;
import module.vo.menu.MenuVO;

import javax.servlet.http.HttpSession;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 기타 관련 Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:13
 */

public class CmnEtcBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnEtcBiz.class); // SLF4J Logger
    private SqlSession sqlSession;

    /**
     * 초기화
     */
    public CmnEtcBiz() {
    }

    /**
     * sqlSession Setting
     *
     * @param sql Mybatis의 xml을 사용하기 위해 인자 전달
     */
    public CmnEtcBiz(SqlSession sql) {
        sqlSession = sql;
    }

    /**
     * 메뉴 DAO
     *
     * @return MenuDao
     */
    public MenuDao getMenuDao() {
        return sqlSession.getMapper(MenuDao.class);
    }

    /**
     * 왼쪽 메뉴 가져오기
     *
     * @param folKey     폴더 코드(S : SLM, O : 운영관리, F : 시설물관리...)
     * @param ses        세션정보
     * @param rootFolder ROOT 경로(시스템 경로)
     * @param nowPageCd  현재 페이지 코드
     * @return List<MenuVO>
     */
    public String getLeftMenu(String folKey, HttpSession ses, String rootFolder, String nowPageCd) {

        StringBuffer retV = new StringBuffer();

        try {

            int menuCnt = 0;

            for (MenuVO vo : getMenuDao().getLeftMenu(folKey, ses.getAttribute("id").toString())) {

                if (StringUtils.endsWith(vo.mnu_cd, "000")) {

                    if (menuCnt > 0) {

                        retV.append("\n    </ul>");
                        retV.append("\n</li>");
                    } else {
                        // 처음 왼쪽 메뉴 시스템 명 설정정
                        retV.append("\n<li class='nav-header'>").append(vo.mnu_nm1).append("</li>");
                    }

                    retV.append("\n<li class='has-sub").append(StringUtils.equals(nowPageCd.substring(0, 3), vo.grp_cd) ? " active" : "").append("'>"); // expand 우선 없앰
                    retV.append("\n    <a href='").append(StringUtils.isEmpty(vo.url) ? "#" : vo.url).append("'>");
                    retV.append("\n        <b class='caret pull-right'></b>");
                    retV.append("\n        <i class='fa ").append(vo.etc).append("'></i>");
                    retV.append("\n        <span>").append(vo.mnu_nm2).append("</span>");
                    retV.append("\n    </a>");
                    retV.append("\n    <ul class='sub-menu'>");
                } else {

                    retV.append("\n        <li").append(StringUtils.equals(nowPageCd, vo.mnu_cd) ? " class='active'" : "").append("><a href='").append(StringUtils.isEmpty(vo.url) ? "#" : rootFolder + vo.url).append("'>").append(vo.mnu_nm3).append("</a></li>");
                }

                menuCnt++;
            }

            if (menuCnt > 0) {

                retV.append("\n    </ul>");
                retV.append("\n</li>");
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return retV.toString();
    }

    /**
     * 전체 시스템 메뉴 가져오기
     *
     * @return List<MenuVO> 패턴 데이터
     */
    public List<MenuVO> getAllMenu() {

        List<MenuVO> ret = new ArrayList<MenuVO>();

        try {

            ret = getMenuDao().getAllMenu();

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return ret;
    }

    /**
     * 사용자 계정 별 CRUD 권한 체크
     *
     * @param ses      세션 가져오기
     * @param menuKey  메뉴 코드
     * @param gubunKey 구분 키(C : reg_fl, R : read_fl, U : mod_fl, D : del_fl, V : use_fl)
     * @return
     * @throws Exception 접근 오류 Exception 발생
     */
    public void isUserAccessValidate(HttpSession ses, String menuKey, String[] gubunKey) throws Exception {

        boolean isSuccess = false;
        String isFlag = "N";
        Map<String, String> getAccess;

        try {

            if (ses != null && menuKey != null && gubunKey != null) {

                getAccess = getMenuDao().getAuthCrud(ses.getAttribute("id").toString(), menuKey);

                if (getAccess.isEmpty() == false) {

                    for (int i = 0; i < gubunKey.length; i++) {

                        switch (gubunKey[i]) {
                            case "C":
                                isFlag = getAccess.get("REG_FL");
                                break;
                            case "R":
                                isFlag = getAccess.get("READ_FL");
                                break;
                            case "U":
                                isFlag = getAccess.get("MOD_FL");
                                break;
                            case "D":
                                isFlag = getAccess.get("DEL_FL");
                                break;
                            case "V":
                                isFlag = getAccess.get("USE_FL");
                                break;
                        }

                        if (StringUtils.equals(isFlag, "N")) {

                            isSuccess = false;
                            break;
                        }
                    }
                }

                if (StringUtils.equals(isFlag, "Y")) {

                    isSuccess = true;
                }
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        } finally {

            isFlag = null;
            getAccess = null;
        }

        if (isSuccess == false) throw new Exception("접근 권한 오류 발생");
    }

    /**
     * 값 Setting
     *
     * @param rVal     실제 값
     * @param dVal     기본 값
     * @param isUseDef 기본 값 사용 유무
     * @return double
     */
    public Double setVal(Double rVal, Double dVal, boolean isUseDef) {

        Double res = rVal;

        if (res == null || res.isNaN()) {

            if (isUseDef) {

                res = dVal;
            }
        }

        if (res == null) {

            res = Double.NaN;
        }

        return res;
    }

    /**
     * 중복 제거 된 고유 태그 리스트
     *
     * @param tagInfos 표출항목 정보의 태그 정보
     * @param colId    칼럼ID
     * @return List<String> tag list
     */
    public List<String> getTagList(List<HashMap<String, Object>> tagInfos, String colId) {

        List<String> arrTags = new ArrayList<String>(); // 태그

        CmnListBiz listBiz = new CmnListBiz(); // List Business Logic

        try {

            for (HashMap<String, Object> obj : tagInfos) {

                if (obj.containsKey(colId)) {

                    arrTags.add(obj.get(colId).toString());
                }
            }

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return listBiz.getUniqueList(arrTags);
    }

    /**
     * Object Null 체크
     *
     * @param object the object
     * @return the boolean
     */
    public boolean isEmpty(Object object) {

        if (object == null) {
            return true;
        }

        if (object instanceof String) {

            String str = (String) object;
            return str.length() == 0;
        }

        if (object instanceof Collection) {

            Collection collection = (Collection) object;
            return collection.size() == 0;
        }

        if (object.getClass().isArray()) {

            try {

                if (Array.getLength(object) == 0) {

                    return true;
                }
            } catch (Exception e) {
                //do nothing
            }
        }

        return false;
    }

    /**
     * VO 패턴을 Map으로 변환
     *
     * @param obj the obj
     * @return the map
     */
    public Map<String, Object> convertObjectToMap(Object obj) {
        try {
            //Field[] fields = obj.getClass().getFields(); //private field는 나오지 않음.
            Field[] fields = obj.getClass().getDeclaredFields();
            Map resultMap = new HashMap();
            for (int i = 0; i <= fields.length - 1; i++) {
                fields[i].setAccessible(true);
                resultMap.put(fields[i].getName(), fields[i].get(obj));
            }
            return resultMap;
        } catch (IllegalArgumentException e) {

            e.printStackTrace();
        } catch (IllegalAccessException e) {

            e.printStackTrace();
        }
        return null;
    }

    /**
     * 천단위 , 로 변경한 값 가져오기
     *
     * @param num   숫자 값
     * @param posit 소수점 자리수
     * @return 결과
     */
    public String setComma(Object num, Integer posit) {

        String defType = "#,##0";
        String positType = "";

        if (posit > 0) {

            positType = positType.concat(".%0").concat(posit.toString()).concat("d");

            defType = defType.concat(String.format(positType, 0));
        }

        DecimalFormat df = new DecimalFormat(defType);

        if (num instanceof Long || num instanceof Integer ||
                num instanceof Short || num instanceof Byte ||
                num instanceof AtomicInteger ||
                num instanceof AtomicLong ||
                (num instanceof BigInteger && ((BigInteger)num).bitLength () < 64)) {

            return df.format(num);
        } else if (num instanceof BigDecimal) {

            return df.format(num);
        } else if (num instanceof BigInteger) {

            return df.format(num);
        } else if (num instanceof Double) {

            return df.format(num);
        } else if (num instanceof Number) {

            return df.format(num);
        } else {

            return String.valueOf(num);
        }
    }

    public String setComma(String num) {

        return setComma(num, 0);
    }
}
