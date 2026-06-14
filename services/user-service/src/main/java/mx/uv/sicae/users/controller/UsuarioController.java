package mx.uv.sicae.users.controller;

import java.util.List;

import mx.uv.sicae.users.config.JwtUtil;
import mx.uv.sicae.users.dto.CambiarEstatusRequest;
import mx.uv.sicae.users.dto.EditarUsuarioRequest;
import mx.uv.sicae.users.dto.RegistrarUsuarioRequest;
import mx.uv.sicae.users.dto.RespuestaApi;
import mx.uv.sicae.users.dto.UsuarioResponse;
import mx.uv.sicae.users.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;
    private final JwtUtil jwtUtil;

    public UsuarioController(UsuarioService usuarioService, JwtUtil jwtUtil) {
        this.usuarioService = usuarioService;
        this.jwtUtil = jwtUtil;
    }

    @PostMapping
    public ResponseEntity<RespuestaApi<UsuarioResponse>> registrar(
            @RequestBody RegistrarUsuarioRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        UsuarioResponse usuario = usuarioService.crearUsuario(request, idRolAutenticado);
        log.info("Usuario registrado: idUsuario={}, username={}", usuario.getIdUsuario(), usuario.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.ok("Usuario registrado correctamente", usuario));
    }

    @GetMapping
    public ResponseEntity<RespuestaApi<List<UsuarioResponse>>> listar(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        List<UsuarioResponse> usuarios = usuarioService.listarUsuarios(idRolAutenticado);
        log.debug("Usuarios consultados: {} registros", usuarios.size());
        return ResponseEntity.ok(RespuestaApi.ok("Usuarios consultados correctamente", usuarios));
    }

    @PutMapping("/{idUsuario}")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> editar(
            @PathVariable Integer idUsuario,
            @RequestBody EditarUsuarioRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idUsuarioAutenticado = jwtUtil.extraerIdUsuario(token);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        UsuarioResponse usuario = usuarioService.editarUsuario(idUsuario, request, idUsuarioAutenticado, idRolAutenticado);
        log.info("Usuario editado: idUsuario={}", idUsuario);
        return ResponseEntity.ok(RespuestaApi.ok("Usuario actualizado correctamente", usuario));
    }

    @GetMapping("/{idUsuario}")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> obtenerPerfil(
            @PathVariable Integer idUsuario,
            @RequestHeader("Authorization") String authorizationHeader) {
        extraerToken(authorizationHeader);
        UsuarioResponse usuario = usuarioService.obtenerPerfil(idUsuario);
        log.debug("Perfil consultado: idUsuario={}", idUsuario);
        return ResponseEntity.ok(RespuestaApi.ok("Perfil consultado correctamente", usuario));
    }

    @PatchMapping("/{idUsuario}/status")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> cambiarEstatus(
            @PathVariable Integer idUsuario,
            @RequestBody CambiarEstatusRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idUsuarioAutenticado = jwtUtil.extraerIdUsuario(token);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        UsuarioResponse usuario = usuarioService.cambiarEstatus(
                idUsuario,
                request,
                idUsuarioAutenticado,
                idRolAutenticado);
        log.info("Estatus cambiado: idUsuario={}, estatus={}", idUsuario, usuario.getEstatus());
        return ResponseEntity.ok(RespuestaApi.ok("Estatus del usuario actualizado correctamente", usuario));
    }

    private String extraerToken(String authorizationHeader) {
        if (authorizationHeader == null || authorizationHeader.trim().isEmpty()) {
            throw new io.jsonwebtoken.JwtException("Authorization Bearer token es obligatorio");
        }
        if (!authorizationHeader.startsWith("Bearer ")) {
            throw new io.jsonwebtoken.JwtException("Authorization debe usar el formato Bearer <token>");
        }
        String token = authorizationHeader.substring(7).trim();
        if (token.isEmpty()) {
            throw new io.jsonwebtoken.JwtException("Token JWT es obligatorio");
        }
        jwtUtil.validarToken(token);
        return token;
    }
}
