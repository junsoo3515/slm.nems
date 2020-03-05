package module.secure.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 필터링 Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 14
 * Time: 오후 3:03
 */
public class CmnFilterBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnFilterBiz.class); // SLF4J Logger

    // 필터링 관련 인자
    private static final Pattern UNSECURED_REGULAR_PATTERN = Pattern.compile("(?i)(select|delete|update|insert|create|alter|drop|if|union|decode)\\b"); // SQL 특수 실행 구문(대소문자 상관없이)
    private static final Pattern EXECUTABLEFILETYPE_REGULAR_PATTERN = Pattern.compile("(?i)ade|adp|application|app|asa|ashx|asmx|asp|bas|bat|cdx|cer|chm|class|cmd|com|config|cpl|crt|csh|dll|exe|fxp|gadget|hlp|hta|htr|htw|ida|idc|idq|ins|inf|isp|its|jar|js|jse|ksh|lnk|mad|maf|mag|mam|maq|mar|mas|mat|mau|mav|maw|mda|mdb|mde|mdt|mdw|mdz|msc|msh|msh1|msh1xml|msh2|msh2xml|mshxml|msi|msp|mst|ops|pcd|pif|prf|prg|printer|ps1|ps1xml|ps2|ps2xml|psc1|psc2|pst|reg|rem|scf|scr|sct|shb|shs|shtm|shtml|soap|stm|url|vb|vbe|vbs|ws|wsc|wsf|wsh"); // 업로드 파일 실행 가능한 확장자
    private static final Pattern NON_SPECIAL_CHARACTER = Pattern.compile("[^\\uAC00-\\uD7A3xfe0-9a-zA-Z\\\\s]"); // 특수문자 제거
    private static final Pattern FILEPATH_PATTERN = Pattern.compile("^[&./\\\\]*$"); // 파일명에 경로 관련 특수문자 제거
    private static final Pattern NON_CHARACTER_PATTERN = Pattern.compile("([^\\d])"); // 순수 숫자 패턴
    private static final Pattern FBLOCK_REMOVE_PATTERN = Pattern.compile("[\\[\\]]"); // [, ] 제거
    private static final Pattern FBLOCK_PATTERN = Pattern.compile("\\[(?<key>.*?)\\]"); // [ x ] 사이의 x 값 추출

    /**
     * 정규식을 통한 SQL 특수구문 입력 값 필터링
     *
     * @param filetype 파일 확장자 명
     * @return 필터링 된 결과 값
     */
    public static boolean isExecutableFileType(final String filetype) {

        boolean isCheck = false;

        Matcher m = EXECUTABLEFILETYPE_REGULAR_PATTERN.matcher(filetype);

        while (m.find()) {

            isCheck = true;
            break;
        }

        return isCheck;
    }

    /**
     * 정규식을 통한 특수문자 제거
     *
     * @param orgStr 입력 값
     * @return 필터링 된 결과 값
     */
    public static String filterSpecialRemoveString(final String orgStr) {

        return filterString(NON_SPECIAL_CHARACTER, orgStr);
    }

    /**
     * 정규식을 통한 필터링
     *
     * @param orgStr 입력 값
     * @return 필터링 된 결과 값
     */
    public static String filterString(Pattern patt, final String orgStr) {

        StringBuffer destStringBuffer = new StringBuffer();

        Matcher m = patt.matcher(orgStr);

        while (m.find()) {
            m.appendReplacement(destStringBuffer, "");
        }
        m.appendTail(destStringBuffer);

        return destStringBuffer.toString();
    }

    /**
     * [ x ] 사이의 x 값 추출
     *
     * @param orgString
     * @return List<String>
     */
    public static List<String> filterBlockList(String orgString) {

        List<String> arrObj = new ArrayList<String>();

        Matcher m = FBLOCK_PATTERN.matcher(orgString);

        while (m.find()) {
            arrObj.add(m.group("key").toString());
        }

        return arrObj;
    }

    /**
     * 정규식을 통한 [, ] 특수문자 제거 추출
     *
     * @param orgStr 입력 값
     * @return 필터링 된 결과 값
     */
    public static String filterBlockRemoveString(final String orgStr) {

        return filterString(FBLOCK_REMOVE_PATTERN, orgStr);
    }

    /**
     * 정규식을 통한 순수 숫자만 추출
     *
     * @param orgStr 입력 값
     * @return 필터링 된 결과 값
     */
    public static String filterPureString(final String orgStr) {

        return filterString(NON_CHARACTER_PATTERN, orgStr);
    }

    /**
     * 정규식을 통한 SQL 특수구문 입력 값 필터링
     *
     * @param orgStr 입력 값
     * @return 필터링 된 결과 값
     */
    public static String filterSqlString(final String orgStr) {

        return filterString(UNSECURED_REGULAR_PATTERN, orgStr);
    }

    /**
     * 정규식을 통한 파일명에 들어오는 URL 관련 특수문자 필터링
     *
     * @param orgStr 입력 값
     * @return 필터링 된 결과 값
     */
    public static String filterFileUrlString(final String orgStr) {

        return filterString(FILEPATH_PATTERN, orgStr);
    }

    private static Object castObject(Object object) {

        if (object != null) {

            switch (object.getClass().getName()) {
                case "java.lang.String":

                    object = filterSqlString(object.toString());
                    break;
                case "java.lang.Integer":

                    object = Integer.valueOf(filterPureString(object.toString()));
                    break;
                case "java.lang.Long":

                    object = Long.valueOf(filterPureString(object.toString()));
                    break;
                case "java.lang.Double":

                    object = Double.valueOf(filterPureString(object.toString()));
                    break;
            }
        }

        return object;
    }

    /**
     * 정규식을 통한 SQL 특수구문 입력 값 필터링
     *
     * @param objClass 입력 VO CLASS
     * @return Object
     */
    public static Object filterSqlClass(final Object objClass) {

        try {
            Class c1 = objClass.getClass();

            for (Field m : c1.getDeclaredFields()) {

                m.setAccessible(true);
                m.set(objClass, castObject(m.get(objClass)));
            }

            return objClass;
        } catch (Exception ex) {

            log.error(ex.toString());

            return null;
        }
    }
}
