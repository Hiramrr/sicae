package mx.uv.sicae.users.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface CatalogoRepository {

    @Select("""
            SELECT COUNT(*)
            FROM rol
            WHERE idrol = #{idRol}
              AND estatus = B'1'
            """)
    int contarRolActivoPorId(@Param("idRol") Integer idRol);

    @Select("""
            SELECT COUNT(*)
            FROM "tipoUsuario"
            WHERE "idTipo" = #{idTipoUsuario}
              AND estatus = B'1'
            """)
    int contarTipoUsuarioActivoPorId(@Param("idTipoUsuario") Integer idTipoUsuario);

    @Select("""
            SELECT COUNT(*)
            FROM "programaEducativo"
            WHERE "idPrograma" = #{idProgramaEducativo}
              AND estatus = B'1'
            """)
    int contarProgramaEducativoActivoPorId(@Param("idProgramaEducativo") Integer idProgramaEducativo);
}
