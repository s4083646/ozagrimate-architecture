package au.edu.rmit.ozagrimate.irrigation.repository;

import au.edu.rmit.ozagrimate.irrigation.model.Recommendation;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * In-memory store for recommendations. Stands in for the proposed
 * Recommendation database in the production architecture.
 */
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

    /** Finds a recommendation only if it belongs to the given farm. */
    public Optional<Recommendation> findByFarmAndId(String farmId, String id) {
        return Optional.ofNullable(recommendations.get(id))
                .filter(r -> farmId.equals(r.getFarmId()));
    }
}
