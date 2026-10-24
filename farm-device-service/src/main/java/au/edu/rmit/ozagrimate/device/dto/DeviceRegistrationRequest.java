package au.edu.rmit.ozagrimate.device.dto;

public record DeviceRegistrationRequest(
    String deviceId,
    String vendor,
    String sensorType,
    String protocol,
    String location
) {}
