# Architecture Overview

This project is built around two key architectural patterns: **Event Sourcing** and **Command Query Responsibility Segregation (CQRS)**.

## Architectural Concepts

### 1. CQRS Pattern (Command Query Responsibility Segregation)
The system is explicitly split into two models:
- **Write Model (Command side)**: Responsible for enforcing business logic and ensuring data consistency.
- **Read Model (Query/Projection side)**: Optimized for data retrieval and serving diverse query requirements.

### 2. The Aggregate (Write Model)
The `BankAccount` is defined as an **Aggregate** in Domain-Driven Design (DDD). It acts as the strict gatekeeper of the system’s state, enforcing business rules and validation before emitting any events into the event store.

### 3. Project Structure
The code is logically divided to maintain a clean separation of concerns:
- **`coreapi`**: Contains shared messaging components like commands and events.
- **`command`**: Houses the aggregate implementation and command-handling controllers.
- **`query`**: Contains projections, read-side models, and query-optimized controllers.

## Core API (Messaging Foundation)

The communication between the Command and Query models is handled via asynchronous messages. For technical details on each message and its annotations, see the [Core API Reference](core-api-reference.md).

- **Commands (Intent-based)**:
  - Examples: `CreateAccountCommand`, `DepositMoneyCommand`.
  - Implemented as Java **`record`s** for immutability.
  - Utilize `@TargetAggregateIdentifier` for correct routing in Axon.
- **Events (Fact-based)**:
  - Examples: `AccountCreatedEvent`, `MoneyDepositedEvent`.
  - Also implemented as Java **`record`s**, representing immutable historical facts that have already occurred.

## Technology Stack

- **Java 17 (Records used)**
- **Spring Boot 3**
- **Axon Framework 4.9.3**
- **PostgreSQL 15**
