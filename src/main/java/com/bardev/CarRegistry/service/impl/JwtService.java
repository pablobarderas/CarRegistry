package com.bardev.CarRegistry.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@RequiredArgsConstructor
@Service
public class JwtService {


    @Value("${token.secret.key}")
    String jwtSecretKey;

    @Value("${token.expiration.ms}")
    Long jwtExpirationMs;

    // EXTRACT USERNAME FROM TOKEN
    public String extractUserName(String token){
        return extractClaim(token, Claims::getSubject);
    }

    // GENERATE TOKEN FROM USER DETAILS
    public String generateToken(UserDetails userDetails){
        return generateToken(new HashMap<>(), userDetails);
    }

    // CHECK IF TOKEN IS VALID
    public boolean isValidToken(String token, UserDetails userDetails){
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    // EXTRACT CLAIM FROM TOKEN
    private <T> T extractClaim(String token, Function<Claims, T> claimsResolvers){
        final Claims claims = extractAllClaims(token);
        return claimsResolvers.apply(claims);
    }

    // GENERATE TOKEN FROM CLAIMS AND USER DETAILS
    private String generateToken(Map<String, Object> extraClaims, UserDetails userDetails){
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(userDetails.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + jwtExpirationMs))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    // CHECK IF TOKEN HAS EXPIRED
    private boolean isTokenExpired(String token){
        return extractExpiration(token).before(new Date());
    }

    // EXTRACT EXPIRATION DATE
    private Date extractExpiration(String token){
        return extractClaim(token, Claims::getExpiration);
    }

    // EXTRACT ALL CLAIMS
    private Claims extractAllClaims(String token){
        return Jwts
                .parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // GET KEY SIGNATURE
    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
