package mx.uv.sicae.parking.dto;

import jakarta.validation.constraints.NotBlank;

// DTO (Data Transfer Object): Al igual que con la entrada, esta clase sirve para
// mapear y validar exclusivamente los datos que el cliente envía en el Body (JSON)
// de la petición PATCH al momento de solicitar la salida de un vehículo.
public class SalidaRequestDTO {

    // @NotBlank es una validación para cadenas de texto (String).
    // Garantiza que el campo no venga nulo, no esté completamente vacío ("")
    // y no contenga únicamente espacios en blanco ("   ").
    @NotBlank(message = "La clave de usuario es obligatoria")
    private String claveUsuario;

    // Se exige la placa en la salida para poder validar la regla de negocio que asegura
    // que el vehículo que está saliendo es exactamente el mismo que registró la entrada.
    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    // Los Getters y Setters son indispensables. La librería Jackson de Spring Boot
    // los utiliza en segundo plano para convertir automáticamente el texto JSON
    // que llega desde Postman o el Frontend en este objeto Java.
    public String getClaveUsuario() { return claveUsuario; }
    public void setClaveUsuario(String claveUsuario) { this.claveUsuario = claveUsuario; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
}
