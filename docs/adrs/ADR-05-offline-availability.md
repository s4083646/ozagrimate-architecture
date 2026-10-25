# ADR-05: Offline Buffering and Availability

## Status
Accepted

## Context
Australian farms often operate in rural locations with intermittent internet connectivity. Network disruptions may prevent IoT sensors from sending readings to OzAgriMate, delaying monitoring alerts and irrigation decisions. Losing measurements during outages could reduce data quality and undermine farmer confidence. The architecture must preserve sensor readings while disconnected and synchronize them after connectivity returns, without introducing duplicates or overwhelming the platform. Alternatives were discarding readings during outages, which loses data, or relying on each sensor's own memory, which is small and varies by vendor.

## Decision
OzAgriMate will use an edge gateway with persistent local buffering. When connectivity is unavailable, the gateway will store sensor readings locally with unique identifiers and timestamps. Once the connection is restored, it will upload buffered readings in batches using retries with exponential backoff. The monitoring service will validate incoming readings and reject duplicate identifiers to support idempotent processing. Local storage limits, retry policies and synchronization status will be monitored. The prototype demonstrates server-side validation and duplicate detection, while offline buffering remains a proposed production capability.

## Consequences
Local buffering reduces data loss during network outages and allows farm monitoring to recover after reconnection. Batch synchronization avoids unnecessary network requests. However, local storage requires capacity management, and delayed readings may produce stale information. Device failures, extended outages and synchronization conflicts still require additional safeguards.

## Compliance
Architecture reviews will verify buffering, retries and deduplication. Production tests will simulate disconnections, reconnections, duplicate uploads and storage exhaustion to assess recovery behaviour.
