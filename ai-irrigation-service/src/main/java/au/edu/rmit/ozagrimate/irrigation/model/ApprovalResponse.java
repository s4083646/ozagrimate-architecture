package au.edu.rmit.ozagrimate.irrigation.model;

import java.time.Instant;

/** Response body for a successful approval or rejection. */
public record ApprovalResponse(
        String recommendationId,
        RecommendationStatus status,
        String reviewedBy,
        Instant reviewedAt,
        String reviewComment) {

    public static ApprovalResponse from(Recommendation r) {
        return new ApprovalResponse(
                r.getRecommendationId(),
                r.getStatus(),
                r.getReviewedBy(),
                r.getReviewedAt(),
                r.getReviewComment());
    }
}
