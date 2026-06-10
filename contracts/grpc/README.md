# Contratos gRPC

Usen esta carpeta para archivos `.proto`.

Contrato sugerido:

```text
VehicleValidationService
ValidateVehicleByPlate
```

Objetivo:

- Permitir que ParkingService valide placa, vehiculo activo y asociacion con usuario sin conectarse directamente a la base de datos de vehiculos.
