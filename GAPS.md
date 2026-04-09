> [!NOTE]
> Missing or deferred behavior must fail fast and be tracked explicitly. No placeholder behavior should mask absent parity work.

# Java Gaps

## Current explicit gaps
- `java.experimental.live-provider-hooks-pending`: provider adapters, encrypted prompt/cache state, and consumer-account adapters are designed but not implemented in the runtime yet.

## Closed in this tranche
- `java.runtime.generator-parity-pending`: replaced by the scheduler-driven runtime, deterministic JSON activity emission, family routing, keyword-based protocol selection, and security flavor overlays.
- `java.maven-wrapper-pending`: Maven Wrapper and the full Java 25 Docker/plugin gate are now in place.
- `java.classic-six-depth-pending`: dedicated renderer depth, richer deterministic messages, and traceability rows are now in place for `code_analyzer`, `data_processing`, `jargon`, `metrics`, `network_activity`, and `system_monitoring`.
- `java.modern-core-depth-pending`: dedicated renderer depth, richer deterministic messages, and traceability rows are now in place for `agent_workflows`, `platform_engineering`, `observability_ai_runtime`, `delivery_preview_ops`, and `supply_chain_security`.
- `java.ai-governance-depth-pending`: dedicated renderer depth, richer deterministic messages, and traceability rows are now in place for `ai_inference_ops`, `knowledge_retrieval`, `evaluation_and_guardrails`, `aibom_provenance`, `data_governance_compliance`, and `finops_capacity`.
- `java.security-blockchain-depth-pending`: dedicated renderer depth, richer deterministic messages, and traceability rows are now in place for `identity_and_trust`, `agent_boundary_security`, `blockchain_protocol_ops`, `cross_chain_interop`, and `proof_and_sequencer_ops`.
- `java.health-protocol-depth-pending`: dedicated renderer depth, richer deterministic messages, and traceability rows are now in place for the healthcare, charging, protocol, edge, and embedded families.
- `java.overlay-quantum-depth-pending`: dedicated renderer depth, richer deterministic messages, and traceability rows are now in place for multilingual overlays, persona overlays, and the quantum families.

## Guardrail
- Do not implement behavior without adding a corresponding traceability row back to audited Rust sources.
