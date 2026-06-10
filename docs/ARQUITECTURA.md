# Arquitectura propuesta

## Vista general

```mermaid
flowchart LR
  Client["Postman / Hoppscotch"] --> Auth["AuthService<br/>Java + Tomcat"]
  Client --> User["UserService<br/>Java + Tomcat"]
  Client --> Vehicle["VehicleService<br/>Java + Tomcat"]
  Client --> Parking["ParkingService<br/>Java + Tomcat"]

  Auth --> UsersDb[("PostgreSQL<br/>usuarios")]
  User --> UsersDb
  Vehicle --> VehiclesDb[("MySQL 8<br/>vehiculos")]
  Parking --> ParkingDb[("MySQL 8<br/>estacionamiento")]

  Parking -. "SOAP: validar usuario" .-> User
  Parking -. "gRPC/RPC: validar vehiculo" .-> Vehicle
```

## Separacion por dominio

- Dominio usuarios: AuthService y UserService comparten PostgreSQL del dominio de usuarios.
- Dominio vehiculos: VehicleService usa su propia base MySQL.
- Dominio estacionamiento: ParkingService usa su propia base MySQL.
- No debe haber llaves foraneas entre bases de datos de dominios distintos.

## Comunicacion entre servicios

- Las APIs publicas se exponen como REST + JSON para pruebas con Postman/Hoppscotch.
- La integracion interna puede usar SOAP, RPC o gRPC, segun lo que el profesor pida.
- Propuesta base: ParkingService consulta UserService por SOAP y VehicleService por gRPC/RPC.
- Todos los microservicios siguen siendo Java; lo que cambia es el protocolo de comunicacion.

## Capas por microservicio

```mermaid
flowchart TB
  Controller["Controller<br/>HTTP, request, response"] --> Service["Service<br/>reglas de negocio"]
  Service --> Repository["Repository<br/>consultas a BD"]
  Repository --> Database[("Base de datos del dominio")]
```

## Puertos locales sugeridos

- AuthService: `8081`
- UserService: `8082`
- VehicleService: `8083`
- ParkingService: `8084`
- PostgreSQL usuarios: `5432`
- MySQL vehiculos: `3307`
- MySQL estacionamiento: `3308`
- Adminer: `8090`

## Despliegue por servidores segun PDF

Servidor 1:

- AuthService
- UserService
- Base de datos de usuarios

Servidor 2:

- VehicleService
- Base de datos de vehiculos

Servidor 3:

- ParkingService
- Base de datos de estacionamiento

Las plantillas de `deployment/` representan esta distribucion.
