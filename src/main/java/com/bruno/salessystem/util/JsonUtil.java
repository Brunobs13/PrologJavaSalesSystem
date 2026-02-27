package com.bruno.salessystem.util;

import java.util.List;
import java.util.Map;

public final class JsonUtil {
    private JsonUtil() {
    }

    public static String toJson(Object object) {
        if (object == null) {
            return "null";
        }
        if (object instanceof String str) {
            return '"' + escape(str) + '"';
        }
        if (object instanceof Number || object instanceof Boolean) {
            return String.valueOf(object);
        }
        if (object instanceof Map<?, ?> map) {
            StringBuilder builder = new StringBuilder();
            builder.append('{');
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append('"').append(escape(String.valueOf(entry.getKey()))).append('"').append(':');
                builder.append(toJson(entry.getValue()));
            }
            builder.append('}');
            return builder.toString();
        }
        if (object instanceof List<?> list) {
            StringBuilder builder = new StringBuilder();
            builder.append('[');
            boolean first = true;
            for (Object entry : list) {
                if (!first) {
                    builder.append(',');
                }
                first = false;
                builder.append(toJson(entry));
            }
            builder.append(']');
            return builder.toString();
        }
        return '"' + escape(String.valueOf(object)) + '"';
    }

    public static String escape(String value) {
        return value
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t");
    }
}
