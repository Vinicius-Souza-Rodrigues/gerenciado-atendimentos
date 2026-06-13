package com.agendamentos.adapter.outbound.auth;

import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private static final long EXPIRATION_MS = 7L * 24 * 60 * 60 * 1000;

    @Value("${jwt.secret}")
    private String secret;

    public String gerarToken(UUID prestadorId) {
        return Jwts.builder()
                .subject(prestadorId.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_MS))
                .signWith(getKey())
                .compact();
    }

    public UUID extrairPrestadorId(String token) {
        var subject = Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        return UUID.fromString(subject);
    }

    public boolean isValido(String token) {
        try {
            extrairPrestadorId(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private SecretKey getKey() {
        var bytes = secret.getBytes();
        if (bytes.length < 32) {
            var padded = new byte[32];
            System.arraycopy(bytes, 0, padded, 0, bytes.length);
            return Keys.hmacShaKeyFor(padded);
        }
        return Keys.hmacShaKeyFor(bytes);
    }

}
