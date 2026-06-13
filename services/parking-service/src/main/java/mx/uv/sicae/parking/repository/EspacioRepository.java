package mx.uv.sicae.parking.repository;

import org.apache.ibatis.annotations.*;
import java.util.Optional;
import mx.uv.sicae.parking.model.Espacio;

@Mapper
public interface EspacioRepository {
    @Select("SELECT idEspacio, claveEspacio, tipo, ocupado, estatus FROM espacioestacionamiento WHERE idEspacio = #{idEspacio}")
    Optional<Espacio> buscarPorId(@Param("idEspacio") Integer idEspacio);

    @Update("UPDATE espacioestacionamiento SET ocupado = #{ocupado} WHERE idEspacio = #{idEspacio}")
    int actualizarOcupacion(@Param("idEspacio") Integer idEspacio, @Param("ocupado") Boolean ocupado);
}
