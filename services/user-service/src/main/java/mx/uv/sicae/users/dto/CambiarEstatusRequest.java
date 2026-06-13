package mx.uv.sicae.users.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class CambiarEstatusRequest {
    private Integer idUsuario;
    @JsonAlias("activo")
    private Boolean estatus;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
    }
}
