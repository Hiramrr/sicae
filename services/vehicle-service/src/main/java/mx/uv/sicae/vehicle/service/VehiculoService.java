package mx.uv.sicae.vehicle.service;

import java.util.List;

import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.model.Vehiculo;
import mx.uv.sicae.vehicle.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {
    private static final int MAXIMO_VEHICULOS_ACTIVOS = 4;

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<Vehiculo> buscarPorUsuario(Integer idUsuario, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(idUsuario, idUsuarioAutenticado);
        return vehiculoRepository.buscarPorUsuario(idUsuario);
    }

    public Vehiculo registrar(VehiculoRequest request, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);
        validarModeloExiste(request.getIdModelo());
        validarPlacaDisponible(request.getPlaca(), null);

        int vehiculosActivos = vehiculoRepository.contarActivosPorUsuario(request.getIdUsuario());
        if (vehiculosActivos >= MAXIMO_VEHICULOS_ACTIVOS) {
            throw new IllegalArgumentException("El usuario ya tiene 4 vehiculos activos");
        }

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdUsuario(request.getIdUsuario());
        vehiculo.setClaveVehiculo(generarClaveVehiculo(request.getPlaca()));
        vehiculo.setIdModelo(request.getIdModelo());
        vehiculo.setPlaca(normalizarPlaca(request.getPlaca()));
        vehiculo.setColor(request.getColor().trim());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setDescripcion(normalizarOpcional(request.getDescripcion()));

        vehiculoRepository.registrar(vehiculo);
        return vehiculoRepository.buscarPorId(vehiculo.getIdVehiculo())
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el vehiculo registrado"));
    }

    public Vehiculo editar(Integer idVehiculo, VehiculoRequest request, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);
        validarModeloExiste(request.getIdModelo());

        Vehiculo vehiculoActual = vehiculoRepository.buscarPorId(idVehiculo)
                .orElseThrow(() -> new IllegalArgumentException("El vehiculo no existe"));

        if (!vehiculoActual.getIdUsuario().equals(request.getIdUsuario())) {
            throw new IllegalArgumentException("El vehiculo no pertenece al usuario autenticado");
        }

        validarPlacaDisponible(request.getPlaca(), idVehiculo);

        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(idVehiculo);
        vehiculo.setIdUsuario(request.getIdUsuario());
        vehiculo.setIdModelo(request.getIdModelo());
        vehiculo.setPlaca(normalizarPlaca(request.getPlaca()));
        vehiculo.setColor(request.getColor().trim());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setDescripcion(normalizarOpcional(request.getDescripcion()));

        vehiculoRepository.editar(vehiculo);
        return vehiculoRepository.buscarPorId(idVehiculo)
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el vehiculo actualizado"));
    }

    public Vehiculo cambiarEstatus(Integer idVehiculo, Integer idUsuario, Boolean activo, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(idUsuario, idUsuarioAutenticado);

        Vehiculo vehiculoActual = vehiculoRepository.buscarPorId(idVehiculo)
                .orElseThrow(() -> new IllegalArgumentException("El vehiculo no existe"));

        if (!vehiculoActual.getIdUsuario().equals(idUsuario)) {
            throw new IllegalArgumentException("El vehiculo no pertenece al usuario autenticado");
        }

        if (Boolean.TRUE.equals(activo) && !Boolean.TRUE.equals(vehiculoActual.getEstatus())) {
            int vehiculosActivos = vehiculoRepository.contarActivosPorUsuario(idUsuario);
            if (vehiculosActivos >= MAXIMO_VEHICULOS_ACTIVOS) {
                throw new IllegalArgumentException("El usuario ya tiene 4 vehiculos activos");
            }
        }

        vehiculoRepository.cambiarEstatus(idVehiculo, idUsuario, activo);
        return vehiculoRepository.buscarPorId(idVehiculo)
                .orElseThrow(() -> new IllegalStateException("No se pudo recuperar el vehiculo actualizado"));
    }

    private void validarUsuarioAutenticado(Integer idUsuario, Integer idUsuarioAutenticado) {
        if (idUsuarioAutenticado != null && !idUsuarioAutenticado.equals(idUsuario)) {
            throw new IllegalArgumentException("Solo se pueden modificar vehiculos del usuario autenticado");
        }
    }

    private void validarModeloExiste(Integer idModelo) {
        if (vehiculoRepository.contarModeloActivoPorId(idModelo) == 0) {
            throw new IllegalArgumentException("El modelo indicado no existe o no esta activo");
        }
    }

    private void validarPlacaDisponible(String placa, Integer idVehiculoActual) {
        String placaNormalizada = normalizarPlaca(placa);
        vehiculoRepository.buscarPorPlaca(placaNormalizada).ifPresent(vehiculo -> {
            if (idVehiculoActual == null || !vehiculo.getIdVehiculo().equals(idVehiculoActual)) {
                throw new IllegalArgumentException("Ya existe un vehiculo registrado con la placa indicada");
            }
        });
    }

    private String normalizarPlaca(String placa) {
        return placa.trim().toUpperCase();
    }

    private String normalizarOpcional(String valor) {
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    private String generarClaveVehiculo(String placa) {
        String normalizada = normalizarPlaca(placa).replaceAll("[^A-Z0-9]", "");
        if (normalizada.length() > 7) {
            normalizada = normalizada.substring(0, 7);
        }
        return "V-" + normalizada;
    }
}
