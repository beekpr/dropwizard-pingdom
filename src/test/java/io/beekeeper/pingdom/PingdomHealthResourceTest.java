package io.beekeeper.pingdom;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;

import java.util.ArrayList;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.beekeeper.pingdom.resources.PingdomHealthResource;
import io.beekeeper.pingdom.resources.HealthCheckDetails;
import io.beekeeper.pingdom.resources.dto.PingdomHealth;
import io.dropwizard.setup.Environment;

public class PingdomHealthResourceTest {

    @Test
    public void testResourceReturnsOKWithHealthyHealthCheck() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithHealthyHealthCheckAndSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW).build();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithUnhealthyCheckOfWrongSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW).build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfRightSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH).build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithHealthyCheckOfRightSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH).build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithMultipleChecksOneUnhealthy() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.healthy();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.unhealthy("foo");
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams);

        assertEquals("\nHealthCheck Failed: HealthCheck 2\nReason: foo\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithSeverityAndMultipleChecks1() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW).build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW).build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH).build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH).build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams);

        assertEquals("\nHealthCheck Failed: HealthCheck 3\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithSeverityAndMultipleChecks2() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW).build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW).build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH).build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH).build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_LOW);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams);

        assertEquals("\nHealthCheck Failed: HealthCheck 1\nReason: null\nException: null\n\nHealthCheck Failed: HealthCheck 3\nReason: null\nException: null\n", actual.status);
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
