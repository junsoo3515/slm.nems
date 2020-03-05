package module.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 수학함수 관련 Business 로직
 * <p/>
 * User: 현재호
 * Date: 2016.06.13
 * Time: 오후 5:19
 */
public class CmnMathBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnMathBiz.class); // SLF4J Logger

    /**
     * jqGrid  옵션
     */
    public static enum calcMathOpt {
        ROUND, CEIL, FLOOR
    }

    /**
     * 절상, 절하, 반올림 처리
     * @param strMode  - 수식
     * @param nCalcVal - 처리할 값(소수점 이하 데이터 포함)
     * @param nDigit   - 연산 기준 자릿수(오라클의 ROUND함수 자릿수 기준)
     *                   -2:십단위, -1:원단위, 0:소수점 1자리
     *                   1:소수점 2자리, 2:소수점 3자리, 3:소수점 4자리, 4:소수점 5자리 처리
     * @return String nCalcVal
     */
    public static String calcMath(calcMathOpt strMode, double nCalcVal, int nDigit) {

        switch( strMode ) {
            case ROUND :
                // 반올림
                if(nDigit < 0) {
                    nDigit = -(nDigit);
                    nCalcVal = Math.round(nCalcVal / Math.pow(10, nDigit)) * Math.pow(10, nDigit);
                } else {
                    nCalcVal = Math.round(nCalcVal * Math.pow(10, nDigit)) / Math.pow(10, nDigit);
                }
                break;
            case CEIL :
                // 절상
                if(nDigit < 0) {
                    nDigit = -(nDigit);
                    nCalcVal = Math.ceil(nCalcVal / Math.pow(10, nDigit)) * Math.pow(10, nDigit);
                } else {
                    nCalcVal = Math.ceil(nCalcVal * Math.pow(10, nDigit)) / Math.pow(10, nDigit);
                }

                break;
            case FLOOR :
                // 절하
                if(nDigit < 0) {
                    nDigit = -(nDigit);
                    nCalcVal = Math.floor(nCalcVal / Math.pow(10, nDigit)) * Math.pow(10, nDigit);
                } else {
                    nCalcVal = Math.floor(nCalcVal * Math.pow(10, nDigit)) / Math.pow(10, nDigit);
                }
                break;
            default :

                nCalcVal = Math.round(nCalcVal);
                break;
        }

        return String.format("%.0"+nDigit+"f", nCalcVal);
    }
}
