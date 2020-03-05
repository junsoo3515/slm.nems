package module.vo.files;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Date;

/**
 * 파일 관련 VO
 * User: 현재호
 * Date: 16. 4. 20
 * Time: 오후 2:45
 */
@JsonIgnoreProperties({"file_type", "file_title", "file_nm", "file_size", "file_url", "reg_dts", "thumbnailFilename", "newFilename", "contentType"})
public class FilesVO {

    // DB 칼럼
    private Long files_seq;
    public String real_tb;
    public String real_seq;
    public String gubun_cd;
    public String file_type;
    public String file_title;
    public String file_nm;
    public String file_size;
    public String file_url;
    public Date reg_dts;

    private String name;
    private String thumbnailFilename;
    private String newFilename;
    private String contentType;
    private Long size;
    private Long thumbnailSize;
    private String url;
    private String thumbnailUrl;
    private String deleteUrl;
    private String deleteType;

    public Long getFiles_seq() {
        return files_seq;
    }

    public void setFiles_seq(Long files_seq) {
        this.files_seq = files_seq;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getThumbnailFilename() {
        return thumbnailFilename;
    }

    public void setThumbnailFilename(String thumbnailFilename) {
        this.thumbnailFilename = thumbnailFilename;
    }

    public String getNewFilename() {
        return newFilename;
    }

    public void setNewFilename(String newFilename) {
        this.newFilename = newFilename;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getThumbnailSize() {
        return thumbnailSize;
    }

    public void setThumbnailSize(Long thumbnailSize) {
        this.thumbnailSize = thumbnailSize;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getDeleteUrl() {
        return deleteUrl;
    }

    public void setDeleteUrl(String deleteUrl) {
        this.deleteUrl = deleteUrl;
    }

    public String getDeleteType() {
        return deleteType;
    }

    public void setDeleteType(String deleteType) {
        this.deleteType = deleteType;
    }
}
