# Edge Gateway (proposed)

Shown in the Deployment diagram (Figure 8) at the farm site, and in the
Offline Sensor Synchronization sequence diagram (Figure 6).

A small device on each farm that keeps sensor readings in a local
buffer when the internet drops, then uploads them in batches with
retries once the connection is back (ADR-05).

Not implemented in the prototype. The server-side part of ADR-05
(rejecting duplicate reading IDs) is implemented in the Monitoring &
Alert Service.
