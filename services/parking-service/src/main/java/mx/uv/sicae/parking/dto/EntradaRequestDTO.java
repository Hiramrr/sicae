package mx.uv.sicae.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

// DTO (Data Transfer Object): Esta clase sirve exclusivamente para mapear y validar
// los datos que nos envían desde el frontend o Postman en el Body (JSON) de la petición.
// Al separar esto del Modelo (Entity), evitamos exponer o modificar directamente la base de datos.
public class EntradaRequestDTO {

    // @NotBlank es específico para textos (String). Valida tres cosas a la vez:
    // 1. Que no sea null. 2. Que no esté vacío (""). 3. Que no sean puros espacios ("   ").
    @NotBlank(message = "La clave de usuario es obligatoria")
    private String claveUsuario;

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    // @NotNull se utiliza para objetos y números, solo verifica que el campo sí venga en el JSON.
    @NotNull(message = "La tarifa por hora es obligatoria")
    // @Positive protege la regla de negocio asegurando que no envíen tarifas negativas ni gratuitas (0).
    @Positive(message = "La tarifa debe ser mayor a 0")
    // Se utiliza BigDecimal en lugar de Double o Float porque es el estándar en Java
    // para manejar dinero y cálculos financieros sin perder precisión en los centavos.
    private BigDecimal tarifaHora;

    @NotNull(message = "El ID del espacio es obligatorio")
    private Integer idEspacio;

    // Los Getters y Setters son estrictamente necesarios.
    // La librería de Spring que convierte el JSON a objetos Java (Jackson)
    // los utiliza por detrás para "llenar" la información de esta clase.
    public String getClaveUsuario() { return claveUsuario; }
    public void setClaveUsuario(String claveUsuario) { this.claveUsuario = claveUsuario; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }

    public Integer getIdEspacio() { return idEspacio; }
    public void setIdEspacio(Integer idEspacio) { this.idEspacio = idEspacio; }
}
