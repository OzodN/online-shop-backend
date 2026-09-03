package dev.ozodn.onlineshop.user.service;

import dev.ozodn.onlineshop.user.config.JwtProperties;
import dev.ozodn.onlineshop.user.entity.Role;
import dev.ozodn.onlineshop.user.entity.User;
import dev.ozodn.onlineshop.user.entity.RefreshToken;
import dev.ozodn.onlineshop.user.repository.RefreshTokenRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Base64;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class JwtServiceImplTest {

    private static final String SECRET =
            Base64.getEncoder().encodeToString(
                    "01234567890123456789012345678901".getBytes()
            );


    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private JwtServiceImpl jwtService;

    @BeforeEach
    void setUp() {
        JwtProperties jwtProperties = new JwtProperties();

        jwtProperties.setSecret(SECRET);
        jwtProperties.setAccessTokenExpirationMs(900_000L);
        jwtProperties.setRefreshTokenExpirationMs(86_400_000L);

        jwtService = new JwtServiceImpl(
                jwtProperties,
                refreshTokenRepository
        );

        jwtService.init();
    }

    @Test
    void generateAccessToken_shouldContainExpectedClaims() {
        UUID externalId = UUID.randomUUID();

        Role customerRole = mock(Role.class);
        Role sellerRole = mock(Role.class);

        when(customerRole.getName()).thenReturn("CUSTOMER");
        when(sellerRole.getName()).thenReturn("SELLER");

        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getEmail()).thenReturn("john@example.com");
        when(user.getRoles())
                .thenReturn(new HashSet<>() {{
                    add(customerRole);
                    add(sellerRole);
                }});

        String token =
                jwtService.generateAccessToken(user);

        Claims claims = jwtService.extractAllClaims(token);

        assertEquals(externalId.toString(), claims.getSubject());
        assertEquals("john@example.com", claims.get("email"));

        assertTrue(
                ((java.util.List<?>) claims.get("roles"))
                        .contains("CUSTOMER")
        );
        assertTrue(
                ((java.util.List<?>) claims.get("roles"))
                        .contains("SELLER")
        );
    }

    @Test
    void generateRefreshToken_shouldPersistRefreshToken() {
        UUID externalId = UUID.randomUUID();
        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(externalId);

        when(refreshTokenRepository.save(any(RefreshToken.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        String token =
                jwtService.generateRefreshToken(user);

        assertNotNull(token);

        verify(refreshTokenRepository).save(
                argThat(refreshToken ->
                        refreshToken.getUser() == user
                                && refreshToken.getToken().equals(token)
                                && !refreshToken.isRevoked()
                                && refreshToken.getExpiresAt()
                                .isAfter(Instant.now())
                )
        );
    }

    @Test
    void extractExternalId_shouldReturnSubjectUuid() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getEmail()).thenReturn("john@example.com");
        when(user.getRoles()).thenReturn(new HashSet<>());

        String token =
                jwtService.generateAccessToken(user);

        assertEquals(
                externalId,
                jwtService.extractExternalId(token)
        );
    }

    @Test
    void extractExternalId_shouldThrow_whenSubjectIsNotUuid() {
        SecretKey signingKey =
                Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(SECRET)
                );

        String token = Jwts.builder()
                .subject("not-a-uuid")
                .issuedAt(java.util.Date.from(Instant.now()))
                .expiration(
                        java.util.Date.from(
                                Instant.now().plusSeconds(60)
                        )
                )
                .signWith(signingKey)
                .compact();

        assertThrows(
                JwtException.class,
                () -> jwtService.extractExternalId(token)
        );
    }

    @Test
    void extractEmail_shouldReturnEmailClaim() {
        User user = mock(User.class);

        UUID externalId = UUID.randomUUID();

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getEmail()).thenReturn("john@example.com");
        when(user.getRoles()).thenReturn(new HashSet<>());

        String token =
                jwtService.generateAccessToken(user);

        assertEquals(
                "john@example.com",
                jwtService.extractEmail(token)
        );
    }

    @Test
    void extractIdentifier_shouldPreferExternalId() {
        UUID externalId = UUID.randomUUID();

        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(externalId);
        when(user.getEmail()).thenReturn("john@example.com");
        when(user.getRoles()).thenReturn(new HashSet<>());

        String token =
                jwtService.generateAccessToken(user);

        assertEquals(
                externalId.toString(),
                jwtService.extractIdentifier(token).orElseThrow()
        );
    }

    @Test
    void isTokenValid_shouldReturnTrue_forValidToken() {
        User user = mock(User.class);

        when(user.getExternalId()).thenReturn(UUID.randomUUID());
        when(user.getEmail()).thenReturn("john@example.com");
        when(user.getRoles()).thenReturn(new HashSet<>());

        String token =
                jwtService.generateAccessToken(user);

        assertTrue(jwtService.isTokenValid(token));
    }

    @Test
    void isTokenValid_shouldReturnFalse_forMalformedToken() {
        assertFalse(
                jwtService.isTokenValid("not-a-jwt")
        );
    }

    @Test
    void isTokenValid_shouldReturnFalse_forExpiredToken() {
        SecretKey signingKey =
                Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(SECRET)
                );

        Instant now = Instant.now();

        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(java.util.Date.from(now.minusSeconds(120)))
                .expiration(java.util.Date.from(now.minusSeconds(60)))
                .signWith(signingKey)
                .compact();

        assertFalse(
                jwtService.isTokenValid(token)
        );
    }

    @Test
    void extractAllClaims_shouldThrow_forInvalidSignature() {
        String anotherSecret =
                Base64.getEncoder().encodeToString(
                        "11111111111111111111111111111111".getBytes()
                );

        SecretKey anotherKey =
                Keys.hmacShaKeyFor(
                        Decoders.BASE64.decode(anotherSecret)
                );

        String token = Jwts.builder()
                .subject(UUID.randomUUID().toString())
                .issuedAt(
                        java.util.Date.from(Instant.now())
                )
                .expiration(
                        java.util.Date.from(
                                Instant.now().plusSeconds(60)
                        )
                )
                .signWith(anotherKey)
                .compact();

        assertThrows(
                JwtException.class,
                () -> jwtService.extractAllClaims(token)
        );
    }
}