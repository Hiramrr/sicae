package mx.uv.sicae.vehicle.entity;

import java.time.LocalDateTime;

public class VehiculoEntity {
    private Integer idVehiculo;
    private Integer idUsuario;
    private String claveVehiculo;
    private Integer idMarca;
    private String marca;
    private Integer idModelo;
    private String modelo;
    private String placa;
    private String color;
    private Integer anio;
    private String descripcion;
    private Boolean estatus;
    private LocalDateTime tiempoCreacion;
    private LocalDateTime tiempoActualizacion;

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getClaveVehiculo() {
        return claveVehiculo;
    }

    public void setClaveVehiculo(String claveVehiculo) {
        this.claveVehiculo = claveVehiculo;
    }

    public Integer getIdMarca() {
        return idMarca;
    }

    public void setIdMarca(Integer idMarca) {
        this.idMarca = idMarca;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public Integer getIdModelo() {
        return idModelo;
    }

    public void setIdModelo(Integer idModelo) {
        this.idModelo = idModelo;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
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

    public Boolean getEstatus() {
        return estatus;
    }

    public void setEstatus(Boolean estatus) {
        this.estatus = estatus;
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
}
