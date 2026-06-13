package mx.uv.sicae.auth.config;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import mx.uv.sicae.auth.model.UsuarioAuth;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {

    // Leemos los valores de application.properties
    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    public String generarToken(UsuarioAuth usuario) {
        // Agregamos datos útiles al interior del Token pra saber quien y que rol hace la peticion al servicio
        Map<String, Object> claims = new HashMap<>();
        claims.put("idUsuario", usuario.getIdUsuario());
        claims.put("idRol", usuario.getIdRol());
        claims.put("rol", usuario.getRol());

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(usuario.getUsername())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}