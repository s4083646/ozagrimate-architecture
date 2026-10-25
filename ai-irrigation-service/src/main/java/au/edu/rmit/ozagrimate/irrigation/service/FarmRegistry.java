package au.edu.rmit.ozagrimate.irrigation.service;

import au.edu.rmit.ozagrimate.irrigation.exception.NotFoundException;
import org.springframework.stereotype.Component;

import java.util.Set;

// same farm IDs as the other services. In production this would call the Farm & Device Service
@Component
public class FarmRegistry {

    private static final Set<String> VALID_FARMS =
            Set.of("farm-001", "farm-002");

    public void requireFarm(String farmId) {
        if (!VALID_FARMS.contains(farmId)) {
            throw new NotFoundException("Farm not found");
        }
    }
}
