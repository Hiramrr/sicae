package mx.uv.sicae.auth.controller;

import mx.uv.sicae.auth.model.LoginRequest;
import mx.uv.sicae.auth.model.LoginResponse;
import mx.uv.sicae.auth.model.RespuestaApi;
import mx.uv.sicae.auth.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


//Este controlador es la puerta de entrada para las peticiones pero 
// no gestiona ni logica de negocios, ni autenticacion ni generacion del JWT

@RestController
@RequestMapping("/auth") 
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login") 
    public ResponseEntity<RespuestaApi<LoginResponse>> login(@RequestBody LoginRequest request) {
        try {
            // Pasamos la petición al servicio 
            LoginResponse response = authService.login(request);
    
            // Si todo sale bien, devolvemos un HTTP 200 OK con nuestra respuesta estándar
            return ResponseEntity.ok(RespuestaApi.ok("Login exitoso", response));
            
        } catch (IllegalArgumentException e) {
            // Atrapa errores de validación de campos: Vacios o muy largos
            //devolvemos un HTTP 400 Bad Request
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(RespuestaApi.fail("Error de validación", e.getMessage()));
                    
        } catch (SecurityException e) {
            //Si es un error de credenciales: No existe en la bd, no coinciden los hashes o sino esta activo
            //Devolvemos un HTTP 401 UNAUTHORIZED
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RespuestaApi.fail("No se pudo completar la operación", e.getMessage()));
        }catch (Exception e) {
            // Otro cualquier erro devolvera -> HTTP 500
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespuestaApi.fail("Error interno del servidor", e.getMessage()));
        }

    }
}