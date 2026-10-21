package au.edu.rmit.ozagrimate.irrigation.model;

import java.time.Instant;

/**
 * An AI-assisted irrigation recommendation. Field order matches the
 * API documentation so JSON responses read the same way.
 */
public class Recommendation {

    private String recommendationId;
    private String farmId;
    private IrrigationAction action;
    private double waterAmountMm;
    private int durationMinutes;
    private RecommendationStatus status;
    private String rationale;
    private SupportingData supportingData;
    private Instant createdAt;
    private String reviewedBy;
    private Instant reviewedAt;
    private String reviewComment;

    public Recommendation() {
    }

    public Recommendation(
            String recommendationId,
            String farmId,
            IrrigationAction action,
            double waterAmountMm,
            int durationMinutes,
            String rationale,
            SupportingData supportingData,
            Instant createdAt) {

        this.recommendationId = recommendationId;
        this.farmId = farmId;
        this.action = action;
        this.waterAmountMm = waterAmountMm;
        this.durationMinutes = durationMinutes;
        this.status = RecommendationStatus.PENDING_APPROVAL;
        this.rationale = rationale;
        this.supportingData = supportingData;
        this.createdAt = createdAt;
    }

    /** Records an agronomist's review. Only ApprovalService calls this. */
    public void recordReview(
            RecommendationStatus newStatus,
            String reviewer,
            String comment,
            Instant at) {

        this.status = newStatus;
        this.reviewedBy = reviewer;
        this.reviewComment = comment;
        this.reviewedAt = at;
    }

    public String getRecommendationId() { return recommendationId; }
    public String getFarmId() { return farmId; }
    public IrrigationAction getAction() { return action; }
    public double getWaterAmountMm() { return waterAmountMm; }
    public int getDurationMinutes() { return durationMinutes; }
    public RecommendationStatus getStatus() { return status; }
    public String getRationale() { return rationale; }
    public SupportingData getSupportingData() { return supportingData; }
    public Instant getCreatedAt() { return createdAt; }
    public String getReviewedBy() { return reviewedBy; }
    public Instant getReviewedAt() { return reviewedAt; }
    public String getReviewComment() { return reviewComment; }
}
