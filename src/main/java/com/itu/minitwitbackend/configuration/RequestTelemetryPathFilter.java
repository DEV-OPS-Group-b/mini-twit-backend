package com.itu.minitwitbackend.configuration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

import com.microsoft.applicationinsights.extensibility.TelemetryProcessor;
import com.microsoft.applicationinsights.telemetry.RequestTelemetry;
import com.microsoft.applicationinsights.telemetry.Telemetry;

/**
 * Prevents telemetry for url paths from being transmitted to azure app insights.
 * For example /v1/actuator/health
 */
@Component
public class RequestTelemetryPathFilter implements TelemetryProcessor {

    private static final Logger LOGGER = LogManager.getLogger(RequestTelemetryPathFilter.class);

    private final List<String> ignoredPaths;
    private final AntPathMatcher matcher;

    /**
     * Prevents the paths provided as parameter from being transmitted to azure app insights.
     */
    @Autowired
    public RequestTelemetryPathFilter(@Value("${ignoredRequestTelemetryPaths}") String[] ignoredRequestTelemetryPaths) {
        ignoredPaths = Arrays.asList(ignoredRequestTelemetryPaths);

        matcher = new AntPathMatcher();
        matcher.setCaseSensitive(false);
    }

    @Override
    public boolean process(Telemetry telemetry) {
        if (telemetry instanceof RequestTelemetry) {
            RequestTelemetry requestTelemetry = (RequestTelemetry) telemetry;

            try {
                URL url = requestTelemetry.getUrl();

                return ignoredPaths.stream().noneMatch(pattern -> matcher.match(pattern, url.getPath()));
            } catch (MalformedURLException e) {
                LOGGER.error("Error getting RequestTelemetry URL", e);
            }
        }
        return true;
    }
}
