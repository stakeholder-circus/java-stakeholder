package com.stakeholder.experimental;

import java.nio.file.Path;

public record ExperimentalRequest(
        String provider,
        String profileId,
        String promptAssetId,
        String model,
        String personalizationProfileId,
        String adapterMode,
        Path sessionFile,
        boolean bootstrapSession) {}
