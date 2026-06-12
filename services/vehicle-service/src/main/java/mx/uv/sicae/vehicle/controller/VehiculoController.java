package mx.uv.sicae.vehicle.controller;

import java.util.List;

import mx.uv.sicae.vehicle.dto.EstatusVehiculoRequest;
import mx.uv.sicae.vehicle.dto.RespuestaApi;
import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.entity.VehiculoEntity;
import mx.uv.sicae.vehicle.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    @Autowired
    private VehiculoService vehiculoService;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<RespuestaApi<List<VehiculoEntity>>> buscarPorUsuario(
            @PathVariable Integer idUsuario,
            @RequestHeader(value = "X-User-Id", required = false) Integer idUsuarioAutenticado) {
        try {
            List<VehiculoEntity> vehiculos = vehiculoService.buscarPorUsuario(idUsuario, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculos consultados correctamente", vehiculos));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<RespuestaApi<VehiculoEntity>> registrar(
            @RequestBody(required = false) VehiculoRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Integer idUsuarioAutenticado) {
        try {
            VehiculoEntity vehiculo = vehiculoService.registrar(request, idUsuarioAutenticado);
            return ResponseEntity.status(201).body(RespuestaApi.ok("Vehiculo registrado correctamente", vehiculo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
        }
    }

    @PutMapping("/editar/{idVehiculo}")
    public ResponseEntity<RespuestaApi<VehiculoEntity>> editar(
            @PathVariable Integer idVehiculo,
            @RequestBody(required = false) VehiculoRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Integer idUsuarioAutenticado) {
        try {
            VehiculoEntity vehiculo = vehiculoService.editar(idVehiculo, request, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculo actualizado correctamente", vehiculo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
        }
    }

    @PatchMapping("/estatus/{idVehiculo}")
    public ResponseEntity<RespuestaApi<VehiculoEntity>> cambiarEstatus(
            @PathVariable Integer idVehiculo,
            @RequestBody(required = false) EstatusVehiculoRequest request,
            @RequestHeader(value = "X-User-Id", required = false) Integer idUsuarioAutenticado) {
        try {
            VehiculoEntity vehiculo = vehiculoService.cambiarEstatus(idVehiculo, request, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Estatus del vehiculo actualizado correctamente", vehiculo));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
        }
    }
}
