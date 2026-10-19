
package au.edu.rmit.ozagrimate.monitoring.service;

import au.edu.rmit.ozagrimate.monitoring.model.FarmAlert;
import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import au.edu.rmit.ozagrimate.monitoring.repository.SensorReadingRepository;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AlertService {

    private final SensorReadingRepository repository;

    public AlertService(SensorReadingRepository repository) {
        this.repository = repository;
    }

    public List<FarmAlert> getAlertsForFarm(String farmId) {

        List<FarmAlert> alerts = new ArrayList<>();

        repository.findLatestByFarmId(farmId).ifPresent(reading -> {

            double moisture = reading.getSoilMoisture();

            if (moisture < 20) {
                alerts.add(createAlert(
                        reading,
                        "CRITICAL",
                        "Very low soil moisture detected"
                ));

            } else if (moisture < 30) {
                alerts.add(createAlert(
                        reading,
                        "WARNING",
                        "Low soil moisture detected"
                ));
            }
        });

        return alerts;
    }

    private FarmAlert createAlert(
            SensorReading reading,
            String severity,
            String message) {

        return new FarmAlert(
                "alert-" + reading.getReadingId(),
                reading.getFarmId(),
                reading.getDeviceId(),
                severity,
                message,
                reading.getTimestamp()
        );
    }
}
