import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.spring-modulith-conventions")
    id("buildlogic.kotlin-logging-conventions")
}

dependencies {
    implementation("com.helloworld.commerce:domain")
    implementation("org.springframework:spring-tx")
}

val bootJar: BootJar by tasks
bootJar.enabled = false
