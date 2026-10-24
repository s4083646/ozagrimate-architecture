
package au.edu.rmit.ozagrimate.device.controller;

import au.edu.rmit.ozagrimate.device.dto.DeviceRegistrationRequest;
import au.edu.rmit.ozagrimate.device.model.Device;
import au.edu.rmit.ozagrimate.device.service.DeviceRegistryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@RequestMapping("/farms/{farmId}/devices")
public class DeviceController {

    private final DeviceRegistryService service;

    public DeviceController(DeviceRegistryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<Device> registerDevice(
            @PathVariable("farmId") String farmId,
            @RequestBody DeviceRegistrationRequest request) {

        Device device = service.register(farmId, request);

        URI location = URI.create(
                "/farms/" + farmId + "/devices/" + device.deviceId());

        return ResponseEntity.created(location).body(device);
    }

    @DeleteMapping("/{deviceId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDevice(
            @PathVariable("farmId") String farmId,
            @PathVariable("deviceId") String deviceId) {

        service.delete(farmId, deviceId);
    }
}
