package mx.uv.sicae.auth.service;

import mx.uv.sicae.auth.config.JwtUtil;
import mx.uv.sicae.auth.model.LoginRequest;
import mx.uv.sicae.auth.model.LoginResponse;
import mx.uv.sicae.auth.model.UsuarioAuth;
import mx.uv.sicae.auth.repository.AuthRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.util.Optional;

/*En este servicio validamos las reglas de negocio:
    1 . Que no haya campos vacios
    2. Revisar el estatus
    3. Comprobar bcrypto
    4. Generar la respuesta

*/

@Service
public class AuthService {

    private final AuthRepository authRepository;
    private final JwtUtil jwtUtil;

    public AuthService(AuthRepository authRepository, JwtUtil jwtUtil) {
        this.authRepository = authRepository;
        this.jwtUtil = jwtUtil;
    }

    public LoginResponse login(LoginRequest request) {
        // Validamos campos
        if (request.getUsuario() == null || request.getUsuario().trim().isEmpty() ||
            request.getContrasena() == null || request.getContrasena().trim().isEmpty()) {
            throw new IllegalArgumentException("Usuario y contraseña son obligatorios.");
        }

        // El username en la BD tiene un límite de 30 caracteres
        if (request.getUsuario().length() > 30) {
            throw new IllegalArgumentException("El usuario no puede exceder los 30 caracteres.");
        }
        // Validamos longitud de la contraseña
        if (request.getContrasena().length() < 4 || request.getContrasena().length() > 50) {
            throw new IllegalArgumentException("La longitud de la contraseña no es válida.");
        }

        //Buscamos si el usuario existe
        Optional<UsuarioAuth> usuarioOpt = authRepository.buscarPorUsername(request.getUsuario());

        if (usuarioOpt.isEmpty()) {
            //Un mensaje generico para no dar detalles a posibles ataques
            throw new IllegalArgumentException("Usuario o contraseña incorrectos."); 
        }

        UsuarioAuth usuario = usuarioOpt.get();

        // Solo usuarios activos
        // La bd nos puede arrojar 1, true o t
        if (usuario.getEstatus() == null || usuario.getEstatus().equals("0") || 
            usuario.getEstatus().equalsIgnoreCase("false") || usuario.getEstatus().equalsIgnoreCase("f")) {
            throw new IllegalArgumentException("El usuario se encuentra inactivo.");
        }

        //Validar la contraseña cifrada comparando hashes
        if (!BCrypt.checkpw(request.getContrasena(), usuario.getPassword())) {
            throw new IllegalArgumentException("Usuario o contraseña incorrectos.");
        }

        //generamos el Token JWT
        String token = jwtUtil.generarToken(usuario);

        //DTO de respuesta para el cliente
        LoginResponse response = new LoginResponse();
        response.setIdUsuario(usuario.getIdUsuario());
        response.setIdRol(usuario.getIdRol());
        response.setRol(usuario.getRol());
        response.setUsuario(usuario.getUsername());
        response.setNombreCompleto(usuario.getNombreCompleto());
        response.setIdTipoUsuario(usuario.getIdTipoUsuario());
        response.setTipoUsuario(usuario.getTipoUsuario());
        response.setToken(token);

        return response;
    }
}
