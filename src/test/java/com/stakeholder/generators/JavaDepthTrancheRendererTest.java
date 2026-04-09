package com.stakeholder.generators;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.junit.jupiter.api.Test;

final class JavaDepthTrancheRendererTest {
    private static final GeneratorRegistry REGISTRY = GeneratorRegistry.defaultRegistry();

    @Test
    void codeAnalyzerDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.CODE_ANALYZER,
                "classic-six",
                "analysisFocus",
                "typed interfaces, agent-authored patches, and MCP assumptions",
                "src/generators/code_analyzer.rs");
    }

    @Test
    void dataProcessingDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.DATA_PROCESSING,
                "classic-six",
                "dataWindow",
                "embeddings, semantic chunks, and batch transforms with deterministic ordering",
                "src/generators/data_processing.rs");
    }

    @Test
    void jargonDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.JARGON,
                "classic-six",
                "languagePolicy",
                "credible 2026 terminology instead of fake-deep phrasing",
                "src/generators/jargon.rs");
    }

    @Test
    void metricsDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.METRICS,
                "classic-six",
                "signalBlend",
                "queue depth, token spend, and GPU occupancy in a single operations lane",
                "src/generators/metrics.rs");
    }

    @Test
    void networkActivityDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.NETWORK_ACTIVITY,
                "classic-six",
                "transportMix",
                "RPC, event-stream, and adapter traffic under deterministic retry rules",
                "src/generators/network_activity.rs");
    }

    @Test
    void systemMonitoringDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.SYSTEM_MONITORING,
                "classic-six",
                "telemetryScope",
                "collector pressure, runner health, and policy-denial signals across the stack",
                "src/generators/system_monitoring.rs");
    }

    @Test
    void agentWorkflowsDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.AGENT_WORKFLOWS,
                "modern-core",
                "coordinationMode",
                "delegated agent work, approval gates, and cross-repo handoff envelopes",
                "src/generators/agent_workflows.rs");
    }

    @Test
    void platformEngineeringDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.PLATFORM_ENGINEERING,
                "modern-core",
                "platformSurface",
                "golden paths, workload identity, and self-service template drift",
                "src/generators/platform_engineering.rs");
    }

    @Test
    void observabilityAIRuntimeDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                "modern-core",
                "runtimeSignal",
                "OTel traces, token spend, and GPU telemetry in one runtime view",
                "src/generators/observability_ai_runtime.rs");
    }

    @Test
    void deliveryPreviewOpsDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.DELIVERY_PREVIEW_OPS,
                "modern-core",
                "releaseGate",
                "preview environments, canary gates, and rollback windows under change control",
                "src/generators/delivery_preview_ops.rs");
    }

    @Test
    void supplyChainSecurityDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderer(
                GeneratorFamily.SUPPLY_CHAIN_SECURITY,
                "modern-core",
                "trustSurface",
                "signed artifacts, provenance evidence, and dependency substitution checks",
                "src/generators/supply_chain_security.rs");
    }

    private static void assertDedicatedRenderer(
            GeneratorFamily family, String rendererGroup, String focusKey, String focusValue, String sourcePath) {
        GeneratorRenderResult result =
                REGISTRY.render(family, new GeneratorRenderContext(config(), new Random(7L), List.of()));

        assertEquals(rendererGroup, result.metadata().get("rendererGroup"));
        assertEquals(family.cliValue(), result.metadata().get("family"));
        assertEquals("hospital-ocpp-quantum-control", result.metadata().get("project"));
        assertEquals(focusKey, result.metadata().get("familyFocusKey"));
        assertEquals(focusValue, result.metadata().get(focusKey));
        assertFalse(result.message().isBlank());
        assertTrue(result.message().contains(family.message(JargonLevel.HIGH)));
        assertTrue(result.message().contains("Traceability is anchored to Rust and stakeholder-core."));

        Map<?, ?> traceabilityRow = (Map<?, ?>) result.metadata().get("traceabilityRow");
        assertEquals(family.cliValue(), traceabilityRow.get("family"));
        assertEquals("rust-stakeholder", traceabilityRow.get("sourceRepo"));
        assertEquals(sourcePath, traceabilityRow.get("sourcePath"));
        assertEquals("stakeholder-core", traceabilityRow.get("contractRepo"));
        assertEquals("docs/generator-families.md", traceabilityRow.get("contractPath"));
        assertEquals("depth", traceabilityRow.get("parityClass"));
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
