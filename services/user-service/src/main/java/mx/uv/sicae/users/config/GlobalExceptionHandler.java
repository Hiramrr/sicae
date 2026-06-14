package mx.uv.sicae.users.config;

import io.jsonwebtoken.JwtException;
import mx.uv.sicae.users.dto.RespuestaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<RespuestaApi<Void>> handleJwtException(JwtException e) {
        log.warn("Error de autenticacion JWT: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespuestaApi.fail("No autorizado", e.getMessage()));
    }

    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<RespuestaApi<Void>> handleSecurityException(SecurityException e) {
        log.warn("Operacion no permitida: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaApi.fail("Operacion no permitida", e.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaApi<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        log.warn("Solicitud incorrecta: {}", e.getMessage());
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RespuestaApi<Void>> handleIllegalStateException(IllegalStateException e) {
        log.error("Error interno: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaApi.fail("Error interno del servidor", e.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaApi<Void>> handleGenericException(Exception e) {
        log.error("Error interno no esperado: ", e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaApi.fail("Error interno del servidor", "Ocurrio un error inesperado"));
    }
}
