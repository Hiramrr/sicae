# ParkingService

Responsable de entradas, salidas y consulta de espacios del estacionamiento.

## Tecnologia propuesta

- Java 17+
- Spring MVC / RestController
- Maven
- MyBatis
- Apache Tomcat
- MySQL 8
- JWT para autenticacion

## Responsabilidades

- Registrar entrada de vehiculo.
- Registrar salida de vehiculo.
- Calcular permanencia y costo.
- Consultar espacios disponibles.
- Actualizar ocupacion de cajones.
- Validar usuario y vehiculo sin relaciones fisicas entre bases de datos.
- Mantener la logica organizada en Controller, Service y Repository.

## Pendiente para el equipo

- Crear el proyecto Maven/Java en esta carpeta.
- Definir como consultara UserService y VehicleService mediante SOAP, RPC o gRPC.
- Implementar calculo de horas/minutos/costo.
- Documentar el contrato final en `docs/API_CONTRATOS.md`.
