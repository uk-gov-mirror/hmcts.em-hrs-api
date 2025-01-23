package uk.gov.hmcts.reform.em.hrs.config.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class UserJwtAuthenticationFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserJwtAuthenticationFilter.class);

    private final JwtDecoder jwtDecoder;

    public UserJwtAuthenticationFilter(JwtDecoder jwtDecoder) {
        this.jwtDecoder = jwtDecoder;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String token = authHeader.substring(7);

            try {
                Jwt jwt = jwtDecoder.decode(token);

                String userId = jwt.getClaimAsString("sub");
                String email = jwt.getClaimAsString("email");


                LOGGER.info("UserJwtAuthenticationFilter userId:{},email:{}", userId, email);
            } catch (Exception e) {
                // Handle JWT decoding/validation errors if necessary
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }

        // Proceed with the filter chain
        filterChain.doFilter(request, response);
    }


}
