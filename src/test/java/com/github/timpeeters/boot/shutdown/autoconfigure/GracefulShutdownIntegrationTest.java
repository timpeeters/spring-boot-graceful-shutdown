package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;

public class GracefulShutdownIntegrationTest extends AbstractIntegrationTest {
    @Override
    protected void configure(Properties properties) {
        properties.setProperty("graceful.shutdown.enabled", "true");
        properties.setProperty("graceful.shutdown.timeout", "10");
    }

    @Test
    public void inFlightRequestSuccessful() throws ExecutionException, InterruptedException {
        ListenableFuture<ResponseEntity<HttpStatus>> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        REQ_FINISHED.release();

        assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }
}
