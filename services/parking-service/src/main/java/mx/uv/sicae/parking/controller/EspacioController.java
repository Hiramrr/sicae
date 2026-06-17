package mx.uv.sicae.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import mx.uv.sicae.parking.model.Espacio;
import mx.uv.sicae.parking.model.RespuestaApi;
import mx.uv.sicae.parking.service.EspacioService;
import mx.uv.sicae.parking.config.JwtUtil;

// Le indica a Spring que esta clase es un controlador REST y expone endpoints web que devuelven JSON
@RestController
// Define la ruta base para todos los endpoints de este controlador
@RequestMapping("/parking/spaces")
public class EspacioController {

    // Dependencias necesarias: el servicio (lógica de negocio) y la utilidad JWT (seguridad)
    private final EspacioService espacioService;
    private final JwtUtil jwtUtil;

    // Constructor para que Spring inyecte automáticamente las dependencias al iniciar
    public EspacioController(EspacioService espacioService, JwtUtil jwtUtil) {
        this.espacioService = espacioService;
        this.jwtUtil = jwtUtil;
    }

    // Mapea las peticiones HTTP GET que lleguen a la ruta /parking/spaces
    @GetMapping
    public ResponseEntity<RespuestaApi<List<Espacio>>> consultarEspacios(
        // Extrae el encabezado "Authorization" de la petición.
        // Se pone required = false para que no falle automáticamente y podamos capturarlo y devolver un código 401 personalizado.
        @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // Se valida el token JWT. Si el token es nulo, tiene mal formato o ya expiró,
            // este método lanzará una IllegalArgumentException que interrumpirá el flujo.
            jwtUtil.obtenerIdUsuario(token);

            // Si el código llega hasta aquí, significa que el token es válido y el usuario está autenticado.
            // Mandamos a traer la lista completa de espacios de estacionamiento.
            List<Espacio> espacios = espacioService.obtenerTodos();

            // Armamos la respuesta estandarizada (success, message, data, error)
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(true, "Espacios consultados correctamente", espacios, null);

            // Retornamos un código HTTP 200 OK con los datos
            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            // Si el token falló, capturamos la excepción y armamos una respuesta con success en false
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(false, "No se pudo consultar los espacios", null, e.getMessage());

            // Retornamos HTTP 401 Unauthorized indicando que no tiene permisos
            return ResponseEntity.status(401).body(respuesta);

        } catch (Exception e) {
            // Si ocurre un error inesperado (ej. se cayó la base de datos), atrapamos la excepción genérica
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(false, "Error interno del servidor", null, e.getMessage());

            // Retornamos HTTP 500 Internal Server Error
            return ResponseEntity.status(500).body(respuesta);
        }
    }
}
