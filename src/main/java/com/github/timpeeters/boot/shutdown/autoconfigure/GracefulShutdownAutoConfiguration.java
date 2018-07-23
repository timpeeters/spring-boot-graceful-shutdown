package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(havingValue = "true", prefix = "graceful.shutdown", name = "enabled")
@ConditionalOnWebApplication
@Configuration
@EnableConfigurationProperties(GracefulShutdownProperties.class)
public class GracefulShutdownAutoConfiguration {
    @Bean
    @ConditionalOnMissingBean
    public GracefulShutdownHealthIndicator gracefulShutdownHealthIndicator(
            ApplicationContext ctx, GracefulShutdownProperties props) {

        return new GracefulShutdownHealthIndicator(ctx, props);
    }

    @Bean
    @ConditionalOnMissingBean
    public GracefulShutdownTomcatContainerCustomizer gracefulShutdownTomcatContainerCustomizer(
            GracefulShutdownTomcatConnectorCustomizer connectorCustomizer) {

        return new GracefulShutdownTomcatContainerCustomizer(connectorCustomizer);
    }

    @Bean
    @ConditionalOnMissingBean
    public GracefulShutdownTomcatConnectorCustomizer gracefulShutdownTomcatConnectorCustomizer(
            ApplicationContext ctx, GracefulShutdownProperties props) {

        return new GracefulShutdownTomcatConnectorCustomizer(ctx, props);
    }
}
