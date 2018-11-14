package com.github.xyyxhcj.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.util.JSONPObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Jackson工具类
 *
 * @author xyyxhcj@qq.com
 * @since 2018/2/6
 */
public class JsonUtils {
    /**
     * 定义处理JSON数据的jackson对象
     */
    private static final ObjectMapper MAPPER = new ObjectMapper();
    /**
     * logger
     */
    private static Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);

    /**
     * 将对象转换成json字符串,可用于Map To Json
     *
     * @param data data
     * @return return
     */
    static String objectToJson(Object data) {
        String jsonString = null;
        try {
            jsonString = MAPPER.writeValueAsString(data);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return jsonString;
    }

    /**
     * 将对象转换成json字符串,可用于Map To Json
     *
     * @param data    data
     * @param include Include.NON_NULL Include.NON_EMPTY
     * @return return
     */
    public static String objectToJson(Object data, JsonInclude.Include include) {
        ObjectMapper mapper = MAPPER;
        // 设置输出时包含属性的风格
        if (include != null) {
            mapper = new ObjectMapper();
            mapper.setSerializationInclusion(include);
        }
        String jsonString = null;
        try {
            jsonString = mapper.writeValueAsString(data);
        } catch (IOException e) {
            LOGGER.warn("write to json string error:" + data, e);
        }
        return jsonString;
    }

    /**
     * 将json格式字符串转换成对象,可用于Json To Map(泛型为'String,Map')
     *
     * @param jsonData jsonData
     * @param beanType beanType
     * @param <T>      T
     * @return return
     */
    public static <T> T jsonToPojo(String jsonData, Class<T> beanType) {
        T t = null;
        try {
            t = MAPPER.readValue(jsonData, beanType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 将json数据转换成pojo对象list
     *
     * @param jsonData jsonData
     * @param beanType beanType
     * @param <T>      T
     * @return return
     */
    public static <T> List<T> jsonToList(String jsonData, Class<T> beanType) {
        JavaType javaType = MAPPER.getTypeFactory().constructParametricType(List.class, beanType);
        List<T> list = null;
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
     *
     * @param jsonData      jsonData
     * @param typeReference 可传入new TypeReference[LinkedHashMap[String, Bean]]() {}
     * @param <T>           T
     * @return return
     */
    public static <T> T jsonToMapPojo(String jsonData, TypeReference<T> typeReference) {
        T t = null;
        try {
            t = MAPPER.readValue(jsonData, typeReference);
        } catch (IOException e) {
            LOGGER.warn("json to map error:" + jsonData, e);
        }
        return t;
    }

    /**
     * @param json jsonData
     * @return jsonNode
     */
    public static JsonNode readTree(String json) {
        JsonNode jsonNode = null;
        try {
            jsonNode = MAPPER.readTree(json);
        } catch (IOException e) {
            LOGGER.warn("json to jsonNode error:" + json, e);
        }
        return jsonNode;
    }

    /**
     * 构造Collection类型
     *
     * @param collectionClass collectionClass
     * @param elementClass    elementClass
     * @return javaType
     */
    public static JavaType buildCollectionType(Class<? extends Collection> collectionClass, Class<?> elementClass) {
        return MAPPER.getTypeFactory().constructCollectionType(collectionClass, elementClass);
    }

    /**
     * 构造Map类型
     *
     * @param keyClass   keyClass
     * @param valueClass valueClass
     * @return javaType
     */
    public static JavaType buildMapType(Class<? extends Map> mapClass, Class<?> keyClass, Class<?> valueClass) {
        return MAPPER.getTypeFactory().constructMapType(mapClass, keyClass, valueClass);
    }

    /**
     * 当JSON里只含有Bean的部分属性时,更新一个已存在Bean,只覆盖部分的属性.
     *
     * @param jsonData jsonData
     * @param object   object
     */
    public static void update(String jsonData, Object object) {
        if (StringUtils.isEmpty(jsonData)) {
            return;
        }
        try {
            MAPPER.readerForUpdating(object).readValue(jsonData);
        } catch (IOException e) {
            LOGGER.warn("update json string:" + jsonData + " to object:" + object + " error.", e);
        }
    }

    /**
     * 输出出JsonP格式
     */
    public static String toJsonP(String functionName, Object object) {
        return objectToJson(new JSONPObject(functionName, object), null);
    }
}
