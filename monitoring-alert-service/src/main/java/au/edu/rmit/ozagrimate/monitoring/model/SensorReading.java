
package au.edu.rmit.ozagrimate.monitoring.model;

import java.time.Instant;

public class SensorReading {

    private String readingId;
    private String farmId;
    private String deviceId;
    private Double soilMoisture;
    private Instant timestamp;

    public SensorReading() {
    }

    public SensorReading(
            String readingId,
            String farmId,
            String deviceId,
            Double soilMoisture,
            Instant timestamp) {

        this.readingId = readingId;
        this.farmId = farmId;
        this.deviceId = deviceId;
        this.soilMoisture = soilMoisture;
        this.timestamp = timestamp;
    }

    public String getReadingId() {
        return readingId;
    }

    public void setReadingId(String readingId) {
        this.readingId = readingId;
    }

    public String getFarmId() {
        return farmId;
    }

    public void setFarmId(String farmId) {
        this.farmId = farmId;
    }

    public String getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(String deviceId) {
        this.deviceId = deviceId;
    }

    public Double getSoilMoisture() {
        return soilMoisture;
    }

    public void setSoilMoisture(Double soilMoisture) {
        this.soilMoisture = soilMoisture;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
