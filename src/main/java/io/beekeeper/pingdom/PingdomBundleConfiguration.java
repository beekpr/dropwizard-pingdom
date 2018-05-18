package io.beekeeper.pingdom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PingdomBundleConfiguration {
    /**
     * The Key to be used to authorize a request. By default this is
     * <code>null</code>
     * <p>
     * If <code>null</code> is provided, there is no check performed and anyone
     * can check on the status
     */
    private String key = null;

    /**
     * The name of the health check category considered as default.
     * If this category is queried, all health checks with no category are also
     * included in the result.
     * <p>
     * If <code>null</code> is provided, health checks with no category are only
     * displayed if no category is queried for.
     */
    private String defaultCategory = null;

    @JsonProperty
    public String getKey() {
        return key;
    }

    @JsonProperty
    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty
    public String getDefaultCategory() {
        return defaultCategory;
    }

    @JsonProperty
    public void setDefaultCategory(String defaultCategory) {
        this.defaultCategory = defaultCategory;
    }
}
