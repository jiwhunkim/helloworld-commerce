# helloworld-commerce

A modular monolith e-commerce application built with Kotlin and Spring Boot, following clean architecture principles with Gradle Composite Build pattern.

## Architecture

This project follows a **modular monolith** (modulith) architecture pattern using **Gradle Composite Build** for better module isolation:

```
helloworld-commerce/              (root build)
├── domain/                       (independent build)
├── data/                         (composite build)
│   ├── mysql/                    (depends on: domain)
│   └── redis/                    (independent)
├── application/                  (independent build, depends on: domain, data/mysql)
├── app/                          (composite build)
│   └── api/                      (depends on: domain, application, data/mysql)
├── build-logic/                  (convention plugins)
└── gradle/                       (version catalog)
```

### Layer Responsibilities

- **Domain**: Pure business logic, domain models with jMolecules DDD patterns. No external dependencies.
- **Data/MySQL**: JPA entities, repositories, Flyway migrations. Converts between domain models and JPA entities.
- **Data/Redis**: Redis caching and session storage configuration.
- **Application**: Use cases and application services with Spring Modulith event-driven architecture. Orchestrates domain and data layers.
- **App/API**: REST controllers, configuration, and Spring Boot entry point. Only module that creates executable JAR.
- **Build-Logic**: Gradle convention plugins that define shared build configurations across modules.

### Dependency Flow

Dependencies flow upward only (domain ← data ← application ← app):

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

## Tech Stack

- **Language**: Kotlin 1.9.21
- **Framework**: Spring Boot 3.2.0
- **Modulith**: Spring Modulith 2.0.1 (event-driven modular architecture)
- **DDD**: jMolecules 1.8.0 (Domain-Driven Design patterns)
- **Build Tool**: Gradle 8.5 with Kotlin DSL (Composite Build)
- **Database**: MySQL 8.0
- **Cache**: Redis 7.2
- **ORM**: Spring Data JPA
- **Migration**: Flyway 10.4.1
- **Testing**: JUnit 5, Kotest 5.8.0, TestContainers 1.19.3

## Prerequisites

- JDK 17 or higher
- Docker and Docker Compose (for MySQL and Redis)

## Getting Started

### 1. Start Database Services

```bash
# Start both MySQL and Redis
docker-compose up -d

# Or start individually
docker-compose up -d mysql
docker-compose up -d redis
```

Wait a few seconds for services to be ready.

### 2. Build the Project

```bash
# Build the entire project
./gradlew :app:api:build

# Or build specific modules
./gradlew :domain:build
./gradlew :data:mysql:build
./gradlew :application:build
```

### 3. Run the Application

```bash
# Run the API application
./gradlew :app:api:bootRun
```

The application will start on `http://localhost:8080`

**Note**: Due to Gradle Composite Build pattern:
- Use `:app:api:bootRun` (not `:app:bootRun`)
- There is no root-level `clean` task
- Clean individual modules if needed: `./gradlew :app:api:clean`

## API Endpoints

### Products API

- `GET /api/products` - Get all products
- `GET /api/products/{id}` - Get product by ID
- `GET /api/products/search?name={name}` - Search products by name
- `POST /api/products` - Create a new product
- `PUT /api/products/{id}` - Update a product
- `DELETE /api/products/{id}` - Delete a product

### Example Request

Create a product:
```bash
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Sample Product",
    "description": "This is a sample product",
    "price": 29.99,
    "stockQuantity": 100
  }'
```

## Project Structure

```
domain/
└── src/main/kotlin/com/helloworld/commerce/domain
    └── Product.kt                    # Domain model

data/
└── src/main/kotlin/com/helloworld/commerce/data
    ├── ProductEntity.kt              # JPA entity
    └── ProductRepository.kt          # Spring Data repository

application/
└── src/main/kotlin/com/helloworld/commerce/application
    └── ProductService.kt             # Application service

app/
├── src/main/kotlin/com/helloworld/commerce
│   ├── CommerceApplication.kt        # Application entry point
│   └── api
│       └── ProductController.kt      # REST controller
└── src/main/resources
    └── application.yml               # Application configuration
```

## Development

### Running Tests

```bash
./gradlew test
```

### Clean Build

```bash
./gradlew clean build
```

### Check Dependencies

```bash
./gradlew dependencies
```

### Build Logic (Convention Plugins)

This project uses Gradle convention plugins located in the `build-logic` directory to share common build configurations across modules. The following convention plugins are available:

- `buildlogic.kotlin-conventions`: Base Kotlin configuration for all modules
- `buildlogic.spring-boot-conventions`: Spring Boot, JPA, and Jackson configuration
- `buildlogic.spring-modulith-conventions`: Spring Modulith support for event-driven architecture
- `buildlogic.kotlin-logging-conventions`: Kotlin logging setup
- `buildlogic.test-conventions`: Test configuration with JUnit 5, Kotest, and integration test support

All versions are centrally managed in `gradle/libs.versions.toml` using Gradle version catalogs.

## Database Configuration

### MySQL Configuration

The project uses MySQL as the primary database:

```yaml
# docker-compose.yml
mysql:
  image: mysql:8.0
  environment:
    MYSQL_ROOT_PASSWORD: mysql
    MYSQL_DATABASE: helloworld-commerce
  ports:
    - "3306:3306"
```

**Connection Details**:
- Host: `localhost:3306`
- Database: `helloworld-commerce`
- Username: `root`
- Password: `mysql`
- Timezone: `Asia/Seoul`

### Redis Configuration

Redis is used for caching and session storage:

```yaml
# docker-compose.yml
redis:
  image: redis:7.2-alpine
  ports:
    - "6379:6379"
```

**Connection Details**:
- Host: `localhost:6379`
- Keyspace notifications: Enabled for expiration events

### Database Commands

```bash
# Connect to MySQL
docker exec -it helloworld-commerce-mysql mysql -uroot -pmysql helloworld-commerce

# Connect to Redis CLI
docker exec -it helloworld-commerce-redis redis-cli

# View logs
docker logs helloworld-commerce-mysql
docker logs helloworld-commerce-redis
```

## Spring Modulith Event-Driven Architecture

This project uses Spring Modulith for event-driven communication between modules, ensuring loose coupling and reliable message delivery.

### Event Publication Registry

Spring Modulith uses a single `event_publication` table to track event processing across all modules:

**Table Structure**:
```sql
event_publication
├── id (UUID)                    -- Event publication ID
├── listener_id (varchar)        -- Module::Listener identifier
├── event_type (varchar)         -- Event class name
├── serialized_event (varchar)   -- Serialized event data
├── publication_date (timestamp) -- When event was published
├── completion_date (timestamp)  -- When processing completed
├── status (varchar)             -- Processing status
└── completion_attempts (int)    -- Retry count
```

**Key Characteristics**:
- **Single table for all modules**: Not module-specific; shared across the application
- **Reliable delivery**: Events are persisted before processing
- **Automatic cleanup**: Completed events are removed from the table
- **Retry mechanism**: Failed events are automatically retried
- **Transactional**: Event publication and business logic are in the same transaction

**How it works**:
1. Module A publishes an event → Record created in `event_publication`
2. Module B's listener processes event successfully → Record deleted
3. If processing fails → Record remains for retry
4. Events are identified by `listener_id` (e.g., `com.helloworld.commerce.order::OrderEventListener.handleProductStockChanged`)

**Migration Management**:
- The `event_publication` table should be created in the root migrations (`__root/V1__init.sql`)
- Avoid duplicating the table creation in module-specific migrations
- Flyway with `CREATE TABLE IF NOT EXISTS` prevents conflicts but keeping it centralized is best practice

### Publishing Events

```kotlin
@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val events: ApplicationEventPublisher
) {
    @Transactional
    fun updateStock(productId: Long, quantity: Int) {
        val product = productRepository.findById(productId)
        val updated = product.decreaseStock(quantity)
        productRepository.save(updated)

        // Publish event (persisted in event_publication table)
        events.publishEvent(ProductStockChanged(productId, quantity))
    }
}
```

### Consuming Events

```kotlin
@Component
class OrderEventListener {
    @ApplicationModuleListener
    fun handleProductStockChanged(event: ProductStockChanged) {
        // Process event
        // If this fails, the event remains in event_publication for retry
    }
}
```

## Benefits of Modulith Architecture

1. **Clear Boundaries**: Each module has well-defined responsibilities
2. **Testability**: Modules can be tested independently
3. **Maintainability**: Changes are isolated to specific modules
4. **Scalability**: Easy to extract modules into microservices if needed
5. **Development Speed**: Faster than microservices while maintaining modularity

## License

This project is licensed under the MIT License.
