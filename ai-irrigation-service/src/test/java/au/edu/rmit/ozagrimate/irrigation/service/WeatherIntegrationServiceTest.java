package au.edu.rmit.ozagrimate.irrigation.service;

import au.edu.rmit.ozagrimate.irrigation.model.WeatherForecast;
import au.edu.rmit.ozagrimate.irrigation.model.WeatherSource;
import org.junit.jupiter.api.Test;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class WeatherIntegrationServiceTest {

    @Test
    void shouldFallBackToCacheDuringOutage() {
        WeatherIntegrationService service =
                new WeatherIntegrationService(Set.of("farm-001"));
        WeatherForecast forecast = service.forecastFor("farm-001");

        assertEquals(WeatherSource.CACHED, forecast.source());
        assertEquals(95, forecast.ageMinutes());
        assertFalse(WeatherIntegrationService.isStale(forecast));
    }

    @Test
    void shouldUseLiveDataWhenApiAvailable() {
        WeatherIntegrationService service = new WeatherIntegrationService(Set.of());
        WeatherForecast forecast = service.forecastFor("farm-001");

        assertEquals(WeatherSource.LIVE, forecast.source());
        assertEquals(0, forecast.ageMinutes());
    }

    @Test
    void shouldFlagCacheOlderThanSixHoursAsStale() {
        Instant start = Instant.parse("2026-10-19T00:00:00Z");
        MutableClock clock = new MutableClock(start);
        WeatherIntegrationService service =
                new WeatherIntegrationService(Set.of("farm-001"), clock);

        clock.advance(Duration.ofHours(6));
        WeatherForecast forecast = service.forecastFor("farm-001");

        assertTrue(WeatherIntegrationService.isStale(forecast));
    }

    private static class MutableClock extends Clock {
        private Instant now;
        MutableClock(Instant now) { this.now = now; }
        void advance(Duration d) { now = now.plus(d); }
        @Override public java.time.ZoneId getZone() { return ZoneOffset.UTC; }
        @Override public Clock withZone(java.time.ZoneId zone) { return this; }
        @Override public Instant instant() { return now; }
    }
}
