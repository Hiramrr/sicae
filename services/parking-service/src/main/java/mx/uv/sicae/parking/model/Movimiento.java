package mx.uv.sicae.parking.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class Movimiento {
    private Integer idMovimiento;
    private Integer idVehiculo;
    private LocalDateTime tiempoEntrada;
    private LocalDateTime tiempoSalida;
    private Integer minutosEstacionado;
    private Integer horasCobradas;
    private BigDecimal costoTotal;

    @NotNull(message = "La tarifa es obligatoria")
    private BigDecimal tarifaHora;

    private LocalDateTime tiempoCreacion;
    private LocalDateTime tiempoActualizacion;

    @NotNull(message = "El id del espacio es obligatorio")
    private Integer idEspacio;

    @NotBlank(message = "La clave de usuario es obligatoria")
    private String claveUsuario;

    @NotBlank(message = "La placa es obligatoria")
    private String placa;

    private String espacioAsignado;

    public Integer getIdMovimiento() {
        return idMovimiento;
    }

    public void setIdMovimiento(Integer idMovimiento) {
        this.idMovimiento = idMovimiento;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public LocalDateTime getTiempoEntrada() {
        return tiempoEntrada;
    }

    public void setTiempoEntrada(LocalDateTime tiempoEntrada) {
        this.tiempoEntrada = tiempoEntrada;
    }

    public LocalDateTime getTiempoSalida() {
        return tiempoSalida;
    }

    public void setTiempoSalida(LocalDateTime tiempoSalida) {
        this.tiempoSalida = tiempoSalida;
    }

    public Integer getMinutosEstacionado() {
        return minutosEstacionado;
    }

    public void setMinutosEstacionado(Integer minutosEstacionado) {
        this.minutosEstacionado = minutosEstacionado;
    }

    public Integer getHorasCobradas() {
        return horasCobradas;
    }

    public void setHorasCobradas(Integer horasCobradas) {
        this.horasCobradas = horasCobradas;
    }

    public BigDecimal getCostoTotal() {
        return costoTotal;
    }

    public void setCostoTotal(BigDecimal costoTotal) {
        this.costoTotal = costoTotal;
    }

    public BigDecimal getTarifaHora() {
        return tarifaHora;
    }

    public void setTarifaHora(BigDecimal tarifaHora) {
        this.tarifaHora = tarifaHora;
    }

    public LocalDateTime getTiempoCreacion() {
        return tiempoCreacion;
    }

    public void setTiempoCreacion(LocalDateTime tiempoCreacion) {
        this.tiempoCreacion = tiempoCreacion;
    }

    public LocalDateTime getTiempoActualizacion() {
        return tiempoActualizacion;
    }

    public void setTiempoActualizacion(LocalDateTime tiempoActualizacion) {
        this.tiempoActualizacion = tiempoActualizacion;
    }

    public Integer getIdEspacio() {
        return idEspacio;
    }

    public void setIdEspacio(Integer idEspacio) {
        this.idEspacio = idEspacio;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getPlaca() {
        return placa;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public String getEspacioAsignado() {
        return espacioAsignado;
    }

    public void setEspacioAsignado(String espacioAsignado) {
        this.espacioAsignado = espacioAsignado;
    }
}
