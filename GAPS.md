> [!NOTE]
> Missing or deferred behavior must fail fast and be tracked explicitly. No placeholder behavior should mask absent parity work.

# Java Gaps

The items below are the remaining gaps on the path from the current Java provider runtime to the workspace-wide full live-provider target.

## Current explicit gaps
- `java.experimental.browser-bootstrap-externalized`: consumer-session capture is implemented through an external bootstrap command configured via `STAKEHOLDER_BROWSER_BOOTSTRAP_CMD`; Java does not embed browser automation directly.
- `java.experimental.live-provider-tests-opt-in`: live provider tests remain opt-in and require local credentials or imported session material.

## Closed in this tranche
- `java.experimental.live-provider-hooks-pending`: provider profiles, prompt assets and versions, personalization profiles, encrypted local material handling, cache/provenance persistence, consumer-session replay adapters, and an opt-in live integration harness are now implemented in the Java runtime.
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
