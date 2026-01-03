plugins {
    id("buildlogic.spring-boot-conventions")
}

dependencies {
    // Spring Data Redis
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}

description = "Redis data module - Caching and session storage"
