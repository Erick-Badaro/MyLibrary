package com.projetogs.mylibrary.jwt;

import com.nimbusds.jwt.proc.BadJWTException;
import com.projetogs.mylibrary.entities.User;
import com.projetogs.mylibrary.security.UserSystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Service
public class JwtService {

    @Autowired
    private JwtEncoder jwtEncoder;

    @Autowired
    private JwtDecoder jwtDecoder;

    private int expirationInterval = 10;

    public String newToken(UserSystem user) {
        Instant now = Instant.now();
        Instant expiration = now.plus(expirationInterval, ChronoUnit.HOURS);
        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("MyLibrary")
                .issuedAt(now)
                .expiresAt(expiration)
                .subject(user.getUsername())
                .claim("name", user.getName())
                .claim("id", user.getId())
                .build();
        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();
        try {
            Jwt jwt = this.jwtEncoder.encode(JwtEncoderParameters.from(header, claims));
            return jwt.getTokenValue();
        } catch (JwtEncodingException ex) {
            throw new RuntimeException("Erro ao gerar token JWT", ex);
        }
    }

    private Jwt decodedToken(String token) {
        try {
            return this.jwtDecoder.decode(token);
        } catch (BadJwtException ex) {
            throw ex;
        } catch (JwtException ex) {
            throw ex;
        }
    }

    public UserSystem getUSerInToken(String token) {
        try {
            Jwt jwt = decodedToken(token);
            final String username = jwt.getSubject();
            String name = jwt.getClaim("name").toString();

            return new UserSystem(name, username);
        } catch(JwtException ex) {
            throw ex;
        }
    }
}
