plugins {
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.spring-modulith-conventions")
    id("buildlogic.kotlin-logging-conventions")
}

dependencies {
    implementation("com.helloworld.commerce:domain")

    // Spring Data JPA (required for repository interfaces)
//    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // jMolecules
    implementation(libs.bundles.jmolecules)

    // Testing
    testImplementation(libs.bundles.kotest)
    testImplementation(libs.kotest.extensions.spring)
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    enabled = false
}

tasks.named<Jar>("jar") {
    enabled = true
}
