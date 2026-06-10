package mx.uv.sicae.vehicle.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class VehiculoRequest {
    @NotNull(message = "idUsuario es obligatorio")
    private Integer idUsuario;

    @NotNull(message = "idModelo es obligatorio")
    private Integer idModelo;

    @NotBlank(message = "placa es obligatoria")
    @Size(max = 7, message = "placa no debe exceder 7 caracteres")
    private String placa;

    @NotBlank(message = "color es obligatorio")
    @Size(max = 20, message = "color no debe exceder 20 caracteres")
    private String color;

    @NotNull(message = "anio es obligatorio")
    @Min(value = 1900, message = "anio no es valido")
    @Max(value = 2100, message = "anio no es valido")
    private Integer anio;

    @Size(max = 255, message = "descripcion no debe exceder 255 caracteres")
    private String descripcion;

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Integer idModelo) {
        this.idModelo = idModelo;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Integer getAnio() {
        return anio;
    }

    public void setAnio(Integer anio) {
        this.anio = anio;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
