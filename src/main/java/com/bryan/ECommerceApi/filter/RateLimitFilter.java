package com.bryan.ECommerceApi.filter;

import com.bryan.ECommerceApi.model.payload.ApiResponse;
import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.ConsumptionProbe;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

    private static final String AUTH_PATTERN = "auth";
    private static final String PRODUCTS_GET_PATTERN = "products";

    private static final int AUTH_LIMIT = 5;
    private static final int PRODUCTS_GET_LIMIT = 15;
    private static final int PROTECTED_LIMIT = 30;

    private final Map<String, Bucket> buckets = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper;

    public RateLimitFilter() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String path = request.getRequestURI();
        String method = request.getMethod();
        String bucketKey = resolveBucketKey(request, path, method);

        Bucket bucket = buckets.computeIfAbsent(bucketKey, this::createBucket);
        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);

        if (probe.isConsumed()) {
            response.addHeader("X-RateLimit-Remaining", String.valueOf(probe.getRemainingTokens()));
            filterChain.doFilter(request, response);
        } else {
            writeRateLimitResponse(response, request, probe);
        }
    }

    private String resolveBucketKey(HttpServletRequest request, String path, String method) {
        if (isAuthEndpoint(path)) {
            return getClientIpAddress(request) + "_auth";
        }

        if (isProductsGetEndpoint(path, method)) {
            return getClientIpAddress(request) + "_products";
        }

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.isAuthenticated() && !"anonymousUser".equals(authentication.getPrincipal())) {
            return authentication.getName() + "_protected";
        }
        return getClientIpAddress(request) + "_protected";
    }

    private boolean isAuthEndpoint(String path) {
        return path.contains(AUTH_PATTERN);
    }

    private boolean isProductsGetEndpoint(String path, String method) {
        return "GET".equalsIgnoreCase(method) && path.contains(PRODUCTS_GET_PATTERN);
    }

    private Bucket createBucket(String key) {
        int limit;
        if (key.endsWith("_auth")) {
            limit = AUTH_LIMIT;
        } else if (key.endsWith("_products")) {
            limit = PRODUCTS_GET_LIMIT;
        } else {
            limit = PROTECTED_LIMIT;
        }

        Bandwidth limitBandwidth = Bandwidth.builder()
                .capacity(limit)
                .refillGreedy(limit, Duration.ofMinutes(1))
                .build();

        return Bucket.builder().addLimit(limitBandwidth).build();
    }

    private void writeRateLimitResponse(HttpServletResponse response, HttpServletRequest request, ConsumptionProbe probe) throws IOException {
        long waitTimeSeconds = probe.getNanosToWaitForRefill() / 1_000_000_000;

        response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.addHeader("Retry-After", String.valueOf(waitTimeSeconds));
        response.addHeader("X-RateLimit-Remaining", "0");

        ApiResponse apiResponse = new ApiResponse(
                request.getRequestURI(),
                "Too many requests. Please retry after " + waitTimeSeconds + " seconds"
        );

        response.getWriter().write(objectMapper.writeValueAsString(apiResponse));
    }

    private String getClientIpAddress(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }

        String xRealIp = request.getHeader("X-Real-IP");
        if (xRealIp != null && !xRealIp.isEmpty()) {
            return xRealIp;
        }

        return request.getRemoteAddr();
    }
}
