package com.stakeholder.generators;

import com.stakeholder.activities.GeneratorFamily;
import com.stakeholder.config.SessionConfig;
import java.util.Map;
import java.util.Set;

final class ConfiguredFamilyRenderer implements GeneratorRenderer {
    @FunctionalInterface
    interface MetadataAugmentor {
        void apply(Map<String, Object> metadata, GeneratorFamily family, GeneratorRenderContext context);
    }

    private final GeneratorFamily family;
    private final String rendererGroup;
    private final String label;
    private final String focusKey;
    private final String focusValue;
    private final String sourcePath;
    private final MetadataAugmentor augmentor;

    ConfiguredFamilyRenderer(
            GeneratorFamily family,
            String rendererGroup,
            String label,
            String focusKey,
            String focusValue,
            String sourcePath,
            MetadataAugmentor augmentor) {
        this.family = family;
        this.rendererGroup = rendererGroup;
        this.label = label;
        this.focusKey = focusKey;
        this.focusValue = focusValue;
        this.sourcePath = sourcePath;
        this.augmentor = augmentor == null ? (metadata, ignoredFamily, ignoredContext) -> {} : augmentor;
    }

    @Override
    public Set<GeneratorFamily> families() {
        return Set.of(family);
    }

    @Override
    public GeneratorRenderResult render(GeneratorFamily ignored, GeneratorRenderContext context) {
        SessionConfig config = context.config();
        Map<String, Object> metadata =
                FamilyRendererSupport.metadata(rendererGroup, config, family.cliValue(), focusKey, focusValue);
        metadata.put("traceabilityRow", FamilyRendererSupport.traceabilityRow(family.cliValue(), sourcePath));
        augmentor.apply(metadata, family, context);
        return new GeneratorRenderResult(
                FamilyRendererSupport.message(label, config, family.message(config.jargonLevel())), metadata);
    }
}
