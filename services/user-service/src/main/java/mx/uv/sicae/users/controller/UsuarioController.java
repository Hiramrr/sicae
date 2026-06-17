package mx.uv.sicae.users.controller;

import java.util.List;

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

    // Registra un usuario nuevo. Solo el admin puede hacerlo.
    @PostMapping
    public ResponseEntity<RespuestaApi<UsuarioResponse>> registrar(
            @RequestBody RegistrarUsuarioRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        UsuarioResponse usuario = usuarioService.crearUsuario(request, idRolAutenticado);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(RespuestaApi.ok("Usuario registrado correctamente", usuario));
    }

    // Devuelve todos los usuarios. Solo el admin puede verlos.
    @GetMapping
    public ResponseEntity<RespuestaApi<List<UsuarioResponse>>> listar(
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        List<UsuarioResponse> usuarios = usuarioService.listarUsuarios(idRolAutenticado);
        return ResponseEntity.ok(RespuestaApi.ok("Usuarios consultados correctamente", usuarios));
    }

    // Edita los datos de un usuario. Puede hacerlo el admin o el propio usuario.
    @PutMapping("/{idUsuario}")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> editar(
            @PathVariable Integer idUsuario,
            @RequestBody EditarUsuarioRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idUsuarioAutenticado = jwtUtil.extraerIdUsuario(token);
        Integer idRolAutenticado = jwtUtil.extraerIdRol(token);
        UsuarioResponse usuario = usuarioService.editarUsuario(idUsuario, request, idUsuarioAutenticado, idRolAutenticado);
        return ResponseEntity.ok(RespuestaApi.ok("Usuario actualizado correctamente", usuario));
    }

    // Obtiene el perfil de un usuario por su id. Cualquier usuario autenticado.
    @GetMapping("/{idUsuario}")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> obtenerPerfil(
            @PathVariable Integer idUsuario,
            @RequestHeader("Authorization") String authorizationHeader) {
        extraerToken(authorizationHeader);
        UsuarioResponse usuario = usuarioService.obtenerPerfil(idUsuario);
        return ResponseEntity.ok(RespuestaApi.ok("Perfil consultado correctamente", usuario));
    }

    // Activa o desactiva un usuario. Solo el admin, y no puede desactivarse a si mismo.
    @PatchMapping("/{idUsuario}/status")
    public ResponseEntity<RespuestaApi<UsuarioResponse>> cambiarEstatus(
            @PathVariable Integer idUsuario,
            @RequestBody CambiarEstatusRequest request,
            @RequestHeader("Authorization") String authorizationHeader) {
        String token = extraerToken(authorizationHeader);
        Integer idUsuarioAutenticado = jwtUtil.extraerIdUsuario(token);
        Integer idRolToken = jwtUtil.extraerIdRol(token);
        UsuarioResponse usuario = usuarioService.cambiarEstatus(
                idUsuario,
                request,
                idUsuarioAutenticado,
                idRolToken);
        return ResponseEntity.ok(RespuestaApi.ok("Estatus del usuario actualizado correctamente", usuario));
    }

    // Saca el token del header "Authorization: Bearer <token>" y lo valida.
    // Si algo falla lanza JwtException.
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
