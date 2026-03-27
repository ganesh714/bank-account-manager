# Configuration & Setup

This document provides instructions on how the Bank Account Manager application is initialized, configured, and run.

## 1. Project Initialization & Tooling
* **Base Setup:** The application was bootstrapped using **Spring Initializr** with a baseline Spring Boot 3 configuration.
* **Standard Dependencies:** The following starters are used:
    - `spring-boot-starter-web` for REST API functionality.
    - `spring-boot-starter-data-jpa` for database persistence.
    - `postgresql` JDBC driver.
    - `spring-boot-starter-validation` for command-level constraints.
* **Axon Integration:** Since Axon is not part of the default Initializr, the `axon-spring-boot-starter` (version 4.9.3) was manually added to the `pom.xml`.

## 2. Infrastructure & Infrastructure
* **Docker Containerization:** A `docker-compose.yml` file defines a **PostgreSQL 15** service with built-in health checks (`pg_isready`) to ensure the database is ready before the application attempts to connect.
* **Environment Variables:** All sensitive or environment-specific configuration is managed via a `.env.example` file, documenting keys for `DB_HOST`, `DB_NAME`, `DB_USER`, `DB_PASSWORD`, and `SERVER_PORT`.

## 3. Application Configuration
Specifically configured in `src/main/resources/application.properties`:

* **Axon JPA Event Store:** Axon Server is explicitly **disabled** (`axon.axonserver.enabled=false`). This forces the Axon Framework to use the JPA datasource and automatically generate event store tables like `domain_event_entry`, `snapshot_event_entry`, and `token_entry` within PostgreSQL.
* **Hibernate/JPA Properties:** 
    - The PostgreSQL dialect is configured for optimal performance.
    - `spring.jpa.show-sql=true` is enabled to allow developers to monitor the database interaction between Axon, JPA, and PostgreSQL.

## 4. Running the Application
1. **Ensure PostgreSQL is Up**: `docker-compose up -d`
2. **Run Spring Boot**: `./mvnw spring-boot:run`
