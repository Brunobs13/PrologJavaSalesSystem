# Prolog Java Sales Platform

Production-structured modernization of an academic Prolog + Java sales project into a commercial web application with MVC-style separation, API endpoints, automation scripts, and deployment-ready tooling.

## Project Overview
The project delivers a lightweight commerce platform where pricing rules are sourced from Prolog facts and business operations are exposed through a Java API and a commercial web dashboard.

Core outcomes:
- Professional repository structure
- Secure configuration strategy (environment-based)
- Reproducible local and containerized execution
- Interview-ready architecture and documentation

## Business Problem
Traditional small/medium commerce operations struggle to unify:
- Pricing and discount policy governance
- Manual quote generation
- Inventory-sensitive order registration
- Operational visibility in one dashboard

This platform addresses that by centralizing rules, automating quote/sale flows, and exposing live sales telemetry.

## Architecture Diagram
```text
[Web Dashboard (View)]
        |
        | HTTP/JSON
        v
[ApiServer (Controller Layer)]
        |
        | method calls
        v
[SalesService (Domain/Application Layer)]
        |
        | read startup facts
        v
[PrologDataLoader + store.pl (Knowledge Layer)]

Support Layers:
- JsonUtil / RequestParsers / StaticAssets
- AppLogger (structured logs)
- Scripts + Makefile + CI + Docker
```

MVC mapping:
- Model: `core/` domain records and `SalesService`
- View: `web/` dashboard
- Controller: `api/ApiServer`

## Tech Stack
- Java 21
- Prolog fact source (`store.pl`)
- JDK HttpServer (`com.sun.net.httpserver`)
- Vanilla HTML/CSS/JavaScript dashboard
- GitHub Actions CI
- Docker + Docker Compose

## Project Structure
```text
.
├── src/
│   ├── main/java/com/bruno/salessystem/
│   │   ├── api/
│   │   ├── core/
│   │   ├── prolog/
│   │   └── util/
│   └── main/resources/prolog/store.pl
├── web/
├── tests/java/
├── docs/
├── configs/
├── scripts/
├── legacy/academic_project/
├── Dockerfile
├── docker-compose.yml
├── Makefile
└── .env.example
```

## Setup Instructions
### 1. Prerequisites
- Java 21+
- Bash shell
- Optional: Docker Desktop

### 2. Configure environment
```bash
cp .env.example .env
```

### 3. Build
```bash
./scripts/build.sh
```

### 4. Run API + website
```bash
./scripts/run_server.sh
```
Open: `http://localhost:8080`

### 5. Run tests
```bash
./scripts/test.sh
```

## API Endpoints
- `GET /health`
- `GET /api/customers`
- `GET /api/items`
- `GET /api/products`
- `GET /api/sales`
- `GET /api/metrics`
- `GET /api/dashboard`
- `POST /api/sales/quote`
- `POST /api/sales/register`
- `POST /api/reset`

Example payload for quote/register:
```json
{
  "customerId": 1,
  "items": [
    { "itemId": 101, "quantity": 1 },
    { "itemId": 302, "quantity": 3 }
  ]
}
```

## CI/CD Overview
CI workflow (`.github/workflows/ci.yml`) runs on push/PR to `main`:
- Source checkout
- Java setup (Temurin 21)
- Build step (`./scripts/build.sh`)
- Test step (`./scripts/test.sh`)

CD recommendation:
- Keep release tags (`vX.Y.Z`)
- Auto-build Docker image on tags
- Deploy to a cloud container service (Render, Fly.io, AWS App Runner)

## Data Versioning Strategy
Current project data is deterministic and stored in `store.pl`.
Recommended strategy for growth:
- Keep baseline facts in Git for reproducible demos
- Use DVC for large evolving datasets
- Tag data versions by release (`data-v1`, `data-v2`)

## Model Tracking Strategy
This repository is rules-based (non-ML), so model tracking is not currently applicable.
If upgraded to ML scoring:
- Track experiments with MLflow
- Version features/data with DVC
- Register model promotion criteria in CI gates

## Deployment Strategy
### Local
- Script-based startup (`run_server.sh`)

### Containerized
```bash
docker compose up --build
```
Service exposed at `http://localhost:8080`.

### Public portfolio deployment
- Build image in CI
- Push image to a registry
- Deploy as a single-container web service

## Security Considerations
- No hardcoded credentials in runtime code
- `.env` ignored by Git
- Path traversal protection in static file serving
- Explicit input validation for cart payloads
- Structured logs to support incident analysis

## Lessons Learned
- Academic prototypes require strict separation of responsibilities to scale
- Deterministic rule engines can still provide modern API and dashboard UX
- Production readiness depends as much on repo hygiene as on source code

## Future Improvements
- Replace regex payload parsing with a typed JSON parser
- Add authentication and role-based access control
- Introduce persistent storage for sales history
- Add OpenAPI specification and contract tests
- Add cloud deployment workflow with release environments

## Documentation Index
- Audit report: [`docs/repository_audit.md`](docs/repository_audit.md)
- Technical interview guide: [`docs/TECHNICAL_OVERVIEW.md`](docs/TECHNICAL_OVERVIEW.md)
- Portfolio material: [`docs/portfolio_ready.md`](docs/portfolio_ready.md)
