package br.com.qawler.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * Handles authentication: login, token generation, validation and user lookup.
 *
 * Expected entity: {@code br.com.qawler.model.Usuario} with fields:
 *   id (Long), email (String), senha (String), nome (String), ativo (Boolean)
 */
@Service
public class AuthService {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final SecretKey signingKey;
    private final long expirationMs;
    private final PasswordEncoder passwordEncoder;

    // Replace with real UsuarioRepository when the entity layer is built
    // private final UsuarioRepository usuarioRepository;

    public AuthService(@Value("${jwt.secret}") String secret,
                       @Value("${jwt.expiration-ms}") long expirationMs,
                       PasswordEncoder passwordEncoder /*, UsuarioRepository usuarioRepository */) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMs = expirationMs;
        this.passwordEncoder = passwordEncoder;
        // this.usuarioRepository = usuarioRepository;
    }

    /**
     * Authenticates a user by email + password and returns a signed JWT.
     *
     * @param email    user email
     * @param password raw password
     * @return JWT token string
     * @throws IllegalArgumentException if credentials are invalid
     */
    public String login(String email, String password) {
        // TODO: replace with actual DB lookup
        // Usuario usuario = usuarioRepository.findByEmail(email)
        //         .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas"));
        // if (!passwordEncoder.matches(password, usuario.getSenha())) {
        //     throw new IllegalArgumentException("Credenciais inválidas");
        // }
        // if (!usuario.getAtivo()) {
        //     throw new IllegalArgumentException("Usuário inativo");
        // }
        // return generateToken(usuario.getId(), usuario.getEmail());

        // Stub — replace when entity/repository exist
        log.info("Authenticating user: {}", email);
        if (!"admin@qawler.com".equals(email) || !"admin".equals(password)) {
            throw new IllegalArgumentException("Credenciais inválidas");
        }
        return generateToken(1L, email);
    }

    /**
     * Generates a JWT for the given user ID and email.
     */
    public String generateToken(Long userId, String email) {
        Date now = new Date();
        Date expiry = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(email)
                .claim("userId", userId)
                .issuedAt(now)
                .expiration(expiry)
                .signWith(signingKey)
                .compact();
    }

    /**
     * Validates a token and returns the subject (email).
     */
    public String validateToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .verifyWith(signingKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return claims.getSubject();
        } catch (JwtException e) {
            log.warn("Invalid JWT: {}", e.getMessage());
            return null;
        }
    }

    /**
     * Extracts the user ID claim from a JWT.
     */
    public Long extractUserId(String token) {
        Claims claims = Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return claims.get("userId", Long.class);
    }
}
