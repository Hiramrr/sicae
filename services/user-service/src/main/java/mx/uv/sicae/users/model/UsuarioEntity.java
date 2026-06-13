package mx.uv.sicae.users.model;

import java.time.LocalDateTime;

public class UsuarioEntity {
    private Integer idUsuario;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String email;
    private String claveUsuario;
    private String telefono;
    private String username;
    private String password;
    private Boolean estatus;
    private Integer idRol;
    private Integer idTipoUsuario;
    private Integer idProgramaEducativo;
    private LocalDateTime tiempoCreacion;
    private LocalDateTime tempoActualizacion;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidoPaterno() {
        return apellidoPaterno;
    }

    public void setApellidoPaterno(String apellidoPaterno) {
        this.apellidoPaterno = apellidoPaterno;
    }

    public String getApellidoMaterno() {
        return apellidoMaterno;
    }
    
    public void setApellidoMaterno(String apellidoMaterno) {
        this.apellidoMaterno = apellidoMaterno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }
    
    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getTelefono() {
        return telefono;
    }
        
    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean getEstatus() {
        return estatus;
    }
    
    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }

    public Integer getIdRol() {
        return idRol;
    }
    
    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }
    
    public Integer getIdTipoUsuario() {
        return idTipoUsuario;
    }
    
    public void setIdTipoUsuario(Integer idTipoUsuario) {
        this.idTipoUsuario = idTipoUsuario;
    }
    
    public Integer getIdProgramaEducativo() {
        return idProgramaEducativo;
    }
    
    public void setIdProgramaEducativo(Integer idProgramaEducativo) {
        this.idProgramaEducativo = idProgramaEducativo;
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
