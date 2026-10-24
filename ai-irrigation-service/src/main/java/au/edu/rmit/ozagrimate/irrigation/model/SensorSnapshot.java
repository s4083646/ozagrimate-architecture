package au.edu.rmit.ozagrimate.irrigation.model;

/** Latest soil moisture reading used as input to a recommendation. */
public record SensorSnapshot(String readingId, double soilMoisture) {
}
