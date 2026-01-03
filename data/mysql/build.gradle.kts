plugins {
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.spring-modulith-conventions")
    id("buildlogic.kotlin-logging-conventions")
}

dependencies {
    implementation("com.helloworld.commerce:domain")
    implementation("com.helloworld.commerce:application")

    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-flyway")
    testImplementation("org.springframework.boot:spring-boot-starter-flyway-test")

    implementation("org.springframework.modulith:spring-modulith-starter-jpa")
    runtimeOnly("org.springframework.modulith:spring-modulith-runtime")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    runtimeOnly("com.mysql:mysql-connector-j")


    intTestImplementation("org.springframework.boot:spring-boot-testcontainers")
    intTestImplementation(libs.kotest.extensions.testcontainers)
    intTestImplementation(platform(libs.testcontainers.bom))
    intTestImplementation(libs.testcontainers.junit.jupiter)
    intTestImplementation(libs.testcontainers.mysql)
}
