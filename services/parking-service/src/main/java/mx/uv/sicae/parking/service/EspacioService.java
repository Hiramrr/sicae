package mx.uv.sicae.parking.service;

import org.springframework.stereotype.Service;
import java.util.List;
import mx.uv.sicae.parking.model.Espacio;
import mx.uv.sicae.parking.repository.EspacioRepository;

// @Service le indica a Spring que esta clase pertenece a la capa de "Lógica de Negocio".
// Aunque en este caso el código sea muy corto, mantener esta capa es una excelente práctica
// de arquitectura (separación de responsabilidades) por si en un futuro necesitas agregar
// validaciones o filtros antes de devolver los espacios.
@Service
public class EspacioService {

    // Dependencia hacia la capa de acceso a datos (Base de datos)
    private final EspacioRepository espacioRepository;

    // Inyección de dependencias por constructor. Spring se encarga de instanciar
    // el EspacioRepository (MyBatis) y pasárselo automáticamente a este servicio.
    public EspacioService(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    // Método que es mandado a llamar desde tu EspacioController.
    // Actualmente funciona como un simple "puente" o "pasarela" que delega
    // la petición directamente al repositorio para traer todos los registros de la tabla.
    public List<Espacio> obtenerTodos() {
        return espacioRepository.obtenerTodos();
    }
}
