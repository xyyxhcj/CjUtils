package com.github.xyyxhcj.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
     * 生成Excel文件数据
     *
     * @param xlsxSource xlsxSource
     * @param workbook   XSSFWorkbook to xlsx;HSSFWorkbook to xls
     */
    private static void setSheets(XlsxSource xlsxSource, Workbook workbook) {
        Sheet sheet = workbook.createSheet(xlsxSource.sheetName);
        Row rowKey = sheet.createRow(0);
        Row rowKeyCn = sheet.createRow(1);
        rowKey.setZeroHeight(true);
        //POI中的行高＝Excel的行高度*20
        rowKeyCn.setHeight((short) 400);
        //创建首行的单元格样式
        CellStyle cellStyle = initCellStyle(workbook);
        //设置背景色
        cellStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        for (int i = 0; i < xlsxSource.keys.length; i++) {
            rowKey.createCell(i).setCellValue(xlsxSource.keys[i]);
            Cell rowKeyCnCell = rowKeyCn.createCell(i);
            if (xlsxSource.keysCn != null && i < xlsxSource.keysCn.length) {
                rowKeyCnCell.setCellValue(xlsxSource.keysCn[i]);
            }
            //设置首行的单元格样式
            rowKeyCnCell.setCellStyle(cellStyle);
        }
        fillData(xlsxSource, workbook, sheet);

    }

    private static void fillData(XlsxSource xlsxSource, Workbook workbook, Sheet sheet) {
        CellStyle cellStyle;
        //创建奇数行数据单元格样式
        cellStyle = initCellStyle(workbook);
        //设置背景色
        cellStyle.setFillForegroundColor(IndexedColors.LIGHT_GREEN.getIndex());
        //创建偶数行单元格样式
        CellStyle evenCellStyle = initCellStyle(workbook);
        evenCellStyle.setFillPattern(FillPatternType.NO_FILL);
        List sources = xlsxSource.sources;
        int begin = sheet.getLastRowNum();
        DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();
        for (int i = 0; i < sources.size(); i++) {
            Map map = null;
            if (sources.get(i) instanceof Map) {
                map = (Map) sources.get(i);
            } else {
                map = new HashMap<>();
                dozerBeanMapper.map(sources.get(i), map);
            }
            int lastRowNum = sheet.getLastRowNum();
            if (lastRowNum % 65534 == 0 && lastRowNum != 0) {
                Sheet nextSheet = workbook.createSheet();
                for (int j = 0; j <= begin; j++) {
                    Row row = sheet.getRow(j);
                    Row nextSheetRow = nextSheet.createRow(j);
                    for (int k = 0; k < row.getLastCellNum(); k++) {
                        Cell nextSheetRowCell = nextSheetRow.createCell(k);
                        Cell cell = row.getCell(k);
                        nextSheetRowCell.setCellValue(cell.getStringCellValue());
                        nextSheetRowCell.setCellStyle(cell.getCellStyle());
                        if (j == 0) {
                            int columnWidth = sheet.getColumnWidth(k);
                            if (columnWidth != 2048) {
                                nextSheet.setColumnWidth(k, columnWidth);
                            }
                        }
                    }
                    nextSheetRow.setHeight(row.getHeight());
                }
                sheet = nextSheet;
                lastRowNum = sheet.getLastRowNum();
            }
            Row dataRow = sheet.createRow(lastRowNum + 1);
            //POI中的行高＝Excel的行高度*20
            //Excel的行高度=POI中的行高/20
            dataRow.setHeight((short) 400);
            if (i % 2 == 1) {
                setData(xlsxSource, map, dataRow, cellStyle);
            } else {
                setData(xlsxSource, map, dataRow, evenCellStyle);
            }
        }
    }

    /**
     * 初始化单元格样式
     *
     * @param workbook workbook
     */
    private static CellStyle initCellStyle(Workbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle();
        //设置单元格填充样式,SOLID_FOREGROUND:使用前景颜色纯色填充
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        //设置边框
        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderTop(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        return cellStyle;
    }

    /**
     * 写入数据
     *
     * @param xlsxSource xlsxSource
     * @param map        map
     * @param dataRow    dataRow
     */
    private static void setData(XlsxSource xlsxSource, Map map, Row dataRow, CellStyle cellStyle) {
        for (int i = 0; i < xlsxSource.keys.length; i++) {
            Object value = map.get(xlsxSource.keys[i]);
            Cell cell = dataRow.createCell(i);
            if (value instanceof String) {
                cell.setCellValue((String) value);
            } else {
                cell.setCellValue(value + "");
            }
            //上色
            cell.setCellStyle(cellStyle);
        }
    }

    /**
     * 导出Excel
     *
     * @param xlsxSource xlsxSource
     * @param workbook   XSSFWorkbook to xlsx; HSSFWorkbook to xls
     * @throws IOException IOException
     */
    public static void export(XlsxSource xlsxSource, Workbook workbook) throws IOException {
        setSheets(xlsxSource, workbook);
        ServletOutputStream outputStream = getServletOutputStream(xlsxSource);
        workbook.write(outputStream);
    }

    /**
     * 将数据装入模板Excel再导出
     *
     * @param xlsxSource xlsxSource
     * @param fileName   classes下的文件名(相对路径)
     * @throws IOException IOException
     */
    public static void export(XlsxSource xlsxSource, String fileName) throws IOException {
        Workbook workbook = ExcelUtils.getExcelFromTemplate(fileName);
        fillData(xlsxSource, workbook, workbook.getSheetAt(0));
        ServletOutputStream outputStream = getServletOutputStream(xlsxSource);
        workbook.write(outputStream);
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
     * 将数据装入模板
     * @param fileName classes目录下文件名(相对路径)
     * @return Workbook
     */
    public static Workbook getExcelFromTemplate(String fileName) {
        Class<ExcelUtils> clazz = ExcelUtils.class;
        Logger logger = LoggerFactory.getLogger(clazz);
        InputStream resourceAsStream = clazz.getClassLoader().getResourceAsStream(fileName);
        Workbook workbook = null;
        try {
            if (fileName.endsWith(".xls")) {
                workbook = new HSSFWorkbook(resourceAsStream);
            } else if (fileName.endsWith(".xlsx")) {
                workbook = new XSSFWorkbook(resourceAsStream);
            }
        } catch (IOException e) {
            logger.info("获取class下的excel文件失败：{}", e);
        }
        return workbook;
    }

    /**
     * 封装导出Excel所需参数
     */
    public static class XlsxSource {
        private String sheetName;
        private List<?> sources;
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
        public XlsxSource(List<?> sources, String[] keys, String[] keysCn, HttpServletResponse response, String fileName, String sheetName) {
            this.sources = sources;
            this.keys = keys;
            this.keysCn = keysCn;
            this.response = response;
            this.fileName = fileName;
            this.sheetName = sheetName;
        }
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
        HSSFWorkbook workbook = new HSSFWorkbook();
        setSheets(xlsxSource, workbook);
        //实际开发中只需调用export()
        try {
            FileOutputStream fileOutputStream = new FileOutputStream("D:\\temp\\1.xls");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
