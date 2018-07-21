package com.github.timpeeters.boot.shutdown.autoconfigure;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.ProtocolHandler;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.core.task.SyncTaskExecutor;

import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GracefulShutdownTomcatConnectorCustomizerTest {

    @Mock
    private ApplicationContext applicationContext;

    @InjectMocks
    private GracefulShutdownTomcatConnectorCustomizer customizer;

    @Test
    public void skipGracefulShutdownIfConnectorIsNull() {
        assertThatCode(() -> customizer.contextClosed(new ContextClosedEvent(applicationContext)))
                .doesNotThrowAnyException();
    }

    @Test
    public void differentThreadPoolImplementation() {
        Connector mockConnector = configureConnectorToReturnDifferentThreadPoolImplementation();

        customizer.customize(mockConnector);

        assertThatCode(() -> customizer.contextClosed(new ContextClosedEvent(applicationContext)))
                .doesNotThrowAnyException();
    }

    private Connector configureConnectorToReturnDifferentThreadPoolImplementation() {
        Connector mockConnector = mock(Connector.class);
        ProtocolHandler mockProtocolHandler = mock(ProtocolHandler.class);

        when(mockConnector.getProtocolHandler()).thenReturn(mockProtocolHandler);
        when(mockProtocolHandler.getExecutor()).thenReturn(new SyncTaskExecutor());

        return mockConnector;
    }
}
