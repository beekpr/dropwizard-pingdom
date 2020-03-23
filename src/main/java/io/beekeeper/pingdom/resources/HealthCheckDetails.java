package io.beekeeper.pingdom.resources;

public class HealthCheckDetails {
    public static class Severity {
        public static final String KEY = Severity.class.getName() + ".severity";
        public static final String VALUE_HIGH = "high";
        public static final String VALUE_LOW = "low";
        public static final String[] VALUES = { VALUE_HIGH, VALUE_LOW };
    }

    public static class Category {
        public static final String KEY = Category.class.getName() + ".category";
    }
}
