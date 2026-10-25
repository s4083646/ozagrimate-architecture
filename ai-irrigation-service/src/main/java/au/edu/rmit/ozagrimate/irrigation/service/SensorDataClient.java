package au.edu.rmit.ozagrimate.irrigation.service;

import au.edu.rmit.ozagrimate.irrigation.model.SensorSnapshot;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

// calls the monitoring service for the latest reading.
// if it's not running we use a fallback reading so this service still works by itself
@Component
public class SensorDataClient {

    static final SensorSnapshot FALLBACK_READING =
            new SensorSnapshot("reading-001", 18.4);

    private static final Logger log = LoggerFactory.getLogger(SensorDataClient.class);

    private final RestClient restClient;

    public SensorDataClient(
            @Value("${monitoring.base-url}") String baseUrl,
            @Value("${monitoring.timeout-ms:2000}") int timeoutMs) {

        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(timeoutMs);
        factory.setReadTimeout(timeoutMs);

        this.restClient = RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(factory)
                .build();
    }

    public SensorSnapshot latestReading(String farmId) {
        try {
            JsonNode body = restClient.get()
                    .uri("/farms/{farmId}/readings/latest", farmId)
                    .retrieve()
                    .body(JsonNode.class);

            if (body != null && body.hasNonNull("soilMoisture")) {
                return new SensorSnapshot(
                        body.path("readingId").asText(),
                        body.get("soilMoisture").asDouble());
            }
        } catch (Exception e) {
            log.warn("Monitoring service unavailable for {}, using fallback reading: {}",
                    farmId, e.getMessage());
        }
        return FALLBACK_READING;
    }
}
