package mx.uv.sicae.vehicle.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import mx.uv.sicae.vehicle.dto.EstatusVehiculoRequest;
import mx.uv.sicae.vehicle.dto.VehiculoRequest;
import mx.uv.sicae.vehicle.dto.VehiculoResponse;
import mx.uv.sicae.vehicle.model.VehiculoEntity;
import mx.uv.sicae.vehicle.repository.VehiculoRepository;
import org.springframework.stereotype.Service;

@Service
public class VehiculoService {
    // Regla del proyecto: un usuario no debe tener mas de cuatro vehiculos activos
    private static final int MAXIMO_VEHICULOS_ACTIVOS = 4;

    private final VehiculoRepository vehiculoRepository;

    public VehiculoService(VehiculoRepository vehiculoRepository) {
        this.vehiculoRepository = vehiculoRepository;
    }

    public List<VehiculoResponse> buscarPorUsuario(Integer idUsuario, Integer idUsuarioAutenticado) {
        // antes de consultar, confirmo que el usuario pedido sea el mismo del token
        validarIdUsuario(idUsuario);
        validarUsuarioAutenticado(idUsuario, idUsuarioAutenticado);

        List<VehiculoEntity> vehiculos = vehiculoRepository.buscarPorUsuario(idUsuario);
        List<VehiculoResponse> respuesta = new ArrayList<>();

        // convierto cada entidad a respuesta para no exponer detalles internos
        for (VehiculoEntity vehiculo : vehiculos) {
            respuesta.add(VehiculoResponse.fromEntity(vehiculo));
        }

        return respuesta;
    }

    public VehiculoResponse buscarPorPlaca(String placa, Integer idUsuarioAutenticado) {
        // la placa se normaliza para que no afecten minusculas o espacios
        validarPlaca(placa);

        Optional<VehiculoEntity> resultado = vehiculoRepository.buscarPorPlaca(normalizarPlaca(placa));
        if (resultado.isEmpty()) {
            throw new IllegalArgumentException("No existe un vehiculo registrado con la placa indicada");
        }

        VehiculoEntity vehiculo = resultado.get();
        // aunque exista la placa, solo su duenio puede ver ese vehiculo
        validarUsuarioAutenticado(vehiculo.getIdUsuario(), idUsuarioAutenticado);
        return VehiculoResponse.fromEntity(vehiculo);
    }

    public VehiculoResponse registrar(VehiculoRequest request, Integer idUsuarioAutenticado) {
        // Aqui se juntan todas las reglas antes de insertar en la base
        validarDatosVehiculo(request);
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);
        validarModeloExiste(request.getIdModelo());
        validarPlacaDisponible(request.getPlaca(), null);

        int vehiculosActivos = vehiculoRepository.contarActivosPorUsuario(request.getIdUsuario());
        if (vehiculosActivos >= MAXIMO_VEHICULOS_ACTIVOS) {
            throw new IllegalArgumentException("El usuario ya tiene 4 vehiculos activos");
        }

        // se arma la entidad ya con los datos limpios para guardar
        VehiculoEntity vehiculo = new VehiculoEntity();
        vehiculo.setIdUsuario(request.getIdUsuario());
        vehiculo.setClaveVehiculo(generarClaveVehiculo(request.getPlaca()));
        vehiculo.setIdModelo(request.getIdModelo());
        vehiculo.setPlaca(normalizarPlaca(request.getPlaca()));
        vehiculo.setColor(request.getColor().trim());
        vehiculo.setAnio(Integer.parseInt(request.getAnio().trim()));
        vehiculo.setDescripcion(normalizarOpcional(request.getDescripcion()));

        vehiculoRepository.registrar(vehiculo);

        // recupero el registro completo para devolver marca, modelo y demas datos de la vista
        Optional<VehiculoEntity> vehiculoRegistrado = vehiculoRepository.buscarPorId(vehiculo.getIdVehiculo());
        if (vehiculoRegistrado.isPresent()) {
            return VehiculoResponse.fromEntity(vehiculoRegistrado.get());
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo registrado");
        }
    }

    public VehiculoResponse editar(Integer idVehiculo, VehiculoRequest request, Integer idUsuarioAutenticado) {
        // para editar se valida tanto el id de la ruta como los datos del cuerpo
        validarIdVehiculo(idVehiculo);
        validarDatosVehiculo(request);
        validarUsuarioAutenticado(request.getIdUsuario(), idUsuarioAutenticado);
        validarModeloExiste(request.getIdModelo());

        Optional<VehiculoEntity> resultado = vehiculoRepository.buscarPorId(idVehiculo);
        if (!resultado.isPresent()) {
            throw new IllegalArgumentException("El vehiculo no existe");
        }
        VehiculoEntity vehiculoActual = resultado.get();

        // evito que un usuario modifique un vehiculo que no le pertenece
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
        vehiculo.setAnio(Integer.parseInt(request.getAnio().trim()));
        vehiculo.setDescripcion(normalizarOpcional(request.getDescripcion()));

        vehiculoRepository.editar(vehiculo);

        // regreso la informacion actualizada tal como queda en la base
        Optional<VehiculoEntity> vehiculoActualizado = vehiculoRepository.buscarPorId(idVehiculo);
        if (vehiculoActualizado.isPresent()) {
            return VehiculoResponse.fromEntity(vehiculoActualizado.get());
        } else {
            throw new IllegalStateException("No se pudo recuperar el vehiculo actualizado");
        }
    }

    public VehiculoResponse cambiarEstatus(Integer idVehiculo, EstatusVehiculoRequest request, Integer idUsuarioAutenticado) {
        // cambiar estatus tambien pasa por token para que no se active algo ajeno
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

        // si se quiere reactivar, vuelvo a revisar el limite de vehiculos activos
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
        // reviso los campos obligatorios y el formato esperado por la regla del proyecto
        if (request == null) {
            throw new IllegalArgumentException("Los datos del vehiculo son obligatorios");
        }
        validarIdUsuario(request.getIdUsuario());

        if (request.getIdModelo() == null) {
            throw new IllegalArgumentException("idModelo es obligatorio");
        }
        if (request.getIdModelo() <= 0) {
            throw new IllegalArgumentException("idModelo debe ser mayor a 0");
        }
        if (request.getPlaca() == null || request.getPlaca().trim().isEmpty()) {
            throw new IllegalArgumentException("placa es obligatoria");
        }
        validarPlaca(request.getPlaca());
        if (request.getColor() == null || request.getColor().trim().isEmpty()) {
            throw new IllegalArgumentException("color es obligatorio");
        }
        if (!request.getColor().trim().matches("^[A-Za-z ]+$")) {
            throw new IllegalArgumentException("color solo debe contener letras y espacios");
        }
        if (request.getColor().trim().length() > 20) {
            throw new IllegalArgumentException("color no debe exceder 20 caracteres");
        }
        if (request.getAnio() == null) {
            throw new IllegalArgumentException("anio es obligatorio");
        }
        validarAnio(request.getAnio());
        if (request.getDescripcion() == null || request.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("descripcion es obligatoria");
        }
        if (request.getDescripcion().trim().length() > 255) {
            throw new IllegalArgumentException("descripcion no debe exceder 255 caracteres");
        }
    }

    private void validarDatosEstatus(Integer idVehiculo, EstatusVehiculoRequest request) {
        // para estatus solo se necesita id del vehiculo, usuario y el valor activo
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
        // los identificadores deben venir informados y ser positivos
        if (idUsuario == null) {
            throw new IllegalArgumentException("idUsuario es obligatorio");
        }
        if (idUsuario <= 0) {
            throw new IllegalArgumentException("idUsuario debe ser mayor a 0");
        }
    }

    private void validarIdVehiculo(Integer idVehiculo) {
        // misma validacion base, pero aplicada al vehiculo
        if (idVehiculo == null) {
            throw new IllegalArgumentException("idVehiculo es obligatorio");
        }
        if (idVehiculo <= 0) {
            throw new IllegalArgumentException("idVehiculo debe ser mayor a 0");
        }
    }

    private void validarPlaca(String placa) {
        // placa vacia o con simbolos raros se rechaza desde aqui
        if (placa == null || placa.trim().isEmpty()) {
            throw new IllegalArgumentException("placa es obligatoria");
        }

        String placaNormalizada = normalizarPlaca(placa);
        if (placaNormalizada.length() < 6 || placaNormalizada.length() > 7) {
            throw new IllegalArgumentException("placa debe tener entre 6 y 7 caracteres");
        }

        if (!placaNormalizada.matches("^[A-Z0-9]+$")) {
            throw new IllegalArgumentException("placa solo debe contener letras y numeros");
        }
    }

    private void validarAnio(String anio) {
        // el anio llega como texto, por eso primero confirmo que sea numerico
        if (anio.trim().isEmpty()) {
            throw new IllegalArgumentException("anio es obligatorio");
        }

        if (!anio.trim().matches("^[0-9]+$")) {
            throw new IllegalArgumentException("anio debe ser un numero entero");
        }

        int anioNumero = Integer.parseInt(anio.trim());
        if (anioNumero < 1980 || anioNumero > 2026) {
            throw new IllegalArgumentException("anio no es valido, debe estar entre 1980 y 2026");
        }
    }

    private void validarUsuarioAutenticado(Integer idUsuario, Integer idUsuarioAutenticado) {
        // esta comparacion amarra la peticion con el usuario que trae el token
        if (idUsuarioAutenticado == null) {
            throw new IllegalArgumentException("Debe enviar el token de autenticacion");
        }
        if (!idUsuarioAutenticado.equals(idUsuario)) {
            throw new IllegalArgumentException("Solo se pueden gestionar vehiculos del usuario autenticado");
        }
    }

    private void validarModeloExiste(Integer idModelo) {
        // no dejo guardar vehiculos con modelos dados de baja o inexistentes
        if (vehiculoRepository.contarModeloActivoPorId(idModelo) == 0) {
            throw new IllegalArgumentException("El modelo indicado no existe o no esta activo");
        }
    }

    private void validarPlacaDisponible(String placa, Integer idVehiculoActual) {
        // al editar permito la misma placa solo si pertenece al mismo vehiculo
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
        // dejo la placa en un formato parejo antes de comparar o guardar
        return placa.trim().toUpperCase();
    }

    private String normalizarOpcional(String valor) {
        // si algun texto opcional viene vacio, lo guardo como null
        if (valor == null || valor.trim().isEmpty()) {
            return null;
        }
        return valor.trim();
    }

    private String generarClaveVehiculo(String placa) {
        // la clave sale de la placa para que sea facil relacionarla
        String normalizada = normalizarPlaca(placa).replaceAll("[^A-Z0-9]", "");
        if (normalizada.length() > 7) {
            normalizada = normalizada.substring(0, 7);
        }
        return "V-" + normalizada;
    }
}
