package com.example.demo.securiry;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class JWTokenProvider {

    private final String JWT_SECRET = "Jason";
    private final Long JWT_EXPIRATION = 604800000L;

    public String generateJWT(CustomUserDetails userDetails) {
        Date date = new Date();
        Date expirationDate = new Date(date.getTime() + JWT_EXPIRATION);

        return Jwts.builder()
                .setSubject(Long.toString(userDetails.getUsers().getUserId()))
                .setIssuedAt(date)
                .setExpiration(expirationDate)
                .signWith(SignatureAlgorithm.HS512, JWT_SECRET)
                .compact();
    }

    public Long getUserIdFromJWT(String token){
        Claims claims = Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
        return Long.valueOf(claims.getSubject());
    }

    public boolean validateToken(String token){
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        }catch (SignatureException ex){
            log.error(ex.getMessage());
        }
        catch (MalformedJwtException ex){
            log.error(ex.getMessage());
        }
        catch (ExpiredJwtException ex){
            log.error(ex.getMessage());
        }
        catch (UnsupportedJwtException ex){
            log.error(ex.getMessage());
        }
        catch (IllegalArgumentException ex){
            log.error(ex.getMessage());
        }
        return false;
    }
}
