package mx.uv.sicae.auth.model;

//Esta clase representa los datos obligatorios que vamos a recibir del cliente
public class LoginRequest {
    private String usuario;
    private String contrasena;

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }
}