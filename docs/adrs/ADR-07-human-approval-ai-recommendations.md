# ADR-07: Human Approval for AI Recommendations

## Status
Accepted

## Context
OzAgriMate generates AI-assisted irrigation recommendations from sensor and weather data. A wrong recommendation could waste scarce water during drought or stress crops, and farmers need to trust the advice before acting on it. Sensor faults and stale weather data (R2) mean the inputs are not always reliable. We considered three options: fully automated irrigation control, advisory-only recommendations with no recorded review, and mandatory agronomist approval. Full automation is fastest but removes human judgement from high-impact decisions, while advisory-only provides no accountability or audit trail.

## Decision
Every AI-generated recommendation will start in a PENDING_APPROVAL state and can only be actioned once an agronomist approves it. The AI & Irrigation Service separates recommendation generation from an Approval Service that enforces allowed state changes (PENDING_APPROVAL to APPROVED or REJECTED only) and checks the reviewer's role. Each recommendation stores its supporting data, rationale, reviewer, timestamp and comment, so decisions can be explained and audited. The prototype uses deterministic rules instead of a trained model, and an X-User-Role header in place of ADR-03's token-based authentication.

## Consequences
Human review reduces the risk of harmful irrigation actions and builds farmer trust, and the audit trail supports accountability. However, approval adds delay, so urgent irrigation may wait on agronomist availability, and review workload grows with farm numbers. Low-risk recommendations could later be auto-approved under defined thresholds.

## Compliance
API tests confirm recommendations are created as PENDING_APPROVAL, non-agronomist approvals return 403 and repeated reviews return 409. Code reviews confirm no other path changes recommendation status.
