package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

final class HealthProtocolRenderer implements GeneratorRenderer {
    private static final Set<GeneratorFamily> FAMILIES = Set.of(
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
            GeneratorFamily.EMBEDDED_AGENTIC_PIPELINE);

    @Override
    public Set<GeneratorFamily> families() {
        return FAMILIES;
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        Map<String, Object> metadata = new LinkedHashMap<>();
        metadata.put("rendererGroup", "health-protocol");
        metadata.put("project", config.projectName());
        metadata.put("protocolSurface", family.protocol() == null ? "mixed" : family.protocol());
        String message =
                switch (family) {
                    case FHIR_PROFILE_GENERATOR ->
                        "FHIR profile lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case SMART_LAUNCH_OAUTH ->
                        "SMART launch lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case BULK_FHIR_POPULATION_OPS ->
                        "Bulk FHIR lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case HL7V2_FEED_OPS ->
                        "HL7 v2 lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case CLINICAL_WORKFLOW_EVENTS ->
                        "clinical workflow lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case DICOMWEB_IMAGING_OPS ->
                        "DICOMweb lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case OPENEHR_SEMANTIC_RECORD_OPS ->
                        "openEHR lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case DEVICE_TELEMETRY_CLINICAL ->
                        "clinical device telemetry lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case EMR_VENDOR_ADAPTER ->
                        "EMR vendor adapter lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case OCPP_CHARGEPOINT_OPS ->
                        "OCPP chargepoint lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case OCPI_ROAMING_OPS ->
                        "OCPI roaming lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case MCP_A2A_OPS ->
                        "MCP/A2A lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case STREAMING_BUS_OPS ->
                        "streaming bus lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case SERVICE_MESH_RPC_OPS ->
                        "service mesh RPC lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    case EDGE_CLIENT_RUNTIME ->
                        "edge runtime lane for " + config.projectName() + ": " + family.message(config.jargonLevel());
                    case EMBEDDED_AGENTIC_PIPELINE ->
                        "embedded agentic lane for " + config.projectName() + ": "
                                + family.message(config.jargonLevel());
                    default -> throw unsupported(family);
                };
        return new GeneratorRenderResult(message, metadata);
    }

    private static IllegalArgumentException unsupported(GeneratorFamily family) {
        return new IllegalArgumentException("unsupported family for health-protocol renderer: " + family.cliValue());
    }
}
