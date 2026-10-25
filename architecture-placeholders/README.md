# Architecture placeholders

These folders are containers from our architecture that we did **not**
code for the prototype. The assignment only asks for the 8 REST
endpoints and says the UI, API gateway, event bus and database don't
need to be implemented, so each folder just has a README explaining
where it fits.

They are here so the repo structure still lines up with the Container
diagram (Figure 2) and the Deployment diagram (Figure 8).

| Folder | Container in the diagrams | Related ADRs |
|---|---|---|
| `api-gateway/` | API Gateway | ADR-01, ADR-03 |
| `iot-ingestion-service/` | IoT Ingestion Service | ADR-04, ADR-06 |
| `message-broker/` | Message Broker | ADR-01, ADR-04 |
| `persistent-data-store/` | Persistent Data Store | ADR-01, ADR-02 |
| `edge-gateway/` | Edge Gateway (farm site, Deployment diagram) | ADR-05 |

The three services we did implement are in the root folders
`farm-device-service/`, `monitoring-alert-service/` and
`ai-irrigation-service/`.
