package mx.uv.sicae.parking.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import mx.uv.sicae.parking.model.*;
import mx.uv.sicae.parking.dto.*;
import mx.uv.sicae.parking.repository.*;
import mx.uv.sicae.parking.client.*;

// @Service le indica a Spring que esta clase es el "cerebro" del microservicio.
// Aquí se concentran todas las reglas de negocio, cálculos y orquestación entre
// repositorios (base de datos) y clientes (otros microservicios).
@Service
public class MovimientoService {

    // Dependencias hacia las bases de datos y la comunicación con otros microservicios.
    private final MovimientoRepository movimientoRepository;
    private final EspacioRepository espacioRepository;
    private final UserServiceClient userServiceClient;
    private final VehicleServiceClient vehicleServiceClient;

    // Inyección de dependencias mediante el constructor.
    public MovimientoService(MovimientoRepository movimientoRepository, EspacioRepository espacioRepository,
                             UserServiceClient userServiceClient, VehicleServiceClient vehicleServiceClient) {
        this.movimientoRepository = movimientoRepository;
        this.espacioRepository = espacioRepository;
        this.userServiceClient = userServiceClient;
        this.vehicleServiceClient = vehicleServiceClient;
    }

    // @Transactional es vital aquí. Significa que todas las operaciones de base de datos
    // dentro de este método ocurren como una sola unidad. Si el registro del movimiento funciona,
    // pero ocurre un error al actualizar el espacio a "ocupado", Spring hace un "Rollback"
    // (deshace el registro del movimiento) para que la base de datos no quede inconsistente.
    @Transactional
    public Movimiento registrarEntrada(EntradaRequestDTO peticion) {
        // 1. Consume UserService (vía SOAP) para asegurar que el usuario existe y es válido.
        Usuario usuario = userServiceClient.validarUsuario(peticion.getClaveUsuario());
        if (usuario == null || !usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no existe o se encuentra inactivo");
        }

        // 2. Consume VehicleService (vía REST) para traer los datos del vehículo.
        Vehiculo vehiculo = vehicleServiceClient.validarVehiculoPorPlaca(peticion.getPlaca());

        // Validaciones de seguridad y negocio sobre el vehículo con mensajes específicos para depuración.
        if (vehiculo == null) {
            throw new IllegalArgumentException("DEBUG 1: El vehiculo llego como NULL. Significa que el vehicle-service rechazo la peticion (posible error 400 por falta de token, revisa logs).");
        }
        if (!vehiculo.isActivo()) {
            throw new IllegalArgumentException("DEBUG 2: El vehiculo llego bien, pero 'activo' es false. (Significa que el parking-service aun no detecta el cambio de @JsonProperty que hicimos antes).");
        }
        if (!vehiculo.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("DEBUG 3: El vehiculo le pertenece al ID " + vehiculo.getIdUsuario() + " pero tu clave ALUMNO001 es del ID " + usuario.getIdUsuario());
        }

        // 3. Regla de negocio: Límite de vehículos en el estacionamiento.
        // Primero trae todos los IDs de los vehículos que posee este usuario.
        List<Integer> idsVehiculosDelUsuario = vehicleServiceClient.obtenerIdsVehiculosPorUsuario(usuario.getIdUsuario());
        if (idsVehiculosDelUsuario == null || idsVehiculosDelUsuario.isEmpty()) {
            throw new IllegalArgumentException("El usuario no tiene vehiculos registrados");
        }

        // Luego cuenta cuántos de esos vehículos tienen un movimiento abierto (sin costo cobrado).
        int vehiculosDentro = movimientoRepository.contarVehiculosActivosDelUsuario(idsVehiculosDelUsuario);
        if (vehiculosDentro >= 2) {
            throw new IllegalArgumentException("El usuario ya tiene el maximo de 2 vehiculos dentro del estacionamiento");
        }

        // 4. Verifica que el cajón solicitado exista, esté activo en catálogo y no esté siendo ocupado por nadie más.
        Espacio espacio = espacioRepository.buscarPorId(peticion.getIdEspacio())
            .orElseThrow(() -> new IllegalArgumentException("El espacio indicado no existe"));

        if (!espacio.getEstatus() || espacio.getOcupado()) {
            throw new IllegalArgumentException("El espacio seleccionado no esta disponible o ya se encuentra ocupado");
        }

        LocalDateTime ahora = LocalDateTime.now();

        // 5. Prepara y guarda el nuevo ticket/movimiento.
        Movimiento nuevoMovimiento = new Movimiento();
        nuevoMovimiento.setIdVehiculo(vehiculo.getIdVehiculo());
        nuevoMovimiento.setTarifaHora(peticion.getTarifaHora());
        nuevoMovimiento.setIdEspacio(peticion.getIdEspacio());
        nuevoMovimiento.setTiempoEntrada(ahora);
        nuevoMovimiento.setTiempoSalida(ahora); // Temporal, se sobreescribe al salir
        nuevoMovimiento.setTiempoCreacion(ahora);

        movimientoRepository.registrarEntrada(nuevoMovimiento);

        // 6. Bloquea el espacio en la base de datos para que nadie más lo use.
        espacioRepository.actualizarOcupacion(espacio.getIdEspacio(), true);

        // 7. Arma una respuesta "limpia" (solo con lo necesario) para mandarla al Frontend.
        Movimiento respuesta = new Movimiento();
        respuesta.setIdMovimiento(nuevoMovimiento.getIdMovimiento());
        respuesta.setTiempoEntrada(nuevoMovimiento.getTiempoEntrada());
        respuesta.setEspacioAsignado(espacio.getClaveEspacio());
        respuesta.setTarifaHora(nuevoMovimiento.getTarifaHora());

        return respuesta;
    }

    // @Transactional asegura que tanto el cobro como la liberación del cajón ocurran juntos o fallen juntos.
    @Transactional
    public Movimiento registrarSalida(Integer idMovimiento, SalidaRequestDTO peticion) {
        // 1. Valida nuevamente al usuario (vía SOAP).
        Usuario usuario = userServiceClient.validarUsuario(peticion.getClaveUsuario());
        if (usuario == null || !usuario.isActivo()) {
            throw new IllegalArgumentException("El usuario no existe o se encuentra inactivo");
        }

        // 2. Valida nuevamente al vehículo y que realmente le pertenezca a la persona (vía REST).
        Vehiculo vehiculo = vehicleServiceClient.validarVehiculoPorPlaca(peticion.getPlaca());
        if (vehiculo == null || !vehiculo.isActivo() || !vehiculo.getIdUsuario().equals(usuario.getIdUsuario())) {
            throw new IllegalArgumentException("El vehiculo no es valido, esta inactivo o no pertenece al usuario");
        }

        // 3. Busca el ticket original en la BD.
        Movimiento movimiento = movimientoRepository.buscarPorId(idMovimiento)
            .orElseThrow(() -> new IllegalArgumentException("El movimiento indicado no existe"));

        // Regla de negocio: Evita cobros duplicados si el movimiento ya tiene un costo asignado.
        if (movimiento.getCostoTotal() != null) {
            throw new IllegalArgumentException("El movimiento ya se encuentra cerrado");
        }

        // Regla de negocio: Evita que alguien intente sacar un vehículo diferente al que metió con este ticket.
        if (!movimiento.getIdVehiculo().equals(vehiculo.getIdVehiculo())) {
            throw new IllegalArgumentException("El vehiculo indicado no corresponde al vehiculo que ingreso en este movimiento");
        }

        // 4. Cálculos de tiempo y dinero.
        LocalDateTime ahora = LocalDateTime.now();
        // Calcula la diferencia en minutos desde que entró hasta ahorita.
        long minutos = Duration.between(movimiento.getTiempoEntrada(), ahora).toMinutes();
        if (minutos < 0) minutos = 0; // Seguridad por si la hora del servidor falla

        // Se redondea hacia arriba: 1 min a 60 min = 1 hora; 61 min a 120 min = 2 horas.
        long horas = (long) Math.ceil((double) minutos / 60.0);
        if (horas == 0) horas = 1; // Se cobra un mínimo de 1 hora siempre

        // Multiplica la tarifa guardada en el ticket por las horas transcurridas.
        BigDecimal costoTotal = movimiento.getTarifaHora().multiply(BigDecimal.valueOf(horas));

        // 5. Actualiza los valores calculados en el objeto de base de datos.
        movimiento.setTiempoSalida(ahora);
        movimiento.setMinutosEstacionado((int) minutos);
        movimiento.setHorasCobradas((int) horas);
        movimiento.setCostoTotal(costoTotal);
        movimiento.setTiempoActualizacion(ahora);

        // 6. Guarda los cambios del ticket y libera el espacio para que otros lo puedan usar.
        movimientoRepository.actualizarSalida(movimiento);
        espacioRepository.actualizarOcupacion(movimiento.getIdEspacio(), false);

        Espacio espacio = espacioRepository.buscarPorId(movimiento.getIdEspacio()).orElse(new Espacio());

        // 7. Arma el "Ticket final" de salida con el desglose del cobro para enviarlo al Frontend.
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
