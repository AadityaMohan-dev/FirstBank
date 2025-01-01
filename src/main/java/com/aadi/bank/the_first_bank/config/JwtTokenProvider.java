package com.aadi.bank.the_first_bank.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtTokenProvider {
    @Value("${app.jwt-secret}")
    private String jwtSecret;

    @Value("${app.jwt-expiration}")
    private String jwtExpirationDate;

    public String generateToken(Authentication authentication){
        String username = authentication.getName();
        Date currentDate = new Date();
        Date expirationDate = new Date(currentDate.getTime() + jwtExpirationDate);

    return Jwts.builder()
            .setSubject(username)
            .setIssuedAt(currentDate)
            .setExpiration(expirationDate)
            .signWith(key())
            .compact();
    }
    // Helper method to get signing key
    private Key key() {
        byte[] bytes = Decoders.BASE64URL.decode(jwtSecret); // Decoding the jwtSecret
        return Keys.hmacShaKeyFor(bytes); // Generate a signing key
    }

    // Get username from the token
    public String getUsername(String token) {
        Claims claims = Jwts.parser() // Use parserBuilder() to create a parser
                .setSigningKey(key()) // Set the correct signing key
                .build() // Build the parser
                .parseClaimsJws(token) // Parse the JWT token to extract claims
                .getBody(); // Extract claims from the body of the JWT

        return claims.getSubject(); // Return the subject (username)
    }

    public Boolean validateToken(String token){
        try{
            Jwts.parser().setSigningKey(key()).build().parse(token);
            return true;
        } catch (ExpiredJwtException | MalformedJwtException | SecurityException | IllegalArgumentException e) {
            throw new RuntimeException(e);
        }
    }
}
