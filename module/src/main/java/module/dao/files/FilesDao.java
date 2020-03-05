package module.dao.files;

import org.apache.ibatis.annotations.Param;
import module.vo.files.FilesVO;

import java.util.List;

public interface FilesDao {

    /**
     * 가장 최근 SEQ 가져오기
     *
     * @return Long 라스트 SEQ
     */
    public Long getFileLastKey();

    /**
     * Max SEQ 가져오기
     *
     * @return Long 라스트 SEQ
     */
    public Long getMaxFileSeq();

    /**
     * 파일 정보 가져오기
     *
     * @param realTB  실 테이블
     * @param realSEQ 실제 SEQ
     * @param gubunCD 구분자 코드
     * @return COM_FILES DB 테이블 조회 결과
     */
    public List<FilesVO> getFileList(@Param("realTB") String realTB, @Param("realSEQ") String realSEQ, @Param("gubunCD") String gubunCD);

    /**
     * 파일 상세 정보 가져오기
     *
     * @param key SEQ
     * @return FilesVO
     */
    public FilesVO getFileInfo(@Param("key") String key);

    /**
     * 파일 DB 추가
     *
     * @param vo FilesVO
     * @return Insert 성공 건수
     */
    public int insertFile(@Param("vo") FilesVO vo);

    /**
     * 파일 DB 삭제
     *
     * @param key SEQ
     * @return 삭제 성공 건수
     */
    public int deleteFile(@Param("key") String key);
}
