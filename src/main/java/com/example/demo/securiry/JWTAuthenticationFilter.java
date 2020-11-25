package com.example.demo.securiry;

import com.example.demo.service.UserService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JWTAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    JWTokenProvider jwTokenProvider;
    @Autowired
    UserService userService;

    @SneakyThrows
    @Override
    protected void doFilterInternal(HttpServletRequest httpServletRequest
            , HttpServletResponse httpServletResponse
            , FilterChain filterChain)
            throws ServletException, IOException {
        String token = getTokenFromRequest(httpServletRequest);
        try {
            if (jwTokenProvider.validateToken(token)) {
                UserDetails userDetails = userService.loadUserByUserID(
                        jwTokenProvider.getUserIdFromJWT(token));
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authenticationToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(httpServletRequest));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        } catch (Exception ex) {
            logger.error(ex.getMessage());
        }
        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (StringUtils.hasText(token) && token.startsWith("Bearer ")) {
            return token.substring(7, token.length());
        }
        return null;
    }
}
