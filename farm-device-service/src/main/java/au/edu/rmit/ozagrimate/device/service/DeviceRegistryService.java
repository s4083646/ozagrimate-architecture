package au.edu.rmit.ozagrimate.device.service;

import au.edu.rmit.ozagrimate.device.dto.DeviceRegistrationRequest;
import au.edu.rmit.ozagrimate.device.model.Device;
import au.edu.rmit.ozagrimate.device.repository.DeviceRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.Set;

@Service
public class DeviceRegistryService {

    private static final Set<String> VALID_FARMS =
            Set.of("farm-001", "farm-002");

    private final DeviceRepository repository;

    public DeviceRegistryService(DeviceRepository repository) {
        this.repository = repository;
    }

    public Device register(String farmId, DeviceRegistrationRequest request) {
        validateFarm(farmId);

        if (request == null ||
            isBlank(request.deviceId()) ||
            isBlank(request.vendor()) ||
            isBlank(request.sensorType()) ||
            isBlank(request.protocol()) ||
            isBlank(request.location())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "All device fields are required");
        }

        String deviceId = request.deviceId().trim();

        if (!deviceId.matches("[A-Za-z0-9_-]+")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Invalid device ID format");
        }

        Device device = new Device(
                deviceId,
                farmId,
                request.vendor().trim(),
                request.sensorType().trim(),
                request.protocol().trim(),
                request.location().trim(),
                "ACTIVE"
        );

        if (!repository.saveIfAbsent(device)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT, "Device already registered");
        }

        return device;
    }

    public void delete(String farmId, String deviceId) {
        validateFarm(farmId);

        Device device = repository.findById(deviceId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Device not found"));

        if (!device.farmId().equals(farmId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Device not found for this farm");
        }

        repository.deleteById(deviceId);
    }

    private void validateFarm(String farmId) {
        if (!VALID_FARMS.contains(farmId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Farm not found");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
