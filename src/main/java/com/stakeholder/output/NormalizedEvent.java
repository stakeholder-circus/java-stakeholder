package com.stakeholder.output;

import java.util.LinkedHashMap;
import java.util.Map;

public record NormalizedEvent(
        String eventType, int sequence, String message, String timestamp, Map<String, Object> context) {
    public Map<String, Object> toMap() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("eventType", eventType);
        map.put("sequence", sequence);
        map.put("message", message);
        map.put("timestamp", timestamp);
        map.put("context", context);
        return map;
    }
}
