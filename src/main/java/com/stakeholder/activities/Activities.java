package com.stakeholder.activities;

import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import com.stakeholder.display.Display;
import com.stakeholder.generators.GeneratorRegistry;
import com.stakeholder.generators.GeneratorRenderContext;
import com.stakeholder.generators.GeneratorRenderResult;
import com.stakeholder.output.JsonOutput;
import com.stakeholder.output.NormalizedEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.fusesource.jansi.AnsiConsole;

/**
 * Scheduler-driven runtime for the Java rewrite.
 */
public final class Activities {
    private static final GeneratorRegistry REGISTRY = GeneratorRegistry.defaultRegistry();
    private static final List<GeneratorFamily> CLASSIC_FAMILIES = List.of(
            GeneratorFamily.CODE_ANALYZER,
            GeneratorFamily.DATA_PROCESSING,
            GeneratorFamily.JARGON,
            GeneratorFamily.METRICS,
            GeneratorFamily.NETWORK_ACTIVITY,
            GeneratorFamily.SYSTEM_MONITORING);

    private static final List<GeneratorFamily> POLICY_FAMILIES = List.of(
            GeneratorFamily.SUPPLY_CHAIN_SECURITY,
            GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
            GeneratorFamily.EVALUATION_AND_GUARDRAILS,
            GeneratorFamily.IDENTITY_AND_TRUST,
            GeneratorFamily.AIBOM_PROVENANCE,
            GeneratorFamily.AGENT_BOUNDARY_SECURITY,
            GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
            GeneratorFamily.FINOPS_CAPACITY);

    private static final List<GeneratorFamily> ALERT_FAMILIES = List.of(
            GeneratorFamily.SUPPLY_CHAIN_SECURITY,
            GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
            GeneratorFamily.AGENT_BOUNDARY_SECURITY,
            GeneratorFamily.DEVICE_TELEMETRY_CLINICAL,
            GeneratorFamily.OCPP_CHARGEPOINT_OPS,
            GeneratorFamily.STREAMING_BUS_OPS,
            GeneratorFamily.SERVICE_MESH_RPC_OPS,
            GeneratorFamily.MCP_A2A_OPS);

    private static final List<GeneratorFamily> TEAM_FAMILIES = List.of(
            GeneratorFamily.AGENT_WORKFLOWS,
            GeneratorFamily.PLATFORM_ENGINEERING,
            GeneratorFamily.DELIVERY_PREVIEW_OPS,
            GeneratorFamily.SERVICE_MESH_RPC_OPS);

    private Activities() {
        // no instances
    }

    public static int simulate(SessionConfig config) {
        Random random = newRandom(config.seed());

        if (config.outputFormat() == OutputFormat.JSON) {
            JsonOutput.writeEvents(AnsiConsole.out(), buildEvents(config));
            return 0;
        }

        AtomicBoolean running = new AtomicBoolean(true);
        Runtime.getRuntime().addShutdownHook(new Thread(() -> running.set(false)));

        int plannedActivities = plannedActivities(config.complexity());
        Display.showIntro(config, plannedActivities);
        if (config.trace()) {
            Display.showTrace(
                    config,
                    "seed=" + (config.seed() == null ? "random" : config.seed()) + " output-format="
                            + config.outputFormat().cliValue());
        }

        long startedAt = System.nanoTime();
        long deadlineNanos =
                config.durationSeconds() > 0 ? startedAt + config.durationSeconds() * 1_000_000_000L : Long.MAX_VALUE;

        while (running.get()) {
            List<ActivitySelection> plan = buildActivityPlan(config, random);
            for (ActivitySelection selection : plan) {
                GeneratorRenderResult rendered = renderSelection(selection, config, random);
                Display.show(config, formatTextActivity(selection, rendered));
                if (config.trace()) {
                    Display.showTrace(config, traceLine(selection));
                }
            }

            if (config.durationSeconds() > 0 && System.nanoTime() >= deadlineNanos) {
                break;
            }

            if (config.durationSeconds() == 0) {
                sleepSilently(160L + random.nextInt(140));
            } else {
                break;
            }
        }

        Display.show(
                config,
                "session terminated (" + (config.durationSeconds() == 0 ? "interrupted" : "duration-elapsed") + ")");
        return 0;
    }

    static List<NormalizedEvent> buildEvents(SessionConfig config) {
        Random random = newRandom(config.seed());
        List<ActivitySelection> plan = buildActivityPlan(config, random);
        return buildEvents(config, plan);
    }

    static List<NormalizedEvent> buildEvents(SessionConfig config, List<ActivitySelection> plan) {
        Random random = newRandom(config.seed());
        List<NormalizedEvent> events = new ArrayList<>();
        int sequence = 0;

        events.add(event(
                config,
                sequence++,
                "session.start",
                "Session configuration accepted",
                context(
                        "project", config.projectName(),
                        "devType", config.devType().cliValue(),
                        "jargon", config.jargonLevel().cliValue(),
                        "complexity", config.complexity().cliValue(),
                        "framework", config.framework(),
                        "durationSeconds", config.durationSeconds())));

        events.add(event(
                config,
                sequence++,
                "boot.sequence",
                "Scheduler baseline initialized",
                context(
                        "plannedActivities", plannedActivities(config.complexity()),
                        "alertsEnabled", config.alertsEnabled(),
                        "teamActivity", config.teamActivity(),
                        "seeded", config.seed() != null,
                        "outputFormat", config.outputFormat().cliValue())));

        for (ActivitySelection selection : plan) {
            GeneratorRenderResult rendered = renderSelection(selection, config, random);
            Map<String, Object> context = new LinkedHashMap<>();
            context.put("family", selection.family().cliValue());
            context.put("kind", selection.kind());
            context.put("protocol", selection.family().protocol());
            context.put("flavors", selection.flavors());
            context.put("project", config.projectName());
            if (!config.framework().isBlank()) {
                context.put("framework", config.framework());
            }
            context.putAll(rendered.metadata());
            events.add(event(config, sequence++, "activity", rendered.message(), context));
            if (config.trace()) {
                events.add(event(
                        config,
                        sequence++,
                        "trace",
                        traceLine(selection),
                        context(
                                "family", selection.family().cliValue(),
                                "protocol", selection.family().protocol(),
                                "flavorCount", selection.flavors().size())));
            }
        }

        events.add(event(
                config,
                sequence,
                "session.end",
                "Session completed",
                context("exitCode", 0, "result", "ok", "plannedActivities", plan.size())));

        return events;
    }

    static List<String> buildTextActivities(SessionConfig config, List<ActivitySelection> plan) {
        Random random = newRandom(config.seed());
        List<String> lines = new ArrayList<>();
        for (ActivitySelection selection : plan) {
            GeneratorRenderResult rendered = renderSelection(selection, config, random);
            lines.add(formatTextActivity(selection, rendered));
            if (config.trace()) {
                lines.add(traceLine(selection));
            }
        }
        return lines;
    }

    public static List<String> availableGeneratorFamilies() {
        return GeneratorFamily.cliValues();
    }

    static List<ActivitySelection> buildActivityPlan(SessionConfig config, Random random) {
        int targetCount = plannedActivities(config.complexity());
        List<GeneratorFamily> eligible = eligibleFamilies(config);
        List<GeneratorFamily> selected = new ArrayList<>();

        pushUnique(selected, eligible, CLASSIC_FAMILIES, random);

        if (targetCount >= 2) {
            List<GeneratorFamily> modern = eligible.stream()
                    .filter(family -> !CLASSIC_FAMILIES.contains(family) && family != GeneratorFamily.JARGON)
                    .toList();
            pushUnique(selected, modern, modern, random);
        }

        if (targetCount >= 3) {
            pushUnique(selected, eligible, POLICY_FAMILIES, random);
        }

        while (selected.size() < targetCount) {
            GeneratorFamily choice = eligible.get(random.nextInt(eligible.size()));
            if (!selected.contains(choice)) {
                selected.add(choice);
            }
        }

        if (config.alertsEnabled()) {
            pushUnique(selected, eligible, ALERT_FAMILIES, random);
        }
        if (config.teamActivity()) {
            pushUnique(selected, eligible, TEAM_FAMILIES, random);
        }

        List<ActivitySelection> plan = new ArrayList<>();
        for (GeneratorFamily family : selected) {
            String kind = config.alertsEnabled() && ALERT_FAMILIES.contains(family)
                    ? "alert-injection"
                    : config.teamActivity() && TEAM_FAMILIES.contains(family) ? "team-injection" : "generator";
            plan.add(new ActivitySelection(family, resolveFlavors(config, family, random), kind));
        }
        return plan;
    }

    static List<GeneratorFamily> eligibleFamilies(SessionConfig config) {
        Set<GeneratorFamily> set = new LinkedHashSet<>(CLASSIC_FAMILIES);

        switch (config.devType()) {
            case BACKEND ->
                extend(
                        set,
                        GeneratorFamily.AGENT_WORKFLOWS,
                        GeneratorFamily.AI_INFERENCE_OPS,
                        GeneratorFamily.PLATFORM_ENGINEERING,
                        GeneratorFamily.SUPPLY_CHAIN_SECURITY,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.DELIVERY_PREVIEW_OPS,
                        GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                        GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                        GeneratorFamily.IDENTITY_AND_TRUST,
                        GeneratorFamily.AIBOM_PROVENANCE,
                        GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
                        GeneratorFamily.FINOPS_CAPACITY,
                        GeneratorFamily.MCP_A2A_OPS,
                        GeneratorFamily.STREAMING_BUS_OPS,
                        GeneratorFamily.SERVICE_MESH_RPC_OPS);
            case FRONTEND ->
                extend(
                        set,
                        GeneratorFamily.AGENT_WORKFLOWS,
                        GeneratorFamily.DELIVERY_PREVIEW_OPS,
                        GeneratorFamily.EDGE_CLIENT_RUNTIME,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                        GeneratorFamily.SERVICE_MESH_RPC_OPS);
            case FULLSTACK ->
                extend(
                        set,
                        GeneratorFamily.AGENT_WORKFLOWS,
                        GeneratorFamily.AI_INFERENCE_OPS,
                        GeneratorFamily.PLATFORM_ENGINEERING,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.DELIVERY_PREVIEW_OPS,
                        GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                        GeneratorFamily.MCP_A2A_OPS,
                        GeneratorFamily.STREAMING_BUS_OPS,
                        GeneratorFamily.SERVICE_MESH_RPC_OPS,
                        GeneratorFamily.SUPPLY_CHAIN_SECURITY);
            case DATA_SCIENCE ->
                extend(
                        set,
                        GeneratorFamily.AI_INFERENCE_OPS,
                        GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                        GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                        GeneratorFamily.AIBOM_PROVENANCE,
                        GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME);
            case DEV_OPS ->
                extend(
                        set,
                        GeneratorFamily.AGENT_WORKFLOWS,
                        GeneratorFamily.PLATFORM_ENGINEERING,
                        GeneratorFamily.SUPPLY_CHAIN_SECURITY,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.DELIVERY_PREVIEW_OPS,
                        GeneratorFamily.IDENTITY_AND_TRUST,
                        GeneratorFamily.FINOPS_CAPACITY,
                        GeneratorFamily.MCP_A2A_OPS,
                        GeneratorFamily.STREAMING_BUS_OPS,
                        GeneratorFamily.SERVICE_MESH_RPC_OPS);
            case BLOCKCHAIN ->
                extend(
                        set,
                        GeneratorFamily.BLOCKCHAIN_PROTOCOL_OPS,
                        GeneratorFamily.CROSS_CHAIN_INTEROP,
                        GeneratorFamily.PROOF_AND_SEQUENCER_OPS,
                        GeneratorFamily.SUPPLY_CHAIN_SECURITY,
                        GeneratorFamily.IDENTITY_AND_TRUST,
                        GeneratorFamily.MCP_A2A_OPS);
            case MACHINE_LEARNING ->
                extend(
                        set,
                        GeneratorFamily.AI_INFERENCE_OPS,
                        GeneratorFamily.KNOWLEDGE_RETRIEVAL,
                        GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.AIBOM_PROVENANCE,
                        GeneratorFamily.FINOPS_CAPACITY);
            case SYSTEMS_PROGRAMMING ->
                extend(
                        set,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.EMBEDDED_AGENTIC_PIPELINE,
                        GeneratorFamily.IDENTITY_AND_TRUST,
                        GeneratorFamily.SUPPLY_CHAIN_SECURITY,
                        GeneratorFamily.STREAMING_BUS_OPS);
            case GAME_DEVELOPMENT ->
                extend(
                        set,
                        GeneratorFamily.EDGE_CLIENT_RUNTIME,
                        GeneratorFamily.DELIVERY_PREVIEW_OPS,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.STREAMING_BUS_OPS,
                        GeneratorFamily.SERVICE_MESH_RPC_OPS);
            case SECURITY ->
                extend(
                        set,
                        GeneratorFamily.AGENT_WORKFLOWS,
                        GeneratorFamily.SUPPLY_CHAIN_SECURITY,
                        GeneratorFamily.OBSERVABILITY_AI_RUNTIME,
                        GeneratorFamily.EVALUATION_AND_GUARDRAILS,
                        GeneratorFamily.IDENTITY_AND_TRUST,
                        GeneratorFamily.AIBOM_PROVENANCE,
                        GeneratorFamily.AGENT_BOUNDARY_SECURITY,
                        GeneratorFamily.DATA_GOVERNANCE_COMPLIANCE,
                        GeneratorFamily.MCP_A2A_OPS,
                        GeneratorFamily.MULTILINGUAL_SECURITY_PACKS,
                        GeneratorFamily.SECURITY_PERSONA_PACKS,
                        GeneratorFamily.STREAMING_BUS_OPS,
                        GeneratorFamily.SERVICE_MESH_RPC_OPS);
        }

        String context = (config.projectName() + " " + config.framework()).toLowerCase();
        if (containsKeyword(
                context, "ehr", "emr", "fhir", "hl7", "openehr", "dicom", "clinical", "patient", "hospital")) {
            extend(
                    set,
                    GeneratorFamily.FHIR_PROFILE_GENERATOR,
                    GeneratorFamily.SMART_LAUNCH_OAUTH,
                    GeneratorFamily.BULK_FHIR_POPULATION_OPS,
                    GeneratorFamily.HL7V2_FEED_OPS,
                    GeneratorFamily.CLINICAL_WORKFLOW_EVENTS,
                    GeneratorFamily.DICOMWEB_IMAGING_OPS,
                    GeneratorFamily.OPENEHR_SEMANTIC_RECORD_OPS,
                    GeneratorFamily.DEVICE_TELEMETRY_CLINICAL,
                    GeneratorFamily.EMR_VENDOR_ADAPTER);
        }
        if (containsKeyword(context, "charge", "charger", "charging", "ev", "ocpp", "ocpi", "roaming")) {
            extend(
                    set,
                    GeneratorFamily.OCPP_CHARGEPOINT_OPS,
                    GeneratorFamily.OCPI_ROAMING_OPS,
                    GeneratorFamily.STREAMING_BUS_OPS,
                    GeneratorFamily.SERVICE_MESH_RPC_OPS);
        }
        if (containsKeyword(context, "quantum", "qir", "qasm", "braket", "qiskit", "cudaq", "ionq")) {
            extend(
                    set,
                    GeneratorFamily.HYBRID_RUNTIME_OPS,
                    GeneratorFamily.CAPACITY_COST_CONTROLLER,
                    GeneratorFamily.BATCH_EXECUTION_TUNER,
                    GeneratorFamily.COMPILER_MAINTAINER,
                    GeneratorFamily.INTEROP_ADAPTER_ENGINEER,
                    GeneratorFamily.PREFLIGHT_CAPACITY_PLANNER,
                    GeneratorFamily.SIMULATOR_PERFORMANCE_ENGINEER);
        }
        if (containsKeyword(context, "mcp", "a2a", "mqtt", "nats", "kafka", "grpc", "graphql", "webtransport")) {
            extend(
                    set,
                    GeneratorFamily.MCP_A2A_OPS,
                    GeneratorFamily.STREAMING_BUS_OPS,
                    GeneratorFamily.SERVICE_MESH_RPC_OPS);
        }

        return List.copyOf(set);
    }

    static List<String> resolveFlavors(SessionConfig config, GeneratorFamily family, Random random) {
        List<String> flavors = new ArrayList<>();
        if (config.devType() == DevelopmentType.SECURITY || isSecurityFamily(family)) {
            if (config.jargonLevel().compareTo(JargonLevel.HIGH) >= 0 || config.alertsEnabled()) {
                String[] languages = {"english", "chinese", "russian", "spanish", "arabic"};
                flavors.add("multilingual-security:" + languages[random.nextInt(languages.length)]);
            }
            if (config.jargonLevel().compareTo(JargonLevel.HIGH) >= 0) {
                String[] personas = {
                    "bug-bounty-operator",
                    "incident-commander",
                    "reverse-engineer",
                    "threat-hunter",
                    "soc-analyst",
                    "dark-market-watcher",
                    "cti-brief-writer"
                };
                flavors.add("security-persona:" + personas[random.nextInt(personas.length)]);
            }
        }

        String context = (config.projectName() + " " + config.framework()).toLowerCase();
        if (containsKeyword(context, "experimental", "openai", "anthropic", "claude", "responses", "llm")
                && (family == GeneratorFamily.AI_INFERENCE_OPS
                        || family == GeneratorFamily.EVALUATION_AND_GUARDRAILS
                        || family == GeneratorFamily.AIBOM_PROVENANCE)) {
            flavors.add("experimental-live-provider");
        }
        return List.copyOf(flavors);
    }

    private static void pushUnique(
            List<GeneratorFamily> selected, List<GeneratorFamily> eligible, List<GeneratorFamily> pool, Random random) {
        List<GeneratorFamily> candidates = pool.stream()
                .filter(eligible::contains)
                .filter(family -> !selected.contains(family))
                .toList();
        if (!candidates.isEmpty()) {
            GeneratorFamily choice = candidates.get(random.nextInt(candidates.size()));
            if (!selected.contains(choice)) {
                selected.add(choice);
            }
        }
    }

    private static void extend(Set<GeneratorFamily> target, GeneratorFamily... families) {
        target.addAll(List.of(families));
    }

    private static boolean containsKeyword(String haystack, String... needles) {
        for (String needle : needles) {
            if (haystack.contains(needle)) {
                return true;
            }
        }
        return false;
    }

    private static boolean isSecurityFamily(GeneratorFamily family) {
        return switch (family) {
            case SUPPLY_CHAIN_SECURITY,
                    AGENT_BOUNDARY_SECURITY,
                    IDENTITY_AND_TRUST,
                    AIBOM_PROVENANCE,
                    DATA_GOVERNANCE_COMPLIANCE,
                    MULTILINGUAL_SECURITY_PACKS,
                    SECURITY_PERSONA_PACKS,
                    MCP_A2A_OPS,
                    BLOCKCHAIN_PROTOCOL_OPS,
                    CROSS_CHAIN_INTEROP,
                    PROOF_AND_SEQUENCER_OPS -> true;
            default -> false;
        };
    }

    private static GeneratorRenderResult renderSelection(
            ActivitySelection selection, SessionConfig config, Random random) {
        return REGISTRY.render(selection.family(), new GeneratorRenderContext(config, random, selection.flavors()));
    }

    private static String formatTextActivity(ActivitySelection selection, GeneratorRenderResult rendered) {
        StringBuilder builder = new StringBuilder();
        builder.append('[').append(selection.family().title()).append("] ").append(rendered.message());
        if (!selection.flavors().isEmpty()) {
            builder.append(" {").append(String.join(", ", selection.flavors())).append('}');
        }
        if (selection.family().protocol() != null) {
            builder.append(" [protocol=").append(selection.family().protocol()).append(']');
        }
        return builder.toString();
    }

    private static String traceLine(ActivitySelection selection) {
        return "scheduled " + selection.family().cliValue()
                + " kind=" + selection.kind()
                + " flavorCount=" + selection.flavors().size()
                + (selection.family().protocol() == null
                        ? ""
                        : " protocol=" + selection.family().protocol());
    }

    private static NormalizedEvent event(
            SessionConfig config, int sequence, String eventType, String message, Map<String, Object> context) {
        Instant base = config.seed() == null ? Instant.now() : Instant.EPOCH;
        return new NormalizedEvent(
                eventType, sequence, message, base.plusSeconds(sequence).toString(), ordered(context));
    }

    private static Map<String, Object> ordered(Map<String, Object> context) {
        return new LinkedHashMap<>(context);
    }

    private static Map<String, Object> context(Object... entries) {
        LinkedHashMap<String, Object> context = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            context.put((String) entries[index], entries[index + 1]);
        }
        return context;
    }

    private static int plannedActivities(Complexity complexity) {
        return switch (complexity) {
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case EXTREME -> 4;
        };
    }

    private static Random newRandom(Long seed) {
        return seed == null ? new Random() : new Random(seed);
    }

    private static void sleepSilently(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException interrupted) {
            Thread.currentThread().interrupt();
        }
    }
}
