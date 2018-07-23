package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

public class GracefulShutdownIT extends AbstractIT {
    @Override
    protected void configure(Properties properties) {
        properties.setProperty("graceful.shutdown.enabled", "true");
        properties.setProperty("graceful.shutdown.timeout", "5s");
        properties.setProperty("graceful.shutdown.wait", "0s");
    }

    @Test
    public void inFlightRequestSuccessful() throws ExecutionException, InterruptedException {
        ListenableFuture<Response> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        REQ_FINISHED.release();

        assertThat(response.get().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    @Test
    public void inFlightRequestFailsAfterTimeout() throws InterruptedException {
        ListenableFuture<Response> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        assertThatCode(response::get).hasCauseInstanceOf(IOException.class);
    }
}
