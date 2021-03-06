package com.github.xyyxhcj.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * 字符串工具类
 * @author xyyxhcj@qq.com
 * @since 2018/2/7
 */
public class FastStringUtils {
    /**
     * 字符串切割
     * @param src 被切割字符串
     * @param replacement 分割符
     * @param <T> T
     * @return 切割后的结果集
     */
    public static<T> List<T> split(String src, String replacement) {
        ArrayList<T> list = new ArrayList<>();
        if (src == null || "".equals(src.trim())) {
            return list;
        }
        int index;
        int length = replacement.length();
        while ((index = src.indexOf(replacement)) != -1) {
            String str = src.substring(0, index);
            if (str.length() != 0) {
                list.add(cast(str.trim()));
            }
            src = src.substring(index+length);
        }
        if (src.length() != 0) {
            list.add(cast(src.trim()));
        }
        return list;
    }

    @SuppressWarnings("unchecked")
    private static <T> T cast(Object object) {
        return (T) object;
    }
}
