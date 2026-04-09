package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class ClassicSixRenderer implements GeneratorRenderer {
    private static final Set<GeneratorFamily> FAMILIES = Set.of(
            GeneratorFamily.CODE_ANALYZER,
            GeneratorFamily.DATA_PROCESSING,
            GeneratorFamily.JARGON,
            GeneratorFamily.METRICS,
            GeneratorFamily.NETWORK_ACTIVITY,
            GeneratorFamily.SYSTEM_MONITORING);

    @Override
    public Set<GeneratorFamily> families() {
        return FAMILIES;
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        Map<String, Object> metadata = metadata(config, "classic-six");
        String message =
                switch (family) {
                    case CODE_ANALYZER ->
                        "code analyzer sweep for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case DATA_PROCESSING ->
                        "data processing window for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case JARGON ->
                        "vocabulary refresh for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case METRICS ->
                        "metrics lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case NETWORK_ACTIVITY ->
                        "network boundary review for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case SYSTEM_MONITORING ->
                        "system heartbeat for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    default -> throw unsupported(family);
                };
        metadata.put("discipline", family.cliValue());
        return new GeneratorRenderResult(message, metadata);
    }

    private static Map<String, Object> metadata(SessionConfig config, String group) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", group);
        metadata.put("project", config.projectName());
        if (!config.framework().isBlank()) {
            metadata.put("framework", config.framework());
        }
        return metadata;
    }

    private static IllegalArgumentException unsupported(GeneratorFamily family) {
        return new IllegalArgumentException("unsupported family for classic-six renderer: " + family.cliValue());
    }
}
