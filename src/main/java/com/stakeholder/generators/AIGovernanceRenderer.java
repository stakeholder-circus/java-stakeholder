package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class AIGovernanceRenderer implements GeneratorRenderer {
    private static final Set<GeneratorFamily> FAMILIES = Set.of(
            GeneratorFamily.AI_INFERENCE_OPS,
            GeneratorFamily.KNOWLEDGE_RETRIEVAL,
            GeneratorFamily.EVALUATION_AND_GUARDRAILS,
            GeneratorFamily.AIBOM_PROVENANCE,
            GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
            GeneratorFamily.FINOPS_CAPACITY);

    @Override
    public Set<GeneratorFamily> families() {
        return FAMILIES;
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        boolean experimental = context.flavors().contains("experimental-live-provider");
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", "ai-governance");
        metadata.put("project", config.projectName());
        metadata.put("experimentalBoundary", experimental);
        String message =
                switch (family) {
                    case AI_INFERENCE_OPS ->
                        "inference operations lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case KNOWLEDGE_RETRIEVAL ->
                        "knowledge retrieval lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case EVALUATION_AND_GUARDRAILS ->
                        "evaluation and guardrails lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case AIBOM_PROVENANCE ->
                        "AIBOM provenance lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case DATA_GOVERNANCE_COMPLIANCE ->
                        "data governance lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case FINOPS_CAPACITY ->
                        "FinOps capacity lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    default -> throw unsupported(family);
                };
        return new GeneratorRenderResult(message, metadata);
    }

    private static IllegalArgumentException unsupported(GeneratorFamily family) {
        return new IllegalArgumentException("unsupported family for ai-governance renderer: " + family.cliValue());
    }
}
