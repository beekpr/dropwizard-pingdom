package io.beekeeper.pingdom;

import io.beekeeper.pingdom.resources.PingdomHealthResource;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

public class PingdomBundle<T extends Configuration> implements ConfiguredBundle<T> {


    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        environment.jersey().register(new PingdomHealthResource(getPingdomBundleConfiguration(), environment));
    }

    public PingdomBundleConfiguration getPingdomBundleConfiguration() {
        return new PingdomBundleConfiguration();
    }
}
