package au.edu.rmit.ozagrimate.irrigation.controller;

import au.edu.rmit.ozagrimate.irrigation.exception.ApiExceptionHandler;
import au.edu.rmit.ozagrimate.irrigation.repository.RecommendationRepository;
import au.edu.rmit.ozagrimate.irrigation.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class RecommendationControllerTest {

    private static final String BASE = "/farms/farm-001/recommendations";
    private static final String VALID_REQUEST =
            "{\"cropType\":\"wheat\",\"requestedBy\":\"farmer-01\"}";
    private static final String APPROVE =
            "{\"decision\":\"APPROVED\",\"reviewerId\":\"agro-07\",\"comment\":\"Run after 6pm\"}";

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        RecommendationRepository repository = new RecommendationRepository();
        FarmRegistry farms = new FarmRegistry();
        // Port 1 refuses connections, so the client uses its fallback reading.
        SensorDataClient sensors = new SensorDataClient("http://localhost:1", 500);
        WeatherIntegrationService weather = new WeatherIntegrationService(Set.of("farm-001"));
        RecommendationService recommendations =
                new RecommendationService(repository, farms, sensors, weather);
        ApprovalService approvals = new ApprovalService(recommendations, repository);

        mockMvc = MockMvcBuilders
                .standaloneSetup(new RecommendationController(recommendations, approvals))
                .setControllerAdvice(new ApiExceptionHandler())
                .build();
    }

    private void createRecommendation() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isCreated());
    }

    // ---- API 6: POST ----

    @Test
    void shouldCreatePendingRecommendationUsingCachedWeather() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.recommendationId").value("rec-001"))
                .andExpect(jsonPath("$.farmId").value("farm-001"))
                .andExpect(jsonPath("$.action").value("IRRIGATE"))
                .andExpect(jsonPath("$.waterAmountMm").value(12.5))
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.supportingData.soilMoisture").value(18.4))
                .andExpect(jsonPath("$.supportingData.weatherSource").value("CACHED"))
                .andExpect(jsonPath("$.supportingData.weatherDataAgeMinutes").value(95));
    }

    @Test
    void shouldUseLiveWeatherAndSkipIrrigationWhenRainForecast() throws Exception {
        mockMvc.perform(post("/farms/farm-002/recommendations")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.action").value("NO_IRRIGATION"))
                .andExpect(jsonPath("$.supportingData.weatherSource").value("LIVE"));
    }

    @Test
    void shouldRejectMissingCropType() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON)
                        .content("{\"requestedBy\":\"farmer-01\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("cropType is required"));
    }

    @Test
    void shouldRejectMissingBody() throws Exception {
        mockMvc.perform(post(BASE).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundForUnknownFarmOnCreate() throws Exception {
        mockMvc.perform(post("/farms/farm-999/recommendations")
                        .contentType(MediaType.APPLICATION_JSON).content(VALID_REQUEST))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Farm not found"));
    }

    // ---- API 7: GET ----

    @Test
    void shouldGetRecommendationWithEmptyReviewFields() throws Exception {
        createRecommendation();
        mockMvc.perform(get(BASE + "/rec-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING_APPROVAL"))
                .andExpect(jsonPath("$.reviewedBy").isEmpty());
    }

    @Test
    void shouldReturnNotFoundForUnknownRecommendation() throws Exception {
        mockMvc.perform(get(BASE + "/rec-999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Recommendation not found"));
    }

    @Test
    void shouldNotReturnRecommendationFromAnotherFarm() throws Exception {
        createRecommendation();
        mockMvc.perform(get("/farms/farm-002/recommendations/rec-001"))
                .andExpect(status().isNotFound());
    }

    // ---- API 8: PUT ----

    @Test
    void shouldApprovePendingRecommendation() throws Exception {
        createRecommendation();
        mockMvc.perform(put(BASE + "/rec-001/approval").header("X-User-Role", "AGRONOMIST")
                        .contentType(MediaType.APPLICATION_JSON).content(APPROVE))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(jsonPath("$.reviewedBy").value("agro-07"));

        mockMvc.perform(get(BASE + "/rec-001"))
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldRejectPendingRecommendation() throws Exception {
        createRecommendation();
        mockMvc.perform(put(BASE + "/rec-001/approval").header("X-User-Role", "AGRONOMIST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"REJECTED\",\"reviewerId\":\"agro-07\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("REJECTED"));
    }

    @Test
    void shouldForbidNonAgronomist() throws Exception {
        createRecommendation();
        mockMvc.perform(put(BASE + "/rec-001/approval").header("X-User-Role", "FARMER")
                        .contentType(MediaType.APPLICATION_JSON).content(APPROVE))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldForbidMissingRoleHeader() throws Exception {
        createRecommendation();
        mockMvc.perform(put(BASE + "/rec-001/approval")
                        .contentType(MediaType.APPLICATION_JSON).content(APPROVE))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldRejectInvalidDecision() throws Exception {
        createRecommendation();
        mockMvc.perform(put(BASE + "/rec-001/approval").header("X-User-Role", "AGRONOMIST")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"decision\":\"MAYBE\",\"reviewerId\":\"agro-07\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnNotFoundWhenApprovingUnknownRecommendation() throws Exception {
        mockMvc.perform(put(BASE + "/rec-999/approval").header("X-User-Role", "AGRONOMIST")
                        .contentType(MediaType.APPLICATION_JSON).content(APPROVE))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnConflictWhenAlreadyReviewed() throws Exception {
        createRecommendation();
        mockMvc.perform(put(BASE + "/rec-001/approval").header("X-User-Role", "AGRONOMIST")
                        .contentType(MediaType.APPLICATION_JSON).content(APPROVE))
                .andExpect(status().isOk());
        mockMvc.perform(put(BASE + "/rec-001/approval").header("X-User-Role", "AGRONOMIST")
                        .contentType(MediaType.APPLICATION_JSON).content(APPROVE))
                .andExpect(status().isConflict());
    }
}
