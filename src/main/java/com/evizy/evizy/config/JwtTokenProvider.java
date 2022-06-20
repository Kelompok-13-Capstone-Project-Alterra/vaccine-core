package com.evizy.evizy.config;

import com.evizy.evizy.domain.dao.Admin;
import com.evizy.evizy.domain.dao.Users;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.*;

@Log4j2
@Component
public class JwtTokenProvider {
    private final Key key = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    @Value("3600000")
    private Long expiration;

    public String generateToken(Authentication authentication) {
        Map<String, Object> claims = new HashMap<>();

        String id = "";
        String subject = "";

        boolean isAdmin = false;

        for(GrantedAuthority grantedAuthority : authentication.getAuthorities()) {
            if (grantedAuthority.getAuthority().equalsIgnoreCase("ROLE_ADMIN")){
                isAdmin = true;
            }
        }

        if (isAdmin) {
            final Admin admin = (Admin) authentication.getPrincipal();

            // admin
            claims.put("is_admin", true);
            claims.put("admin_id", admin.getId());
            claims.put("admin_username", admin.getUsername());
            id = admin.getId().toString();
            subject = admin.getUsername();
        } else {
            final Users user = (Users) authentication.getPrincipal();
            claims.put("is_admin", false);
            claims.put("user_nik", user.getNik());
            claims.put("user_id", user.getId());
            id = user.getId().toString();
            subject = user.getNik();
        }

        Date now = new Date(System.currentTimeMillis());
        Date expiredDate = new Date(now.getTime() + expiration);


        return Jwts.builder()
                .setId(id)
                .setSubject(subject)
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(expiredDate)
                .signWith(key)
                .compact();
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid Jwt Signature: {}", ex.getMessage());
        } catch (MalformedJwtException ex) {
            log.error("Invalid Jwt Token: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.error("Expired Jwt Token: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported Jwt Token: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.error("Jwt claim string is empty: {}", ex.getMessage());
        }
        return false;
    }

    public String getUsername(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        if ((boolean) claims.get("is_admin")) {
            return claims.get("admin_username").toString();
        } else {
            return claims.get("user_nik").toString();
        }
    }

    public boolean getIsAdmin(String token) {
        Claims claims = Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token).getBody();
        if ((boolean) claims.get("is_admin")) {
            return true;
        } else {
            return false;
        }
    }
}
