package au.edu.rmit.ozagrimate.irrigation.service;

import au.edu.rmit.ozagrimate.irrigation.exception.InvalidRequestException;
import au.edu.rmit.ozagrimate.irrigation.exception.NotFoundException;
import au.edu.rmit.ozagrimate.irrigation.model.*;
import au.edu.rmit.ozagrimate.irrigation.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

// uses simple rules instead of a real model for the prototype.
// every recommendation starts as PENDING_APPROVAL (ADR-07)
@Service
public class RecommendationService {

    static final double CRITICAL_MOISTURE = 20.0;
    static final double LOW_MOISTURE = 30.0;
    static final double RAIN_SKIP_MM = 5.0;

    private final RecommendationRepository repository;
    private final FarmRegistry farmRegistry;
    private final SensorDataClient sensorDataClient;
    private final WeatherIntegrationService weatherService;

    public RecommendationService(
            RecommendationRepository repository,
            FarmRegistry farmRegistry,
            SensorDataClient sensorDataClient,
            WeatherIntegrationService weatherService) {

        this.repository = repository;
        this.farmRegistry = farmRegistry;
        this.sensorDataClient = sensorDataClient;
        this.weatherService = weatherService;
    }

    public Recommendation generate(String farmId, RecommendationRequest request) {
        farmRegistry.requireFarm(farmId);

        if (request == null || isBlank(request.getCropType())) {
            throw new InvalidRequestException("cropType is required");
        }

        SensorSnapshot sensor = sensorDataClient.latestReading(farmId);
        WeatherForecast weather = weatherService.forecastFor(farmId);

        double moisture = sensor.soilMoisture();
        double rain = weather.forecastRainMm();

        IrrigationAction action;
        double waterMm;
        int minutes;
        String rationale;

        if (rain >= RAIN_SKIP_MM) {
            action = IrrigationAction.NO_IRRIGATION;
            waterMm = 0;
            minutes = 0;
            rationale = "Rain of at least 5mm forecast, irrigation not needed";
        } else if (moisture < CRITICAL_MOISTURE) {
            action = IrrigationAction.IRRIGATE;
            waterMm = 12.5;
            minutes = 45;
            rationale = "Soil moisture below 20 and no rain forecast";
        } else if (moisture < LOW_MOISTURE) {
            action = IrrigationAction.IRRIGATE;
            waterMm = 6.0;
            minutes = 25;
            rationale = "Soil moisture below 30 and no rain forecast";
        } else {
            action = IrrigationAction.NO_IRRIGATION;
            waterMm = 0;
            minutes = 0;
            rationale = "Soil moisture adequate";
        }

        if (WeatherIntegrationService.isStale(weather)) {
            rationale += ". Warning: weather data older than 6 hours";
        }

        Recommendation recommendation = new Recommendation(
                repository.nextId(),
                farmId,
                action,
                waterMm,
                minutes,
                rationale,
                new SupportingData(sensor, weather),
                Instant.now().truncatedTo(ChronoUnit.SECONDS));

        return repository.save(recommendation);
    }

    public Recommendation get(String farmId, String id) {
        farmRegistry.requireFarm(farmId);
        return repository.findByFarmAndId(farmId, id)
                .orElseThrow(() -> new NotFoundException("Recommendation not found"));
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
