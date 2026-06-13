package mx.uv.sicae.parking.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.uv.sicae.parking.model.*;
import mx.uv.sicae.parking.service.MovimientoService;

@RestController
@RequestMapping("/parking/movements")
public class ParkingController {

    private final MovimientoService movimientoService;

    public ParkingController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping("/entry")
    public ResponseEntity<RespuestaApi<Movimiento>> registrarEntrada(@Valid @RequestBody Movimiento peticion) {
        try {
            Movimiento respuesta = movimientoService.registrarEntrada(peticion);
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Vehiculo ingresado correctamente");
            apiResponse.setData(respuesta);
            return ResponseEntity.status(201).body(apiResponse);
        } catch (IllegalArgumentException e) {
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No se pudo registrar la entrada");
            apiResponse.setError(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
