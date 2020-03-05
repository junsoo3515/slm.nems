package slm.www.dao.security;

import org.apache.ibatis.annotations.Param;
import module.vo.jqgrid.SrcJQGridVO;
import module.vo.menu.MenuAuthVO;
import slm.www.vo.security.AuthorityVO;

import java.util.List;

/**
 * 권한 관리
 * <p/>
 * User: 현재호
 * Date: 2016.04.18
 * Time: 오전 10:21
 */
public interface AuthorityDao {

    /**
     * 권한 총 개수 가져오기
     *
     * @param srcAuth 권한 명칭
     * @param srcEtc 권한설명
     * @param vo jqGrid 파라미터
     * @return 총 개수
     */
    public int getAuthListCnt(@Param("srcAuth") String srcAuth, @Param("srcEtc") String srcEtc, @Param("vo") SrcJQGridVO vo);

    /**
     * 권한 목록 가져오기
     *
     * @param srcAuth 권한코드
     * @param srcEtc 권한설명
     * @param vo jqGrid 파라미터
     * @return 권한 목록
     */
    public List<AuthorityVO> getAuthList(@Param("srcAuth") String srcAuth, @Param("srcEtc") String srcEtc, @Param("vo") SrcJQGridVO vo);

    /**
     * 권한코드 중복확인
     *
     * @param key 권한코드
     * @return 권한코드 존재 개수
     */
    public int getAuthChk(@Param("key") String key);

    /**
     * 권한 정보 Insert/Update 처리
     *   : COM_MEM_AUTH
     *   : MERGE INTO로 처리
     *
     * @param vo the vo
     * @return the auth act
     */
    public int setAuthAct(@Param("vo") AuthorityVO vo);

    /**
     * 메뉴접근 설정 데이터 존재 유무 판단
     *
     * @param key 권한 코드
     * @return COM_MEM_MNU 존재 개수
     */
    public int getAuthMenuCnt(@Param("key") String key);

    /**
     * 메뉴접근 설정 Insert
     *
     * @param firstSeq 가장 처음 들어가는 고유 SEQ
     * @param authCd 권한 코드
     * @param listData List<MenuAuthVO>
     * @return 쿼리 결과
     */
    public int setAuthMenuInsert(@Param("firstSeq") long firstSeq, @Param("authCd") String authCd, @Param("listData") List<MenuAuthVO> listData);

    /**
     * 메뉴접근 설정 Update
     *
     * @param authCd 권한 코드
     * @param vo MenuAuthVO
     * @return 쿼리 결과
     */
    public int setAuthMenuUpdate(@Param("authCd") String authCd, @Param("vo") MenuAuthVO vo);
}
