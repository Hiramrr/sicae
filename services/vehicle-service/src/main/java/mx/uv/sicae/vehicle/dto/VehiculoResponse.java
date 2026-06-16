package mx.uv.sicae.vehicle.dto;

import mx.uv.sicae.vehicle.model.VehiculoEntity;

public class VehiculoResponse {
    // Estos campos son los que se regresan ya listos para la app cliente
    private Integer idUsuario;
    private Integer idVehiculo;
    private Integer idModelo;
    private String modelo;
    private Integer idMarca;
    private String marca;
    private String placa;
    private String color;
    private Integer anio;
    private String descripcion;
    private Boolean estatus;

    public static VehiculoResponse fromEntity(VehiculoEntity entity) {
        // paso la entidad de base de datos a un objeto mas limpio para responder
        VehiculoResponse response = new VehiculoResponse();
        response.setIdUsuario(entity.getIdUsuario());
        response.setIdVehiculo(entity.getIdVehiculo());
        response.setIdModelo(entity.getIdModelo());
        response.setModelo(entity.getModelo());
        response.setIdMarca(entity.getIdMarca());
        response.setMarca(entity.getMarca());
        response.setPlaca(entity.getPlaca());
        response.setColor(entity.getColor());
        response.setAnio(entity.getAnio());
        response.setDescripcion(entity.getDescripcion());
        response.setEstatus(entity.getEstatus());
        return response;
    }

    public Integer getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(Integer idUsuario) {
        this.idUsuario = idUsuario;
    }

    public Integer getIdVehiculo() {
        return idVehiculo;
    }

    public void setIdVehiculo(Integer idVehiculo) {
        this.idVehiculo = idVehiculo;
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
}
