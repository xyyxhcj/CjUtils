package com.github.xyyxhcj.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

/**
 * Jackson工具类
 * @author xyyxhcj@qq.com
 * @since 2018/2/6
 */
public class JsonUtils {
    //定义处理JSON数据的jackson对象
    private static final ObjectMapper MAPPER = new ObjectMapper();

    /**
     * 将对象转换成json字符串,可用于Map To Json
     * @param data data
     * @return return
     */
    public static String objectToJson(Object data) {
        String jsonString=null;
        try {
            jsonString = MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * 将json格式字符串转换成对象,可用于Json To Map(泛型为'String,Map')
     * @param jsonData jsonData
     * @param beanType beanType
     * @param <T> T
     * @return return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        T t=null;
        try {
            t = MAPPER.readValue(jsonData, beanType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 将json数据转换成pojo对象list
     * @param jsonData jsonData
     * @param beanType beanType
     * @param <T> T
     * @return return
     */
    public static <T>List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        List<T> list=null;
        try {
            list = MAPPER.readValue(jsonData, javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }
    /**
     * 设置属性值为null时不参与序列化,只作用于Bean属性
     */
    public static void setSerializationInclusionNotNull() {
        MAPPER.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    /**
     * 将json格式字符串转换成对象,可用于Json To Map(泛型为'String,Bean')
     * @param jsonData jsonData
     * @param typeReference 可传入new TypeReference[LinkedHashMap[String, Bean]]() {}
     * @param <T> T
     * @return return
     */
    public static<T> T jsonToMapPojo(String jsonData, TypeReference<T> typeReference) {
        T t=null;
        try {
            t = MAPPER.readValue(jsonData,typeReference);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }
}
