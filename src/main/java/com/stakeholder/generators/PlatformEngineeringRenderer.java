package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class PlatformEngineeringRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.PLATFORM_ENGINEERING);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "modern-core",
                config,
                family.cliValue(),
                "platformSurface",
                "golden paths, workload identity, and self-service template drift");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/platform_engineering.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("platform engineering", config, family.message(config.jargonLevel())),
                metadata);
    }
}
