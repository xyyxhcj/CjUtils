package com.github.hcj.utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Mysql数据库备份
 * @author xyyxhcj@qq.com
 * @date 2018/2/26
 */
public class ExportDatabaseUtils {
    /**
     * Java代码实现MySQL数据库导出
     *
     * @author GaoHuanjie
     * @param hostIP MySQL数据库所在服务器地址IP
     * @param userName 进入数据库所需要的用户名
     * @param password 进入数据库所需要的密码
     * @param savePath 数据库导出文件保存路径
     * @param fileName 数据库导出文件文件名
     * @param databaseName 要导出的数据库名
     * @return 返回true表示导出成功，否则返回false。
     */
    public static boolean exportDatabaseTool(String hostIP,int hostPost, String userName, String password, String savePath, String fileName, String databaseName) {
        File saveFile = new File(savePath);
        if (!saveFile.exists()) {// 如果目录不存在
            saveFile.mkdirs();// 创建文件夹
        }
        if (!savePath.endsWith(File.separator)) {
            savePath = savePath + File.separator;
        }
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-");
        String date = simpleDateFormat.format(new Date());
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("mysqldump").append(" --opt").append(" -h").append(hostIP).append(" -P").append(hostPost);
        stringBuilder.append(" --user=").append(userName) .append(" --password=").append(password).append(" --lock-all-tables=true");
        stringBuilder.append(" --result-file=").append(savePath +date+ fileName).append(" --default-character-set=utf8 ").append(databaseName);
        System.out.println(stringBuilder);
        try {
            Process process = Runtime.getRuntime().exec(stringBuilder.toString());
            if (process.waitFor() == 0) {// 0 表示线程正常终止。
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }





    /**
     * 使用配置文件中的对应配置备份数据库
     * @return
     * @throws InterruptedException
     */
    public static boolean defaultExport() throws InterruptedException {
        boolean flag = exportDatabaseTool("172.16.0.127",3306, "root", "123456", "D:/backupDatabase", "2014-10-14.sql", "test");
        if (flag) {
            System.out.println("数据库备份成功！！！");
        } else {
            System.out.println("数据库备份失败！！！");
        }
        return flag;
    }
}
