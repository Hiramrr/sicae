package mx.uv.sicae.parking.config;

import mx.uv.sicae.parking.ws.ValidarUsuarioPorClaveRequest;
import mx.uv.sicae.parking.ws.ValidarUsuarioPorClaveResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

// Le indica a Spring que esta clase es de configuracion y debe instanciar los @Bean al iniciar la aplicacion
@Configuration
public class SoapClientConfig {

    // Lee la URL del endpoint SOAP del microservicio de usuarios desde el archivo application.properties
    @Value("${user.service.soap.url}")
    private String userServiceSoapUrl;

    // Configura el Marshaller, que es el encargado de traducir (serializar) los objetos Java a XML y viceversa
    @Bean
    public Jaxb2Marshaller userValidationMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();

        // Se le indica exactamente que clases (generadas para el contrato SOAP) va a estar transformando
        marshaller.setClassesToBeBound(
            ValidarUsuarioPorClaveRequest.class,
            ValidarUsuarioPorClaveResponse.class
        );
        return marshaller;
    }

    // Crea el cliente SOAP (WebServiceTemplate) que se inyectara en los servicios para hacer las peticiones
    @Bean
    public WebServiceTemplate userValidationWebServiceTemplate(Jaxb2Marshaller userValidationMarshaller) {
        WebServiceTemplate template = new WebServiceTemplate();

        // Se le asigna el traductor XML que configuramos en el @Bean anterior tanto para la ida como para el regreso
        template.setMarshaller(userValidationMarshaller);
        template.setUnmarshaller(userValidationMarshaller);

        // Se establece la ruta por defecto hacia el UserService para no tener que declararla en cada peticion
        template.setDefaultUri(userServiceSoapUrl);

        return template;
    }
}
