package io.beekeeper.pingdom.resources;

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

/**
 * The Pingdom Healthcheck endpoint resource
 */
@Path("/health/pingdom")
public class PingdomHealthResource {

    private final PingdomBundleConfiguration configuration;
    private final Environment environment;

    public PingdomHealthResource(PingdomBundleConfiguration configuration, Environment environment) {
        this.configuration = configuration;
        this.environment = environment;
    }

    @GET
    @Produces(MediaType.TEXT_XML)
    public PingdomHealth get(@QueryParam("key") String key) {
        String authKey = configuration.getKey();
        if (authKey != null && !authKey.isEmpty() && !authKey.equals(key)) {
            throw new NotFoundException();
        }

        Timer timer = environment.metrics().timer("io.dropwizard.jetty.MutableServletContextHandler.requests");
        SortedMap<String, Result> healthChecks = environment.healthChecks().runHealthChecks();
        StringBuilder status = new StringBuilder();

        for (Entry<String, Result> healthCheck : healthChecks.entrySet()) {
            Result result = healthCheck.getValue();
            if (!result.isHealthy()) {
                String name = healthCheck.getKey();
                status.append('\n')
                      .append("HealthCheck Failed: ").append(name).append('\n')
                      .append("Reason: ").append(result.getMessage()).append('\n')
                      .append("Exception: ").append(result.getError() != null ? result.getError().getMessage() : null)
                      .append('\n');
            }
        }

        return new PingdomHealth(status.length() == 0 ? PingdomHealth.HEALTH_OK : status.toString(), timer.getSnapshot().getMean() / 1000000.0d);
    }
}
