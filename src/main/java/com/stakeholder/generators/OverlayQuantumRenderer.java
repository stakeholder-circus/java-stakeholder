package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class OverlayQuantumRenderer implements GeneratorRenderer {
    private static final Set<GeneratorFamily> FAMILIES = Set.of(
            GeneratorFamily.HYBRID_RUNTIME_OPS,
            GeneratorFamily.CAPACITY_COST_CONTROLLER,
            GeneratorFamily.BATCH_EXECUTION_TUNER,
            GeneratorFamily.COMPILER_MAINTAINER,
            GeneratorFamily.INTEROP_ADAPTER_ENGINEER,
            GeneratorFamily.PREFLIGHT_CAPACITY_PLANNER,
            GeneratorFamily.SIMULATOR_PERFORMANCE_ENGINEER);

    @Override
    public Set<GeneratorFamily> families() {
        return FAMILIES;
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", "overlay-quantum");
        metadata.put("project", config.projectName());
        metadata.put("overlayCount", context.flavors().size());
        String message =
                switch (family) {
                    case HYBRID_RUNTIME_OPS ->
                        "hybrid runtime lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case CAPACITY_COST_CONTROLLER ->
                        "capacity and cost lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case BATCH_EXECUTION_TUNER ->
                        "batch execution lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case COMPILER_MAINTAINER ->
                        "compiler maintenance lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case INTEROP_ADAPTER_ENGINEER ->
                        "interop adapter lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case PREFLIGHT_CAPACITY_PLANNER ->
                        "preflight capacity lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case SIMULATOR_PERFORMANCE_ENGINEER ->
                        "simulator performance lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    default -> throw unsupported(family);
                };
        return new GeneratorRenderResult(message, metadata);
    }

    private static IllegalArgumentException unsupported(GeneratorFamily family) {
        return new IllegalArgumentException("unsupported family for overlay-quantum renderer: " + family.cliValue());
    }
}
