package au.edu.rmit.ozagrimate.irrigation.model;

// forecast + whether it was live or cached (ADR-08)
public record WeatherForecast(
        double forecastRainMm,
        WeatherSource source,
        long ageMinutes) {
}
