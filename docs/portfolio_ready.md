# Portfolio-Ready Pack

## LinkedIn Version (Short)
Built a production-structured **Prolog + Java Commercial Sales Platform** with MVC architecture, REST API, and interactive dashboard. Implemented quote/sale flows with discount intelligence, inventory controls, observability metrics, Docker support, CI pipeline, and professional documentation.

## CV Version (Technical)
Modernized an academic Prolog-Java sales project into a production-ready web platform. Designed layered architecture (`api/core/prolog/util`), implemented deterministic pricing engine with stock validation, exposed REST endpoints, and delivered a commercial dashboard with live metrics and operational controls. Added automation scripts, CI (GitHub Actions), Dockerized deployment, secure environment configuration, and interview-focused technical documentation.

## 60-Second Pitch
I transformed a legacy Prolog and Java academic solution into a production-grade commercial sales platform. The system now has a clean MVC-style architecture, a Java API, and a dashboard where users can generate quotes, register sales, track inventory, and monitor business metrics in real time. I introduced secure environment-based configuration, CI automation, containerization, and a full technical documentation package. The result is portfolio-ready, demo-friendly, and aligned with engineering standards used in real companies.

## 5-Minute Technical Pitch
This project started as a monolithic academic implementation with mixed source and binary artifacts. I restructured it into a professional repository with clear boundaries: API routing in `api`, business logic in `core`, Prolog parsing in `prolog`, and support utilities in `util`.

At startup, the API loads deterministic business rules and baseline data from Prolog facts. The central service computes quotes by combining item totals, category discounts, loyalty discounts, and shipping costs. Sale registration reuses quote logic, mutates stock safely, and records telemetry such as revenue, conversion rate, and discount totals.

On top of this, I built a commercial web UI that acts as the View layer: catalog browsing, cart management, quote generation, sale registration, and live KPI cards. It polls dashboard telemetry and keeps operators aware of stock health and commercial performance.

For operational maturity, I added scripts, tests, CI workflow, Docker support, `.env` management, and structured logging. I also documented architecture trade-offs: lightweight runtime and deterministic rules now, with clear extension points for persistence, auth, and horizontal scale. This turns the project from coursework into a credible production case for backend and systems interviews.
