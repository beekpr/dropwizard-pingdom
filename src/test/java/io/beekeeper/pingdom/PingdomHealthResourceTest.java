package io.beekeeper.pingdom;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.health.HealthCheck;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.beekeeper.pingdom.resources.HealthCheckDetails;
import io.beekeeper.pingdom.resources.PingdomHealthResource;
import io.beekeeper.pingdom.resources.dto.PingdomHealth;
import io.dropwizard.setup.Environment;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

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

        PingdomHealth actual = testObject.get(null, null, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithHealthyHealthCheckAndSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .healthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithHealthyHealthCheckAndCategory() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().healthy().withDetail(HealthCheckDetails.Category.KEY, "someCategory").build();
            }
        });

        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithUnhealthyCheckOfWrongSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfRightSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfRightSeverityAndCategory() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .withDetail(HealthCheckDetails.Category.KEY, "someCategory")
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        ArrayList<String> targetQueryParams2 = new ArrayList<>();
        targetQueryParams.add("someCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, targetQueryParams2);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfRightSeverityAndCategoryWithDefaultConfigured()
            throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .withDetail(HealthCheckDetails.Category.KEY, "someCategory")
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        ArrayList<String> targetQueryParams2 = new ArrayList<>();
        targetQueryParams2.add("someCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(
                aConfigurationWithDefaultCategory("defaultCategory"),
                target
        );

        PingdomHealth actual = testObject.get(null, targetQueryParams, targetQueryParams2);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfRightSeverityAndDefaultCategoryWithDefaultConfigured()
            throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .withDetail(HealthCheckDetails.Category.KEY, "defaultCategory")
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(
                aConfigurationWithDefaultCategory("defaultCategory"),
                target
        );

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfRightCategory() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Category.KEY, "someCategory").build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add("someCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null, targetQueryParams);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfNoCategoryWhenDefaultConfigured() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add("defaultCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(
                aConfigurationWithDefaultCategory("defaultCategory"),
                target
        );

        PingdomHealth actual = testObject.get(null, null, targetQueryParams);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfDefaultCategoryWhenDefaultConfigured() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Category.KEY, "defaultCategory")
                    .build();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(
                aConfigurationWithDefaultCategory("defaultCategory"),
                target
        );

        PingdomHealth actual = testObject.get(null, null, null);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithUnhealthyCheckOfAnyCategoryWhenNoDefaultConfigured() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Category.KEY, "someCategory").build();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null, null);

        assertEquals("\nHealthCheck Failed: HealthCheck 0\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithUnhealthyCheckOfWrongCategory() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Category.KEY, "someCategory").build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add("someOtherCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, null, targetQueryParams);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithUnhealthyCheckOfWrongCategoryRightSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Category.KEY, "someCategory")
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        ArrayList<String> targetQueryParams2 = new ArrayList<>();
        targetQueryParams2.add("someOtherCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, targetQueryParams2);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithUnhealthyCheckOfWrongSeverityRightCategory() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Category.KEY, "someCategory")
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        ArrayList<String> targetQueryParams2 = new ArrayList<>();
        targetQueryParams2.add("someCategory");
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, targetQueryParams2);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithUnhealthyCheckOfNonDefaultCategory() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder().unhealthy().withDetail(HealthCheckDetails.Category.KEY, "someCategory").build();
            }
        });
        PingdomHealthResource testObject = new PingdomHealthResource(
                aConfigurationWithDefaultCategory("defaultCategory"),
                target
        );

        PingdomHealth actual = testObject.get(null, null, null);

        assertEquals("OK", actual.status);
    }

    @Test
    public void testResourceReturnsOKWithHealthyCheckOfRightSeverity() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .healthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

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
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

        assertEquals("\nHealthCheck Failed: HealthCheck 2\nReason: foo\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithSeverityAndMultipleChecks1() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .healthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .healthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

        assertEquals("\nHealthCheck Failed: HealthCheck 3\nReason: null\nException: null\n", actual.status);
    }

    @Test
    public void testResourceReturnsNotOKWithSeverityAndMultipleChecks2() throws Exception {
        Environment target = env(new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .healthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_LOW)
                    .build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .healthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        }, new HealthCheck() {
            @Override
            protected Result check() throws Exception {
                return Result.builder()
                    .unhealthy()
                    .withDetail(HealthCheckDetails.Severity.KEY, HealthCheckDetails.Severity.VALUE_HIGH)
                    .build();
            }
        });
        ArrayList<String> targetQueryParams = new ArrayList<>();
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_HIGH);
        targetQueryParams.add(HealthCheckDetails.Severity.VALUE_LOW);
        PingdomHealthResource testObject = new PingdomHealthResource(aConfiguration(), target);

        PingdomHealth actual = testObject.get(null, targetQueryParams, null);

        assertEquals(
            "\nHealthCheck Failed: HealthCheck 1\nReason: null\nException: null\n\nHealthCheck Failed: HealthCheck 3\nReason: null\nException: null\n",
            actual.status
        );
    }

    public static PingdomBundleConfiguration aConfiguration() {
        return aConfiguration(null, null);
    }

    public static Environment env(HealthCheck... checks) {
        Environment environment = new Environment(
                "Test Environment",
                new ObjectMapper(),
                null,
                new MetricRegistry(),
                PingdomHealthResourceTest.class.getClassLoader()
        );

        int i = 0;
        for (HealthCheck healthCheck : checks) {
            environment.healthChecks().register("HealthCheck " + i++, healthCheck);
        }
        return environment;
    }

    private static PingdomBundleConfiguration aConfigurationWithDefaultCategory(String defaultCategory) {
        return aConfiguration(null, defaultCategory);
    }

    private static PingdomBundleConfiguration aConfiguration(String authKey, String defaultCategory) {
        PingdomBundleConfiguration config = new PingdomBundleConfiguration();
        config.setKey(authKey);
        config.setDefaultCategory(defaultCategory);
        return config;
    }

}
