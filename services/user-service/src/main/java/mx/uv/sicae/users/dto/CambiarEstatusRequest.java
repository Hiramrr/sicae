package mx.uv.sicae.users.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

// Dato que envia el cliente para activar/desactivar un usuario.
public class CambiarEstatusRequest {
    private Integer idUsuario;
    private Integer idRol;
    private Boolean estatus;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdRol() {
        return idRol;
    }

    public void setIdRol(Integer idRol) {
        this.idRol = idRol;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }
}
