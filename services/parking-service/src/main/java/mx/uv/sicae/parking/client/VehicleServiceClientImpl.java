package mx.uv.sicae.parking.client;

import mx.uv.sicae.parking.model.Vehiculo;
import org.springframework.stereotype.Service;
import java.util.Collections;
import java.util.List;

@Service
public class VehicleServiceClientImpl implements VehicleServiceClient {

    @Override
    public Vehiculo validarVehiculoPorPlaca(String placa) {
        Vehiculo vehiculo = new Vehiculo();
        vehiculo.setIdVehiculo(1);
        vehiculo.setIdUsuario(1);
        vehiculo.setPlaca(placa);
        vehiculo.setActivo(true);
        return vehiculo;
    }

    @Override
    public List<Integer> obtenerIdsVehiculosPorUsuario(Integer idUsuario) {
        return Collections.singletonList(1);
    }
}
