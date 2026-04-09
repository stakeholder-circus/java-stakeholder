package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class DeliveryPreviewOpsRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.DELIVERY_PREVIEW_OPS);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "modern-core",
                config,
                family.cliValue(),
                "releaseGate",
                "preview environments, canary gates, and rollback windows under change control");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/delivery_preview_ops.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("delivery preview ops", config, family.message(config.jargonLevel())),
                metadata);
    }
}
