package au.edu.rmit.ozagrimate.irrigation.service;

import au.edu.rmit.ozagrimate.irrigation.model.WeatherForecast;
import au.edu.rmit.ozagrimate.irrigation.model.WeatherSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

// ADR-08: all weather calls go through here. If the weather API is down we use the
// cached forecast and record how old it is.
// The API is simulated - farms in weather.simulated-outage-farms act like it's down
@Service
public class WeatherIntegrationService {

    // older than this = stale
    public static final Duration STALE_AFTER = Duration.ofHours(6);

    private final Set<String> simulatedOutageFarms;
    private final Clock clock;
    private final Map<String, CachedForecast> cache = new ConcurrentHashMap<>();

    @Autowired
    public WeatherIntegrationService(
            @Value("${weather.simulated-outage-farms:}") Set<String> simulatedOutageFarms) {
        this(simulatedOutageFarms, Clock.systemUTC());
    }

    WeatherIntegrationService(Set<String> simulatedOutageFarms, Clock clock) {
        this.simulatedOutageFarms = simulatedOutageFarms;
        this.clock = clock;
        // pretend the last good fetch was 95 mins ago
        Instant fetched = clock.instant().minus(Duration.ofMinutes(95));
        cache.put("farm-001", new CachedForecast(0.0, fetched));
        cache.put("farm-002", new CachedForecast(8.0, fetched));
    }

    public WeatherForecast forecastFor(String farmId) {
        if (!simulatedOutageFarms.contains(farmId)) {
            double rain = callExternalWeatherApi(farmId);
            cache.put(farmId, new CachedForecast(rain, clock.instant()));
            return new WeatherForecast(rain, WeatherSource.LIVE, 0);
        }

        CachedForecast cached = cache.getOrDefault(
                farmId, new CachedForecast(0.0, clock.instant()));
        long age = Duration.between(cached.fetchedAt(), clock.instant()).toMinutes();
        return new WeatherForecast(cached.rainMm(), WeatherSource.CACHED, age);
    }

    public static boolean isStale(WeatherForecast forecast) {
        return forecast.ageMinutes() > STALE_AFTER.toMinutes();
    }

    // fake weather API call for the prototype
    private double callExternalWeatherApi(String farmId) {
        return "farm-002".equals(farmId) ? 8.0 : 0.0;
    }

    private record CachedForecast(double rainMm, Instant fetchedAt) {
    }
}
