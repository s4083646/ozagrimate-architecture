# ADR-06: Heterogeneous IoT Device Interoperability

## Status
Accepted

## Context
Australian farms use IoT devices from multiple suppliers with different protocols, data formats and measurement units. ASR1 and constraint C2 require integration across these devices. Risk R3 identifies incompatible devices and unreliable readings, while ASR26 requires administrators to manage device registration. One option is to implement vendor-specific parsing independently in each service, but this duplicates logic and increases coupling. Another is to use protocol adapters at a defined ingestion boundary that convert supported messages into a common data format. The architecture must support new devices without repeatedly modifying monitoring and irrigation services.

## Decision
OzAgriMate will adopt protocol-specific adapters at the IoT ingestion boundary. The Farm & Device Management Service will record each device's farm association, vendor, sensor type and supported protocol. Adapters will convert supported device messages into a standardised format containing identifiers, timestamps, measurement units and values. The ingestion layer will publish normalised events for downstream processing. The Monitoring & Alert Service will validate readings and reject duplicates according to ADR-02. This approach separates vendor-specific integration from core services and supports future device compatibility. The assignment prototype implements in-memory device registration and deletion, while protocol adapters remain proposed production components.

## Consequences
Standardised messages reduce vendor coupling and simplify the integration of new devices. Consistent data formats improve maintainability and support reliable downstream processing. However, adapters require ongoing maintenance as device protocols evolve. Data transformation introduces processing overhead and may lose vendor-specific information. Additional integration components also increase architectural complexity and require monitoring.

## Compliance
Architecture reviews will verify adapter boundaries and standardised data contracts. Integration tests will assess supported protocols, invalid payloads and unit conversion. Prototype tests will verify device registration and deletion.
