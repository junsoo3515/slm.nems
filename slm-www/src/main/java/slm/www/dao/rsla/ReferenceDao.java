package slm.www.dao.rsla;

import slm.www.vo.rsla.ReferenceVO;

import java.util.List;


/**
 * SLA 보고서 > 항목평가기준
 * <p/>
 * User: 이종혁
 * Date: 2016.04.28
 * Time: 오후 1:55
 */
public interface ReferenceDao {

    /**
     * SLA 항목평가기준 리스트 가져오기
     *
     * @return SLA 항목기준 목록
     */
    public List<ReferenceVO> getSlaEva();

}
