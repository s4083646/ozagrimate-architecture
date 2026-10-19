
# OzAgriMate Monitoring & Alert Service API

## Overview

The Monitoring & Alert Service receives, validates and stores
farm sensor readings. It also provides access to the latest
reading and generates moisture-based alerts.

**Technology:** Java, Spring Boot 3.5.7

**Local base URL:** http://localhost:8082

**Storage:** ConcurrentHashMap (in-memory prototype)

**Supported farms:** farm-001, farm-002

## 1. Submit Sensor Reading

**Method:** POST

**Endpoint:** `/farms/{farmId}/readings`

### Example Request

POST http://localhost:8082/farms/farm-001/readings

Content-Type: application/json

```json
{
  "readingId": "reading-001",
  "deviceId": "sensor-001",
  "soilMoisture": 22.5,
  "timestamp": "2026-10-19T03:00:00Z"
}
```

The `farmId` is taken from the URL. If `farmId` is also included
in the request body, it must match the URL.

### Validation Rules

- Reading ID must not be blank.
- Device ID must not be blank.
- Soil moisture must be between 0 and 100, inclusive.
- Soil moisture must be a finite number.
- Timestamp must be present and must not be in the future.
- Reading IDs must be unique within the running prototype.
- Farm must be one of the supported demonstration farms.

### Success Response: 201 Created

```json
{
  "readingId": "reading-001",
  "farmId": "farm-001",
  "deviceId": "sensor-001",
  "soilMoisture": 22.5,
  "timestamp": "2026-10-19T03:00:00Z"
}
```

### Error Responses

| Status | Meaning |
|---|---|
| 400 Bad Request | Invalid or missing reading fields |
| 404 Not Found | Unknown farm |
| 409 Conflict | Duplicate reading ID |

Example duplicate response:

```json
{
  "error": "Duplicate reading ID"
}
```

## 2. Retrieve Latest Sensor Reading

**Method:** GET

**Endpoint:** `/farms/{farmId}/readings/latest`

### Example Request

GET http://localhost:8082/farms/farm-001/readings/latest

### Success Response: 200 OK

```json
{
  "readingId": "reading-001",
  "farmId": "farm-001",
  "deviceId": "sensor-001",
  "soilMoisture": 22.5,
  "timestamp": "2026-10-19T03:00:00Z"
}
```

The latest reading is selected using its measurement timestamp,
not its insertion order.

### Error Responses

| Status | Meaning |
|---|---|
| 404 Not Found | Unknown farm or no readings available |

## 3. Retrieve Farm Alerts

**Method:** GET

**Endpoint:** `/farms/{farmId}/alerts`

### Example Request

GET http://localhost:8082/farms/farm-001/alerts

### Alert Rules

| Soil moisture | Alert |
|---|---|
| Below 20% | CRITICAL |
| 20% to below 30% | WARNING |
| 30% or above | No alert |

These thresholds are demonstration values, not agronomic
recommendations.

### Success Response: 200 OK

Example critical alert:

```json
[
  {
    "alertId": "alert-reading-004",
    "farmId": "farm-001",
    "deviceId": "sensor-001",
    "severity": "CRITICAL",
    "message": "Very low soil moisture detected",
    "timestamp": "2026-10-19T03:30:00Z"
  }
]
```

When no alert is generated, the response is:

```json
[]
```

### Error Responses

| Status | Meaning |
|---|---|
| 404 Not Found | Unknown farm |

## Architectural Decisions

- ADR-02: Sensor Data Reliability and Validation
- ADR-04: Event-Driven Scalability for Sensor Processing
- ADR-05: Offline Buffering and Availability

## Prototype Limitations

This assignment prototype uses in-memory storage.
Readings are lost when the application restarts.

Alerts are calculated from the latest reading for each farm.
Historical alerts are not persisted.

The production architecture proposes persistent storage,
event-driven processing and offline edge buffering. These
capabilities are not implemented in the current prototype.

Authentication and authorization are not implemented in this
monitoring service prototype.
