package com.github.xyyxhcj.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Excel工具类
 *
 * @author xyyxhcj@qq.com
 * @since 2018-03-23
 */

public class ExcelUtils {
    /**
     * 用于转换
     */
    private static final DozerBeanMapper DOZER_BEAN_MAPPER = new DozerBeanMapper();

    /**
     * 生成.xlsx文件数据
     *
     * @param xlsxSource xlsxSource
     * @return return
     */
    private static XSSFWorkbook getXssfSheets(XlsxSource xlsxSource) {
        Map map = new HashMap(xlsxSource.keys.length);
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        XSSFSheet sheet = xssfWorkbook.createSheet(xlsxSource.sheetName);
        XSSFRow rowKey = sheet.createRow(0);
        XSSFRow rowKeyCn = sheet.createRow(1);
        rowKey.setZeroHeight(true);
        for (int i = 0; i < xlsxSource.keys.length; i++) {
            rowKey.createCell(i).setCellValue(xlsxSource.keys[i]);
            rowKeyCn.createCell(i).setCellValue(xlsxSource.keysCn[i]);
        }
        for (Object obj : xlsxSource.sources) {
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            if (obj instanceof Map) {
                map = (Map) obj;
            } else {
                DOZER_BEAN_MAPPER.map(obj, map);
            }
            setData(xlsxSource, map, dataRow);
        }
        return xssfWorkbook;
    }

    /**
     * 生成.xls文件数据
     *
     * @param xlsxSource xlsxSource
     * @return return
     */
    private static HSSFWorkbook getHssfSheets(XlsxSource xlsxSource) {
        Map map = new HashMap(xlsxSource.keys.length);
        HSSFWorkbook hssfWorkbook = new HSSFWorkbook();
        HSSFSheet sheet = hssfWorkbook.createSheet(xlsxSource.sheetName);
        HSSFRow rowKey = sheet.createRow(0);
        HSSFRow rowKeyCn = sheet.createRow(1);
        rowKey.setZeroHeight(true);
        for (int i = 0; i < xlsxSource.keys.length; i++) {
            rowKey.createCell(i).setCellValue(xlsxSource.keys[i]);
            rowKeyCn.createCell(i).setCellValue(xlsxSource.keysCn[i]);
        }
        for (Object obj : xlsxSource.sources) {
            HSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            if (obj instanceof Map) {
                map = (Map) obj;
            } else {
                DOZER_BEAN_MAPPER.map(obj, map);
            }
            setData(xlsxSource, map, dataRow);
        }
        return hssfWorkbook;
    }

    /**
     * 写入数据
     *
     * @param xlsxSource xlsxSource
     * @param map        map
     * @param dataRow    dataRow
     */
    private static void setData(XlsxSource xlsxSource, Map map, Row dataRow) {
        for (int i = 0; i < xlsxSource.keys.length; i++) {
            Object value = map.get(xlsxSource.keys[i]);
            if (value instanceof String) {
                dataRow.createCell(i).setCellValue((String) value);
            } else {
                dataRow.createCell(i).setCellValue(value + "");
            }
        }
    }

    /**
     * 导出xlsx
     *
     * @param xlsxSource xlsxSource
     * @throws IOException IOException
     */
    public static void export(XlsxSource xlsxSource) throws IOException {
        XSSFWorkbook xssfWorkbook = getXssfSheets(xlsxSource);
        ServletOutputStream outputStream = getServletOutputStream(xlsxSource);
        xssfWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 导出xls
     *
     * @param xlsxSource xlsxSource
     * @throws IOException IOException
     */
    public static void exportXls(XlsxSource xlsxSource) throws IOException {
        HSSFWorkbook hssfWorkbook = getHssfSheets(xlsxSource);
        ServletOutputStream outputStream = getServletOutputStream(xlsxSource);
        hssfWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 获取输出流
     *
     * @param xlsxSource xlsxSource
     * @return 输出流
     * @throws IOException IOException
     */
    private static ServletOutputStream getServletOutputStream(XlsxSource xlsxSource) throws IOException {
        ResponseUtils.setupDownLoadResponse(xlsxSource.response, xlsxSource.fileName);
        xlsxSource.response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        return xlsxSource.response.getOutputStream();
    }

    /**
     * 将Xlsx/Xls转为List
     *
     * @param multipartFile multipartFile
     * @return return
     */
    public static List<Map> getListFromExcel(MultipartFile multipartFile) {
        List<Map> records = null;
        String originalFilename = multipartFile.getOriginalFilename();
        try {
            InputStream fileInputStream = multipartFile.getInputStream();
            if (originalFilename.endsWith(".xls")) {
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
                HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
                HSSFRow row = sheet.getRow(0);
                records = getListFromSheet(sheet, row);
                fileInputStream.close();
            } else if (originalFilename.endsWith(".xlsx")) {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook(fileInputStream);
                XSSFSheet sheet = xssfWorkbook.getSheetAt(0);
                XSSFRow row = sheet.getRow(0);
                records = getListFromSheet(sheet, row);
                fileInputStream.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return records;
    }

    /**
     * @param sheet sheet
     * @param row   row
     * @return return
     */
    private static List<Map> getListFromSheet(Sheet sheet, Row row) {
        List<Map> list = new ArrayList<>();
        short cellNum = row.getLastCellNum();
        String[] strings = new String[cellNum];
        for (int i = 0; i < cellNum; i++) {
            strings[i] = row.getCell(i).getStringCellValue();
        }
        loop:
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Map<String, String> map = new HashMap<>(strings.length);
            for (int j = 0; j < cellNum; j++) {
                Cell cell = sheet.getRow(i).getCell(j);
                if (cell == null || StringUtils.isBlank(cell.toString())) {
                    if (j == 0) {
                        break loop;
                    }
                    continue;
                }
                CellType cellTypeEnum = cell.getCellTypeEnum();
                if (CellType.STRING == cellTypeEnum) {
                    map.put(strings[j], cell.getStringCellValue());
                } else if (CellType.NUMERIC == cellTypeEnum) {
                    map.put(strings[j], cell.getNumericCellValue() + "");
                }
            }
            if (!map.isEmpty()) {

                list.add(map);
            }
        }
        return list;
    }

    /**
     * 测试
     *
     * @param args args
     */
    public static void main(String[] args) {
        //模拟数据
        List<Map<String, Object>> sources = new ArrayList<>(2);
        for (int i = 0; i < 2; i++) {
            Map<String, Object> source = new HashMap<>();
            source.put("id", "C-00" + (i + 1));
            source.put("price", 199.9 + i);
            source.put("pcs", 150 + i);
            sources.add(source);
        }
        String[] keys = "id,price,pcs".split(",");
        String[] keysCn = "商品编码,价格,数量".split(",");
        XlsxSource xlsxSource = new XlsxSource(sources, keys, keysCn, null, "测试导出.xls", "TestSheet");
        HSSFWorkbook hssfWorkbook = getHssfSheets(xlsxSource);
        //实际开发中只需调用export()或exportXls()
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\temp\\1.xls");
            hssfWorkbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 封装导出Excel所需参数
     */
    public static class XlsxSource {
        private String sheetName;
        private List<Map<String, Object>> sources;
        private String[] keys;
        private String[] keysCn;
        private HttpServletResponse response;
        private String fileName;

        /**
         * 封装导出Excel所需参数
         *
         * @param sources   数据源
         * @param keys      实体类字段
         * @param keysCn    实体类中文字段
         * @param response  response
         * @param fileName  输出文件名(包含后缀)
         * @param sheetName Excel工作表名
         */
        public XlsxSource(List<Map<String, Object>> sources, String[] keys, String[] keysCn, HttpServletResponse response, String fileName, String sheetName) {
            this.sources = sources;
            this.keys = keys;
            this.keysCn = keysCn;
            this.response = response;
            this.fileName = fileName;
            this.sheetName = sheetName;
        }
    }
}
