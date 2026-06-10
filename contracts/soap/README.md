# Contratos SOAP

Usen esta carpeta para el WSDL o la descripcion formal del servicio SOAP.

Contrato sugerido:

```text
UserValidationService
validarUsuarioPorClave(claveUsuario)
```

Objetivo:

- Permitir que ParkingService valide que un usuario existe y esta activo sin conectarse directamente a la base de datos de usuarios.
