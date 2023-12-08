package com.apapedia.user.security.jwt;

import com.apapedia.user.restService.UserRestService;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtils {
    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${apapedia.app.jwtSecret}")
    private String jwtSecret;

    @Value("${apapedia.app.jwtExpirationMs}")
    private int jwtExpirationMs;

    @Autowired
    private UserRestService userRestService;

    public String generateJwtToken(String username) {
        Map<String, Object> claims = new HashMap<>();

        var user = userRestService.getUserByUsername(username);

        if (user != null) {
            claims.put("userId", user.getId());
            claims.put("role", user.getRole());
            claims.put("name", user.getName());
        }

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    public String getUserNameFromJwtToken(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody().getSubject();
    }

    public String getClaimFromJwtToken(String token, String attribute) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.get(attribute, String.class);
    }

    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException e) {
            logger.error("Invalid JWT Signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT Token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT tokes is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}
