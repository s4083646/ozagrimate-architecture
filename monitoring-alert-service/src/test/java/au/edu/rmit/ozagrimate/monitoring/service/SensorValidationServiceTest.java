
package au.edu.rmit.ozagrimate.monitoring.service;

import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SensorValidationServiceTest {

    private SensorValidationService validationService;

    @BeforeEach
    void setUp() {
        validationService = new SensorValidationService();
    }

    @Test
    void shouldAcceptValidSensorReading() {

        SensorReading reading = new SensorReading(
                "reading-001",
                "farm-001",
                "sensor-001",
                45.0,
                Instant.now()
        );

        assertDoesNotThrow(() ->
                validationService.validate(reading));
    }

    @Test
    void shouldRejectMoistureAbove100() {

        SensorReading reading = new SensorReading(
                "reading-002",
                "farm-001",
                "sensor-001",
                145.0,
                Instant.now()
        );

        IllegalArgumentException exception =
                assertThrows(
                        IllegalArgumentException.class,
                        () -> validationService.validate(reading)
                );

        assertEquals(
                "Soil moisture must be between 0 and 100",
                exception.getMessage()
        );
    }

    @Test
    void shouldRejectNegativeMoisture() {

        SensorReading reading = new SensorReading(
                "reading-003",
                "farm-001",
                "sensor-001",
                -5.0,
                Instant.now()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validate(reading)
        );
    }

    @Test
    void shouldRejectMissingDeviceId() {

        SensorReading reading = new SensorReading(
                "reading-004",
                "farm-001",
                null,
                30.0,
                Instant.now()
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> validationService.validate(reading)
        );
    }
}
