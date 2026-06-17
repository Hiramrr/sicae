package mx.uv.sicae.users;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("mx.uv.sicae.users.repository")
public class UserServiceApplication {

	// Punto de entrada de la aplicacion. Arranca Spring Boot y escanea los mappers de MyBatis.
	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

}
