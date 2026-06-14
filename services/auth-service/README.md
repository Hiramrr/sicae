# Microservicio: AuthService

Este es el microservicio encargado de la seguridad y autenticación. Su principal responsabilidad es validar las credenciales de los usuarios contra la base de datos de PostgreSQL y emitir un **JSON Web Token (JWT)** válido para interactuar con el resto de los módulos (Usuarios, Vehículos, Parqueo).

## Puerto de Ejecución
**`8081`**.

---

## Cómo obtener tu JWT

Los tokens expiran cada 24 horas pero bien esto se puede cambiar desde el application.properties , debes generar uno cada vez que vayas a realizar pruebas en tus propios microservicios.

En el script de la base de datos (`001_sicaeUsuario.sql`) se inserta automaticamente el **usuario de prueba** con una contraseña encriptada  en BCrypt para poder hacer pruebas. 

### 1. Endpoint de Login
Para generar tu token, realiza la siguiente petición desde Postman:

**Ruta:** `POST http://localhost:8081/auth/login`

**Body (JSON):**
```json
{
  "usuario": "prueba",
  "contraseña": "prueba"
}
```

### 2. Respuesta 
Si tu base de datos `users-db` está levantada y el AuthService corriendo, recibirás un `Status 200 OK` con la siguiente estructura:

```json
{
  "success": true,
  "message": "Login exitoso",
  "data": {
    "idUsuario": 2,
    "idRol": 1,
    "rol": "administrador",
    "usuario": "prueba",
    "nombreCompleto": "Usuario Prueba",
    "idTipoUsuario": 1,
    "tipoUsuario": "Docente",
    "token": "eyJhbGciOiJIUzI1NiJ9.eyJpZFJv..." 
  },
  "error": null
}
```

### 3. Uso en los demás servicios
Copia el valor del campo `"token"`. Para probar tus propios endpoints, deberás enviar este token en el Header de tus peticiones HTTP:
* **Key:** `Authorization`
* **Value:** `Bearer eyJhbGciOiJIUzI1...`.

---

## Manejo de Errores
Este servicio atrapa excepciones de seguridad y devuelve mensajes estandarizados.
* Si el usuario no existe o la contraseña es incorrecta (falla bcrypt), devolverá un `401 Unauthorized`.
* Si el usuario tiene estatus inactivo `0`, devolverá un `401 Unauthorized` indicando la inactividad.