package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NonGracefulShutdownIntegrationTest extends AbstractIntegrationTest {
    @Override
    protected void configure(Properties properties) {
    }

    @Test
    public void inFlightRequestFails() throws InterruptedException {
        ListenableFuture<ResponseEntity<HttpStatus>> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        assertThatThrownBy(response::get).hasCauseInstanceOf(IOException.class);
    }
}
