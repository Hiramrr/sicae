package mx.uv.sicae.vehicle.service;

import java.util.List;
import java.util.Optional;

import mx.uv.sicae.vehicle.dto.EstatusVehiculoRequest;
import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.dto.VehiculoResponse;
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

    public List<VehiculoResponse> buscarPorUsuario(Integer idUsuario, Integer idUsuarioAutenticado) {
        validarIdUsuario(idUsuario);
        validarUsuarioAutenticado(idUsuario, idUsuarioAutenticado);
        return vehiculoRepository.buscarPorUsuario(idUsuario).stream()
                .map(VehiculoResponse::fromEntity)
                .toList();
    }

    public VehiculoResponse registrar(VehiculoRequest request, Integer idUsuarioAutenticado) {
        validarDatosVehiculo(request);
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
            return VehiculoResponse.fromEntity(vehiculoRegistrado.get());
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo registrado");
        }
    }

    public VehiculoResponse editar(Integer idVehiculo, VehiculoRequest request, Integer idUsuarioAutenticado) {
        validarIdVehiculo(idVehiculo);
        validarDatosVehiculo(request);
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
            return VehiculoResponse.fromEntity(vehiculoActualizado.get());
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo actualizado");
        }
    }

    public VehiculoResponse cambiarEstatus(Integer idVehiculo, EstatusVehiculoRequest request, Integer idUsuarioAutenticado) {
        validarDatosEstatus(idVehiculo, request);
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);

        Optional<VehiculoEntity> resultado = vehiculoRepository.buscarPorId(idVehiculo);
        if (!resultado.isPresent()) {
            throw new IllegalArgumentException("El vehiculo no existe");
        }
        VehiculoEntity vehiculoActual = resultado.get();

        if (!vehiculoActual.getIdUsuario().equals(request.getIdUsuario())) {
            throw new IllegalArgumentException("El vehiculo no pertenece al usuario autenticado");
        }

        if (Boolean.TRUE.equals(request.getActivo()) && !Boolean.TRUE.equals(vehiculoActual.getEstatus())) {
            int vehiculosActivos = vehiculoRepository.contarActivosPorUsuario(request.getIdUsuario());
            if (vehiculosActivos >= MAXIMO_VEHICULOS_ACTIVOS) {
                throw new IllegalArgumentException("El usuario ya tiene 4 vehiculos activos");
            }
        }

        vehiculoRepository.cambiarEstatus(idVehiculo, request.getIdUsuario(), request.getActivo());

        Optional<VehiculoEntity> vehiculoActualizado = vehiculoRepository.buscarPorId(idVehiculo);
        if (vehiculoActualizado.isPresent()) {
            return VehiculoResponse.fromEntity(vehiculoActualizado.get());
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo actualizado");
        }
    }

    private void validarDatosVehiculo(VehiculoRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Los datos del vehiculo son obligatorios");
        }
        validarIdUsuario(request.getIdUsuario());

        if (request.getIdModelo() == null) {
            throw new IllegalArgumentException("idModelo es obligatorio");
        }
        if (request.getPlaca() == null || request.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("placa es obligatoria");
        }
        if (request.getPlaca().trim().length() > 7) {
            throw new IllegalArgumentException("placa no debe exceder 7 caracteres");
        }
        if (request.getColor() == null || request.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("color es obligatorio");
        }
        if (request.getColor().trim().length() > 20) {
            throw new IllegalArgumentException("color no debe exceder 20 caracteres");
        }
        if (request.getAnio() == null) {
            throw new IllegalArgumentException("anio es obligatorio");
        }
        if (request.getAnio() < 1900 || request.getAnio() > 2100) {
            throw new IllegalArgumentException("anio no es valido");
        }
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("descripcion es obligatoria");
        }
        if (request.getDescripcion().trim().length() > 255) {
            throw new IllegalArgumentException("descripcion no debe exceder 255 caracteres");
        }
    }

    private void validarDatosEstatus(Integer idVehiculo, EstatusVehiculoRequest request) {
        validarIdVehiculo(idVehiculo);

        if (request == null) {
            throw new IllegalArgumentException("Los datos del estatus son obligatorios");
        }
        validarIdUsuario(request.getIdUsuario());

        if (request.getActivo() == null) {
            throw new IllegalArgumentException("activo es obligatorio");
        }
    }

    private void validarIdUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario es obligatorio");
        }
    }

    private void validarIdVehiculo(Integer idVehiculo) {
        if (idVehiculo == null) {
            throw new IllegalArgumentException("idVehiculo es obligatorio");
        }
    }

    private void validarUsuarioAutenticado(Integer idUsuario, Integer idUsuarioAutenticado) {
        if (idUsuarioAutenticado == null) {
            throw new IllegalArgumentException("X-User-Id es obligatorio");
        }
        if (!idUsuarioAutenticado.equals(idUsuario)) {
            throw new IllegalArgumentException("Solo se pueden gestionar vehiculos del usuario autenticado");
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
