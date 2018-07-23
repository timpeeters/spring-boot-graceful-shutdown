package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.asynchttpclient.ListenableFuture;
import org.asynchttpclient.Response;
import org.junit.After;
import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.SocketUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Properties;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.asynchttpclient.Dsl.asyncHttpClient;
import static org.asynchttpclient.Dsl.get;

public abstract class AbstractIT {
    protected static final Logger LOG = LoggerFactory.getLogger(NonGracefulShutdownIT.class);

    protected static final Semaphore REQ_RECEIVED = new Semaphore(0);
    protected static final Semaphore REQ_FINISHED = new Semaphore(0);

    private final int port = SocketUtils.findAvailableTcpPort();

    private ConfigurableApplicationContext applicationContext;
    private CompletableFuture<Void> shutdown;

    @Before
    public void startSpringBootApplication() throws ExecutionException, InterruptedException {
        applicationContext = SpringApplication.run(TestApplication.class, getArgs());

        emulateSuccessfulRequest();
    }

    @After
    public void verifySpringBootApplicationShutdownComplete() {
        assertThatCode(() -> shutdown.get(30, TimeUnit.SECONDS)).doesNotThrowAnyException();
    }

    protected String[] getArgs() {
        return getProperties().entrySet().stream()
                .map(e -> "--" + e.getKey() + "=" + e.getValue())
                .toArray(String[]::new);

    }

    protected final Properties getProperties() {
        Properties props = new Properties();
        props.setProperty("server.port", Integer.toString(port));
        props.setProperty("spring.main.banner-mode", "off");

        configure(props);

        return props;
    }

    protected abstract void configure(Properties properties);

    protected void emulateSuccessfulRequest() throws ExecutionException, InterruptedException {
        ListenableFuture<Response> slowRequest = sendRequestAndWaitForServerToStartProcessing();

        REQ_FINISHED.release();

        assertThat(slowRequest.get().getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    protected ListenableFuture<Response> sendRequestAndWaitForServerToStartProcessing()
            throws InterruptedException {

        ListenableFuture<Response> response = sendRequest("/");

        REQ_RECEIVED.acquire();

        return response;
    }

    protected ListenableFuture<Response> sendRequest(String path) {
        return asyncHttpClient().executeRequest(get("http://localhost:" + port + path));
    }

    protected void stopSpringBootApp() {
        shutdown = CompletableFuture.runAsync(() -> applicationContext.close());
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
