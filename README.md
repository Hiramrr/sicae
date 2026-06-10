# SICAE - Sistema de Control de Acceso de Estacionamiento

Starter kit del equipo para comenzar el desarrollo del proyecto final. Este repositorio prepara el entorno, la estructura y los acuerdos de trabajo; la implementacion de la logica de negocio debe hacerla el equipo.

## Importante sobre el uso de IA

El documento del proyecto prohibe usar IA generativa para desarrollar el codigo fuente evaluable. Por eso este repositorio solo deja:

- estructura de carpetas;
- contenedores de bases de datos;
- plantillas de configuracion;
- guias de requisitos y entregables;
- coleccion Postman base sin pruebas de negocio.

El equipo debe escribir, entender y poder explicar todo el codigo de los microservicios.

## Stack propuesto

- AuthService: Java 17, Spring MVC/RestController, Maven, MyBatis, Tomcat, PostgreSQL.
- UserService: Java 17, Spring MVC/RestController, Maven, MyBatis, Tomcat, PostgreSQL.
- VehicleService: Node.js LTS, Express, TypeScript, MySQL 8.
- ParkingService: Java 17, Spring MVC/RestController, Maven, MyBatis, Tomcat, MySQL 8.
- Seguridad: JWT y bcrypt.
- Pruebas API: Postman o Hoppscotch.
- Despliegue: Docker y Docker Compose.

La mezcla Java + Node.js cubre el requisito de usar al menos dos tecnologias backend. Si el profesor pide una combinacion distinta, actualicen `docs/ARQUITECTURA.md` antes de programar.

## Requisitos locales

- Git
- Docker Desktop o Docker Engine con Docker Compose
- Java 17 o superior
- Maven 3.9 o superior
- Node.js LTS y npm
- NetBeans, IntelliJ o VS Code
- Postman o Hoppscotch

Verificacion rapida:

```bash
make check-env
```

## Primer arranque

1. Revisar los scripts SQL entregados por el profesor. Ya quedaron colocados en:

- `database/users/postgres/init/001_sicaeUsuario.sql`
- `database/vehicles/mysql/init/001_sicaevehiculo.sql`
- `database/parking/mysql/init/001_sicaeestacionamiento.sql`

Los nombres reales de las bases son `sicaeUsuario`, `sicaevehiculo` y `sicaeEstacionamiento`. Para este proyecto escolar, las credenciales locales de Docker quedaron fijas en los archivos `docker-compose.yml` y `deployment/*.compose.example.yml`.

2. Levantar bases de datos:

```bash
make up-db
```

3. Revisar estado:

```bash
make ps
```

4. Verificar que se importaron tablas y vistas:

```bash
make verify-db
```

5. Abrir Postman e importar:

```text
postman/SICAE.postman_collection.json
```

## Carpetas principales

- `services/`: microservicios del sistema.
- `database/`: scripts de inicializacion por dominio.
- `deployment/`: plantillas de despliegue por servidor.
- `docs/`: requisitos, arquitectura, contratos de API y checklist.
- `postman/`: colecciones para pruebas manuales.
- `scripts/`: utilidades de entorno.

## Flujo sugerido de Git

- `main`: solo versiones estables.
- `feature/auth-service`, `feature/user-service`, etc.: ramas por servicio o caso de uso.
- Antes de integrar: correr pruebas, revisar Postman y explicar los cambios al equipo.

## Comandos utiles

```bash
make up-db       # levanta solo las bases de datos
make tools       # levanta Adminer para inspeccionar BD
make logs-db     # muestra logs de bases de datos
make down        # apaga contenedores
make reset-db    # borra contenedores y volumenes de BD
make verify-db   # lista tablas y vistas importadas
```

Si ya habian levantado las bases antes de tener los scripts SQL, ejecuten `make reset-db` y luego `make up-db` para que Docker vuelva a importar los scripts.
