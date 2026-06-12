package mx.uv.sicae.vehicle.service;

import java.util.List;
import java.util.Optional;

import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.entity.VehiculoEntity;
import mx.uv.sicae.vehicle.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {
    private static final int MAXIMO_VEHICULOS_ACTIVOS = 4;

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<VehiculoEntity> buscarPorUsuario(Integer idUsuario, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(idUsuario, idUsuarioAutenticado);
        return vehiculoRepository.buscarPorUsuario(idUsuario);
    }

    public VehiculoEntity registrar(VehiculoRequest request, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);
        validarModeloExiste(request.getIdModelo());
        validarPlacaDisponible(request.getPlaca(), null);

        int vehiculosActivos = vehiculoRepository.contarActivosPorUsuario(request.getIdUsuario());
        if (vehiculosActivos >= MAXIMO_VEHICULOS_ACTIVOS) {
            throw new IllegalArgumentException("El usuario ya tiene 4 vehiculos activos");
        }

        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setIdUsuario(request.getIdUsuario());
        vehiculo.setClaveVehiculo(generarClaveVehiculo(request.getPlaca()));
        vehiculo.setIdModelo(request.getIdModelo());
        vehiculo.setPlaca(normalizarPlaca(request.getPlaca()));
        vehiculo.setColor(request.getColor().trim());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setDescripcion(normalizarOpcional(request.getDescripcion()));

        vehiculoRepository.registrar(vehiculo);

        Optional<VehiculoEntity> vehiculoRegistrado = vehiculoRepository.buscarPorId(vehiculo.getIdVehiculo());
        if (vehiculoRegistrado.isPresent()) {
            return vehiculoRegistrado.get();
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo registrado");
        }
    }

    public VehiculoEntity editar(Integer idVehiculo, VehiculoRequest request, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);
        validarModeloExiste(request.getIdModelo());

        Optional<VehiculoEntity> resultado = vehiculoRepository.buscarPorId(idVehiculo);
        if (!resultado.isPresent()) {
            throw new IllegalArgumentException("El vehiculo no existe");
        }
        VehiculoEntity vehiculoActual = resultado.get();

        if (!vehiculoActual.getIdUsuario().equals(request.getIdUsuario())) {
            throw new IllegalArgumentException("El vehiculo no pertenece al usuario autenticado");
        }

        validarPlacaDisponible(request.getPlaca(), idVehiculo);

        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setIdVehiculo(idVehiculo);
        vehiculo.setIdUsuario(request.getIdUsuario());
        vehiculo.setIdModelo(request.getIdModelo());
        vehiculo.setPlaca(normalizarPlaca(request.getPlaca()));
        vehiculo.setColor(request.getColor().trim());
        vehiculo.setAnio(request.getAnio());
        vehiculo.setDescripcion(normalizarOpcional(request.getDescripcion()));

        vehiculoRepository.editar(vehiculo);

        Optional<VehiculoEntity> vehiculoActualizado = vehiculoRepository.buscarPorId(idVehiculo);
        if (vehiculoActualizado.isPresent()) {
            return vehiculoActualizado.get();
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo actualizado");
        }
    }

    public VehiculoEntity cambiarEstatus(Integer idVehiculo, Integer idUsuario, Boolean activo, Integer idUsuarioAutenticado) {
        validarUsuarioAutenticado(idUsuario, idUsuarioAutenticado);

        Optional<VehiculoEntity> resultado = vehiculoRepository.buscarPorId(idVehiculo);
        if (!resultado.isPresent()) {
            throw new IllegalArgumentException("El vehiculo no existe");
        }
        VehiculoEntity vehiculoActual = resultado.get();

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

        Optional<VehiculoEntity> vehiculoActualizado = vehiculoRepository.buscarPorId(idVehiculo);
        if (vehiculoActualizado.isPresent()) {
            return vehiculoActualizado.get();
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo actualizado");
        }
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
        Optional<VehiculoEntity> resultado = vehiculoRepository.buscarPorPlaca(placaNormalizada);
        if (resultado.isPresent()) {
            VehiculoEntity vehiculo = resultado.get();
            if (idVehiculoActual == null || !vehiculo.getIdVehiculo().equals(idVehiculoActual)) {
                throw new IllegalArgumentException("Ya existe un vehiculo registrado con la placa indicada");
            }
        }
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
