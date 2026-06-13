package mx.uv.sicae.parking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import mx.uv.sicae.parking.model.*;
import mx.uv.sicae.parking.repository.*;
import mx.uv.sicae.parking.client.*;

@Service
public class MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final EspacioRepository espacioRepository;
    private final UserServiceClient userServiceClient;
    private final VehicleServiceClient vehicleServiceClient;

    public MovimientoService(MovimientoRepository movimientoRepository, EspacioRepository espacioRepository,
                             UserServiceClient userServiceClient, VehicleServiceClient vehicleServiceClient) {
        this.movimientoRepository = movimientoRepository;
        this.espacioRepository = espacioRepository;
        this.userServiceClient = userServiceClient;
        this.vehicleServiceClient = vehicleServiceClient;
    }

    @Transactional
    public Movimiento registrarEntrada(Movimiento peticion) {
        Usuario usuario = userServiceClient.validarUsuario(peticion.getClaveUsuario());
        if (usuario == null || !usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no existe o se encuentra inactivo");
        }

        Vehiculo vehiculo = vehicleServiceClient.validarVehiculoPorPlaca(peticion.getPlaca());
        if (vehiculo == null || !vehiculo.isActivo() || !vehiculo.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El vehiculo no es valido, esta inactivo o no pertenece al usuario");
        }

        List<Integer> idsVehiculosDelUsuario = vehicleServiceClient.obtenerIdsVehiculosPorUsuario(usuario.getIdUsuario());
        if (idsVehiculosDelUsuario == null || idsVehiculosDelUsuario.isEmpty()) {
            throw new IllegalArgumentException("El usuario no tiene vehiculos registrados");
        }

        int vehiculosDentro = movimientoRepository.contarVehiculosActivosDelUsuario(idsVehiculosDelUsuario);
        if (vehiculosDentro >= 2) {
            throw new IllegalArgumentException("El usuario ya tiene el maximo de 2 vehiculos dentro del estacionamiento");
        }

        Espacio espacio = espacioRepository.buscarPorId(peticion.getIdEspacio())
            .orElseThrow(() -> new IllegalArgumentException("El espacio indicado no existe"));

        if (!espacio.getEstatus() || espacio.getOcupado()) {
            throw new IllegalArgumentException("El espacio seleccionado no esta disponible o ya se encuentra ocupado");
        }

        LocalDateTime ahora = LocalDateTime.now();
        peticion.setIdVehiculo(vehiculo.getIdVehiculo());
        peticion.setTiempoEntrada(ahora);
        peticion.setTiempoSalida(ahora);
        peticion.setTiempoCreacion(ahora);

        movimientoRepository.registrarEntrada(peticion);
        espacioRepository.actualizarOcupacion(espacio.getIdEspacio(), true);

        Movimiento respuesta = new Movimiento();
        respuesta.setIdMovimiento(peticion.getIdMovimiento());
        respuesta.setTiempoEntrada(peticion.getTiempoEntrada());
        respuesta.setEspacioAsignado(espacio.getClaveEspacio());
        respuesta.setTarifaHora(peticion.getTarifaHora());

        return respuesta;
    }

    @Transactional
    public Movimiento registrarSalida(Integer idMovimiento, Movimiento peticion) {
        Usuario usuario = userServiceClient.validarUsuario(peticion.getClaveUsuario());
        if (usuario == null || !usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no existe o se encuentra inactivo");
        }

        Vehiculo vehiculo = vehicleServiceClient.validarVehiculoPorPlaca(peticion.getPlaca());
        if (vehiculo == null || !vehiculo.isActivo() || !vehiculo.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El vehiculo no es valido, esta inactivo o no pertenece al usuario");
        }

        Movimiento movimiento = movimientoRepository.buscarPorId(idMovimiento)
            .orElseThrow(() -> new IllegalArgumentException("El movimiento indicado no existe"));

        if (movimiento.getCostoTotal() != null) {
            throw new IllegalArgumentException("El movimiento ya se encuentra cerrado (el vehiculo ya salio)");
        }

        if (!movimiento.getIdVehiculo().equals(vehiculo.getIdVehiculo())) {
            throw new IllegalArgumentException("El vehiculo indicado no corresponde al vehiculo que ingreso en este movimiento");
        }

        LocalDateTime ahora = LocalDateTime.now();
        long minutos = Duration.between(movimiento.getTiempoEntrada(), ahora).toMinutes();
        if (minutos < 0) minutos = 0;

        long horas = (long) Math.ceil((double) minutos / 60.0);
        if (horas == 0) horas = 1;

        BigDecimal costoTotal = movimiento.getTarifaHora().multiply(BigDecimal.valueOf(horas));

        movimiento.setTiempoSalida(ahora);
        movimiento.setMinutosEstacionado((int) minutos);
        movimiento.setHorasCobradas((int) horas);
        movimiento.setCostoTotal(costoTotal);
        movimiento.setTiempoActualizacion(ahora);

        movimientoRepository.actualizarSalida(movimiento);
        espacioRepository.actualizarOcupacion(movimiento.getIdEspacio(), false);

        Espacio espacio = espacioRepository.buscarPorId(movimiento.getIdEspacio()).orElse(new Espacio());

        Movimiento respuesta = new Movimiento();
        respuesta.setIdMovimiento(movimiento.getIdMovimiento());
        respuesta.setTiempoEntrada(movimiento.getTiempoEntrada());
        respuesta.setTiempoSalida(movimiento.getTiempoSalida());
        respuesta.setEspacioAsignado(espacio.getClaveEspacio());
        respuesta.setTarifaHora(movimiento.getTarifaHora());
        respuesta.setCostoTotal(movimiento.getCostoTotal());
        respuesta.setHorasCobradas(movimiento.getHorasCobradas());

        return respuesta;
    }
}
