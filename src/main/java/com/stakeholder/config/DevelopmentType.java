package com.stakeholder.config;

import java.util.Arrays;

public enum DevelopmentType implements CliValue {
    BACKEND("backend"),
    FRONTEND("frontend"),
    FULLSTACK("fullstack"),
    DATA_SCIENCE("data-science"),
    DEV_OPS("dev-ops"),
    BLOCKCHAIN("blockchain"),
    MACHINE_LEARNING("machine-learning"),
    SYSTEMS_PROGRAMMING("systems-programming"),
    GAME_DEVELOPMENT("game-development"),
    SECURITY("security");

    private final String cliValue;

    DevelopmentType(String cliValue) {
        this.cliValue = cliValue;
    }

    @Override
    public String cliValue() {
        return cliValue;
    }

    public static DevelopmentType fromCliValue(String value) {
        return Arrays.stream(values())
                .filter(candidate -> candidate.cliValue.equalsIgnoreCase(value))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported --dev-type value: " + value));
    }
}
