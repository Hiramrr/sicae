package mx.uv.sicae.vehicle.controller;

import java.util.List;

import jakarta.validation.Valid;
import mx.uv.sicae.vehicle.dto.EstatusVehiculoRequest;
import mx.uv.sicae.vehicle.dto.RespuestaApi;
import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.entity.VehiculoEntity;
import mx.uv.sicae.vehicle.service.VehiculoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/vehiculos")
public class VehiculoController {
    private final VehiculoService vehiculoService;

    public VehiculoController(VehiculoService vehiculoService) {
        this.vehiculoService = vehiculoService;
    }

    @GetMapping("/usuario/{idUsuario}")
    public RespuestaApi<List<VehiculoEntity>> buscarPorUsuario(@PathVariable Integer idUsuario,
                                                               @RequestHeader(value = "X-User-Id") Integer idUsuarioAutenticado) {
        return RespuestaApi.ok("Vehiculos consultados correctamente",
                vehiculoService.buscarPorUsuario(idUsuario, idUsuarioAutenticado));
    }

    @PostMapping("/registrar")
    public ResponseEntity<RespuestaApi<VehiculoEntity>> registrar(@Valid @RequestBody VehiculoRequest request,
                                                                  @RequestHeader("X-User-Id") Integer idUsuarioAutenticado) {
        VehiculoEntity vehiculo = vehiculoService.registrar(request, idUsuarioAutenticado);
        return ResponseEntity.status(201).body(RespuestaApi.ok("Vehiculo registrado correctamente", vehiculo));
    }

    @PutMapping("/editar/{idVehiculo}")
    public RespuestaApi<VehiculoEntity> editar(@PathVariable Integer idVehiculo,
                                               @Valid @RequestBody VehiculoRequest request,
                                               @RequestHeader("X-User-Id") Integer idUsuarioAutenticado) {
        return RespuestaApi.ok("Vehiculo actualizado correctamente",
                vehiculoService.editar(idVehiculo, request, idUsuarioAutenticado));
    }

    @PatchMapping("/estatus/{idVehiculo}")
    public RespuestaApi<VehiculoEntity> cambiarEstatus(@PathVariable Integer idVehiculo,
                                                       @Valid @RequestBody EstatusVehiculoRequest request,
                                                       @RequestHeader("X-User-Id") Integer idUsuarioAutenticado) {
        return RespuestaApi.ok("Estatus del vehiculo actualizado correctamente",
                vehiculoService.cambiarEstatus(idVehiculo, request.getIdUsuario(), request.getActivo(), idUsuarioAutenticado));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarErrorNegocio(IllegalArgumentException exception) {
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("No se pudo completar la operacion", exception.getMessage()));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaApi<Void>> manejarErrorValidacion(MethodArgumentNotValidException exception) {
        String mensaje = exception.getBindingResult().getFieldErrors().stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("Datos invalidos");
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("No se pudo completar la operacion", mensaje));
    }
}
