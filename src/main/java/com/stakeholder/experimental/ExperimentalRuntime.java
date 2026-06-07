package com.stakeholder.experimental;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.stakeholder.activities.Activities;
import com.stakeholder.config.SessionConfig;
import com.stakeholder.output.NormalizedEvent;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public final class ExperimentalRuntime {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();
    private static final TypeReference<LinkedHashMap<String, Object>> MAP_TYPE = new TypeReference<>() {};

    private final ExperimentalStore store;
    private final HttpClient httpClient;
    private final String bootstrapCommand;

    public ExperimentalRuntime() throws IOException {
        this(
                null,
                HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(20)).build(),
                System.getenv(ExperimentalDefaults.BOOTSTRAP_ENV));
    }

    ExperimentalRuntime(Path stateRoot, HttpClient httpClient, String bootstrapCommand) throws IOException {
        this.store = new ExperimentalStore(stateRoot);
        this.httpClient = httpClient;
        this.bootstrapCommand = bootstrapCommand;
    }

    public Map<String, Object> run(SessionConfig config, ExperimentalRequest request)
            throws IOException, InterruptedException {
        ProviderProfile profile = resolveProfile(request);
        if (profile == null) {
            throw new IOException("No provider profile is configured for " + request.provider() + ".");
        }
        PromptAsset promptAsset = resolvePromptAsset(request);
        PromptAssetVersion promptVersion = store.getPromptAssetVersion(promptAsset.id(), promptAsset.version());
        PersonalizationProfile personalizationProfile =
                store.getPersonalizationProfile(request.personalizationProfileId());
        String adapterMode = resolveAdapterMode(request.adapterMode(), profile);
        List<String> selectedFamilies = selectedFamilies(config);
        String model = request.model() == null || request.model().isBlank() ? profile.model() : request.model();
        String promptText = fillTemplate(
                promptAsset.template(),
                Map.of(
                        "devType", config.devType().cliValue(),
                        "complexity", config.complexity().cliValue(),
                        "jargon", config.jargonLevel().cliValue(),
                        "project", config.projectName(),
                        "framework", config.framework(),
                        "families", String.join(", ", selectedFamilies)));
        String cacheKey = ExperimentalDefaults.sha256(MAPPER.writeValueAsString(Map.of(
                        "provider", profile.provider(),
                        "profile", profile.id(),
                        "model", model,
                        "promptVersion", promptVersion.version(),
                        "promptAsset", promptAsset.id(),
                        "personalizationProfile", personalizationProfile == null ? null : personalizationProfile.id(),
                        "selectedFamilies", selectedFamilies,
                        "config", configMap(config)))
                .replace(" ", ""));

        CacheRecord cached = store.getCache(cacheKey);
        if (cached != null) {
            return cachedOutput(
                    profile, promptAsset, promptVersion, personalizationProfile, adapterMode, selectedFamilies, cached);
        }

        ProviderResponse response =
                switch (profile.provider()) {
                    case "local-demo" -> localDemo(profile, model, promptText, selectedFamilies, config);
                    case "openai-compatible" -> openAiCompatible(profile, model, promptText);
                    case "anthropic" -> anthropic(profile, model, promptText);
                    case "consumer-session" -> consumerSession(profile, model, promptText, request);
                    default -> throw new IOException("Unsupported experimental provider " + profile.provider() + ".");
                };

        Map<String, Object> cache =
                Map.of("hit", false, "key", cacheKey, "createdAt", Instant.now().toString());
        Map<String, Object> provenance = ExperimentalDefaults.provenanceSnapshot(
                profile, model, adapterMode, promptAsset, promptVersion, personalizationProfile, cache);
        Map<String, Object> output = outputMap(
                profile,
                promptAsset,
                promptVersion,
                personalizationProfile,
                new ExperimentalGenerationMode(
                        profile.provider(),
                        profile.id(),
                        adapterMode,
                        promptAsset.id(),
                        promptVersion.version(),
                        personalizationProfile == null ? null : personalizationProfile.id(),
                        request.bootstrapSession()),
                selectedFamilies,
                response.text(),
                response.raw(),
                cache,
                provenance);
        store.putCache(new CacheRecord(
                cacheKey,
                profile.provider(),
                model,
                promptVersion.version(),
                promptAsset.id(),
                personalizationProfile == null ? null : personalizationProfile.id(),
                configMap(config),
                selectedFamilies,
                Map.of("text", response.text(), "raw", response.raw()),
                provenance,
                Instant.now().toString()));
        store.putProvenance(provenance);
        return output;
    }

    private ProviderProfile resolveProfile(ExperimentalRequest request) throws IOException {
        String idOrProvider = request.profileId();
        if (idOrProvider == null || idOrProvider.isBlank()) {
            idOrProvider = request.provider();
        }
        return store.getProviderProfile(idOrProvider);
    }

    private PromptAsset resolvePromptAsset(ExperimentalRequest request) throws IOException {
        PromptAsset asset = store.getPromptAsset(request.promptAssetId());
        if (asset == null) {
            throw new IOException("No prompt asset is available for experimental generation.");
        }
        return asset;
    }

    private List<String> selectedFamilies(SessionConfig config) {
        LinkedHashSet<String> families = new LinkedHashSet<>();
        for (NormalizedEvent event : Activities.buildEvents(config)) {
            if ("activity".equals(event.eventType()) && event.context().containsKey("family")) {
                families.add(String.valueOf(event.context().get("family")));
            }
        }
        return List.copyOf(families);
    }

    private String resolveAdapterMode(String requestedMode, ProviderProfile profile) {
        if (requestedMode != null && !requestedMode.isBlank()) {
            return requestedMode;
        }
        return profile.adapterModes().stream().findFirst().orElse("api");
    }

    private ProviderResponse localDemo(
            ProviderProfile profile,
            String model,
            String promptText,
            List<String> selectedFamilies,
            SessionConfig config) {
        String text = "Local demo provider generated a "
                + config.devType().cliValue()
                + " session for "
                + String.join(", ", selectedFamilies)
                + ". Prompt length "
                + promptText.length()
                + " characters and project "
                + config.projectName()
                + " stayed inside the experimental boundary.";
        return new ProviderResponse(
                text,
                Map.of(
                        "provider",
                        profile.provider(),
                        "model",
                        model,
                        "families",
                        selectedFamilies,
                        "promptLength",
                        promptText.length()));
    }

    private ProviderResponse openAiCompatible(ProviderProfile profile, String model, String promptText)
            throws IOException, InterruptedException {
        String apiKey = resolveApiKey(profile);
        LinkedHashMap<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("input", promptText);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Objects.requireNonNull(profile.baseUrl(), "Provider baseUrl is required")))
                .timeout(Duration.ofSeconds(60))
                .header("Authorization", "Bearer " + apiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(requestBody)))
                .build();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            throw new IOException(
                    "OpenAI-compatible provider failed (" + response.statusCode() + "): " + response.body());
        }
        JsonNode root = MAPPER.readTree(response.body());
        String text = extractOpenAiText(root);
        return new ProviderResponse(text, MAPPER.convertValue(root, MAP_TYPE));
    }

    private ProviderResponse anthropic(ProviderProfile profile, String model, String promptText)
            throws IOException, InterruptedException {
        String apiKey = resolveApiKey(profile);
        LinkedHashMap<String, Object> requestBody = new LinkedHashMap<>();
        requestBody.put("model", model);
        requestBody.put("max_tokens", 512);
        requestBody.put("messages", List.of(Map.of("role", "user", "content", promptText)));
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(Objects.requireNonNull(profile.baseUrl(), "Provider baseUrl is required")))
                .timeout(Duration.ofSeconds(60))
                .header("x-api-key", apiKey)
                .header("anthropic-version", "2023-06-01")
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(MAPPER.writeValueAsString(requestBody)))
                .build();
        HttpResponse<String> response =
                httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            throw new IOException("Anthropic provider failed (" + response.statusCode() + "): " + response.body());
        }
        JsonNode root = MAPPER.readTree(response.body());
        JsonNode content = root.path("content");
        String text = content.isArray() && !content.isEmpty()
                ? content.get(0).path("text").asText("")
                : "";
        if (text.isBlank()) {
            throw new IOException("Anthropic response did not include content text.");
        }
        return new ProviderResponse(text, MAPPER.convertValue(root, MAP_TYPE));
    }

    private ProviderResponse consumerSession(
            ProviderProfile profile, String model, String promptText, ExperimentalRequest request)
            throws IOException, InterruptedException {
        ConsumerSessionRecord record = null;
        if (request.sessionFile() != null) {
            record =
                    store.importConsumerSession(request.sessionFile(), profile.id(), profile.provider(), "file-import");
        } else if (request.bootstrapSession()) {
            record = bootstrapConsumerSession(profile);
        } else {
            record = store.getLatestConsumerSession(profile.id());
        }
        if (record == null) {
            throw new IOException("No consumer session material is available for profile " + profile.id() + ".");
        }
        RequestTemplate template =
                mergedTemplate(profile.requestTemplate(), record.material().get("requestTemplate"));
        if (template == null || template.url() == null || template.url().isBlank()) {
            throw new IOException("Consumer session profile is missing requestTemplate.url for live replay.");
        }
        LinkedHashMap<String, String> headers = new LinkedHashMap<>();
        if (template.headers() != null) {
            headers.putAll(template.headers());
        }
        Object materialHeaders = record.material().get("headers");
        if (materialHeaders instanceof Map<?, ?> headerMap) {
            headerMap.forEach((key, value) -> headers.put(String.valueOf(key), String.valueOf(value)));
        }
        Object cookieObject = record.material().get("cookies");
        if (cookieObject instanceof Map<?, ?> cookies && !cookies.isEmpty()) {
            String cookieHeader = cookies.entrySet().stream()
                    .map(entry -> entry.getKey() + "=" + entry.getValue())
                    .reduce((left, right) -> left + "; " + right)
                    .orElse("");
            if (!cookieHeader.isBlank()) {
                headers.put("Cookie", cookieHeader);
            }
        }
        String requestBody = fillTemplate(
                template.bodyTemplate() == null ? "{\"prompt\":\"${prompt}\"}" : template.bodyTemplate(),
                Map.of("prompt", promptText, "model", model));
        HttpRequest.Builder builder =
                HttpRequest.newBuilder().uri(URI.create(template.url())).timeout(Duration.ofSeconds(60));
        headers.forEach(builder::header);
        String method = template.method() == null ? "POST" : template.method().toUpperCase();
        if ("GET".equals(method)) {
            builder.GET();
        } else {
            builder.method(method, HttpRequest.BodyPublishers.ofString(requestBody));
        }
        HttpResponse<String> response =
                httpClient.send(builder.build(), HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        if (response.statusCode() >= 400) {
            throw new IOException(
                    "Consumer session request failed (" + response.statusCode() + "): " + response.body());
        }
        String contentType = response.headers().firstValue("content-type").orElse("");
        Map<String, Object> raw = new LinkedHashMap<>();
        raw.put("sessionId", record.id());
        raw.put("source", record.source());
        raw.put("statusCode", response.statusCode());
        if (contentType.contains("json")) {
            JsonNode root = MAPPER.readTree(response.body());
            raw.put("response", MAPPER.convertValue(root, MAP_TYPE));
            String responseText = extractResponsePath(root, template.responsePath());
            return new ProviderResponse(responseText, raw);
        }
        raw.put("response", response.body());
        return new ProviderResponse(response.body(), raw);
    }

    private ConsumerSessionRecord bootstrapConsumerSession(ProviderProfile profile)
            throws IOException, InterruptedException {
        if (bootstrapCommand == null || bootstrapCommand.isBlank()) {
            throw new IOException("Set " + ExperimentalDefaults.BOOTSTRAP_ENV
                    + " to an external browser bootstrap command before using --experimental-bootstrap-session.");
        }
        ProcessBuilder processBuilder = new ProcessBuilder(shellCommand(bootstrapCommand));
        processBuilder
                .environment()
                .put("STAKEHOLDER_CAPTURE_URL", profile.captureUrl() == null ? "" : profile.captureUrl());
        processBuilder
                .environment()
                .put(
                        "STAKEHOLDER_CAPTURE_SELECTOR",
                        profile.captureSelector() == null ? "" : profile.captureSelector());
        processBuilder.environment().put("STAKEHOLDER_CAPTURE_PROFILE", profile.id());
        processBuilder.environment().put("STAKEHOLDER_CAPTURE_PROVIDER", profile.provider());
        processBuilder
                .environment()
                .put("STAKEHOLDER_STATE_ROOT", store.stateRoot().toString());
        Process process = processBuilder.start();
        String stdout = new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        String stderr = new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8);
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Browser bootstrap failed (" + exitCode + "): " + stderr.trim());
        }
        Map<String, Object> material = MAPPER.readValue(stdout, MAP_TYPE);
        return store.saveConsumerSession(profile.id(), profile.provider(), material, "bootstrap-command");
    }

    private RequestTemplate mergedTemplate(RequestTemplate base, Object override) {
        if (!(override instanceof Map<?, ?> map)) {
            return base;
        }
        Map<String, String> headers = new LinkedHashMap<>();
        if (base != null && base.headers() != null) {
            headers.putAll(base.headers());
        }
        Object overrideHeaders = map.get("headers");
        if (overrideHeaders instanceof Map<?, ?> headerMap) {
            headerMap.forEach((key, value) -> headers.put(String.valueOf(key), String.valueOf(value)));
        }
        Object method = map.containsKey("method") ? map.get("method") : base == null ? null : base.method();
        Object url = map.containsKey("url") ? map.get("url") : base == null ? null : base.url();
        Object bodyTemplate =
                map.containsKey("bodyTemplate") ? map.get("bodyTemplate") : base == null ? null : base.bodyTemplate();
        Object responsePath =
                map.containsKey("responsePath") ? map.get("responsePath") : base == null ? null : base.responsePath();
        return new RequestTemplate(
                stringValue(method), stringValue(url), headers, stringValue(bodyTemplate), stringValue(responsePath));
    }

    private String resolveApiKey(ProviderProfile profile) throws IOException {
        if (profile.apiKey() != null && !profile.apiKey().isBlank()) {
            return profile.apiKey();
        }
        if (profile.apiKeyEnv() != null && !profile.apiKeyEnv().isBlank()) {
            String env = System.getenv(profile.apiKeyEnv());
            if (env != null && !env.isBlank()) {
                return env;
            }
            throw new IOException("Missing API key for " + profile.id() + ". Set " + profile.apiKeyEnv()
                    + " or store apiKey in the encrypted provider profile.");
        }
        throw new IOException("No API key source is configured for provider " + profile.id() + ".");
    }

    private Map<String, Object> cachedOutput(
            ProviderProfile profile,
            PromptAsset promptAsset,
            PromptAssetVersion promptVersion,
            PersonalizationProfile personalizationProfile,
            String adapterMode,
            List<String> selectedFamilies,
            CacheRecord cached) {
        Map<String, Object> cache = new LinkedHashMap<>();
        cache.put("hit", true);
        cache.put("key", cached.cacheKey());
        cache.put("createdAt", cached.createdAt());
        @SuppressWarnings("unchecked")
        Map<String, Object> response = (Map<String, Object>) cached.response();
        return outputMap(
                profile,
                promptAsset,
                promptVersion,
                personalizationProfile,
                new ExperimentalGenerationMode(
                        profile.provider(),
                        profile.id(),
                        adapterMode,
                        promptAsset.id(),
                        promptVersion.version(),
                        personalizationProfile == null ? null : personalizationProfile.id(),
                        false),
                selectedFamilies,
                String.valueOf(response.get("text")),
                (Map<String, Object>) response.get("raw"),
                cache,
                cached.provenance());
    }

    private Map<String, Object> outputMap(
            ProviderProfile profile,
            PromptAsset promptAsset,
            PromptAssetVersion promptVersion,
            PersonalizationProfile personalizationProfile,
            ExperimentalGenerationMode generationMode,
            List<String> selectedFamilies,
            String text,
            Map<String, Object> raw,
            Map<String, Object> cache,
            Map<String, Object> provenance) {
        LinkedHashMap<String, Object> output = new LinkedHashMap<>();
        output.put("mode", "experimental");
        output.put("provider", profile.provider());
        output.put("profileId", profile.id());
        output.put("model", provenance.get("model"));
        output.put("selectedFamilies", selectedFamilies);
        output.put("generationMode", generationMode.toMap());
        output.put(
                "promptAsset",
                Map.of(
                        "id", promptAsset.id(),
                        "version", promptAsset.version(),
                        "label", promptAsset.label()));
        output.put(
                "promptAssetVersion",
                Map.of(
                        "assetId", promptVersion.assetId(),
                        "version", promptVersion.version(),
                        "promptHash", promptVersion.promptHash(),
                        "toolSchemaHash", promptVersion.toolSchemaHash(),
                        "outputSchemaHash", promptVersion.outputSchemaHash(),
                        "evalSuiteVersion", promptVersion.evalSuiteVersion()));
        output.put(
                "personalizationProfile",
                personalizationProfile == null
                        ? null
                        : Map.of(
                                "id", personalizationProfile.id(),
                                "label", personalizationProfile.label()));
        output.put("cache", cache);
        output.put("text", text);
        output.put("raw", raw);
        output.put("provenance", provenance);
        return output;
    }

    private Map<String, Object> configMap(SessionConfig config) {
        LinkedHashMap<String, Object> map = new LinkedHashMap<>();
        map.put("devType", config.devType().cliValue());
        map.put("jargon", config.jargonLevel().cliValue());
        map.put("complexity", config.complexity().cliValue());
        map.put("durationSeconds", config.durationSeconds());
        map.put("alertsEnabled", config.alertsEnabled());
        map.put("projectName", config.projectName());
        map.put("minimalOutput", config.minimalOutput());
        map.put("teamActivity", config.teamActivity());
        map.put("framework", config.framework());
        map.put("seed", config.seed());
        map.put("outputFormat", config.outputFormat().cliValue());
        map.put("noColor", config.noColor());
        map.put("trace", config.trace());
        return map;
    }

    private String fillTemplate(String template, Map<String, String> values) {
        String output = template;
        for (Map.Entry<String, String> entry : values.entrySet()) {
            output = output.replace("${" + entry.getKey() + "}", entry.getValue() == null ? "" : entry.getValue());
        }
        return output;
    }

    private String extractOpenAiText(JsonNode root) throws IOException {
        if (root.hasNonNull("output_text")
                && !root.get("output_text").asText("").isBlank()) {
            return root.get("output_text").asText();
        }
        JsonNode output = root.path("output");
        if (output.isArray() && !output.isEmpty()) {
            for (JsonNode item : output) {
                JsonNode content = item.path("content");
                if (content.isArray() && !content.isEmpty()) {
                    for (JsonNode contentItem : content) {
                        String text = contentItem.path("text").asText("");
                        if (!text.isBlank()) {
                            return text;
                        }
                    }
                }
            }
        }
        throw new IOException("OpenAI-compatible response did not include output text.");
    }

    private String extractResponsePath(JsonNode root, String responsePath) throws IOException {
        if (responsePath == null || responsePath.isBlank()) {
            return root.toString();
        }
        JsonNode cursor = root;
        for (String segment : responsePath.split("\\.")) {
            cursor = cursor.path(segment);
        }
        if (cursor.isMissingNode() || cursor.isNull()) {
            throw new IOException("Consumer session response path " + responsePath + " was not found.");
        }
        if (cursor.isTextual()) {
            return cursor.asText();
        }
        return cursor.toString();
    }

    private String stringValue(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private record ProviderResponse(String text, Map<String, Object> raw) {}
}
