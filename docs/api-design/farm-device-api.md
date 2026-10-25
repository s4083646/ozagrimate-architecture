# OzAgriMate Farm & Device Service API

## Overview

The Farm & Device Service registers IoT devices against a farm and
removes them when they are no longer used. Each device records its
vendor, sensor type and protocol, which supports the device
interoperability decision in ADR-06.

**Technology:** Java, Spring Boot 3.5.7

**Local base URL:** http://localhost:8081

**Storage:** ConcurrentHashMap (in-memory prototype)

**Supported farms:** farm-001, farm-002

## 1. Register Device

**Method:** POST

**Endpoint:** `/farms/{farmId}/devices`

### Example Request

POST http://localhost:8081/farms/farm-001/devices

Content-Type: application/json

```json
{
  "deviceId": "sensor-002",
  "vendor": "AgriSense",
  "sensorType": "SOIL_MOISTURE",
  "protocol": "MQTT",
  "location": "North Field"
}
```

All five fields are required. `deviceId` can only contain letters,
numbers, `-` and `_`.

### Success Response: 201 Created

The response includes a `Location` header pointing to the new device.

```json
{
  "deviceId": "sensor-002",
  "farmId": "farm-001",
  "vendor": "AgriSense",
  "sensorType": "SOIL_MOISTURE",
  "protocol": "MQTT",
  "location": "North Field",
  "status": "ACTIVE"
}
```

### Error Responses

| Status | Meaning |
|---|---|
| 400 Bad Request | Missing field or invalid device ID format |
| 404 Not Found | Unknown farm |
| 409 Conflict | Device ID already registered |

## 2. Remove Device

**Method:** DELETE

**Endpoint:** `/farms/{farmId}/devices/{deviceId}`

### Example Request

DELETE http://localhost:8081/farms/farm-001/devices/sensor-002

### Success Response: 204 No Content

No response body.

### Error Responses

| Status | Meaning |
|---|---|
| 404 Not Found | Unknown farm, unknown device, or the device belongs to a different farm |

## Architectural Decisions

- ADR-03: Security and Access Control
- ADR-06: Heterogeneous IoT Device Interoperability

## Prototype Limitations

Devices are stored in memory, so they are lost when the service
restarts.

ADR-03 says only platform administrators should be able to register
or remove devices. Authentication and role checks are part of the
proposed production design and are not implemented in this prototype.

Protocol adapters at the IoT ingestion boundary (ADR-06) are also
proposed only. The prototype stores the protocol on each device but
does not convert messages.
