# ADR-08: Weather-Data Fallback and Resilience

## Status
Accepted

## Context
Irrigation recommendations depend on rainfall and temperature forecasts from external weather APIs that OzAgriMate does not control (C5). These services can be slow, rate-limited or unavailable, and using outdated forecasts without warning could produce poor irrigation advice (R2). We considered three options: calling the weather API on every request, failing the recommendation when weather data is unavailable, and caching recent forecasts with freshness rules. Calling live every time adds latency and cost, while failing outright leaves farmers without advice during outages, which is when drought decisions often matter most.

## Decision
The AI & Irrigation Service will access weather data only through a Weather Integration component that caches the latest forecast for each farm. Forecasts refresh hourly, and if the external API fails or exceeds a 3-second timeout, the cached forecast is used instead. Every recommendation records its weather source (LIVE or CACHED) and data age in minutes. Cached data older than 6 hours is treated as stale and is flagged in the recommendation rationale for the reviewing agronomist. The prototype simulates the external API and cache with in-memory data.

## Consequences
Recommendations remain available during weather API outages, and recording data age makes stale inputs visible to agronomists. Caching also reduces external API calls and cost. However, cached forecasts may miss sudden weather changes, freshness thresholds may need tuning by region, and the cache adds state to manage when scaling.

## Compliance
Tests simulate weather API failure and confirm recommendations still return 201 with weatherSource CACHED and the correct data age. Reviews confirm all weather access uses the Weather Integration component.
