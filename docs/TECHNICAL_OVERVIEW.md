# Technical Overview

## 1. Deep Architecture Explanation

### End-to-End Flow
1. Browser dashboard sends JSON requests to `ApiServer`.
2. `ApiServer` validates method, parses payload, routes to `SalesService`.
3. `SalesService` computes quote/sale using pricing rules and stock checks.
4. Initial data is loaded at startup from Prolog facts (`store.pl`) by `PrologDataLoader`.
5. Metrics and sales history are exposed through API endpoints for observability.

### Technical Decisions
- **JDK HttpServer** instead of heavyweight frameworks to keep runtime lean and dependency-light.
- **Prolog facts** as deterministic rule source to preserve original academic concept.
- **In-memory service state** for fast execution and easy demo portability.
- **Vanilla frontend** for zero-build deployment simplicity.

### Trade-offs
- In-memory state is fast but not durable across restarts.
- Regex payload parsing is lightweight but less robust than full JSON parsing.
- No database means easy setup but limited production persistence.

### Alternatives Considered
- Spring Boot for richer ecosystem support (rejected for overhead in this stage).
- PostgreSQL persistence for sales ledger (planned future upgrade).
- Message queue for async processing (not necessary at current scale).

## 2. Junior Interview Questions
- What role does Prolog play in this system?
- Why did you separate `api`, `core`, `prolog`, and `util`?
- How does quote generation differ from sale registration?
- How do you ensure inventory does not go negative?
- Why use environment variables in this repository?

## 3. Senior Interview Questions
- How would you scale this architecture to multi-instance deployment?
- How would you introduce durable state and transactional guarantees?
- How would you secure public-facing endpoints?
- How would you design blue/green deployment for this service?
- How would you model observability SLOs for quote latency and error rate?

## 4. Critical Code Sections

### `SalesService.calculateQuote(...)`
- **What it does:** Validates items and stock, computes subtotal, category discount, loyalty discount, shipping, and final total.
- **Why important:** Core business logic correctness directly impacts financial output.
- **Interview angle:** Numeric precision, edge-case validation, and idempotency of quote operations.

### `SalesService.registerSale(...)`
- **What it does:** Reuses quote logic with stock mutation, records sale, updates metrics.
- **Why important:** Converts simulation into committed business transaction.
- **Interview angle:** Transaction boundaries and consistency guarantees when persistence is introduced.

### `ApiServer` routing
- **What it does:** Exposes health, inventory, metrics, quote, sale, and reset endpoints.
- **Why important:** Defines service contract for dashboard and external clients.
- **Interview angle:** API contract stability, versioning, and error-handling strategy.

### `PrologDataLoader`
- **What it does:** Parses Prolog facts into strongly typed Java structures.
- **Why important:** Bridges declarative knowledge base into executable service layer.
- **Interview angle:** Parser resilience, schema evolution, and startup validation behavior.

### `web/app.js`
- **What it does:** Orchestrates UI state, cart behavior, API integration, and periodic refresh.
- **Why important:** Demonstrates full-stack operational visibility using the same backend contract.
- **Interview angle:** State synchronization and defensive UX when requests fail.

## 5. Scaling Discussion
- Introduce PostgreSQL for persistent sales and stock ledger.
- Add Redis for caching product catalog and metrics snapshots.
- Add event-driven processing for asynchronous order workflows.
- Deploy multiple stateless API replicas behind a load balancer.

## 6. Memory Management Discussion
- Current Java memory profile is bounded by in-memory sales history and inventory maps.
- For long-lived production instances:
  - limit retained history in memory,
  - persist archive records,
  - expose JVM memory metrics to monitoring.

## 7. Concurrency Discussion
- `SalesService` methods are `synchronized`, providing coarse-grained safety.
- At higher throughput, migrate to:
  - fine-grained locking or lock-free counters,
  - persistence-backed transactions,
  - optimistic concurrency for stock updates.
