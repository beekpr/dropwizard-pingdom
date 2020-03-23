package io.beekeeper.pingdom;

import io.beekeeper.pingdom.resources.PingdomHealthResource;
import io.dropwizard.Configuration;
import io.dropwizard.ConfiguredBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;

import java.util.function.Function;

public class PingdomBundle<T extends Configuration> implements ConfiguredBundle<T> {

    private final Function<T, PingdomBundleConfiguration> configProvider;

    public PingdomBundle(Function<T, PingdomBundleConfiguration> configProvider) {
        this.configProvider = configProvider;
    }

    public PingdomBundle() {
        this(null);
    }

    @Override
    public void initialize(Bootstrap<?> bootstrap) {
    }

    @Override
    public void run(T configuration, Environment environment) throws Exception {
        PingdomBundleConfiguration bundleConfig;

        if (configProvider == null) {
            bundleConfig = getPingdomBundleConfiguration();
        } else {
            bundleConfig = configProvider.apply(configuration);
        }

        environment.jersey().register(new PingdomHealthResource(bundleConfig, environment));
    }

    public PingdomBundleConfiguration getPingdomBundleConfiguration() {
        return new PingdomBundleConfiguration();
    }
}
