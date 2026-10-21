# OzAgriMate AI & Irrigation Service API

## Overview

The AI & Irrigation Service generates irrigation recommendations from
the latest soil moisture reading and the weather forecast. Every
recommendation starts as `PENDING_APPROVAL` and must be approved or
rejected by an agronomist before it can be actioned (ADR-07). Weather
data comes through the Weather Integration component, which falls back
to a cached forecast when the external weather API is unavailable
(ADR-08).

**Technology:** Java, Spring Boot 3.5.7

**Local base URL:** http://localhost:8083

**Storage:** ConcurrentHashMap (in-memory prototype)

**Supported farms:** farm-001, farm-002

**Prototype notes:**

- The recommendation engine uses deterministic rules instead of a
  trained AI model.
- The latest reading is fetched from the Monitoring & Alert Service
  (`GET http://localhost:8082/farms/{farmId}/readings/latest`). If that
  service is not running or has no reading, a fallback reading
  (`reading-001`, soil moisture 18.4) is used.
- The external weather API is simulated. `farm-001` is configured as a
  simulated outage, so it uses the cached forecast (`CACHED`).
  `farm-002` uses the live forecast (`LIVE`).
- The reviewer's role is read from the `X-User-Role` header instead of
  an authenticated token.

### Recommendation rules

| Condition | Action | Water | Duration |
|---|---|---|---|
| Forecast rain ≥ 5 mm | NO_IRRIGATION | 0 mm | 0 min |
| Soil moisture < 20 | IRRIGATE | 12.5 mm | 45 min |
| Soil moisture 20 to < 30 | IRRIGATE | 6.0 mm | 25 min |
| Soil moisture ≥ 30 | NO_IRRIGATION | 0 mm | 0 min |

Cached weather older than 6 hours adds a stale-data warning to the
rationale.

## 1. Generate Recommendation

**Method:** POST

**Endpoint:** `/farms/{farmId}/recommendations`

### Example Request

POST http://localhost:8083/farms/farm-001/recommendations

Content-Type: application/json

```json
{
  "cropType": "wheat",
  "requestedBy": "farmer-01"
}
```

### Example Response — 201 Created

```json
{
  "recommendationId": "rec-001",
  "farmId": "farm-001",
  "action": "IRRIGATE",
  "waterAmountMm": 12.5,
  "durationMinutes": 45,
  "status": "PENDING_APPROVAL",
  "rationale": "Soil moisture below 20 and no rain forecast",
  "supportingData": {
    "readingId": "reading-001",
    "soilMoisture": 18.4,
    "forecastRainMm": 0.0,
    "weatherSource": "CACHED",
    "weatherDataAgeMinutes": 95
  },
  "createdAt": "2026-10-19T04:00:00Z",
  "reviewedBy": null,
  "reviewedAt": null,
  "reviewComment": null
}
```

### Error Responses

| Status | When |
|---|---|
| 400 Bad Request | `cropType` missing, or body missing/malformed |
| 404 Not Found | Unknown farm |

## 2. Get Recommendation

**Method:** GET

**Endpoint:** `/farms/{farmId}/recommendations/{id}`

### Example Request

GET http://localhost:8083/farms/farm-001/recommendations/rec-001

### Example Response — 200 OK

Same body as the generate response. The review fields stay `null`
until an agronomist reviews the recommendation.

### Error Responses

| Status | When |
|---|---|
| 404 Not Found | Unknown farm, unknown recommendation, or the recommendation belongs to a different farm |

## 3. Approve or Reject Recommendation

**Method:** PUT

**Endpoint:** `/farms/{farmId}/recommendations/{id}/approval`

**Required header:** `X-User-Role: AGRONOMIST`

### Example Request

PUT http://localhost:8083/farms/farm-001/recommendations/rec-001/approval

Content-Type: application/json

X-User-Role: AGRONOMIST

```json
{
  "decision": "APPROVED",
  "reviewerId": "agro-07",
  "comment": "Run after 6pm to reduce evaporation"
}
```

`decision` must be `APPROVED` or `REJECTED`.

### Example Response — 200 OK

```json
{
  "recommendationId": "rec-001",
  "status": "APPROVED",
  "reviewedBy": "agro-07",
  "reviewedAt": "2026-10-19T05:00:00Z",
  "reviewComment": "Run after 6pm to reduce evaporation"
}
```

### Error Responses

| Status | When |
|---|---|
| 400 Bad Request | `decision` not APPROVED/REJECTED, `reviewerId` missing, or body malformed |
| 403 Forbidden | `X-User-Role` missing or not AGRONOMIST |
| 404 Not Found | Unknown farm or recommendation |
| 409 Conflict | Recommendation has already been reviewed |

All errors use the body `{"error": "<message>"}`.
