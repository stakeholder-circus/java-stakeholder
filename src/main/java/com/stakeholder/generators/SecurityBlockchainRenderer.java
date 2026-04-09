package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class SecurityBlockchainRenderer implements GeneratorRenderer {
    private static final Set<GeneratorFamily> FAMILIES = Set.of(
            GeneratorFamily.IDENTITY_AND_TRUST,
            GeneratorFamily.AGENT_BOUNDARY_SECURITY,
            GeneratorFamily.BLOCKCHAIN_PROTOCOL_OPS,
            GeneratorFamily.CROSS_CHAIN_INTEROP,
            GeneratorFamily.PROOF_AND_SEQUENCER_OPS);

    @Override
    public Set<GeneratorFamily> families() {
        return FAMILIES;
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", "security-blockchain");
        metadata.put("project", config.projectName());
        metadata.put("alertsEnabled", config.alertsEnabled());
        String message =
                switch (family) {
                    case IDENTITY_AND_TRUST ->
                        "identity and trust lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case AGENT_BOUNDARY_SECURITY ->
                        "agent boundary lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case BLOCKCHAIN_PROTOCOL_OPS ->
                        "blockchain protocol lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case CROSS_CHAIN_INTEROP ->
                        "cross-chain interop lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case PROOF_AND_SEQUENCER_OPS ->
                        "proof and sequencer lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    default -> throw unsupported(family);
                };
        metadata.put("trustBoundary", family.cliValue());
        return new GeneratorRenderResult(message, metadata);
    }

    private static IllegalArgumentException unsupported(GeneratorFamily family) {
        return new IllegalArgumentException(
                "unsupported family for security-blockchain renderer: " + family.cliValue());
    }
}
