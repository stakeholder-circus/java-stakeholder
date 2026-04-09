package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class NetworkActivityRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.NETWORK_ACTIVITY);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "classic-six",
                config,
                family.cliValue(),
                "transportMix",
                "RPC, event-stream, and adapter traffic under deterministic retry rules");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/network_activity.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("network activity", config, family.message(config.jargonLevel())),
                metadata);
    }
}
