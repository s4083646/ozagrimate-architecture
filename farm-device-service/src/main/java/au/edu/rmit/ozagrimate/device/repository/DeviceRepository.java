package au.edu.rmit.ozagrimate.device.repository;

import au.edu.rmit.ozagrimate.device.model.Device;
import org.springframework.stereotype.Repository;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class DeviceRepository {

    private final ConcurrentHashMap<String, Device> devices =
            new ConcurrentHashMap<>();

    public boolean saveIfAbsent(Device device) {
        return devices.putIfAbsent(device.deviceId(), device) == null;
    }

    public Optional<Device> findById(String deviceId) {
        return Optional.ofNullable(devices.get(deviceId));
    }

    public void deleteById(String deviceId) {
        devices.remove(deviceId);
    }
}
