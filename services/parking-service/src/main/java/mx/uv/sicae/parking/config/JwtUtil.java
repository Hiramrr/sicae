package mx.uv.sicae.parking.config;

import java.security.Key;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    // Se toma la misma llave que usan los demas servicios para validar el token
    @Value("${jwt.secret}")
    private String secret;

    public Integer obtenerIdUsuario(String authorizationHeader) {
        // primero reviso que si venga algo en el encabezado Authorization
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }

        // el token debe venir con el formato normal de Bearer
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new IllegalArgumentException("El formato del token no es correcto");
        }

        // extraigo la cadena pura del token quitando la palabra "Bearer "
        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }

        try {
            // aqui se abre el token para leer los datos guardados dentro
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();

            // del token solo necesito el idUsuario para autorizar los registros de entradas y salidas
            Integer idUsuario = claims.get("idUsuario", Integer.class);
            if (idUsuario == null) {
                throw new IllegalArgumentException("No se pudo obtener el usuario del token");
            }
            return idUsuario;
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            // si el token tiene problemas o la firma no coincide, mostramos el detalle del error
            throw new IllegalArgumentException("Error JWT: " + e.getMessage());
        }
    }

    private Key getSigningKey() {
        // convierte el secreto configurado en una llave valida para JWT
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
