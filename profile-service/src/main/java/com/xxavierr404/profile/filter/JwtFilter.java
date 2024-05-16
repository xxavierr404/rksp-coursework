package com.xxavierr404.profile.filter;

import com.xxavierr404.profile.domain.Role;
import com.xxavierr404.profile.domain.User;
import com.xxavierr404.profile.security.SecurityRuleChain;
import com.xxavierr404.profile.security.UserAuthContext;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.util.HashSet;

public class JwtFilter extends GenericFilterBean {
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    private final SecretKey jwtAccessSecret;
    private final SecurityRuleChain securityRuleChain;

    public JwtFilter(
            String jwtAccessSecret,
            SecurityRuleChain securityRuleChain
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.securityRuleChain = securityRuleChain;
    }

    @Override
    public void doFilter(
            ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain
    ) throws ServletException {
        try {
            HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
            String jwt = this.resolveToken(httpServletRequest);
            if (StringUtils.hasText(jwt)) {
                if (validateToken(jwt)) {
                    Authentication authentication = getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
            filterChain.doFilter(servletRequest, servletResponse);

            this.resetAuthenticationAfterRequest();
        } catch (ExpiredJwtException | IOException eje) {
            ((HttpServletResponse) servletResponse).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }

    private void resetAuthenticationAfterRequest() {
        SecurityContextHolder.getContext().setAuthentication(null);
    }

    private String resolveToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtAccessSecret).build().parseSignedClaims(authToken);
            return true;
        } catch (SignatureException e) {
            return false;
        }
    }

    public Authentication getAuthentication(String token) {
        var claims = Jwts.parser().setSigningKey(jwtAccessSecret).build().parseSignedClaims(token).getBody();
        var authorities = securityRuleChain.getPermissions(claims);
        var user = new User(
                claims.get("id", Long.class),
                claims.get("sub", String.class),
                null,
                claims.get("firstName", String.class),
                null,
                Role.valueOf(claims.get("role", String.class)),
                claims.get("organizationId", Long.class)
        );
        var principal = new UserAuthContext(user, new HashSet<>(authorities));

        authorities.forEach(auth -> logger.debug(auth.getAuthority()));

        return new UsernamePasswordAuthenticationToken(principal, "", authorities);
    }
}