
package com.github.xyyxhcj.utils;


import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;


/**
 * @author CJ
 */
public class ResponseUtils {
    /**
     * 设置下载文件的响应流,并处理中文文件名乱码
     *
     * @param response response
     * @param fileName "中文.pdf"
     */
    public static void setupResponse(HttpServletResponse response, String fileName) {
        Logger logger = LoggerFactory.getLogger(FileUtils.class);
        //设置强制下载
        response.setContentType("application/force-download");
        //处理中文文件名乱码
        try {
            response.setHeader("Content-disposition", "attachment;filename=" + fileName + ";filename*=utf-8''" + URLEncoder.encode(fileName, "UTF-8"));
        } catch (UnsupportedEncodingException e) {
            logger.error("设置下载文件响应流错误: {}", e);
        }
    }
}
