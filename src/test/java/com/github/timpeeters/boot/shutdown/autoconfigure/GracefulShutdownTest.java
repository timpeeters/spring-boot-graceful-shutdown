package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GracefulShutdownTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    public void ok() {
        RequestEntity request = new RequestEntity(HttpMethod.GET, URI.create("/"));

        assertThat(restTemplate.exchange(request, String.class).getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @SpringBootApplication
    public static class TestApplication {
        @RestController
        public class TestController {
            @GetMapping("/")
            public HttpStatus get() {
                return HttpStatus.OK;
            }
        }

        public static void main(String[] args) {
            SpringApplication.run(TestApplication.class);
        }
    }
}
