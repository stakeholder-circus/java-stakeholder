package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class SupplyChainSecurityRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.SUPPLY_CHAIN_SECURITY);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "modern-core",
                config,
                family.cliValue(),
                "trustSurface",
                "signed artifacts, provenance evidence, and dependency substitution checks");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/supply_chain_security.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("supply chain security", config, family.message(config.jargonLevel())),
                metadata);
    }
}
