# Arquitectura propuesta

## Vista general

```mermaid
flowchart LR
  Client["Postman / Hoppscotch"] --> Auth["AuthService<br/>Java + Tomcat"]
  Client --> User["UserService<br/>Java + Tomcat"]
  Client --> Vehicle["VehicleService<br/>Node.js + Express"]
  Client --> Parking["ParkingService<br/>Java + Tomcat"]

  Auth --> UsersDb[("PostgreSQL<br/>usuarios")]
  User --> UsersDb
  Vehicle --> VehiclesDb[("MySQL 8<br/>vehiculos")]
  Parking --> ParkingDb[("MySQL 8<br/>estacionamiento")]

  Parking -. "REST: validar usuario" .-> User
  Parking -. "REST: validar vehiculo" .-> Vehicle
```

## Separacion por dominio

- Dominio usuarios: AuthService y UserService comparten PostgreSQL del dominio de usuarios.
- Dominio vehiculos: VehicleService usa su propia base MySQL.
- Dominio estacionamiento: ParkingService usa su propia base MySQL.
- No debe haber llaves foraneas entre bases de datos de dominios distintos.

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
