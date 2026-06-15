package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.Usuario;
import mx.uv.sicae.parking.ws.ValidarUsuarioPorClaveRequest;
import mx.uv.sicae.parking.ws.ValidarUsuarioPorClaveResponse;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class UserServiceClientImpl implements UserServiceClient {

    private final WebServiceTemplate webServiceTemplate;

    public UserServiceClientImpl(@Qualifier("userValidationWebServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public Usuario validarUsuario(String claveUsuario) {
        ValidarUsuarioPorClaveRequest request = new ValidarUsuarioPorClaveRequest();
        request.setClaveUsuario(claveUsuario);

        try {
            ValidarUsuarioPorClaveResponse response = (ValidarUsuarioPorClaveResponse)
                webServiceTemplate.marshalSendAndReceive(request);

            Usuario usuario = new Usuario();
            usuario.setIdUsuario(response.getIdUsuario());
            usuario.setClaveUsuario(response.getClaveUsuario());
            usuario.setActivo(response.isActivo());
            usuario.setNombreCompleto(response.getNombreCompleto());
            usuario.setRol(response.getRol());
            usuario.setTipoUsuario(response.getTipoUsuario());

            return usuario;

        } catch (Exception e) {
            return null;
        }
    }
}
