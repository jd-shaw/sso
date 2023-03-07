package com.shaw.sso.utils;


import org.apache.commons.lang3.StringUtils;

/**
 * @author shaw
 * @date 2017年1月5日
 */
public class TypeUtils {

    public static int toInt(Object value) {
        if (value != null) {
            if (value instanceof Integer)
                return (Integer) value;

            String str = value.toString();
            if (StringUtils.isNotBlank(str)) {
                return Integer.parseInt(str);
            }
        }
        return 0;
    }

    public static double toDouble(Object value) {
        if (value != null) {
            if (value instanceof Double)
                return (Double) value;

            String str = value.toString();
            if (StringUtils.isNotBlank(str)) {
                return Double.parseDouble(str);
            }
        }
        return 0.0;
    }

    public static float toFloat(Object value) {
        if (value != null) {
            if (value instanceof Double)
                return (Float) value;

            String str = value.toString();
            if (StringUtils.isNotBlank(str)) {
                return Float.parseFloat(str);
            }
        }
        return 0.0f;
    }

    public static boolean toBoolean(Object value) {
        if (value != null) {
            if (value instanceof Boolean)
                return (Boolean) value;

            String str = value.toString();
            if (StringUtils.isNotBlank(str)) {
                return Boolean.parseBoolean(str);
            }
        }
        return false;
    }

    public static long toLong(Object value) {
        if (value != null) {
            if (value instanceof Long)
                return (Long) value;

            String str = value.toString();
            if (StringUtils.isNotBlank(str)) {
                return Long.parseLong(str);
            }
        }
        return 0L;
    }

    public static String toString(Object value) {
        return value == null ? null : value.toString();
    }

}
