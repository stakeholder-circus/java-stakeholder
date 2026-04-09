package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class SystemMonitoringRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.SYSTEM_MONITORING);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "classic-six",
                config,
                family.cliValue(),
                "telemetryScope",
                "collector pressure, runner health, and policy-denial signals across the stack");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/system_monitoring.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("system monitoring", config, family.message(config.jargonLevel())),
                metadata);
    }
}
