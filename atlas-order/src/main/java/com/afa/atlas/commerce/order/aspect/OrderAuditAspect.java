package com.afa.atlas.commerce.order.aspect;

import com.afa.atlas.commerce.common.annotation.OrderAudit;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
@SuppressWarnings("PMD.AvoidCatchingThrowable")
public class OrderAuditAspect {

    @Around("@annotation(orderAudit)")
    public Object audit(
            final ProceedingJoinPoint joinPoint,
            final OrderAudit orderAudit) throws Throwable {

        final long started = System.currentTimeMillis();

        try {
            final Object result = joinPoint.proceed();

            log.info("Order operation={} completed in {} ms",
                    orderAudit.operation(),
                    System.currentTimeMillis() - started);

            return result;
        } catch (Throwable ex) {
            log.error("Order operation={} failed after {} ms",
                    orderAudit.operation(), System.currentTimeMillis() - started, ex);
            throw ex;
        }
    }
}
