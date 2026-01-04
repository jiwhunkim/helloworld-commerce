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
 │   ├── api/                      (depends on: domain, application, data/mysql)
 │   └── worker/                   (depends on: domain, application)
 ├── build-logic/                  (convention plugins)
 └── gradle/                       (version catalog)
 ```

 ### Layer Responsibilities

 - **Domain**: Pure business logic, domain models with jMolecules DDD patterns (order, delivery modules). No external dependencies.
 - **Data/MySQL**: JPA entities, repositories, Flyway migrations. Infrastructure layer for MySQL data access. Depends on domain and application to implement hexagonal ports.
 - **Data/Redis**: Redis caching and session storage configuration. Infrastructure layer for Redis access.
 - **Application**: Use cases and application services with Spring Modulith event-driven architecture. Orchestrates domain and defines hexagonal ports.
 - **App/API**: REST controllers, HTTP files, and API entry point. Creates executable JAR for REST API.
 - **App/Worker**: Kafka consumers and event listeners for async processing. Creates executable JAR for background workers.
 - **Build-Logic**: Gradle convention plugins that define shared build configurations across modules.

 ### Dependency Flow

 Infrastructure modules (data/mysql, data/redis) depend on application to implement hexagonal ports:

 ```
 domain (no dependencies)
   ↑
 application (depends on: domain)
   ↑
 data/mysql (depends on: domain, application)
 data/redis (independent)
   ↑
 app/api (depends on: domain, application, mysql)
 app/worker (depends on: domain, application, mysql)
 ```

 ## Tech Stack

 - **Language**: Kotlin 2.3.0
 - **Framework**: Spring Boot 4.0.1
 - **DDD**: jMolecules 2025.0.2 (Domain-Driven Design patterns)
 - **Build Tool**: Gradle 8.5 with Kotlin DSL (Composite Build)
 - **Database**: MySQL 8.0
 - **Cache**: Redis 7.2
 - **Messaging**: Kafka (for async event processing)
 - **ORM**: Spring Data JPA
 - **Migration**: Flyway
 - **Testing**: JUnit 5, Kotest 6.0.2, TestContainers 2.0.3

 ## Prerequisites

 - JDK 25
 - Docker and Docker Compose (for MySQL, Redis, and Kafka)

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
 # Build API module
 ./gradlew :app:api:build

 # Build worker module
 ./gradlew :app:worker:build

 # Build specific modules
 ./gradlew :domain:build
 ./gradlew :data:mysql:build
 ./gradlew :application:build
 ```

 ### 3. Run the Application

 ```bash
 # Run the API application
 ./gradlew :app:api:bootRun

 # Run the worker application (requires Kafka)
 ./gradlew :app:worker:bootRun
 ```

 The API application will start on `http://localhost:8080`

 **Note**: Due to Gradle Composite Build pattern:
 - Use module-qualified tasks (e.g., `:app:api:bootRun`, not `:app:bootRun`)
 - There is no root-level `clean` task
 - Clean individual modules: `./gradlew :app:api:clean`

 ## API Endpoints

 ### Orders API

 - `GET /orders/completed` - Complete an order (triggers OrderComplete event)

 ### Products API

 - `GET /api/products` - Get all products
 - `GET /api/products/{id}` - Get product by ID
 - `GET /api/products/search?name={name}` - Search products by name
 - `POST /api/products` - Create a new product
 - `PUT /api/products/{id}` - Update a product
 - `DELETE /api/products/{id}` - Delete a product

 ### Example Requests

 Complete an order:
 ```bash
 curl -X GET http://localhost:8080/orders/completed
 ```

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
 └── src/main/kotlin/com/helloworld/commerce
     └── order/domain/
         └── OrderComplete.kt           # Order domain event
     └── delivery/domain/              # (planned)

 data/
 └── src/main/kotlin/com/helloworld/commerce
     ├── order/adapter/output/mysql/
     │   ├── OrderJpaEntity.kt          # Order JPA entity
     │   ├── OrderJpaRepository.kt      # Order repository
     │   └── OrderPersistenceAdapter.kt # Order persistence adapter
     ├── product/adapter/output/mysql/
     │   ├── ProductJpaEntity.kt        # Product JPA entity
     │   └── ProductJpaRepository.kt    # Product repository
     └── resources/db/migration/
         ├── __root/V1__init.sql       # Event publication table
         ├── order/V1__order.sql        # Order table
         └── product/V1__product.sql    # Product table

 application/
 └── src/main/kotlin/com/helloworld/commerce
     └── order/application/
         ├── port/input/
         │   └── CompleteOrderUseCase.kt   # Order use case interface
         ├── port/output/
         │   └── LoadOrderPort.kt          # Order port interface
         └── service/
             └── OrderService.kt           # Order service implementation
     └── delivery/application/
         └── service/
             └── DeliveryService.kt        # Delivery service (event listener)

 app/
 ├── api/
 │   └── src/main/kotlin/com/helloworld/commerce
 │       ├── ApiApplication.kt            # API entry point
 │       └── order/api/
 │           └── OrderController.kt       # Order REST controller
 │   └── src/main/resources/
 │       └── application.yml
 └── worker/
     └── src/main/kotlin/com/helloworld/commerce
         ├── WorkerApplication.kt          # Worker entry point
         ├── order/worker/
         │   └── OrderConsumer.kt          # Order Kafka consumer
         └── delivery/worker/
             └── DeliveryConsumer.kt       # Delivery Kafka consumer
 ```

 ## Development

 ### Running Tests

 ```bash
 # Run unit tests
 ./gradlew :app:api:test
 ./gradlew :data:mysql:test
 ./gradlew :application:test

 # Run integration tests (uses Testcontainers)
 ./gradlew :data:mysql:intTest

 # Run all tests
 ./gradlew check

 # Run a single test class
 ./gradlew :data:mysql:intTest --tests "ApplicationIntegrationTests"
 ```

 ### Cleaning

 ```bash
 # Clean a specific module
 ./gradlew :app:api:clean
 ./gradlew :data:mysql:clean

 # Note: No root-level clean task due to composite build
 ```

 ### Checking Dependencies

 ```bash
 # Check dependencies for a specific module
 ./gradlew :app:api:dependencies
 ```

 ### Build Logic (Convention Plugins)

 This project uses Gradle convention plugins located in the `build-logic` directory to share common build configurations across modules:

 - `buildlogic.kotlin-conventions`: Base Kotlin configuration for all modules (JDK 25, free compiler args)
 - `buildlogic.spring-boot-conventions`: Spring Boot, JPA, and Jackson configuration
 - `buildlogic.spring-modulith-conventions`: Spring Modulith support for event-driven architecture
 - `buildlogic.kotlin-logging-conventions`: Kotlin logging setup
 - `buildlogic.test-conventions`: Test configuration with JUnit 5, Kotest 6.0.2, and integration test support

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
 class OrderService(val events: ApplicationEventPublisher): CompleteOrderUseCase {
     @Transactional
     override fun complete(orderId: Long) {
         // Business logic here
         events.publishEvent(OrderComplete(orderId))
     }
 }
 ```

 ### Consuming Events (Spring Modulith)

 ```kotlin
 @Service
 class DeliveryService {
     @ApplicationModuleListener
     fun on(orderComplete: OrderComplete) {
         // Process event - automatically retries on failure
     }
 }
 ```

 ### Consuming Events (Kafka - Worker Module)

 ```kotlin
 @Service
 class DeliveryConsumer {
     @KafkaListener(
         id = "deliveryEventListener",
         topics = ["order-event"],
         groupId = "delivery-worker-order-event"
     )
     fun on() {
         // Process Kafka message
     }
 }
 ```

 ## Benefits of Modulith Architecture

 1. **Clear Boundaries**: Each module (order, delivery, product) has well-defined responsibilities
 2. **Testability**: Modules can be tested independently with proper mocking
 3. **Maintainability**: Changes are isolated to specific modules
 4. **Scalability**: Easy to extract modules into microservices if needed
 5. **Development Speed**: Faster than microservices while maintaining modularity
 6. **Flexible Deployment**: Separate API and Worker applications for different use cases

## License

This project is licensed under the MIT License.
