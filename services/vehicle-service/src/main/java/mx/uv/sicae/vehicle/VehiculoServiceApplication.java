package mx.uv.sicae.vehicle;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("mx.uv.sicae.vehicle.repository")
public class VehiculoServiceApplication {

    public static void main(String[] args) {
        // Punto de arranque del microservicio de vehiculos
        SpringApplication.run(VehiculoServiceApplication.class, args);
    }
}
