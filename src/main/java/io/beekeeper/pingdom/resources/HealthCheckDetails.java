package io.beekeeper.pingdom.resources;

public class HealthCheckDetails {
    public static class Severity {
        private static final String KEY = Severity.class.getName() + ".severity";
        private static final String VALUE_HIGH = "high";
        private static final String VALUE_LOW = "low";
        private static final String[] VALUES = { VALUE_HIGH, VALUE_LOW };

        public static String getKey() {
            return KEY;
        }

        public static String[] getValues() {
            return VALUES.clone();
        }

        public static String getValueHigh() {
            return VALUE_HIGH;
        }

        public static String getValueLow() {
            return VALUE_LOW;
        }
    }

    public static class Category {
        private static final String KEY = Category.class.getName() + ".category";

        public static String getKey() {
            return KEY;
        }
    }
}
