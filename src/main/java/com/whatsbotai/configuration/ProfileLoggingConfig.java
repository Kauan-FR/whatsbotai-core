package com.whatsbotai.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

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

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Environment env = event.getApplicationContext().getEnvironment();

        String[] activeProfiles = env.getActiveProfiles();
        String[] defaultProfiles = env.getDefaultProfiles();
        String applicationName = env.getProperty("spring.application.name", "unknown");
        String serverPort = env.getProperty("server.port", "unknown");
        String datasourceUrl = env.getProperty("spring.datasource.url", "not-configured");

        String activeProfilesStr = activeProfiles.length == 0
                ? "NONE (using defaults: " + String.join(", ", defaultProfiles) + ")"
                : String.join(", ", activeProfiles);

        log.info("");
        log.info("═══════════════════════════════════════════════════════════════════");
        log.info("  Application: {}", applicationName);
        log.info("  Active Profile(s): {}", activeProfilesStr);
        log.info("  Server Port: {}", serverPort);
        log.info("  Datasource: {}", maskDatasourceUrl(datasourceUrl));
        log.info("═══════════════════════════════════════════════════════════════════");
        log.info("");
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
