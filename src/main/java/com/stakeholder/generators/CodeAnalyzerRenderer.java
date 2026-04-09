package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Set;

final class CodeAnalyzerRenderer implements GeneratorRenderer {
    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(GeneratorFamily.CODE_ANALYZER);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily family, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        var metadata = FamilyRendererSupport.metadata(
                "classic-six",
                config,
                family.cliValue(),
                "analysisFocus",
                "typed interfaces, agent-authored patches, and MCP assumptions");
        metadata.put(
                "traceabilityRow",
                FamilyRendererSupport.traceabilityRow(family.cliValue(), "src/generators/code_analyzer.rs"));
        return new GeneratorRenderResult(
                FamilyRendererSupport.message("code analyzer", config, family.message(config.jargonLevel())), metadata);
    }
}
