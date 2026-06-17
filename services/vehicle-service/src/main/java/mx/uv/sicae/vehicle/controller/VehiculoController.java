package mx.uv.sicae.vehicle.controller;

import java.util.List;

import mx.uv.sicae.vehicle.dto.EstatusVehiculoRequest;
import mx.uv.sicae.vehicle.dto.RespuestaApi;
import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.dto.VehiculoResponse;
import mx.uv.sicae.vehicle.config.JwtUtil;
import mx.uv.sicae.vehicle.service.VehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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
    // El controlador solo recibe las peticiones y deja la logica fuerte al servicio
    @Autowired
    private VehiculoService vehiculoService;
    @Autowired
    private JwtUtil jwtUtil;

    // Edpoint para buscar todos los vehiculos de un usuario, se necesita el idUsuario para saber de quien buscar y el token para validar permisos.
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<RespuestaApi<List<VehiculoResponse>>> buscarPorUsuario(
            @PathVariable Integer idUsuario,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // se valida el token antes de dejar consultar los vehiculos del usuario
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            List<VehiculoResponse> vehiculos = vehiculoService.buscarPorUsuario(idUsuario, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculos consultados correctamente", vehiculos));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RespuestaApi.fail("No autorizado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Validaciones de datos o token invalido
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudieron consultar los vehiculos", e.getMessage()));
        } catch (Exception e) {
            // Error no contemplado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespuestaApi.fail("Error interno al consultar los vehiculos", "Intente nuevamente mas tarde"));
        }
    }

    // Esta ruta cubre el caso cuando el cliente olvida enviar el idUsuario en la consulta
    @GetMapping({"/usuario", "/usuario/"})
    public ResponseEntity<RespuestaApi<List<VehiculoResponse>>> buscarPorUsuarioSinId() {
        // si falta el id, respondo claro sin mandar la peticion al servicio
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("Datos de entrada invalidos", "idUsuario es obligatorio"));
    }

    // Edpoint para buscar un vehicuo y saber si esta activo, ya que lo ocupa parking-service.
    @GetMapping("/placa/{placa}")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> buscarPorPlaca(
            @PathVariable String placa,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // la placa se busca solo si el token confirma quien esta consultando
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.buscarPorPlaca(placa, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculo consultado correctamente", vehiculo));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RespuestaApi.fail("No autorizado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("No se pudo consultar el vehiculo", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespuestaApi.fail("Error interno al consultar el vehiculo", "Intente nuevamente mas tarde"));
        }
    }

    // Esta ruta cubre el caso cuando el cliente olvida enviar la placa en la consulta
    @GetMapping({"/placa", "/placa/"})
    public ResponseEntity<RespuestaApi<VehiculoResponse>> buscarPorPlacaSinPlaca() {
        // esta ruta cubre el caso cuando el cliente olvida enviar la placa
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("Datos de entrada invalidos", "placa es obligatoria"));
    }
 
    // Edpoint para registrar un nuevo vehiculo
    @PostMapping("/registrar")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> registrar(
            @RequestBody(required = false) VehiculoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // al registrar, el usuario del token debe coincidir con el del cuerpo
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.registrar(request, idUsuarioAutenticado);
            return ResponseEntity.status(201).body(RespuestaApi.ok("Vehiculo registrado correctamente", vehiculo));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RespuestaApi.fail("No autorizado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Validaciones de datos o token invalido
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Datos de entrada invalidos", e.getMessage()));
        } catch (Exception e) {
            // Error no contemplado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespuestaApi.fail("Error interno al registrar el vehiculo", "Intente nuevamente mas tarde"));
        }
    }

    // Edpoint para recordar que el idVehiculo es necesario en la ruta de editar.
    @PutMapping({"/editar", "/editar/"})
    public ResponseEntity<RespuestaApi<VehiculoResponse>> editarSinId() {
        // Para editar siempre se necesita saber que vehiculo se va a mover.
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("Datos de entrada invalidos", "idVehiculo es obligatorio"));
    }

    // Edpoint para editar un vehiculo, solo se pueden cambiar los datos, no el usuario ni el estatus.
    @PutMapping("/editar/{idVehiculo}")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> editar(
            @PathVariable Integer idVehiculo,
            @RequestBody(required = false) VehiculoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // el servicio valida los datos nuevos y tambien que el vehiculo sea del usuario
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.editar(idVehiculo, request, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Vehiculo actualizado correctamente", vehiculo));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RespuestaApi.fail("No autorizado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Validaciones de datos o token invalido
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Datos de entrada invalidos", e.getMessage()));
        } catch (Exception e) {
            // Error no contemplado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespuestaApi.fail("Error interno al actualizar el vehiculo", "Intente nuevamente mas tarde"));
        }
    }

    // Edpoint para recordar que el idVehiculo es necesario en la ruta de cambiar estatus.
    @PatchMapping({"/estatus", "/estatus/"})
    public ResponseEntity<RespuestaApi<VehiculoResponse>> cambiarEstatusSinId() {
        // cambiar estatus sin id no tiene sentido, por eso se corta aqui
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("Datos de entrada invalidos", "idVehiculo es obligatorio"));
    }

    // Edpoint para cambiar el estatus del vehiculo, se puede usar para activar o desactivar sin borrar el registro.
    @PatchMapping("/estatus/{idVehiculo}")
    public ResponseEntity<RespuestaApi<VehiculoResponse>> cambiarEstatus(
            @PathVariable Integer idVehiculo,
            @RequestBody(required = false) EstatusVehiculoRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            // este endpoint sirve para activar o desactivar el vehiculo
            Integer idUsuarioAutenticado = jwtUtil.obtenerIdUsuario(authorizationHeader);
            VehiculoResponse vehiculo = vehiculoService.cambiarEstatus(idVehiculo, request, idUsuarioAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Estatus del vehiculo actualizado correctamente", vehiculo));
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(RespuestaApi.fail("No autorizado", e.getMessage()));
        } catch (IllegalArgumentException e) {
            // Validaciones de datos o token invalido
            return ResponseEntity.badRequest()
                    .body(RespuestaApi.fail("Datos de entrada invalidos", e.getMessage()));
        } catch (Exception e) {
            // Error no contemplado
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(RespuestaApi.fail("Error interno al cambiar el estatus del vehiculo", "Intente nuevamente mas tarde"));
        }
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<RespuestaApi<Object>> manejarJsonInvalido(HttpMessageNotReadableException e) {
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("Datos de entrada invalidos",
                        "Los campos idUsuario e idModelo deben ser numeros enteros"));
    }
}
