package mx.uv.sicae.users.repository;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import mx.uv.sicae.users.model.UsuarioPerfil;
import mx.uv.sicae.users.model.UsuarioEntity;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface UsuarioRepository {

    // Busca un usuario por su id, devuelve el perfil completo.
    UsuarioPerfil buscarPerfilPorId(Integer idUsuario);

    // Busca un usuario por su correo.
    UsuarioPerfil buscarPorEmail(@Param("email") String email);

    // Busca un usuario por su nombre de usuario.
    UsuarioPerfil buscarPorUsername(@Param("username") String username);

    // Busca un usuario por su clave (ej. RGR-ABC123).
    UsuarioPerfil buscarPorClaveUsuario(@Param("claveUsuario") String claveUsuario);

    // Lista todos los usuarios registrados.
    List<UsuarioPerfil> listarTodos();

    // Inserta un usuario nuevo. MyBatis asigna el id automaticamente.
    @Options(useGeneratedKeys = true, keyProperty = "idUsuario")    
    int insertar(UsuarioEntity usuario);

    // Actualiza los datos de un usuario existente.
    int actualizar(UsuarioEntity usuario);

    // Cambia el estatus (activo/inactivo) de un usuario.
    int cambiarEstatus(@Param("idUsuario") Integer idUsuario, @Param("estatus") Boolean estatus);
}
