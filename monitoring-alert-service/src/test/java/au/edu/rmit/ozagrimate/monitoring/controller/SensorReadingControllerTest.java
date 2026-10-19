
package au.edu.rmit.ozagrimate.monitoring.controller;

import au.edu.rmit.ozagrimate.monitoring.repository.SensorReadingRepository;
import au.edu.rmit.ozagrimate.monitoring.service.SensorValidationService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SensorReadingControllerTest {

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        SensorReadingRepository repository =
                new SensorReadingRepository();

        SensorValidationService validationService =
                new SensorValidationService();

        SensorReadingController controller =
                new SensorReadingController(
                        repository,
                        validationService
                );

        mockMvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();
    }

    @Test
    void shouldCreateValidSensorReading() throws Exception {
        mockMvc.perform(post("/farms/farm-001/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "readingId": "reading-001",
                      "deviceId": "sensor-001",
                      "soilMoisture": 45.0,
                      "timestamp": "2026-10-18T10:00:00Z"
                    }
                    """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.farmId").value("farm-001"))
                .andExpect(jsonPath("$.soilMoisture").value(45.0));
    }

    @Test
    void shouldRejectInvalidMoisture() throws Exception {
        mockMvc.perform(post("/farms/farm-001/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "readingId": "reading-002",
                      "deviceId": "sensor-001",
                      "soilMoisture": 145.0,
                      "timestamp": "2026-10-18T10:00:00Z"
                    }
                    """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error")
                        .value("Soil moisture must be between 0 and 100"));
    }

    @Test
    void shouldRejectDuplicateReading() throws Exception {
        String body = """
                {
                  "readingId": "reading-003",
                  "deviceId": "sensor-001",
                  "soilMoisture": 25.0,
                  "timestamp": "2026-10-18T10:00:00Z"
                }
                """;

        mockMvc.perform(post("/farms/farm-001/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/farms/farm-001/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetLatestReading() throws Exception {
        mockMvc.perform(post("/farms/farm-001/readings")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "readingId": "reading-004",
                      "deviceId": "sensor-001",
                      "soilMoisture": 35.0,
                      "timestamp": "2026-10-18T10:00:00Z"
                    }
                    """))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/farms/farm-001/readings/latest"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.readingId")
                        .value("reading-004"));
    }

    @Test
    void shouldRejectUnknownFarm() throws Exception {
        mockMvc.perform(get("/farms/farm-999/readings/latest"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error")
                        .value("Farm not found"));
    }
}
