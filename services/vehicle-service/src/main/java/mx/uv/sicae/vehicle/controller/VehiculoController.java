package mx.uv.sicae.vehicle.controller;

import java.util.List;

import mx.uv.sicae.vehicle.dto.EstatusVehiculoRequest;
import mx.uv.sicae.vehicle.dto.RespuestaApi;
import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.dto.VehiculoResponse;
import mx.uv.sicae.vehicle.config.JwtUtil;
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
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<RespuestaApi<List<VehiculoResponse>>> buscarPorUsuario(
            @PathVariable Integer idUsuario,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            List<VehiculoResponse> vehiculos = vehiculoService.buscarPorUsuario(idUsuario, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculos consultados correctamente", vehiculos));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudieron consultar los vehiculos", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Ocurrio un error al consultar los vehiculos", e.getMessage()));
        }
    }

    @PostMapping("/registrar")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> registrar(
            @RequestBody(required = false) VehiculoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.registrar(request, idUsuarioAutenticado);
            return ResponseEntity.status(201).body(RespuestaApi.ok("Vehiculo registrado correctamente", vehiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo registrar el vehiculo", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Ocurrio un error al registrar el vehiculo", e.getMessage()));
        }
    }

    @PutMapping("/editar/{idVehiculo}")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> editar(
            @PathVariable Integer idVehiculo,
            @RequestBody(required = false) VehiculoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.editar(idVehiculo, request, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculo actualizado correctamente", vehiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo actualizar el vehiculo", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Ocurrio un error al actualizar el vehiculo", e.getMessage()));
        }
    }

    @PatchMapping("/estatus/{idVehiculo}")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> cambiarEstatus(
            @PathVariable Integer idVehiculo,
            @RequestBody(required = false) EstatusVehiculoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.cambiarEstatus(idVehiculo, request, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Estatus del vehiculo actualizado correctamente", vehiculo));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo cambiar el estatus del vehiculo", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Ocurrio un error al cambiar el estatus del vehiculo", e.getMessage()));
        }
    }
}
