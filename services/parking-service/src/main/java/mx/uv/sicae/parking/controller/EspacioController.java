package mx.uv.sicae.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import mx.uv.sicae.parking.model.Espacio;
import mx.uv.sicae.parking.model.RespuestaApi;
import mx.uv.sicae.parking.service.EspacioService;
import mx.uv.sicae.parking.config.JwtUtil;

@RestController
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
        @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            jwtUtil.obtenerIdUsuario(token);

            List<Espacio> espacios = espacioService.obtenerTodos();
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(true, "Espacios consultados correctamente", espacios, null);
            return ResponseEntity.ok(respuesta);

        } catch (IllegalArgumentException e) {
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(false, "No se pudo consultar los espacios", null, e.getMessage());
            return ResponseEntity.status(401).body(respuesta);
        } catch (Exception e) {
            RespuestaApi<List<Espacio>> respuesta = new RespuestaApi<>(false, "Error interno del servidor", null, e.getMessage());
            return ResponseEntity.status(500).body(respuesta);
        }
    }
}
