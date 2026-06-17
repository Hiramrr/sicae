package mx.uv.sicae.users.ws;

import mx.uv.sicae.users.model.UsuarioPerfil;
import mx.uv.sicae.users.repository.UsuarioRepository;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;

@Endpoint
public class UserValidationEndpoint {

    private static final String NAMESPACE_URI = "http://sicae.uv.mx/users/validation";

    private final UsuarioRepository usuarioRepository;

    public UserValidationEndpoint(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    // Busca un usuario por su clave (RGR-XXXXXX) y devuelve sus datos via SOAP.
    // Si no existe, regresa valores por defecto (id=0, activo=false).
    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "validarUsuarioPorClaveRequest")
    @ResponsePayload
    public ValidarUsuarioPorClaveResponse validarUsuarioPorClave(
            @RequestPayload ValidarUsuarioPorClaveRequest request) {

        String claveUsuario = request.getClaveUsuario();

        UsuarioPerfil usuario = usuarioRepository.buscarPorClaveUsuario(claveUsuario);

        ValidarUsuarioPorClaveResponse response = new ValidarUsuarioPorClaveResponse();
        if (usuario == null) {
            response.setIdUsuario(0);
            response.setClaveUsuario(claveUsuario);
            response.setNombreCompleto("");
            response.setActivo(false);
            response.setRol("");
            response.setTipoUsuario("");
        } else {
            response.setIdUsuario(usuario.getIdUsuario());
            response.setClaveUsuario(usuario.getClaveUsuario());
            response.setNombreCompleto(usuario.getNombreCompleto());
            response.setActivo(usuario.getEstatus() != null && usuario.getEstatus());
            response.setRol(usuario.getRol());
            response.setTipoUsuario(usuario.getTipoUsuario());
        }

        return response;
    }
}
