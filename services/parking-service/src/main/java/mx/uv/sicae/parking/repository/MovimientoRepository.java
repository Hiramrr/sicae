package mx.uv.sicae.parking.repository;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;
import mx.uv.sicae.parking.model.Movimiento;

@Mapper
public interface MovimientoRepository {
    @Insert("INSERT INTO movimiento (idVehiculo, tiempoEntrada, tiempoSalida, tarifaHora, tiempoCreacion, idEspacio) " +
        "VALUES (#{idVehiculo}, #{tiempoEntrada}, #{tiempoSalida}, #{tarifaHora}, #{tiempoCreacion}, #{idEspacio})")
    @Options(useGeneratedKeys = true, keyProperty = "idMovimiento")
    int registrarEntrada(Movimiento movimiento);

    @Select("<script>" +
        "SELECT COUNT(*) FROM movimiento WHERE costoTotal IS NULL AND idVehiculo IN " +
        "<foreach item='item' collection='vehiculosIds' open='(' separator=',' close=')'>" +
        "#{item}" +
        "</foreach>" +
        "</script>")
    int contarVehiculosActivosDelUsuario(@Param("vehiculosIds") List<Integer> vehiculosIds);

    @Select("SELECT idMovimiento, idVehiculo, tiempoEntrada, tiempoSalida, tarifaHora, idEspacio, costoTotal " +
        "FROM movimiento WHERE idMovimiento = #{idMovimiento}")
    Optional<Movimiento> buscarPorId(@Param("idMovimiento") Integer idMovimiento);

    @Update("UPDATE movimiento SET tiempoSalida = #{tiempoSalida}, minutosEstacionado = #{minutosEstacionado}, " +
        "horasCobradas = #{horasCobradas}, costoTotal = #{costoTotal}, tiempoActualizacion = #{tiempoActualizacion} " +
        "WHERE idMovimiento = #{idMovimiento}")
    int actualizarSalida(Movimiento movimiento);
}
