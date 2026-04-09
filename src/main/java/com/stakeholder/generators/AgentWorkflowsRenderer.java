package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class AgentWorkflowsRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.AGENT_WORKFLOWS);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "modern-core",
                config,
                family.cliValue(),
                "coordinationMode",
                "delegated agent work, approval gates, and cross-repo handoff envelopes");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/agent_workflows.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("agent workflows", config, family.message(config.jargonLevel())),
                metadata);
    }
}
