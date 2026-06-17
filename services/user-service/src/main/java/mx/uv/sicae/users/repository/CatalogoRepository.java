package mx.uv.sicae.users.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CatalogoRepository {

    // Verifica si el rol existe y esta activo.
    @Select("""
            SELECT COUNT(*)
            FROM rol
            WHERE idrol = #{idRol}
              AND estatus = B'1'
            """)
    int contarRolActivoPorId(@Param("idRol") Integer idRol);

    // Verifica si el tipo de usuario existe y esta activo.
    @Select("""
            SELECT COUNT(*)
            FROM "tipoUsuario"
            WHERE "idTipo" = #{idTipoUsuario}
              AND estatus = B'1'
            """)
    int contarTipoUsuarioActivoPorId(@Param("idTipoUsuario") Integer idTipoUsuario);

    // Verifica si el programa educativo existe y esta activo.
    @Select("""
            SELECT COUNT(*)
            FROM "programaEducativo"
            WHERE "idPrograma" = #{idProgramaEducativo}
              AND estatus = B'1'
            """)
    int contarProgramaEducativoActivoPorId(@Param("idProgramaEducativo") Integer idProgramaEducativo);
}
