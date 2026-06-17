package mx.uv.sicae.users.config;

import io.jsonwebtoken.JwtException;
import mx.uv.sicae.users.dto.RespuestaApi;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Token JWT invalido o expirado, regresa 401
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<RespuestaApi<Void>> handleJwtException(JwtException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespuestaApi.fail("No autorizado", e.getMessage()));
    }

    // El usuario no tiene permisos para la operacion, regresa 403
    @ExceptionHandler(SecurityException.class)
    public ResponseEntity<RespuestaApi<Void>> handleSecurityException(SecurityException e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaApi.fail("Operacion no permitida", e.getMessage()));
    }

    // Datos invalidos enviados por el cliente, regresa 400
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaApi<Void>> handleIllegalArgumentException(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
    }

    // Error inesperado del lado del servidor, regresa 500
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<RespuestaApi<Void>> handleIllegalStateException(IllegalStateException e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaApi.fail("Error interno del servidor", e.getMessage()));
    }

    // Cualquier otra exception no contemplada, regresa 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaApi<Void>> handleGenericException(Exception e) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(RespuestaApi.fail("Error interno del servidor", "Ocurrio un error inesperado"));
    }
}
