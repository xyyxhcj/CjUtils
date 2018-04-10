package com.github.hcj.utils;

import java.util.UUID;

/**
 * 随机数工具类
 * @author xyyxhcj@qq.com
 * @since 2018/3/30
 */
public class RandomUtils {
    public RandomUtils() {
    }

    /**
     * 获取随机字符串并去除"-"
     * @return return
     */
    public static String getText() {
        String r = UUID.randomUUID().toString().replace("-", "");
        return r;
    }

    /**
     * 在原字符串前拼接随机字符串
     * @param fileName fileName
     * @return return
     */
    public static String getUUIDFileName(String fileName) {
        return getText() + "_" + fileName;
    }

    /**
     * 获取两层目录
     * @param uuidFileName uuidFileName
     * @return return
     */
    public static String getPath(String uuidFileName) {
        StringBuilder sb = new StringBuilder();
        int hashCode = uuidFileName.hashCode();
        for(int i = 0; i < 2; ++i) {
            int x = hashCode ^ 15;
            sb.append(x + "/");
            hashCode >>>= 4;
        }
        return sb.toString().replaceAll("-","");
    }

    public static void main(String[] args) {
        System.out.println(getText());
        System.out.println(getUUIDFileName("ak.jpg"));
        System.out.println(getPath("ak.jpg"));
    }
}
