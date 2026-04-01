package in.bawvpl.Authify.filter;

import in.bawvpl.Authify.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtRequestFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;

    // ✅ Public APIs (no auth needed)
    private static final String[] PUBLIC_PATHS = {
            "/", "/error",

            "/api/v1.0/register",
            "/api/v1.0/login",
            "/api/v1.0/login/verify-otp",
            "/api/v1.0/send-otp",
            "/api/v1.0/send-reset-otp",
            "/api/v1.0/reset-password",

            "/swagger-ui",
            "/swagger-ui/**",
            "/v3/api-docs",
            "/v3/api-docs/**"
    };

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {

        String path = request.getRequestURI();

        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        for (String publicPath : PUBLIC_PATHS) {
            if (path.startsWith(publicPath)) {
                return true;
            }
        }

        return false;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        log.info("===== JWT FILTER START =====");

        final String authHeader = request.getHeader("Authorization");
        log.info("Authorization Header: {}", authHeader);

        // ❌ No token → continue
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("No Bearer token found");
            filterChain.doFilter(request, response);
            return;
        }

        String jwt = authHeader.substring(7);
        log.info("JWT Token: {}", jwt);

        String username;

        try {
            username = jwtUtil.extractUsername(jwt);
            log.info("Extracted Username: {}", username);
        } catch (Exception e) {
            log.error("Invalid JWT token", e);
            filterChain.doFilter(request, response);
            return;
        }

        // ✅ Authenticate only if not already authenticated
        if (username != null &&
                SecurityContextHolder.getContext().getAuthentication() == null) {

            try {
                UserDetails userDetails =
                        userDetailsService.loadUserByUsername(username);

                log.info("User loaded from DB: {}", userDetails.getUsername());

                if (jwtUtil.validateToken(jwt, userDetails.getUsername())) {

                    UsernamePasswordAuthenticationToken auth =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    auth.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(auth);

                    log.info("✅ Authentication set successfully");

                } else {
                    log.error("❌ Token validation failed");
                }

            } catch (Exception e) {
                log.error("User not found or authentication error", e);
            }
        }

        // ✅ Continue request
        filterChain.doFilter(request, response);
    }
}