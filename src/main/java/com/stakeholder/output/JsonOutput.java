package com.stakeholder.output;

import java.io.PrintStream;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public final class JsonOutput {
    private JsonOutput() {}

    public static void writeObject(PrintStream out, Map<String, ?> value) {
        out.println(toJson(value));
    }

    public static void writeArray(PrintStream out, List<?> value) {
        out.println(toJson(value));
    }

    public static void writeEvents(PrintStream out, List<NormalizedEvent> events) {
        writeArray(out, events.stream().map(NormalizedEvent::toMap).toList());
    }

    private static String toJson(Object value) {
        if (value == null) {
            return "null";
        }
        if (value instanceof String string) {
            return quote(string);
        }
        if (value instanceof Number || value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Map<?, ?> map) {
            StringBuilder builder = new StringBuilder("{");
            Iterator<? extends Map.Entry<?, ?>> iterator = map.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<?, ?> entry = iterator.next();
                builder.append(quote(String.valueOf(entry.getKey())))
                        .append(':')
                        .append(toJson(entry.getValue()));
                if (iterator.hasNext()) {
                    builder.append(',');
                }
            }
            return builder.append('}').toString();
        }
        if (value instanceof List<?> list) {
            StringBuilder builder = new StringBuilder("[");
            for (int index = 0; index < list.size(); index++) {
                if (index > 0) {
                    builder.append(',');
                }
                builder.append(toJson(list.get(index)));
            }
            return builder.append(']').toString();
        }
        return quote(String.valueOf(value));
    }

    private static String quote(String value) {
        return "\""
                + value.replace("\\", "\\\\")
                        .replace("\"", "\\\"")
                        .replace("\n", "\\n")
                        .replace("\r", "\\r")
                        .replace("\t", "\\t")
                + "\"";
    }
}
