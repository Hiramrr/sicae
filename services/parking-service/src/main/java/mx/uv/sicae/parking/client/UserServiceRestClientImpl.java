package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.RespuestaApi;
import mx.uv.sicae.parking.model.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class UserServiceRestClientImpl {

    private static final Logger log = LoggerFactory.getLogger(UserServiceRestClientImpl.class);

    private final RestTemplate restTemplate;
    private final String userServiceRestUrl;

    public UserServiceRestClientImpl(RestTemplate restTemplate,
                                     @Value("${user.service.rest.url}") String userServiceRestUrl) {
        this.restTemplate = restTemplate;
        this.userServiceRestUrl = userServiceRestUrl;
    }

    public Usuario obtenerPerfil(Integer idUsuario) {
        String url = userServiceRestUrl + "/" + idUsuario;
        log.debug("Consultando perfil de usuario via REST: {}", idUsuario);

        try {
            ResponseEntity<RespuestaApi<Usuario>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RespuestaApi<Usuario>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            log.error("Error al consultar perfil del usuario {}: {}", idUsuario, e.getMessage());
        }
        return null;
    }
}
