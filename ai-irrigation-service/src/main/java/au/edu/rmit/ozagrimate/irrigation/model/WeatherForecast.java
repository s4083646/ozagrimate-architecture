package au.edu.rmit.ozagrimate.irrigation.model;

/** Rainfall forecast plus where it came from and how old it is (ADR-08). */
public record WeatherForecast(
        double forecastRainMm,
        WeatherSource source,
        long ageMinutes) {
}
