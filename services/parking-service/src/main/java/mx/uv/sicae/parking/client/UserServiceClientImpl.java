package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.Usuario;
import org.springframework.stereotype.Service;

@Service
public class UserServiceClientImpl implements UserServiceClient {

    @Override
    public Usuario validarUsuario(String claveUsuario) {
        Usuario usuario = new Usuario();
        usuario.setIdUsuario(1);
        usuario.setClaveUsuario(claveUsuario);
        usuario.setActivo(true);
        return usuario;
    }
}
