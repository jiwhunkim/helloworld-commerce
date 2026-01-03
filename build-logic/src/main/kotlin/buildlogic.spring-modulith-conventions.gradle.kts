import org.gradle.kotlin.dsl.kotlin

plugins {
    kotlin("jvm")
    kotlin("plugin.spring")
    kotlin("plugin.jpa")
    id("buildlogic.kotlin-conventions")
    id("buildlogic.test-conventions")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation(platform("org.springframework.modulith:spring-modulith-bom:2.0.1"))
    implementation("org.springframework.modulith:spring-modulith-starter-core")
//    implementation("org.springframework.modulith:spring-modulith-events-api")
    implementation("org.springframework.modulith:spring-modulith-events-core")
//    implementation("org.springframework.modulith:spring-modulith-moments")
//    runtimeOnly("org.springframework.modulith:spring-modulith-actuator")
//    runtimeOnly("org.springframework.modulith:spring-modulith-observability")
    testImplementation("org.springframework.modulith:spring-modulith-starter-test")
}
