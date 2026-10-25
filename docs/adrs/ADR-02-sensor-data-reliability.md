# ADR-02: Sensor Data Reliability and Validation

## Status
Accepted

## Context
OzAgriMate receives sensor readings from IoT devices across Australian farms. Sensors may produce invalid measurements, duplicate readings or incomplete data due to equipment faults and unreliable connectivity. Incorrect readings could affect monitoring alerts and AI-assisted irrigation recommendations. As the platform expands from approximately 5,000 to over 50,000 farms, validation must remain consistent. The architecture requires a reliable mechanism to identify invalid or duplicate readings before they influence downstream decisions. Alternatives were accepting all readings and filtering them later, which lets bad data reach alerts, or validating only on devices, which cannot be trusted across vendors.

## Decision
OzAgriMate will validate incoming sensor readings before accepting them for processing. The monitoring service checks required identifiers, timestamps and soil moisture values against defined ranges. Duplicate reading identifiers are rejected to prevent repeated processing. The architecture separates REST controllers, validation services and repositories to isolate responsibilities. The production design will support event-driven ingestion with durable storage and idempotent processing, while the assignment prototype demonstrates validation and duplicate detection using an in-memory repository.

## Consequences
Valid readings can be processed consistently, reducing the risk of incorrect alerts and recommendations. Separating validation from storage improves maintainability and testability. However, validation adds processing overhead, and an in-memory repository cannot guarantee durability or deduplication across multiple service instances. Production deployment requires persistent storage, distributed idempotency and monitoring.

## Compliance
JUnit and HTTP-level tests verify valid readings, invalid measurements, duplicate rejection and expected response codes. Architecture reviews will confirm that validation occurs before downstream processing.
