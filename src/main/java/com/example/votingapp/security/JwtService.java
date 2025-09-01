    package com.example.votingapp.security;

    import io.jsonwebtoken.*;
    import io.jsonwebtoken.security.Keys;
    import jakarta.annotation.PostConstruct;
    import org.springframework.beans.factory.annotation.Value;
    import org.springframework.security.core.userdetails.UserDetails;
    import org.springframework.stereotype.Service;

    import java.security.Key;
    import java.util.Date;
    import java.util.Map;
    import java.util.function.Function;

    @Service
    public class JwtService {


        @Value("${app.jwt.secret}")
        private String secret;

        @Value("${app.jwt.expiration-ms}")
        private long expirationMs;

        private Key key;

        @PostConstruct
        public void init() {
            if (secret.length() < 32) {
                secret = "defaultSecretKeyThatIsAtLeast256BitsLongForHS256Algorithm";
            }
            this.key = Keys.hmacShaKeyFor(secret.getBytes());
        }

        public String extractUsername(String token) {
            return extractClaim(token, Claims::getSubject);
        }

        public <T> T extractClaim(String token, Function<Claims, T> resolver) {
            final Claims claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
            return resolver.apply(claims);
        }

        public String generateToken(UserDetails user, boolean isAdmin) {
            Map<String, Object> claims = Map.of("role", isAdmin ? "ADMIN" : "USER");
            return Jwts.builder()
                    .setClaims(claims)
                    .setSubject(user.getUsername())
                    .setIssuedAt(new Date())
                    .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                    .signWith(key, SignatureAlgorithm.HS256)
                    .compact();
        }

        public boolean isTokenValid(String token, UserDetails userDetails) {
            final String username = extractUsername(token);
            return username.equals(userDetails.getUsername()) && !isExpired(token);
        }

        public boolean isTokenValid(String token) {
            try {
                Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token);
                return !isExpired(token);
            } catch (JwtException | IllegalArgumentException e) {
                return false;
            }
        }

        private boolean isExpired(String token) {
            try {
                Date exp = extractClaim(token, Claims::getExpiration);
                return exp.before(new Date());
            } catch (Exception e) {
                return true;
            }
        }
    }