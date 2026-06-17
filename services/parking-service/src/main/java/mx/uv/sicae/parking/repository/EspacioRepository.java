package mx.uv.sicae.parking.repository;

import org.apache.ibatis.annotations.*;
import java.util.List;
import java.util.Optional;
import mx.uv.sicae.parking.model.Espacio;

// @Mapper le indica a MyBatis (y a Spring) que esta interfaz sirve para comunicarse directamente con la base de datos.
// No necesitas escribir el código de conexión, MyBatis lo genera por detrás basándose en las consultas SQL que pones aquí.
@Mapper
public interface EspacioRepository {

    // Ejecuta una consulta SELECT para buscar un cajón de estacionamiento específico usando su ID.
    // Se usa Optional<Espacio> como una buena práctica para evitar errores de tipo NullPointerException
    // en caso de que manden un ID que no existe en la base de datos.
    @Select("SELECT idEspacio, claveEspacio, tipo, ocupado, estatus FROM espacioestacionamiento WHERE idEspacio = #{idEspacio}")
    Optional<Espacio> buscarPorId(@Param("idEspacio") Integer idEspacio);

    // Ejecuta un UPDATE para cambiar únicamente el estado de ocupación de un espacio (true = ocupado, false = libre).
    // Tu MovimientoService llama a este método cuando un vehículo registra su entrada (para ocuparlo)
    // o su salida (para liberarlo). Retorna un int que indica cuántas filas se actualizaron.
    @Update("UPDATE espacioestacionamiento SET ocupado = #{ocupado} WHERE idEspacio = #{idEspacio}")
    int actualizarOcupacion(@Param("idEspacio") Integer idEspacio, @Param("ocupado") Boolean ocupado);

    // Ejecuta un SELECT general sin filtros para traer todo el catálogo de espacios.
    // Este es exactamente el método que utiliza tu EspacioController para listar qué lugares están libres u ocupados.
    @Select("SELECT idEspacio, claveEspacio, tipo, ocupado, estatus FROM espacioestacionamiento")
    List<Espacio> obtenerTodos();
}
