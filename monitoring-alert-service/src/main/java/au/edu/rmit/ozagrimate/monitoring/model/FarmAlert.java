
package au.edu.rmit.ozagrimate.monitoring.model;

import java.time.Instant;

public class FarmAlert {

    private String alertId;
    private String farmId;
    private String deviceId;
    private String severity;
    private String message;
    private Instant timestamp;

    public FarmAlert() {
    }

    public FarmAlert(
            String alertId,
            String farmId,
            String deviceId,
            String severity,
            String message,
            Instant timestamp) {

        this.alertId = alertId;
        this.farmId = farmId;
        this.deviceId = deviceId;
        this.severity = severity;
        this.message = message;
        this.timestamp = timestamp;
    }

    public String getAlertId() {
        return alertId;
    }

    public void setAlertId(String alertId) {
        this.alertId = alertId;
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

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}
