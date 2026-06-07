package com.stakeholder.experimental;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class ExperimentalDefaults {
    static final String SECRET_ENV = "STAKEHOLDER_ENCRYPTION_KEY";
    static final String BOOTSTRAP_ENV = "STAKEHOLDER_BROWSER_BOOTSTRAP_CMD";
    private static final String DEFAULT_TIMESTAMP = "2026-04-09T00:00:00Z";
    private static final String TOOL_SCHEMA_SOURCE = "stakeholder-core/spec/experimental/provider-adapter.schema.json";
    private static final String OUTPUT_SCHEMA_SOURCE = "stakeholder-java.experimental-output.v1";
    private static final String EVAL_SUITE_VERSION = "experimental-local-smoke-v1";

    private ExperimentalDefaults() {}

    public static List<String> availableProviderIds() {
        return defaultProviderProfiles().stream().map(ProviderProfile::id).toList();
    }

    public static List<String> availablePromptAssetIds() {
        return defaultPromptAssets().stream().map(PromptAsset::id).toList();
    }

    public static List<String> availablePersonalizationProfileIds() {
        return defaultPersonalizationProfiles().stream()
                .map(PersonalizationProfile::id)
                .toList();
    }

    public static List<String> availableFlagNames() {
        return List.of(
                "experimental-provider",
                "experimental-profile",
                "experimental-prompt",
                "experimental-model",
                "experimental-personalization",
                "experimental-adapter-mode",
                "experimental-session-file",
                "experimental-bootstrap-session");
    }

    static List<ProviderProfile> defaultProviderProfiles() {
        RequestTemplate consumerTemplate =
                new RequestTemplate("POST", "", Map.of(), "{\"prompt\":\"${prompt}\"}", "text");
        return List.of(
                new ProviderProfile(
                        "local-demo",
                        "local-demo",
                        "Local demo provider",
                        null,
                        null,
                        null,
                        "deterministic-demo",
                        List.of("api", "consumer"),
                        null,
                        null,
                        null,
                        DEFAULT_TIMESTAMP),
                new ProviderProfile(
                        "openai-compatible",
                        "openai-compatible",
                        "OpenAI-compatible API",
                        "https://api.openai.com/v1/responses",
                        "OPENAI_API_KEY",
                        null,
                        "gpt-5.4-mini",
                        List.of("api"),
                        null,
                        null,
                        null,
                        DEFAULT_TIMESTAMP),
                new ProviderProfile(
                        "anthropic",
                        "anthropic",
                        "Anthropic API",
                        "https://api.anthropic.com/v1/messages",
                        "ANTHROPIC_API_KEY",
                        null,
                        "claude-sonnet-4-5",
                        List.of("api"),
                        null,
                        null,
                        null,
                        DEFAULT_TIMESTAMP),
                new ProviderProfile(
                        "consumer-session",
                        "consumer-session",
                        "Consumer session adapter",
                        null,
                        null,
                        null,
                        "session-replay",
                        List.of("consumer"),
                        "https://chatgpt.com/",
                        "textarea, [contenteditable=\"true\"]",
                        consumerTemplate,
                        DEFAULT_TIMESTAMP),
                new ProviderProfile(
                        "openai-consumer",
                        "consumer-session",
                        "OpenAI consumer session",
                        null,
                        null,
                        null,
                        "chatgpt-consumer",
                        List.of("consumer"),
                        "https://chatgpt.com/",
                        "textarea, [contenteditable=\"true\"]",
                        consumerTemplate,
                        DEFAULT_TIMESTAMP),
                new ProviderProfile(
                        "claude-consumer",
                        "consumer-session",
                        "Claude consumer session",
                        null,
                        null,
                        null,
                        "claude-consumer",
                        List.of("consumer"),
                        "https://claude.ai/",
                        "textarea, [contenteditable=\"true\"]",
                        consumerTemplate,
                        DEFAULT_TIMESTAMP));
    }

    static List<PromptAsset> defaultPromptAssets() {
        return List.of(
                new PromptAsset(
                        "operator-brief",
                        "1.0.0",
                        "Operator brief",
                        "Generate a concise stakeholder session for ${devType} using ${complexity} complexity and ${jargon} jargon. "
                                + "Project: ${project}. Framework: ${framework}. Families: ${families}.",
                        DEFAULT_TIMESTAMP),
                new PromptAsset(
                        "provider-narrative",
                        "1.0.0",
                        "Provider narrative",
                        "Produce a terminal-ready engineering narrative for ${devType}. Emphasize ${families}. "
                                + "Keep provenance-friendly structure and mention the project ${project}.",
                        DEFAULT_TIMESTAMP));
    }

    static List<PromptAssetVersion> defaultPromptAssetVersions() {
        return defaultPromptAssets().stream()
                .map(asset -> new PromptAssetVersion(
                        asset.id(),
                        asset.version(),
                        sha256(asset.template()),
                        sha256(TOOL_SCHEMA_SOURCE),
                        sha256(OUTPUT_SCHEMA_SOURCE),
                        EVAL_SUITE_VERSION,
                        DEFAULT_TIMESTAMP))
                .toList();
    }

    static List<PersonalizationProfile> defaultPersonalizationProfiles() {
        return List.of(new PersonalizationProfile(
                "local-operator",
                "Local operator",
                "Default local operator profile for deterministic and experimental sessions.",
                DEFAULT_TIMESTAMP));
    }

    static Map<String, Object> provenanceSnapshot(
            ProviderProfile profile,
            String model,
            String adapterMode,
            PromptAsset promptAsset,
            PromptAssetVersion promptVersion,
            PersonalizationProfile personalizationProfile,
            Map<String, Object> cache) {
        LinkedHashMap<String, Object> provenance = new LinkedHashMap<>();
        provenance.put("provider", profile.provider());
        provenance.put("profileId", profile.id());
        provenance.put("model", model);
        provenance.put("adapterType", adapterMode);
        provenance.put("promptVersion", promptVersion.version());
        provenance.put("promptHash", promptVersion.promptHash());
        provenance.put("toolSchemaHash", promptVersion.toolSchemaHash());
        provenance.put("outputSchemaHash", promptVersion.outputSchemaHash());
        provenance.put("evalSuiteVersion", promptVersion.evalSuiteVersion());
        provenance.put("cache", cache);
        provenance.put("personalizationProfile", personalizationProfile == null ? null : personalizationProfile.id());
        provenance.put("timestamp", Instant.now().toString());
        provenance.put("promptAsset", promptAsset.id());
        provenance.put(
                "provenanceId",
                sha256(profile.id() + ":" + model + ":" + promptVersion.version() + ":" + promptAsset.id())
                        .substring(0, 16));
        return provenance;
    }

    static String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 should be available", exception);
        }
    }
}
