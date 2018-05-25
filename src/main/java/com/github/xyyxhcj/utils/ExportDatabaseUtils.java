package com.github.xyyxhcj.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mysql数据库备份
 *
 * @author xyyxhcj@qq.com
 * @since 2018/2/26
 */
public class ExportDatabaseUtils {
    /**
     * Java代码实现MySQL数据库导出
     * @param hostIP IP
     * @param hostPort 端口
     * @param userName 用户名
     * @param password 密码
     * @param savePath 保存路径
     * @param fileName 保存文件名
     * @param databaseName 要导出的数据库名
     * @return 返回true表示导出成功，否则返回false。
     */
    public static boolean exportDatabaseTool(String hostIP, int hostPort, String userName, String password, String savePath, String fileName, String databaseName) {
        File saveFile = new File(savePath);
        saveFile.mkdirs();
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-");
        String date = simpleDateFormat.format(new Date());
        StringBuilder stringBuilder = new StringBuilder("cmd /c mysqldump -h");
        try {
            String exeString = stringBuilder.append(hostIP).append(" -P").append(hostPort).append(" -u").append(userName).append(" -p").append(password).append(" --result-file=").append(savePath).append(date).append(fileName).append(" ").append(databaseName).toString();
            Process process = Runtime.getRuntime().exec(exeString);
            InputStream errorStream = process.getErrorStream();
            byte[] bys = new byte[1024];
            int len;
            while ((len = errorStream.read(bys)) != -1) {
                System.out.println(new String(bys, 0, len));
            }
            System.out.println(errorStream);
            if (process.waitFor() == 0) {
                return true;
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 测试:使用配置文件中的对应配置备份数据库
     * @param args args
     */
    public static void main(String[] args) {
        boolean flag = exportDatabaseTool("127.0.0.1", 3306, "root", "as123456", "D:/backupDatabase", "sqlBack.sql", "test");
        if (flag) {
            System.out.println("数据库备份成功！！！");
        } else {
            System.out.println("数据库备份失败！！！");
        }
    }
}
