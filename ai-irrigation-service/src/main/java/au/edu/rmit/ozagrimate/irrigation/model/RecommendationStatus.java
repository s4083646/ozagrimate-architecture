package au.edu.rmit.ozagrimate.irrigation.model;

/**
 * Approval lifecycle for an AI-assisted recommendation (ADR-07).
 * Allowed transitions: PENDING_APPROVAL -> APPROVED or REJECTED.
 */
public enum RecommendationStatus {
    PENDING_APPROVAL,
    APPROVED,
    REJECTED
}
