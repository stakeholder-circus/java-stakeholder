package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public final class GeneratorRegistry {
    private final Map<GeneratorFamily, GeneratorRenderer> renderers;

    private GeneratorRegistry(List<GeneratorRenderer> rendererSet) {
        this.renderers = new EnumMap<>(GeneratorFamily.class);
        for (GeneratorRenderer renderer : rendererSet) {
            for (GeneratorFamily family : renderer.families()) {
                GeneratorRenderer previous = renderers.put(family, renderer);
                if (previous != null) {
                    throw new IllegalStateException("duplicate renderer registration for " + family.cliValue());
                }
            }
        }

        Set<GeneratorFamily> missing = EnumSet.allOf(GeneratorFamily.class);
        missing.removeAll(renderers.keySet());
        if (!missing.isEmpty()) {
            throw new IllegalStateException("missing renderers for " + missing);
        }
    }

    public static GeneratorRegistry defaultRegistry() {
        List<GeneratorRenderer> renderers = new ArrayList<>();
        renderers.add(new CodeAnalyzerRenderer());
        renderers.add(new DataProcessingRenderer());
        renderers.add(new JargonRenderer());
        renderers.add(new MetricsRenderer());
        renderers.add(new NetworkActivityRenderer());
        renderers.add(new SystemMonitoringRenderer());
        renderers.add(new AgentWorkflowsRenderer());
        renderers.add(new PlatformEngineeringRenderer());
        renderers.add(new ObservabilityAIRuntimeRenderer());
        renderers.add(new DeliveryPreviewOpsRenderer());
        renderers.add(new SupplyChainSecurityRenderer());

        renderers.add(configured(
                GeneratorFamily.AI_INFERENCE_OPS,
                "ai-governance",
                "ai inference ops",
                "inferenceSurface",
                "model routing, cache hits, and prompt rollouts across live inference paths",
                "src/generators/ai_inference_ops.rs",
                (metadata, family, context) -> metadata.put(
                        "experimentalBoundary", context.flavors().contains("experimental-live-provider"))));
        renderers.add(configured(
                GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                "ai-governance",
                "knowledge retrieval",
                "retrievalSurface",
                "stale embeddings, reranker drift, and citation coverage under corpus freshness pressure",
                "src/generators/knowledge_retrieval.rs",
                (metadata, family, context) -> metadata.put(
                        "experimentalBoundary", context.flavors().contains("experimental-live-provider"))));
        renderers.add(configured(
                GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                "ai-governance",
                "evaluation and guardrails",
                "guardrailSurface",
                "tool-use regressions, rubric drift, and structured-output failures before release",
                "src/generators/evaluation_and_guardrails.rs",
                (metadata, family, context) -> metadata.put(
                        "experimentalBoundary", context.flavors().contains("experimental-live-provider"))));
        renderers.add(configured(
                GeneratorFamily.AIBOM_PROVENANCE,
                "ai-governance",
                "aibom provenance",
                "provenanceSurface",
                "model lineage, prompt assets, and reproducible cache metadata",
                "src/generators/aibom_provenance.rs",
                (metadata, family, context) -> metadata.put(
                        "experimentalBoundary", context.flavors().contains("experimental-live-provider"))));
        renderers.add(configured(
                GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
                "ai-governance",
                "data governance compliance",
                "governanceSurface",
                "retention, consent, and audit evidence across governed retrieval flows",
                "src/generators/data_governance_compliance.rs",
                (metadata, family, context) -> metadata.put(
                        "experimentalBoundary", context.flavors().contains("experimental-live-provider"))));
        renderers.add(configured(
                GeneratorFamily.FINOPS_CAPACITY,
                "ai-governance",
                "finops capacity",
                "capacitySurface",
                "GPU scheduling, budget ceilings, and storage burn under shared capacity limits",
                "src/generators/finops_capacity.rs",
                (metadata, family, context) -> metadata.put(
                        "experimentalBoundary", context.flavors().contains("experimental-live-provider"))));

        renderers.add(configured(
                GeneratorFamily.IDENTITY_AND_TRUST,
                "security-blockchain",
                "identity and trust",
                "identitySurface",
                "workload identity, signer provenance, and delegated access boundaries",
                "src/generators/identity_and_trust.rs",
                (metadata, family, context) -> metadata.put("trustBoundary", family.cliValue())));
        renderers.add(configured(
                GeneratorFamily.AGENT_BOUNDARY_SECURITY,
                "security-blockchain",
                "agent boundary security",
                "boundarySurface",
                "unsafe delegation, wrong-principal action, and retrieval poisoning controls",
                "src/generators/agent_boundary_security.rs",
                (metadata, family, context) -> metadata.put("trustBoundary", family.cliValue())));
        renderers.add(configured(
                GeneratorFamily.BLOCKCHAIN_PROTOCOL_OPS,
                "security-blockchain",
                "blockchain protocol ops",
                "chainSurface",
                "rollups, validators, smart-account operations, and gas sponsorship controls",
                "src/generators/blockchain_protocol_ops.rs",
                (metadata, family, context) -> metadata.put("trustBoundary", family.cliValue())));
        renderers.add(configured(
                GeneratorFamily.CROSS_CHAIN_INTEROP,
                "security-blockchain",
                "cross-chain interop",
                "interopSurface",
                "chain abstraction, cross-domain execution, and trust-minimized transfer routing",
                "src/generators/cross_chain_interop.rs",
                (metadata, family, context) -> metadata.put("trustBoundary", family.cliValue())));
        renderers.add(configured(
                GeneratorFamily.PROOF_AND_SEQUENCER_OPS,
                "security-blockchain",
                "proof and sequencer ops",
                "sequencerSurface",
                "proof queues, ordering policy, MEV pressure, and finality windows",
                "src/generators/proof_and_sequencer_ops.rs",
                (metadata, family, context) -> metadata.put("trustBoundary", family.cliValue())));

        renderers.add(configured(
                GeneratorFamily.FHIR_PROFILE_GENERATOR,
                "health-protocol",
                "fhir profile generator",
                "profileSurface",
                "FHIR R4 resources, profile constraints, and deployable clinical bundles",
                "src/generators/fhir_profile_generator.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.SMART_LAUNCH_OAUTH,
                "health-protocol",
                "smart launch oauth",
                "launchSurface",
                "SMART launch context, scope negotiation, and token refresh boundaries",
                "src/generators/smart_launch_oauth.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.BULK_FHIR_POPULATION_OPS,
                "health-protocol",
                "bulk fhir population ops",
                "bulkSurface",
                "Bulk FHIR exports, NDJSON manifests, and cohort-scale analytics handoff",
                "src/generators/bulk_fhir_population_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.HL7V2_FEED_OPS,
                "health-protocol",
                "hl7v2 feed ops",
                "feedSurface",
                "ADT, ORM, ORU, SIU, and ACK/NACK handling across interface-engine boundaries",
                "src/generators/hl7v2_feed_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.CLINICAL_WORKFLOW_EVENTS,
                "health-protocol",
                "clinical workflow events",
                "workflowSurface",
                "CDS Hooks, subscriptions, prior-auth flows, and clinical event triggers",
                "src/generators/clinical_workflow_events.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.DICOMWEB_IMAGING_OPS,
                "health-protocol",
                "dicomweb imaging ops",
                "imagingSurface",
                "QIDO-RS, WADO-RS, STOW-RS, and conformance-aware imaging flows",
                "src/generators/dicomweb_imaging_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.OPENEHR_SEMANTIC_RECORD_OPS,
                "health-protocol",
                "openehr semantic record ops",
                "semanticSurface",
                "archetypes, templates, compositions, and AQL query semantics",
                "src/generators/openehr_semantic_record_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.DEVICE_TELEMETRY_CLINICAL,
                "health-protocol",
                "device telemetry clinical",
                "deviceSurface",
                "IHE device telemetry, bedside monitor alerts, and point-of-care identity flow",
                "src/generators/device_telemetry_clinical.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.EMR_VENDOR_ADAPTER,
                "health-protocol",
                "emr vendor adapter",
                "vendorSurface",
                "Epic and Oracle Health launch, scope, and error-mode normalization",
                "src/generators/emr_vendor_adapter.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.OCPP_CHARGEPOINT_OPS,
                "health-protocol",
                "ocpp chargepoint ops",
                "chargingSurface",
                "OCPP 2.x operations with brownfield 1.6 compatibility boundaries",
                "src/generators/ocpp_chargepoint_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.OCPI_ROAMING_OPS,
                "health-protocol",
                "ocpi roaming ops",
                "roamingSurface",
                "OCPI roaming authorization, tariff exchange, and settlement synchronization",
                "src/generators/ocpi_roaming_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.MCP_A2A_OPS,
                "health-protocol",
                "mcp and a2a ops",
                "agentSurface",
                "MCP auth, remote tool execution, AgentCard discovery, and A2A handoff control",
                "src/generators/mcp_a2a_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.STREAMING_BUS_OPS,
                "health-protocol",
                "streaming bus ops",
                "busSurface",
                "Kafka, MQTT, NATS, replay windows, and consumer-lag control",
                "src/generators/streaming_bus_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.SERVICE_MESH_RPC_OPS,
                "health-protocol",
                "service mesh rpc ops",
                "rpcSurface",
                "gRPC, GraphQL federation, timeout budgets, and routed mesh retries",
                "src/generators/service_mesh_rpc_ops.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.EDGE_CLIENT_RUNTIME,
                "health-protocol",
                "edge client runtime",
                "edgeSurface",
                "edge execution, hydration boundaries, offline sync, and client cache recovery",
                "src/generators/edge_client_runtime.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));
        renderers.add(configured(
                GeneratorFamily.EMBEDDED_AGENTIC_PIPELINE,
                "health-protocol",
                "embedded agentic pipeline",
                "embeddedSurface",
                "deterministic control loops, constrained inference, and firmware toolchain provenance",
                "src/generators/embedded_agentic_pipeline.rs",
                (metadata, family, context) ->
                        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol())));

        renderers.add(configured(
                GeneratorFamily.MULTILINGUAL_SECURITY_PACKS,
                "overlay-quantum",
                "multilingual security packs",
                "languageSurface",
                "english, chinese, russian, spanish, and arabic security-ops packs without nationality stereotypes",
                "src/activities.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.SECURITY_PERSONA_PACKS,
                "overlay-quantum",
                "security persona packs",
                "personaSurface",
                "bug bounty, incident command, reverse engineering, CTI, and SOC persona overlays",
                "src/activities.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.HYBRID_RUNTIME_OPS,
                "overlay-quantum",
                "hybrid runtime ops",
                "runtimeSurface",
                "hybrid jobs, managed sessions, and cancel-or-retry runtime orchestration",
                "src/generators/hybrid_runtime_ops.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.CAPACITY_COST_CONTROLLER,
                "overlay-quantum",
                "capacity and cost controller",
                "reservationSurface",
                "reservations, task admission, queue visibility, and spend ceilings",
                "src/generators/capacity_cost_controller.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.BATCH_EXECUTION_TUNER,
                "overlay-quantum",
                "batch execution tuner",
                "batchSurface",
                "parameter sweeps, batch windows, and deterministic result collation",
                "src/generators/batch_execution_tuner.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.COMPILER_MAINTAINER,
                "overlay-quantum",
                "compiler maintainer",
                "compilerSurface",
                "transpiler passes, plugin manifests, and backend-target mismatch handling",
                "src/generators/compiler_maintainer.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.INTEROP_ADAPTER_ENGINEER,
                "overlay-quantum",
                "interop adapter engineer",
                "interopSurface",
                "QIR, OpenQASM, adapter round-trip checks, and backend capability tags",
                "src/generators/interop_adapter_engineer.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.PREFLIGHT_CAPACITY_PLANNER,
                "overlay-quantum",
                "preflight capacity planner",
                "plannerSurface",
                "resource estimation, backend profiles, and preflight admission gating",
                "src/generators/preflight_capacity_planner.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));
        renderers.add(configured(
                GeneratorFamily.SIMULATOR_PERFORMANCE_ENGINEER,
                "overlay-quantum",
                "simulator performance engineer",
                "simulatorSurface",
                "GPU-backed simulation, local mode, and benchmark repeatability",
                "src/generators/simulator_performance_engineer.rs",
                (metadata, family, context) ->
                        metadata.put("overlayCount", context.flavors().size())));

        return new GeneratorRegistry(renderers);
    }

    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        return rendererFor(family).render(family, context);
    }

    public Set<GeneratorFamily> registeredFamilies() {
        return EnumSet.copyOf(renderers.keySet());
    }

    private GeneratorRenderer rendererFor(GeneratorFamily family) {
        GeneratorRenderer renderer = renderers.get(family);
        if (renderer == null) {
            throw new IllegalStateException("no renderer registered for " + family.cliValue());
        }
        return renderer;
    }

    private static GeneratorRenderer configured(
            GeneratorFamily family,
            String rendererGroup,
            String label,
            String focusKey,
            String focusValue,
            String sourcePath) {
        return configured(family, rendererGroup, label, focusKey, focusValue, sourcePath, null);
    }

    private static GeneratorRenderer configured(
            GeneratorFamily family,
            String rendererGroup,
            String label,
            String focusKey,
            String focusValue,
            String sourcePath,
            ConfiguredFamilyRenderer.MetadataAugmentor augmentor) {
        return new ConfiguredFamilyRenderer(family, rendererGroup, label, focusKey, focusValue, sourcePath, augmentor);
    }
}
