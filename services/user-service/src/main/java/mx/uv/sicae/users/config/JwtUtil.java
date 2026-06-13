package mx.uv.sicae.users.config;

import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import java.security.Key;
import io.jsonwebtoken.Claims;

@Component
public class JwtUtil {
    @Value("${jwt.secret}")
    private String secreto;

    public Claims validarToken(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    public Integer extraerIdUsuario(String token) {
        Claims claims = validarToken(token);
        return extraerEntero(claims, "idUsuario");
    }

    public Integer extraerIdRol(String token) {
        Claims claims = validarToken(token);
        return extraerEntero(claims, "idRol");
    }

    public String extraerRol(String token) {
        Claims claims = validarToken(token);
        return claims.get("rol", String.class);
    }

    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secreto.getBytes());
    }

    private Integer extraerEntero(Claims claims, String nombreClaim) {
        Object valor = claims.get(nombreClaim);
        if (valor instanceof Integer) {
            return (Integer) valor;
        }
        if (valor instanceof Number) {
            return ((Number) valor).intValue();
        }
        if (valor instanceof String) {
            return Integer.parseInt((String) valor);
        }
        return null;
    }
}
