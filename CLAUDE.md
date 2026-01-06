# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

This is a **modular monolith** (modulith) e-commerce application built with Kotlin and Spring Boot 3.2.0, following clean architecture principles with strict layer separation using **Gradle Composite Build** pattern.

## Architecture

### Composite Build Structure

This project uses Gradle's **includeBuild** (composite build) pattern where each major module (domain, data, application, app) is an independent build. This provides better isolation and parallel build capabilities compared to traditional multi-project builds.

```
Root (helloworld-commerce)
├── domain          (independent build)
├── data            (composite build: mysql, redis)
├── application     (independent build)
└── app             (composite build: api, worker)
```

### Module Structure and Dependencies

The project follows a strict dependency hierarchy (bottom-up):

```
domain (no dependencies)
  ↑
application (depends on: domain)
  ↑
data/mysql (depends on: domain, application)
data/redis (independent)
  ↑
app/api (depends on: domain, application, data/mysql)
app/worker (depends on: domain, application, data/mysql)
```

**Critical Rules**:
1. Dependencies flow upward only. Never add dependencies in reverse (e.g., domain cannot depend on data)
2. Data/mysql depends on application to implement secondary ports; data/redis remains independent
3. Use group IDs for cross-build dependencies: `com.helloworld:domain`, `com.helloworld.data:mysql`, `com.helloworld.app:api`

### Layer Responsibilities

- **domain/**: Pure business logic and domain models. Uses jMolecules `@Entity`, `@AggregateRoot`, `@ValueObject` for DDD patterns. No Spring, no JPA annotations.
- **data/mysql/**: JPA entities, repositories, Flyway migrations, persistence adapters. Converts between domain models and JPA entities.
- **data/redis/**: Redis caching and session storage configuration.
- **application/**: Use cases (primary ports), application services, hexagonal ports (input/output). Orchestrates domain/data layers with `@Service` and `@Transactional`. Uses Spring Modulith for event-driven communication.
- **app/api/**: REST controllers (`@RestController`), HTTP files, API entry point. Only module creating executable JAR for API.
- **app/worker/**: Kafka consumers, event listeners. Worker entry point for async processing.

### Build Logic (Convention Plugins)

This project uses Gradle convention plugins in `build-logic/` to avoid duplication. When adding a new module:

1. Create module directory with `settings.gradle.kts` that includes build-logic
2. For composite builds (like data, app), include submodules in module's `settings.gradle.kts`
3. Add `includeBuild("module-name")` to root `settings.gradle.kts`
4. Apply appropriate convention plugins in module's `build.gradle.kts`:
   - `buildlogic.kotlin-conventions` - Base Kotlin config (all modules)
   - `buildlogic.spring-boot-conventions` - Spring Boot + JPA + Jackson
   - `buildlogic.spring-modulith-conventions` - Spring Modulith support
   - `buildlogic.kotlin-logging-conventions` - Kotlin logging
   - `buildlogic.test-conventions` - JUnit 5 + Kotest + integration tests

**Important**:
- Only `app/api` module should have `bootJar { enabled = true }`
- All other Spring Boot modules must set `bootJar { enabled = false }` and `jar { enabled = true }`
- Set `group` property for proper artifact identification in composite builds

### Version Management

All dependency versions are centralized in `gradle/libs.versions.toml`. Key dependencies:

- **Kotlin**: 2.3.0
- **Java Toolchain**: 25
- **Spring Boot**: 4.0.1
- **Spring Modulith**: Latest (managed by Spring Boot)
- **jMolecules**: 2025.0.2 (DDD patterns)
- **Kotlin Logging**: 7.0.13
- **Kotest**: 6.0.2 (testing framework)
- **Testcontainers**: 2.0.3 (integration testing)

To update versions:
1. Edit the version in `[versions]` section
2. The change propagates to all modules automatically
3. Never hardcode versions in individual module `build.gradle.kts` files

## Common Commands

### Build and Run

```bash
# Start databases (MySQL and Redis)
docker-compose up -d

# Build specific module (composite build pattern)
./gradlew :app:api:build
./gradlew :app:worker:build
./gradlew :data:mysql:build
./gradlew :domain:build
./gradlew :application:build

# Build with tests
./gradlew :app:api:build

# Build without tests
./gradlew :app:api:build -x test

# Run the application (note the :api suffix)
./gradlew :app:api:bootRun

# Run the worker (if needed)
./gradlew :app:worker:bootRun

# Note: There is NO root-level clean task in composite builds
# Clean individual modules if needed:
./gradlew :app:api:clean
./gradlew :app:worker:clean
```

### Testing

```bash
# Run tests for specific module (composite build)
./gradlew :domain:test
./gradlew :application:test
./gradlew :app:api:test
./gradlew :app:worker:test
./gradlew :data:mysql:test

# Run integration tests (uses intTest suite)
./gradlew :app:api:intTest
./gradlew :data:mysql:intTest

# Run all tests including integration tests
./gradlew check

# Run tests with specific test class
./gradlew :app:api:test --tests "OrderControllerTest"

# Run specific test method
./gradlew :app:api:test --tests "OrderControllerTest.should complete an order"

# Note: Use Kotest DescribeSpec framework for new tests
```

### Database

```bash
# Start all services (MySQL + Redis)
docker-compose up -d

# Start specific service
docker-compose up -d mysql
docker-compose up -d redis

# Stop all services
docker-compose down

# View service logs
docker logs helloworld-commerce-mysql
docker logs helloworld-commerce-redis

# Connect to MySQL
docker exec -it helloworld-commerce-mysql mysql -uroot -pmysql helloworld-commerce

# Connect to Redis CLI
docker exec -it helloworld-commerce-redis redis-cli

# Check event_publication table
docker exec -it helloworld-commerce-mysql mysql -uroot -pmysql helloworld-commerce \
  -e "SELECT id, listener_id, event_type, publication_date, completion_date, status FROM event_publication;"
```

**Database Configuration**:
- **MySQL**:
  - Database: `helloworld-commerce`
  - Username: `root`
  - Password: `mysql`
  - Port: `3306`
  - Timezone: `Asia/Seoul`
  - Migrations: Managed by Flyway in `data/mysql` module
    - `db/migration/__root/` - Shared infrastructure (event_publication, etc.)
    - `db/migration/order/` - Order module tables
    - `db/migration/product/` - Product module tables
- **Redis**:
  - Port: `6379`
  - Keyspace notifications: Enabled for expiration events
  - Used for: Caching, session storage

### Module-specific Tasks

```bash
# Check dependencies (specify module in composite build)
./gradlew :app:api:dependencies
./gradlew :app:worker:dependencies
./gradlew :data:mysql:dependencies
./gradlew :application:dependencies

# View all tasks for a module
./gradlew :app:api:tasks
./gradlew :app:worker:tasks

# Build specific modules
./gradlew :domain:build
./gradlew :data:mysql:jar
./gradlew :application:jar
./gradlew :app:api:bootJar
./gradlew :app:worker:bootJar
```

## Code Style & Conventions

### Language & Formatting
- **Primary language**: Kotlin 2.3.0 (use Kotlin DSL for Gradle)
- **Indentation**: 4 spaces (defined in `.editorconfig`)
- **Line endings**: LF (Unix-style)
- **Encoding**: UTF-8
- **Trailing whitespace**: Trim
- **Java toolchain**: 25

### Naming Conventions

#### Domain Layer
- **Domain events**: `OrderComplete` (data class in domain package)
- **Aggregates**: Annotate with `@AggregateRoot` (jMolecules)
- **Entities**: Annotate with `@Entity` (jMolecules, not JPA)
- **Value objects**: Annotate with `@ValueObject` (jMolecules)

#### Data Layer
- **JPA entities**: `OrderJpaEntity` (class with JPA annotations)
- **Repositories**: `OrderJpaRepository` (interface extends `JpaRepository`)
- **Persistence adapters**: `OrderPersistenceAdapter` (implements secondary ports)

#### Application Layer
- **Primary ports (use cases)**: `CompleteOrderUseCase` (interface annotated with `@PrimaryPort`)
- **Secondary ports**: `LoadOrderPort` (interface annotated with `@SecondaryPort`)
- **Services**: `OrderService` (class annotated with `@Service`, implements primary ports)

#### App Layer
- **Controllers**: `OrderController` (class annotated with `@RestController`)
- **Consumers**: `OrderConsumer` (class annotated with `@Service`)

### Import Guidelines

Always use the correct imports:
- **JPA**: `jakarta.persistence.*` (NOT `javax.persistence.*`)
- **jMolecules DDD**: `org.jmolecules.ddd.annotation.*` (`@Entity`, `@AggregateRoot`, `@ValueObject`)
- **jMolecules Events**: `org.jmolecules.event.annotation.*` (`@DomainEvent`)
- **jMolecules Hexagonal**: `org.jmolecules.architecture.hexagonal.*` (`@PrimaryPort`, `@SecondaryPort`)
- **Logging**: `io.github.oshai.kotlinlogging.KotlinLogging`
- **Spring Modulith**: `org.springframework.modulith.events.ApplicationModuleListener`

### Type System & Properties

- **Domain models**: Immutable `data class` with validation in `init` blocks
- **JPA entities**: `class` with `val` for immutable properties, `var` for mutable ones
- **Prefer `val` over `var`**: Use `var` only when mutability is required
- **Constructor injection**: Always use constructor injection
  ```kotlin
  class OrderService(
      val orderPort: LoadOrderPort,
      val events: ApplicationEventPublisher
  )
  ```
- **Function return types**: Omit `: Unit` explicitly for void functions

### Architecture Patterns

#### Hexagonal Architecture with jMolecules
- **Primary ports**: `@PrimaryPort` interfaces for use cases (inbound)
- **Secondary ports**: `@SecondaryPort` interfaces for data access (outbound)
- **Primary adapters**: Controllers implementing REST API
- **Secondary adapters**: Persistence adapters implementing secondary ports

#### Event-Driven Architecture
- **Event publishing**: Use `ApplicationEventPublisher`
- **Event listening**: Use `@ApplicationModuleListener` for Spring Modulith events
- **Event externalization**: Use `@Externalized("order-event::#{#this.getId()}")` on domain events
- **Transactional events**: Events are published within transaction boundaries

#### Transaction Management
- Use `@Transactional` on service methods
- Default to `@Transactional(readOnly = true)` for read operations
- Override with `@Transactional` for write operations

### Error Handling

- Use Kotlin's null safety features (`?`, `?.`, `?:`, `!!`)
- Throw meaningful exceptions with descriptive messages
- Consider custom domain exceptions for business rule violations
- Avoid swallowing exceptions; let them propagate to appropriate handlers

### Logging

Use Kotlin Logging for structured logging:
```kotlin
import io.github.oshai.kotlinlogging.KotlinLogging

private val log = KotlinLogging.logger {}

class OrderService {
    fun completeOrder(orderId: Long) {
        log.info { "Completing order: $orderId" }
        log.error(exception) { "Failed to complete order: $orderId" }
    }
}
```

**Best practices**:
- Use lazy evaluation with curly braces: `log.info { "message" }`
- Log at appropriate levels: `info`, `warn`, `error`, `debug`, `trace`
- Include context in log messages (IDs, relevant data)

### Testing

- **Framework**: JUnit 5 + Kotest 6.0.2
- **Integration tests**: Testcontainers for MySQL, Spring Boot Test
- **Test style**: Use Kotest `DescribeSpec` style for behavior-driven tests
  ```kotlin
  class OrderServiceTest : DescribeSpec({
      describe("OrderService") {
          it("should complete an order") {
              // test implementation
          }
      }
  })
  ```
- **Test naming**: Name tests descriptively after the class under test
  - `OrderControllerTest`, `OrderServiceTest`, `OrderPersistenceAdapterTest`
- **Test profiles**: Use `@ActiveProfiles("test")` for integration tests
- **Spring Boot tests**: Use `@SpringBootTest` with `@Import(TestcontainersConfiguration::class)` for full context

### Database Migrations

- **Tool**: Flyway for schema migrations
- **Root migrations**: `db/migration/__root/` (e.g., `event_publication` table)
- **Module-specific migrations**: `db/migration/{module}/` (e.g., `order`, `product`)
- **Safety**: Use `CREATE TABLE IF NOT EXISTS` to avoid conflicts
- **Naming**: `V1__init.sql`, `V2__add_column.sql`, etc.

### API Documentation

- **HTTP files**: Store in `app/api/http/` directory
- **Example**: `order.http` with sample requests
  ```http
  GET http://localhost:8080/orders/completed
  ```
- **Documentation**: Document new endpoints in both HTTP files and this CLAUDE.md

### Configuration

- **MySQL**: localhost:3306, database: `helloworld-commerce`, user: `root`, password: `mysql`
- **Redis**: localhost:6379
- **Consistency**: Update both `docker-compose.yml` and `application.yml` when changing credentials
- **Profiles**: Use Spring profiles (e.g., `application.yml` includes `application-mysql.properties`)

## Domain Model Pattern

Domain models in `domain/` module use Kotlin data classes with validation in `init` blocks:

```kotlin
data class Product(
    val id: Long? = null,
    val name: String,
    // ... other fields
) {
    init {
        require(name.isNotBlank()) { "Product name cannot be blank" }
        require(price > BigDecimal.ZERO) { "Product price must be positive" }
    }

    // Business logic methods
    fun decreaseStock(quantity: Int): Product { ... }
}
```

**Key points**:
- Immutable by default (use `val`)
- Validation in `init` block
- Business methods return new instances (copy)
- No JPA annotations

## Data Layer Pattern

JPA entities in `data/` module convert to/from domain models:

```kotlin
@Entity
@Table(name = "products")
class ProductEntity(...) {
    fun toDomain(): Product { ... }

    companion object {
        fun from(product: Product): ProductEntity { ... }
    }
}
```

Repositories extend Spring Data JPA:

```kotlin
@Repository
interface ProductRepository : JpaRepository<ProductEntity, Long>
```

## Application Layer Pattern

Services use `@Service` and `@Transactional`:

```kotlin
@Service
@Transactional(readOnly = true)
class ProductService(private val productRepository: ProductRepository) {

    @Transactional
    fun create(product: Product): Product {
        val entity = ProductEntity.from(product)
        return productRepository.save(entity).toDomain()
    }
}
```

**Pattern**: Always convert between domain models and entities at service boundaries.

## REST API Pattern

Controllers return DTOs, not domain models:

```kotlin
@RestController
@RequestMapping("/api/products")
class ProductController(private val productService: ProductService) {

    @GetMapping("/{id}")
    fun getProduct(@PathVariable id: Long): ResponseEntity<ProductResponse> {
        val product = productService.findById(id) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(ProductResponse.from(product))
    }
}
```

Separate request/response DTOs from domain models to maintain clean boundaries.

## Package Structure

All code uses base package `com.helloworld.commerce` with module-specific subpackages following hexagonal architecture:

### Domain Module
- **Domain models**: `com.helloworld.commerce.{module}.domain`
  - Example: `com.helloworld.commerce.order.domain.Order`
  - Use jMolecules annotations: `@Entity`, `@AggregateRoot`, `@ValueObject`
  - No Spring or JPA annotations

### Data Modules
- **JPA entities**: `com.helloworld.commerce.{module}.adapter.output.mysql`
  - Example: `com.helloworld.commerce.order.adapter.output.mysql.OrderJpaEntity`
  - Suffix: `*JpaEntity`
- **Repositories**: `com.helloworld.commerce.{module}.adapter.output.mysql`
  - Example: `com.helloworld.commerce.order.adapter.output.mysql.OrderJpaRepository`
  - Suffix: `*JpaRepository`
- **Persistence adapters**: `com.helloworld.commerce.{module}.adapter.output.mysql`
  - Example: `com.helloworld.commerce.order.adapter.output.mysql.OrderPersistenceAdapter`
  - Implements secondary ports from application layer
  - Suffix: `*PersistenceAdapter`

### Application Module
- **Primary ports (use cases)**: `com.helloworld.commerce.{module}.application.port.input`
  - Example: `com.helloworld.commerce.order.application.port.input.CompleteOrderUseCase`
  - Annotated with `@PrimaryPort` (jMolecules)
  - Suffix: `*UseCase`
- **Secondary ports**: `com.helloworld.commerce.{module}.application.port.output`
  - Example: `com.helloworld.commerce.order.application.port.output.LoadOrderPort`
  - Annotated with `@SecondaryPort` (jMolecules)
  - Suffix: `*Port`
- **Services**: `com.helloworld.commerce.{module}.application.service`
  - Example: `com.helloworld.commerce.order.application.service.OrderService`
  - Annotated with `@Service`
  - Implements primary ports (use cases)
  - Suffix: `*Service`

### App Modules
- **Controllers (API)**: `com.helloworld.commerce.{module}.api`
  - Example: `com.helloworld.commerce.order.api.OrderController`
  - Annotated with `@RestController`
  - Suffix: `*Controller`
- **Consumers (Worker)**: `com.helloworld.commerce.{module}.worker`
  - Example: `com.helloworld.commerce.order.worker.OrderConsumer`
  - Annotated with `@Service`
  - Suffix: `*Consumer`

## Important Configuration Notes

### Database Connection

The application expects MySQL on `localhost:3306` with database `helloworld-commerce`. The `docker-compose.yml` is configured to match this. If changing database settings:

1. Update `docker-compose.yml` (database name, credentials)
2. Update `app/src/main/resources/application.yml` (JDBC URL, username, password)
3. Ensure both configurations match exactly

### Hibernate DDL Mode

Currently set to `ddl-auto: update` for development. For production, change to `validate` or `none` and use migration tools.

## Spring Modulith Event-Driven Architecture

This project uses Spring Modulith for reliable, event-driven communication between modules.

### Event Publication Registry

Spring Modulith maintains a **single `event_publication` table** shared across all modules:

**Table Location**:
- **MUST** be created in `data/mysql/src/main/resources/db/migration/__root/V1__init.sql`
- **DO NOT** duplicate in module-specific migrations (e.g., `order/V1__order.sql`, `product/V1__product.sql`)
- Currently there are duplicates in `order/V1__order.sql` - this should be removed

**Table Structure**:
```sql
event_publication (
  id                     varchar(36) PRIMARY KEY,
  listener_id            varchar(512),     -- Module::Listener (e.g., com.example.order::OrderListener.handle)
  event_type             varchar(512),     -- Event class name
  serialized_event       varchar(4000),    -- Serialized event payload
  publication_date       timestamp(6),     -- When event was published
  completion_date        timestamp(6),     -- When processing completed (NULL if pending)
  status                 varchar(20),      -- Processing status
  completion_attempts    int,              -- Retry count
  last_resubmission_date timestamp(6)      -- Last retry timestamp
)
```

**Key Principles**:
- **Not module-specific**: Single table for entire application
- **Transactional guarantee**: Event publication and business logic in same transaction
- **Automatic retry**: Failed events remain in table for automatic reprocessing
- **Automatic cleanup**: Successfully processed events are deleted
- **Cross-module tracking**: `listener_id` identifies which module is handling which event

**How it Works**:
1. Service publishes event via `ApplicationEventPublisher` → Row inserted into `event_publication`
2. Listener processes event successfully → Row deleted from `event_publication`
3. Listener fails → Row remains, Spring Modulith retries automatically
4. Multiple listeners for same event → Multiple rows created (one per listener)

### Event Publishing Pattern

```kotlin
@Service
@Transactional
class ProductService(
    private val productRepository: ProductRepository,
    private val events: ApplicationEventPublisher
) {
    fun decreaseStock(productId: Long, quantity: Int): Product {
        val product = productRepository.findById(productId).orElseThrow()
        val updated = product.decreaseStock(quantity)
        val saved = productRepository.save(ProductEntity.from(updated))

        // Event is persisted in event_publication table
        events.publishEvent(ProductStockDecreased(productId, quantity))

        return saved.toDomain()
    }
}
```

### Event Listening Pattern

```kotlin
@Component
class OrderEventListener {

    @ApplicationModuleListener  // Spring Modulith listener
    @Transactional
    fun on(event: ProductStockDecreased) {
        // Process event
        // If this method throws exception:
        //   - Transaction rolls back
        //   - Event remains in event_publication table
        //   - Spring Modulith retries automatically
    }
}
```

### Migration File Management

**Critical Rules**:
1. **Shared infrastructure tables** (like `event_publication`) → `__root/V1__init.sql`
2. **Module-specific tables** (like `products`, `orders`) → Module migrations (e.g., `product/V1__product.sql`)
3. **Never duplicate** `event_publication` table creation across modules

**Current Issue to Fix**:
The `event_publication` table is currently defined in:
- ✅ `__root/V1__init.sql` (correct location)
- ❌ `order/V1__order.sql` (should be removed)

While `CREATE TABLE IF NOT EXISTS` prevents errors, having it in one location is cleaner.

**Flyway Migration Locations**:
Spring Modulith's Flyway integration uses:
```properties
spring.modulith.runtime.flyway-enabled=true
```

This enables module-specific migration paths:
- `db/migration/__root/` - Shared infrastructure
- `db/migration/order/` - Order module tables
- `db/migration/product/` - Product module tables

## Adding New Features

When adding a new entity/aggregate following hexagonal architecture:

### 1. Domain Module
- Create domain model with business logic
- Use immutable `data class` with validation in `init` blocks
- Annotate with jMolecules:
  - `@AggregateRoot` for aggregates
  - `@Entity` for entities (NOT JPA `@Entity`)
  - `@ValueObject` for value objects
  - `@DomainEvent` for domain events
- **No Spring or JPA annotations in domain layer**

Example:
```kotlin
@AggregateRoot
data class Order(
    val id: Long? = null,
    val status: OrderStatus
) {
    init {
        require(id == null || id > 0) { "Order ID must be positive" }
    }

    fun complete(): Order = copy(status = OrderStatus.COMPLETED)
}
```

### 2. Data/MySQL Module
- Create JPA entity with suffix `*JpaEntity`
- Create repository extending `JpaRepository` with suffix `*JpaRepository`
- Create persistence adapter implementing secondary ports with suffix `*PersistenceAdapter`
- Add conversion methods: `toDomain()` and `from(domain)`
- Add Flyway migration if schema changes needed:
  - **Infrastructure tables** (shared across modules) → `db/migration/__root/`
  - **Module-specific tables** → `db/migration/{module-name}/`

Example:
```kotlin
@Entity
@Table(name = "orders")
class OrderJpaEntity(
    @Id @GeneratedValue val id: Long? = null,
    val status: String
) {
    fun toDomain() = Order(id, OrderStatus.valueOf(status))

    companion object {
        fun from(order: Order) = OrderJpaEntity(order.id, order.status.name)
    }
}

interface OrderJpaRepository : JpaRepository<OrderJpaEntity, Long>

@Component
class OrderPersistenceAdapter(
    private val repository: OrderJpaRepository
) : LoadOrderPort, SaveOrderPort {
    override fun loadOrder(id: Long): Order? =
        repository.findById(id).orElse(null)?.toDomain()

    override fun saveOrder(order: Order): Order =
        repository.save(OrderJpaEntity.from(order)).toDomain()
}
```

### 3. Application Module
- Create **primary ports (use cases)** in `port.input` package
  - Annotate with `@PrimaryPort` (jMolecules)
  - Suffix: `*UseCase`
- Create **secondary ports** in `port.output` package
  - Annotate with `@SecondaryPort` (jMolecules)
  - Suffix: `*Port`
- Create service implementing primary ports
  - Annotate with `@Service`
  - Use `@Transactional` for transaction boundaries
  - Use `ApplicationEventPublisher` for Spring Modulith events
- For event consumption:
  - Use `@ApplicationModuleListener` to consume events from other modules

Example:
```kotlin
@PrimaryPort
interface CompleteOrderUseCase {
    fun completeOrder(orderId: Long): Order
}

@SecondaryPort
interface LoadOrderPort {
    fun loadOrder(id: Long): Order?
}

@SecondaryPort
interface SaveOrderPort {
    fun saveOrder(order: Order): Order
}

@Service
@Transactional(readOnly = true)
class OrderService(
    private val loadOrderPort: LoadOrderPort,
    private val saveOrderPort: SaveOrderPort,
    private val events: ApplicationEventPublisher
) : CompleteOrderUseCase {

    @Transactional
    override fun completeOrder(orderId: Long): Order {
        val order = loadOrderPort.loadOrder(orderId)
            ?: throw OrderNotFoundException(orderId)
        val completed = order.complete()
        val saved = saveOrderPort.saveOrder(completed)

        events.publishEvent(OrderCompleted(orderId))

        return saved
    }
}
```

### 4. App/API Module
- Create controller with DTOs and REST endpoints
- Annotate with `@RestController`
- Suffix: `*Controller`
- Inject and use primary ports (use cases), NOT services directly
- Separate request/response DTOs from domain models

Example:
```kotlin
@RestController
@RequestMapping("/api/orders")
class OrderController(
    private val completeOrderUseCase: CompleteOrderUseCase
) {
    @PostMapping("/{id}/complete")
    fun completeOrder(@PathVariable id: Long): ResponseEntity<OrderResponse> {
        val order = completeOrderUseCase.completeOrder(id)
        return ResponseEntity.ok(OrderResponse.from(order))
    }
}
```

### 5. App/Worker Module (Optional)
- Create Kafka consumers or event listeners for async processing
- Annotate with `@Service`
- Suffix: `*Consumer`
- Use `@ApplicationModuleListener` for Spring Modulith events

Example:
```kotlin
@Service
class OrderEventConsumer {

    @ApplicationModuleListener
    @Transactional
    fun on(event: OrderCompleted) {
        log.info { "Order completed: ${event.orderId}" }
        // Process event asynchronously
    }
}
```

### Critical Rules

**Dependency Flow**: Always maintain the strict hierarchy:
```
domain ← application ← data/mysql ← app/api|worker
```

**Never reverse dependencies**:
- Domain MUST NOT depend on data, application, or app layers
- Data/mysql depends on application layer to implement secondary ports (follow the documented flow)
- Data/redis should remain independent

**For Event-Driven Communication**:
- Events are published within transactions (transactional guarantee)
- Failed event processing is automatically retried by Spring Modulith
- Successfully processed events are automatically removed from `event_publication` table
- Monitor `event_publication` table for stuck events:
  - Events with old `publication_date` and NULL `completion_date` indicate failures
  - Check `completion_attempts` for retry count

**Package Naming**:
- Follow hexagonal architecture package structure strictly
- Use adapter pattern for data access: `{module}.adapter.output.mysql`
- Use port pattern for interfaces: `{module}.application.port.{input|output}`

## Gradle Build Troubleshooting

### Composite Build Issues

If you encounter "Cannot find module" errors:
- Ensure module has correct `group` property in `build.gradle.kts`
- Check that cross-build dependencies use correct coordinates: `com.helloworld:domain`, `com.helloworld.data:mysql`
- Verify module is included in root `settings.gradle.kts` using `includeBuild("module-name")`

If you encounter "Circular dependency" errors:
- Data/mysql depends on application; data/redis should remain independent
- Check dependency graph: domain → application → data → app
- Data/redis should remain independent

If you encounter "Cannot access supertype" errors:
- Ensure the module has correct dependencies in `build.gradle.kts`
- Application module needs `spring-boot-starter-data-jpa` if using repositories from data/mysql
- Verify `build-logic` compiled successfully (it builds before main project)

If build-logic changes don't apply:
```bash
rm -rf build-logic/build
./gradlew :app:api:build --refresh-dependencies
```

### Composite Build Limitations

- No root-level `clean` task - clean individual modules instead
- Cannot use `allprojects` or `subprojects` blocks in root `build.gradle.kts`
- Each included build is independent and must configure its own repositories
