# SICAE - Sistema de Control de Acceso de Estacionamiento

```bash
# Iniciar todos los servicios
docker-compose up
```

## Acceso a Bases de Datos

### users-db (Postgres - puerto 5432)
```bash
docker exec -it sicae-users-db psql -U sicae_usuario_app -d sicaeUsuario
```

### vehicles-db (MySQL - puerto 3307)
```bash
docker exec -it sicae-vehicles-db mysql -u sicae_vehiculo_app -p sicaevehiculo
```
Contraseña: `sicae123`

### parking-db (MySQL - puerto 3308)
```bash
docker exec -it sicae-parking-db mysql -u sicae_estacionamiento_app -p sicaeEstacionamiento
```
Contraseña: `sicae123`

## Servicios

| Servicio | Puerto |
|---|---|
| auth-service | 8081 |
| user-service | 8082 |
| vehicle-service | 8083 |
| parking-service | 8080 |
| users-db (Postgres) | 5432 |
| vehicles-db (MySQL) | 3307 |
| parking-db (MySQL) | 3308 |

