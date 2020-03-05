package module.object;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

/**
 * List/Map 타입 가공을 위한 추가 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:26
 */
public class CmnListBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnListBiz.class); // SLF4J Logger

    /**
     * 중복 값 삭제
     *
     * @param oriItem List<String> 인자 값
     * @return List<String>
     */
    public List<String> getUniqueList(List<String> oriItem) {

        // 중복 인자 삭제
        List<String> arrItems = new ArrayList<String>(new HashSet<String>(oriItem));

        return arrItems;
    }

    /**
     * List에 prefix 추가
     *
     * @param oriItem List<String> 인자 값
     * @return List<String>
     */
    public List<String> setPrefixList(List<String> oriItem, String str) {

        List<String> arrItems = new ArrayList<String>(oriItem);

        for(int i = 0; i < arrItems.size(); i ++) {

            arrItems.set(i, str.concat(arrItems.get(i)));
        }

        return arrItems;
    }

    /**
     * Map Key 기준으로 정렬
     *
     * @param oriItem Map<String, Double> 인자 값
     * @param isDesc 내림차순 여부
     * @return TreeMap<String, Double>
     */
    public Map<String, Map<String, Double>> getSortKeyMap2(Map<String, Map<String, Double>> oriItem, boolean isDesc) {

        TreeMap<String, Map<String, Double>> tm = new TreeMap<String, Map<String, Double>>(oriItem);

        if ( isDesc ) {

            return tm.descendingMap();
        } else {

            return tm;
        }
    }

    /**
     * Map Key 기준으로 정렬
     *
     * @param oriItem Map<String, Double> 인자 값
     * @param isDesc 내림차순 여부
     * @return TreeMap<String, Double>
     */
    public Map<Double, Double> getSortKeyMap(Map<Double, Double> oriItem, boolean isDesc) {

        TreeMap<Double, Double> tm = new TreeMap<Double, Double>(oriItem);

        if ( isDesc ) {

            return tm.descendingMap();
        } else {

            return tm;
        }
    }

}
