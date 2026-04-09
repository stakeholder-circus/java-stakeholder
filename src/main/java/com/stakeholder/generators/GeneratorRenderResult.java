package com.stakeholder.generators;

import java.util.LinkedHashMap;
import java.util.Map;

public record GeneratorRenderResult(String message, Map<String, Object> metadata) {
    public GeneratorRenderResult {
        metadata = Map.copyOf(new LinkedHashMap<>(metadata));
    }
}
