package mx.uv.sicae.users.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import mx.uv.sicae.users.model.UsuarioPerfil;
import mx.uv.sicae.users.model.UsuarioEntity;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UsuarioRepository {

    UsuarioPerfil buscarPerfilPorId(Integer idUsuario);
    
    UsuarioPerfil buscarPorEmail(@Param("email") String email);

    UsuarioPerfil buscarPorUsername(@Param("username") String username);

    UsuarioPerfil buscarPorClaveUsuario(@Param("claveUsuario") String claveUsuario);

    List<UsuarioPerfil> listarTodos();
    
    @Options(useGeneratedKeys = true, keyProperty = "idUsuario")    
    int insertar(UsuarioEntity usuario);

    int actualizar(UsuarioEntity usuario);

    int cambiarEstatus(@Param("idUsuario") Integer idUsuario, @Param("estatus") Boolean estatus);
}
