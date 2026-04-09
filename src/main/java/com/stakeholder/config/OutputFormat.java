package com.stakeholder.config;

import java.util.Arrays;

public enum OutputFormat implements CliValue {
    TEXT("text"),
    JSON("json");

    private final String cliValue;

    OutputFormat(String cliValue) {
        this.cliValue = cliValue;
    }

    @Override
    public String cliValue() {
        return cliValue;
    }

    public static OutputFormat fromCliValue(String value) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.cliValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported --output-format value: " + value));
    }
}
