# ADR-03: Security and Access Control

## Status
Accepted

## Context
OzAgriMate manages sensitive farm, user and operational data accessed by farmers, agronomists and platform administrators. ASR19–ASR22 require secure authentication, encryption, role-based access restrictions and audit logging. ASR26 requires administrators to manage IoT devices. Unauthorised access could expose confidential information or allow improper device modifications. Shared API keys offer simple access control but lack individual user accountability and fine-grained permissions. Centralised session-based authentication is another option but introduces shared session dependencies across services. Signed access tokens combined with role-based and farm-scoped authorisation provide a more suitable approach for the proposed distributed architecture.

## Decision
OzAgriMate will adopt signed token-based authentication with role-based access control (RBAC) and farm-scoped authorisation. Each service will validate authentication tokens and verify permissions before processing sensitive requests. Only authorised platform administrators may register or remove IoT devices. HTTPS with TLS will protect transmitted data, while encryption at rest will safeguard sensitive stored information. Audit logging will record security-relevant activities, including access attempts and device modifications. This approach supports consistent security across independently deployable services while limiting unauthorised cross-farm access. These controls form part of the proposed production architecture rather than the limited in-memory prototype.

## Consequences
This approach strengthens data protection, supports individual accountability and enforces consistent access restrictions across farms. Token-based authentication reduces dependence on centralised sessions. However, token management, key rotation and permission configuration increase operational complexity. Compromised tokens remain a security risk, requiring expiration and revocation mechanisms. Audit logs also require secure storage and appropriate retention policies.

## Compliance
Security tests will verify token validation, role permissions, cross-farm access restrictions and audit logging. Architecture reviews will assess TLS, encryption at rest and token management configurations before production deployment.
