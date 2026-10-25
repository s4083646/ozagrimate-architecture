# Persistent Data Store (proposed)

Shown in the Container diagram (Figure 2) and Deployment diagram (Figure 8).

A PostgreSQL database with a separate schema for each service
(`device_schema`, `monitoring_schema`, `irrigation_schema`), as decided
in ADR-01.

Not implemented in the prototype. Each service uses an in-memory
`ConcurrentHashMap` in its `repository` package instead, so data is
reset whenever a service restarts.
