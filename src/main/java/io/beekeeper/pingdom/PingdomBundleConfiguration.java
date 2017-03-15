package io.beekeeper.pingdom;

import com.fasterxml.jackson.annotation.JsonProperty;

public class PingdomBundleConfiguration {
    /**
     * The Key to be used to authorize a request. By default this is
     * <code>null</code>
     *
     * If <code>null</code> is provided, there is no check performed and anyone
     * can check on the status
     */
    private String key = null;

    @JsonProperty
    public String getKey() {
        return key;
    }

    @JsonProperty
    public void setKey(String key) {
        this.key = key;
    }
}
