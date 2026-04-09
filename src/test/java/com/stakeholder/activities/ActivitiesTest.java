package com.stakeholder.activities;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import com.stakeholder.output.NormalizedEvent;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

final class ActivitiesTest {
    @Test
    void eventsAreDeterministicForTheSameSeed() {
        SessionConfig config = config();

        List<NormalizedEvent> first = Activities.buildEvents(config);
        List<NormalizedEvent> second = Activities.buildEvents(config);

        assertEquals(first, second);
    }

    @Test
    void keywordRoutingIncludesHealthChargingAndQuantumFamilies() {
        List<GeneratorFamily> families = Activities.eligibleFamilies(config());

        assertTrue(families.contains(GeneratorFamily.FHIR_PROFILE_GENERATOR));
        assertTrue(families.contains(GeneratorFamily.OCPP_CHARGEPOINT_OPS));
        assertTrue(families.contains(GeneratorFamily.HYBRID_RUNTIME_OPS));
        assertTrue(families.contains(GeneratorFamily.MULTILINGUAL_SECURITY_PACKS));
        assertTrue(families.contains(GeneratorFamily.SECURITY_PERSONA_PACKS));
    }

    @Test
    void securityFlavorOverlaysAreApplied() {
        List<String> flavors =
                Activities.resolveFlavors(config(), GeneratorFamily.SUPPLY_CHAIN_SECURITY, new java.util.Random(7L));

        assertTrue(flavors.stream().anyMatch(value -> value.startsWith("multilingual-security:")));
        assertTrue(flavors.stream().anyMatch(value -> value.startsWith("security-persona:")));
    }

    @Test
    void newlyDedicatedFamiliesProduceSeededJsonSmoke() {
        for (GeneratorFamily family : remainingDepthFamilies()) {
            ActivitySelection selection = new ActivitySelection(
                    family, Activities.resolveFlavors(config(), family, new java.util.Random(7L)), "generator");

            List<NormalizedEvent> events = Activities.buildEvents(config(), List.of(selection));
            NormalizedEvent activity = events.stream()
                    .filter(event -> event.eventType().equals("activity"))
                    .findFirst()
                    .orElseThrow();

            assertEquals(family.cliValue(), activity.context().get("family"));
            assertEquals(family.message(JargonLevel.HIGH), extractFamilyMessage(activity.message()));
            assertTrue(activity.context().containsKey("traceabilityRow"));
            Map<?, ?> traceability = (Map<?, ?>) activity.context().get("traceabilityRow");
            assertEquals("rust-stakeholder", traceability.get("sourceRepo"));
            assertEquals("stakeholder-core", traceability.get("contractRepo"));
        }
    }

    @Test
    void newlyDedicatedFamiliesProduceSeededTextSmoke() {
        SessionConfig textConfig = textConfig();
        for (GeneratorFamily family : remainingDepthFamilies()) {
            ActivitySelection selection = new ActivitySelection(
                    family, Activities.resolveFlavors(textConfig, family, new java.util.Random(7L)), "generator");

            List<String> lines = Activities.buildTextActivities(textConfig, List.of(selection));
            assertEquals(1, lines.size());
            assertTrue(lines.getFirst().contains(family.title()));
            assertTrue(lines.getFirst().contains(family.message(JargonLevel.HIGH)));
        }
    }

    private SessionConfig config() {
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

    private SessionConfig textConfig() {
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
                OutputFormat.TEXT,
                true,
                false);
    }

    private static String extractFamilyMessage(String message) {
        return message.substring(message.indexOf(": ") + 2, message.indexOf(" Traceability is anchored"));
    }

    private static List<GeneratorFamily> remainingDepthFamilies() {
        return List.of(
                GeneratorFamily.AI_INFERENCE_OPS,
                GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                GeneratorFamily.AIBOM_PROVENANCE,
                GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
                GeneratorFamily.FINOPS_CAPACITY,
                GeneratorFamily.IDENTITY_AND_TRUST,
                GeneratorFamily.AGENT_BOUNDARY_SECURITY,
                GeneratorFamily.BLOCKCHAIN_PROTOCOL_OPS,
                GeneratorFamily.CROSS_CHAIN_INTEROP,
                GeneratorFamily.PROOF_AND_SEQUENCER_OPS,
                GeneratorFamily.FHIR_PROFILE_GENERATOR,
                GeneratorFamily.SMART_LAUNCH_OAUTH,
                GeneratorFamily.BULK_FHIR_POPULATION_OPS,
                GeneratorFamily.HL7V2_FEED_OPS,
                GeneratorFamily.CLINICAL_WORKFLOW_EVENTS,
                GeneratorFamily.DICOMWEB_IMAGING_OPS,
                GeneratorFamily.OPENEHR_SEMANTIC_RECORD_OPS,
                GeneratorFamily.DEVICE_TELEMETRY_CLINICAL,
                GeneratorFamily.EMR_VENDOR_ADAPTER,
                GeneratorFamily.OCPP_CHARGEPOINT_OPS,
                GeneratorFamily.OCPI_ROAMING_OPS,
                GeneratorFamily.MCP_A2A_OPS,
                GeneratorFamily.STREAMING_BUS_OPS,
                GeneratorFamily.SERVICE_MESH_RPC_OPS,
                GeneratorFamily.EDGE_CLIENT_RUNTIME,
                GeneratorFamily.EMBEDDED_AGENTIC_PIPELINE,
                GeneratorFamily.MULTILINGUAL_SECURITY_PACKS,
                GeneratorFamily.SECURITY_PERSONA_PACKS,
                GeneratorFamily.HYBRID_RUNTIME_OPS,
                GeneratorFamily.CAPACITY_COST_CONTROLLER,
                GeneratorFamily.BATCH_EXECUTION_TUNER,
                GeneratorFamily.COMPILER_MAINTAINER,
                GeneratorFamily.INTEROP_ADAPTER_ENGINEER,
                GeneratorFamily.PREFLIGHT_CAPACITY_PLANNER,
                GeneratorFamily.SIMULATOR_PERFORMANCE_ENGINEER);
    }
}
