package au.edu.rmit.ozagrimate.irrigation.model;

/** Request body for PUT /farms/{farmId}/recommendations/{id}/approval. */
public class ApprovalRequest {

    private String decision;
    private String reviewerId;
    private String comment;

    public ApprovalRequest() {
    }

    public ApprovalRequest(String decision, String reviewerId, String comment) {
        this.decision = decision;
        this.reviewerId = reviewerId;
        this.comment = comment;
    }

    public String getDecision() { return decision; }
    public void setDecision(String decision) { this.decision = decision; }
    public String getReviewerId() { return reviewerId; }
    public void setReviewerId(String reviewerId) { this.reviewerId = reviewerId; }
    public String getComment() { return comment; }
    public void setComment(String comment) { this.comment = comment; }
}
