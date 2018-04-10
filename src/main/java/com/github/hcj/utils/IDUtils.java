package com.github.hcj.utils;

import java.util.List;
import java.util.Random;
import java.util.UUID;

/**
 * 主键生成工具类(使用13位毫秒值进行拼接)
 * @author xyyxhcj@qq.com
 * @since 2018/2/6
 */
public class IDUtils {
    /**
     * 16位ID生成,毫秒值+随机整数(整型字符串)
     * @return return
     */
    public static String getId16() {
        long millis = System.currentTimeMillis();
        int end = new Random().nextInt(999);
        //格式化数字;"%03d":在不足3位的数字前补0
        return millis + String.format("%03d", end);
    }
    /**
     * 18位ID生成,纳秒值+随机整数(整型字符串)
     * @return return
     */
    public static String getId18() {
        long millis = System.nanoTime();
        int end = new Random().nextInt(9999);
        //格式化数字;"%03d":在不足4位的数字前补0
        return millis + String.format("%04d", end);
    }

    /**
     * 20位字符串ID生成(毫秒值+UUID)
     * @return return
     */
    public static String getIdStr20() {
        long millis=System.currentTimeMillis();
        String temp = UUID.randomUUID().toString();
        return millis + temp.replaceAll("-", "").substring(32-7);
    }

    /**
     * 测试
     * @param args args
     */
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            String id16 = getId16();
            System.out.println(id16.length()+" : "+id16);
        }
        for (int i = 0; i < 10; i++) {
            String id18 = getId18();
            System.out.println(id18.length()+" : "+id18);
        }
        for (int i = 0; i < 10; i++) {
            String idStr20 = getIdStr20();
            System.out.println(idStr20.length()+" : "+idStr20);
        }
    }
}
