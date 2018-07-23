package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

public class GracefulShutdownHealthIndicator implements HealthIndicator {
    private static final Logger LOG = LoggerFactory.getLogger(GracefulShutdownHealthIndicator.class);

    private final ApplicationContext applicationContext;
    private final GracefulShutdownProperties props;

    private Health health = Health.up().build();

    public GracefulShutdownHealthIndicator(ApplicationContext ctx, GracefulShutdownProperties props) {
        this.applicationContext = ctx;
        this.props = props;
    }

    @Override
    public Health health() {
        return health;
    }

    @EventListener(ContextClosedEvent.class)
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public void contextClosed(ContextClosedEvent event) throws InterruptedException {
        if (isEventFromLocalContext(event)) {
            updateHealthToOutOfService();
            waitForKubernetesToSeeOutOfService();
        }
    }

    private void updateHealthToOutOfService() {
        health = Health.outOfService().build();

        LOG.info("Health status set to out of service");
    }

    private void waitForKubernetesToSeeOutOfService() throws InterruptedException {
        LOG.info("Wait {} seconds for Kubernetes to see the out of service status", props.getWait().getSeconds());

        Thread.sleep(props.getWait().toMillis());
    }

    private boolean isEventFromLocalContext(ContextClosedEvent event) {
        return event.getApplicationContext().equals(applicationContext);
    }
}
