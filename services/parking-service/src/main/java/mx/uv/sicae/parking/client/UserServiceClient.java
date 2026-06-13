package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.Usuario;

public interface UserServiceClient {
    Usuario validarUsuario(String claveUsuario);
}
