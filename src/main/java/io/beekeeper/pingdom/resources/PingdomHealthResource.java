package io.beekeeper.pingdom.resources;

import java.util.Arrays;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;

import javax.ws.rs.GET;
import javax.ws.rs.NotFoundException;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.Timer;
import com.codahale.metrics.health.HealthCheck.Result;

import io.beekeeper.pingdom.PingdomBundleConfiguration;
import io.beekeeper.pingdom.resources.dto.PingdomHealth;
import io.dropwizard.setup.Environment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Pingdom Healthcheck endpoint resource
 */
@Path("/health/pingdom")
public class PingdomHealthResource {
    private final Logger log = LoggerFactory.getLogger(PingdomHealthResource.class);

    private final PingdomBundleConfiguration configuration;
    private final Environment environment;

    public PingdomHealthResource(PingdomBundleConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public PingdomHealth get(
            @QueryParam("key") String key,
            @QueryParam("severity") List<String> severity,
            @QueryParam("category") List<String> categories
    ) {
        String authKey = configuration.getKey();
        if (authKey != null && !authKey.isEmpty() && !authKey.equals(key)) {
            throw new NotFoundException();
        }

        if (severity == null || severity.isEmpty()) {
            severity = Arrays.asList(HealthCheckDetails.Severity.VALUES);
        }

        String defaultCategory = configuration.getDefaultCategory();

        boolean filterByCategories = true;
        boolean categoryFilterSpecified = (categories != null && categories.size() > 0);

        if (!categoryFilterSpecified) {
            if (defaultCategory != null) {
                // If we have a default category, and no category is given as a query parameter, we return all checks of
                // the default category.
                categories = Arrays.asList(defaultCategory);
            } else {
                // If no default category is set, and no category is given as a query parameter, we return all
                // categories.
                filterByCategories = false;
            }
        }

        Timer timer = environment.metrics().timer("io.dropwizard.jetty.MutableServletContextHandler.requests");

        SortedMap<String, Result> healthChecks = environment.healthChecks().runHealthChecks();
        StringBuilder status = new StringBuilder();

        for (Entry<String, Result> healthCheck : healthChecks.entrySet()) {
            Result result = healthCheck.getValue();

            Object resultSeverity = result.getDetails() != null
                ? result.getDetails().get(HealthCheckDetails.Severity.KEY)
                : null;

            Object resultCategory = result.getDetails() != null
                ? result.getDetails().get(HealthCheckDetails.Category.KEY)
                : null;

            if (resultCategory == null) {
                resultCategory = defaultCategory;
            }

            boolean isUnhealthy = !result.isHealthy();

            boolean severityFilterMatches = (resultSeverity == null
                || severity.contains(resultSeverity));

            boolean categoryFilterMatches = (!filterByCategories
                || resultCategory == null
                || categories.contains(resultCategory));


            if (isUnhealthy && severityFilterMatches && categoryFilterMatches) {
                String name = healthCheck.getKey();
                status.append('\n')
                    .append("HealthCheck Failed: ")
                    .append(name)
                    .append('\n')
                    .append("Reason: ")
                    .append(result.getMessage())
                    .append('\n')
                    .append("Exception: ")
                    .append(result.getError() != null ? result.getError().getMessage() : null)
                    .append('\n');
            }
        }
        String statusMessage;
        if (status.length() == 0) {
            statusMessage = PingdomHealth.HEALTH_OK;
        } else {
            statusMessage = status.toString();
            log.info(statusMessage);
        }
        return new PingdomHealth(statusMessage, timer.getSnapshot().getMean() / 1000000.0d);
    }
}
