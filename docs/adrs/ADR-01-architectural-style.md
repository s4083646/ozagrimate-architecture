# ADR-01: Hybrid Service-Based and Event-Driven Architecture

## Status
Accepted

## Context
OzAgriMate must support about 5,000 farms now and over 50,000 within five years (C4), receive continuous IoT sensor data over unreliable rural connections (C1), and let monitoring, device management and AI features change independently. Reliability, Security and Scalability are the top quality attributes. We compared a modular monolith, microservices, a purely event-driven style and a hybrid service-based and event-driven style. A modular monolith is simple but scales and fails as one unit. Microservices scale most precisely but add heavy operational overhead for a small team. A purely event-driven style suits sensor streams but complicates request-response tasks such as approvals.

## Decision
OzAgriMate will use a hybrid service-based architecture with event-driven sensor processing. Three coarse-grained, separately deployable services cover the main domains: Farm & Device, Monitoring & Alert, and AI & Irrigation. Users reach them through an API Gateway using synchronous REST, which suits request-response tasks such as recommendation approval. Sensor readings flow asynchronously from the IoT Ingestion Service through a message broker to the Monitoring & Alert Service. Each service owns a logically separate schema in a shared PostgreSQL database. The prototype implements the three services as Spring Boot applications using in-memory data.

## Consequences
Services can be deployed, scaled and fail independently, and sensor ingestion can scale separately from user-facing APIs. Coarse-grained services keep operational overhead manageable. However, the broker introduces eventual consistency and extra infrastructure, the shared database couples services at the data layer, and coarse services scale less precisely than microservices.

## Compliance
Architecture reviews confirm one repository module per service matching the Container diagram, and that services communicate only through REST APIs or the broker, never another service's schema.
