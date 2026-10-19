
package au.edu.rmit.ozagrimate.monitoring.controller;

import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import au.edu.rmit.ozagrimate.monitoring.repository.SensorReadingRepository;
import au.edu.rmit.ozagrimate.monitoring.service.SensorValidationService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/farms/{farmId}/readings")
public class SensorReadingController {

    private static final Set<String> VALID_FARMS =
            Set.of("farm-001", "farm-002");

    private final SensorReadingRepository repository;
    private final SensorValidationService validationService;

    public SensorReadingController(
            SensorReadingRepository repository,
            SensorValidationService validationService) {
        this.repository = repository;
        this.validationService = validationService;
    }
    

    @PostMapping
    public ResponseEntity<?> createReading(
        @PathVariable("farmId") String farmId,
        @RequestBody SensorReading reading) {
 

        if (!VALID_FARMS.contains(farmId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Farm not found"));
        }

        // The URL is the authoritative farm identifier.
        if (reading == null) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Reading is required"));
        }

        if (reading.getFarmId() != null
                && !farmId.equals(reading.getFarmId())) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Farm ID does not match URL"));
        }

        reading.setFarmId(farmId);

        try {
            validationService.validate(reading);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        }

        if (!repository.saveIfAbsent(reading)) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(Map.of("error", "Duplicate reading ID"));
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(reading);
    }
}
