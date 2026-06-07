package com.stakeholder.experimental;

import static org.junit.jupiter.api.Assertions.assertFalse;

import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.io.TempDir;

final class ExperimentalLiveIntegrationTest {
    @TempDir
    Path tempDir;

    @Test
    @EnabledIfEnvironmentVariable(named = "STAKEHOLDER_JAVA_RUN_LIVE_PROVIDER_TESTS", matches = "true")
    void openAiCompatibleLivePathWorksWhenCredentialsAreSupplied() throws Exception {
        Assumptions.assumeTrue(System.getenv("OPENAI_API_KEY") != null
                && !System.getenv("OPENAI_API_KEY").isBlank());

        ExperimentalRuntime runtime = new ExperimentalRuntime(tempDir, HttpClient.newHttpClient(), null);
        var result = runtime.run(
                new SessionConfig(
                        DevelopmentType.BACKEND,
                        JargonLevel.MEDIUM,
                        Complexity.MEDIUM,
                        1L,
                        false,
                        "live-provider-harness",
                        true,
                        false,
                        "mcp",
                        7L,
                        OutputFormat.JSON,
                        true,
                        false),
                new ExperimentalRequest(
                        "openai-compatible", null, "provider-narrative", null, "local-operator", "api", null, false));

        assertFalse(String.valueOf(result.get("text")).isBlank());
        assertFalse(Files.readString(tempDir.resolve("runtime").resolve("provenance-snapshots.json"))
                .isBlank());
    }
}
