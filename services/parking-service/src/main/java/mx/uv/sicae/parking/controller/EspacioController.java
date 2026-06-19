package mx.uv.sicae.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import mx.uv.sicae.parking.model.Espacio;
import mx.uv.sicae.parking.model.RespuestaApi;
import mx.uv.sicae.parking.service.EspacioService;
import mx.uv.sicae.parking.config.JwtUtil;

@RestController
// Define la ruta base para todos los endpoints de este controlador
@RequestMapping("/parking/spaces")
public class EspacioController {

    private final EspacioService espacioService;
    private final JwtUtil jwtUtil;

    public EspacioController(EspacioService espacioService, JwtUtil jwtUtil) {
        this.espacioService = espacioService;
        this.jwtUtil = jwtUtil;
    }

    @GetMapping
    public ResponseEntity<RespuestaApi<List<Espacio>>> consultarEspacios(
        // Extrae el encabezado "Authorization" de la petición.
        // Se pone required = false para que no falle automáticamente y podamos capturarlo y devolver un código 401 personalizado.
        @RequestHeader(value = "Authorization", required = false) String token) {

        try {
            // Se valida el token JWT. Si el token es nulo, tiene mal formato o ya expiró,
            // este método lanzará una IllegalArgumentException
            jwtUtil.obtenerIdUsuario(token);

            // Si el código llega hasta aquí, significa que el token es válido y el usuario está autenticado.
            // Mandamos a traer la lista completa de espacios de estacionamiento.
            List<Espacio> espacios = espacioService.obtenerTodos();

            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(true, "Espacios consultados correctamente", espacios, null);

            // Retornamos un código HTTP 200 OK con los datos
            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            // Si el token falló, capturamos la excepción y armamos una respuesta con success en false
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(false, "No se pudo consultar los espacios", null, e.getMessage());

            return ResponseEntity.status(401).body(respuesta);

        } catch (Exception e) {
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(false, "Error interno del servidor", null, e.getMessage());

            return ResponseEntity.status(500).body(respuesta);
        }
    }
}
