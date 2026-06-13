package mx.uv.sicae.auth.repository;

import mx.uv.sicae.auth.model.UsuarioAuth;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import java.util.Optional;

@Mapper
public interface AuthRepository {
    
    // Consultamos la vista usuarioFullInfo 
    @Select("SELECT \"idUsuario\", username, password, nombre, \"apellidoPaterno\", \"apellidoMaterno\", " +
            "estatus, \"idRol\", rol, \"idTipoUsuario\", \"tipoUsuario\" " +
            "FROM \"public\".\"usuarioFullInfo\" " +
            "WHERE username = #{username}")
    Optional<UsuarioAuth> buscarPorUsername(String username); 
    //usamos optional para evitar conflictos con NullPointerException si el usario no existe
}