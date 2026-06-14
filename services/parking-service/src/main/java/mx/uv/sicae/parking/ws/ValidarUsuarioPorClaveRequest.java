package mx.uv.sicae.parking.ws;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "validarUsuarioPorClaveRequest", namespace = "http://sicae.uv.mx/users/validation")
@XmlAccessorType(XmlAccessType.FIELD)
public class ValidarUsuarioPorClaveRequest {

    @XmlElement(name = "claveUsuario", namespace = "http://sicae.uv.mx/users/validation", required = true)
    private String claveUsuario;

    public String getClaveUsuario() {
        return claveUsuario;
    }

    public void setClaveUsuario(String claveUsuario) {
        this.claveUsuario = claveUsuario;
    }
}
