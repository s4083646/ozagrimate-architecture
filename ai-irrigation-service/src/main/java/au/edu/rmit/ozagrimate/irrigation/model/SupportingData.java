package au.edu.rmit.ozagrimate.irrigation.model;

/** Inputs behind a recommendation, shown to the reviewing agronomist. */
public class SupportingData {

    private String readingId;
    private double soilMoisture;
    private double forecastRainMm;
    private WeatherSource weatherSource;
    private long weatherDataAgeMinutes;

    public SupportingData() {
    }

    public SupportingData(SensorSnapshot sensor, WeatherForecast weather) {
        this.readingId = sensor.readingId();
        this.soilMoisture = sensor.soilMoisture();
        this.forecastRainMm = weather.forecastRainMm();
        this.weatherSource = weather.source();
        this.weatherDataAgeMinutes = weather.ageMinutes();
    }

    public String getReadingId() { return readingId; }
    public double getSoilMoisture() { return soilMoisture; }
    public double getForecastRainMm() { return forecastRainMm; }
    public WeatherSource getWeatherSource() { return weatherSource; }
    public long getWeatherDataAgeMinutes() { return weatherDataAgeMinutes; }
}
