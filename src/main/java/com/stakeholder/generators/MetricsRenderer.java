package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class MetricsRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.METRICS);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "classic-six",
                config,
                family.cliValue(),
                "signalBlend",
                "queue depth, token spend, and GPU occupancy in a single operations lane");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/metrics.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("metrics", config, family.message(config.jargonLevel())), metadata);
    }
}
