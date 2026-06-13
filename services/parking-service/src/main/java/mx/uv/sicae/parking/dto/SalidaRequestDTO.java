package mx.uv.sicae.parking.dto;

import jakarta.validation.constraints.NotBlank;

public class SalidaRequestDTO {

    @NotBlank(message = "La clave de usuario es obligatoria")
    private String claveUsuario;

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    public String getClaveUsuario() { return claveUsuario; }
    public void setClaveUsuario(String claveUsuario) { this.claveUsuario = claveUsuario; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
}
