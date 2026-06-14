package mx.uv.sicae.parking.config;

import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secret;

    public Integer obtenerIdUsuario(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }

        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("El formato del token no es correcto");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }

        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            Integer idUsuario = claims.get("idUsuario", Integer.class);
            if (idUsuario == null) {
                throw new IllegalArgumentException("No se pudo obtener el usuario del token");
            }
            return idUsuario;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("El token no es valido o ya expiro");
        }
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
