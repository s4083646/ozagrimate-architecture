# ADR-04: Event-Driven Scalability for Sensor Processing

## Status
Accepted

## Context
OzAgriMate currently targets approximately 5,000 farms, with anticipated growth to over 50,000 farms within five years. Each farm may contain numerous IoT sensors producing continuous measurements. Processing all readings synchronously could overwhelm application services during peak ingestion periods, causing delays in monitoring and irrigation decisions. The architecture must accommodate increasing data volumes and traffic spikes while maintaining responsive APIs and allowing individual services to scale independently. A synchronous monolith was rejected because one ingestion spike would slow every feature, while separate microservices per sensor type would add unnecessary operational overhead.

## Decision
Building on ADR-01, sensor processing will be decoupled from user-facing services. IoT readings will enter through an ingestion gateway and be published to a durable message broker. Monitoring and analytics consumers will process events asynchronously, allowing ingestion and processing to scale independently. Stateless service instances will support horizontal scaling behind load balancers. Queue depth, consumer lag and API latency will guide scaling decisions. The assignment prototype implements the monitoring REST APIs using in-memory storage, while distributed messaging remains a proposed production component.

## Consequences
Asynchronous processing reduces coupling and allows services to absorb traffic spikes through buffering. Independent scaling improves resource efficiency. However, the architecture introduces broker management, eventual consistency and additional operational complexity. The in-memory prototype does not demonstrate distributed scaling, so performance testing and durable infrastructure are required before production deployment.

## Compliance
Architecture reviews will verify independent service scaling and asynchronous processing. Production load tests will evaluate throughput, latency, queue depth and consumer lag under projected farm growth.
