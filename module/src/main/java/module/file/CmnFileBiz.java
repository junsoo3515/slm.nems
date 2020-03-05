package module.file;

import module.dao.files.FilesDao;
import module.secure.filter.CmnFilterBiz;
import module.vo.files.FilesVO;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ResourceBundle;
import java.util.UUID;


/**
 * 파일 관리 Business 로직
 * <p/>
 * User: 현재호
 * Date: 13. 8. 27
 * Time: 오후 1:42
 */
public class CmnFileBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnFileBiz.class); // SLF4J Logger

    public String fileUploadDirectory = ResourceBundle.getBundle("config").getString("file.upload.directory");
    public long MAX_FILE_SIZE = Long.valueOf(ResourceBundle.getBundle("config").getString("file.upload.maxsize"));

    /**
     * Gets absolute directory.
     *
     * @param req the req
     * @return the absolute directory
     */
    public String getAbsoluteDirectory(MultipartHttpServletRequest req) {

        return fileUploadDirectory;
        //return req.getSession().getServletContext().getRealPath(fileUploadDirectory);
    }

    /**
     * Gets absolute directory.
     *
     * @param req the req
     * @return the absolute directory
     */
    public String getAbsoluteDirectory(HttpServletRequest req) {

        return fileUploadDirectory;
        //return req.getSession().getServletContext().getRealPath(fileUploadDirectory);
    }

    /**
     * 폴더 생성
     *
     * @param gFolder   시스템 구분 폴더(ex : oms, fms...)
     * @param subFolder 서브 폴더
     * @return String 최종 폴더
     */
    public String createFolder(MultipartHttpServletRequest req, String gFolder, String subFolder) {

        // ROOT 저장경로 체크
        String targetPath = getAbsoluteDirectory(req);

        return createFolder(targetPath, gFolder, subFolder);
    }

    /**
     * 폴더 생성
     *
     * @param targetPath 설정 된 root 폴더
     * @param gFolder   시스템 구분 폴더(ex : oms, fms...)
     * @param subFolder 서브 폴더
     * @return String 최종 폴더
     */
    public String createFolder(String targetPath, String gFolder, String subFolder) {

        File savePath = new File(targetPath);

        if (!savePath.exists()) {
            savePath.mkdir();
        }

        if (StringUtils.isEmpty(gFolder) == false) {

            savePath = new File(savePath.getAbsolutePath() + File.separator + gFolder);

            if (!savePath.exists()) {
                savePath.mkdir();
            }
        }

        if (StringUtils.isEmpty(subFolder) == false) {

            savePath = new File(savePath.getAbsolutePath() + File.separator + subFolder);

            if (!savePath.exists()) {
                savePath.mkdir();
            }
        }

        return savePath.getAbsolutePath();
    }

    /**
     * 단일 파일 업로드
     *
     * @param dao 파일 dao
     * @param req MultipartHttpServletRequest
     * @param mf MultipartFile
     * @param sysFol 시스템 구분 폴더(ex : oms, fms...)
     * @param folFol 서브 폴더
     * @param realTb 실제 테이블 명
     * @param realSeq 실제 테이블 고유 키
     * @param gubunCd 첨부파일 DB의 종류 코드
     */
    public void upload(FilesDao dao, MultipartHttpServletRequest req, MultipartFile mf, String sysFol, String folFol, String realTb, String realSeq, String gubunCd) {

        log.debug("Single File uploadPost called");

        String filePath = ""; // 프로젝트 경로-파일 Upload Root

        if (StringUtils.isEmpty(sysFol) == false) {
            filePath += "/" + sysFol;
        }
        if (StringUtils.isEmpty(folFol) == false) {
            filePath += "/" + folFol;
        }

        String storageDirectory = createFolder(req, sysFol, folFol);

        String newFilenameBase = UUID.randomUUID().toString();
        String originalFileExtension = mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf("."));
        String newFilename = newFilenameBase + originalFileExtension;

        // 파일 확장자 체크
        if ( CmnFilterBiz.isExecutableFileType(originalFileExtension) == true ) {

            log.error("실행가능한 첨부파일은 파일을 업로드 할 수 없습니다. : " + mf.getOriginalFilename());
            return;
        }
        // 최대 파일 개당 사이즈 체크
        if ( mf.getSize() >= MAX_FILE_SIZE ) {

            log.error("최대 파일 허용 용량을 초과하였습니다. : " + mf.getSize());
            return;
        }

        File newFile = new File(storageDirectory + "/" + newFilename);

        try {

            if (mf.getSize() > 0) {

                mf.transferTo(newFile); // 파일 물리경로에 저장

                // JSON으로 리턴 결과 보내기 위한 값 Setting
                FilesVO filesvo = new FilesVO();

                // DB에 저장
                filesvo.file_type = mf.getContentType();
                filesvo.file_title = mf.getOriginalFilename();
                filesvo.file_nm = newFilename;
                filesvo.file_size = String.valueOf(mf.getSize());
                filesvo.file_url = filePath;
                filesvo.real_tb = realTb;
                filesvo.real_seq = realSeq;
                filesvo.gubun_cd = gubunCd;

                dao.insertFile(filesvo);
            }

        } catch (Exception e) {

            log.error(e.toString(), e);
        } finally {

            newFile = null;
        }
    }

    public void download(FilesDao dao, String fileKey, HttpServletResponse res, HttpServletRequest req) {

        FilesVO files;

        try {

            files = dao.getFileInfo(fileKey);

            if(files != null) {

                String fileExt = files.file_nm.substring(files.file_nm.lastIndexOf("."));

                files.file_nm = CmnFilterBiz.filterFileUrlString(files.file_nm.replace(fileExt, "")); // 파일명에서 경로 관련 특수문자 제거

                String file = getAbsoluteDirectory(req) + files.file_url + "/" + URLDecoder.decode(files.file_nm + fileExt, "UTF-8");

                File f = new File(file);

                if (f.exists()) {
                    // TODO 개선필요, POI 다운로드 작업시 개선.
//                String fileName = URLEncoder.encode(f.getName(), "utf-8");
                    String fileName = URLEncoder.encode(files.file_title, "utf-8");

                    res.setHeader("Content-Disposition", String.format("attachment; filename=\"%s\";", fileName));
                    res.setHeader("Content-Transfer-Encoding", "binary");

                    FileCopyUtils.copy(new FileInputStream(f), res.getOutputStream());

                } else {

                    log.debug(String.format("파일없음 : %s", f.getAbsolutePath()));
                }
            } else {

                log.warn("파일없음");
            }

        } catch (Exception e) {

            log.error(e.toString(), e);
        }
    }
}