package module.excel.poi;

import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.FileCopyUtils;
import module.file.CmnFileBiz;
import module.object.CmnDateBiz;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.util.*;

/**
 * 엑셀 관련 Business 로직
 * <p/>
 * User: 현재호
 * Date: 2016.04.26
 * Time: 오후 5:40
 */
public class CmnExcelBiz {

    private static final Logger log = LoggerFactory.getLogger(CmnExcelBiz.class); // SLF4J Logger

    /**
     * List 객체를 xlsx로 내보내기
     *
     * @param pojoObjectList List Parameter
     * @param outputFileName 출력 파일 이름
     * @param res            HttpServletResponse
     */
    public static void createListToXlsx(List pojoObjectList, String outputFileName, HttpServletResponse res) {

        try {

            Workbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();

            if (pojoObjectList.size() > 0) {

                RowFilledWithPojoHeader(pojoObjectList.get(0), sheet.createRow(0));

                for (int i = 0; i < pojoObjectList.size(); i++) {

                    Row row = sheet.createRow(i + 1);

                    RowFilledWithPojoData(pojoObjectList.get(i), row);
                }
            }
            String fileName = URLEncoder.encode(outputFileName, "utf-8");

            res.setContentType("application/vnd.ms-excel");
            res.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName + "\";");
            res.setHeader("Content-Transfer-Encoding", "binary");
            res.setHeader("Connection", "close");

            OutputStream out = res.getOutputStream();
            workbook.write(out);
            out.close();

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * List<HashMap> 객체를 xlsx로 내보내기
     *
     * @param pojoObjectList List Parameter
     * @param outputFileName 출력 파일 이름
     * @param res            HttpServletResponse
     */
    public static void createListMapToXlsx(List pojoObjectList, String outputFileName, HttpServletResponse res) {

        try {

            Workbook workbook = new SXSSFWorkbook();
            SXSSFSheet sheet = (SXSSFSheet) workbook.createSheet();

            if (pojoObjectList.size() > 0) {

                HashMap map = (HashMap) pojoObjectList.get(0);
                Row headerRow = sheet.createRow(0);

                int k = 0;
                Iterator<String> keys = map.keySet().iterator();
                while (keys.hasNext()) {
                    String key = keys.next();
                    headerRow.createCell(k++).setCellValue(key.toUpperCase());
                }

                for (int i = 0; i < pojoObjectList.size(); i++) {
                    map = (HashMap) pojoObjectList.get(i);
                    Row row = sheet.createRow(i + 1);

                    k = 0;
                    keys = map.keySet().iterator();
                    while (keys.hasNext()) {
                        String key = keys.next();
                        row.createCell(k++).setCellValue(String.valueOf(map.get(key)));
                    }
                }
            }

            String fileName = URLEncoder.encode(outputFileName, "utf-8");

            res.setContentType("application/vnd.ms-excel");
            res.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName + "\";");
            res.setHeader("Content-Transfer-Encoding", "binary");
            res.setHeader("Connection", "close");

            OutputStream out = res.getOutputStream();
            workbook.write(out);
            out.close();

        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * 보고서 양식 엑셀 xlsx로 내보내기
     *
     * @param pojoObjectList   List Parameter
     * @param sysFol           시스템 구분 폴더(ex : slm, fms...)
     * @param folFol           서브 폴더
     * @param templateFileName 템플릿 보고서 양식
     * @param sheetName        시트 이름
     * @param outputFileName   출력 파일 이름
     * @param isCalculator     계산 옵션 적용 여부
     * @param res              HttpServletResponse
     */
    public static void generateXlsx(List pojoObjectList, String sysFol, String folFol, String templateFileName, String sheetName, String outputFileName, boolean isCalculator, HttpServletResponse res) {

        try {

            CmnFileBiz fileBiz = new CmnFileBiz();

            String storageDirectory = fileBiz.createFolder(fileBiz.fileUploadDirectory, sysFol, folFol);

            File tmpOutputFile = new File(storageDirectory + "/TEMP_" + outputFileName);

            FileCopyUtils.copy(new File(storageDirectory + "/" + templateFileName), tmpOutputFile);

            if (tmpOutputFile.exists()) {

                OPCPackage opcPackage = OPCPackage.open(tmpOutputFile);
                XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
                XSSFSheet sheet = workbook.getSheet(sheetName);

                if (pojoObjectList.size() > 0) {

                    RowFilledWithPojoHeader(pojoObjectList.get(0), sheet.createRow(0));

                    for (int i = 0; i < pojoObjectList.size(); i++) {

                        Row row = sheet.createRow(i + 1);

                        RowFilledWithPojoData(pojoObjectList.get(i), row);
                    }
                }

                if (isCalculator) {

                    sheet.setForceFormulaRecalculation(true);
                }

                String fileName = URLEncoder.encode(outputFileName, "utf-8");

                res.setContentType("application/vnd.ms-excel");
                res.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName + "\";");
                res.setHeader("Content-Transfer-Encoding", "binary");
                res.setHeader("Connection", "close");

                OutputStream out = res.getOutputStream();
                workbook.write(out);
                out.close();

                opcPackage.close();

                tmpOutputFile.delete();
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * 보고서(리스트 유형) 엑셀 xlsx로 내보내기
     *
     * @param templateFileName 템플릿 보고서 양식
     * @param sheetName        시트 이름
     * @param sysFol           시스템 구분 폴더(ex : slm, fms...)
     * @param folFol           서브 폴더
     * @param outputFileName   출력 파일 이름
     * @param arrHeader        헤더 이름
     * @param arrKey           arrData의 키 코드
     * @param arrData          리스트 결과
     * @param isCalculator     계산 옵션 적용 여부
     * @param req              HttpServletRequest
     * @param res              HttpServletResponse
     */
    public static void generateXlsxList(String templateFileName, String sheetName, String sysFol, String folFol, String outputFileName, List<String> arrHeader, List<String> arrKey, List<Map<String, Object>> arrData, boolean isCalculator, HttpServletRequest req, HttpServletResponse res) {

        try {

            CmnFileBiz fileBiz = new CmnFileBiz();

            String storageDirectory = fileBiz.createFolder(fileBiz.fileUploadDirectory, sysFol, folFol);

            File tmpOutputFile = new File(storageDirectory + "/TEMP_" + outputFileName);

            FileCopyUtils.copy(new File(fileBiz.fileUploadDirectory + templateFileName), tmpOutputFile);

            if (tmpOutputFile.exists()) {

                OPCPackage opcPackage = OPCPackage.open(tmpOutputFile);
                XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
                XSSFSheet sheet = workbook.getSheet(sheetName);

                Row row = sheet.getRow(0);
                Cell cell = row.getCell(0);
                CellStyle cellStyle = cell.getCellStyle();

                int rIdx = 0, cIdx = 0;

                for (String val : arrHeader) {

                    cell = ((row.getCell(cIdx) == null) ? row.createCell(cIdx) : row.getCell(cIdx));

                    cell.setCellValue(val);
                    cell.setCellStyle(cellStyle);

                    cIdx++;
                }

                rIdx = 1;
                for (Map<String, Object> obj : arrData) {

                    row = ((sheet.getRow(rIdx) == null) ? sheet.createRow(rIdx) : sheet.getRow(rIdx));

                    cIdx = 0;
                    for (String key : arrKey) {

                        cell = ((row.getCell(cIdx) == null) ? row.createCell(cIdx) : row.getCell(cIdx));

                        if (rIdx == 1 && cIdx == 0) {

                            cellStyle = cell.getCellStyle();
                        }

                        cell.setCellValue(obj.get(key) == null ? "" : obj.get(key).toString());
                        cell.setCellStyle(cellStyle);

                        cIdx++;
                    }

                    rIdx++;
                }

                if (isCalculator) {
                    // 엑셀 서식 안에 계산 함수 실행
                    sheet.setForceFormulaRecalculation(true);
                }

                // 셀 크기 조정
                for (int i = 0; i < arrKey.size(); i++) {

                    sheet.autoSizeColumn((short) i);
                    sheet.setColumnWidth(i, (sheet.getColumnWidth(i)) + 512);  // 윗줄만으로는 컬럼의 width 가 부족하여 더 늘려야 함.
                }

                String fileName = URLEncoder.encode(outputFileName, "utf-8");

                res.setContentType("application/vnd.ms-excel");
                res.setHeader("Content-Disposition", "attachment; fileName=\"" + fileName + "\";");
                res.setHeader("Content-Transfer-Encoding", "binary");
                res.setHeader("Connection", "close");

                // 쿠키 값에 complete 상태 Update
                Cookie[] cookies = req.getCookies();

                for (Cookie ck : cookies) {

                    if (ck.getName().equals("export-end-state")) {

                        Cookie resCk = new Cookie("export-end-state", "1");
                        resCk.setPath("/");

                        res.addCookie(resCk);
                    }
                }

                OutputStream out = res.getOutputStream();
                workbook.write(out);
                out.close();

                opcPackage.close();

                tmpOutputFile.delete();
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }
    }

    /**
     * Row filled with pojo header.
     *
     * @param pojoObject Object
     * @param row        엑셀 ROW 행
     * @return Row
     */
    private static Row RowFilledWithPojoHeader(Object pojoObject, Row row) {

        try {

            Class c1 = pojoObject.getClass();

            int i = 0;
            for (Field m : c1.getDeclaredFields()) {

                row.createCell(i).setCellValue(m.getName().toUpperCase());

                i++;
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return row;
    }

    /**
     * Row filled with pojo data.
     *
     * @param pojoObject Object
     * @param row        엑셀 ROW 행
     * @return Row
     */
    private static Row RowFilledWithPojoData(Object pojoObject, Row row) {

        try {

            Class c1 = pojoObject.getClass();

            int i = 0;
            for (Field m : c1.getDeclaredFields()) {

                String cellValue;
                String returnType = m.getType().getName();

                Object tmpVal = m.get(pojoObject);

                if (returnType.equals("java.util.Date")) {

                    cellValue = (tmpVal != null ? CmnDateBiz.convertString((Date) tmpVal, "yyyy-MM-dd HH:mm:ss") : "");
                } else {

                    cellValue = (tmpVal != null ? tmpVal.toString() : "");
                }

                row.createCell(i).setCellValue(cellValue);

                i++;
            }
        } catch (Exception ex) {

            log.error(ex.toString(), ex);
        }

        return row;
    }

//    /**
//     * Gets cell value.
//     *
//     * @param row       Row
//     * @param cellIndex Cell Index
//     * @return the cell value
//     */
//    public static String getCellValue(Row row, int cellIndex) {
//
//        Cell cell = row.getCell(cellIndex);
//
//        if (cell == null) return null;
//
//        cell.setCellType(Cell.CELL_TYPE_STRING);
//
//        return cell.getStringCellValue();
//    }
//
//    /**
//     * Object -> String
//     *
//     * @param object 객체
//     * @return String
//     */
//    public static String setObjToStr(Object object) {
//
//        if (object == null) return "";
//
//        return object.toString();
//    }
}
