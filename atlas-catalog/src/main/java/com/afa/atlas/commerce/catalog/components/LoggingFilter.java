package com.afa.atlas.commerce.catalog.components;

import com.afa.atlas.commerce.catalog.controllers.internal.ControllerConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Slf4j
@Component
public class LoggingFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID = "correlationId";
    private static final String CORRELATION_HEADER = "X-Correlation-Id";

    @Override
    protected boolean shouldNotFilter(final HttpServletRequest request) {
        final String uri = request.getRequestURI();

        return uri.startsWith(ControllerConstants.ACTUATOR)
                || uri.startsWith(ControllerConstants.API_DOCS)
                || uri.startsWith(ControllerConstants.SWAGGER);
    }

    @Override
    protected void doFilterInternal(
            final HttpServletRequest request,
            final HttpServletResponse response,
            final FilterChain filterChain
    ) throws ServletException, IOException {

        final long startTime = System.currentTimeMillis();

        final String correlationId = resolveCorrelationId(request);
        MDC.put(CORRELATION_ID, correlationId);
        response.setHeader(CORRELATION_HEADER, correlationId);

        try {
            log.info("Request: method={}, uri={}, query={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    request.getQueryString());

            filterChain.doFilter(request, response);

        } finally {
            log.info("Response: method={}, uri={}, status={}, durationMs={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    response.getStatus(),
                    System.currentTimeMillis() - startTime);

            MDC.remove(CORRELATION_ID);
        }
    }

    private String resolveCorrelationId(final HttpServletRequest request) {
        final String headerValue = request.getHeader(CORRELATION_HEADER);

        if (headerValue == null || headerValue.isBlank()) {
            return UUID.randomUUID().toString();
        }

        return headerValue;
    }
}