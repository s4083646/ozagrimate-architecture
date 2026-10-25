# OzAgriMate - Assignment 4 (ISYS3459 Systems Architecture and Design)

Prototype implementation of the OzAgriMate platform architecture for
Assignment 4. OzAgriMate helps Australian farmers monitor their farms,
get AI-assisted irrigation recommendations and keep working when rural
internet drops out.

The full architecture (ADRs, diagrams, API design) is in our
architecture document. This repo shows how that architecture maps to
code. As the assignment asks, we only implemented the **8 REST
endpoints** using in-memory data. Everything else in the architecture
has a placeholder folder so the repo still matches our diagrams.

## Team

| Name | Student number | Role |
|---|---|---|
| Shahira Jasmine Binti Yahaya | S4111555 | Role A - Farm & Device Service, security and interoperability |
| Aryan Gupta | S4098479 | Role B - AI & Irrigation Service, human approval and weather fallback |
| Dhiya Batrisya Binti Norfaiz | S4083646 | Role C - Monitoring & Alert Service, reliability, scalability and offline sync |

## Architecture in one paragraph

We used a **hybrid service-based and event-driven** style (ADR-01).
There are three coarse-grained Spring Boot services, one per main
domain. Users reach them through an API Gateway using REST, and sensor
readings are meant to flow asynchronously through an IoT Ingestion
Service and Message Broker. Each service would own its own schema in a
shared PostgreSQL database.

## How the repo maps to the Container diagram (Figure 2)

| Folder | Container | Port | Status |
|---|---|---|---|
| `farm-device-service/` | Farm & Device Service | 8081 | Implemented (API 4-5) |
| `monitoring-alert-service/` | Monitoring & Alert Service | 8082 | Implemented (API 1-3) |
| `ai-irrigation-service/` | AI & Irrigation Service | 8083 | Implemented (API 6-8) |
| `architecture-placeholders/api-gateway/` | API Gateway | - | Proposed only |
| `architecture-placeholders/iot-ingestion-service/` | IoT Ingestion Service | - | Proposed only |
| `architecture-placeholders/message-broker/` | Message Broker | - | Proposed only |
| `architecture-placeholders/persistent-data-store/` | Persistent Data Store | - | Proposed only (in-memory maps used instead) |
| `architecture-placeholders/edge-gateway/` | Edge Gateway (Deployment diagram) | - | Proposed only |

## How each service maps to its Component diagram

All three services use the same package layout under
`src/main/java/au/edu/rmit/ozagrimate/<service>/`, so it's easy to find
each component from the diagrams.

**Farm & Device Service** - Figure 3 (`...ozagrimate/device/`)

| Component | Class |
|---|---|
| Device Controller | `controller/DeviceController` |
| Device Registry Service | `service/DeviceRegistryService` |
| Device Repository | `repository/DeviceRepository` |

**Monitoring & Alert Service** - Figure 4 (`...ozagrimate/monitoring/`)

| Component | Class |
|---|---|
| SensorReadingController | `controller/SensorReadingController` |
| FarmAlertController | `controller/FarmAlertController` |
| SensorValidationService | `service/SensorValidationService` |
| AlertService | `service/AlertService` |
| SensorReadingRepository | `repository/SensorReadingRepository` |

**AI & Irrigation Service** - Figure 5 (`...ozagrimate/irrigation/`)

| Component | Class |
|---|---|
| Recommendation Controller | `controller/RecommendationController` |
| API Exception Handler | `exception/ApiExceptionHandler` |
| Recommendation Service | `service/RecommendationService` |
| Approval Service | `service/ApprovalService` |
| Sensor Data Client | `service/SensorDataClient` |
| Weather Integration | `service/WeatherIntegrationService` |
| Farm Registry | `service/FarmRegistry` |
| Recommendation Repository | `repository/RecommendationRepository` |

## The 8 endpoints

| # | Method | Endpoint | Service | Success |
|---|---|---|---|---|
| 1 | POST | `/farms/{farmId}/readings` | Monitoring & Alert (8082) | 201 |
| 2 | GET | `/farms/{farmId}/readings/latest` | Monitoring & Alert (8082) | 200 |
| 3 | GET | `/farms/{farmId}/alerts` | Monitoring & Alert (8082) | 200 |
| 4 | POST | `/farms/{farmId}/devices` | Farm & Device (8081) | 201 |
| 5 | DELETE | `/farms/{farmId}/devices/{deviceId}` | Farm & Device (8081) | 204 |
| 6 | POST | `/farms/{farmId}/recommendations` | AI & Irrigation (8083) | 201 |
| 7 | GET | `/farms/{farmId}/recommendations/{id}` | AI & Irrigation (8083) | 200 |
| 8 | PUT | `/farms/{farmId}/recommendations/{id}/approval` | AI & Irrigation (8083) | 200 |

Seeded farms are `farm-001` and `farm-002` in every service. Full
request/response examples and error codes are in `docs/api-design/`.

API 6 calls API 2 on the Monitoring & Alert Service to get the latest
soil moisture. If that service isn't running it falls back to a fixed
reading, so the AI & Irrigation Service can still be tested on its own.

## Running it

You need **Java 17+** and **Maven 3.9+**.

Run all the unit tests (50 tests across the three services):

```
mvn test
```

Start the services, each in its own terminal:

```
mvn -pl farm-device-service spring-boot:run
mvn -pl monitoring-alert-service spring-boot:run
mvn -pl ai-irrigation-service spring-boot:run
```

On Windows you can also run `scripts\start-services.ps1`, which builds
everything and opens the three services in separate windows.

## Testing with Postman

Import `docs/postman/OzAgriMate.postman_collection.json` and run the
whole collection in order (26 requests, each with a status-code test).
It covers success and error cases for all 8 endpoints.

Data is kept in memory, so **restart the services before running the
collection again**. Otherwise the duplicate and approval tests won't
start from a clean state.

## Docs

- `docs/adrs/` - all 8 ADRs (ADR-01 style, ADR-02 to ADR-05 quality
  attributes, ADR-06 to ADR-08 other decisions)
- `docs/diagrams/` - exported diagrams (Figures 1, 2, 4, 5, 6, 7, 8)
- `docs/api-design/` - API docs for each service
- `docs/postman/` - Postman collection

## What's prototype vs proposed

- Storage is in-memory (`ConcurrentHashMap`), not PostgreSQL.
- There's no API gateway, message broker or edge gateway. Services are
  called directly.
- The recommendation "AI" is a set of deterministic rules, not a
  trained model (ADR-07).
- The external weather API is simulated. `farm-001` is set up as a
  weather outage so the cached fallback (ADR-08) can be shown;
  `farm-002` uses the live path.
- Token-based authentication from ADR-03 isn't implemented. Approvals
  use an `X-User-Role: AGRONOMIST` header instead.
