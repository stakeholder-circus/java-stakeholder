package com.stakeholder;

import com.stakeholder.activities.Activities;
import com.stakeholder.config.CliValue;
import com.stakeholder.config.Complexity;
import com.stakeholder.config.DevelopmentType;
import com.stakeholder.config.JargonLevel;
import com.stakeholder.config.OutputFormat;
import com.stakeholder.config.SessionConfig;
import com.stakeholder.experimental.ExperimentalDefaults;
import com.stakeholder.experimental.ExperimentalRequest;
import com.stakeholder.experimental.ExperimentalRuntime;
import com.stakeholder.output.JsonOutput;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import org.fusesource.jansi.AnsiConsole;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ITypeConverter;
import picocli.CommandLine.Option;

/**
 * Entry point for java-stakeholder.
 */
@Command(
        name = "java-stakeholder",
        mixinStandardHelpOptions = true,
        version = "0.1.0",
        description = "Deterministic stakeholder rewrite foundation")
public final class Main implements Callable<Integer> {
    @Option(
            names = {"-d", "--dev-type"},
            converter = DevelopmentTypeConverter.class,
            defaultValue = "backend",
            description = "Development domain")
    private DevelopmentType devType = DevelopmentType.BACKEND;

    @Option(
            names = {"-j", "--jargon"},
            converter = JargonLevelConverter.class,
            defaultValue = "medium",
            description = "Jargon intensity")
    private JargonLevel jargonLevel = JargonLevel.MEDIUM;

    @Option(
            names = {"-c", "--complexity"},
            converter = ComplexityConverter.class,
            defaultValue = "medium",
            description = "Complexity level")
    private Complexity complexity = Complexity.MEDIUM;

    @Option(
            names = {"-T", "--duration"},
            defaultValue = "0",
            description = "Duration in seconds; 0 means until interrupted")
    private long durationSeconds = 0L;

    @Option(
            names = {"-a", "--alerts"},
            defaultValue = "false",
            description = "Enable alert activity")
    private boolean alertsEnabled;

    @Option(
            names = {"-p", "--project"},
            defaultValue = "distributed-cluster",
            description = "Project name")
    private String projectName = "distributed-cluster";

    @Option(
            names = {"--minimal"},
            defaultValue = "false",
            description = "Reduce styling only")
    private boolean minimalOutput;

    @Option(
            names = {"-t", "--team"},
            defaultValue = "false",
            description = "Enable team activity")
    private boolean teamActivity;

    @Option(
            names = {"-F", "--framework"},
            defaultValue = "",
            description = "Framework name")
    private String framework = "";

    @Option(
            names = {"--seed"},
            description = "Deterministic seed")
    private Long seed;

    @Option(
            names = {"--output-format"},
            converter = OutputFormatConverter.class,
            defaultValue = "text",
            description = "Output format: text or json")
    private OutputFormat outputFormat = OutputFormat.TEXT;

    @Option(
            names = {"--no-color"},
            defaultValue = "false",
            description = "Disable color output")
    private boolean noColor;

    @Option(
            names = {"--trace"},
            defaultValue = "false",
            description = "Emit trace details")
    private boolean trace;

    @Option(
            names = {"--list-values"},
            defaultValue = "false",
            description = "List accepted enum values as JSON")
    private boolean listValues;

    @Option(
            names = {"--experimental-provider"},
            description = "Experimental provider id or provider family")
    private String experimentalProvider;

    @Option(
            names = {"--experimental-profile"},
            description = "Experimental provider profile id")
    private String experimentalProfile;

    @Option(
            names = {"--experimental-prompt"},
            description = "Experimental prompt asset id")
    private String experimentalPrompt;

    @Option(
            names = {"--experimental-model"},
            description = "Experimental provider model override")
    private String experimentalModel;

    @Option(
            names = {"--experimental-personalization"},
            description = "Experimental personalization profile id")
    private String experimentalPersonalization;

    @Option(
            names = {"--experimental-adapter-mode"},
            description = "Experimental adapter mode: api or consumer")
    private String experimentalAdapterMode;

    @Option(
            names = {"--experimental-session-file"},
            description = "Path to consumer-session material JSON for experimental mode")
    private Path experimentalSessionFile;

    @Option(
            names = {"--experimental-bootstrap-session"},
            defaultValue = "false",
            description = "Use the configured browser bootstrap command to capture consumer-session material")
    private boolean experimentalBootstrapSession;

    public static void main(String[] args) {
        AnsiConsole.systemInstall();
        int exitCode = new CommandLine(new Main()).execute(args);
        AnsiConsole.systemUninstall();
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        if (listValues) {
            JsonOutput.writeObject(AnsiConsole.out(), availableValues());
            return 0;
        }

        SessionConfig config = new SessionConfig(
                devType,
                jargonLevel,
                complexity,
                durationSeconds,
                alertsEnabled,
                projectName,
                minimalOutput,
                teamActivity,
                framework,
                seed,
                outputFormat,
                noColor,
                trace);
        if (experimentalRequested()) {
            if (experimentalProvider == null || experimentalProvider.isBlank()) {
                System.err.println("Experimental mode requires --experimental-provider.");
                return 2;
            }
            try {
                ExperimentalRuntime runtime = new ExperimentalRuntime();
                Map<String, Object> payload = runtime.run(
                        config,
                        new ExperimentalRequest(
                                experimentalProvider,
                                experimentalProfile,
                                experimentalPrompt,
                                experimentalModel,
                                experimentalPersonalization,
                                experimentalAdapterMode,
                                experimentalSessionFile,
                                experimentalBootstrapSession));
                if (outputFormat == OutputFormat.JSON) {
                    JsonOutput.writeObject(AnsiConsole.out(), payload);
                } else {
                    String text = String.valueOf(payload.getOrDefault("text", ""));
                    if (!text.isBlank()) {
                        AnsiConsole.out().println(text);
                    }
                    if (trace) {
                        @SuppressWarnings("unchecked")
                        Map<String, Object> provenance = (Map<String, Object>) payload.get("provenance");
                        @SuppressWarnings("unchecked")
                        Map<String, Object> cache = (Map<String, Object>) payload.get("cache");
                        AnsiConsole.out()
                                .println("[experimental provider="
                                        + payload.get("provider")
                                        + " model="
                                        + payload.get("model")
                                        + " cache="
                                        + (cache == null ? "unknown" : cache.get("hit"))
                                        + " promptVersion="
                                        + (provenance == null ? "unknown" : provenance.get("promptVersion"))
                                        + "]");
                    }
                }
                return 0;
            } catch (Exception exception) {
                System.err.println("experimental provider runtime failed: " + exception.getMessage());
                return 2;
            }
        }
        return Activities.simulate(config);
    }

    private static Map<String, Object> availableValues() {
        Map<String, Object> values = new LinkedHashMap<>();
        values.put("devType", enumValues(DevelopmentType.values()));
        values.put("jargon", enumValues(JargonLevel.values()));
        values.put("complexity", enumValues(Complexity.values()));
        values.put("outputFormat", enumValues(OutputFormat.values()));
        values.put("generatorFamilies", Activities.availableGeneratorFamilies());
        values.put(
                "flags",
                List.of("alerts", "minimal", "team", "seed", "output-format", "no-color", "trace", "list-values"));
        values.put("experimentalProviders", ExperimentalDefaults.availableProviderIds());
        values.put("experimentalPromptAssets", ExperimentalDefaults.availablePromptAssetIds());
        values.put("experimentalPersonalizationProfiles", ExperimentalDefaults.availablePersonalizationProfileIds());
        values.put("experimentalFlags", ExperimentalDefaults.availableFlagNames());
        return values;
    }

    private static <T extends CliValue> List<String> enumValues(T[] values) {
        return java.util.Arrays.stream(values).map(CliValue::cliValue).toList();
    }

    private boolean experimentalRequested() {
        return isSet(experimentalProvider)
                || isSet(experimentalProfile)
                || isSet(experimentalPrompt)
                || isSet(experimentalModel)
                || isSet(experimentalPersonalization)
                || isSet(experimentalAdapterMode)
                || experimentalSessionFile != null
                || experimentalBootstrapSession;
    }

    private static boolean isSet(String value) {
        return value != null && !value.isBlank();
    }

    private static final class DevelopmentTypeConverter implements ITypeConverter<DevelopmentType> {
        @Override
        public DevelopmentType convert(String value) {
            return DevelopmentType.fromCliValue(value);
        }
    }

    private static final class JargonLevelConverter implements ITypeConverter<JargonLevel> {
        @Override
        public JargonLevel convert(String value) {
            return JargonLevel.fromCliValue(value);
        }
    }

    private static final class ComplexityConverter implements ITypeConverter<Complexity> {
        @Override
        public Complexity convert(String value) {
            return Complexity.fromCliValue(value);
        }
    }

    private static final class OutputFormatConverter implements ITypeConverter<OutputFormat> {
        @Override
        public OutputFormat convert(String value) {
            return OutputFormat.fromCliValue(value);
        }
    }
}
