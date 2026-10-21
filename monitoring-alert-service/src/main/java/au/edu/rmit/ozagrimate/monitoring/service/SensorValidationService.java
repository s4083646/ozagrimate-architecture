
package au.edu.rmit.ozagrimate.monitoring.service;

import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class SensorValidationService {

    public void validate(SensorReading reading) {

        if (reading == null) {
            throw new IllegalArgumentException(
                    "Sensor reading is required");
        }

        if (isBlank(reading.getReadingId())) {
            throw new IllegalArgumentException(
                    "Reading ID is required");
        }

        if (isBlank(reading.getFarmId())) {
            throw new IllegalArgumentException(
                    "Farm ID is required");
        }

        if (isBlank(reading.getDeviceId())) {
            throw new IllegalArgumentException(
                    "Device ID is required");
        }

        Double moisture = reading.getSoilMoisture();

        if (moisture == null ||
                !Double.isFinite(moisture) ||
                moisture < 0 ||
                moisture > 100) {

            throw new IllegalArgumentException(
                    "Soil moisture must be between 0 and 100");
        }

        if (reading.getTimestamp() == null) {
            throw new IllegalArgumentException(
                    "Timestamp is required");
        }

        if (reading.getTimestamp().isAfter(Instant.now())) {
            throw new IllegalArgumentException(
                    "Timestamp cannot be in the future");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
