
package au.edu.rmit.ozagrimate.monitoring.repository;

import au.edu.rmit.ozagrimate.monitoring.model.SensorReading;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SensorReadingRepository {

    private final Map<String, SensorReading> readings =
            new ConcurrentHashMap<>();

    public boolean saveIfAbsent(SensorReading reading) {
        return readings.putIfAbsent(
                reading.getReadingId(), reading) == null;
    }

    public boolean existsById(String readingId) {
        return readings.containsKey(readingId);
    }

    public Optional<SensorReading> findById(String readingId) {
        return Optional.ofNullable(readings.get(readingId));
    }

    public Optional<SensorReading> findLatestByFarmId(String farmId) {
        return readings.values().stream()
                .filter(reading -> farmId.equals(reading.getFarmId()))
                .filter(reading -> reading.getTimestamp() != null)
                .max((a, b) -> a.getTimestamp()
                        .compareTo(b.getTimestamp()));
    }
}
