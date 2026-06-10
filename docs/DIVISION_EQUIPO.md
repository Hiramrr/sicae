# Division de trabajo para 4 integrantes

Descripcion basada en los requerimientos del PDF del proyecto SICAE.

## Integrante 1 - AuthService

### Servicio asignado

Servicio de Autenticacion.

### Lo que debe desarrollar

Implementar el servicio de login del sistema. Este servicio permite autenticar usuarios mediante credenciales validas, es decir, usuario y contrasena, para obtener acceso al sistema.

### Reglas de negocio esperadas

- Solo usuarios registrados pueden iniciar sesion.
- Solo usuarios activos pueden iniciar sesion.
- Los datos obligatorios son usuario y contrasena.
- El servicio debe generar un token de autenticacion JWT.
- El token generado debe usarse para consumir los demas servicios del sistema.
- La contrasena debe validarse considerando que estara cifrada con bcrypt en la base de datos.

### Validaciones que debe implementar

- Validar que usuario y contrasena no vengan vacios.
- Validar el tamano de los campos.
- Verificar que el usuario exista en la base de datos.
- Verificar que la contrasena sea correcta.
- Verificar que el usuario tenga estatus activo.

### Respuesta esperada

Cuando el login sea correcto, debe devolver:

- idUsuario;
- idRol;
- rol;
- usuario;
- nombre completo;
- idTipoUsuario;
- tipo de usuario;
- token JWT.

Cuando el login falle, debe devolver un mensaje claro indicando la causa.

### Entregables del integrante

- Codigo fuente del AuthService.
- Configuracion de conexion a PostgreSQL.
- Implementacion de JWT.
- Validacion de bcrypt.
- Endpoint probado en Postman.
- Explicacion preparada de como se genera y valida el token.

## Integrante 2 - UserService

### Servicio asignado

Servicio de Usuario.

### Lo que debe desarrollar

Implementar las operaciones para administrar usuarios del sistema: registrar usuarios, editar usuarios, ver perfil y cambiar estatus.

### Funcionalidades esperadas

#### Registrar usuario

Permite registrar nuevos usuarios en el sistema.

Reglas de negocio:

- Solo usuarios con rol de administrador pueden registrar usuarios.
- El usuario debe autenticarse previamente mediante login.
- La solicitud debe incluir un token JWT valido.
- No deben existir usuarios con el mismo correo.
- No deben existir usuarios con el mismo username.
- La contrasena debe cifrarse con bcrypt.
- El usuario debe quedar activo por defecto.
- La clave de usuario debe generarse internamente.
- La clave de usuario debe ser unica.
- La clave debe seguir un patron acordado, por ejemplo `RGR-254`.

Datos obligatorios:

- rol;
- tipo de usuario;
- nombre;
- apellido paterno;
- programa educativo;
- usuario;
- contrasena;
- correo;
- telefono;
- tiempo de registro;
- clave de usuario generada internamente.

Validaciones:

- Validar datos obligatorios.
- Validar formato de correo.
- Validar que no exista correo repetido.
- Validar que no exista username repetido.
- Validar el tamano de los campos.

Respuesta esperada:

- Mensaje de exito si se registro correctamente.
- Mensaje de error con causa si no se pudo registrar.

#### Editar usuario

Permite actualizar informacion de un usuario existente.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- No se puede editar directamente el usuario.
- No se puede editar directamente la contrasena.
- No se puede editar directamente la clave del usuario.

Datos obligatorios:

- rol;
- tipo de usuario;
- nombre;
- apellido paterno;
- programa educativo;
- usuario;
- contrasena;
- correo;
- telefono;
- tiempo de actualizacion.

Validaciones:

- Validar datos obligatorios.
- Validar formato de correo.
- Validar que no exista otro usuario con el mismo correo.
- Validar el tamano de los campos.

Respuesta esperada:

- Mensaje de exito si se actualizo correctamente.
- Mensaje de error con causa si no se pudo actualizar.

#### Ver perfil

Permite visualizar la informacion completa del usuario.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- Debe enviarse el identificador del usuario.

Datos obligatorios:

- idUsuario.

Respuesta esperada:

- rol;
- nombre completo;
- tipo de usuario;
- programa educativo;
- usuario;
- correo;
- telefono;
- estatus;
- clave de usuario;
- tiempo de creacion;
- tiempo de actualizacion.

#### Cambiar estatus

Permite actualizar el estatus del usuario a activo o inactivo.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- Solo usuarios con rol de administrador pueden cambiar el estatus de otro usuario.
- No se debe eliminar fisicamente al usuario.

Datos obligatorios:

- idUsuario;
- idRol.

Respuesta esperada:

- Mensaje de exito si se cambio el estatus correctamente.
- Mensaje de error con causa si no se pudo cambiar.

### Entregables del integrante

- Codigo fuente del UserService.
- Conexion a PostgreSQL.
- Repositorios/MyBatis para usuarios y catalogos.
- Endpoints probados en Postman.
- Validaciones documentadas.
- Explicacion preparada de reglas de administrador, estatus y bcrypt.

## Integrante 3 - VehicleService

### Servicio asignado

Servicio de Vehiculo.

### Lo que debe desarrollar

Implementar las operaciones para administrar vehiculos asociados a usuarios: buscar vehiculos, registrar, editar y cambiar estatus.

### Funcionalidades esperadas

#### Buscar vehiculos

Permite buscar o cargar solo los vehiculos de un usuario especifico.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- Solo deben devolverse vehiculos asociados al usuario solicitado.

Datos obligatorios:

- idUsuario.

Validaciones:

- Validar datos obligatorios.

Respuesta esperada:

- idUsuario;
- idVehiculo;
- idModelo;
- modelo;
- idMarca;
- marca;
- placa;
- color;
- anio;
- descripcion;
- estatus.

#### Registrar vehiculo

Permite registrar y asociar vehiculos a un usuario registrado.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- No deben existir vehiculos con la misma placa.
- El vehiculo debe quedar activo por defecto.
- Un usuario solo puede tener 4 vehiculos activos al mismo tiempo.
- Si ya tiene 4 vehiculos activos, no se debe permitir registrar otro hasta desactivar uno.

Datos obligatorios:

- idUsuario;
- idVehiculo;
- idModelo;
- placa;
- color;
- anio;
- estatus;
- descripcion.

Validaciones:

- Validar datos obligatorios.
- Validar que el usuario no tenga mas de 4 vehiculos activos.
- Validar que no exista otro vehiculo con la misma placa.
- Validar el tamano de los campos.

Respuesta esperada:

- Mensaje de exito si se registro correctamente.
- Mensaje de error con causa si no se pudo registrar.

#### Editar vehiculo

Permite actualizar informacion de un vehiculo existente.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- Solo se puede actualizar un vehiculo asociado al usuario autenticado.

Datos obligatorios:

- idUsuario;
- idVehiculo;
- idModelo;
- placa;
- color;
- anio;
- descripcion.

Validaciones:

- Validar datos obligatorios.
- Validar que no exista otro vehiculo con la misma placa.
- Validar el tamano de los campos.

Respuesta esperada:

- Mensaje de exito si se actualizo correctamente.
- Mensaje de error con causa si no se pudo actualizar.

#### Cambiar estatus

Permite actualizar el estatus del vehiculo a activo o inactivo.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.
- Solo se puede cambiar el estatus de vehiculos asociados al usuario autenticado.
- No se debe eliminar fisicamente el vehiculo.

Datos obligatorios:

- idUsuario;
- idVehiculo.

Respuesta esperada:

- Mensaje de exito si se cambio el estatus correctamente.
- Mensaje de error con causa si no se pudo cambiar.

### Entregables del integrante

- Codigo fuente del VehicleService.
- Conexion a MySQL 8.
- Endpoints de vehiculos probados en Postman.
- Validacion de placas duplicadas.
- Validacion de maximo 4 vehiculos activos.
- Explicacion preparada de asociacion usuario-vehiculo.

## Integrante 4 - ParkingService

### Servicio asignado

Servicio de Estacionamiento.

### Lo que debe desarrollar

Implementar el registro de entrada, registro de salida, calculo de permanencia y costo, y consulta de espacios del estacionamiento.

### Funcionalidades esperadas

#### Registrar movimiento de entrada

Permite registrar la entrada de vehiculos al estacionamiento.

Reglas de negocio:

- Solo pueden entrar vehiculos asociados a un usuario registrado y activo.
- Solo pueden entrar vehiculos activos.
- Maximo pueden acceder 2 vehiculos por usuario al mismo tiempo.
- Debe validarse la asociacion entre usuario y vehiculo.
- Si la entrada es valida, se debe crear el movimiento.
- Despues de crear el movimiento, se debe marcar el espacio asignado como ocupado.

Datos obligatorios:

- claveUsuario;
- placa;
- tiempo de entrada;
- tiempo de creacion;
- tarifa;
- idEspacio.

Validaciones:

- Validar que el usuario este activo.
- Validar datos obligatorios.
- Validar que el vehiculo pertenezca al usuario.
- Validar que el vehiculo este activo.
- Validar que el usuario no tenga mas de 2 vehiculos dentro del estacionamiento.
- Validar que el espacio este disponible.

Respuesta esperada:

- idMovimiento;
- tiempo de entrada;
- espacio asignado;
- tarifa por hora.

Si no puede ingresar, debe devolver un mensaje claro con la causa.

#### Actualizar movimiento de salida

Permite registrar la salida de vehiculos del estacionamiento.

Reglas de negocio:

- Solo pueden salir vehiculos asociados a un usuario registrado y activo.
- Debe validarse la asociacion entre usuario y vehiculo.
- Si la salida es valida, se debe actualizar el movimiento.
- Despues de registrar la salida, se debe liberar el espacio correspondiente.
- Debe calcularse el costo total.
- Deben calcularse las horas cobradas y minutos estacionado.

Datos obligatorios:

- claveUsuario;
- placa;
- tiempo de salida;
- tiempo de actualizacion;
- costo total;
- horas cobradas;
- minutos estacionado.

Validaciones:

- Validar que el usuario este activo.
- Validar datos obligatorios.
- Validar que el vehiculo pertenezca al usuario.
- Validar que exista un movimiento de entrada abierto.

Respuesta esperada:

- idMovimiento;
- tiempo de entrada;
- tiempo de salida;
- espacio asignado;
- tarifa por hora;
- costo total;
- horas cobradas.

Si no puede salir, debe devolver un mensaje claro con la causa.

#### Consultar espacios

Permite consultar los espacios del estacionamiento.

Reglas de negocio:

- El usuario debe autenticarse previamente.
- La solicitud debe incluir token JWT valido.

Respuesta esperada:

- Datos de los espacios disponibles y ocupados, segun el diseno final del equipo.

### Entregables del integrante

- Codigo fuente del ParkingService.
- Conexion a MySQL 8.
- Endpoints de entrada, salida y espacios probados en Postman.
- Calculo correcto de tiempo y costo.
- Actualizacion correcta de espacios ocupados/libres.
- Integracion REST con UserService y VehicleService, si el equipo decide validarlo por servicios.
- Explicacion preparada del flujo completo de entrada y salida.

## Responsabilidades de todos los integrantes

### Reglas generales del sistema

- Toda solicitud protegida debe incluir token JWT valido.
- Cada microservicio debe validar datos obligatorios.
- Cada microservicio debe validar formatos y restricciones de negocio.
- No deben permitirse registros duplicados.
- Todos los servicios deben retornar mensajes claros de exito o error.
- Cada microservicio debe usar arquitectura en capas: Controller, Service y Repository.
- Cada microservicio debe administrar exclusivamente su propia base de datos.
- No deben existir relaciones fisicas entre bases de datos de dominios diferentes.
- Las entidades principales no deben eliminarse fisicamente; solo debe cambiarse su estatus.

### Actividades compartidas

- Actualizar la coleccion Postman con los endpoints reales.
- Probar casos correctos y casos de error.
- Documentar cambios en `docs/API_CONTRATOS.md`.
- Mantener Dockerfiles y configuracion funcionando.
- Participar en la documentacion tecnica.
- Preparar una explicacion individual de su servicio.
- Preparar una explicacion grupal de la arquitectura completa.

## Orden recomendado de desarrollo

1. Acordar rutas, nombres de campos y formato de respuestas.
2. Importar scripts SQL del profesor.
3. Desarrollar AuthService para obtener JWT.
4. Desarrollar UserService y VehicleService en paralelo.
5. Desarrollar ParkingService e integrar validaciones entre servicios.
6. Completar coleccion Postman.
7. Probar despliegue completo con Docker.
8. Preparar documento tecnico y demo.
