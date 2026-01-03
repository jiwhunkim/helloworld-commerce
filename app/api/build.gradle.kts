plugins {
    id("buildlogic.spring-boot-conventions")
    id("buildlogic.spring-modulith-conventions")
    id("buildlogic.kotlin-logging-conventions")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")


    implementation("com.helloworld.commerce:domain")
    implementation("com.helloworld.commerce:application")
    implementation("com.helloworld.commerce:mysql")
}
