package com.stakeholder.generators;

import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;

final class FamilyRendererSupport {
    private FamilyRendererSupport() {}

    static Map<String, Object> metadata(
            String rendererGroup, SessionConfig config, String family, String familyFocusKey, String familyFocusValue) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", rendererGroup);
        metadata.put("family", family);
        metadata.put("project", config.projectName());
        metadata.put("familyFocusKey", familyFocusKey);
        metadata.put(familyFocusKey, familyFocusValue);
        if (!config.framework().isBlank()) {
            metadata.put("framework", config.framework());
        }
        return metadata;
    }

    static Map<String, Object> traceabilityRow(String family, String sourcePath) {
        Map<String, Object> traceability = new LinkedHashMap<>();
        traceability.put("family", family);
        traceability.put("sourceRepo", "rust-stakeholder");
        traceability.put("sourcePath", sourcePath);
        traceability.put("contractRepo", "stakeholder-core");
        traceability.put("contractPath", "docs/generator-families.md");
        traceability.put("parityClass", "depth");
        return traceability;
    }

    static String message(String label, SessionConfig config, String familyMessage) {
        return label + " depth pass for " + config.projectName() + ": " + familyMessage
                + " Traceability is anchored to Rust and stakeholder-core.";
    }
}
