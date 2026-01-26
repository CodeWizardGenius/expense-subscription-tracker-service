package org.siliconmade.expenstracker.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestLoggingFilter extends OncePerRequestFilter {
    private static final Logger log = LoggerFactory.getLogger(RequestLoggingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String auth = request.getHeader("Authorization");
        String userAgent = request.getHeader("User-Agent");
        String remoteAddr = request.getRemoteAddr();
        if (auth != null) {
            log.info("Incoming request {} {} from {} - Authorization header present ({} chars, first 10): {}... | User-Agent: {}",
                    request.getMethod(), request.getRequestURI(), remoteAddr, auth.length(), auth.substring(0, Math.min(10, auth.length())), userAgent);
            // JWT header kısmını logla (isteğe bağlı, güvenlik için sadece ilk kısmı)
            if (auth.startsWith("Bearer ")) {
                String[] jwtParts = auth.substring(7).split("\\.");
                if (jwtParts.length == 3) {
                    try {
                        String headerJson = new String(java.util.Base64.getUrlDecoder().decode(jwtParts[0]));
                        log.info("JWT header: {}", headerJson);
                        String payloadJson = new String(java.util.Base64.getUrlDecoder().decode(jwtParts[1]));
                        log.info("JWT payload: {}", payloadJson);
                    } catch (Exception e) {
                        log.warn("JWT decode failed: {}", e.getMessage());
                    }
                } else {
                    log.warn("Authorization header looks like Bearer but JWT parça sayısı 3 değil! ({} parça)", jwtParts.length);
                }
            }
        } else {
            log.info("Incoming request {} {} from {} - No Authorization header | User-Agent: {}", request.getMethod(), request.getRequestURI(), remoteAddr, userAgent);
        }
        filterChain.doFilter(request, response);
    }
}
