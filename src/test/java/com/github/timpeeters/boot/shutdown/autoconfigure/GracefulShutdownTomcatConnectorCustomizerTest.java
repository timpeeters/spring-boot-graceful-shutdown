package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThatCode;

public class GracefulShutdownTomcatConnectorCustomizerTest {
    private final GracefulShutdownTomcatConnectorCustomizer customizer =
            new GracefulShutdownTomcatConnectorCustomizer(null, null);

    @Test
    public void skipGracefulShutdownIfConnectorIsNull() {
        assertThatCode(() -> customizer.contextClosed(null)).doesNotThrowAnyException();
    }
}
