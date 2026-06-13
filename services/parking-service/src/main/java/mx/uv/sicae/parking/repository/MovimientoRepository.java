package mx.uv.sicae.parking.repository;

import org.apache.ibatis.annotations.*;
import java.util.List;
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
}
