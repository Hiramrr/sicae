package mx.uv.sicae.parking.service;

import org.springframework.stereotype.Service;
import java.util.List;
import mx.uv.sicae.parking.model.Espacio;
import mx.uv.sicae.parking.repository.EspacioRepository;


@Service
public class EspacioService {

    private final EspacioRepository espacioRepository;

    public EspacioService(EspacioRepository espacioRepository) {
        this.espacioRepository = espacioRepository;
    }

    public List<Espacio> obtenerTodos() {
        return espacioRepository.obtenerTodos();
    }
}
