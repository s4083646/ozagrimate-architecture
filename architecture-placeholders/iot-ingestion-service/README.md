# IoT Ingestion Service (proposed)

Shown in the Container diagram (Figure 2) and Deployment diagram (Figure 8).

Receives readings from farm sensors and edge gateways, converts
vendor-specific messages into our standard format using protocol
adapters (ADR-06), and publishes them to the Message Broker (ADR-04).

Not implemented in the prototype. Readings are sent straight to the
Monitoring & Alert Service with `POST /farms/{farmId}/readings`, which
still does the validation and duplicate checks from ADR-02.
