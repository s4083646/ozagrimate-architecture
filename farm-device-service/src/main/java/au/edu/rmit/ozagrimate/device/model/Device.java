package au.edu.rmit.ozagrimate.device.model;

public record Device(
    String deviceId,
    String farmId,
    String vendor,
    String sensorType,
    String protocol,
    String location,
    String status
) {}
