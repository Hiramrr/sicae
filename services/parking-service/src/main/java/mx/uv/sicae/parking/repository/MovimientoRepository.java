package mx.uv.sicae.parking.repository;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;
import mx.uv.sicae.parking.model.Movimiento;

// @Mapper le indica a MyBatis que esta interfaz es el puente de comunicación directa
// entre la aplicación y la tabla 'movimiento' de la base de datos.
@Mapper
public interface MovimientoRepository {

    // Ejecuta un INSERT para crear un nuevo registro cuando un vehículo entra al estacionamiento.
    @Insert("INSERT INTO movimiento (idVehiculo, tiempoEntrada, tiempoSalida, tarifaHora, tiempoCreacion, idEspacio) " +
        "VALUES (#{idVehiculo}, #{tiempoEntrada}, #{tiempoSalida}, #{tarifaHora}, #{tiempoCreacion}, #{idEspacio})")
    // @Options es fundamental aquí: 'useGeneratedKeys = true' hace que después de insertar en la BD,
    // MyBatis recupere el ID autoincrementable generado y se lo asigne a la propiedad "idMovimiento" del objeto original.
    @Options(useGeneratedKeys = true, keyProperty = "idMovimiento")
    int registrarEntrada(Movimiento movimiento);

    // Este SELECT es clave para la regla de negocio que limita a 2 vehículos simultáneos por usuario.
    // Utiliza <script> para indicarle a MyBatis que la consulta será dinámica.
    // El <foreach> sirve para inyectar una lista de IDs de vehículos (los que le pertenecen al usuario)
    // armando una cláusula IN dinámica, por ejemplo: IN (1, 5, 8).
    // Se filtra por "costoTotal IS NULL" porque si no tiene costo, significa que el vehículo sigue adentro.
    @Select("<script>" +
        "SELECT COUNT(*) FROM movimiento WHERE costoTotal IS NULL AND idVehiculo IN " +
        "<foreach item='item' collection='vehiculosIds' open='(' separator=',' close=')'>" +
        "#{item}" +
        "</foreach>" +
        "</script>")
    int contarVehiculosActivosDelUsuario(@Param("vehiculosIds") List<Integer> vehiculosIds);

    // Consulta un movimiento específico por su ID.
    // Se utiliza antes de registrar una salida para verificar que el ticket existe
    // y obtener los datos originales (como el tiempo de entrada y la tarifa).
    // Devuelve Optional para manejar de forma segura el caso de que manden un ID incorrecto.
    @Select("SELECT idMovimiento, idVehiculo, tiempoEntrada, tiempoSalida, tarifaHora, idEspacio, costoTotal " +
        "FROM movimiento WHERE idMovimiento = #{idMovimiento}")
    Optional<Movimiento> buscarPorId(@Param("idMovimiento") Integer idMovimiento);

    // Ejecuta un UPDATE para "cerrar" el movimiento cuando el vehículo sale.
    // Actualiza los campos calculados por la lógica de negocio en el MovimientoService
    // (hora de salida, minutos totales, las horas que se le van a cobrar y el costo final).
    @Update("UPDATE movimiento SET tiempoSalida = #{tiempoSalida}, minutosEstacionado = #{minutosEstacionado}, " +
        "horasCobradas = #{horasCobradas}, costoTotal = #{costoTotal}, tiempoActualizacion = #{tiempoActualizacion} " +
        "WHERE idMovimiento = #{idMovimiento}")
    int actualizarSalida(Movimiento movimiento);
}
