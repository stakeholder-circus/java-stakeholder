package com.stakeholder.config;

import java.util.Arrays;

public enum Complexity implements CliValue {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    EXTREME("extreme");

    private final String cliValue;

    Complexity(String cliValue) {
        this.cliValue = cliValue;
    }

    @Override
    public String cliValue() {
        return cliValue;
    }

    public static Complexity fromCliValue(String value) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.cliValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported --complexity value: " + value));
    }
}
