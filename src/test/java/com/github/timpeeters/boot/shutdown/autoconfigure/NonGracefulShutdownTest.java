package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SocketUtils;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.AsyncRestTemplate;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

public class NonGracefulShutdownTest {
    private static final Logger LOG = LoggerFactory.getLogger(NonGracefulShutdownTest.class);

    private static final Semaphore REQ_RECEIVED = new Semaphore(0);
    private static final Semaphore REQ_FINISHED = new Semaphore(0);

    private final int port = SocketUtils.findAvailableTcpPort();

    private ConfigurableApplicationContext applicationContext;

    @Before
    public void startSpringBootApplication() {
        applicationContext = SpringApplication.run(TestApplication.class, "--server.port=" + port);
    }

    @Test
    public void inFlightRequestFails() throws ExecutionException, InterruptedException {
        emulateSuccessfulRequest();
        ListenableFuture<ResponseEntity<HttpStatus>> response = sendRequestAndWaitForServerToStartProcessing();

        stopSpringBootApp();

        assertThatThrownBy(response::get).hasCauseInstanceOf(IOException.class);
    }

    private void emulateSuccessfulRequest() throws ExecutionException, InterruptedException {
        ListenableFuture<ResponseEntity<HttpStatus>> slowRequest = sendRequestAndWaitForServerToStartProcessing();

        REQ_FINISHED.release();

        assertThat(slowRequest.get().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
    }

    private ListenableFuture<ResponseEntity<HttpStatus>> sendRequestAndWaitForServerToStartProcessing() throws InterruptedException {
        ListenableFuture<ResponseEntity<HttpStatus>> response =
                new AsyncRestTemplate().getForEntity("http://localhost:" + port, HttpStatus.class);

        REQ_RECEIVED.acquire();

        return response;
    }

    private void stopSpringBootApp() {
        applicationContext.close();
    }

    @SpringBootApplication
    public static class TestApplication {
        @RestController
        public class TestController {
            @GetMapping("/")
            public ResponseEntity<?> get() throws InterruptedException {
                REQ_RECEIVED.release();

                LOG.debug("Received get request, try to acquire semaphore");

                REQ_FINISHED.acquire();

                LOG.debug("Semaphore acquired, returning empty response");

                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
        }

        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class);
        }
    }
}
