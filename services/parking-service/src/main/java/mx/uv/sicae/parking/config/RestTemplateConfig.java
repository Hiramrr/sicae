package mx.uv.sicae.parking.config;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class RestTemplateConfig {

    // @Bean permite que este RestTemplate ya configurado pueda ser inyectado automáticamente en otras clases (como tus clientes REST)
    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        // Se agrega un "interceptor". Esto significa que CADA VEZ que ParkingService haga una petición HTTP hacia otro microservicio (ej. VehicleService), pasará por este bloque de código antes de salir.
        restTemplate.getInterceptors().add((request, body, execution) -> {

            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest servletRequest = attributes.getRequest();

                // Extraemos el encabezado "Authorization" (donde viene el token JWT) de esa petición original
                String token = servletRequest.getHeader("Authorization");

                // Si la petición original traía un token, se lo "inyectamos" automáticamente a la NUEVA petición saliente
                if (token != null) {
                    request.getHeaders().add("Authorization", token);
                }
            }

            // Continúa con la ejecución normal de la petición hacia el otro microservicio
            return execution.execute(request, body);
        });

        return restTemplate;
    }
}
