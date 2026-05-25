package com.whatsbotai.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.management.ManagementFactory;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Logs the active Spring profile(s) when the application is ready.
 *
 * <p>This is a diagnostic listener that fires once at startup, immediately
 * after the application context is fully initialized. It provides a clear,
 * visual confirmation of which environment the application is running under,
 * which is critical when multiple profiles exist (dev-docker, dev-local, prod).
 *
 * <p>The log output is intentionally formatted with a visual banner to make it
 * easy to spot in console output, especially when scrolling through startup
 * logs during development.
 *
 * <p><b>Why ApplicationListener instead of @PostConstruct or CommandLineRunner?</b>
 * {@link ApplicationReadyEvent} fires after the application is fully ready to
 * accept traffic, ensuring all autoconfiguration (including profile resolution)
 * is complete. Earlier hooks may log incomplete state.
 *
 * @author Kauan
 * @since 1.0
 */

@Component
public class ProfileLoggingConfig implements ApplicationListener<ApplicationReadyEvent>{

    private static final Logger log = LoggerFactory.getLogger(ProfileLoggingConfig.class);

    private static final DateTimeFormatter TZ_OFFSET_TIME_FORMATTER = DateTimeFormatter.ofPattern("XXX");

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();

        String applicationName = env.getProperty("spring.application.name", "unknown");
        String serverPort = env.getProperty("server.port", "unknown");
        String datasourceUrl = env.getProperty("spring.datasource.url", "not-configured");
        String activeProfilesStr = formatActiveProfile(env);
        String actuatorEndpoints = formatActuatorEndpoints(env);

        // Runtime info (JVM, memory, PID, timezone, uptime)
        String jvmInfo = formatJvmInfo();
        String memoryInfo = formatMemoryInfo();
        String timezoneInfo = formatTimezoneString();
        long pid = ProcessHandle.current().pid();
        double startupSeconds = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;

        log.info("");
        log.info("═══════════════════════════════════════════════════════════════════");
        log.info("  Application       : {}", applicationName);
        log.info("  Active Profile(s) : {}", activeProfilesStr);
        log.info("  Server Port       : {}", serverPort);
        log.info("  Datasource        : {}", maskDatasourceUrl(datasourceUrl));
        log.info("  ----------------------------------------------------------------");
        log.info("  JVM               : {}", jvmInfo);
        log.info("  Memory            : {}", memoryInfo);
        log.info("  Timezone          : {}", timezoneInfo);
        log.info("  Process ID        : {}", pid);
        log.info("  Startup Time      : {} s", String.format("%.3f", startupSeconds));
        log.info("  ----------------------------------------------------------------");
        log.info("  Actuator          : {}", actuatorEndpoints);
        log.info("═══════════════════════════════════════════════════════════════════");
        log.info("");
    }

    /**
     * Formats active profiles, warning if more than one is active (which is
     * usually unintentional and can lead to subtle config bugs).
     */
    private String formatActiveProfile(Environment env) {
        String[] activeProfiles = env.getActiveProfiles();

        if (activeProfiles.length == 0) {
            return "NONE (using defaults: " + String.join(", ", env.getDefaultProfiles()) + ")";
        }
        if (activeProfiles.length > 1) {
            return String.join(", ", activeProfiles) + "  ⚠ MULTIPLE PROFILES ACTIVE";
        }
        return activeProfiles[0];
    }

    /**
     * Formats JVM vendor and version.
     * Example: {@code OpenJDK 21.0.5 (Eclipse Adoptium)}
     */
    private String formatJvmInfo() {
        String vmName = System.getProperty("java.vm.name", "unknown");
        String version = System.getProperty("java.version", "unknown");
        String vendor = System.getProperty("java.vendor", "unknown");
        return String.format("| VM Name: %s | Version: %s | Vendor: (%s) |", vmName, version, vendor);
    }

    /**
     * Formats heap memory: used / total / max (all in MB).
     * Useful for detecting memory pressure during startup.
     */
    private String formatMemoryInfo() {
        Runtime runtime = Runtime.getRuntime();
        long used = (runtime.totalMemory() - runtime.freeMemory()) / (1024 * 1024);
        long total = runtime.totalMemory() / (1024 * 1024);
        long max = runtime.maxMemory() / (1024 * 1024);
        return String.format("| %d MB used | %d MB total | %d MB max |", used, total, max);
    }

    /**
     * Formats system timezone with UTC offset.
     * Example: {@code America/Recife (UTC-03:00)}
     */
    private String formatTimezoneString() {
        ZoneId zone = ZoneId.systemDefault();
        String offset = ZonedDateTime.now(zone).format(TZ_OFFSET_TIME_FORMATTER);
        return String.format("%s (UTC%s)", zone.getId(), offset);
    }

    /**
     * Reads exposed Actuator endpoints from configuration.
     * Returns "none" if Actuator isn't configured.
     */
    private String formatActuatorEndpoints(Environment env) {
        String exposed = env.getProperty("management.endpoints.web.exposure.include");

        if (exposed == null || exposed.isBlank()) {
            return "none";
        }

        // Comma-separated string from YAML may have whitespace — normalize.
        String normalized = Arrays.stream(exposed.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(", "));

        String basePath = env.getProperty("management.endpoints.web.base-path", "/actuator");
        return String.format("%s (base path: %s)", normalized, basePath);
    }

    /**
     * Masks sensitive parts of the datasource URL while keeping it identifiable.
     *
     * <p>Example: {@code jdbc:postgresql://localhost:5432/whatsbotai} stays as is
     * (no credentials in URL). For URLs with embedded credentials, the password
     * portion is replaced with asterisks.
     *
     * @param url the raw datasource URL from the environment
     * @return a sanitized version safe for log output
     */
    private String maskDatasourceUrl(String url) {
        if (url == null || url.isEmpty()) {
            return "not-configured";
        }
        // Mask any password embedded in URL (format: //user:password@host)
        return url.replaceAll("://([^:]+):([^@]+)@", "://$1:****@");
    }
}
