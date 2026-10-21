
package au.edu.rmit.ozagrimate.monitoring.controller;

import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import au.edu.rmit.ozagrimate.monitoring.repository.SensorReadingRepository;
import au.edu.rmit.ozagrimate.monitoring.service.AlertService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class FarmAlertControllerTest {

    private MockMvc mockMvc;
    private SensorReadingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SensorReadingRepository();

        AlertService alertService = new AlertService(repository);
        FarmAlertController controller =
                new FarmAlertController(alertService);

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldReturnCriticalAlert() throws Exception {
        saveReading("reading-001", "farm-001", 15.0);

        mockMvc.perform(get("/farms/farm-001/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].severity")
                        .value("CRITICAL"))
                .andExpect(jsonPath("$[0].alertId")
                        .value("alert-reading-001"));
    }

    @Test
    void shouldReturnWarningAlert() throws Exception {
        saveReading("reading-002", "farm-001", 25.0);

        mockMvc.perform(get("/farms/farm-001/alerts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].severity")
                        .value("WARNING"));
    }

    @Test
    void shouldReturnNoAlertsForNormalMoisture() throws Exception {
        saveReading("reading-003", "farm-001", 35.0);

        mockMvc.perform(get("/farms/farm-001/alerts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnEmptyListWithoutReadings() throws Exception {
        mockMvc.perform(get("/farms/farm-001/alerts"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldRejectUnknownFarm() throws Exception {
        mockMvc.perform(get("/farms/farm-999/alerts"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Farm not found"));
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
