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

final class RemainingJavaDepthTrancheRendererTest {
    private static final GeneratorRegistry REGISTRY = GeneratorRegistry.defaultRegistry();

    @Test
    void aiGovernanceDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderers(
                "ai-governance",
                List.of(
                        expectation(
                                GeneratorFamily.AI_INFERENCE_OPS,
                                "inferenceSurface",
                                "model routing, cache hits, and prompt rollouts across live inference paths",
                                "src/generators/ai_inference_ops.rs"),
                        expectation(
                                GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                                "retrievalSurface",
                                "stale embeddings, reranker drift, and citation coverage under corpus freshness pressure",
                                "src/generators/knowledge_retrieval.rs"),
                        expectation(
                                GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                                "guardrailSurface",
                                "tool-use regressions, rubric drift, and structured-output failures before release",
                                "src/generators/evaluation_and_guardrails.rs"),
                        expectation(
                                GeneratorFamily.AIBOM_PROVENANCE,
                                "provenanceSurface",
                                "model lineage, prompt assets, and reproducible cache metadata",
                                "src/generators/aibom_provenance.rs"),
                        expectation(
                                GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
                                "governanceSurface",
                                "retention, consent, and audit evidence across governed retrieval flows",
                                "src/generators/data_governance_compliance.rs"),
                        expectation(
                                GeneratorFamily.FINOPS_CAPACITY,
                                "capacitySurface",
                                "GPU scheduling, budget ceilings, and storage burn under shared capacity limits",
                                "src/generators/finops_capacity.rs")));
    }

    @Test
    void securityBlockchainDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderers(
                "security-blockchain",
                List.of(
                        expectation(
                                GeneratorFamily.IDENTITY_AND_TRUST,
                                "identitySurface",
                                "workload identity, signer provenance, and delegated access boundaries",
                                "src/generators/identity_and_trust.rs"),
                        expectation(
                                GeneratorFamily.AGENT_BOUNDARY_SECURITY,
                                "boundarySurface",
                                "unsafe delegation, wrong-principal action, and retrieval poisoning controls",
                                "src/generators/agent_boundary_security.rs"),
                        expectation(
                                GeneratorFamily.BLOCKCHAIN_PROTOCOL_OPS,
                                "chainSurface",
                                "rollups, validators, smart-account operations, and gas sponsorship controls",
                                "src/generators/blockchain_protocol_ops.rs"),
                        expectation(
                                GeneratorFamily.CROSS_CHAIN_INTEROP,
                                "interopSurface",
                                "chain abstraction, cross-domain execution, and trust-minimized transfer routing",
                                "src/generators/cross_chain_interop.rs"),
                        expectation(
                                GeneratorFamily.PROOF_AND_SEQUENCER_OPS,
                                "sequencerSurface",
                                "proof queues, ordering policy, MEV pressure, and finality windows",
                                "src/generators/proof_and_sequencer_ops.rs")));
    }

    @Test
    void healthProtocolDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderers(
                "health-protocol",
                List.of(
                        expectation(
                                GeneratorFamily.FHIR_PROFILE_GENERATOR,
                                "profileSurface",
                                "FHIR R4 resources, profile constraints, and deployable clinical bundles",
                                "src/generators/fhir_profile_generator.rs"),
                        expectation(
                                GeneratorFamily.SMART_LAUNCH_OAUTH,
                                "launchSurface",
                                "SMART launch context, scope negotiation, and token refresh boundaries",
                                "src/generators/smart_launch_oauth.rs"),
                        expectation(
                                GeneratorFamily.BULK_FHIR_POPULATION_OPS,
                                "bulkSurface",
                                "Bulk FHIR exports, NDJSON manifests, and cohort-scale analytics handoff",
                                "src/generators/bulk_fhir_population_ops.rs"),
                        expectation(
                                GeneratorFamily.HL7V2_FEED_OPS,
                                "feedSurface",
                                "ADT, ORM, ORU, SIU, and ACK/NACK handling across interface-engine boundaries",
                                "src/generators/hl7v2_feed_ops.rs"),
                        expectation(
                                GeneratorFamily.CLINICAL_WORKFLOW_EVENTS,
                                "workflowSurface",
                                "CDS Hooks, subscriptions, prior-auth flows, and clinical event triggers",
                                "src/generators/clinical_workflow_events.rs"),
                        expectation(
                                GeneratorFamily.DICOMWEB_IMAGING_OPS,
                                "imagingSurface",
                                "QIDO-RS, WADO-RS, STOW-RS, and conformance-aware imaging flows",
                                "src/generators/dicomweb_imaging_ops.rs"),
                        expectation(
                                GeneratorFamily.OPENEHR_SEMANTIC_RECORD_OPS,
                                "semanticSurface",
                                "archetypes, templates, compositions, and AQL query semantics",
                                "src/generators/openehr_semantic_record_ops.rs"),
                        expectation(
                                GeneratorFamily.DEVICE_TELEMETRY_CLINICAL,
                                "deviceSurface",
                                "IHE device telemetry, bedside monitor alerts, and point-of-care identity flow",
                                "src/generators/device_telemetry_clinical.rs"),
                        expectation(
                                GeneratorFamily.EMR_VENDOR_ADAPTER,
                                "vendorSurface",
                                "Epic and Oracle Health launch, scope, and error-mode normalization",
                                "src/generators/emr_vendor_adapter.rs"),
                        expectation(
                                GeneratorFamily.OCPP_CHARGEPOINT_OPS,
                                "chargingSurface",
                                "OCPP 2.x operations with brownfield 1.6 compatibility boundaries",
                                "src/generators/ocpp_chargepoint_ops.rs"),
                        expectation(
                                GeneratorFamily.OCPI_ROAMING_OPS,
                                "roamingSurface",
                                "OCPI roaming authorization, tariff exchange, and settlement synchronization",
                                "src/generators/ocpi_roaming_ops.rs"),
                        expectation(
                                GeneratorFamily.MCP_A2A_OPS,
                                "agentSurface",
                                "MCP auth, remote tool execution, AgentCard discovery, and A2A handoff control",
                                "src/generators/mcp_a2a_ops.rs"),
                        expectation(
                                GeneratorFamily.STREAMING_BUS_OPS,
                                "busSurface",
                                "Kafka, MQTT, NATS, replay windows, and consumer-lag control",
                                "src/generators/streaming_bus_ops.rs"),
                        expectation(
                                GeneratorFamily.SERVICE_MESH_RPC_OPS,
                                "rpcSurface",
                                "gRPC, GraphQL federation, timeout budgets, and routed mesh retries",
                                "src/generators/service_mesh_rpc_ops.rs"),
                        expectation(
                                GeneratorFamily.EDGE_CLIENT_RUNTIME,
                                "edgeSurface",
                                "edge execution, hydration boundaries, offline sync, and client cache recovery",
                                "src/generators/edge_client_runtime.rs"),
                        expectation(
                                GeneratorFamily.EMBEDDED_AGENTIC_PIPELINE,
                                "embeddedSurface",
                                "deterministic control loops, constrained inference, and firmware toolchain provenance",
                                "src/generators/embedded_agentic_pipeline.rs")));
    }

    @Test
    void overlayQuantumDepthIncludesTraceabilityAndFocus() {
        assertDedicatedRenderers(
                "overlay-quantum",
                List.of(
                        expectation(
                                GeneratorFamily.MULTILINGUAL_SECURITY_PACKS,
                                "languageSurface",
                                "english, chinese, russian, spanish, and arabic security-ops packs without nationality stereotypes",
                                "src/activities.rs"),
                        expectation(
                                GeneratorFamily.SECURITY_PERSONA_PACKS,
                                "personaSurface",
                                "bug bounty, incident command, reverse engineering, CTI, and SOC persona overlays",
                                "src/activities.rs"),
                        expectation(
                                GeneratorFamily.HYBRID_RUNTIME_OPS,
                                "runtimeSurface",
                                "hybrid jobs, managed sessions, and cancel-or-retry runtime orchestration",
                                "src/generators/hybrid_runtime_ops.rs"),
                        expectation(
                                GeneratorFamily.CAPACITY_COST_CONTROLLER,
                                "reservationSurface",
                                "reservations, task admission, queue visibility, and spend ceilings",
                                "src/generators/capacity_cost_controller.rs"),
                        expectation(
                                GeneratorFamily.BATCH_EXECUTION_TUNER,
                                "batchSurface",
                                "parameter sweeps, batch windows, and deterministic result collation",
                                "src/generators/batch_execution_tuner.rs"),
                        expectation(
                                GeneratorFamily.COMPILER_MAINTAINER,
                                "compilerSurface",
                                "transpiler passes, plugin manifests, and backend-target mismatch handling",
                                "src/generators/compiler_maintainer.rs"),
                        expectation(
                                GeneratorFamily.INTEROP_ADAPTER_ENGINEER,
                                "interopSurface",
                                "QIR, OpenQASM, adapter round-trip checks, and backend capability tags",
                                "src/generators/interop_adapter_engineer.rs"),
                        expectation(
                                GeneratorFamily.PREFLIGHT_CAPACITY_PLANNER,
                                "plannerSurface",
                                "resource estimation, backend profiles, and preflight admission gating",
                                "src/generators/preflight_capacity_planner.rs"),
                        expectation(
                                GeneratorFamily.SIMULATOR_PERFORMANCE_ENGINEER,
                                "simulatorSurface",
                                "GPU-backed simulation, local mode, and benchmark repeatability",
                                "src/generators/simulator_performance_engineer.rs")));
    }

    private static void assertDedicatedRenderers(String rendererGroup, List<Expectation> expectations) {
        for (Expectation expectation : expectations) {
            GeneratorRenderResult result = REGISTRY.render(
                    expectation.family(), new GeneratorRenderContext(config(), new Random(7L), List.of()));

            assertEquals(rendererGroup, result.metadata().get("rendererGroup"));
            assertEquals(expectation.family().cliValue(), result.metadata().get("family"));
            assertEquals("hospital-ocpp-quantum-control", result.metadata().get("project"));
            assertEquals(expectation.focusKey(), result.metadata().get("familyFocusKey"));
            assertEquals(expectation.focusValue(), result.metadata().get(expectation.focusKey()));
            assertFalse(result.message().isBlank());
            assertTrue(result.message().contains(expectation.family().message(JargonLevel.HIGH)));
            assertTrue(result.message().contains("Traceability is anchored to Rust and stakeholder-core."));

            Map<?, ?> traceabilityRow = (Map<?, ?>) result.metadata().get("traceabilityRow");
            assertEquals(expectation.family().cliValue(), traceabilityRow.get("family"));
            assertEquals("rust-stakeholder", traceabilityRow.get("sourceRepo"));
            assertEquals(expectation.sourcePath(), traceabilityRow.get("sourcePath"));
            assertEquals("stakeholder-core", traceabilityRow.get("contractRepo"));
            assertEquals("docs/generator-families.md", traceabilityRow.get("contractPath"));
            assertEquals("depth", traceabilityRow.get("parityClass"));
        }
    }

    private static Expectation expectation(
            GeneratorFamily family, String focusKey, String focusValue, String sourcePath) {
        return new Expectation(family, focusKey, focusValue, sourcePath);
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

    private record Expectation(GeneratorFamily family, String focusKey, String focusValue, String sourcePath) {}
}
