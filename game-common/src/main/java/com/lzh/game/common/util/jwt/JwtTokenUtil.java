package com.lzh.game.common.util.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Duration;
import java.util.Date;

public class JwtTokenUtil {

    private static Claims getAllClaimsFromToken(String token, String secret) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody();
    }

    public static String createToken(Claims claims, String secret, int keepTime) {
        final Date expirationDate = calculateExpirationDate(new Date(), keepTime);
        claims.setExpiration(expirationDate);
        claims.setIssuedAt(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static String refreshToken(String token, String secret, int keepTime) {
        final Date createdDate = new Date();
        final Date expirationDate = calculateExpirationDate(createdDate, keepTime);

        final Claims claims = getAllClaimsFromToken(token, secret);
        claims.setIssuedAt(createdDate);
        claims.setExpiration(expirationDate);

        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }

    public static ValidateResult validateToken(String token, String secret) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
            ValidateResult result = new ValidateResult(true);
            result.setClaims(claims);
            return result;
        } catch (Exception e) {
            return new ValidateResult(false);
        }
    }

    private static Date calculateExpirationDate(Date createdDate, int keepTime) {

        return new Date(createdDate.getTime() + Duration.ofSeconds(keepTime).toMillis());
    }

    @ToString
    public static class ValidateResult {

        @Getter
        private boolean valid;

        @Getter
        @Setter
        private Claims claims;

        private ValidateResult(boolean valid) {
            this.valid = valid;
        }
    }
}
