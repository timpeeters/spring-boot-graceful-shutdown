package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class GracefulShutdownWithWaitIT extends AbstractIT {
    @Override
    protected void configure(Properties properties) {
        properties.setProperty("graceful.shutdown.enabled", "true");
        properties.setProperty("graceful.shutdown.timeout", "0");
        properties.setProperty("graceful.shutdown.wait", "5");
    }

    @Test
    public void test() throws ExecutionException, InterruptedException {
        stopSpringBootApp();

        Thread.sleep(2000);

        assertThat(sendRequest("/health").get().getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
    }
}
