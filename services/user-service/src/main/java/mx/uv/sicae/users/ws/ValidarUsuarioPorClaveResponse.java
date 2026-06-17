package mx.uv.sicae.users.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "validarUsuarioPorClaveResponse", namespace = "http://sicae.uv.mx/users/validation")
@XmlAccessorType(XmlAccessType.FIELD)
// Lo que el servicio SOAP responde cuando le preguntan por un usuario.
public class ValidarUsuarioPorClaveResponse {

    @XmlElement(name = "idUsuario", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private int idUsuario;

    @XmlElement(name = "claveUsuario", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private String claveUsuario;

    @XmlElement(name = "nombreCompleto", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private String nombreCompleto;

    @XmlElement(name = "activo", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private boolean activo;

    @XmlElement(name = "rol", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private String rol;

    @XmlElement(name = "tipoUsuario", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private String tipoUsuario;

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }
}
