package com.shaw.sso.utils;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * jackjson一些转换方法
 *
 * @author shaw
 * @date 2015-2-4
 */
public class JackJson {
    private static final Logger logger = LoggerFactory.getLogger(JackJson.class);

    private final static ObjectMapper mapper = new ObjectMapper();

    /**
     * 格式化时间的string
     */
    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm:ss";

    public static boolean isJson(String str) {
        if (StringUtils.isNotBlank(str)) {
            if (StringUtils.startsWith(str, "{") && StringUtils.endsWith(str, "}")) {
                try {
                    mapper.readValue(str, Map.class);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else if (StringUtils.startsWith(str, "[") && StringUtils.endsWith(str, "]")) {
                try {
                    mapper.readValue(str, List.class);
                    return true;
                } catch (Exception e) {
                    return false;
                }
            } else {
                return false;
            }
        }
        return false;
    }

    /**
     * jackjson把json字符串转换为Java对象的实现方法
     *
     * <pre>
     * return JackJson.fromJsonToObject(this.answersJson, new TypeReference&lt;List&lt;StanzaAnswer&gt;&gt;() {
     * });
     * </pre>
     *
     * @param <T>           转换为的java对象
     * @param json          json字符串
     * @param typeReference jackjson自定义的类型
     * @return 返回Java对象
     */
    public static <T> T fromJsonToObject(String json, TypeReference<T> typeReference) {
        try {
            return mapper.readValue(json, typeReference);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromJsonToHasDateObject(String json, TypeReference<T> typeReference) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            return mapper.readValue(json, typeReference);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromJsonToObject(String json, Class<T> collectionClass, Class<?> elementClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
            return mapper.readValue(json, javaType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromJsonToHasDateObject(String json, Class<T> collectionClass, Class<?> elementClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
            return mapper.readValue(json, javaType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    /**
     * json转换为java对象
     *
     * <pre>
     * return JackJson.fromJsonToObject(this.answersJson, JackJson.class);
     * </pre>
     *
     * @param <T>       要转换的对象
     * @param json      字符串
     * @param valueType 对象的class
     * @return 返回对象
     */
    public static <T> T fromJsonToHasDateObject(String json, Class<T> valueType, String dateFormat) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setDateFormat(new SimpleDateFormat(dateFormat));
            return mapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromJsonToHasDateObject(String json, Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            return mapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromUnknownJsonToHasDateObject(String json, Class<T> valueType) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            return mapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromUnknownJsonToHasDateObject(String json, Class<T> collectionClass, Class<?> elementClass) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            JavaType javaType = mapper.getTypeFactory().constructParametricType(collectionClass, elementClass);
            return mapper.readValue(json, javaType);
        } catch (JsonParseException e) {
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static <T> T fromJsonToObject(String json, Class<T> valueType) {
        try {
            return mapper.readValue(json, valueType);
        } catch (JsonParseException e) {
            e.printStackTrace();
            logger.error("JsonParseException: ", e);
        } catch (JsonMappingException e) {
            e.printStackTrace();
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("IOException: ", e);
        }
        return null;
    }

    /**
     * java对象转换为json字符串
     *
     * @param object Java对象
     * @return 返回字符串
     */
    public static String fromObjectToJson(Object object) {
        try {
            return mapper.writeValueAsString(object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    /**
     * java对象转换为json字符串
     *
     * @param object     要转换的对象
     * @param filterName 过滤器的名称
     * @param properties 要过滤哪些字段
     * @return
     */
    public static String fromObjectToJson(Object object, String filterName, Set<String> properties) {
        ObjectMapper mapper = new ObjectMapper();
        FilterProvider filters = new SimpleFilterProvider().addFilter(filterName,
                SimpleBeanPropertyFilter.serializeAllExcept(properties));
        try {
            return mapper.writer(filters).writeValueAsString(object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    /**
     * java对象转换为json字符串
     *
     * @param object
     * @param serializationView
     * @return
     */
    public static String fromObjectToJson(Object object, Class<?> serializationView) {
        try {
            return createObjectMapper().writerWithView(serializationView).writeValueAsString(object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    public static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }

    /**
     * java对象转换为json字符串
     *
     * @param object     要转换的对象
     * @param filterName 过滤器的名称
     * @param properties 要过滤的字段名称
     * @return
     */
    public static String fromObjectToJson(Object object, String filterName, String property) {
        FilterProvider filters = new SimpleFilterProvider().addFilter(filterName,
                SimpleBeanPropertyFilter.serializeAllExcept(property));
        try {
            return createObjectMapper().writer(filters).writeValueAsString(object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    /**
     * java对象(包含日期字段或属性)转换为json字符串
     *
     * @param object Java对象
     * @return 返回字符串
     */
    public static String fromObjectHasDateToJson(Object object) {
        try {
            ObjectMapper mapper = createObjectMapper();
            mapper.setDateFormat(new SimpleDateFormat(DATE_TIME_FORMAT));
            return mapper.writeValueAsString(object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    /**
     * java对象(包含日期字段或属性)转换为json字符串
     *
     * @param object               Java对象
     * @param dateTimeFormatString 自定义的日期/时间格式。该属性的值遵循java标准的date/time格式规范。如：yyyy-MM-dd
     * @return 返回字符串
     */
    public static String fromObjectHasDateToJson(Object object, String dateTimeFormatString) {
        try {
            return createObjectMapper().setDateFormat(new SimpleDateFormat(dateTimeFormatString))
                    .writeValueAsString(object);
        } catch (JsonGenerationException e) {
            logger.error("JsonGenerationException: ", e);
        } catch (JsonMappingException e) {
            logger.error("JsonMappingException: ", e);
        } catch (IOException e) {
            logger.error("IOException: ", e);
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> T getValue(Object map, String key, Class<T> clazz) {
        T result = null;
        if (map instanceof Map) {
            for (Map.Entry<String, Object> entry : ((Map<String, Object>) map).entrySet()) {
                if (entry.getKey().equals(key) && entry.getValue() != null
                        && clazz.isAssignableFrom(entry.getValue().getClass())) {
                    return (T) entry.getValue();
                } else if (entry.getValue() != null) {
                    result = getValue(entry.getValue(), key, clazz);
                    if (result != null)
                        return result;
                }
            }
        } else if (map instanceof List) {
            for (Object value : (List) map) {
                result = getValue(value, key, clazz);

                if (result != null)
                    return result;
            }
        }
        return result;
    }

    public static <T> T copy(Object obj, Class<T> clazz) {
        try {
            return mapper.readValue(mapper.writeValueAsString(obj), clazz);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> List<T> copyList(Object obj, Class<T> clazz) {
        try {
            ObjectMapper mapper = createObjectMapper();
            JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, clazz);
            return mapper.readValue(mapper.writeValueAsString(obj), javaType);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
