package mx.uv.sicae.parking.dto;

import jakarta.validation.constraints.NotBlank;


public class SalidaRequestDTO {

    @NotBlank(message = "La clave de usuario es obligatoria")
    private String claveUsuario;

    // Se exige la placa en la salida para poder validar la regla de negocio que asegura
    // que el vehículo que está saliendo es el mismo que registró la entrada.
    @NotBlank(message = "La placa es obligatoria")
    private String placa;


    public String getClaveUsuario() { return claveUsuario; }
    public void setClaveUsuario(String claveUsuario) { this.claveUsuario = claveUsuario; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
}
