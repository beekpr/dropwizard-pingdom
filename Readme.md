# Dropwizard Pingdom plugin

A simple plugin to expose a dropwizard health check on the following path "/health/pingdom".

This plugin is useful because:
* By default Dropwizard puts the healthchecks on a different admin port. In production deployments, the admin rest interface should usually not be exposed to the world. Instead we just want to have a simple healthcheck
* The default healthcheck response is not pingdom compatible. This plugin returns the valid pingdom XML

# How to use:

## Include the dropwizard-pingdom plugin as a dependency in your project.

### Maven
```
<dependency>
  <groupId>io.beekeeper</groupId>
  <artifactId>dropwizard-pingdom</artifactId>
  <version>1.0.0</version>
  <type>pom</type>
</dependency>
```

### Gradle
```
compile 'io.beekeeper:dropwizard-pingdom:1.0.0'
```

## Add the PingdomBundle to your bootstrap

```java
bootstrap.addBundle(new PingdomBundle<QRApplicationConfiguration>());
```


Thats it!

## Customization
By Default the endpoint will be world visible on "/health/pingdom". If you would like to have some protection, you can add a key, so that only requests which provide the key over a query parameter return data. E.g.

```java
bootstrap.addBundle(new PingdomBundle<QRApplicationConfiguration>() {
    @Override
    public PingdomBundleConfiguration getPingdomBundleConfiguration() {
        PingdomBundleConfiguration configuration = new PingdomBundleConfiguration();
        configuration.setKey("MySpecialKey");
        return configuration;
    }
});
```

Now, only requests with the MySpecialKey return the healthcheck report. E.g. "health/pingdom?key=MySpecialKey"