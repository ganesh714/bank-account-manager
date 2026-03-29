# Bank Account Manager API

A bank account management system built with **Event Sourcing** and **CQRS** patterns using **Axon Framework** and **PostgreSQL**.

## Project Motivation

This project is designed to build an auditable, resilient, and scalable banking system. It demonstrates how to model business logic using aggregates, commands, and events; implement CQRS with optimized read projections; and perform temporal queries to reconstruct states at any point in history.

## Documentation

The project documentation is organized into modular sections for better clarity and maintainability.

- [**Main Documentation Index**](docs/README.md)
- [Architecture Overview](docs/architecture.md)
- [Core API Reference](docs/core-api-reference.md)
- [**Query Side (Read Model)**](./docs/query-side.md): Learn about projections, view models, and the Query REST API.
- [**Temporal Queries**](./docs/temporal-queries.md): How to reconstruct account balance at any specific point in history.
- [**Infrastructure & Advanced**](./docs/infrastructure-advanced.md): Guide on Snapshotting and Event Replay (Read Model Rebuilding).
- [**Exceptions & Mapping**](./docs/exceptions-package.md): Documentation on the system's error-handling strategy (404/409).
- [**API Specification**](./docs/api-specification.md): Technical details of all REST endpoints.
- [**Configuration & Setup**](./docs/configuration-setup.md): Deployment and setup guide.
- [Implementation Details](docs/implementation-details.md)
- [**Current Progress Report**](docs/progress-report.md)

## Quick Start

1.  **Clone the Repository**:
    ```bash
    git clone https://github.com/ganesh714/bank-account-manager.git
    cd bank-account-manager
    ```
2.  **Environment Setup**:
    Copy `.env.example` to `.env` and configure accordingly.
3.  **Start Database**:
    ```bash
    docker-compose up -d
    ```
4.  **Run Application**:
    ```bash
    ./mvnw spring-boot:run
    ```

For detailed instructions, refer to the [Setup Guide](docs/configuration-setup.md).

---
*Created by Antigravity AI*
