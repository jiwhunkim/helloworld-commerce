plugins {
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.spring-modulith-conventions")
    id("buildlogic.kotlin-logging-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")

    implementation("org.springframework.boot:spring-boot-starter-validation")

    implementation("org.springframework.modulith:spring-modulith-events-kafka")

    implementation("com.helloworld.commerce:domain")
    implementation("com.helloworld.commerce:application")
    implementation("com.helloworld.commerce:mysql")
}
