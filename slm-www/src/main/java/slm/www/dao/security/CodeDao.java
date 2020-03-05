package slm.www.dao.security;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.list.ListObjVO;
import slm.www.vo.security.CodeVO;

import java.util.List;

/**
 * <p/>
 * User: 현재호
 * Date: 2016.04.22
 * Time: 오전 10:00
 */
public interface CodeDao {

    /**
     * 코드구분 가져오기
     *
     * @return List<ListObjVO>
     */
    public List<ListObjVO> getCodeGubunList();

    /**
     * 코드 총 개수 가져오기
     *
     * @param srcJong 코드종류
     * @param vo jqGrid 파라미터
     * @return int 총 개수
     */
    public int getCodeListCnt(@Param("srcJong") String srcJong, @Param("vo") SrcJQGridVO vo);

    /**
     * 코드 목록 가져오기
     *
     * @param srcJong 코드종류
     * @param vo jqGrid 파라미터
     * @return List<WorkVO> 코드목록
     */
    public List<CodeVO> getCodeList(@Param("srcJong") String srcJong, @Param("vo") SrcJQGridVO vo);

    /**
     * 코드정보 Insert
     *
     * @param vo WorkVO
     * @return 쿼리 결과
     */
    public int setCodeInsert(@Param("vo") CodeVO vo);

    /**
     * 코드정보 Update
     *
     * @param vo WorkVO
     * @return 쿼리 결과
     */
    public int setCodeUpdate(@Param("vo") CodeVO vo);
}
