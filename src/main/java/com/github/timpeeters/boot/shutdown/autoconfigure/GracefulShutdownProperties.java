package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("graceful.shutdown")
public class GracefulShutdownProperties {
    /**
     * Indicates whether graceful shutdown is enabled or not.
     */
    private boolean enabled;

    /**
     * The number of seconds to wait for active threads to finish before shutting down the embedded web container.
     */
    private int timeout = 60;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }
}
