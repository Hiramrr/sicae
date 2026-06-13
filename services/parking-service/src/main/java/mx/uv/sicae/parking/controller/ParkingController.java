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

    @PatchMapping("/{idMovimiento}/exit")
    public ResponseEntity<RespuestaApi<Movimiento>> registrarSalida(@PathVariable Integer idMovimiento, @RequestBody Movimiento peticion) {
        try {
            if(peticion.getClaveUsuario() == null || peticion.getClaveUsuario().isBlank() ||
                peticion.getPlaca() == null || peticion.getPlaca().isBlank()) {
                throw new IllegalArgumentException("La clave de usuario y la placa son obligatorios");
            }

            Movimiento respuesta = movimientoService.registrarSalida(idMovimiento, peticion);
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Salida registrada correctamente");
            apiResponse.setData(respuesta);
            return ResponseEntity.ok(apiResponse);
        } catch (IllegalArgumentException e) {
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No se pudo registrar la salida");
            apiResponse.setError(e.getMessage());
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
