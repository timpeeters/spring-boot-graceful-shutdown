package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(havingValue = "true", prefix = "graceful.shutdown", name = "enabled")
@ConditionalOnWebApplication
@Configuration
@EnableConfigurationProperties(GracefulShutdownProperties.class)
public class GracefulShutdownAutoConfiguration {
    @Bean
    public HealthIndicator gracefulShutdownHealthIndicator(
            ApplicationContext ctx, GracefulShutdownProperties props) {

        return new GracefulShutdownHealthIndicator(ctx, props);
    }

    @Bean
    public WebServerFactoryCustomizer<TomcatServletWebServerFactory> gracefulShutdownTomcatContainerCustomizer(
            ApplicationContext ctx, GracefulShutdownProperties props) {

        return container -> container.addConnectorCustomizers(gracefulShutdownTomcatConnectorCustomizer(ctx, props));
    }

    @Bean
    public TomcatConnectorCustomizer gracefulShutdownTomcatConnectorCustomizer(
            ApplicationContext ctx, GracefulShutdownProperties props) {

        return new GracefulShutdownTomcatConnectorCustomizer(ctx, props);
    }
}
