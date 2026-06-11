package mx.uv.sicae.vehicle.dto;

import jakarta.validation.constraints.NotNull;

public class EstatusVehiculoRequest {
    @NotNull(message = "idUsuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "activo es obligatorio")
    private Boolean activo;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
