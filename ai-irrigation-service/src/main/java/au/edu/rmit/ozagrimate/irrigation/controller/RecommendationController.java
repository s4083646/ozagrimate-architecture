package au.edu.rmit.ozagrimate.irrigation.controller;

import au.edu.rmit.ozagrimate.irrigation.model.ApprovalRequest;
import au.edu.rmit.ozagrimate.irrigation.model.ApprovalResponse;
import au.edu.rmit.ozagrimate.irrigation.model.Recommendation;
import au.edu.rmit.ozagrimate.irrigation.model.RecommendationRequest;
import au.edu.rmit.ozagrimate.irrigation.service.ApprovalService;
import au.edu.rmit.ozagrimate.irrigation.service.RecommendationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

// API 6-8. errors are handled in ApiExceptionHandler
@RestController
@RequestMapping("/farms/{farmId}/recommendations")
public class RecommendationController {

    private final RecommendationService recommendationService;
    private final ApprovalService approvalService;

    public RecommendationController(
            RecommendationService recommendationService,
            ApprovalService approvalService) {

        this.recommendationService = recommendationService;
        this.approvalService = approvalService;
    }

    // API 6
    @PostMapping
    public ResponseEntity<Recommendation> generate(
            @PathVariable("farmId") String farmId,
            @RequestBody RecommendationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(recommendationService.generate(farmId, request));
    }

    // API 7
    @GetMapping("/{id}")
    public ResponseEntity<Recommendation> get(
            @PathVariable("farmId") String farmId,
            @PathVariable("id") String id) {

        return ResponseEntity.ok(recommendationService.get(farmId, id));
    }

    // API 8
    @PutMapping("/{id}/approval")
    public ResponseEntity<ApprovalResponse> review(
            @PathVariable("farmId") String farmId,
            @PathVariable("id") String id,
            @RequestHeader(value = "X-User-Role", required = false) String userRole,
            @RequestBody ApprovalRequest request) {

        Recommendation reviewed = approvalService.review(farmId, id, userRole, request);
        return ResponseEntity.ok(ApprovalResponse.from(reviewed));
    }
}
