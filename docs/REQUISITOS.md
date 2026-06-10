# Requisitos resumidos del proyecto

Resumen operativo basado en `Proyecto_Final.pdf`.

## Objetivo

Desarrollar SICAE como sistema distribuido de control de acceso a estacionamiento privado, usando microservicios orientados al dominio, bases de datos independientes, seguridad con JWT/bcrypt y despliegue con contenedores.

## Alcance

Incluye:

- diseno de microservicios independientes;
- arquitectura interna por capas: Controller, Service y Repository;
- contenerizacion de servicios y bases de datos;
- despliegue distribuido;
- pruebas de API con Postman o Hoppscotch.
- integracion entre servicios mediante REST, SOAP, RPC o gRPC segun lo visto en clase.

No incluye:

- cliente web;
- aplicacion movil;
- aplicacion de escritorio;
- interfaz grafica de usuario.

## Servicios

### AuthService

- Login con usuario y contrasena.
- Solo usuarios registrados y activos pueden iniciar sesion.
- Genera token JWT.
- Responde con datos del usuario y token.

### UserService

- Registrar usuarios.
- Editar usuarios.
- Ver perfil.
- Cambiar estatus activo/inactivo.
- Solo administradores pueden registrar usuarios y cambiar estatus de otros usuarios.
- No se elimina fisicamente a los usuarios.
- Contrasenas cifradas con bcrypt.
- Clave de usuario unica con patron acordado por el equipo.

### VehicleService

- Buscar vehiculos de un usuario.
- Registrar vehiculos.
- Editar vehiculos.
- Cambiar estatus activo/inactivo.
- No se permiten placas duplicadas.
- Maximo 4 vehiculos activos por usuario.
- Solo se pueden modificar vehiculos asociados al usuario autenticado.

### ParkingService

- Registrar movimiento de entrada.
- Actualizar movimiento de salida.
- Consultar espacios.
- Al entrar, marcar espacio como ocupado.
- Al salir, liberar espacio.
- Calcular tiempo de permanencia y costo.
- Maximo 2 vehiculos dentro por usuario.

## Reglas generales

- Todas las solicitudes protegidas requieren token JWT valido.
- Validar datos obligatorios, formatos y tamanos.
- No permitir duplicados.
- Retornar mensajes claros de exito o error.
- Cada microservicio administra solo su base de datos.
- No crear relaciones fisicas entre dominios.
- No eliminar entidades principales, solo cambiar estatus.
- Las validaciones entre dominios deben hacerse por comunicacion entre servicios, no consultando directamente bases de datos ajenas.

## Comunicacion

- REST + JSON para las APIs publicas y pruebas con Postman/Hoppscotch.
- SOAP, RPC o gRPC para integracion interna entre microservicios, segun lo acordado con el profesor.
- Propuesta: UserService expone validacion SOAP para ParkingService; VehicleService expone validacion gRPC/RPC para ParkingService.

## Bases de datos

PostgreSQL para usuarios, base `sicaeUsuario`:

- usuarios;
- roles;
- tipos de usuario;
- programas educativos.

MySQL 8 para vehiculos, base `sicaevehiculo`:

- marcas;
- modelos;
- vehiculos.

MySQL 8 para estacionamiento, base `sicaeEstacionamiento`:

- cajones de estacionamiento;
- movimientos de estacionamiento.

## Entregables

- Codigo fuente de microservicios y bases de datos.
- Documento tecnico.
- Coleccion de Postman.
- Dockerfiles, scripts y configuracion.
- Carpeta comprimida con nomenclatura `Equipo_NumEquipo.zip`, por ejemplo `Equipo_3.zip`.

## Nota importante

El PDF menciona "3 proyectos de microservicios", pero tambien separa AuthService, UserService, VehicleService y ParkingService. Antes de cerrar arquitectura, confirmen con el profesor si AuthService y UserService deben ser proyectos separados o si cuentan como servicios separados dentro del servidor 1.
