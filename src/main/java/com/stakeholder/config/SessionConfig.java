package com.stakeholder.config;

/**
 * Shared runtime configuration derived from the CLI contract.
 */
public record SessionConfig(
        DevelopmentType devType,
        JargonLevel jargonLevel,
        Complexity complexity,
        long durationSeconds,
        boolean alertsEnabled,
        String projectName,
        boolean minimalOutput,
        boolean teamActivity,
        String framework,
        Long seed,
        OutputFormat outputFormat,
        boolean noColor,
        boolean trace) {}
