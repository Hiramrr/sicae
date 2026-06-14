package mx.uv.sicae.parking.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.ws.client.core.WebServiceTemplate;

@Configuration
public class SoapClientConfig {

    @Bean
    public Jaxb2Marshaller userValidationMarshaller() {
        Jaxb2Marshaller marshaller = new Jaxb2Marshaller();
        marshaller.setContextPath("mx.uv.sicae.parking.ws");
        return marshaller;
    }

    @Bean
    public WebServiceTemplate userValidationWebServiceTemplate(Jaxb2Marshaller userValidationMarshaller) {
        WebServiceTemplate template = new WebServiceTemplate();
        template.setMarshaller(userValidationMarshaller);
        template.setUnmarshaller(userValidationMarshaller);
        template.setDefaultUri("http://user-service:8082/ws");
        return template;
    }
}
