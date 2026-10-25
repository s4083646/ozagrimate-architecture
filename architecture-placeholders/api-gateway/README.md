# API Gateway (proposed)

Shown in the Container diagram (Figure 2) and Deployment diagram (Figure 8).

In production this would be the single entry point for farmers,
agronomists and administrators. It routes requests to the three
services and is where token checks and TLS termination from ADR-03
would happen.

Not implemented in the prototype. In Postman we call each service
directly on its own port (8081, 8082, 8083).
