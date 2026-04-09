package com.stakeholder.activities;

import com.stakeholder.config.JargonLevel;
import java.util.Arrays;
import java.util.List;

public enum GeneratorFamily {
    CODE_ANALYZER(
            "code-analyzer",
            "Code analyzer",
            null,
            "reviewing typed interfaces and SDK drift across the active service graph",
            "triaging monorepo dependency edges, generated patches, and schema compatibility before merge",
            "replaying agent-authored patchsets against contract drift, ownership boundaries, and MCP tool assumptions"),
    DATA_PROCESSING(
            "data-processing",
            "Data processing",
            null,
            "refreshing embedding corpora, batch transforms, and event windows for the current dataset",
            "rebuilding hybrid retrieval indexes, semantic chunks, and NDJSON backfills for downstream consumers",
            "reconciling multimodal pipelines, lakehouse batch cuts, and evaluation-ready data slices under deterministic ordering"),
    JARGON(
            "jargon",
            "Jargon refresh",
            null,
            "keeping technical language current without drifting into fake-deep jargon",
            "switching phrasing toward credible 2026 agent, platform, protocol, and security terminology",
            "enforcing modern domain vocabulary so advanced output stays precise instead of sounding synthetic"),
    METRICS(
            "metrics",
            "Metrics",
            null,
            "tracking queue depth, latency bands, and cost signals across the active workload",
            "correlating token spend, SLO burn, GPU occupancy, and attestation coverage in one metrics lane",
            "folding evaluation score movement, blob economics, and runner pressure into a single operations dashboard"),
    NETWORK_ACTIVITY(
            "network-activity",
            "Network activity",
            "grpc",
            "observing RPC, event-stream, and adapter traffic across the current service boundary",
            "mapping MCP calls, inference APIs, registry fetches, and cross-domain message flow under backpressure",
            "profiling mixed gRPC, Kafka, MQTT, and bridge traffic while preserving replay semantics and retry windows"),
    SYSTEM_MONITORING(
            "system-monitoring",
            "System monitoring",
            null,
            "watching collector pressure, runner health, and process saturation on the active stack",
            "capturing GPU memory pressure, secret-scan spikes, sandbox failures, and scheduler queue churn",
            "stitching host telemetry, proof queues, provisioning lag, and policy denials into one operational heartbeat"),
    AGENT_WORKFLOWS(
            "agent-workflows",
            "Agent workflows",
            "mcp",
            "routing coding-agent work through review queues and approval gates",
            "coordinating delegated patch runs, blocked tool calls, and human checkpoints across multiple repos",
            "orchestrating branch handoff envelopes, MCP leases, and merge-safe approval chains for background agents"),
    AI_INFERENCE_OPS(
            "ai-inference-ops",
            "AI inference ops",
            "responses-api",
            "monitoring model routing, cache hits, and prompt rollouts for live inference paths",
            "tuning fallback chains, context-budget pressure, and eval regressions across provider tiers",
            "balancing retrieval freshness, safety fallbacks, and token-cost envelopes under multi-model orchestration"),
    PLATFORM_ENGINEERING(
            "platform-engineering",
            "Platform engineering",
            null,
            "maintaining golden paths, service templates, and workload identity for self-service delivery",
            "resolving platform policy denials, tenant quotas, and template drift inside the internal developer portal",
            "reconciling workload identity, cluster tenancy, policy bundles, and queue fairness across platform control planes"),
    SUPPLY_CHAIN_SECURITY(
            "supply-chain-security",
            "Supply-chain security",
            null,
            "checking artifact trust, secret exposure, and dependency health before release",
            "verifying provenance attestations, AIBOM coverage, revocation posture, and tamper signals across build lanes",
            "gating release promotion on signed artifacts, dependency substitution checks, and cross-tool trust evidence"),
    OBSERVABILITY_AI_RUNTIME(
            "observability-ai-runtime",
            "Observability AI runtime",
            null,
            "recording traces, token spend, and latency bands for the active runtime",
            "tracking OTel collector saturation, span cardinality, and GPU telemetry alongside tool-call traces",
            "driving burn-rate analysis across inference queues, cost attribution, and distributed reasoning spans"),
    DELIVERY_PREVIEW_OPS(
            "delivery-preview-ops",
            "Delivery preview ops",
            null,
            "managing preview environments, feature flags, and canary promotions for current changes",
            "holding rollout gates on runner saturation, preview drift, and canary health regression signals",
            "sequencing flag freezes, rollback windows, and staged promotion rules across agent-authored delivery pipelines"),
    EVALUATION_AND_GUARDRAILS(
            "evaluation-and-guardrails",
            "Evaluation and guardrails",
            "responses-api",
            "running evaluation suites and schema checks against generated outputs",
            "measuring tool-use regressions, rubric drift, and structured-output failures before release",
            "enforcing guardrail coverage against jailbreak attempts, policy escapes, and benchmark regressions in one pass"),
    KNOWLEDGE_RETRIEVAL(
            "knowledge-retrieval",
            "Knowledge retrieval",
            "responses-api",
            "refreshing vector search indexes and citation coverage for current knowledge sets",
            "repairing stale embeddings, reranker drift, and low-confidence retrieval before answers ship",
            "rebalancing hybrid search, chunk overlap, and provenance coverage under corpus freshness pressure"),
    EDGE_CLIENT_RUNTIME(
            "edge-client-runtime",
            "Edge client runtime",
            "webtransport",
            "stabilizing edge execution, streaming UI, and offline sync paths",
            "handling hydration boundaries, wasm loads, and client cache invalidation across distributed edges",
            "coordinating edge cold-start budgets, sync conflict recovery, and realtime streaming under browser constraints"),
    IDENTITY_AND_TRUST(
            "identity-and-trust",
            "Identity and trust",
            null,
            "maintaining signer trust, workload identity, and delegated access boundaries",
            "rotating keys, validating session trust, and reconciling workload provenance across service edges",
            "stitching smart-account recovery, signer provenance, and delegated authority windows into one trust fabric"),
    AIBOM_PROVENANCE(
            "aibom-provenance",
            "AIBOM provenance",
            null,
            "capturing model lineage and runtime dependency provenance for generated outputs",
            "versioning prompt assets, adapter state, and AIBOM evidence with reproducible cache metadata",
            "reconstructing full generation provenance across model lineage, adapter drift, and environment evidence"),
    AGENT_BOUNDARY_SECURITY(
            "agent-boundary-security",
            "Agent boundary security",
            "mcp",
            "checking unsafe delegation, tool overreach, and principal confusion in agent flows",
            "blocking retrieval poisoning, denial-of-wallet patterns, and cross-boundary action mistakes",
            "hardening planning loops against wrong-principal execution, policy bypass, and agent-to-agent trust collapse"),
    EMBEDDED_AGENTIC_PIPELINE(
            "embedded-agentic-pipeline",
            "Embedded agentic pipeline",
            null,
            "keeping deterministic control loops and constrained inference pipelines stable",
            "coordinating firmware toolchains, on-device models, and traceable agent steps under resource limits",
            "balancing safety-critical timing, agentic orchestration, and hardware build provenance across embedded fleets"),
    DATA_GOVERNANCE_COMPLIANCE(
            "data-governance-compliance",
            "Data governance compliance",
            null,
            "applying retention, consent, and regional handling rules to active data flows",
            "enforcing governed retrieval, explainability evidence, and audit-ready policy checkpoints",
            "reconciling cross-border data use, consent state, and explainability artifacts across automated workflows"),
    FINOPS_CAPACITY(
            "finops-capacity",
            "FinOps capacity",
            null,
            "tracking spend, queue pressure, and storage growth across the active platform",
            "tuning GPU scheduling, preview-environment budgets, and token-cost ceilings against workload demand",
            "balancing inference burn, runner economics, and blob-or-data-availability spend under shared capacity limits"),
    BLOCKCHAIN_PROTOCOL_OPS(
            "blockchain-protocol-ops",
            "Blockchain protocol ops",
            null,
            "processing rollup, validator, and smart-account operations against the current chain state",
            "coordinating gas sponsorship, bridge verification, and sequencer health across modern execution layers",
            "managing rollup batch flow, validator signals, and smart-account recovery under chain-abstraction demands"),
    CROSS_CHAIN_INTEROP(
            "cross-chain-interop",
            "Cross-chain interop",
            null,
            "routing assets and calls across chain boundaries with explicit trust assumptions",
            "coordinating wallet-level chain abstraction, liquidity paths, and cross-domain execution guarantees",
            "sequencing sign-once cross-chain flows with interoperability proofs, settlement safeguards, and bridge-minimized UX"),
    PROOF_AND_SEQUENCER_OPS(
            "proof-and-sequencer-ops",
            "Proof and sequencer ops",
            null,
            "watching proof jobs, sequencing order, and batch submission latency",
            "triaging prover queues, ordering policy, MEV pressure, and data-availability throughput",
            "balancing shared sequencing, proof lag, and finality windows across rollup infrastructure"),
    HYBRID_RUNTIME_OPS(
            "hybrid-runtime-ops",
            "Hybrid runtime ops",
            null,
            "moving jobs between notebooks, sessions, and managed runtime batches",
            "coordinating hybrid execution windows, session reuse, and cancel-or-retry flow on quantum runtimes",
            "balancing job orchestration across managed sessions, classical sidecars, and backend-specific runtime constraints"),
    CAPACITY_COST_CONTROLLER(
            "capacity-cost-controller",
            "Capacity and cost controller",
            null,
            "tracking queue visibility, reservations, and spend controls on quantum backends",
            "planning reservations, task admission, and budget alerts against scarce quantum capacity",
            "holding hybrid workloads inside reservation windows, spend ceilings, and backend admission constraints"),
    BATCH_EXECUTION_TUNER(
            "batch-execution-tuner",
            "Batch execution tuner",
            null,
            "assembling parameter sweeps and batched runs for repeatable throughput checks",
            "optimizing batch submission order, parametric compilation reuse, and aggregated result handling",
            "driving high-volume sweep orchestration across batch windows, compilation reuse, and deterministic result collation"),
    COMPILER_MAINTAINER(
            "compiler-maintainer",
            "Compiler maintainer",
            "openqasm3",
            "adjusting transpiler passes and backend targets for the active circuit set",
            "managing compiler plugins, routing passes, and backend-target mismatch under current constraints",
            "tuning custom transpiler stacks, plugin manifests, and pass-manager behavior across backend generations"),
    INTEROP_ADAPTER_ENGINEER(
            "interop-adapter-engineer",
            "Interop adapter engineer",
            "qir",
            "translating execution artifacts across QIR, OpenQASM, and adapter boundaries",
            "running round-trip tests between quantum IRs while preserving semantics and backend capability tags",
            "reconciling QIR adaptor output, OpenQASM transforms, and backend profile gaps under interop pressure"),
    PREFLIGHT_CAPACITY_PLANNER(
            "preflight-capacity-planner",
            "Preflight capacity planner",
            null,
            "estimating runtime size, qubit demand, and target fit before dispatch",
            "checking backend profiles, resource estimators, and admission limits before quantum submission",
            "gating workloads on qubit budgets, error tolerance, and backend profile constraints before execution"),
    SIMULATOR_PERFORMANCE_ENGINEER(
            "simulator-performance-engineer",
            "Simulator performance engineer",
            "openqasm3",
            "profiling simulator throughput and local-mode performance under current inputs",
            "tuning GPU-backed simulators, container runtime compatibility, and local execution benchmarks",
            "balancing CUDA-class simulation paths, local-mode orchestration, and benchmark repeatability across quantum stacks"),
    FHIR_PROFILE_GENERATOR(
            "fhir-profile-generator",
            "FHIR profile generator",
            "fhir-r4",
            "generating FHIR R4 resources and profile-constrained clinical records",
            "assembling US Core aligned resource graphs, validation rules, and vendor-capability expectations",
            "producing profile-aware clinical bundles with deployable R4 semantics and forward-looking R5 awareness"),
    SMART_LAUNCH_OAUTH(
            "smart-launch-oauth",
            "SMART launch OAuth",
            "smart-launch",
            "negotiating SMART launch context, scopes, and token refresh for current EHR sessions",
            "stabilizing standalone and EHR launch flows with patient, user, and encounter context propagation",
            "coordinating SMART launch scope negotiation, refresh policy, and context handoff across vendor sandboxes"),
    BULK_FHIR_POPULATION_OPS(
            "bulk-fhir-population-ops",
            "Bulk FHIR population ops",
            "bulk-fhir",
            "preparing Bulk FHIR exports and NDJSON population slices",
            "running cohort-scale export jobs, manifest tracking, and downstream analytics handoff for current datasets",
            "coordinating high-volume Bulk Data exports, job polling, and reconciliation against multi-tenant analytics lanes"),
    HL7V2_FEED_OPS(
            "hl7v2-feed-ops",
            "HL7 v2 feed ops",
            "hl7v2",
            "processing HL7 v2 feeds, ACKs, and interface-engine traffic for operational workflows",
            "mapping ADT, ORM, ORU, and SIU message behavior into modern validation and bridge checks",
            "reconciling repeating-segment churn, ACK/NACK behavior, and v2-to-FHIR boundary conditions at scale"),
    CLINICAL_WORKFLOW_EVENTS(
            "clinical-workflow-events",
            "Clinical workflow events",
            "fhir-r4",
            "driving CDS Hooks, subscriptions, and workflow triggers for clinical events",
            "coordinating prior-auth, questionnaire, measure, and case-reporting workflows across FHIR operations",
            "orchestrating subscription events, CDS cards, and payer-provider workflow state under live clinical policy"),
    DICOMWEB_IMAGING_OPS(
            "dicomweb-imaging-ops",
            "DICOMweb imaging ops",
            "dicomweb",
            "serving imaging studies over QIDO-RS, WADO-RS, and STOW-RS surfaces",
            "matching DICOMweb retrieval, storage, and conformance expectations across modality and PACS flows",
            "balancing DICOMweb ingest, retrieval, and conformance evidence across imaging workflows and archive tiers"),
    OPENEHR_SEMANTIC_RECORD_OPS(
            "openehr-semantic-record-ops",
            "openEHR semantic record ops",
            "openehr",
            "managing openEHR compositions, templates, and semantic record queries",
            "running archetype-driven data capture with AQL query validation and template-aware persistence",
            "reconciling template semantics, composition versioning, and AQL-driven access across longitudinal records"),
    DEVICE_TELEMETRY_CLINICAL(
            "device-telemetry-clinical",
            "Device telemetry clinical",
            "ihe-device",
            "tracking bedside device telemetry, alerts, and identity across care settings",
            "coordinating IHE device flows, monitor alarms, and point-of-care telemetry under clinical constraints",
            "balancing device identity, telemetry burst control, and alert escalation across monitored clinical infrastructure"),
    EMR_VENDOR_ADAPTER(
            "emr-vendor-adapter",
            "EMR vendor adapter",
            "epic-fhir",
            "aligning application flows with Epic and Oracle Health integration patterns",
            "normalizing vendor-specific launch, scope, and error behavior across EHR adapter boundaries",
            "reconciling vendor capability gaps, sandbox behavior, and operational error modes across EMR integrations"),
    OCPP_CHARGEPOINT_OPS(
            "ocpp-chargepoint-ops",
            "OCPP chargepoint ops",
            "ocpp-2.x",
            "handling charger sessions, heartbeats, and smart-charging commands over OCPP",
            "coordinating OCPP 2.x profiles, security state, and brownfield 1.6 compatibility boundaries",
            "balancing certificate flows, smart charging, and transaction lifecycle state across mixed charger fleets"),
    OCPI_ROAMING_OPS(
            "ocpi-roaming-ops",
            "OCPI roaming ops",
            "ocpi-2.x",
            "processing roaming session state, tariffs, and settlement exchanges over OCPI",
            "managing CPO-to-EMSP synchronization, booking flow, and roaming settlement at protocol boundaries",
            "reconciling roaming authorization, tariff exchange, and multi-party settlement across current OCPI networks"),
    MCP_A2A_OPS(
            "mcp-a2a-ops",
            "MCP and A2A ops",
            "mcp",
            "routing tool calls and agent messages across MCP and A2A surfaces",
            "aligning MCP auth, remote tool execution, and AgentCard discovery across agent networks",
            "coordinating MCP resource exchange and A2A task handoff with explicit auth boundaries and policy controls"),
    STREAMING_BUS_OPS(
            "streaming-bus-ops",
            "Streaming bus ops",
            "kafka",
            "moving events through MQTT, NATS, and Kafka channels with replay-safe routing",
            "balancing consumer lag, retention, and JetStream-or-Kafka durability across streaming workloads",
            "coordinating brokered telemetry, replay windows, and stream processor contracts across multi-bus topologies"),
    SERVICE_MESH_RPC_OPS(
            "service-mesh-rpc-ops",
            "Service mesh RPC ops",
            "grpc",
            "serving typed RPC and federated API traffic across internal service boundaries",
            "tuning gRPC, GraphQL federation, and timeout budgets across routed service meshes",
            "reconciling streaming RPC, schema composition, and retry propagation across mesh-aware service fabrics"),
    MULTILINGUAL_SECURITY_PACKS(
            "multilingual-security-packs",
            "Multilingual security packs",
            null,
            "layering multilingual security-ops phrasing over deterministic incident and threat workflows",
            "switching between English, Chinese, Russian, Spanish, and Arabic security discourse without stereotyping operators",
            "coordinating forum-tone overlays, CTI idiom shifts, and incident language packs while keeping the simulator operationally safe"),
    SECURITY_PERSONA_PACKS(
            "security-persona-packs",
            "Security persona packs",
            null,
            "shaping output around bug bounty, incident response, and SOC operator perspectives",
            "switching between threat hunter, reverse engineer, CTI, and incident commander viewpoints for the same evidence lane",
            "binding dark-market watcher, CTI brief writer, and response leadership personas onto deterministic security output without unsafe detail");

    private final String cliValue;
    private final String title;
    private final String protocol;
    private final String low;
    private final String high;
    private final String extreme;

    GeneratorFamily(String cliValue, String title, String protocol, String low, String high, String extreme) {
        this.cliValue = cliValue;
        this.title = title;
        this.protocol = protocol;
        this.low = low;
        this.high = high;
        this.extreme = extreme;
    }

    public String cliValue() {
        return cliValue;
    }

    public String title() {
        return title;
    }

    public String protocol() {
        return protocol;
    }

    public String message(JargonLevel level) {
        return switch (level) {
            case LOW, MEDIUM -> low;
            case HIGH -> high;
            case EXTREME -> extreme;
        };
    }

    public static List<String> cliValues() {
        return Arrays.stream(values()).map(GeneratorFamily::cliValue).toList();
    }
}
