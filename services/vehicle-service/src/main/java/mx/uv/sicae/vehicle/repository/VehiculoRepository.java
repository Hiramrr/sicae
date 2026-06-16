package mx.uv.sicae.vehicle.repository;

import java.util.List;
import java.util.Optional;

import mx.uv.sicae.vehicle.model.VehiculoEntity;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

@Mapper
public interface VehiculoRepository {
    // consulta la vista con la informacion completa para no armar marca/modelo a mano
    @Select("""
            SELECT idVehiculo, idUsuario, claveVehiculo, idMarca, marca, idModelo, modelo,
                   placa, color, anio, descripcion, estatus, tiempoCreacion, tiempoActualizacion
            FROM vehiculofullinfo
            WHERE idUsuario = #{idUsuario}
            ORDER BY idVehiculo
            """)
    List<VehiculoEntity> buscarPorUsuario(@Param("idUsuario") Integer idUsuario);

    // se usa despues de guardar o actualizar para regresar el registro completo
    @Select("""
            SELECT idVehiculo, idUsuario, claveVehiculo, idMarca, marca, idModelo, modelo,
                   placa, color, anio, descripcion, estatus, tiempoCreacion, tiempoActualizacion
            FROM vehiculofullinfo
            WHERE idVehiculo = #{idVehiculo}
            """)
    Optional<VehiculoEntity> buscarPorId(@Param("idVehiculo") Integer idVehiculo);

    // la placa funciona como dato unico para ubicar un vehiculo
    @Select("""
            SELECT idVehiculo, idUsuario, claveVehiculo, idMarca, marca, idModelo, modelo,
                   placa, color, anio, descripcion, estatus, tiempoCreacion, tiempoActualizacion
            FROM vehiculofullinfo
            WHERE placa = #{placa}
            """)
    Optional<VehiculoEntity> buscarPorPlaca(@Param("placa") String placa);

    // aqui se controla la regla de maximo cuatro vehiculos activos
    @Select("SELECT COUNT(*) FROM vehiculo WHERE idUsuario = #{idUsuario} AND estatus = b'1'")
    int contarActivosPorUsuario(@Param("idUsuario") Integer idUsuario);

    // antes de guardar reviso que el modelo exista y este activo
    @Select("SELECT COUNT(*) FROM modelo WHERE idModelo = #{idModelo} AND estatus = b'1'")
    int contarModeloActivoPorId(@Param("idModelo") Integer idModelo);

    // registra el vehiculo activo desde el inicio
    @Insert("""
            INSERT INTO vehiculo (idUsuario, claveVehiculo, idModelo, placa, color, anio, descripcion, estatus)
            VALUES (#{idUsuario}, #{claveVehiculo}, #{idModelo}, #{placa}, #{color}, #{anio}, #{descripcion}, b'1')
            """)
    @Options(useGeneratedKeys = true, keyProperty = "idVehiculo")
    int registrar(VehiculoEntity vehiculo);

    // actualiza solo los datos editables del vehiculo.
    @Update("""
            UPDATE vehiculo
            SET idModelo = #{idModelo},
                placa = #{placa},
                color = #{color},
                anio = #{anio},
                descripcion = #{descripcion},
                tiempoActualizacion = CURRENT_TIMESTAMP
            WHERE idVehiculo = #{idVehiculo}
              AND idUsuario = #{idUsuario}
            """)
    int editar(VehiculoEntity vehiculo);

    // cambia el estatus sin borrar el registro, para mantener el historial
    @Update("""
            UPDATE vehiculo
            SET estatus = #{estatus},
                tiempoActualizacion = CURRENT_TIMESTAMP
            WHERE idVehiculo = #{idVehiculo}
              AND idUsuario = #{idUsuario}
            """)
    int cambiarEstatus(@Param("idVehiculo") Integer idVehiculo,
                       @Param("idUsuario") Integer idUsuario,
                       @Param("estatus") Boolean estatus);
}
