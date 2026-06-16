package mx.uv.sicae.vehicle.config;

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
            throw new SecurityException("Debe enviar el token de autenticacion");
        }

        // el token debe venir con el formato normal de Bearer
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new SecurityException("El formato del token no es correcto");
        }

        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new SecurityException("Debe enviar el token de autenticacion");
        }

        try {
            // aqui se abre el token para leer los datos guardados dentro
            Claims claims = Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();

            // del token solo necesito el idUsuario para comparar permisos
            Integer idUsuario = claims.get("idUsuario", Integer.class);
            if (idUsuario == null) {
                throw new SecurityException("No se pudo obtener el usuario del token");
            }
            return idUsuario;
        } catch (SecurityException e) {
            throw e;
        } catch (Exception e) {
            throw new SecurityException("El token no es valido o ya expiro");
        }
    }

    private Key getSigningKey() {
        // convierte el secreto configurado en una llave valida para JWT
        return Keys.hmacShaKeyFor(secret.getBytes());
    }
}
