package com.manish.pragyan.modules.auth.services;

import java.security.Key;
import java.util.*;
import java.util.function.Function;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.manish.pragyan.modules.auth.entities.CustomUserDetails;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;


@Service 
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.expiration}")
    private long expirationTime;

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    private long getExpirationTime() {
        return expirationTime;
    }

    public String generateToken(String userName, CustomUserDetails customUserDetails) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + getExpirationTime());
        Map<String, String> claims = new HashMap<>();
        claims.put("role", customUserDetails.getRole().name());
        return Jwts.builder()   
                .subject(userName)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    private Claims extractAllClaims(String token) {
        SecretKey secretKey = (SecretKey) getSigningKey();
        return Jwts
              .parser()
              .verifyWith(secretKey)
              .build()
              .parseSignedClaims(token)
              .getPayload();
    }
            
    private <T> T getClaim(String token, Function<Claims, T> claimResolver) {
        Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    public String getUsername(String token) {
        return getClaim(token, Claims::getSubject);
    }

    private Date getExpiration(String token) {
        return getClaim(token, Claims::getExpiration);
    }

    private Boolean isExpired(String token) {
        Date expiration = getExpiration(token);
        return expiration.before(new Date());
    }

    public Boolean isValidToken(String token, UserDetails userDetails) {
        String userName = this.getUsername(token);
        return !isExpired(token) && userName.equals(userDetails.getUsername());
    }



}
