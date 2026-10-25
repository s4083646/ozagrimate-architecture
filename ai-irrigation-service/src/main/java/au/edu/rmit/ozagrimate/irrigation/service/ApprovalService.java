package au.edu.rmit.ozagrimate.irrigation.service;

import au.edu.rmit.ozagrimate.irrigation.exception.ConflictException;
import au.edu.rmit.ozagrimate.irrigation.exception.ForbiddenException;
import au.edu.rmit.ozagrimate.irrigation.exception.InvalidRequestException;
import au.edu.rmit.ozagrimate.irrigation.model.ApprovalRequest;
import au.edu.rmit.ozagrimate.irrigation.model.Recommendation;
import au.edu.rmit.ozagrimate.irrigation.model.RecommendationStatus;
import au.edu.rmit.ozagrimate.irrigation.repository.RecommendationRepository;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

// ADR-07: only place the status can change, and only for agronomists.
// role comes from the X-User-Role header for now (no real login in the prototype)
@Service
public class ApprovalService {

    static final String AGRONOMIST_ROLE = "AGRONOMIST";

    private final RecommendationService recommendationService;
    private final RecommendationRepository repository;

    public ApprovalService(
            RecommendationService recommendationService,
            RecommendationRepository repository) {

        this.recommendationService = recommendationService;
        this.repository = repository;
    }

    public synchronized Recommendation review(
            String farmId,
            String id,
            String userRole,
            ApprovalRequest request) {

        if (!AGRONOMIST_ROLE.equalsIgnoreCase(userRole)) {
            throw new ForbiddenException("Only agronomists can review recommendations");
        }

        Recommendation recommendation = recommendationService.get(farmId, id);

        RecommendationStatus newStatus = parseDecision(request);

        if (isBlank(request.getReviewerId())) {
            throw new InvalidRequestException("reviewerId is required");
        }

        if (recommendation.getStatus() != RecommendationStatus.PENDING_APPROVAL) {
            throw new ConflictException(
                    "Recommendation has already been " + recommendation.getStatus());
        }

        recommendation.recordReview(
                newStatus,
                request.getReviewerId(),
                request.getComment(),
                Instant.now().truncatedTo(ChronoUnit.SECONDS));

        return repository.save(recommendation);
    }

    private RecommendationStatus parseDecision(ApprovalRequest request) {
        if (request == null || request.getDecision() == null) {
            throw new InvalidRequestException("decision must be APPROVED or REJECTED");
        }
        return switch (request.getDecision().toUpperCase()) {
            case "APPROVED" -> RecommendationStatus.APPROVED;
            case "REJECTED" -> RecommendationStatus.REJECTED;
            default -> throw new InvalidRequestException("decision must be APPROVED or REJECTED");
        };
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
