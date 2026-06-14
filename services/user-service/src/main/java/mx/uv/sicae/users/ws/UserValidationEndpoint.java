package mx.uv.sicae.users.ws;

import mx.uv.sicae.users.model.UsuarioPerfil;
import mx.uv.sicae.users.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class UserValidationEndpoint {

    private static final Logger log = LoggerFactory.getLogger(UserValidationEndpoint.class);
    private static final String NAMESPACE_URI = "http://sicae.uv.mx/users/validation";

    private final UsuarioRepository usuarioRepository;

    public UserValidationEndpoint(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "validarUsuarioPorClaveRequest")
    @ResponsePayload
    public ValidarUsuarioPorClaveResponse validarUsuarioPorClave(
            @RequestPayload ValidarUsuarioPorClaveRequest request) {

        String claveUsuario = request.getClaveUsuario();
        log.debug("SOAP request: validarUsuarioPorClave(claveUsuario={})", claveUsuario);

        UsuarioPerfil usuario = usuarioRepository.buscarPorClaveUsuario(claveUsuario);

        ValidarUsuarioPorClaveResponse response = new ValidarUsuarioPorClaveResponse();
        if (usuario == null) {
            response.setIdUsuario(0);
            response.setClaveUsuario(claveUsuario);
            response.setNombreCompleto("");
            response.setActivo(false);
            response.setRol("");
            response.setTipoUsuario("");
            log.warn("Usuario no encontrado con clave: {}", claveUsuario);
        } else {
            response.setIdUsuario(usuario.getIdUsuario());
            response.setClaveUsuario(usuario.getClaveUsuario());
            response.setNombreCompleto(usuario.getNombreCompleto());
            response.setActivo(usuario.getEstatus() != null && usuario.getEstatus());
            response.setRol(usuario.getRol());
            response.setTipoUsuario(usuario.getTipoUsuario());
            log.debug("Usuario encontrado: idUsuario={}, activo={}", usuario.getIdUsuario(), usuario.getEstatus());
        }

        return response;
    }
}
