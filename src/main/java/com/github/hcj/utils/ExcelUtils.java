package com.github.hcj.utils;


import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.dozer.DozerBeanMapper;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
     * 用于转换Bean->Map
     */
    private static DozerBeanMapper dozerBeanMapper = new DozerBeanMapper();

    /**
     * 生成.xlsx文件数据
     * @param xlsxSource xlsxSource
     * @return return
     */
    private static XSSFWorkbook getXssfSheets(XlsxSource xlsxSource) {
        Map map = new HashMap(xlsxSource.keys.length);
        //生成Excel: 新建Excel->sheet->cell->写入单元格数据
        //新建.xlsx文件
        XSSFWorkbook xssfWorkbook = new XSSFWorkbook();
        //新建工作表
        XSSFSheet sheet = xssfWorkbook.createSheet(xlsxSource.sheetName);
        //创建前两行数据
        XSSFRow rowKey = sheet.createRow(0);
        XSSFRow rowKeyCn = sheet.createRow(1);
        //隐藏首行
        rowKey.setZeroHeight(true);
        for (int i = 0; i < xlsxSource.keys.length; i++) {
            rowKey.createCell(i).setCellValue(xlsxSource.keys[i]);
            rowKeyCn.createCell(i).setCellValue(xlsxSource.keysCn[i]);
        }
        //写入数据
        for (Object obj : xlsxSource.sources) {
            //获取尾行的下一行: getLastRowNum+1
            XSSFRow dataRow = sheet.createRow(sheet.getLastRowNum() + 1);
            if (obj instanceof Map) {
                map = (Map) obj;
            } else {
                dozerBeanMapper.map(obj, map);
            }
            for (int i = 0; i < xlsxSource.keys.length; i++) {
                Object value = map.get(xlsxSource.keys[i]);
                if (value instanceof String) {
                    dataRow.createCell(i).setCellValue((String) value);
                } else {
                    //数值字段转String
                    dataRow.createCell(i).setCellValue(value + "");
                }
            }
        }
        return xssfWorkbook;
    }

    /**
     * 指定输出流参数
     * @param xlsxSource xlsxSource
     * @throws IOException IOException
     */
    private static void setHttpServletResponse(XlsxSource xlsxSource) throws IOException {
        String agent = xlsxSource.request.getHeader("user-agent");
        xlsxSource.fileName = FileUtils.encodeDownloadFilename(xlsxSource.fileName, agent);
        xlsxSource.response.setHeader("Content-Disposition", "attachment;filename=" + xlsxSource.fileName);
    }

    /**
     * 导出xlsx
     * @param xlsxSource xlsxSource
     * @throws IOException IOException
     */
    public static void export(XlsxSource xlsxSource) throws IOException {
        XSSFWorkbook xssfWorkbook = getXssfSheets(xlsxSource);
        setHttpServletResponse(xlsxSource);
        //指定.xlsx对应的ContentType
        xlsxSource.response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        ServletOutputStream outputStream = xlsxSource.response.getOutputStream();
        xssfWorkbook.write(outputStream);
        outputStream.close();
    }

    /**
     * 将Xlsx转为List
     * @param multipartFile multipartFile
     * @return return
     */
    public static List<Map> getListFromExcel(MultipartFile multipartFile) {
        List<Map> records = null;
        String originalFilename = multipartFile.getOriginalFilename();
        try {
            InputStream fileInputStream = multipartFile.getInputStream();
            if (originalFilename.endsWith(".xls")) {
                //加载Excel文件对象
                HSSFWorkbook hssfWorkbook = new HSSFWorkbook(fileInputStream);
                //根据名字或者索引获取指定工作表
                HSSFSheet sheet = hssfWorkbook.getSheetAt(0);
                //获取首行数据(对应实体类字段)
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
     *
     * @param sheet sheet
     * @param row row
     * @return return
     */
    private static List<Map> getListFromSheet(Sheet sheet, Row row) {
        List<Map> list = new ArrayList<>();
        //将首行数据存入数组(对应实体类属性名)
        short cellNum = row.getLastCellNum();
        String[] strings = new String[cellNum];
        for (int i = 0; i < cellNum; i++) {
            strings[i] = row.getCell(i).getStringCellValue();
        }
        //从第3行开始逐行读取工作表中的每一行数据
        loop:
        for (int i = 2; i <= sheet.getLastRowNum(); i++) {
            Map<String, String> map = new HashMap<String, String>(strings.length);
            for (int j = 0; j < cellNum; j++) {
                //获取每个单元格的数据(判断类型),并以对应的首行列名作为key存入map集合
                Cell cell = sheet.getRow(i).getCell(j);
                if (cell == null || StringUtils.isBlank(cell.toString())) {
                    if (j == 0) {
                        //当下一行的第一个单元格无数据时退出循环
                        break loop;
                    }
                    //当该单元格无数据时跳过
                    continue;
                }
                int cellType = cell.getCellType();
                if (Cell.CELL_TYPE_STRING == cellType) {
                    map.put(strings[j], cell.getStringCellValue());
                } else if (Cell.CELL_TYPE_NUMERIC == cellType) {
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
     * 封装导出Excel所需参数
     */
    public static class XlsxSource {
        private String sheetName;
        private List<Map<String, Object>> sources;
        private String[] keys;
        private String[] keysCn;
        private HttpServletRequest request;
        private HttpServletResponse response;
        private String fileName;

        /**
         * 封装导出Excel所需参数
         * @param sources 数据源
         * @param keys 实体类字段
         * @param keysCn 实体类中文字段
         * @param request request
         * @param response response
         * @param fileName 输出文件名(包含后缀)
         * @param sheetName Excel工作表名
         */
        public XlsxSource(List<Map<String, Object>> sources, String[] keys, String[] keysCn, HttpServletRequest request, HttpServletResponse response, String fileName, String sheetName) {
            this.sources = sources;
            this.keys = keys;
            this.keysCn = keysCn;
            this.request = request;
            this.response = response;
            this.fileName = fileName;
            this.sheetName = sheetName;
        }
    }
}
