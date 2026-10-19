
package au.edu.rmit.ozagrimate.monitoring.service;

import au.edu.rmit.ozagrimate.monitoring.model.FarmAlert;
import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import au.edu.rmit.ozagrimate.monitoring.repository.SensorReadingRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class AlertServiceTest {

    private SensorReadingRepository repository;
    private AlertService alertService;

    @BeforeEach
    void setUp() {
        repository = new SensorReadingRepository();
        alertService = new AlertService(repository);
    }

    @Test
    void shouldGenerateCriticalAlert() {
        saveReading("reading-001", "farm-001", 15.0);

        List<FarmAlert> alerts =
                alertService.getAlertsForFarm("farm-001");

        assertEquals(1, alerts.size());
        assertEquals("CRITICAL", alerts.get(0).getSeverity());
        assertEquals("alert-reading-001", alerts.get(0).getAlertId());
    }

    @Test
    void shouldGenerateWarningAlert() {
        saveReading("reading-002", "farm-001", 25.0);

        List<FarmAlert> alerts =
                alertService.getAlertsForFarm("farm-001");

        assertEquals(1, alerts.size());
        assertEquals("WARNING", alerts.get(0).getSeverity());
    }

    @Test
    void shouldNotGenerateAlertForNormalMoisture() {
        saveReading("reading-003", "farm-001", 35.0);

        List<FarmAlert> alerts =
                alertService.getAlertsForFarm("farm-001");

        assertTrue(alerts.isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoReadingsExist() {
        List<FarmAlert> alerts =
                alertService.getAlertsForFarm("farm-001");

        assertTrue(alerts.isEmpty());
    }

    @Test
    void shouldUseLatestReadingForAlerts() {
        repository.saveIfAbsent(new SensorReading(
                "reading-old",
                "farm-001",
                "sensor-001",
                15.0,
                Instant.parse("2026-10-18T10:00:00Z")
        ));

        repository.saveIfAbsent(new SensorReading(
                "reading-new",
                "farm-001",
                "sensor-001",
                35.0,
                Instant.parse("2026-10-18T12:00:00Z")
        ));

        List<FarmAlert> alerts =
                alertService.getAlertsForFarm("farm-001");

        assertTrue(alerts.isEmpty());
    }

    private void saveReading(
            String readingId,
            String farmId,
            double moisture) {

        repository.saveIfAbsent(new SensorReading(
                readingId,
                farmId,
                "sensor-001",
                moisture,
                Instant.parse("2026-10-18T10:00:00Z")
        ));
    }
}
