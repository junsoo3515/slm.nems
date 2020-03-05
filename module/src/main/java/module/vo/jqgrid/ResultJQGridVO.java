package module.vo.jqgrid;

import java.util.List;
import java.util.Map;

/**
 * jqGrid Plugin 리턴 VO 타입
 * <p/>
 * jqGrid Plugin 자체에서 인식 하는 패턴이 정형화 되어 있기 때문에 사용
 * User: 현재호
 * Date: 16. 4. 12
 * Time: 오후 4:24
 */
public class ResultJQGridVO {

    public int page; // 현재 페이지
    public int records; // 총 자료 수
    public int total; // 총 페이지 수

    public List<?> rows; // 쿼리 결과 List VO

    public Map<String, Object> etcdata; // 추가 결과 Map 객체(추가적인 정보 가공해서 결과 보낼 때 사용)

    /**
     * 초기화
     */
    public ResultJQGridVO() {

    }

    /**
     * 기본 값 일괄 적용
     *
     * @param nowPG     현재 페이지
     * @param totalData 총 자료수
     * @param totalPage 총 페이지 수
     * @param res       쿼리 결과
     */
    public ResultJQGridVO(int nowPG, int totalData, int totalPage, List<?> res) {

        this.page = nowPG;
        this.records = totalData;
        this.total = totalPage;
        this.rows = res;
    }

    /**
     * 기본 값 일괄 적용
     *
     * @param nowPG 현재 페이지
     * @param totalData 총 자료수
     * @param totalPage 총 페이지 수
     * @param res 쿼리 결과
     * @param etcdata 추가로 보낼 결과
     */
    public ResultJQGridVO(int nowPG, int totalData, int totalPage, List<?> res, Map<String, Object> etcdata) {

        this.page = nowPG;
        this.records = totalData;
        this.total = totalPage;
        this.rows = res;
        this.etcdata = etcdata;
    }
}
