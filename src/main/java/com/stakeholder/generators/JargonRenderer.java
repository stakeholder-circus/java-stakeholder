package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class JargonRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.JARGON);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "classic-six",
                config,
                family.cliValue(),
                "languagePolicy",
                "credible 2026 terminology instead of fake-deep phrasing");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/jargon.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("jargon refresh", config, family.message(config.jargonLevel())),
                metadata);
    }
}
