package au.edu.rmit.ozagrimate.irrigation.model;

// ADR-07: can only go PENDING_APPROVAL -> APPROVED or REJECTED
public enum RecommendationStatus {
    PENDING_APPROVAL,
    APPROVED,
    REJECTED
}
