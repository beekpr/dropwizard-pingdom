package io.beekeeper.pingdom;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.beekeeper.pingdom.resources.PingdomHealthResource;
import io.beekeeper.pingdom.resources.dto.PingdomHealth;
import io.dropwizard.setup.Environment;

public class PingdomHealthResourceTest {

    // A A A
    @Test
    public void testResourceReturnsOKWithHealthyHealthCheck() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null);

        assertEquals("OK", actual.status);
    }

    private PingdomBundleConfiguration aConfiguration() {
        return aConfiguration(null);
    }

    private PingdomBundleConfiguration aConfiguration(String authKey) {
        PingdomBundleConfiguration config = new PingdomBundleConfiguration();
        config.setKey(authKey);
        return config;
    }

    private Environment env() {
        return env(new HealthCheck[] {});
    }

    private Environment env(HealthCheck... checks) {
        Environment environment = new Environment("Test Environment", new ObjectMapper(), null, new MetricRegistry(), this.getClass().getClassLoader());

        int i = 0;
        for (HealthCheck healthCheck : checks) {
            environment.healthChecks().register("HealthCheck " + i++, healthCheck);
        }
        return environment;
    }

}
