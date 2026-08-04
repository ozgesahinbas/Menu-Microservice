package io.ozgesahinbas.restaurant.menu.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * Cross-cutting logging for the service: request/response tracing on the
 * controller layer, execution time on the service layer, and a single place
 * where every failure of either layer is reported.
 *
 * <p>The pointcuts are declared as constants rather than annotated methods so
 * the class carries no empty method bodies.
 */
@Slf4j
@Aspect
@Component
public class LoggingAspect {

    private static final String CONTROLLER_LAYER =
            "within(io.ozgesahinbas.restaurant.menu.controller..*)";

    private static final String SERVICE_LAYER =
            "within(io.ozgesahinbas.restaurant.menu.service..*)";

    @Around(CONTROLLER_LAYER)
    public Object logRequestAndResponse(ProceedingJoinPoint joinPoint) throws Throwable {
        log.info("--> {} arguments={}", name(joinPoint), Arrays.toString(joinPoint.getArgs()));

        Object response = joinPoint.proceed();

        log.info("<-- {} response={}", name(joinPoint), response);

        return response;
    }

    @Around(SERVICE_LAYER)
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startedAt = System.nanoTime();
        try {
            return joinPoint.proceed();
        } finally {
            long elapsedMillis = (System.nanoTime() - startedAt) / 1_000_000;
            log.info("{} executed in {} ms", name(joinPoint), elapsedMillis);
        }
    }

    @AfterThrowing(pointcut = CONTROLLER_LAYER + " || " + SERVICE_LAYER, throwing = "exception")
    public void logException(JoinPoint joinPoint, Throwable exception) {
        log.error("{} failed with {}: {}",
                name(joinPoint),
                exception.getClass().getSimpleName(),
                exception.getMessage());
    }

    private String name(JoinPoint joinPoint) {
        return joinPoint.getSignature().toShortString();
    }
}
