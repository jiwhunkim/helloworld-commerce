# Repository Guidelines

## Project Structure & Module Organization
Kotlin/Spring Boot modular monolith using Gradle Composite Build pattern.

Key modules:
- `domain/`: Pure business logic, domain models, events (no Spring/JPA). Uses jMolecules DDD annotations.
- `data/mysql/`: JPA entities, repositories, Flyway migrations. Converts between domain models and JPA entities.
- `data/redis/`: Redis caching and session configuration.
- `application/`: Use cases, application services, hexagonal ports (input/output). Orchestrates domain/data layers with Spring Modulith.
- `app/api/`: REST controllers, HTTP files, API entry point. Only module creating executable JAR for API.
- `app/worker/`: Kafka consumers, event listeners. Worker entry point for async processing.
- `build-logic/`: Gradle convention plugins (kotlin, spring-boot, test, etc.).

Dependency flow: `domain ← data/mysql ← application ← app/api|worker`. Never reverse dependencies.

## Build, Test, and Development Commands

### Build Commands
Use module-qualified Gradle tasks (composite build pattern):
- `./gradlew :app:api:build` - Build the API module
- `./gradlew :app:worker:build` - Build the worker module
- `./gradlew :domain:build` - Build domain module
- `./gradlew :data:mysql:build` - Build MySQL data module
- `./gradlew :application:build` - Build application module
- `./gradlew :app:api:bootRun` - Run API service locally (starts on http://localhost:8080)
- `./gradlew :app:api:clean` - Clean API module (no root-level `clean` task)

### Test Commands
- `./gradlew :app:api:test` - Run unit tests for API module
- `./gradlew :data:mysql:test` - Run unit tests for MySQL module
- `./gradlew :data:mysql:intTest` - Run integration tests (uses Testcontainers)
- `./gradlew :application:test` - Run application layer tests
- `./gradlew check` - Run all tests including integration tests

### Running a Single Test
- `./gradlew :module:path:test --tests "ClassName"` - Run all tests in a class
- `./gradlew :module:path:test --tests "ClassName.methodName"` - Run specific test method

Examples:
- `./gradlew :app:api:test --tests "OrderControllerTest"`
- `./gradlew :data:mysql:intTest --tests "ApplicationIntegrationTests.bootstrapsApplication"`

### Infrastructure
- `docker-compose up -d` - Start MySQL and Redis for local dev
- `docker-compose up -d mysql` - Start only MySQL
- `docker-compose up -d redis` - Start only Redis

## Code Style & Conventions

### Language & Formatting
- Primary language: Kotlin 2.3.0 (use Kotlin DSL for Gradle)
- Indentation: 4 spaces (defined in .editorconfig)
- No tabs, LF line endings, UTF-8 encoding, trim trailing whitespace
- Java toolchain: 25

### Package Structure
- Base package: `com.helloworld.commerce`
- Domain models: `com.helloworld.commerce.{module}.domain`
- JPA entities: `com.helloworld.commerce.{module}.adapter.output.mysql`
- Repositories: `com.helloworld.commerce.{module}.adapter.output.mysql`
- Persistence adapters: `com.helloworld.commerce.{module}.adapter.output.mysql`
- Input ports (use cases): `com.helloworld.commerce.{module}.application.port.input`
- Output ports: `com.helloworld.commerce.{module}.application.port.output`
- Services: `com.helloworld.commerce.{module}.application.service`
- Controllers: `com.helloworld.commerce.{module}.api`
- Consumers: `com.helloworld.commerce.{module}.worker`

### Naming Conventions
- Domain events: `OrderComplete` (data class in domain package)
- JPA entities: `OrderJpaEntity`
- Repositories: `OrderJpaRepository` (extends JpaRepository)
- Persistence adapters: `OrderPersistenceAdapter` (implements secondary ports)
- Primary ports (use cases): `CompleteOrderUseCase` (interface annotated with @PrimaryPort)
- Secondary ports: `LoadOrderPort` (interface annotated with @SecondaryPort)
- Services: `OrderService` (annotated with @Service)
- Controllers: `OrderController` (annotated with @RestController)
- Consumers: `OrderConsumer` (annotated with @Service)

### Imports
- Import jakarta.persistence.* for JPA annotations (not javax)
- Import org.jmolecules.* for DDD patterns (architecture, events, hexagonal)
- Import io.github.oshai.kotlinlogging.KotlinLogging for logging
- Import org.springframework.modulith.events.ApplicationModuleListener for event listeners

### Type System & Properties
- Domain models: immutable `data class` with validation in `init` blocks
- JPA entities: class with `val` for immutable properties, `var` for mutable ones
- Use `val` over `var` by default; `var` only when needed
- Use constructor injection: `class OrderService(val events: ApplicationEventPublisher)`
- Function return types: omit `: Unit` explicitly for void functions

### Architecture Patterns
- Hexagonal architecture with jMolecules:
  - Primary ports: `@PrimaryPort` interfaces for use cases
  - Secondary ports: `@SecondaryPort` interfaces for data access
  - Primary adapters: Controllers implementing REST
  - Secondary adapters: Persistence adapters implementing secondary ports
- Event-driven: Spring Modulith with `@ApplicationModuleListener`
- Event externalization: `@Externalized("order-event::#{#this.getId()}")` on domain events
- Transaction boundaries: `@Transactional` on service methods

### Error Handling
- Use Kotlin's null safety features
- Throw meaningful exceptions with descriptive messages
- Consider custom domain exceptions for business rules
- Avoid swallowing exceptions; let them propagate to appropriate handlers

### Logging
- Use KotlinLogging: `val log = KotlinLogging.logger {}`
- Log at appropriate levels: `log.info { "message" }`, `log.error { "message" }`, etc.
- Use structured logging with curly braces for lazy evaluation

### Testing
- Test framework: JUnit 5 + Kotest 6.0.2
- Integration tests: Testcontainers for MySQL, Spring Boot Test
- Use DescribeSpec style for tests: `class SomeTest : DescribeSpec { init { it("should do something") { } } }`
- Name tests descriptively after the class under test: `ProductControllerTest`, `OrderServiceTest`
- Use `@ActiveProfiles("test")` for integration tests
- Use `@SpringBootTest` with `@Import(TestcontainersConfiguration::class)` for full context

### Database Migrations
- Use Flyway for schema migrations
- Root migrations in `db/migration/__root/` (e.g., event_publication table)
- Module-specific migrations in `db/migration/{module}/` (e.g., order, product)
- Use `CREATE TABLE IF NOT EXISTS` to avoid conflicts
- Migration naming: `V1__init.sql`, `V2__add_column.sql`, etc.

### API Documentation
- HTTP request files in `app/api/http/` directory
- Example: `order.http` with `GET localhost:8080/orders/completed`
- Document new endpoints in both HTTP files and this AGENTS.md

### Configuration
- MySQL: localhost:3306, database: helloworld-commerce, user: root, password: mysql
- Redis: localhost:6379
- Update both `docker-compose.yml` and `application.yml` when changing credentials
- Use Spring profiles: `application.yml` includes `application-mysql.properties`

### Commit Messages
- Keep it short and descriptive (e.g., "init commit", "add order module")
- Focus on "why" not "what"
- Conventional commits are encouraged: `feat:`, `fix:`, `refactor:`, `test:`, etc.

### Dependencies
- Spring Boot 4.0.1
- Spring Modulith for event-driven architecture
- jMolecules 2025.0.2 for DDD patterns
- Kotlin Logging 7.0.13
- All versions managed in `gradle/libs.versions.toml`
- Use bundles from version catalog: `libs.bundles.kotest`, `libs.bundles.jmolecules`
