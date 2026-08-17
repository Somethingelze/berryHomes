package net.berryhomes.filter;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import io.github.bucket4j.Refill;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class RateLimitingFilter implements Filter {

    private static final Set<String> CONTACT_ENDPOINTS = Set.of("/tenants/message", "/homeowners/message", "/investors/message", "/contact/message", "/contact/message/modal");
    private static final long ENTRY_TTL_MILLIS = Duration.ofHours(1).toMillis();
    private static final int MAX_CACHE_ENTRIES = 10_000;
    private final Map<String, ClientBucket> cache = new ConcurrentHashMap<>();

    private Bucket createNewBucket() {
        Refill refill = Refill.intervally(1, Duration.ofSeconds(20));
        Bandwidth limit = Bandwidth.classic(3, refill);
        return Bucket.builder().addLimit(limit).build();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if ("POST".equalsIgnoreCase(httpRequest.getMethod()) && CONTACT_ENDPOINTS.contains(httpRequest.getRequestURI().substring(httpRequest.getContextPath().length()))) {
            
            String ip = getClientIP(httpRequest);
            long now = System.currentTimeMillis();
            if (cache.size() >= MAX_CACHE_ENTRIES) {
                cache.entrySet().removeIf(entry -> now - entry.getValue().lastSeenMillis > ENTRY_TTL_MILLIS);
                if (cache.size() >= MAX_CACHE_ENTRIES && !cache.containsKey(ip)) {
                    httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                    return;
                }
            }
            ClientBucket clientBucket = cache.compute(ip, (key, current) ->
                    current == null || now - current.lastSeenMillis > ENTRY_TTL_MILLIS
                            ? new ClientBucket(createNewBucket(), now)
                            : new ClientBucket(current.bucket, now));
            Bucket bucket = clientBucket.bucket;

            if (!bucket.tryConsume(1)) {
                log.warn("[Rate Limit] Превышен лимит запросов для IP: {} на URL: {}", ip, httpRequest.getRequestURI());
                
                httpResponse.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                httpResponse.setContentType("application/json;charset=UTF-8");
                httpResponse.getWriter().write("{\"error\": \"Too many requests. Please wait a moment before submitting another form.\"}");
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private String getClientIP(HttpServletRequest request) {
        return request.getRemoteAddr();
    }
    private record ClientBucket(Bucket bucket, long lastSeenMillis) {}
}
