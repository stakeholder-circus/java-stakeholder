package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class ObservabilityAIRuntimeRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.OBSERVABILITY_AI_RUNTIME);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "modern-core",
                config,
                family.cliValue(),
                "runtimeSignal",
                "OTel traces, token spend, and GPU telemetry in one runtime view");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/observability_ai_runtime.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("observability AI runtime", config, family.message(config.jargonLevel())),
                metadata);
    }
}
