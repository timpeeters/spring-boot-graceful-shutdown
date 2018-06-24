package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class GracefulShutdownIT extends AbstractIT {
    @Override
    protected void configure(Properties properties) {
        properties.setProperty("graceful.shutdown.enabled", "true");
        properties.setProperty("graceful.shutdown.timeout", "5");
        properties.setProperty("graceful.shutdown.wait", "0");
    }

    @Test
    public void inFlightRequestSuccessful() throws ExecutionException, InterruptedException {
        ListenableFuture<ResponseEntity<String>> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        REQ_FINISHED.release();

        assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    @Test
    public void inFlightRequestFailsAfterTimeout() throws InterruptedException {
        ListenableFuture<ResponseEntity<String>> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        assertThatCode(response::get).hasCauseInstanceOf(IOException.class);
    }
}
