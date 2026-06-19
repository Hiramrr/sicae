package mx.uv.sicae.parking.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public class EntradaRequestDTO {


    @NotBlank(message = "La clave de usuario es obligatoria")
    private String claveUsuario;

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    @NotNull(message = "La tarifa por hora es obligatoria")
    @Positive(message = "La tarifa debe ser mayor a 0")
    private BigDecimal tarifaHora;

    @NotNull(message = "El ID del espacio es obligatorio")
    private Integer idEspacio;

    public String getClaveUsuario() { return claveUsuario; }
    public void setClaveUsuario(String claveUsuario) { this.claveUsuario = claveUsuario; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }

    public BigDecimal getTarifaHora() { return tarifaHora; }
    public void setTarifaHora(BigDecimal tarifaHora) { this.tarifaHora = tarifaHora; }

    public Integer getIdEspacio() { return idEspacio; }
    public void setIdEspacio(Integer idEspacio) { this.idEspacio = idEspacio; }
}
