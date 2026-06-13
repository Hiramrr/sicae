package mx.uv.sicae.parking.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import mx.uv.sicae.parking.model.Espacio;
import mx.uv.sicae.parking.model.RespuestaApi;
import mx.uv.sicae.parking.service.EspacioService;

@RestController
@RequestMapping("/parking/spaces")
public class EspacioController {

    private final EspacioService espacioService;

    public EspacioController(EspacioService espacioService) {
        this.espacioService = espacioService;
    }

    @GetMapping
    public ResponseEntity<RespuestaApi<List<Espacio>>> consultarEspacios(
        @RequestHeader(value = "Authorization", required = false) String token) {
        try {
            if (token == null || token.trim().isEmpty()) {
                throw new IllegalArgumentException("Token JWT valido es requerido");
            }

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
