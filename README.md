# Internal Developer Platform (IDP)

A microservices-based Internal Developer Platform that enables engineering teams to scaffold new services from templates, manage GitHub repositories, track deployments to Kubernetes, and maintain a full audit trail of platform activity.

---

## Overview

The platform is composed of independently deployable Spring Boot services, all fronted by a single API Gateway. Each service owns its own database schema (shared PostgreSQL instance) and enforces authentication via a shared JWT secret. The Template Service is the primary self-service entry point: a developer submits a request, the platform scaffolds a Spring Boot project, creates a GitHub repository, pushes the initial code, and records the event.

---

## Architecture

```
Client
  |
  v
API Gateway (:8080)
  |
  +-- /api/auth/**        --> user-service (:8081)
  +-- /api/users/**       --> user-service (:8081)
  +-- /api/services/**    --> catalog-service (:8082)
  +-- /api/deployments/** --> deployment-service (:8083)
  +-- /api/audit/**       --> audit-service (:8084)
  +-- /api/environments/**--> environment-service (:8085)
  +-- /api/releases/**    --> release-service (:8086)
  +-- /api/templates/**   --> template-service (:8088)

github-service (:8087) -- internal only, not exposed via gateway
```

All services connect to a single PostgreSQL instance. The gateway is reactive (Spring WebFlux + Spring Cloud Gateway). All downstream services are servlet-based (Spring MVC).

---

## Services

### api-gateway
**Port:** 8080

Routes all inbound traffic to the appropriate downstream service. Applies path rewriting so that `/api/users/**` is forwarded as `/users/**` to the user-service, and so on. Built with Spring Cloud Gateway on WebFlux. Actuator endpoints are fully exposed for health and metrics.

---

### user-service
**Port:** 8081

Handles user registration, authentication, and role management. On startup, seeds a default `admin` user (username: `admin`, password: `admin123`). Issues signed JWTs on successful login; the same secret is shared across all services for token verification.

Roles: `ADMIN`, `DEVELOPER`

Endpoints (internal paths, pre-gateway rewrite):

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /auth/login | Public | Returns a JWT |
| POST | /users/register | Public | Registers a new user with DEVELOPER role |
| GET | /users | ADMIN | Lists all users |
| GET | /users/{id} | Authenticated | Fetches a user by ID |
| GET | /users/username/{username} | Public | Fetches a user by username |
| GET | /health | Public | Liveness check |

---

### catalog-service
**Port:** 8082

Maintains a registry of services known to the platform. When a new service is scaffolded via the Template Service, it is registered here. Stores service name, owner, description, repository name, repository URL, and git provider.

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /services | Authenticated | Registers a new service |
| GET | /services | Authenticated | Lists all services |
| GET | /services/{id} | Authenticated | Fetches a service by ID |
| GET | /health | Public | Liveness check |

---

### deployment-service
**Port:** 8083

Manages deployments of catalog services to Kubernetes environments. On receiving a deployment request it validates that the service exists in the catalog, uses the Fabric8 Kubernetes client to create or replace a Deployment in the target namespace, records the deployment to its own database, and emits an audit event to the audit-service. Supports rollback by looking up the previous deployment for a given service and environment.

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /deployments | Authenticated | Triggers a new deployment |
| GET | /deployments | Authenticated | Lists all deployments |
| GET | /deployments/{id} | Authenticated | Fetches a deployment by ID |
| POST | /deployments/{id}/rollback | Authenticated | Rolls back to the prior deployment |
| GET | /health | Public | Liveness check |

Request body for POST /deployments:
```json
{
  "serviceId": 1,
  "environment": "production",
  "version": "1.0.0",
  "image": "my-service:1.0.0"
}
```

---

### audit-service
**Port:** 8084

Append-only log of platform events. Other services (notably deployment-service) post events here directly over HTTP. Reading the audit log requires ADMIN or DEVELOPER role. Writing is open (the `/audit` POST endpoint is permit-all) so that internal services can emit events without carrying a user token.

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /audit | Public | Records an audit event |
| GET | /audit | ADMIN or DEVELOPER | Returns all audit events |
| GET | /health | Public | Liveness check |

---

### environment-service
**Port:** 8085

Manages deployment targets (clusters, namespaces, regions). Environments are referenced by name when creating deployments.

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /environments | Authenticated | Creates an environment |
| GET | /environments | Authenticated | Lists all environments |
| GET | /environments/{id} | Authenticated | Fetches an environment by ID |
| GET | /health | Public | Liveness check |

Request body for POST /environments:
```json
{
  "name": "production",
  "cluster": "my-cluster",
  "namespace": "prod",
  "region": "ap-south-1"
}
```

---

### github-service
**Port:** 8087 (internal, not routed through the gateway)

Wraps the GitHub API. Creates repositories on behalf of the platform using a personal access token. Stores repository metadata (name, URL, owner, default branch) in its own table. Called by the Template Service when scaffolding a new project.

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /github/repositories | Internal | Creates a GitHub repository |
| GET | /health | Public | Liveness check |

Required environment variables: `GITHUB_TOKEN`, `SPRING_DATASOURCE_URL`, `SPRING_DATASOURCE_USERNAME`, `SPRING_DATASOURCE_PASSWORD`

---

### template-service
**Port:** 8088

The primary self-service endpoint for developers. Accepts a scaffold request, generates a Spring Boot project on disk under `generated/<service-name>/`, calls github-service to create a repository, and pushes the generated code via `git`. Returns the service name, status, local path, and repository URL.

Generated project structure includes: `build.gradle`, `settings.gradle`, `Dockerfile`, Kubernetes manifests (`k8s/deployment.yaml`, `k8s/service.yaml`), a GitHub Actions workflow, and standard Spring Boot package layout (controller, service, entity, repository, dto).

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /templates/java-service | Public | Scaffolds a new Spring Boot service |
| GET | /health | Public | Liveness check |

Request body:
```json
{
  "serviceName": "inventory-service",
  "groupId": "com.company",
  "artifactId": "inventory-service",
  "database": "postgres",
  "packageName": "com.company"
}
```

Required environment variables: `GITHUB_TOKEN`, `GITHUB_OWNER`

---

### release-service
**Port:** 8086

Tracks releases by associating a service, a deployment, a version, an environment, and a status. Intended as a lightweight release record after a deployment has been promoted.

Endpoints:

| Method | Path | Auth | Description |
|--------|------|------|-------------|
| POST | /releases | Authenticated | Creates a release record |
| GET | /releases | Authenticated | Lists all releases |
| GET | /releases/{id} | Authenticated | Fetches a release by ID |
| GET | /health | Public | Liveness check |

---

## Technology Stack

| Concern | Choice |
|---------|--------|
| Language | Java 21 |
| Framework | Spring Boot 3.5.x (MVC) / 4.0.x (user-service) |
| Gateway | Spring Cloud Gateway 2025.0.0 (WebFlux) |
| Security | Spring Security, JWT (jjwt 0.12.x) |
| Persistence | Spring Data JPA, PostgreSQL |
| Kubernetes client | Fabric8 Kubernetes Client 6.13.4 |
| Build tool | Gradle 9.5.1 (Groovy DSL) |
| Containerization | Docker, Docker Compose |
| Boilerplate reduction | Lombok |

---

## Prerequisites

- Docker and Docker Compose
- Java 21 (for local development without Docker)
- A GitHub personal access token with `repo` scope (for github-service and template-service)
- A Kubernetes cluster accessible from the deployment-service container (kubeconfig or in-cluster config)

---

## Running with Docker Compose

All services and a PostgreSQL instance are defined in `docker-compose.yml` at the project root.

```bash
# Set required secrets before starting
export GITHUB_TOKEN=ghp_your_token_here
export GITHUB_OWNER=your_github_username

docker compose up --build
```

The API Gateway will be available at `http://localhost:8080`.

---

## Local Development

Build all modules from the project root:

```bash
./gradlew build
```

Build a specific module:

```bash
./gradlew :user-service:build
```

Run a specific service (requires a running PostgreSQL instance):

```bash
./gradlew :user-service:bootRun
```

---

## Authentication

All protected endpoints require a Bearer token in the `Authorization` header.

1. Obtain a token:

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username": "admin", "password": "admin123"}'
```

Response:
```json
{ "token": "eyJ..." }
```

2. Use the token on subsequent requests:

```bash
curl http://localhost:8080/api/services \
  -H "Authorization: Bearer eyJ..."
```

The default admin credentials (`admin` / `admin123`) are seeded by the user-service on first startup if the user does not already exist.

---

## Service Port Reference

| Service | Internal Port |
|---------|--------------|
| api-gateway | 8080 |
| user-service | 8081 |
| catalog-service | 8082 |
| deployment-service | 8083 |
| audit-service | 8084 |
| environment-service | 8085 |
| release-service | 8086 |
| github-service | 8087 |
| template-service | 8088 |

---

## Project Structure

```
idp/
├── api-gateway/
├── audit-service/
├── catalog-service/
├── deployment-service/
├── environment-service/
├── github-service/
├── release-service/
├── template-service/
├── user-service/
├── common-lib/              # Shared library (reserved)
├── generated/               # Output directory for scaffolded services
├── docker-compose.yml
├── settings.gradle
└── build.gradle
```

Each service follows the same internal layout:

```
<service>/
├── build.gradle
├── Dockerfile
└── src/main/
    ├── java/com/idp/<service>/
    │   ├── config/          # Security, Kubernetes, WebClient config
    │   ├── controller/      # REST controllers
    │   ├── dto/             # Request and response records
    │   ├── entity/          # JPA entities
    │   ├── repository/      # Spring Data repositories
    │   ├── security/        # JwtAuthenticationFilter, JwtService
    │   └── service/         # Business logic
    └── resources/
        └── application.properties
```

---

## Security Notes

The JWT signing secret (`mySuperSecretKeyForJwtGeneration12345678901234567890`) is currently hardcoded in each service's `JwtService`. Before deploying to any non-local environment this must be replaced with a secret injected via environment variable or a secrets manager. The same applies to the default admin password and any database credentials embedded in `application.properties`.

---

## Generated Services

The `generated/` directory contains the output of previous template scaffold runs (inventory-service-v10 through v23). These are working Spring Boot projects and can be built and deployed independently using their own `build.gradle` and Kubernetes manifests.
