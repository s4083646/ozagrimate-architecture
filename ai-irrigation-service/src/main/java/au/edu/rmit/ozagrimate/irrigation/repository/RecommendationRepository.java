package au.edu.rmit.ozagrimate.irrigation.repository;

import au.edu.rmit.ozagrimate.irrigation.model.Recommendation;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

// in-memory for the prototype (would be irrigation_schema in PostgreSQL)
@Repository
public class RecommendationRepository {

    private final Map<String, Recommendation> recommendations =
            new ConcurrentHashMap<>();

    private final AtomicInteger sequence = new AtomicInteger();

    public String nextId() {
        return String.format("rec-%03d", sequence.incrementAndGet());
    }

    public Recommendation save(Recommendation recommendation) {
        recommendations.put(recommendation.getRecommendationId(), recommendation);
        return recommendation;
    }

    // returns empty if the recommendation belongs to a different farm
    public Optional<Recommendation> findByFarmAndId(String farmId, String id) {
        return Optional.ofNullable(recommendations.get(id))
                .filter(r -> farmId.equals(r.getFarmId()));
    }
}
