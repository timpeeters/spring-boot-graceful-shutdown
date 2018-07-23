package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;

public class GracefulShutdownTomcatContainerCustomizer
        implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

    private final GracefulShutdownTomcatConnectorCustomizer connectorCustomizer;

    public GracefulShutdownTomcatContainerCustomizer(GracefulShutdownTomcatConnectorCustomizer connectorCustomizer) {
        this.connectorCustomizer = connectorCustomizer;
    }

    @Override
    public void customize(TomcatServletWebServerFactory factory) {
        factory.addConnectorCustomizers(connectorCustomizer);
    }
}
