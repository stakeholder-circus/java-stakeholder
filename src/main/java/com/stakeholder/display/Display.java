package com.stakeholder.display;

import com.stakeholder.config.SessionConfig;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

/**
 * Basic display utilities.
 */
public final class Display {
    private Display() {
        // no instances
    }

    public static void show(SessionConfig config, String message) {
        AnsiConsole.out().println(message);
    }

    public static void showIntro(SessionConfig config, int plannedActivities) {
        show(
                config,
                accent(config, Ansi.Color.GREEN, "java-stakeholder")
                        + " project=" + config.projectName()
                        + " dev-type=" + config.devType().cliValue()
                        + " jargon=" + config.jargonLevel().cliValue()
                        + " complexity=" + config.complexity().cliValue());

        if (!config.framework().isBlank()) {
            show(config, "framework=" + config.framework());
        }

        show(
                config,
                "planned-activity-slots=" + plannedActivities
                        + " duration-seconds=" + config.durationSeconds()
                        + " alerts=" + config.alertsEnabled()
                        + " team=" + config.teamActivity());
    }

    public static void showTrace(SessionConfig config, String message) {
        show(config, accent(config, Ansi.Color.CYAN, "[trace] " + message));
    }

    public static void showGap(SessionConfig config, String message) {
        show(config, accent(config, Ansi.Color.YELLOW, message));
    }

    private static String accent(SessionConfig config, Ansi.Color color, String message) {
        if (colorsEnabled(config)) {
            return Ansi.ansi().fg(color).a(message).reset().toString();
        }
        return message;
    }

    private static boolean colorsEnabled(SessionConfig config) {
        return !config.noColor() && !config.minimalOutput() && System.getenv("NO_COLOR") == null;
    }
}
