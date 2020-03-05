package slm.www.ctr.cmn.files;

import com.fasterxml.jackson.core.JsonEncoding;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.session.SqlSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import module.dao.files.FilesDao;
import module.file.CmnCompressBiz;
import module.file.CmnFileBiz;
import module.secure.filter.CmnFilterBiz;
import module.vo.files.FilesVO;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.*;

/**
 * 첨부파일 관련 컨트롤
 * <p/>
 * User: 현재호
 * Date: 2016.04.20
 * Time: 오후 2:43
 */
@Controller
@RequestMapping("/files")
public class FilesCtr {

    private static final Logger log = LoggerFactory.getLogger(FilesCtr.class);

    @Autowired
    private SqlSession sqlSession;

    public FilesDao getDao() {

        return sqlSession.getMapper(FilesDao.class);
    }

    @RequestMapping(value = "/getData/{rootPath}/{tableKey}/{seq}/{gubunKey}", method = RequestMethod.GET)
    @ResponseBody
    public Map list(HttpServletRequest req, @PathVariable String rootPath, @PathVariable String tableKey, @PathVariable String seq, @PathVariable String gubunKey) {

        log.debug("uploadGet called");

        CmnFileBiz fileBiz = new CmnFileBiz();

        List<FilesVO> list = getDao().getFileList(tableKey, seq, gubunKey);

        for (FilesVO vo : list) {
            // json 결과 저장
            vo.real_tb = tableKey;
            vo.real_seq = seq;
            vo.gubun_cd = gubunKey;

            vo.setName(vo.file_title);
            vo.setNewFilename(vo.file_nm);
            vo.setContentType(vo.file_type);
            vo.setSize(Long.valueOf(vo.file_size));
            //vo.setUrl("/" + rootPath + fileBiz.fileUploadDirectory + vo.file_url + "/" + vo.file_nm);
            vo.setUrl("/" + rootPath + "/files/download/" + vo.getFiles_seq());

            if (vo.file_type.indexOf("image") > -1) {
                vo.setThumbnailSize(Long.valueOf(vo.file_size));
                vo.setThumbnailFilename(vo.file_nm);
                vo.setThumbnailUrl(vo.getUrl());
            }

            vo.setDeleteUrl("/" + rootPath + "/files/delete/" + vo.getFiles_seq());
            vo.setDeleteType("DELETE");
        }

        Map<String, Object> files = new HashMap<>();
        files.put("files", list);

        log.debug("Returning: {}", files);

        return files;
    }

    /**
     * File Upload(Single)
     *
     * @param req MultipartHttpServletRequest auto passed
     * @return Map<String, List<FilesVO>> json Format
     */
    @RequestMapping(value = "/singleUpload", method = RequestMethod.POST) // , headers = "Accept=application/json"
    @ResponseBody
    public long singleUpload(MultipartHttpServletRequest req, HttpServletResponse res) {

        log.debug("uploadPost called");

        long ret = 0;

//        List<FilesVO> arrList = new ArrayList<FilesVO>();

        // 1. build an iterator
        Iterator<String> itr = req.getFileNames();
        MultipartFile mf;
        List<FilesVO> list = new LinkedList<>();

        CmnFileBiz fileBiz = new CmnFileBiz();

        String rootFolder = req.getParameter("rootPath");
        String sysFolder = req.getParameter("systemPath");
        String folFolder = req.getParameter("folPath");

        String filePath = ""; // 프로젝트 경로-파일 Upload Root

        if (StringUtils.isEmpty(sysFolder) == false) {
            filePath += "/" + sysFolder;
        }
        if (StringUtils.isEmpty(folFolder) == false) {
            filePath += "/" + folFolder;
        }

        String storageDirectory = fileBiz.createFolder(req, sysFolder, folFolder);

        //2. get each file
        while (itr.hasNext()) {

            //2.1 get next MultipartFile
            mf = req.getFile(itr.next());
            log.debug("Uploading {}", mf.getOriginalFilename());

            String newFilenameBase = UUID.randomUUID().toString();
            String originalFileExtension = mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf("."));
            String newFilename = newFilenameBase + originalFileExtension;

            File newFile = new File(storageDirectory + "/" + newFilename);

            try {

                mf.transferTo(newFile); // 파일 물리경로에 저장

//                // TODO thumbnail 이미지 생성 하는 건데... 우선 제외..
//                BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile), 60);
//                String thumbnailFilename = newFilenameBase + "-thumbnail.png";
//                File thumbnailFile = new File(storageDirectory + "/" + thumbnailFilename);
//                ImageIO.write(thumbnail, "png", thumbnailFile);

                // JSON으로 리턴 결과 보내기 위한 값 Setting
                FilesVO filesvo = new FilesVO();

                // DB에 저장
                filesvo.file_type = mf.getContentType();
                filesvo.file_title = mf.getOriginalFilename();
                filesvo.file_nm = newFilename;
                filesvo.file_size = String.valueOf(mf.getSize());
                filesvo.file_url = filePath;
                filesvo.real_tb = req.getParameter("tableKey");
                filesvo.real_seq = req.getParameter("seq");
                filesvo.gubun_cd = req.getParameter("gubunKey");

                getDao().insertFile(filesvo);

                ret = getDao().getFileLastKey();

                filesvo = null;

            } catch (Exception e) {

                log.error("Could not upload file " + mf.getOriginalFilename(), e);
            }
        }

        return ret;
    }

    /**
     * File Upload(Multi)
     *
     * @param req MultipartHttpServletRequest auto passed
     * @return Map<String, List<FilesVO>> json Format
     */
    @RequestMapping(value = "/upload", method = RequestMethod.POST) // , headers = "Accept=application/json"
    @ResponseBody
    public void upload(MultipartHttpServletRequest req, HttpServletResponse res) {

        log.debug("uploadPost called");

//        List<FilesVO> arrList = new ArrayList<FilesVO>();

        // 1. build an iterator
        Iterator<String> itr = req.getFileNames();
        MultipartFile mf;
        List<FilesVO> list = new LinkedList<>();

        CmnFileBiz fileBiz = new CmnFileBiz();

        String rootFolder = req.getParameter("rootPath");
        String sysFolder = req.getParameter("systemPath");
        String folFolder = req.getParameter("folPath");

        String filePath = ""; // 프로젝트 경로-파일 Upload Root

        if (StringUtils.isEmpty(sysFolder) == false) {
            filePath += "/" + sysFolder;
        }
        if (StringUtils.isEmpty(folFolder) == false) {
            filePath += "/" + folFolder;
        }

        String storageDirectory = fileBiz.createFolder(req, sysFolder, folFolder);

        //2. get each file
        while (itr.hasNext()) {

            //2.1 get next MultipartFile
            mf = req.getFile(itr.next());
            log.debug("Uploading {}", mf.getOriginalFilename());

            String newFilenameBase = UUID.randomUUID().toString();
            String originalFileExtension = mf.getOriginalFilename().substring(mf.getOriginalFilename().lastIndexOf("."));
            String newFilename = newFilenameBase + originalFileExtension;

            File newFile = new File(storageDirectory + "/" + newFilename);

            try {

                mf.transferTo(newFile); // 파일 물리경로에 저장

//                // TODO thumbnail 이미지 생성 하는 건데... 우선 제외..
//                BufferedImage thumbnail = Scalr.resize(ImageIO.read(newFile), 60);
//                String thumbnailFilename = newFilenameBase + "-thumbnail.png";
//                File thumbnailFile = new File(storageDirectory + "/" + thumbnailFilename);
//                ImageIO.write(thumbnail, "png", thumbnailFile);

                // JSON으로 리턴 결과 보내기 위한 값 Setting
                FilesVO filesvo = new FilesVO();

                // DB에 저장
                filesvo.file_type = mf.getContentType();
                filesvo.file_title = mf.getOriginalFilename();
                filesvo.file_nm = newFilename;
                filesvo.file_size = String.valueOf(mf.getSize());
                filesvo.file_url = filePath;
                filesvo.real_tb = req.getParameter("tableKey");
                filesvo.real_seq = req.getParameter("seq");
                filesvo.gubun_cd = req.getParameter("gubunKey");

                getDao().insertFile(filesvo);
                filesvo.setFiles_seq(getDao().getFileLastKey());

                // json 결과 저장
                filesvo.setName(filesvo.file_title);
                filesvo.setNewFilename(filesvo.file_nm);
                filesvo.setContentType(filesvo.file_type);
                filesvo.setSize(Long.valueOf(filesvo.file_size));
                //filesvo.setUrl(rootFolder + fileBiz.fileUploadDirectory + "/" + filePath + "/" + newFilename);
                filesvo.setUrl(rootFolder + "/files/download/" + filesvo.getFiles_seq());

                if (filesvo.file_type.indexOf("image") > -1) {
                    filesvo.setThumbnailFilename(filesvo.getNewFilename()); // thumbnailFilename
                    filesvo.setThumbnailSize(filesvo.getSize()); // thumbnailFile.length()
                    filesvo.setThumbnailUrl(filesvo.getUrl()); // rootFolder + fileBiz.fileUploadDirectory + "/" + filePath + "/" + thumbnailFilename
                }

                filesvo.setDeleteUrl(rootFolder + "/files/delete/" + filesvo.getFiles_seq());
                filesvo.setDeleteType("DELETE");

                list.add(filesvo);

                filesvo = null;

            } catch (Exception e) {

                log.error("Could not upload file " + mf.getOriginalFilename(), e);
            }
        }

        Map<String, Object> files = new HashMap<>();
        files.put("files", list);

        if (req.getHeader("accept").indexOf("application/json") != -1) {

            res.setContentType("application/json; charset=UTF-8");
        } else {
            // IE workaround
            res.setContentType("text/plain; charset=UTF-8");
            res.setCharacterEncoding("UTF-8");
            res.setHeader("Content-Type", "text/plain; charset=UTF-8");
        }

        // ie9 이하에서 되게 하기 위해서... 아래와 같이 변경.. ㅜ.ㅜ;; 3일 삽질 끝에...
        ObjectMapper mapper = new ObjectMapper();

        try {

            JsonGenerator generator = mapper.getJsonFactory().createJsonGenerator(res.getOutputStream(), JsonEncoding.UTF8);
            mapper.writeValue(generator, files);
            generator.flush();

        } catch (Exception e) {

            log.error(e.toString(), e);
        }
//        return files;
    }

    @RequestMapping(value = "/download/{id}", method = RequestMethod.GET)
    public void download(HttpServletResponse res, HttpServletRequest req, @PathVariable Long id) {

        CmnFileBiz fileBiz = new CmnFileBiz();

        try {

            fileBiz.download(getDao(), id.toString(), res, req);

        } catch (Exception e) {

            log.error(e.toString(), e);
        }
    }

    /**
     * File Delete
     *
     * @param req HttpServletRequest
     * @param id  파일 SEQ
     * @return the list
     */
    @RequestMapping(value = "/delete/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public List delete(HttpServletRequest req, @PathVariable Long id) {

        boolean isSuccess = false;
        CmnFileBiz fileBiz = new CmnFileBiz();

        FilesVO files = getDao().getFileInfo(id.toString()); // 파일 정보 불러오기

        String fileExt = files.file_nm.substring(files.file_nm.lastIndexOf("."));

        files.file_nm = CmnFilterBiz.filterFileUrlString(files.file_nm.replace(fileExt, ""));

        File f = new File(fileBiz.getAbsoluteDirectory(req) + files.file_url + "/" + files.file_nm + fileExt);

        if (f.exists()) {

            f.delete();

//        File thumbnailFile = new File(files.file_url + "/" + files.file_nm);
//        thumbnailFile.delete();
        }

        isSuccess = (getDao().deleteFile(id.toString()) > 0 ? true : false);

        List<Map<String, Object>> results = new ArrayList<>();

        Map<String, Object> success = new HashMap<>();
        success.put("success", isSuccess);
        results.add(success);

        return results;
    }

    /**
     * File All Delete
     *
     * @param req HttpServletRequest
     * @param rvo FilesVO
     * @return the list
     */
    @RequestMapping(value = "/deleteAll", method = RequestMethod.POST)
    @ResponseBody
    public int deleteAll(HttpServletRequest req, @RequestBody FilesVO rvo) {

        CmnFileBiz fileBiz = new CmnFileBiz();

        // DB에서 파일 찾아서 파일 삭제
        List<FilesVO> delList = getDao().getFileList(rvo.real_tb, rvo.real_seq, rvo.gubun_cd);

        for (FilesVO vo : delList) {

            try {

                String fileExt = vo.file_nm.substring(vo.file_nm.lastIndexOf("."));

                vo.file_nm = CmnFilterBiz.filterFileUrlString(vo.file_nm.replace(fileExt, ""));

                File f = new File(fileBiz.getAbsoluteDirectory(req) + vo.file_url + "/" + vo.file_nm + fileExt);

                if (f.exists()) {

                    f.delete(); // 물리 경로 삭제

                    getDao().deleteFile(vo.getFiles_seq().toString()); // DB 파일 삭제
                }

                return 1;
            } catch (Exception e) {

                log.debug("파일 삭제 실패");
            }
        }

        return 0;
    }

    @RequestMapping(value = "/compress", method = RequestMethod.POST)
    @ResponseBody
    public int compress(HttpServletRequest req, @RequestBody FilesVO rvo) {

        CmnFileBiz fileBiz = new CmnFileBiz();

        // DB에서 압축 파일 찾아서 파일 삭제
        List<FilesVO> delList = getDao().getFileList(rvo.real_tb, rvo.real_seq, "Z");

        for (FilesVO vo : delList) {

            try {

                String fileExt = vo.file_nm.substring(vo.file_nm.lastIndexOf("."));

                vo.file_nm = CmnFilterBiz.filterFileUrlString(vo.file_nm.replace(fileExt, ""));

                File f = new File(fileBiz.getAbsoluteDirectory(req) + vo.file_url + "/" + vo.file_nm + fileExt);

                if (f.exists()) {

                    f.delete(); // 물리 경로 삭제

                    getDao().deleteFile(vo.getFiles_seq().toString()); // DB 파일 삭제
                }
            } catch (Exception e) {

                log.debug("파일 삭제 실패");
            }
        }

        // 압축파일 생성 및 DB 추가
        List<FilesVO> zipList = getDao().getFileList(rvo.real_tb, rvo.real_seq, rvo.gubun_cd);

        if (zipList.size() > 0) {

            String filePath = "";
            List<File> tmpList = new ArrayList<File>();

            for (FilesVO vo : zipList) {

                filePath = vo.file_url;
                tmpList.add(new File(fileBiz.getAbsoluteDirectory(req) + vo.file_url + "/" + vo.file_nm));
            }

            zipList = null;

            if (tmpList.size() > 0) {

                try {

                    CmnCompressBiz comp = new CmnCompressBiz();

                    File newFile = new File(fileBiz.getAbsoluteDirectory(req) + filePath + "/" + UUID.randomUUID().toString() + ".zip");

                    comp.zip(tmpList, new FileOutputStream(newFile), Charset.defaultCharset().name(), false);

                    FilesVO filesvo = new FilesVO();
                    int maxSeq = Integer.valueOf(String.valueOf(getDao().getMaxFileSeq()));

                    // DB에 저장
                    filesvo.setFiles_seq(Long.valueOf(maxSeq));
                    filesvo.file_type = "application/zip";
                    filesvo.file_title = newFile.getName();
                    filesvo.file_nm = filesvo.file_title;
                    filesvo.file_size = String.valueOf(newFile.length());
                    filesvo.file_url = filePath;
                    filesvo.real_tb = rvo.real_tb;
                    filesvo.real_seq = rvo.real_seq;
                    filesvo.gubun_cd = "Z";

                    getDao().insertFile(filesvo);

                    filesvo = null;
                    tmpList = null;

                    return maxSeq;
                } catch (Exception e) {

                    log.error("압축 파일 생성 실패");
                } finally {

                    tmpList = null;
                }
            }
        }

        return 0;
    }
}
