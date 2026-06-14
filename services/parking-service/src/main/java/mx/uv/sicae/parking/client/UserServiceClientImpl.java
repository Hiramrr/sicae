package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.Usuario;
import mx.uv.sicae.parking.ws.ValidarUsuarioPorClaveRequest;
import mx.uv.sicae.parking.ws.ValidarUsuarioPorClaveResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceTemplate;

@Service
public class UserServiceClientImpl implements UserServiceClient {

    private static final Logger log = LoggerFactory.getLogger(UserServiceClientImpl.class);

    private final WebServiceTemplate webServiceTemplate;

    public UserServiceClientImpl(@Qualifier("userValidationWebServiceTemplate") WebServiceTemplate webServiceTemplate) {
        this.webServiceTemplate = webServiceTemplate;
    }

    @Override
    public Usuario validarUsuario(String claveUsuario) {
        log.debug("Consultando usuario por clave via SOAP: {}", claveUsuario);

        ValidarUsuarioPorClaveRequest request = new ValidarUsuarioPorClaveRequest();
        request.setClaveUsuario(claveUsuario);

        try {
            ValidarUsuarioPorClaveResponse response = (ValidarUsuarioPorClaveResponse)
                    webServiceTemplate.marshalSendAndReceive(
                            "http://user-service:8082/ws",
                            request);

            Usuario usuario = new Usuario();
            usuario.setIdUsuario(response.getIdUsuario());
            usuario.setClaveUsuario(response.getClaveUsuario());
            usuario.setActivo(response.isActivo());

            log.debug("Respuesta SOAP: idUsuario={}, activo={}", response.getIdUsuario(), response.isActivo());
            return usuario;

        } catch (Exception e) {
            log.error("Error al consultar usuario por clave {}: {}", claveUsuario, e.getMessage());
            return null;
        }
    }
}
