package mx.uv.sicae.users.controller;

import io.jsonwebtoken.JwtException;
import mx.uv.sicae.users.config.JwtUtil;
import mx.uv.sicae.users.dto.CambiarEstatusRequest;
import mx.uv.sicae.users.dto.EditarUsuarioRequest;
import mx.uv.sicae.users.dto.RegistrarUsuarioRequest;
import mx.uv.sicae.users.dto.RespuestaApi;
import mx.uv.sicae.users.dto.UsuarioResponse;
import mx.uv.sicae.users.service.UsuarioService;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/users")
public class UsuarioController {
    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<RespuestaApi<UsuarioResponse>> registrar(
            @RequestBody(required = false) RegistrarUsuarioRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            String token = extraerToken(authorizationHeader);
            Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
            UsuarioResponse usuario = usuarioService.crearUsuario(request, idRolAutenticado);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(RespuestaApi.ok("Usuario registrado correctamente", usuario));
        } catch (JwtException e) {
            return noAutorizado(e);
        } catch (SecurityException e) {
            return prohibido(e);
        } catch (Exception e) {
            return solicitudIncorrecta(e);
        }
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> editar(
            @PathVariable Integer idUsuario,
            @RequestBody(required = false) EditarUsuarioRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            extraerToken(authorizationHeader);
            UsuarioResponse usuario = usuarioService.editarUsuario(idUsuario, request);
            return ResponseEntity.ok(RespuestaApi.ok("Usuario actualizado correctamente", usuario));
        } catch (JwtException e) {
            return noAutorizado(e);
        } catch (SecurityException e) {
            return prohibido(e);
        } catch (Exception e) {
            return solicitudIncorrecta(e);
        }
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> obtenerPerfil(
            @PathVariable Integer idUsuario,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            extraerToken(authorizationHeader);
            UsuarioResponse usuario = usuarioService.obtenerPerfil(idUsuario);
            return ResponseEntity.ok(RespuestaApi.ok("Perfil consultado correctamente", usuario));
        } catch (JwtException e) {
            return noAutorizado(e);
        } catch (SecurityException e) {
            return prohibido(e);
        } catch (Exception e) {
            return solicitudIncorrecta(e);
        }
    }

    @PatchMapping("/{idUsuario}/status")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> cambiarEstatus(
            @PathVariable Integer idUsuario,
            @RequestBody(required = false) CambiarEstatusRequest request,
            @RequestHeader(value = "Authorization", required = false) String authorizationHeader) {
        try {
            String token = extraerToken(authorizationHeader);
            Integer idUsuarioAutenticado = jwtUtil.extraerIdUsuario(token);
            Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
            UsuarioResponse usuario = usuarioService.cambiarEstatus(
                    idUsuario,
                    request,
                    idUsuarioAutenticado,
                    idRolAutenticado);
            return ResponseEntity.ok(RespuestaApi.ok("Estatus del usuario actualizado correctamente", usuario));
        } catch (JwtException e) {
            return noAutorizado(e);
        } catch (SecurityException e) {
            return prohibido(e);
        } catch (Exception e) {
            return solicitudIncorrecta(e);
        }
    }

    private String extraerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new JwtException("Authorization Bearer token es obligatorio");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new JwtException("Authorization debe usar el formato Bearer <token>");
        }
        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new JwtException("Token JWT es obligatorio");
        }
        jwtUtil.validarToken(token);
        return token;
    }

    private ResponseEntity<RespuestaApi<UsuarioResponse>> noAutorizado(Exception e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(RespuestaApi.fail("No autorizado", e.getMessage()));
    }

    private ResponseEntity<RespuestaApi<UsuarioResponse>> prohibido(Exception e) {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(RespuestaApi.fail("Operacion no permitida", e.getMessage()));
    }

    private ResponseEntity<RespuestaApi<UsuarioResponse>> solicitudIncorrecta(Exception e) {
        return ResponseEntity.badRequest()
                .body(RespuestaApi.fail("No se pudo completar la operacion", e.getMessage()));
    }
}
