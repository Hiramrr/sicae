package mx.uv.sicae.users.dto;

import mx.uv.sicae.users.model.UsuarioPerfil;
import java.time.LocalDateTime;

public class UsuarioResponse {
    private Integer idUsuario;
    private String rol;
    private String nombreCompleto;
    private String tipoUsuario;
    private String programaEducativo;
    private String username;
    private String email;
    private String telefono;
    private Boolean estatus;
    private String claveUsuario;
    private LocalDateTime tiempoCreacion;
    private LocalDateTime tempoActualizacion;

    // Convierte un UsuarioPerfil (de la BD) en UsuarioResponse (para la API).
    public static UsuarioResponse fromEntity(UsuarioPerfil perfil) {
        UsuarioResponse response = new UsuarioResponse();
        response.setIdUsuario(perfil.getIdUsuario());
        response.setRol(perfil.getRol());
        response.setNombreCompleto(perfil.getNombreCompleto());
        response.setTipoUsuario(perfil.getTipoUsuario());
        response.setProgramaEducativo(perfil.getProgramaEducativo());
        response.setUsername(perfil.getUsername());
        response.setEmail(perfil.getEmail());
        response.setTelefono(perfil.getTelefono());
        response.setEstatus(perfil.getEstatus());
        response.setClaveUsuario(perfil.getClaveUsuario());
        response.setTiempoCreacion(perfil.getTiempoCreacion());
        response.setTempoActualizacion(perfil.getTempoActualizacion());
        return response;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getProgramaEducativo() {
        return programaEducativo;
    }

    public void setProgramaEducativo(String programaEducativo) {
        this.programaEducativo = programaEducativo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public LocalDateTime getTiempoCreacion() {
        return tiempoCreacion;
    }

    public void setTiempoCreacion(LocalDateTime tiempoCreacion) {
        this.tiempoCreacion = tiempoCreacion;
    }

    public LocalDateTime getTempoActualizacion() {
        return tempoActualizacion;
    }

    public void setTempoActualizacion(LocalDateTime tempoActualizacion) {
        this.tempoActualizacion = tempoActualizacion;
    }
}
