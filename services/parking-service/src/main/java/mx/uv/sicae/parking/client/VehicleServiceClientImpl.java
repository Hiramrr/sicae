package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.Vehiculo;
import mx.uv.sicae.parking.model.RespuestaApi;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleServiceClientImpl implements VehicleServiceClient {

    private static final Logger log = LoggerFactory.getLogger(VehicleServiceClientImpl.class);

    private final RestTemplate restTemplate;
    private final String vehicleServiceUrl;

    public VehicleServiceClientImpl(RestTemplate restTemplate, @Value("${vehicle.service.rest.url}") String vehicleServiceUrl) {
        this.restTemplate = restTemplate;
        this.vehicleServiceUrl = vehicleServiceUrl;
    }

    @Override
    public Vehiculo validarVehiculoPorPlaca(String placa) {
        log.debug("Consultando vehiculo por placa via REST: {}", placa);
        String url = vehicleServiceUrl + "/placa/" + placa;

        try {
            ResponseEntity<RespuestaApi<Vehiculo>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RespuestaApi<Vehiculo>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody().getData();
            }
        } catch (Exception e) {
            log.error("Error al consultar vehiculo por placa {}: {}", placa, e.getMessage());
        }
        return null;
    }

    @Override
    public List<Integer> obtenerIdsVehiculosPorUsuario(Integer idUsuario) {
        log.debug("Consultando vehiculos del usuario via REST: {}", idUsuario);
        String url = vehicleServiceUrl + "/usuario/" + idUsuario;

        try {
            ResponseEntity<RespuestaApi<List<Vehiculo>>> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<RespuestaApi<List<Vehiculo>>>() {}
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null && response.getBody().getData() != null) {
                return response.getBody().getData().stream()
                    .map(Vehiculo::getIdVehiculo)
                    .collect(Collectors.toList());
            }
        } catch (Exception e) {
            log.error("Error al consultar vehiculos por idUsuario {}: {}", idUsuario, e.getMessage());
        }
        return new ArrayList<>();
    }
}
