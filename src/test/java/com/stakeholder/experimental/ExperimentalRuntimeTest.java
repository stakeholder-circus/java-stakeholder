package com.stakeholder.experimental;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

final class ExperimentalRuntimeTest {
    private static final ObjectMapper MAPPER = new ObjectMapper().findAndRegisterModules();

    @TempDir
    Path tempDir;

    @Test
    void storeSeedsDefaultsAndEncryptsSensitiveFiles() throws Exception {
        ExperimentalStore store = new ExperimentalStore(tempDir);

        List<ProviderProfile> profiles = store.listProviderProfiles();
        List<PromptAsset> promptAssets = store.listPromptAssets();
        List<PersonalizationProfile> personalizationProfiles = store.listPersonalizationProfiles();

        assertTrue(profiles.stream().anyMatch(profile -> "openai-compatible".equals(profile.id())));
        assertTrue(promptAssets.stream().anyMatch(asset -> "operator-brief".equals(asset.id())));
        assertTrue(personalizationProfiles.stream().anyMatch(profile -> "local-operator".equals(profile.id())));

        String encryptedProviderFile = Files.readString(
                tempDir.resolve("encrypted").resolve("provider-profiles.json.enc"), StandardCharsets.UTF_8);
        assertFalse(encryptedProviderFile.contains("openai-compatible"));
        assertTrue(Files.exists(tempDir.resolve(".secrets").resolve("master.key")));
    }

    @Test
    void localDemoCachesResponsesAndPersistsProvenance() throws Exception {
        ExperimentalRuntime runtime = new ExperimentalRuntime(tempDir, HttpClient.newHttpClient(), null);

        Map<String, Object> first = runtime.run(
                config(),
                new ExperimentalRequest(
                        "local-demo", null, "operator-brief", null, "local-operator", "api", null, false));
        Map<String, Object> second = runtime.run(
                config(),
                new ExperimentalRequest(
                        "local-demo", null, "operator-brief", null, "local-operator", "api", null, false));

        @SuppressWarnings("unchecked")
        Map<String, Object> firstCache = (Map<String, Object>) first.get("cache");
        @SuppressWarnings("unchecked")
        Map<String, Object> secondCache = (Map<String, Object>) second.get("cache");
        assertEquals(Boolean.FALSE, firstCache.get("hit"));
        assertEquals(Boolean.TRUE, secondCache.get("hit"));
        assertEquals(first.get("text"), second.get("text"));
        assertTrue(Files.exists(tempDir.resolve("runtime").resolve("provenance-snapshots.json")));
    }

    @Test
    void openAiCompatibleAdapterPassesAgainstLocalContractServer() throws Exception {
        try (ContractServer server = ContractServer.json(200, "{\"output_text\":\"local-openai-response\"}")) {
            seedProviderProfile(
                    tempDir,
                    new ProviderProfile(
                            "openai-compatible",
                            "openai-compatible",
                            "OpenAI-compatible API",
                            server.url(),
                            null,
                            "token",
                            "gpt-5.4-mini",
                            List.of("api"),
                            null,
                            null,
                            null,
                            "2026-04-09T00:00:00Z"));
            ExperimentalRuntime runtime = new ExperimentalRuntime(tempDir, HttpClient.newHttpClient(), null);

            Map<String, Object> output = runtime.run(
                    config(),
                    new ExperimentalRequest(
                            "openai-compatible",
                            null,
                            "provider-narrative",
                            null,
                            "local-operator",
                            "api",
                            null,
                            false));

            assertEquals("local-openai-response", output.get("text"));
            assertEquals("openai-compatible", output.get("provider"));
        }
    }

    @Test
    void anthropicAdapterPassesAgainstLocalContractServer() throws Exception {
        try (ContractServer server =
                ContractServer.json(200, "{\"content\":[{\"text\":\"local-anthropic-response\"}]}")) {
            seedProviderProfile(
                    tempDir,
                    new ProviderProfile(
                            "anthropic",
                            "anthropic",
                            "Anthropic API",
                            server.url(),
                            null,
                            "token",
                            "claude-sonnet-4-5",
                            List.of("api"),
                            null,
                            null,
                            null,
                            "2026-04-09T00:00:00Z"));
            ExperimentalRuntime runtime = new ExperimentalRuntime(tempDir, HttpClient.newHttpClient(), null);

            Map<String, Object> output = runtime.run(
                    config(),
                    new ExperimentalRequest(
                            "anthropic", null, "provider-narrative", null, "local-operator", "api", null, false));

            assertEquals("local-anthropic-response", output.get("text"));
            assertEquals("anthropic", output.get("provider"));
        }
    }

    @Test
    void consumerSessionAdapterImportsFileAndReplaysTemplate() throws Exception {
        try (ContractServer server =
                ContractServer.json(200, "{\"result\":{\"text\":\"consumer-session-response\"}}")) {
            Path sessionFile = tempDir.resolve("consumer-session.json");
            Map<String, Object> material = new LinkedHashMap<>();
            material.put(
                    "requestTemplate",
                    Map.of(
                            "method", "POST",
                            "url", server.url(),
                            "headers", Map.of("Content-Type", "application/json"),
                            "bodyTemplate", "{\"prompt\":\"${prompt}\"}",
                            "responsePath", "result.text"));
            material.put("cookies", Map.of("session", "cookie-value"));
            Files.writeString(sessionFile, MAPPER.writeValueAsString(material), StandardCharsets.UTF_8);

            ExperimentalRuntime runtime = new ExperimentalRuntime(tempDir, HttpClient.newHttpClient(), null);
            Map<String, Object> output = runtime.run(
                    config(),
                    new ExperimentalRequest(
                            "consumer-session",
                            "consumer-session",
                            "provider-narrative",
                            null,
                            "local-operator",
                            "consumer",
                            sessionFile,
                            false));

            assertEquals("consumer-session-response", output.get("text"));
            assertEquals("consumer-session", output.get("provider"));
        }
    }

    @Test
    void bootstrapCommandCanCaptureConsumerSessionMaterial() throws Exception {
        try (ContractServer server =
                ContractServer.json(200, "{\"result\":{\"text\":\"bootstrap-session-response\"}}")) {
            Path script = tempDir.resolve("bootstrap-session.sh");
            String scriptContent = """
                    #!/bin/sh
                    printf '%%s' '{"requestTemplate":{"method":"POST","url":"%s","headers":{"Content-Type":"application/json"},"bodyTemplate":"{\\"prompt\\":\\"${prompt}\\"}","responsePath":"result.text"},"cookies":{"session":"bootstrap-cookie"}}'
                    """.formatted(server.url());
            Files.writeString(script, scriptContent, StandardCharsets.UTF_8);
            script.toFile().setExecutable(true);

            ExperimentalRuntime runtime =
                    new ExperimentalRuntime(tempDir, HttpClient.newHttpClient(), script.toString());
            Map<String, Object> output = runtime.run(
                    config(),
                    new ExperimentalRequest(
                            "consumer-session",
                            "consumer-session",
                            "provider-narrative",
                            null,
                            "local-operator",
                            "consumer",
                            null,
                            true));

            assertEquals("bootstrap-session-response", output.get("text"));
        }
    }

    private static SessionConfig config() {
        return new SessionConfig(
                DevelopmentType.SECURITY,
                JargonLevel.HIGH,
                Complexity.EXTREME,
                1L,
                true,
                "hospital-ocpp-quantum-control",
                true,
                true,
                "mcp grpc experimental",
                42L,
                OutputFormat.JSON,
                true,
                true);
    }

    private static void seedProviderProfile(Path tempDir, ProviderProfile profile) throws Exception {
        ExperimentalStore store = new ExperimentalStore(tempDir);
        List<ProviderProfile> profiles = List.of(profile);
        String payload = new ObjectMapper().findAndRegisterModules().writeValueAsString(profiles);
        byte[] key = Files.exists(tempDir.resolve(".secrets").resolve("master.key"))
                ? java.util.Base64.getDecoder()
                        .decode(Files.readString(
                                        tempDir.resolve(".secrets").resolve("master.key"), StandardCharsets.UTF_8)
                                .trim())
                : null;
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/GCM/NoPadding");
        byte[] iv = java.security.SecureRandom.getSeed(12);
        cipher.init(
                javax.crypto.Cipher.ENCRYPT_MODE,
                new javax.crypto.spec.SecretKeySpec(key, "AES"),
                new javax.crypto.spec.GCMParameterSpec(128, iv));
        byte[] encrypted = cipher.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        byte[] cipherText = java.util.Arrays.copyOf(encrypted, encrypted.length - 16);
        byte[] tag = java.util.Arrays.copyOfRange(encrypted, encrypted.length - 16, encrypted.length);
        Map<String, Object> envelope = new LinkedHashMap<>();
        envelope.put("algorithm", "aes-256-gcm");
        envelope.put("iv", java.util.Base64.getEncoder().encodeToString(iv));
        envelope.put("tag", java.util.Base64.getEncoder().encodeToString(tag));
        envelope.put("ciphertext", java.util.Base64.getEncoder().encodeToString(cipherText));
        Files.writeString(
                tempDir.resolve("encrypted").resolve("provider-profiles.json.enc"),
                new ObjectMapper().findAndRegisterModules().writeValueAsString(envelope),
                StandardCharsets.UTF_8);
    }

    private static final class ContractServer implements AutoCloseable {
        private final HttpServer server;

        private ContractServer(HttpServer server) {
            this.server = server;
        }

        static ContractServer json(int status, String body) throws IOException {
            HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
            server.createContext("/", exchange -> {
                byte[] payload = body.getBytes(StandardCharsets.UTF_8);
                exchange.getResponseHeaders().add("Content-Type", "application/json");
                exchange.sendResponseHeaders(status, payload.length);
                exchange.getResponseBody().write(payload);
                exchange.close();
            });
            server.start();
            return new ContractServer(server);
        }

        String url() {
            return "http://127.0.0.1:" + server.getAddress().getPort() + "/";
        }

        @Override
        public void close() {
            server.stop(0);
        }
    }
}
