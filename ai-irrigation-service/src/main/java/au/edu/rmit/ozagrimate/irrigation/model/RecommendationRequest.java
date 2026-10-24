package au.edu.rmit.ozagrimate.irrigation.model;

/** Request body for POST /farms/{farmId}/recommendations. */
public class RecommendationRequest {

    private String cropType;
    private String requestedBy;

    public RecommendationRequest() {
    }

    public RecommendationRequest(String cropType, String requestedBy) {
        this.cropType = cropType;
        this.requestedBy = requestedBy;
    }

    public String getCropType() { return cropType; }
    public void setCropType(String cropType) { this.cropType = cropType; }
    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }
}
