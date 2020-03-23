package io.beekeeper.pingdom;

import io.beekeeper.pingdom.resources.PingdomHealthResource;
import io.dropwizard.testing.junit.DropwizardClientRule;
import org.assertj.core.api.Assertions;
import org.junit.ClassRule;
import org.junit.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class PingdomHealthFullResourceTest {

    @ClassRule
    public static final DropwizardClientRule dropwizard =
        new DropwizardClientRule(
                new PingdomHealthResource(
                        PingdomHealthResourceTest.aConfiguration(),
                        PingdomHealthResourceTest.env()
                )
        );

    @Test
    public void shouldPing() throws IOException {
        final URL url = new URL(dropwizard.baseUri() + "/health/pingdom");
        final String response = new BufferedReader(new InputStreamReader(url.openStream())).readLine();

        final String expectedResponse = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>"
            +
            "<pingdom_http_custom_check><status>OK</status><response_time>0</response_time>"
            +
            "</pingdom_http_custom_check>";

        Assertions.assertThat(response).isXmlEqualTo(expectedResponse);
    }
}
