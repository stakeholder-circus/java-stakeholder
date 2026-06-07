package com.stakeholder.experimental;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

record RequestTemplate(
        String method, String url, Map<String, String> headers, String bodyTemplate, String responsePath) {}

record ProviderProfile(
        String id,
        String provider,
        String label,
        String baseUrl,
        String apiKeyEnv,
        String apiKey,
        String model,
        List<String> adapterModes,
        String captureUrl,
        String captureSelector,
        RequestTemplate requestTemplate,
        String updatedAt) {}

record PromptAsset(String id, String version, String label, String template, String updatedAt) {}

record PromptAssetVersion(
        String assetId,
        String version,
        String promptHash,
        String toolSchemaHash,
        String outputSchemaHash,
        String evalSuiteVersion,
        String updatedAt) {}

record PersonalizationProfile(String id, String label, String description, String updatedAt) {}

record ConsumerSessionRecord(
        String id, String profileId, String provider, Map<String, Object> material, String source, String updatedAt) {}

record CacheRecord(
        String cacheKey,
        String provider,
        String model,
        String promptVersion,
        String promptAsset,
        String personalizationProfile,
        Map<String, Object> config,
        List<String> selectedFamilies,
        Map<String, Object> response,
        Map<String, Object> provenance,
        String createdAt) {}

record ExperimentalGenerationMode(
        String provider,
        String profileId,
        String adapterMode,
        String promptAsset,
        String promptVersion,
        String personalizationProfile,
        boolean bootstrapSession) {
    Map<String, Object> toMap() {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("provider", provider);
        map.put("profileId", profileId);
        map.put("adapterMode", adapterMode);
        map.put("promptAsset", promptAsset);
        map.put("promptVersion", promptVersion);
        map.put("personalizationProfile", personalizationProfile);
        map.put("bootstrapSession", bootstrapSession);
        return map;
    }
}
