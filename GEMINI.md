# Gemini Project Overview: helloworld-commerce

This document provides a comprehensive overview of the `helloworld-commerce` project, designed to give future AI agents the context needed to understand and interact with this codebase.

## Project Purpose and Architecture

`helloworld-commerce` is a modular monolith e-commerce application built with Kotlin and Spring Boot. It follows clean architecture principles and utilizes a Gradle Composite Build pattern to enforce strong module boundaries.

- **Architecture**: Modular Monolith (Modulith)
- **Design Pattern**: Domain-Driven Design (DDD) with jMolecules, Hexagonal Architecture (Ports and Adapters)
- **Build System**: Gradle with Kotlin DSL and Composite Builds

The project is divided into distinct layers:

-   **`domain`**: Contains the core business logic and domain models, with no external dependencies. It's an independent build.
-   **`data`**: The persistence layer, responsible for database interactions. It's a composite build with submodules for `mysql` and `redis`.
-   **`application`**: The application service layer, containing use cases and orchestrating the domain. It is an independent build and depends on `domain` and `data/mysql`.
-   **`app`**: The entry-point layer, containing the API and background workers. It's a composite build with `api` and `worker` submodules.
-   **`build-logic`**: Houses Gradle convention plugins to share build configurations across all modules.

## Tech Stack

-   **Language**: Kotlin
-   **Framework**: Spring Boot
-   **Database**: MySQL
-   **Cache**: Redis
-   **Messaging**: Kafka
-   **ORM**: Spring Data JPA
-   **Database Migration**: Flyway
-   **Testing**: JUnit 5, Kotest, Testcontainers
-   **DDD**: jMolecules

## Key Commands

### Building the Project

-   **Build the entire project (all modules):**
    ```bash
    ./gradlew build
    ```
-   **Build the API application:**
    ```bash
    ./gradlew :app:api:build
    ```
-   **Build the Worker application:**
    ```bash
    ./gradlew :app:worker:build
    ```

### Running the Application

1.  **Start external services (MySQL, Redis, Kafka):**
    ```bash
    docker-compose up -d
    ```

2.  **Run the applications:**

    -   **Run the API application:**
        ```bash
        ./gradlew :app:api:bootRun
        ```
        The API will be available at `http://localhost:8080`.

    -   **Run the Worker application:**
        ```bash
        ./gradlew :app:worker:bootRun
        ```

### Running Tests

-   **Run all checks (unit and integration tests):**
    ```bash
    ./gradlew check
    ```
-   **Run unit tests for a specific module:**
    ```bash
    ./gradlew :app:api:test
    ```
-   **Run integration tests for a specific module:**
    ```bash
    ./gradlew :data:mysql:intTest
    ```

## Development Conventions

-   **Modular Development**: Code is organized into modules with clear responsibilities. The Gradle Composite Build helps enforce these boundaries.
-   **Convention Plugins**: Shared build logic is managed through convention plugins in the `build-logic` directory. This promotes consistency across modules.
-   **Versioning**: All dependency versions are centrally managed in `gradle/libs.versions.toml`.
-   **Testing**: The project has a strong emphasis on testing, with both unit and integration tests. Integration tests use Testcontainers to create a realistic test environment.
-   **Event-Driven**: Spring Modulith is used for event-driven communication between modules, ensuring loose coupling.
-   **Database Migrations**: Database schema changes are managed by Flyway. Migration scripts are located in the `data/mysql/src/main/resources/db/migration` directory.
