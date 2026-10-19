
package au.edu.rmit.ozagrimate.monitoring.repository;

import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class SensorReadingRepositoryTest {

    private SensorReadingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SensorReadingRepository();
    }

    @Test
    void shouldSaveNewReading() {
        SensorReading reading = createReading(
                "reading-001", "farm-001",
                45.0, "2026-10-18T10:00:00Z"
        );

        assertTrue(repository.saveIfAbsent(reading));
        assertTrue(repository.existsById("reading-001"));
    }

    @Test
    void shouldRejectDuplicateReadingId() {
        SensorReading first = createReading(
                "reading-001", "farm-001",
                45.0, "2026-10-18T10:00:00Z"
        );

        SensorReading duplicate = createReading(
                "reading-001", "farm-001",
                20.0, "2026-10-18T11:00:00Z"
        );

        assertTrue(repository.saveIfAbsent(first));
        assertFalse(repository.saveIfAbsent(duplicate));

        assertEquals(
                45.0,
                repository.findById("reading-001")
                        .orElseThrow()
                        .getSoilMoisture()
        );
    }

    @Test
    void shouldReturnLatestReadingByTimestamp() {
        SensorReading older = createReading(
                "reading-001", "farm-001",
                40.0, "2026-10-18T10:00:00Z"
        );

        SensorReading newer = createReading(
                "reading-002", "farm-001",
                25.0, "2026-10-18T12:00:00Z"
        );

        repository.saveIfAbsent(newer);
        repository.saveIfAbsent(older);

        SensorReading latest = repository
                .findLatestByFarmId("farm-001")
                .orElseThrow();

        assertEquals("reading-002", latest.getReadingId());
    }

    @Test
    void shouldKeepReadingsSeparatedByFarm() {
        SensorReading farmOne = createReading(
                "reading-001", "farm-001",
                45.0, "2026-10-18T10:00:00Z"
        );

        SensorReading farmTwo = createReading(
                "reading-002", "farm-002",
                15.0, "2026-10-18T12:00:00Z"
        );

        repository.saveIfAbsent(farmOne);
        repository.saveIfAbsent(farmTwo);

        assertEquals(
                "reading-001",
                repository.findLatestByFarmId("farm-001")
                        .orElseThrow()
                        .getReadingId()
        );
    }

    private SensorReading createReading(
            String id,
            String farmId,
            double moisture,
            String timestamp) {

        return new SensorReading(
                id,
                farmId,
                "sensor-001",
                moisture,
                Instant.parse(timestamp)
        );
    }
}
