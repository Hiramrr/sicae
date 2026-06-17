package mx.uv.sicae.parking.controller;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import mx.uv.sicae.parking.model.*;
import mx.uv.sicae.parking.dto.*;
import mx.uv.sicae.parking.service.MovimientoService;

// Le indica a Spring que esta clase es un controlador REST para manejar peticiones web y devolver JSON
@RestController
// Define la ruta base para todos los endpoints relacionados con los movimientos del estacionamiento
@RequestMapping("/parking/movements")
public class ParkingController {

    // Dependencia del servicio donde reside toda la lógica de negocio y validaciones
    private final MovimientoService movimientoService;

    // Inyección de dependencias a través del constructor para que Spring lo inicialice automáticamente
    public ParkingController(MovimientoService movimientoService) {
        this.movimientoService = movimientoService;
    }

    // Mapea las peticiones HTTP POST a la ruta /parking/movements/entry
    @PostMapping("/entry")
    // @Valid asegura que los datos de entrada cumplan con las reglas (ej. campos obligatorios) antes de ejecutar el código
    // @RequestBody indica que los datos vendrán estructurados en el cuerpo de la petición (JSON)
    public ResponseEntity<RespuestaApi<Movimiento>> registrarEntrada(@Valid @RequestBody EntradaRequestDTO peticion) {
        try {
            // Delega la lógica de negocio al servicio. Si todo sale bien, retorna el movimiento creado.
            Movimiento respuesta = movimientoService.registrarEntrada(peticion);

            // Construye la estructura estándar de respuesta del sistema
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Vehiculo ingresado correctamente");
            apiResponse.setData(respuesta);

            // Retorna un HTTP 201 Created indicando que el registro en base de datos fue exitoso
            return ResponseEntity.status(201).body(apiResponse);

        } catch (IllegalArgumentException e) {
            // Si el servicio detecta un problema (ej. el estacionamiento está lleno o el auto no es del usuario), lanza la excepción y cae aquí
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No se pudo registrar la entrada");
            apiResponse.setError(e.getMessage()); // Muestra el detalle específico del problema

            // Retorna un HTTP 400 Bad Request porque los datos o el estado actual no permiten la operación
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }

    // Mapea las peticiones HTTP PATCH a la ruta /parking/movements/{idMovimiento}/exit
    @PatchMapping("/{idMovimiento}/exit")
    // @PathVariable toma el valor dinámico directamente de la URL (el número del ticket/movimiento)
    public ResponseEntity<RespuestaApi<Movimiento>> registrarSalida(@PathVariable Integer idMovimiento, @Valid @RequestBody SalidaRequestDTO peticion) {
        try {
            // Pide al servicio que registre la salida, calcule el tiempo transcurrido y determine el costo a pagar
            Movimiento respuesta = movimientoService.registrarSalida(idMovimiento, peticion);

            // Construye la estructura de respuesta exitosa
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(true);
            apiResponse.setMessage("Salida registrada correctamente");
            apiResponse.setData(respuesta);

            // Retorna un HTTP 200 OK junto con el desglose del cobro
            return ResponseEntity.ok(apiResponse);

        } catch (IllegalArgumentException e) {
            // Atrapa errores de negocio, como intentar cerrar un movimiento que ya estaba cobrado o si no coinciden las placas
            RespuestaApi<Movimiento> apiResponse = new RespuestaApi<>();
            apiResponse.setSuccess(false);
            apiResponse.setMessage("No se pudo registrar la salida");
            apiResponse.setError(e.getMessage());

            // Retorna HTTP 400 Bad Request
            return ResponseEntity.badRequest().body(apiResponse);
        }
    }
}
