package io.ozgesahinbas.restaurant.menu.aspect;

import io.ozgesahinbas.restaurant.menu.exception.MenuNotFoundException;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LoggingAspectTest {

    @Mock
    private ProceedingJoinPoint joinPoint;

    @Mock
    private Signature signature;

    private final LoggingAspect loggingAspect = new LoggingAspect();

    @Test
    void shouldLogControllerCallAndReturnResponseUnchanged() throws Throwable {
        when(signature.toShortString()).thenReturn("MenuController.getMenuById(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"menu::1"});
        when(joinPoint.proceed()).thenReturn("menu-response");

        Object response = loggingAspect.logRequestAndResponse(joinPoint);

        assertThat(response).isEqualTo("menu-response");
        verify(joinPoint).proceed();
    }

    @Test
    void shouldPropagateControllerFailure() throws Throwable {
        when(signature.toShortString()).thenReturn("MenuController.getMenuById(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.getArgs()).thenReturn(new Object[]{"menu::404"});
        when(joinPoint.proceed()).thenThrow(new MenuNotFoundException("menu::404"));

        assertThatThrownBy(() -> loggingAspect.logRequestAndResponse(joinPoint))
                .isInstanceOf(MenuNotFoundException.class);
    }

    @Test
    void shouldMeasureServiceExecutionAndReturnResult() throws Throwable {
        when(signature.toShortString()).thenReturn("MenuServiceImpl.getAllMenus()");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenReturn("service-result");

        assertThat(loggingAspect.logExecutionTime(joinPoint)).isEqualTo("service-result");
    }

    @Test
    void shouldStillMeasureExecutionWhenServiceFails() throws Throwable {
        when(signature.toShortString()).thenReturn("MenuServiceImpl.getMenuById(..)");
        when(joinPoint.getSignature()).thenReturn(signature);
        when(joinPoint.proceed()).thenThrow(new MenuNotFoundException("menu::404"));

        assertThatThrownBy(() -> loggingAspect.logExecutionTime(joinPoint))
                .isInstanceOf(MenuNotFoundException.class);

        verify(joinPoint).getSignature();
    }

    @Test
    void shouldLogThrownException() {
        when(signature.toShortString()).thenReturn("MenuServiceImpl.getMenuById(..)");
        when(joinPoint.getSignature()).thenReturn(signature);

        loggingAspect.logException(joinPoint, new MenuNotFoundException("menu::404"));

        verify(joinPoint).getSignature();
    }
}
