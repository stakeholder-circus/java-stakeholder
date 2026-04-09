package com.stakeholder.config;

import java.util.Arrays;

public enum JargonLevel implements CliValue {
    LOW("low"),
    MEDIUM("medium"),
    HIGH("high"),
    EXTREME("extreme");

    private final String cliValue;

    JargonLevel(String cliValue) {
        this.cliValue = cliValue;
    }

    @Override
    public String cliValue() {
        return cliValue;
    }

    public static JargonLevel fromCliValue(String value) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.cliValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported --jargon value: " + value));
    }
}
