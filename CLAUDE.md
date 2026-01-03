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
└── app             (composite build: api)
```

### Module Structure and Dependencies

The project follows a strict dependency hierarchy (bottom-up):

```
domain (no dependencies)
  ↑
data/mysql (depends on: domain)
data/redis (independent)
  ↑
application (depends on: domain, data/mysql)
  ↑
app/api (depends on: domain, application, data/mysql)
```

**Critical Rules**:
1. Dependencies flow upward only. Never add dependencies in reverse (e.g., domain cannot depend on data)
2. Data modules (mysql, redis) must NOT depend on application layer to avoid circular dependencies
3. Use group IDs for cross-build dependencies: `com.helloworld:domain`, `com.helloworld.data:mysql`, `com.helloworld.app:api`

### Layer Responsibilities

- **domain/**: Pure business logic and domain models. Uses jMolecules for DDD patterns. No Spring, no JPA annotations.
- **data/mysql/**: JPA entities, repositories, Flyway migrations. Converts between domain models and JPA entities.
- **data/redis/**: Redis caching and session storage configuration.
- **application/**: Use cases and application services. Orchestrates domain and data layers with `@Service` and `@Transactional`. Uses Spring Modulith for modularity.
- **app/api/**: REST controllers (`@RestController`), configuration, Spring Boot entry point. Only module that creates executable JAR.

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

- **Kotlin**: 1.9.21
- **Spring Boot**: 3.2.0
- **Spring Modulith**: 1.1.0
- **jMolecules**: 1.8.0 (DDD patterns)
- **Flyway**: 10.4.1 (database migrations)
- **Kotest**: 5.8.0 (testing framework)
- **TestContainers**: 1.19.3 (integration testing)

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
./gradlew :data:mysql:build
./gradlew :domain:build

# Build with tests
./gradlew :app:api:build

# Build without tests
./gradlew :app:api:build -x test

# Run the application (note the :api suffix)
./gradlew :app:api:bootRun

# Note: There is NO root-level clean task in composite builds
# Clean individual modules if needed:
./gradlew :app:api:clean
```

### Testing

```bash
# Run tests for specific module (composite build)
./gradlew :domain:test
./gradlew :application:test
./gradlew :app:api:test
./gradlew :data:mysql:test

# Run integration tests (uses intTest suite)
./gradlew :app:api:intTest
./gradlew :data:mysql:intTest

# Run tests with specific test class
./gradlew :app:api:test --tests "ProductControllerTest"

# Note: Use Kotest framework for new tests
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
./gradlew :data:mysql:dependencies

# View all tasks for a module
./gradlew :app:api:tasks

# Build specific modules
./gradlew :domain:build
./gradlew :data:mysql:jar
./gradlew :app:api:bootJar
```

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

## Important Configuration Notes

### Database Connection

The application expects MySQL on `localhost:3306` with database `helloworld-commerce`. The `docker-compose.yml` is configured to match this. If changing database settings:

1. Update `docker-compose.yml` (database name, credentials)
2. Update `app/src/main/resources/application.yml` (JDBC URL, username, password)
3. Ensure both configurations match exactly

### Hibernate DDL Mode

Currently set to `ddl-auto: update` for development. For production, change to `validate` or `none` and use migration tools.

### Package Structure

All code uses base package `com.helloworld.commerce` with module-specific subpackages:
- `com.helloworld.commerce.domain`
- `com.helloworld.commerce.data`
- `com.helloworld.commerce.application`
- `com.helloworld.commerce` (app module)
- `com.helloworld.commerce.api` (controllers)

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

When adding a new entity/aggregate:

1. **Domain module**: Create domain model with business logic, annotate with jMolecules `@Entity` if needed
2. **Data/mysql module**: Create JPA entity, repository, and conversion methods. Add Flyway migration if schema changes needed.
   - **Infrastructure tables** (shared across modules) → `db/migration/__root/`
   - **Module-specific tables** → `db/migration/{module-name}/`
3. **Application module**: Create service with use cases, use Spring Modulith events if cross-module communication needed
   - Use `ApplicationEventPublisher` to publish events
   - Use `@ApplicationModuleListener` to consume events from other modules
4. **App/api module**: Create controller with DTOs and REST endpoints

Always maintain the dependency flow: domain ← data/mysql ← application ← app/api.

**For data modules**: Never add application layer dependencies to avoid circular dependencies.

**For event-driven communication**:
- Events are published and consumed asynchronously but within transactions
- Failed event processing is automatically retried by Spring Modulith
- Monitor `event_publication` table for stuck events (events with old `publication_date` and NULL `completion_date`)

## Gradle Build Troubleshooting

### Composite Build Issues

If you encounter "Cannot find module" errors:
- Ensure module has correct `group` property in `build.gradle.kts`
- Check that cross-build dependencies use correct coordinates: `com.helloworld:domain`, `com.helloworld.data:mysql`
- Verify module is included in root `settings.gradle.kts` using `includeBuild("module-name")`

If you encounter "Circular dependency" errors:
- Data modules (mysql, redis) must NOT depend on application layer
- Check dependency graph: domain → data → application → app
- Data modules should only depend on domain

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
