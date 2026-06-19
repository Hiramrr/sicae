package mx.uv.sicae.parking.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.uv.sicae.parking.model.*;
import mx.uv.sicae.parking.dto.*;
import mx.uv.sicae.parking.service.MovimientoService;

@RestController
@RequestMapping("/parking/movements")
public class ParkingController {

    private final MovimientoService movimientoService;

    public ParkingController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    @PostMapping("/entry")
    public ResponseEntity<RespuestaApi<Movimiento>> registrarEntrada(@Valid @RequestBody EntradaRequestDTO peticion) {
        try {
            // Delega la lógica de negocio al servicio. Si todo sale bien, retorna el movimiento creado.
            Movimiento respuesta = movimientoService.registrarEntrada(peticion);

            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Vehiculo ingresado correctamente");
            apiResponse.setData(respuesta);

            return ResponseEntity.status(201).body(apiResponse);

        } catch (IllegalArgumentException e) {
            // Si el servicio detecta un problema (ej. el estacionamiento está lleno o el auto no es del usuario), lanza la excepción y cae aquí
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No se pudo registrar la entrada");
            apiResponse.setError(e.getMessage()); // Muestra el detalle específico del problema

            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    // Mapea las peticiones HTTP PATCH a la ruta /parking/movements/{idMovimiento}/exit
    @PatchMapping("/{idMovimiento}/exit")
    public ResponseEntity<RespuestaApi<Movimiento>> registrarSalida(@PathVariable Integer idMovimiento, @Valid @RequestBody SalidaRequestDTO peticion) {
        try {
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
