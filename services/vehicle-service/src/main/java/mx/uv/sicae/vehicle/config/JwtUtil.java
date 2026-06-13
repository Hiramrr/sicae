package mx.uv.sicae.vehicle.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtUtil {
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String HMAC_SHA256 = "HmacSHA256";

    @Value("${jwt.secret}")
    private String secret;

    private final ObjectMapper objectMapper;

    public JwtUtil(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public Integer obtenerIdUsuario(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }
        if (!authorizationHeader.startsWith(BEARER_PREFIX)) {
            throw new IllegalArgumentException("El formato del token no es correcto");
        }

        String token = authorizationHeader.substring(BEARER_PREFIX.length()).trim();
        if (token.isEmpty()) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }

        try {
            Map<String, Object> claims = validarYObtenerClaims(token);
            Object idUsuario = claims.get("idUsuario");
            if (idUsuario instanceof Integer) {
                return (Integer) idUsuario;
            }
            if (idUsuario instanceof Number) {
                return ((Number) idUsuario).intValue();
            }
            if (idUsuario instanceof String) {
                return Integer.valueOf((String) idUsuario);
            }
            throw new IllegalArgumentException("No se pudo obtener el usuario del token");
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            throw new IllegalArgumentException("El token no es valido o ya expiro");
        }
    }

    private Map<String, Object> validarYObtenerClaims(String token) throws Exception {
        String[] partes = token.split("\\.");
        if (partes.length != 3) {
            throw new IllegalArgumentException("El formato del token no es correcto");
        }

        String headerYPayload = partes[0] + "." + partes[1];
        String firmaEsperada = firmar(headerYPayload);
        if (!MessageDigest.isEqual(firmaEsperada.getBytes(StandardCharsets.UTF_8),
                partes[2].getBytes(StandardCharsets.UTF_8))) {
            throw new IllegalArgumentException("El token no es valido o ya expiro");
        }

        byte[] payloadBytes = Base64.getUrlDecoder().decode(partes[1]);
        Map<String, Object> claims = objectMapper.readValue(payloadBytes, new TypeReference<Map<String, Object>>() {
        });
        validarExpiracion(claims);
        return claims;
    }

    private String firmar(String contenido) throws Exception {
        Mac mac = Mac.getInstance(HMAC_SHA256);
        mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), HMAC_SHA256));
        byte[] firma = mac.doFinal(contenido.getBytes(StandardCharsets.UTF_8));
        return Base64.getUrlEncoder().withoutPadding().encodeToString(firma);
    }

    private void validarExpiracion(Map<String, Object> claims) {
        Object exp = claims.get("exp");
        if (exp == null) {
            return;
        }

        long expiracionSegundos;
        if (exp instanceof Number) {
            expiracionSegundos = ((Number) exp).longValue();
        } else {
            expiracionSegundos = Long.parseLong(exp.toString());
        }

        long ahoraSegundos = System.currentTimeMillis() / 1000;
        if (expiracionSegundos < ahoraSegundos) {
            throw new IllegalArgumentException("El token no es valido o ya expiro");
        }
    }
}
