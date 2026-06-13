package mx.uv.sicae.parking.client;

import java.util.List;
import mx.uv.sicae.parking.model.Vehiculo;

public interface VehicleServiceClient {
    Vehiculo validarVehiculoPorPlaca(String placa);
    List<Integer> obtenerIdsVehiculosPorUsuario(Integer idUsuario);
}
