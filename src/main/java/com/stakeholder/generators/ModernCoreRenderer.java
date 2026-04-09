package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class ModernCoreRenderer implements GeneratorRenderer {
    private static final Set<GeneratorFamily> FAMILIES = Set.of(
            GeneratorFamily.AGENT_WORKFLOWS,
            GeneratorFamily.PLATFORM_ENGINEERING,
            GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
            GeneratorFamily.DELIVERY_PREVIEW_OPS,
            GeneratorFamily.SUPPLY_CHAIN_SECURITY);

    @Override
    public Set<GeneratorFamily> families() {
        return FAMILIES;
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        Map<String, Object> metadata = baseMetadata(config);
        String message =
                switch (family) {
                    case AGENT_WORKFLOWS ->
                        "agent workflow lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case PLATFORM_ENGINEERING ->
                        "platform engineering lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case OBSERVABILITY_AI_RUNTIME ->
                        "observability runtime lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case DELIVERY_PREVIEW_OPS ->
                        "delivery preview lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case SUPPLY_CHAIN_SECURITY ->
                        "supply-chain gate for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    default -> throw unsupported(family);
                };
        metadata.put("controlPlane", family.cliValue());
        return new GeneratorRenderResult(message, metadata);
    }

    private static Map<String, Object> baseMetadata(SessionConfig config) {
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", "modern-core");
        metadata.put("project", config.projectName());
        metadata.put("teamActivity", config.teamActivity());
        return metadata;
    }

    private static IllegalArgumentException unsupported(GeneratorFamily family) {
        return new IllegalArgumentException("unsupported family for modern-core renderer: " + family.cliValue());
    }
}
