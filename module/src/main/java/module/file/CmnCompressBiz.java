package module.file;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Stack;

/**
 * 파일 관리 Business 로직
 * <p/>
 * User: 현재호
 * Date: 16. 4. 20
 * Time: 오후 2:53
 */
public class CmnCompressBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnCompressBiz.class); // SLF4J Logger

    public String fileUploadDirectory = ResourceBundle.getBundle("config").getString("file.upload.directory");

    /**
     * 압축파일 해제
     *
     * @param zippedFile 압축풀 대상파일
     * @throws java.io.IOException
     */
    public void unzip(File zippedFile) throws IOException {

        unzip(zippedFile, Charset.defaultCharset().name());
    }

    /**
     * 압축파일 해제
     *
     * @param zippedFile  압축풀 대상파일
     * @param charsetName 캐릭터셋 지정
     * @throws java.io.IOException
     */
    public void unzip(File zippedFile, String charsetName) throws IOException {

        unzip(zippedFile, zippedFile.getParentFile(), charsetName);
    }

    /**
     * 압축파일 해제
     *
     * @param zippedFile 압축풀 대상파일
     * @param destDir    압축이 해제될 디렉토리 경로
     * @throws java.io.IOException
     */
    public void unzip(File zippedFile, File destDir) throws IOException {

        unzip(new FileInputStream(zippedFile), destDir, Charset.defaultCharset().name());
    }

    /**
     * 압축파일 해제
     *
     * @param zippedFile  압축풀 대상파일
     * @param destDir     압축이 해제될 디렉토리 경로
     * @param charsetName 캐릭터셋 지정
     * @throws java.io.IOException
     */
    public void unzip(File zippedFile, File destDir, String charsetName) throws IOException {

        unzip(new FileInputStream(zippedFile), destDir, charsetName);
    }

    /**
     * 압축파일 해제
     *
     * @param is      InputStream 압축풀 대상파일
     * @param destDir 압축이 해제될 디렉토리 경로
     * @throws java.io.IOException
     */
    public void unzip(InputStream is, File destDir) throws IOException {

        unzip(is, destDir, Charset.defaultCharset().name());
    }

    /**
     * 압축파일 해제
     *
     * @param is          InputStream 압축풀 대상파일
     * @param destDir     압축이 해제될 디렉토리 경로
     * @param charsetName 캐릭터셋 지정
     * @throws java.io.IOException
     */
    public void unzip(InputStream is, File destDir, String charsetName) throws IOException {

        ZipArchiveInputStream zis;
        ZipArchiveEntry entry;
        String name;
        File target;
        int nWritten = 0;
        BufferedOutputStream bos;
        byte[] buf = new byte[1024 * 8];

        ensureDestDir(destDir);

        zis = new ZipArchiveInputStream(is, charsetName, false);

        while ((entry = zis.getNextZipEntry()) != null) {

            name = entry.getName();
            target = new File(destDir, name);

            if (entry.isDirectory()) {

                ensureDestDir(target);
            } else {

                target.createNewFile();
                bos = new BufferedOutputStream(new FileOutputStream(target));

                while ((nWritten = zis.read(buf)) >= 0) {

                    bos.write(buf, 0, nWritten);
                }

                bos.close();
                log.debug("file : " + name);
            }
        }
        zis.close();
    }

    /**
     * compresses the given file(or dir) and creates new file under the same directory.
     *
     * @param src 압축할 파일 또는 디렉터리 경로
     * @throws java.io.IOException
     */
    public void zip(File src) throws IOException {

        zip(src, Charset.defaultCharset().name(), true);
    }

    /**
     * zips the given file(or dir) and create
     *
     * @param src        압축할 파일 또는 디렉터리 경로
     * @param includeSrc 압축시 루트디렉터리 안에 압축할지 여부
     * @throws java.io.IOException
     */
    public void zip(File src, boolean includeSrc) throws IOException {

        zip(src, Charset.defaultCharset().name(), includeSrc);
    }

    /**
     * compresses the given src file (or directory) with the given encoding
     *
     * @param src         압축할 파일 또는 디렉터리 경로
     * @param charSetName 캐릭터셋 지정
     * @param includeSrc  압축시 루트디렉터리 안에 압축할지 여부
     * @throws java.io.IOException
     */
    public void zip(File src, String charSetName, boolean includeSrc) throws IOException {

        zip(src, src.getParentFile(), charSetName, includeSrc);
    }

    /**
     * compresses the given src file(or directory) and writes to the given output stream.
     *
     * @param src 압축할 파일 또는 디렉터리 경로
     * @param os
     * @throws java.io.IOException
     */
    public void zip(File src, OutputStream os) throws IOException {

        zip(src, os, Charset.defaultCharset().name(), true);
    }

    /**
     * compresses the given src file(or directory) and create the compressed file under the given destDir.
     *
     * @param src         압축할 파일
     * @param destDir     압축파일 생성 디렉토리 경로
     * @param charSetName 캐릭터셋 지정
     * @param includeSrc  압축시 루트디렉터리 안에 압축할지 여부
     * @throws java.io.IOException
     */
    public void zip(File src, File destDir, String charSetName, boolean includeSrc) throws IOException {

        String fileName = src.getName();

        if (!src.isDirectory()) {

            int pos = fileName.lastIndexOf(".");

            if (pos > 0) {

                fileName = fileName.substring(0, pos);
            }
        }

        fileName += ".zip";

        ensureDestDir(destDir);

        File zippedFile = new File(destDir, fileName);

        if (!zippedFile.exists()) zippedFile.createNewFile();

        zip(src, new FileOutputStream(zippedFile), charSetName, includeSrc);
    }

    public void zip(File src, OutputStream os, String charsetName, boolean includeSrc) throws IOException {

        List<File> one = new ArrayList<File>();

        one.add(src);

        zip(one, os, charsetName, includeSrc);
    }

    /**
     * Zip void.
     *
     * @param src         압축할 파일 또는 디렉터리 경로
     * @param os          FileOutputStream
     * @param charsetName 캐릭터셋 지정
     * @param includeSrc  압축시 루트디렉터리 안에 압축할지 여부
     * @throws java.io.IOException the iO exception
     */
    public void zip(List<File> src, OutputStream os, String charsetName, boolean includeSrc) throws IOException {

        ZipArchiveOutputStream zos = new ZipArchiveOutputStream(os);
        zos.setEncoding(charsetName);
        FileInputStream fis;

        int length;
        ZipArchiveEntry ze;
        byte[] buf = new byte[8 * 1024];
        String name;

        Stack<File> stack = new Stack<File>();
        File root = null;

        for (int i = 0; i < src.size(); i++) {

            if (src.get(i).isDirectory()) {

                if (includeSrc) {

                    stack.push(src.get(i));
                    root = src.get(i).getParentFile();
                } else {

                    File[] fs = src.get(i).listFiles();

                    for (int j = 0; j < fs.length; j++) {
                        stack.push(fs[j]);
                    }

                    root = src.get(i);
                }
            } else {

                stack.push(src.get(i));
                root = src.get(i).getParentFile();
            }
        }

        while (!stack.isEmpty()) {

            File f = stack.pop();

            name = toPath(root, f);

            if (f.isDirectory()) {

                log.debug("dir : " + name);
                File[] fs = f.listFiles();

                for (int i = 0; i < fs.length; i++) {

                    if (fs[i].isDirectory()) {

                        stack.push(fs[i]);
                    } else {

                        stack.add(0, fs[i]);
                    }
                }
            } else {

                log.debug("file : " + name);
                ze = new ZipArchiveEntry(name);
                zos.putArchiveEntry(ze);
                fis = new FileInputStream(f);

                while ((length = fis.read(buf, 0, buf.length)) >= 0) {

                    zos.write(buf, 0, length);
                }

                fis.close();
                zos.closeArchiveEntry();
            }
        }

        zos.close();
    }

    private String toPath(File root, File dir) {

        String path = dir.getAbsolutePath();
        path = path.substring(root.getAbsolutePath().length()).replace(File.separatorChar, '/');

        if (path.startsWith("/")) {

            path = path.substring(1);
        }

        if (dir.isDirectory() && !path.endsWith("/")) {

            path += "/";
        }

        return path;
    }

    private void ensureDestDir(File dir) throws IOException {

        if (!dir.exists()) {

            dir.mkdirs();
            /*  does it always work? */
            log.debug("dir  : " + dir);
        }

    }
}