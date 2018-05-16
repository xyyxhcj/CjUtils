package com.github.xyyxhcj.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.StringWriter;


/**
 * freemarker模板工具
 *
 * @author xyyxhcj@qq.com
 * @since 2018-05-14
 */

public class FreemarkerUtils {

    /**
     * 输出模板与数据合并后的html字符串
     *
     * @param basePackagePath 模板文件夹在classes下的相对路径,如"/templates/"
     * @param templateName    模板名称
     * @param params          模板显示的数据
     * @return 模板与数据合并后的字符串
     */
    public static String generate(String basePackagePath, String templateName, Object params) {
        //1.创建Configuration对象(指定版本)
        Configuration configuration = new Configuration();
        //2.指定模板文件位置
        configuration.setClassForTemplateLoading(FreemarkerUtils.class, basePackagePath);
        //3.设置模板文件字符集(utf-8)
        configuration.setDefaultEncoding("utf-8");
        String htmlStr = "";
        StringWriter writer = new StringWriter();
        try {
            //4.加载模板,创建模板对象
            Template template = configuration.getTemplate(templateName);
            template.setOutputEncoding("UTF-8");
            template.process(params, writer);
            htmlStr = writer.toString();
        } catch (IOException e) {
            throw new RuntimeException("Freemarker 加载模板失败" + e);
        } catch (TemplateException e) {
            throw new RuntimeException("Freemarker 数据加载失败" + e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return htmlStr;
    }
}
