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

    // Valida que el token JWT sea correcto y devuelve sus claims (datos internos).
    public Claims validarToken(String token){
        return Jwts.parserBuilder()
        .setSigningKey(getSigningKey())
        .build()
        .parseClaimsJws(token)
        .getBody();
    }

    // Toma el id del usuario desde el token.
    public Integer extraerIdUsuario(String token) {
        Claims claims = validarToken(token);
        return extraerEntero(claims, "idUsuario");
    }

    // Toma el id del rol desde el token.
    public Integer extraerIdRol(String token) {
        Claims claims = validarToken(token);
        return extraerEntero(claims, "idRol");
    }

    // Toma el nombre del rol desde el token.
    public String extraerRol(String token) {
        Claims claims = validarToken(token);
        return claims.get("rol", String.class);
    }

    // Construye la llave a partir del secreto.
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secreto.getBytes());
    }

    // Saca un numero entero de un claim
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
