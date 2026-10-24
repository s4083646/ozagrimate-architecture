package au.edu.rmit.ozagrimate.device.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private String validDevice(String id) {
        return """
            {
                "deviceId": "%s",
                "vendor": "AgriSense",
                "sensorType": "SOIL_MOISTURE",
                "protocol": "MQTT",
                "location": "North Field"
            }
            """.formatted(id);
    }

    @Test
    void registerValidDevice() throws Exception {
        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validDevice("test-valid-001")))
            .andExpect(status().isCreated())
            .andExpect(header().string(
                "Location", "/farms/farm-001/devices/test-valid-001"))
            .andExpect(jsonPath("$.deviceId").value("test-valid-001"))
            .andExpect(jsonPath("$.farmId").value("farm-001"))
            .andExpect(jsonPath("$.status").value("ACTIVE"));
    }

    @Test
    void rejectDuplicateDevice() throws Exception {
        String body = validDevice("test-duplicate-001");

        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isCreated());

        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(body))
            .andExpect(status().isConflict());
    }

    @Test
    void rejectMissingDeviceId() throws Exception {
        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validDevice("")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rejectMissingRequiredField() throws Exception {
        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "deviceId": "test-missing-001",
                        "vendor": "AgriSense"
                    }
                    """))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rejectInvalidDeviceId() throws Exception {
        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validDevice("invalid device!")))
            .andExpect(status().isBadRequest());
    }

    @Test
    void rejectUnknownFarm() throws Exception {
        mockMvc.perform(post("/farms/farm-999/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validDevice("test-unknown-farm-001")))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteExistingDevice() throws Exception {
        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validDevice("test-delete-001")))
            .andExpect(status().isCreated());

        mockMvc.perform(delete(
                "/farms/farm-001/devices/test-delete-001"))
            .andExpect(status().isNoContent())
            .andExpect(content().string(""));

        mockMvc.perform(delete(
                "/farms/farm-001/devices/test-delete-001"))
            .andExpect(status().isNotFound());
    }

    @Test
    void rejectUnknownDeviceDeletion() throws Exception {
        mockMvc.perform(delete(
                "/farms/farm-001/devices/nonexistent-999"))
            .andExpect(status().isNotFound());
    }

    @Test
    void rejectWrongFarmDeletion() throws Exception {
        mockMvc.perform(post("/farms/farm-001/devices")
                .contentType(MediaType.APPLICATION_JSON)
                .content(validDevice("test-wrong-farm-001")))
            .andExpect(status().isCreated());

        mockMvc.perform(delete(
                "/farms/farm-002/devices/test-wrong-farm-001"))
            .andExpect(status().isNotFound());

        mockMvc.perform(delete(
                "/farms/farm-001/devices/test-wrong-farm-001"))
            .andExpect(status().isNoContent());
    }
}
