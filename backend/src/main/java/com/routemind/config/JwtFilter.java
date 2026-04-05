package com.routemind.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts user_id from JWT Bearer token and injects it as a cookie
 * so existing @CookieValue annotations continue working.
 * Also supports legacy cookie-based auth for backward compatibility.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);
            if (jwtUtil.isValid(token)) {
                Long userId = jwtUtil.getUserId(token);
                // Wrap request to inject user_id cookie so @CookieValue still works
                HttpServletRequest wrapped = new UserIdCookieWrapper(request, userId);
                chain.doFilter(wrapped, response);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    private static class UserIdCookieWrapper extends HttpServletRequestWrapper {
        private final Cookie[] cookies;

        public UserIdCookieWrapper(HttpServletRequest request, Long userId) {
            super(request);
            Cookie jwtCookie = new Cookie("user_id", userId.toString());
            Cookie[] existing = request.getCookies();
            if (existing != null) {
                cookies = new Cookie[existing.length + 1];
                System.arraycopy(existing, 0, cookies, 0, existing.length);
                cookies[existing.length] = jwtCookie;
            } else {
                cookies = new Cookie[]{jwtCookie};
            }
        }

        @Override
        public Cookie[] getCookies() {
            return cookies;
        }
    }
}
