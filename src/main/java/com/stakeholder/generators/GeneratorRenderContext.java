package com.stakeholder.generators;

import com.stakeholder.config.SessionConfig;
import java.util.List;
import java.util.Random;

public record GeneratorRenderContext(SessionConfig config, Random random, List<String> flavors) {
    public GeneratorRenderContext {
        flavors = List.copyOf(flavors);
    }
}
