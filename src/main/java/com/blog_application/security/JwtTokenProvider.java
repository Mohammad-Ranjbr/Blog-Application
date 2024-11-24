package com.blog_application.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {

    @Value("${app-jwt-secret}")
    private String JWT_SECRET;
    @Value("${app-jwt-expiration-milliseconds}")
    private long JWT_EXPIRATION_TIME;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentTime = new Date();
        Date expireTime = new Date(currentTime.getTime() + JWT_EXPIRATION_TIME);
        return Jwts.builder().subject(username).issuedAt(currentTime).expiration(expireTime).signWith(key()).compact();
    }

    public String getUsername(String token){
        return Jwts.parser()
                .verifyWith((SecretKey) key())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    public boolean validateToken(String token) {
        try{
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parse(token);
            return true;
        } catch (MalformedJwtException malformedJwtException){
            throw new MalformedJwtException("Invalid token");
        } catch (ExpiredJwtException expiredJwtException){
            throw new ExpiredJwtException(null,null,"Token is expired");
        } catch (UnsupportedJwtException unsupportedJwtException){
            throw new UnsupportedJwtException("Unsupported Jwt token");
        } catch (IllegalArgumentException illegalArgumentException){
            throw new IllegalArgumentException("Jwt claims string is null or empty");
        }
    }

    private Key key(){
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(JWT_SECRET));
    }

}
