package mx.uv.sicae.vehicle.dto;

import jakarta.validation.constraints.NotNull;

public class EstatusVehiculoRequest {
    @NotNull(message = "activo es obligatorio")
    private Boolean activo;

    public Boolean getActivo() {
        return activo;
    }

    public void setActivo(Boolean activo) {
        this.activo = activo;
    }
}
