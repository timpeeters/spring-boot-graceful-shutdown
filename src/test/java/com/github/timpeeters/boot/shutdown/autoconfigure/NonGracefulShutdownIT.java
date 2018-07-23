package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.assertj.core.api.Assertions.assertThatCode;

public class NonGracefulShutdownIT extends AbstractIT {
    @Override
    protected void configure(Properties properties) {
    }

    @Test
    public void inFlightRequestFails() throws InterruptedException {
        ListenableFuture<Response> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        assertThatCode(response::get).hasCauseInstanceOf(IOException.class);
    }
}
