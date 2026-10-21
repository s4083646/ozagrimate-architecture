
package au.edu.rmit.ozagrimate.monitoring.controller;

import au.edu.rmit.ozagrimate.monitoring.model.FarmAlert;
import au.edu.rmit.ozagrimate.monitoring.service.AlertService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/farms/{farmId}/alerts")
public class FarmAlertController {

    private static final Set<String> VALID_FARMS =
            Set.of("farm-001", "farm-002");

    private final AlertService alertService;

    public FarmAlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public ResponseEntity<?> getFarmAlerts(
            @PathVariable("farmId") String farmId) {

        if (!VALID_FARMS.contains(farmId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Farm not found"));
        }

        List<FarmAlert> alerts =
                alertService.getAlertsForFarm(farmId);

        return ResponseEntity.ok(alerts);
    }
}
