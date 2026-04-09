package com.stakeholder.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import java.util.EnumSet;
import java.util.List;
import java.util.Random;
import org.junit.jupiter.api.Test;

final class GeneratorRegistryTest {
    private static final GeneratorRegistry REGISTRY = GeneratorRegistry.defaultRegistry();

    @Test
    void registryCoversEveryFamily() {
        assertEquals(EnumSet.allOf(GeneratorFamily.class), REGISTRY.registeredFamilies());
    }

    @Test
    void classicSixRendererProducesProjectMetadata() {
        GeneratorRenderResult result = render(GeneratorFamily.CODE_ANALYZER, List.of());

        assertEquals("classic-six", result.metadata().get("rendererGroup"));
        assertEquals("hospital-ocpp-quantum-control", result.metadata().get("project"));
        assertFalse(result.message().isBlank());
    }

    @Test
    void modernCoreRendererProducesDedicatedMetadata() {
        GeneratorRenderResult result = render(GeneratorFamily.AGENT_WORKFLOWS, List.of());

        assertEquals("modern-core", result.metadata().get("rendererGroup"));
        assertEquals("coordinationMode", result.metadata().get("familyFocusKey"));
        assertEquals(
                "delegated agent work, approval gates, and cross-repo handoff envelopes",
                result.metadata().get("coordinationMode"));
        assertFalse(result.message().isBlank());
    }

    @Test
    void aiGovernanceRendererCarriesExperimentalBoundaryFlag() {
        GeneratorRenderResult result = render(GeneratorFamily.AIBOM_PROVENANCE, List.of("experimental-live-provider"));

        assertEquals("ai-governance", result.metadata().get("rendererGroup"));
        assertEquals(Boolean.TRUE, result.metadata().get("experimentalBoundary"));
    }

    @Test
    void securityBlockchainRendererCarriesTrustBoundaryMetadata() {
        GeneratorRenderResult result = render(GeneratorFamily.BLOCKCHAIN_PROTOCOL_OPS, List.of());

        assertEquals("security-blockchain", result.metadata().get("rendererGroup"));
        assertEquals("blockchain-protocol-ops", result.metadata().get("trustBoundary"));
    }

    @Test
    void healthProtocolRendererCarriesProtocolSurfaceMetadata() {
        GeneratorRenderResult result = render(GeneratorFamily.FHIR_PROFILE_GENERATOR, List.of());

        assertEquals("health-protocol", result.metadata().get("rendererGroup"));
        assertEquals("fhir-r4", result.metadata().get("protocolSurface"));
    }

    @Test
    void overlayQuantumRendererCarriesOverlayCountMetadata() {
        GeneratorRenderResult result = render(
                GeneratorFamily.HYBRID_RUNTIME_OPS,
                List.of("multilingual-security:english", "security-persona:incident-commander"));

        assertEquals("overlay-quantum", result.metadata().get("rendererGroup"));
        assertEquals(2, result.metadata().get("overlayCount"));
    }

    private static GeneratorRenderResult render(GeneratorFamily family, List<String> flavors) {
        return REGISTRY.render(family, new GeneratorRenderContext(config(), new Random(7L), flavors));
    }

    private static SessionConfig config() {
        return new SessionConfig(
                DevelopmentType.SECURITY,
                JargonLevel.HIGH,
                Complexity.EXTREME,
                1L,
                true,
                "hospital-ocpp-quantum-control",
                true,
                true,
                "mcp grpc",
                42L,
                OutputFormat.JSON,
                true,
                true);
    }
}
